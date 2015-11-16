package com.capitalone.dashboard.model;

import java.util.Date;

public class AWSConfig extends CollectorItem {
	private static final String ACCESS_KEY = "accessKey";
	private static final String SECRET_KEY = "secretKey";
	private static final String CLOUD_PROVIDER= "cloudProvider";
	private static final String LAST_UPDATE_TIME = "lastUpdate";

	
    public String getCloudProvider() {
    	return (String) getOptions().get(CLOUD_PROVIDER);
	}

    public void setCloudProvider(String cloudProvider) {
    	 getOptions().put(CLOUD_PROVIDER, cloudProvider);
    }
    
	public void setAccessKey (String accessKey) {
        getOptions().put(ACCESS_KEY, accessKey);
    }
    
	public String getAccessKey() {
		return (String) getOptions().get(ACCESS_KEY);
	}

    public void setSecretKey (String secretKey) {
        getOptions().put(SECRET_KEY, secretKey);
    }
    
	public String getSecretKey() {
		return (String) getOptions().get(SECRET_KEY);
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
		return getAccessKey().equals(that.getAccessKey())
				&& getSecretKey().equals(that.getSecretKey());
	}

}
