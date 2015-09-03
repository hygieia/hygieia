package com.capitalone.dashboard.collector;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.amazonaws.services.cloudwatch.AmazonCloudWatchClient;
import com.amazonaws.services.cloudwatch.model.Datapoint;
import com.amazonaws.services.cloudwatch.model.Dimension;
import com.amazonaws.services.cloudwatch.model.GetMetricStatisticsRequest;
import com.amazonaws.services.cloudwatch.model.GetMetricStatisticsResult;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.InstanceState;
import com.amazonaws.services.ec2.model.Tag;
import com.capitalone.dashboard.model.CloudRawData;

/**
 * Collects the instance specific data from AWS.
 * 
 * @author NAA505
 * @author CUO722
 */
@Component
public class DefaultAWSCloudClient implements AWSCloudClient {

	private final AWSCloudSettings settings;

	@Autowired
	public DefaultAWSCloudClient(AWSCloudSettings settings) {
		this.settings = settings;
	}

	@Override
	/*Creates the AWSObject for a given instance.*/
	public CloudRawData getMetrics(Instance currInstance, AmazonCloudWatchClient cwClient, String accessKey) {
		CloudRawData object = new CloudRawData();			
		object.setTimestamp(new Date());
		object.setInstanceId(currInstance.getInstanceId());
		object.setAge(getAge(currInstance));
		object.setEncrypted(isEncrypted(currInstance));
		object.setCpuUtilization(getLastHourCPU(currInstance.getInstanceId(), cwClient));
		object.setTagged(isTagged(currInstance));
		object.setStopped(isStopped(currInstance));
		object.setAccountName("cof-sandbox-dev"); //TODO: account name is hardcoded currently
		return object;
	}

	/* Gets the age in days of an instance */
	private static int getAge(Instance myInstance) {
		Date launchDate = myInstance.getLaunchTime();
		Date today = new Date();
		long diffInMillies = today.getTime() - launchDate.getTime();
		return (int) TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
	}

	/*
	 * Returns if the instance is encrypted. The AMI is the level of encryption,
	 * so this checks if AMI is encrypted. Uses the AMI Id.
	 */
	private static boolean isEncrypted(Instance myInstance) {
		String imageId = myInstance.getImageId();
		imageId.toLowerCase();
		return imageId.contains("enc") && !imageId.contains("noEnc");
	}

	/* Averages CPUUtil every minute for the last hour */
	private static Double getLastHourCPU(String instanceId, AmazonCloudWatchClient ec2Client) {
		long offsetInMilliseconds = 1000 * 60 * 60; // one hour in msec
		Dimension instanceDimension = new Dimension().withName("InstanceId").withValue(instanceId);
		GetMetricStatisticsRequest request = new GetMetricStatisticsRequest().withMetricName("CPUUtilization")
				.withNamespace("AWS/EC2").withPeriod(60 * 60) // one hour
				.withDimensions(instanceDimension) // to get metrics a specific
													// instance
				.withStatistics("Average").withStartTime(new Date(new Date().getTime() - offsetInMilliseconds))
				.withEndTime(new Date());
		GetMetricStatisticsResult result = ec2Client.getMetricStatistics(request);
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
	private static boolean isTagged(Instance currInstance) {
		List<Tag> tags = currInstance.getTags();
		for (Tag currTag : tags)
			if (currTag.getKey().equals("ASV") || currTag.getKey().equals("ENV"))
				return true;
		return false;
	}

	/*
	 * Returns true if instance is stopped. Other possible states include
	 * pending, running, shutting-down, terminated, stopping
	 */
	public static boolean isStopped(Instance myInstance) {
		InstanceState instanceState = myInstance.getState();
		if (instanceState.getName().equals("stopped"))
			return true;
		else
			return false;
	}

}
