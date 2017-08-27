package com.capitalone.dashboard.collector;

import com.amazonaws.auth.AWSCredentialsProviderChain;
import com.amazonaws.auth.InstanceProfileCredentialsProvider;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.autoscaling.AmazonAutoScaling;
import com.amazonaws.services.autoscaling.AmazonAutoScalingClient;
import com.amazonaws.services.autoscaling.model.AutoScalingInstanceDetails;
import com.amazonaws.services.autoscaling.model.DescribeAutoScalingInstancesResult;
import com.amazonaws.services.cloudwatch.AmazonCloudWatchClient;
import com.amazonaws.services.cloudwatch.model.Datapoint;
import com.amazonaws.services.cloudwatch.model.Dimension;
import com.amazonaws.services.cloudwatch.model.GetMetricStatisticsRequest;
import com.amazonaws.services.cloudwatch.model.GetMetricStatisticsResult;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.model.DescribeInstancesResult;
import com.amazonaws.services.ec2.model.DescribeVolumesResult;
import com.amazonaws.services.ec2.model.GroupIdentifier;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.InstanceState;
import com.amazonaws.services.ec2.model.Reservation;
import com.amazonaws.services.ec2.model.Tag;
import com.amazonaws.services.ec2.model.Volume;
import com.amazonaws.services.ec2.model.VolumeAttachment;
import com.capitalone.dashboard.model.CloudInstance;
import com.capitalone.dashboard.model.CloudSubNetwork;
import com.capitalone.dashboard.model.CloudVirtualNetwork;
import com.capitalone.dashboard.model.CloudVolumeStorage;
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
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Collects the instance specific data from AWS.
 */
