package com.capitalone.dashboard.collector;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Bean to hold settings specific to the UDeploy collector.
 */
@Component
@ConfigurationProperties(prefix = "gerrit")
public class GerritSettings {
    private String cron;
    private String host;
    private String user;
    private String password;
    private String statusToCollect;
    private int firstRunHistoryDays;
    private int collectionOffsetMins;


	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getCron() {
        return cron;
    }

    public void setCron(String cron) {
        this.cron = cron;
    }

    public int getFirstRunHistoryDays() {
		return firstRunHistoryDays;
	}

	public void setFirstRunHistoryDays(int firstRunHistoryDays) {
		this.firstRunHistoryDays = firstRunHistoryDays;
	}

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getStatusToCollect() {
        return statusToCollect;
    }

    public void setStatusToCollect(String statusToCollect) {
        this.statusToCollect = statusToCollect;
    }

    public int getCollectionOffsetMins() {
        return collectionOffsetMins;
    }

    public void setCollectionOffsetMins(int collectionOffsetMins) {
        this.collectionOffsetMins = collectionOffsetMins;
    }
}
