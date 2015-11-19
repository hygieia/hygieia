package com.capitalone.dashboard.collector;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.cloudwatch.AmazonCloudWatchClient;
import com.amazonaws.services.cloudwatch.model.Datapoint;
import com.amazonaws.services.cloudwatch.model.Dimension;
import com.amazonaws.services.cloudwatch.model.GetMetricStatisticsRequest;
import com.amazonaws.services.cloudwatch.model.GetMetricStatisticsResult;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.model.*;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.*;
import com.capitalone.dashboard.model.*;
import com.capitalone.dashboard.util.Encryption;
import com.capitalone.dashboard.util.EncryptionException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Collects the instance specific data from AWS.
 * 
 */
@Component
public class DefaultAWSCloudClient implements AWSCloudClient {

	private final AWSCloudSettings settings;
	private static final Log logger = LogFactory
			.getLog(AWSCloudCollectorTask.class);
	private static long ONE_HOUR_MILLI_SECOND = 60 * 60 * 1000;
	private static long ONE_DAY_MILLI_SECOND = 24 * 60 * 60 * 1000;

	@Autowired
	public DefaultAWSCloudClient(AWSCloudSettings settings) {
		this.settings = settings;
	}

	@Override
	public CloudComputeData getCloudComputeData(AWSConfig config) {
		CloudComputeData computeData = null;
		String proxyPassword;
		try {
			proxyPassword = Encryption.decryptString(
					settings.getProxyPassword(), settings.getKey());

			ClientConfiguration clientConfig = new ClientConfiguration()
					.withProxyHost(settings.getProxyURL())
					.withProxyPort(settings.getProxyPort())
					.withPreemptiveBasicProxyAuth(true)
					.withProxyUsername(settings.getProxyUser())
					.withProxyPassword(proxyPassword);

			String accessKey = Encryption.decryptString(config.getAccessKey(),
					settings.getKey());
			String secretKey = Encryption.decryptString(config.getSecretKey(),
					settings.getKey());

			AWSCredentials creds = new BasicAWSCredentials(accessKey, secretKey);
			AmazonEC2Client ec2Client = new AmazonEC2Client(creds, clientConfig);

			AmazonCloudWatchClient cwClient = new AmazonCloudWatchClient(creds,
					clientConfig);
			DescribeInstancesResult instanceResult = ec2Client.describeInstances();
			DescribeImagesResult imageResult = ec2Client.describeImages();

			DescribeVolumesResult volumeResult = ec2Client.describeVolumes();
			List<Instance> instanceList = new ArrayList<>();
			List<Reservation> reservations = instanceResult.getReservations();
			for (Reservation currRes : reservations) {
				List<Instance> currInstanceList = currRes.getInstances();
				instanceList.addAll(currInstanceList);
			}

			List<Volume> volumes = volumeResult.getVolumes();
			HashMap<String, Volume> instanceVolMap = new HashMap<>();
			for (Volume volume : volumes) {
				List<VolumeAttachment> attaches = volume.getAttachments();
				for (VolumeAttachment volumeAttachment : attaches) {
					instanceVolMap
							.put(volumeAttachment.getInstanceId(), volume);
				}
			}
			ArrayList<CloudComputeInstanceData> rawDataList = new ArrayList<>();
			int i = 0;
			for (Instance currInstance : instanceList) {
				i = i + 1;
				logger.info("Collecting instance details for " + i + " of "
						+ instanceList.size());
				CloudComputeInstanceData object = getComputeInstanceDetails(
						currInstance, instanceVolMap, cwClient,
						config.getLastUpdateTime());
				rawDataList.add(object);
			} 

			computeData = new CloudComputeData();
			AWSCloudComptuteStatistics stat = new AWSCloudComptuteStatistics(rawDataList);
			computeData.setAgeWarning(stat.getAgeWarningCount());
			computeData.setAgeExpired(stat.getAgeExpireCount());
			computeData.setAgeGood(stat.getAgeGoodCount());
			computeData.setCpuHigh(stat.getCpuHighCount());
			computeData.setCpuMid(stat.getCpuMidCount());
			computeData.setCpuLow(stat.getCpuLowCount());
			computeData.setNonEncryptedCount(stat.getUnEcryptedCount());
			computeData.setNonTaggedCount(stat.getUnTaggedCount());
			computeData.setStoppedCount(stat.getStoppedCount());
			computeData.setTotalInstanceCount(stat.getTotalCount());
			computeData.setDetailList(rawDataList);
			computeData.setLastUpdated(System.currentTimeMillis());
			computeData
					.setEstimatedCharge(get24HourInstanceEstimatedCharge(cwClient));

		} catch (EncryptionException e) {
			logger.fatal("Error Decrypting one or more required data fields", e);
		}
		return computeData;
	}

