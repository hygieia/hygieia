package com.capitalone.dashboard.model;

public class AWSConfig extends CollectorItem {
    private static final String CLOUD_PROVIDER = "cloudProvider";
    private static final String LAST_UPDATE_TIME = "lastUpdate";


    public String getCloudProvider() {
        return (String) getOptions().get(CLOUD_PROVIDER);
    }

    public void setCloudProvider(String cloudProvider) {
        getOptions().put(CLOUD_PROVIDER, cloudProvider);
    }

    public long getLastUpdateTime() {
        Object latest = getOptions().get(LAST_UPDATE_TIME);
        return (!(latest == null) ? (long) latest : 0);
    }

    public void setLastUpdateTime(long millisecond) {
        getOptions().put(LAST_UPDATE_TIME, millisecond);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        AWSConfig that = (AWSConfig) o;
        return getCloudProvider().equals(that.getCloudProvider());
    }

}
