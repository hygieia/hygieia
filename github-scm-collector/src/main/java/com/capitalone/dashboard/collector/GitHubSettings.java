package com.capitalone.dashboard.collector;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Bean to hold settings specific to the UDeploy collector.
 */
@Component
@ConfigurationProperties(prefix = "github")
public class GitHubSettings {
    private String cron;
    private String host;
    private String key;
    private int firstRunHistoryDays;
    private String[] notBuiltCommits;


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

	public String getKey() {
		return key;
	}
	
	public void setKey(String key) {
		this.key = key;
	}
	
    public int getFirstRunHistoryDays() {
		return firstRunHistoryDays;
	}

	public void setFirstRunHistoryDays(int firstRunHistoryDays) {
		this.firstRunHistoryDays = firstRunHistoryDays;
	}

    public String[] getNotBuiltCommits() {
        return notBuiltCommits;
    }

    public void setNotBuiltCommits(String[] notBuiltCommits) {
        this.notBuiltCommits = notBuiltCommits;
    }
}