	private CloudComputeInstanceData getComputeInstanceDetails(
			Instance currInstance, HashMap<String, Volume> instanceVolMap,
			AmazonCloudWatchClient cwClient, long lastUpdated) {
		
		CloudComputeInstanceData object = new CloudComputeInstanceData();
		object.setTimestamp(new Date());
		object.setAge(getInstanceAge(currInstance));
		object.setEncrypted(isInstanceVolumneEncrypted(currInstance,
				instanceVolMap));
		object.setCpuUtilization(getInstanceCPUSinceLastRun(
				currInstance.getInstanceId(), cwClient, lastUpdated));
		object.setTagged(isInstanceTagged(currInstance));
		object.setStopped(isInstanceStopped(currInstance));
		object.setNetworkIn(getLastHourInstanceNetworkIn(
				currInstance.getInstanceId(), cwClient, lastUpdated));
		object.setNetworkOut(getLastHourIntanceNetworkOut(
				currInstance.getInstanceId(), cwClient, lastUpdated));
		object.setDiskRead(getLastHourInstanceDiskRead(
				currInstance.getInstanceId(), cwClient, lastUpdated));
		object.setDiskWrite(getLastInstanceHourDiskWrite(
				currInstance.getInstanceId(), cwClient));
		// rest of the details
		object.setImageId(currInstance.getImageId());
		object.setInstanceId(currInstance.getInstanceId());
		object.setInstanceType(currInstance.getInstanceType());
		object.setMonitored(false);
		object.setPrivateDns(currInstance.getPrivateDnsName());
		object.setPrivateIp(currInstance.getPrivateIpAddress());
		object.setPublicDns(currInstance.getPublicDnsName());
		object.setPublicIp(currInstance.getPublicIpAddress());
		object.setVirtualPrivateCloudId(currInstance.getVpcId());
		object.setRootDeviceName(currInstance.getRootDeviceName());
		object.setSubnetId(currInstance.getSubnetId());
		List<GroupIdentifier> groups = currInstance.getSecurityGroups();
		for (GroupIdentifier gi : groups) {
			object.addSecurityGroups(gi.getGroupName());
		}

		return object;
	}

	/* Gets the age in days of an instance */
	private static int getInstanceAge(Instance myInstance) {
		Date launchDate = myInstance.getLaunchTime();
		long diffInMillies = System.currentTimeMillis() - launchDate.getTime();
		return (int) TimeUnit.DAYS
				.convert(diffInMillies, TimeUnit.MILLISECONDS);
	}

	/*
	 * Returns if the instance is encrypted. The AMI is the level of encryption,
	 * so this checks if AMI is encrypted. Uses the AMI Id.
	 */
	private static boolean isInstanceVolumneEncrypted(Instance myInstance,
			HashMap<String, Volume> instanceVolMap) {
		Volume vol = instanceVolMap.get(myInstance.getInstanceId());
		return ((vol != null) ? vol.isEncrypted() : false);
	}

	/* Averages CPUUtil every minute for the last hour */
	private static Double getInstanceCPUSinceLastRun(String instanceId,
			AmazonCloudWatchClient ec2Client, long lastUpdated) {
		long offsetInMilliseconds = Math.min(ONE_DAY_MILLI_SECOND,
				System.currentTimeMillis() - lastUpdated);
		Dimension instanceDimension = new Dimension().withName("InstanceId")
				.withValue(instanceId);
		GetMetricStatisticsRequest request = new GetMetricStatisticsRequest()
				.withMetricName("CPUUtilization")
				.withNamespace("AWS/EC2")
				.withPeriod(60 * 60)
				// one hour
				.withDimensions(instanceDimension)
				// to get metrics a specific
				// instance
				.withStatistics("Average")
				.withStartTime(
						new Date(new Date().getTime() - offsetInMilliseconds))
				.withEndTime(new Date());
		GetMetricStatisticsResult result = ec2Client
				.getMetricStatistics(request);
		// to read data
		List<Datapoint> datapoints = result.getDatapoints();
		if (datapoints.size() == 0) {
			return 0.0;
		}
		Datapoint datapoint = datapoints.get(0);
		return datapoint.getAverage();
	}

