package com.capitalone.dashboard.collector;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Bean to hold settings specific to the Cloud collector.
 */
@Component
@ConfigurationProperties(prefix = "aws")
public class AWSCloudSettings {
	private String cron;

	private List<String> validTagKey;

    private String profile;

	public List<String> getValidTagKey() {
		return validTagKey;
	}

	public void setValidTagKey(List<String> validTagKey) {
		this.validTagKey = validTagKey;
	}

	public String getCron() {
		return cron;
	}

	public void setCron(String cron) {
		this.cron = cron;
	}

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }
}
