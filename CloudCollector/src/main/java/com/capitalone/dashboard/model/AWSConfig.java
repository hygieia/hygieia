package com.capitalone.dashboard.model;

public class AWSConfig extends CollectorItem {
	private static final String ACCESS_KEY = "accessKey";
	private static final String SECRET_KEY = "secretKey";

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
