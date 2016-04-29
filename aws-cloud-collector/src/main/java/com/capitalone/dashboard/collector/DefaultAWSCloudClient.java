package com.capitalone.dashboard.collector;

import com.amazonaws.auth.AWSCredentialsProviderChain;
import com.amazonaws.auth.InstanceProfileCredentialsProvider;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.cloudwatch.AmazonCloudWatchClient;
import com.amazonaws.services.cloudwatch.model.Datapoint;
import com.amazonaws.services.cloudwatch.model.Dimension;
import com.amazonaws.services.cloudwatch.model.GetMetricStatisticsRequest;
import com.amazonaws.services.cloudwatch.model.GetMetricStatisticsResult;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.model.*;
import com.capitalone.dashboard.model.CloudInstance;
import com.capitalone.dashboard.model.CloudSubNetwork;
import com.capitalone.dashboard.model.CloudVirtualNetwork;
import com.capitalone.dashboard.model.NameValue;
import com.capitalone.dashboard.repository.CloudInstanceRepository;
import com.capitalone.dashboard.repository.CloudSubNetworkRepository;
import com.capitalone.dashboard.repository.CloudVirtualNetworkRepository;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Collects the instance specific data from AWS.
 */
@Component
public class DefaultAWSCloudClient implements AWSCloudClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(AWSCloudCollectorTask.class);
    private static final long ONE_DAY_MILLI_SECOND = TimeUnit.DAYS.toMillis(1);
    private final AWSCloudSettings settings;


    @Autowired
    public DefaultAWSCloudClient(AWSCloudSettings settings) {
        this.settings = settings;
    }

    /**
     * Calls AWS API and collects instance details.
     *
     * @param repository
     * @return List of CloudInstance
     */
    @Override
    public Map<String, List<CloudInstance>> getCloundInstances(CloudInstanceRepository repository) {
        System.getProperties().put("http.proxyHost", settings.getProxyHost());
        System.getProperties().put("http.proxyPort", settings.getProxyPort());
        System.getProperties().put("https.proxyHost", settings.getProxyHost());
        System.getProperties().put("https.proxyPort", settings.getProxyPort());
        System.getProperties().put("http.nonProxyHosts", settings.getNonProxy());

       // DefaultAWSCredentialsProviderChain creds = new DefaultAWSCredentialsProviderChain();

        AmazonEC2Client ec2Client = new AmazonEC2Client(new AWSCredentialsProviderChain(new InstanceProfileCredentialsProvider(),
                new ProfileCredentialsProvider(settings.getProfile())));

        AmazonCloudWatchClient cloudWatchClient = new AmazonCloudWatchClient(new AWSCredentialsProviderChain(new InstanceProfileCredentialsProvider(),
                new ProfileCredentialsProvider(settings.getProfile())));

        DescribeInstancesResult instanceResult = ec2Client.describeInstances();

//        DescribeImagesResult imageResult = ec2Client.describeImages();
        DescribeVolumesResult volumeResult = ec2Client.describeVolumes();
        Map<String, List<Instance>> ownerInstanceMap = new HashMap<>();
        List<Instance> instanceList = new ArrayList<>();
        List<Reservation> reservations = instanceResult.getReservations();
        for (Reservation currRes : reservations) {
            List<Instance> currInstanceList = currRes.getInstances();
            if (CollectionUtils.isEmpty(ownerInstanceMap.get(currRes.getOwnerId()))) {
                ownerInstanceMap.put(currRes.getOwnerId(), currRes.getInstances());
            } else {
                ownerInstanceMap.get(currRes.getOwnerId()).addAll(currRes.getInstances());
            }
            instanceList.addAll(currInstanceList);
        }
        List<Volume> volumes = volumeResult.getVolumes();
        Map<String, Volume> instanceVolMap = new HashMap<>();
        for (Volume volume : volumes) {
            List<VolumeAttachment> attaches = volume.getAttachments();
            for (VolumeAttachment volumeAttachment : attaches) {
                instanceVolMap
                        .put(volumeAttachment.getInstanceId(), volume);
            }
        }


        Map<String, List<CloudInstance>> returnList = new HashMap<>();
        int i = 0;
        for (String acct : ownerInstanceMap.keySet()) {
            ArrayList<CloudInstance> rawDataList = new ArrayList<>();
            for (Instance currInstance : ownerInstanceMap.get(acct)) {
                i = i + 1;
                LOGGER.info("Collecting instance details for " + i + " of "
                        + instanceList.size() + ". Instance ID=" + currInstance.getInstanceId());
                CloudInstance object = getCloudInstanceDetails(acct,
                        currInstance, instanceVolMap, cloudWatchClient, repository);
                rawDataList.add(object);
            }
            if (CollectionUtils.isEmpty(returnList.get(acct))) {
                returnList.put(acct, rawDataList);
            } else {
                returnList.get(acct).addAll(rawDataList);
            }
        }
        return returnList;
    }

    /**
     * Fill out the CloudInstance object
     *
     * @param account
     * @param currInstance
     * @param instanceVolMap
     * @param cwClient
     * @param repository
     * @return A single CloundInstance
     */
    private CloudInstance getCloudInstanceDetails(String account,
            Instance currInstance, Map<String, Volume> instanceVolMap,
            AmazonCloudWatchClient cwClient, CloudInstanceRepository repository) {

        long lastUpdated = System.currentTimeMillis();
        CloudInstance instance = repository.findByInstanceId(currInstance.getInstanceId());
        if (instance != null) {
            lastUpdated = instance.getLastUpdatedDate();
        }
        CloudInstance object = new CloudInstance();
        object.setAccountNumber(account);
        object.setLastUpdatedDate(System.currentTimeMillis());
        object.setAge(getInstanceAge(currInstance));
        object.setEncrypted(isInstanceVolumneEncrypted(currInstance,
                instanceVolMap));
        object.setCpuUtilization(getInstanceCPUSinceLastRun(currInstance.getInstanceId(), cwClient, lastUpdated));
        object.setTagged(isInstanceTagged(currInstance));
        object.setStopped(isInstanceStopped(currInstance));
        object.setNetworkIn(getLastHourInstanceNetworkIn(currInstance.getInstanceId(), cwClient, lastUpdated));
        object.setNetworkOut(getLastHourIntanceNetworkOut(currInstance.getInstanceId(), cwClient, lastUpdated));
        object.setDiskRead(getLastHourInstanceDiskRead(currInstance.getInstanceId(), cwClient, lastUpdated));
        object.setDiskWrite(getLastInstanceHourDiskWrite(currInstance.getInstanceId(), cwClient));
        // rest of the details
        object.setImageId(currInstance.getImageId());
        object.setInstanceId(currInstance.getInstanceId());
        object.setInstanceType(currInstance.getInstanceType());
        object.setMonitored(false);
        object.setPrivateDns(currInstance.getPrivateDnsName());
        object.setPrivateIp(currInstance.getPrivateIpAddress());
        object.setPublicDns(currInstance.getPublicDnsName());
        object.setPublicIp(currInstance.getPublicIpAddress());
        object.setVirtualNetworkId(currInstance.getVpcId());
        object.setRootDeviceName(currInstance.getRootDeviceName());
        object.setSubnetId(currInstance.getSubnetId());
        List<Tag> tags = currInstance.getTags();

        if (!CollectionUtils.isEmpty(tags)) {
            for (Tag tag : tags) {
                NameValue nv = new NameValue(tag.getKey(), tag.getValue());
                object.getTags().add(nv);
            }
        }
        List<GroupIdentifier> groups = currInstance.getSecurityGroups();
        for (GroupIdentifier gi : groups) {
            object.addSecurityGroups(gi.getGroupName());
        }

        return object;
    }


    //Helper methods

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
                                                      Map<String, Volume> instanceVolMap) {
        Volume vol = instanceVolMap.get(myInstance.getInstanceId());
        return ((vol != null) ? vol.isEncrypted() : false);
    }

    /* Averages CPUUtil every minute for the last hour */
    private static Double getInstanceCPUSinceLastRun(String instanceId,
                                                     AmazonCloudWatchClient ec2Client, long lastUpdated) {

        // long offsetInMilliseconds = Math.min(ONE_DAY_MILLI_SECOND,System.currentTimeMillis() - lastUpdated);
        Dimension instanceDimension = new Dimension().withName("InstanceId")
                .withValue(instanceId);

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, -1);
        long oneDayAgo = cal.getTimeInMillis();

        GetMetricStatisticsRequest request = new GetMetricStatisticsRequest()
                .withMetricName("CPUUtilization")
                .withNamespace("AWS/EC2")
                .withPeriod(60 * 60)
                // one hour
                .withDimensions(instanceDimension)
                // to get metrics a specific
                // instance
                .withStatistics("Average")
                .withStartTime(new Date(new Date().getTime() - 1440 * 1000))
                .withEndTime(new Date());
        GetMetricStatisticsResult result = ec2Client
                .getMetricStatistics(request);
        // to read data
        List<Datapoint> datapoints = result.getDatapoints();
        if (CollectionUtils.isEmpty(datapoints)) return 0.0;
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
        if (CollectionUtils.isEmpty(datapoints)) return 0.0;
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
                .withStartTime(new Date(new Date().getTime() - offsetInMilliseconds))
                .withEndTime(new Date());
        GetMetricStatisticsResult result = ec2Client
                .getMetricStatistics(request);

        // to read data
        List<Datapoint> datapoints = result.getDatapoints();
        if (CollectionUtils.isEmpty(datapoints)) return 0.0;
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
        if (CollectionUtils.isEmpty(datapoints)) return 0.0;
        Datapoint datapoint = datapoints.get(0);
        return datapoint.getAverage();
    }

    /* Averages CPUUtil every minute for the last hour */
    private static Double getLastInstanceHourDiskWrite(String instanceId,
                                                       AmazonCloudWatchClient ec2Client) {
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
                .withStartTime(DateTime.now().minusHours(1).toDate())
                .withEndTime(new Date());
        GetMetricStatisticsResult result = ec2Client
                .getMetricStatistics(request);

        // to read data
        List<Datapoint> datapoints = result.getDatapoints();
        if (CollectionUtils.isEmpty(datapoints)) return 0.0;
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
                .withStartTime(DateTime.now().minusDays(1).toDate())
                .withEndTime(new Date());
        GetMetricStatisticsResult result = ec2Client
                .getMetricStatistics(request);
        // to read data
        List<Datapoint> datapoints = result.getDatapoints();
        if (CollectionUtils.isEmpty(datapoints)) return 0.0;
        Datapoint datapoint = datapoints.get(0);
        return datapoint.getAverage();
    }

    /* If the instance is tagged with correct */
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
        return (instanceState.getName().equals("stopped"));
    }


    @Override
    public CloudVirtualNetwork getCloudVPC(CloudVirtualNetworkRepository repository) {
        return null;
    }

    @Override
    public CloudSubNetwork getCloudSubnet(CloudSubNetworkRepository repository) {
        return null;
    }
}
