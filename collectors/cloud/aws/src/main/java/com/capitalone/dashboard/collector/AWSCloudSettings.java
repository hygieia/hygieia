package com.capitalone.dashboard.collector;

import com.amazonaws.regions.Regions;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Bean to hold settings specific to the Cloud collector.
 */
@Component
@ConfigurationProperties(prefix = "aws")
public class AWSCloudSettings {
    private String proxyHost;
    private String proxyPort;
    private String nonProxy;

	private String cron;

	private List<String> validTagKey;
    private List<String> protectedFields;

    private String profile;

    private int historyDays;

    private Map<String,List<String>> filters= new HashMap<>();

    private Regions region;

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

    public int getHistoryDays() {
        return historyDays;
    }

    public void setHistoryDays(int historyDays) {
        this.historyDays = historyDays;
    }

    public String getProxyHost() {
        return proxyHost;
    }

    public void setProxyHost(String proxyHost) {
        this.proxyHost = proxyHost;
    }

    public String getProxyPort() {
        return proxyPort;
    }

    public void setProxyPort(String proxyPort) {
        this.proxyPort = proxyPort;
    }

    public String getNonProxy() {
        return nonProxy;
    }

    public void setNonProxy(String nonProxy) {
        this.nonProxy = nonProxy;
    }

    public List<String> getProtectedFields() {
        return protectedFields;
    }

    public Map<String, List<String>> getFilters() {
        return filters;
    }

    public void setFilters(Map<String, List<String>> filters) {
        this.filters = filters;
    }

    public Regions getRegion() {
        return region;
    }

    public void setRegion(Regions region) {
        this.region = region;
    }
}