@Component
public class DefaultAWSCloudClient implements AWSCloudClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(AWSCloudCollectorTask.class);
    private static final long ONE_DAY_MILLI_SECOND = TimeUnit.DAYS.toMillis(1);
    private final AWSCloudSettings settings;
    private static AmazonEC2Client ec2Client;
    private static AmazonCloudWatchClient cloudWatchClient;
    private static AmazonAutoScaling autoScalingClient;
    private static final String NO_ACCOUNT = "NOACCOUNT";


    @Autowired
    public DefaultAWSCloudClient(AWSCloudSettings settings) {
        this.settings = settings;
        setClients();
    }


    public final void setClients() {
        System.getProperties().put("http.proxyHost", settings.getProxyHost());
        System.getProperties().put("http.proxyPort", settings.getProxyPort());
        System.getProperties().put("https.proxyHost", settings.getProxyHost());
        System.getProperties().put("https.proxyPort", settings.getProxyPort());
        System.getProperties().put("http.nonProxyHosts", settings.getNonProxy());

        ec2Client = new AmazonEC2Client(new AWSCredentialsProviderChain(new ProfileCredentialsProvider(settings.getProfile()),
                new InstanceProfileCredentialsProvider()));

        cloudWatchClient = new AmazonCloudWatchClient(new AWSCredentialsProviderChain(new ProfileCredentialsProvider(settings.getProfile()),
                new InstanceProfileCredentialsProvider()));
        autoScalingClient = new AmazonAutoScalingClient(new AWSCredentialsProviderChain(new ProfileCredentialsProvider(settings.getProfile()),
                new InstanceProfileCredentialsProvider()));
    }

    /**
     * Calls AWS API and collects instance details.
     *
     * @param repository
     * @return List of CloudInstance
     */
    @Override
    public Map<String, List<CloudInstance>> getCloudInstances(CloudInstanceRepository repository) {
        DescribeInstancesResult instanceResult = ec2Client.describeInstances();
        DescribeAutoScalingInstancesResult autoScaleResult = autoScalingClient.describeAutoScalingInstances();
        List<AutoScalingInstanceDetails> autoScalingInstanceDetails = autoScaleResult.getAutoScalingInstances();
        Map<String, String> autoScaleMap = new HashMap<>();
        for (AutoScalingInstanceDetails ai : autoScalingInstanceDetails) {
            autoScaleMap.put(ai.getInstanceId(), ai.getAutoScalingGroupName());
        }

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

        Map<String, List<CloudInstance>> returnList = new HashMap<>();
        int i = 0;
        for (String acct : ownerInstanceMap.keySet()) {
            ArrayList<CloudInstance> rawDataList = new ArrayList<>();
            for (Instance currInstance : ownerInstanceMap.get(acct)) {
                i = i + 1;
                LOGGER.info("Collecting instance details for " + i + " of "
                        + instanceList.size() + ". Instance ID=" + currInstance.getInstanceId());
                CloudInstance object = getCloudInstanceDetails(acct,
                        currInstance, autoScaleMap, repository);
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
     * @param account      Cloud Account
     * @param currInstance Cloud Instance
     * @param repository   CloudInstnceRepository
     * @return A single CloudInstance
     */
    private CloudInstance getCloudInstanceDetails(String account,
                                                  Instance currInstance, Map<String, String> autoScaleMap, CloudInstanceRepository repository) {

        long lastUpdated = System.currentTimeMillis();
        CloudInstance instance = repository.findByInstanceId(currInstance.getInstanceId());
        if (instance != null) {
            lastUpdated = instance.getLastUpdatedDate();
        }
        CloudInstance object = new CloudInstance();
        object.setAccountNumber(account);
        object.setLastUpdatedDate(System.currentTimeMillis());
        object.setAge(getInstanceAge(currInstance));
        object.setCpuUtilization(getInstanceCPUSinceLastRun(currInstance.getInstanceId(), lastUpdated));
        object.setIsTagged(isInstanceTagged(currInstance));
        object.setIsStopped(isInstanceStopped(currInstance));
        object.setNetworkIn(getLastHourInstanceNetworkIn(currInstance.getInstanceId(), lastUpdated));
        object.setNetworkOut(getLastHourIntanceNetworkOut(currInstance.getInstanceId(), lastUpdated));
        object.setDiskRead(getLastHourInstanceDiskRead(currInstance.getInstanceId(), lastUpdated));
        object.setDiskWrite(getLastInstanceHourDiskWrite(currInstance.getInstanceId()));
        // rest of the details
        object.setImageId(currInstance.getImageId());
        object.setInstanceId(currInstance.getInstanceId());
        object.setInstanceType(currInstance.getInstanceType());
        object.setIsMonitored("enabled".equalsIgnoreCase(currInstance.getMonitoring().getState()));
        object.setPrivateDns(currInstance.getPrivateDnsName());
        object.setPrivateIp(currInstance.getPrivateIpAddress());
        object.setPublicDns(currInstance.getPublicDnsName());
        object.setPublicIp(currInstance.getPublicIpAddress());
        object.setVirtualNetworkId(currInstance.getVpcId());
        object.setRootDeviceName(currInstance.getRootDeviceName());
        object.setSubnetId(currInstance.getSubnetId());
        object.setAutoScaleName(autoScaleMap.getOrDefault(currInstance.getInstanceId(), "NONE"));
        object.setLastAction("ADD");
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

    /**
     * Returns a map of account number of list of volumes associated with the account
     *
     * @param instanceToAccountMap
     * @return Map of account number and a list of Volumes
     */

    public Map<String, List<CloudVolumeStorage>> getCloudVolumes(Map<String, String> instanceToAccountMap) {
        Map<String, List<CloudVolumeStorage>> returnMap = new HashMap<>();
        DescribeVolumesResult volumeResult = ec2Client.describeVolumes();
        for (Volume v : volumeResult.getVolumes()) {
            CloudVolumeStorage object = new CloudVolumeStorage();
            for (VolumeAttachment va : v.getAttachments()) {
                object.getAttachInstances().add(va.getInstanceId());
            }
            String account = NO_ACCOUNT;
            //Get any instance id if any and get corresponding account number
            if (!CollectionUtils.isEmpty(object.getAttachInstances()) &&
                    !StringUtils.isEmpty(instanceToAccountMap.get(object.getAttachInstances().get(0)))) {
                account = instanceToAccountMap.get(object.getAttachInstances().get(0));
            }
            object.setAccountNumber(account);
            object.setZone(v.getAvailabilityZone());
            object.setAccountNumber(account);
            object.setCreationDate(v.getCreateTime().getTime());
            object.setEncrypted(v.isEncrypted());
            object.setSize(v.getSize());
            object.setStatus(v.getState());
            object.setType(v.getVolumeType());
            object.setVolumeId(v.getVolumeId());
            List<Tag> tags = v.getTags();
            if (!CollectionUtils.isEmpty(tags)) {
                for (Tag tag : tags) {
                    NameValue nv = new NameValue(tag.getKey(), tag.getValue());
                    object.getTags().add(nv);
                }
            }
            if (CollectionUtils.isEmpty(returnMap.get(object.getAccountNumber()))) {
                List<CloudVolumeStorage> temp = new ArrayList<>();
                temp.add(object);
                returnMap.put(account, temp);
            } else {
                returnMap.get(account).add(object);
            }
        }
        return returnMap;
    }


    //Helper methods

    /* Gets the age in days of an instance */
    private static int getInstanceAge(Instance myInstance) {
        Date launchDate = myInstance.getLaunchTime();
        long diffInMillies = System.currentTimeMillis() - launchDate.getTime();
        return (int) TimeUnit.DAYS
                .convert(diffInMillies, TimeUnit.MILLISECONDS);
    }


    /* Averages CPUUtil every minute for the last hour */
    @SuppressWarnings("PMD.UnusedFormalParameter")
    @Override
    public Double getInstanceCPUSinceLastRun(String instanceId, long lastUpdated) {

//        long offsetInMilliseconds = Math.min(ONE_DAY_MILLI_SECOND,System.currentTimeMillis() - lastUpdated);
        Dimension instanceDimension = new Dimension().withName("InstanceId")
                .withValue(instanceId);

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, -1);
//        long oneDayAgo = cal.getTimeInMillis();

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
        GetMetricStatisticsResult result = cloudWatchClient
                .getMetricStatistics(request);
        // to read data
        List<Datapoint> datapoints = result.getDatapoints();
        if (CollectionUtils.isEmpty(datapoints)) return 0.0;
        Datapoint datapoint = datapoints.get(0);
        return datapoint.getAverage();
    }

    /* Averages CPUUtil every minute for the last hour */
    @Override
    public Double getLastHourInstanceNetworkIn(String instanceId,
                                               long lastUpdated) {
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
        GetMetricStatisticsResult result = cloudWatchClient
                .getMetricStatistics(request);
        // to read data
        List<Datapoint> datapoints = result.getDatapoints();
        if (CollectionUtils.isEmpty(datapoints)) return 0.0;
        Datapoint datapoint = datapoints.get(0);
        return datapoint.getAverage();
    }

    /* Averages CPUUtil every minute for the last hour */
    @Override
    public Double getLastHourIntanceNetworkOut(String instanceId, long lastUpdated) {
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
        GetMetricStatisticsResult result = cloudWatchClient
                .getMetricStatistics(request);

        // to read data
        List<Datapoint> datapoints = result.getDatapoints();
        if (CollectionUtils.isEmpty(datapoints)) return 0.0;
        Datapoint datapoint = datapoints.get(0);
        return datapoint.getAverage();
    }

    /* Averages CPUUtil every minute for the last hour */
    @Override
    public Double getLastHourInstanceDiskRead(String instanceId,
                                              long lastUpdated) {


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
        GetMetricStatisticsResult result = cloudWatchClient
                .getMetricStatistics(request);

        // to read data
        List<Datapoint> datapoints = result.getDatapoints();
        if (CollectionUtils.isEmpty(datapoints)) return 0.0;
        Datapoint datapoint = datapoints.get(0);
        return datapoint.getAverage();
    }

    /* Averages CPUUtil every minute for the last hour */
    @Override
    public Double getLastInstanceHourDiskWrite(String instanceId) {
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
        GetMetricStatisticsResult result = cloudWatchClient
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


    @Override
    public Double get24HourInstanceEstimatedCharge() {
        Dimension instanceDimension = new Dimension().withName("Currency")
                .withValue("USD");
        Dimension typeDimension = new Dimension().withName("ServiceName").withValue("AmazonEC2");

        GetMetricStatisticsRequest request = new GetMetricStatisticsRequest()
                .withMetricName("EstimatedCharges")
                .withNamespace("AWS/Billing")
                .withPeriod(60 * 60 * 24)
                //
                // one hour
                .withDimensions(instanceDimension, typeDimension)
                // to get metrics a specific
                // instance
                .withStatistics("Average")
                .withStartTime(DateTime.now().minusDays(1).toDate())
                .withEndTime(new Date());
        GetMetricStatisticsResult result = cloudWatchClient
                .getMetricStatistics(request);
        // to read data
        List<Datapoint> datapoints = result.getDatapoints();
        if (datapoints.isEmpty()) {
            // This instance has no CPU Util
            return 0.0;
        }
        Datapoint datapoint = datapoints.get(0);
        return datapoint.getAverage();
    }

    /*
     * Returns true if instance is stopped. Other possible states include
     * pending, running, shutting-down, terminated, stopping
     */
    private boolean isInstanceStopped(Instance myInstance) {
        InstanceState instanceState = myInstance.getState();
        return (instanceState.getName().equals("stopped"));
    }


    @Override
    public CloudVirtualNetwork getCloudVPC(CloudVirtualNetworkRepository repository) {
        //Not implemented yet.
        return null;
    }

    @Override
    public CloudSubNetwork getCloudSubnet(CloudSubNetworkRepository repository) {

        //Not implemented yet
        return null;
    }

    public void setEc2Client(AmazonEC2Client ec2Client) {
        DefaultAWSCloudClient.ec2Client = ec2Client;
    }

    public  void setCloudWatchClient(AmazonCloudWatchClient cloudWatchClient) {
        DefaultAWSCloudClient.cloudWatchClient = cloudWatchClient;
    }

    public  void setAutoScalingClient(AmazonAutoScaling autoScalingClient) {
        DefaultAWSCloudClient.autoScalingClient = autoScalingClient;
    }
}
