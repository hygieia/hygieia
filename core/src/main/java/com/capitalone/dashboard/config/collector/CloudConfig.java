package com.capitalone.dashboard.config.collector;

import com.capitalone.dashboard.model.CollectorItem;
import com.capitalone.dashboard.model.NameValue;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

public class CloudConfig extends CollectorItem {
    private static final String CLOUD_PROVIDER = "provider";
    private static final List<NameValue> TAGS = new ArrayList<>();

    private static final int AGE_ERROR_THRESHOLD_DEFAULT = 60;
    private static final int AGE_ALERT_THRESHOLD_DEFAULT = 45;

    private static final double CPU_ERROR_THRESHOLD_DEFAULT = 80.0;
    private static final double CPU_ALERT_THRESHOLD_DEFAULT = 50.0;

    private static final double MEMORY_ERROR_THRESHOLD_DEFAULT = 80.0;
    private static final double MEMORY_ALERT_THRESHOLD_DEFAULT = 50.0;

    private static final int DISK_IO_ERROR_THRESHOLD_DEFAULT = 80;
    private static final int DISK_IO_ALERT_THRESHOLD_DEFAULT = 50;

    private static final int NETWORK_IO_ERROR_THRESHOLD_DEFAULT = 80;
    private static final int NETWORK_IO_ALERT_THRESHOLD_DEFAULT = 50;

//    private static final int SUBNET_IP_ERROR_THRESHOLD_DEFAULT = 80;
//    private static final int SUBNET_IP_ALERT_THRESHOLD_DEFAULT = 50;

    private static final String AGE_ERROR = "ageError";
    private static final String AGE_ALERT = "ageAlert";
    private static final String CPU_ERROR = "cpuError";
    private static final String CPU_ALERT = "cpuAlert";
    private static final String MEMORY_ERROR = "memoryError";
    private static final String MEMORY_ALERT = "memoryAlert";
    private static final String DISKIO_ERROR = "diskIOError";
    private static final String DISKIO_ALERT = "diskIOAlert";
    private static final String NETWORKIO_ERROR = "networkIOError";
    private static final String NETWORKIO_ALERT = "networkIOAlert";

    public String getCloudProvider() {
        return (String) getOptions().get(CLOUD_PROVIDER);
    }

    public void setCloudProvider(String cloudProvider) {
        getOptions().put(CLOUD_PROVIDER, cloudProvider);
    }

    public int getAgeError() {
        return (getOptions().get(AGE_ERROR) != null) ? (int) getOptions().get(AGE_ERROR) : AGE_ERROR_THRESHOLD_DEFAULT;
    }

    public  int getAgeAlert() {
        return (getOptions().get(AGE_ALERT) != null) ? (int) getOptions().get(AGE_ALERT) : AGE_ALERT_THRESHOLD_DEFAULT;
    }

    public double getCpuError() {
        return (getOptions().get(CPU_ERROR) != null) ? (double) getOptions().get(CPU_ERROR) : CPU_ERROR_THRESHOLD_DEFAULT;
    }

    public double getCpuAlert() {
        return (getOptions().get(CPU_ALERT) != null) ? (double) getOptions().get(CPU_ALERT) : CPU_ALERT_THRESHOLD_DEFAULT;
    }

    public double  getMemoryError() {
        return (getOptions().get(MEMORY_ERROR) != null) ? (long) getOptions().get(MEMORY_ERROR) : MEMORY_ERROR_THRESHOLD_DEFAULT;
    }

    public double getMemoryAlert() {
        return (getOptions().get(MEMORY_ALERT) != null) ? (long) getOptions().get(MEMORY_ALERT) : MEMORY_ALERT_THRESHOLD_DEFAULT;

    }

    public long getDiskioError() {
        return (getOptions().get(DISKIO_ERROR) != null) ? (long) getOptions().get(DISKIO_ERROR) : DISK_IO_ERROR_THRESHOLD_DEFAULT;
    }

    public long getDiskioAlert() {
        return (getOptions().get(DISKIO_ALERT) != null) ? (long) getOptions().get(DISKIO_ALERT) : DISK_IO_ALERT_THRESHOLD_DEFAULT;
    }

    public long getNetworkioError() {
        return (getOptions().get(NETWORKIO_ERROR) != null) ? (long) getOptions().get(NETWORKIO_ERROR) : NETWORK_IO_ERROR_THRESHOLD_DEFAULT;
    }

    public long getNetworkioAlert() {
        return (getOptions().get(NETWORKIO_ALERT) != null) ? (long) getOptions().get(NETWORKIO_ALERT) : NETWORK_IO_ALERT_THRESHOLD_DEFAULT;
    }

    public List<NameValue> getTags() {
        return TAGS;
    }

    public String getValue(String name) {
        for (NameValue nv : TAGS) {
            if (nv.getName().equalsIgnoreCase(name)) return nv.getValue();
        }
        return "";
    }

    public boolean allTagsMatch(List<NameValue> tags1, List<NameValue> tags2) {
        if (CollectionUtils.isEmpty(tags1) && CollectionUtils.isEmpty(tags2)) return true;
        if (CollectionUtils.isEmpty(tags1)) return false;
        if (CollectionUtils.isEmpty(tags2)) return false;
        if (tags1.size() != tags2.size()) return false;
        for (NameValue nv1 : tags1) {
            boolean match1 = false;
            for (NameValue nv2 : tags2) {
                if (nv1.equals(nv2)) {
                    match1 = true;
                    break;
                }
            }
            if (!match1) return false;
        }
        return true;
    }

    private boolean allTagsMatch(List<NameValue> tags2) {
        List<NameValue> tags1 = getTags();
        return allTagsMatch(tags1, tags2);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        CloudConfig that = (CloudConfig) o;
        return getCloudProvider().equals(that.getCloudProvider()) && allTagsMatch(that.getTags());
    }

    @Override
    public int hashCode(){
        int result =  getCloudProvider().hashCode();
        result = 31 * result + getId().hashCode();
        return result;
    }

}