	/* Averages CPUUtil every minute for the last hour */
	private static Double getLastHourInstanceNetworkIn(String instanceId,
			AmazonCloudWatchClient ec2Client, long lastUpdated) {
		long offsetInMilliseconds = Math.min(ONE_DAY_MILLI_SECOND,
				System.currentTimeMillis() - lastUpdated);
		Dimension instanceDimension = new Dimension().withName("InstanceId")
				.withValue(instanceId);
		GetMetricStatisticsRequest request = new GetMetricStatisticsRequest()
				.withMetricName("NetworkIn")
				.withNamespace("AWS/EC2")
				.withPeriod(60 * 60)
				// one hour
				.withDimensions(instanceDimension)
				// to get metrics a specific
				// instance
				.withStatistics("Average")
				.withStartTime(
						new Date(new Date().getTime() - offsetInMilliseconds))
				.withEndTime(new Date());
		GetMetricStatisticsResult result = ec2Client
				.getMetricStatistics(request);
		// to read data
		List<Datapoint> datapoints = result.getDatapoints();
		if (datapoints.size() == 0) {
			// This instance has no CPU Util
			return 0.0;
		}
		Datapoint datapoint = datapoints.get(0);
		return datapoint.getAverage();
	}

	/* Averages CPUUtil every minute for the last hour */
	private static Double getLastHourIntanceNetworkOut(String instanceId,
			AmazonCloudWatchClient ec2Client, long lastUpdated) {
		long offsetInMilliseconds = Math.min(ONE_DAY_MILLI_SECOND,
				System.currentTimeMillis() - lastUpdated);
		Dimension instanceDimension = new Dimension().withName("InstanceId")
				.withValue(instanceId);
		GetMetricStatisticsRequest request = new GetMetricStatisticsRequest()
				.withMetricName("NetworkOut")
				.withNamespace("AWS/EC2")
				.withPeriod(60 * 60)
				// one hour
				.withDimensions(instanceDimension)
				// to get metrics a specific
				// instance
				.withStatistics("Average")
				.withStartTime(
						new Date(new Date().getTime() - offsetInMilliseconds))
				.withEndTime(new Date());
		GetMetricStatisticsResult result = ec2Client
				.getMetricStatistics(request);

		// to read data
		List<Datapoint> datapoints = result.getDatapoints();
		if (datapoints.size() == 0) {
			// This instance has no CPU Util
			return 0.0;
		}
		Datapoint datapoint = datapoints.get(0);
		return datapoint.getAverage();
	}

	/* Averages CPUUtil every minute for the last hour */
	private static Double getLastHourInstanceDiskRead(String instanceId,
			AmazonCloudWatchClient ec2Client, long lastUpdated) {
		long offsetInMilliseconds = Math.min(ONE_DAY_MILLI_SECOND,
				System.currentTimeMillis() - lastUpdated);
		Dimension instanceDimension = new Dimension().withName("InstanceId")
				.withValue(instanceId);
		GetMetricStatisticsRequest request = new GetMetricStatisticsRequest()
				.withMetricName("DiskReadBytes")
				.withNamespace("AWS/EC2")
				.withPeriod(60 * 60)
				// one hour
				.withDimensions(instanceDimension)
				// to get metrics a specific
				// instance
				.withStatistics("Average")
				.withStartTime(
						new Date(new Date().getTime() - offsetInMilliseconds))
				.withEndTime(new Date());
		GetMetricStatisticsResult result = ec2Client
				.getMetricStatistics(request);

		// to read data
		List<Datapoint> datapoints = result.getDatapoints();
		if (datapoints.size() == 0) {
			// This instance has no CPU Util
			return 0.0;
		}
		Datapoint datapoint = datapoints.get(0);
		return datapoint.getAverage();
	}

	/* Averages CPUUtil every minute for the last hour */
	private static Double getLastInstanceHourDiskWrite(String instanceId,
			AmazonCloudWatchClient ec2Client) {
		long offsetInMilliseconds = 1000 * 60 * 60; // one hour in msec
		Dimension instanceDimension = new Dimension().withName("InstanceId")
				.withValue(instanceId);
		GetMetricStatisticsRequest request = new GetMetricStatisticsRequest()
				.withMetricName("DiskWriteBytes")
				.withNamespace("AWS/EC2")
				.withPeriod(60 * 60)
				// one hour
				.withDimensions(instanceDimension)
				// to get metrics a specific
				// instance
				.withStatistics("Average")
				.withStartTime(
						new Date(new Date().getTime() - offsetInMilliseconds))
				.withEndTime(new Date());
		GetMetricStatisticsResult result = ec2Client
				.getMetricStatistics(request);

		// to read data
		List<Datapoint> datapoints = result.getDatapoints();
		if (datapoints.size() == 0) {
			// This instance has no CPU Util
			return 0.0;
		}
		Datapoint datapoint = datapoints.get(0);
		return datapoint.getAverage();
	}

