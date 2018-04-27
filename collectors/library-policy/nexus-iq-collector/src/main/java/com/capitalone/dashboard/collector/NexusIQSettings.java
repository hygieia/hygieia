package com.capitalone.dashboard.collector;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Bean to hold settings specific to the Sonar collector.
 */
@Component
@ConfigurationProperties(prefix = "nexusiq")
public class NexusIQSettings {

	private String cron;
    private List<String> usernames;
    private List<String> passwords;
    private boolean selectStricterLicense;
    private List<String> servers;

    public String getCron() {
        return cron;
    }

    public void setCron(String cron) {
        this.cron = cron;
    }

    public List<String> getUsernames() {
		return usernames;
	}

	public void setUsernames(List<String> usernames) {
		this.usernames = usernames;
	}

	public List<String> getPasswords() {
		return passwords;
	}

	public void setPassword(List<String> passwords) {
		this.passwords = passwords;
	}

	public boolean isSelectStricterLicense() {
        return selectStricterLicense;
    }

    public void setSelectStricterLicense(boolean selectStricterLicense) {
        this.selectStricterLicense = selectStricterLicense;
    }

    public List<String> getServers() {
        return servers;
    }

    public void setServers(List<String> servers) {
        this.servers = servers;
    }

}