	private Double get24HourInstanceEstimatedCharge(
			AmazonCloudWatchClient ec2Client) {
		Dimension instanceDimension = new Dimension().withName("Currency")
				.withValue("USD");
		GetMetricStatisticsRequest request = new GetMetricStatisticsRequest()
				.withMetricName("EstimatedCharges")
				.withNamespace("AWS/Billing")
				.withPeriod(60 * 60 * 24)
				//
				// one hour
				.withDimensions(instanceDimension)
				// to get metrics a specific
				// instance
				.withStatistics("Average")
				.withStartTime(
						new Date(new Date().getTime() - ONE_DAY_MILLI_SECOND))
				.withEndTime(new Date());
		GetMetricStatisticsResult result = ec2Client
				.getMetricStatistics(request);
		// to read data
		List<Datapoint> datapoints = result.getDatapoints();
		if (datapoints.size() == 0) {
			// This instance has no CPU Util
			return 0.0;
		}
		Datapoint datapoint = datapoints.get(0);
		return datapoint.getAverage();
	}

	/* If the instance is tagged with ASV or ENV */
	private boolean isInstanceTagged(Instance currInstance) {
		List<Tag> tags = currInstance.getTags();
		if (settings.getValidTagKey().isEmpty())
			return false;

		for (Tag currTag : tags) {
			for (String tagKey : settings.getValidTagKey()) {
				if (currTag.getKey().equals(tagKey))
					return true;
			}
		}
		return false;
	}

	/*
	 * Returns true if instance is stopped. Other possible states include
	 * pending, running, shutting-down, terminated, stopping
	 */
	public boolean isInstanceStopped(Instance myInstance) {
		InstanceState instanceState = myInstance.getState();
		return (instanceState.getName().equals("stopped") ? true : false);
	}

	@Override
	public CloudStorageData getCloudStorageData(AWSConfig config) {
		String proxyPassword;
		logger.info("Collecting AWS Cloud Storage Data...");
		try {
			proxyPassword = Encryption.decryptString(
					settings.getProxyPassword(), settings.getKey());

			ClientConfiguration clientConfig = new ClientConfiguration()
					.withProxyHost(settings.getProxyURL())
					.withProxyPort(settings.getProxyPort())
					.withPreemptiveBasicProxyAuth(true)
					.withProxyUsername(settings.getProxyUser())
					.withProxyPassword(proxyPassword);

			String accessKey = Encryption.decryptString(config.getAccessKey(),
					settings.getKey());
			String secretKey = Encryption.decryptString(config.getSecretKey(),
					settings.getKey());

			AWSCredentials creds = new BasicAWSCredentials(accessKey, secretKey);

			AmazonS3 s3Client = new AmazonS3Client(creds, clientConfig);
			
			List<Bucket> buckets = s3Client.listBuckets();
			CloudStorageData storageData = new CloudStorageData();
			ArrayList<CloudStorageBucket> bucketList = new ArrayList<>();
			for (Bucket bucket : buckets) {
				CloudStorageBucket buk = new CloudStorageBucket();
				buk.setCreationDate(bucket.getCreationDate().getTime());
				buk.setName(bucket.getName());
				buk.setOwner(bucket.getOwner().getDisplayName());
/**
 * The following code to get the objects does not work fully without the owner details.
 * Need more work. In the mean time, leave the object browsing alone!
				ArrayList<CloudStorageObject> objectList = new ArrayList<>();
				try {
					ObjectListing s3Objects = s3Client.listObjects(bucket
							.getName());

					List<S3ObjectSummary> summary = s3Objects
							.getObjectSummaries();
					for (S3ObjectSummary s3ObjectSummary : summary) {
						CloudStorageObject myObject = new CloudStorageObject();
						String key = s3ObjectSummary.getKey();
						S3Object object = s3Client
								.getObject(new GetObjectRequest(bucket.getName(), key));
						ObjectMetadata meta = object.getObjectMetadata();

						String sseKey = meta.getSSEAlgorithm();
						myObject.setCreationDate(meta.getLastModified().getTime());
						myObject.setEncryption(sseKey);
						myObject.setName(object.getKey());
						myObject.setSize(meta.getContentLength());
						objectList.add(myObject);
					}

				} catch (AmazonS3Exception s3e) {
					logger.debug(s3e);
				} finally {
					buk.setObjects(objectList);
				}
 **/
				bucketList.add(buk);
			}
			storageData.setBucketList(bucketList);


		} catch (AmazonS3Exception | EncryptionException se) {
			logger.error("Error collecting storage data for access key=" + config.getAccessKey(), se);
		}
		return null;
	}
}
