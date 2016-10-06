package com.capitalone.dashboard.collector;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Bean to hold settings specific to the XLDeploy collector.
 */
@Component
@ConfigurationProperties(prefix = "xldeploy")
public class XLDeploySettings {
    private String cron;
    private List<String> usernames;
    private List<String> passwords;
    private List<String> servers;
    private List<String> niceNames;

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

    public void setPasswords(List<String> passwords) {
        this.passwords = passwords;
    }

    public List<String> getServers() {
        return servers;
    }

    public void setServers(List<String> servers) {
        this.servers = servers;
    }
    
    public List<String> getNiceNames() {
    	return niceNames;
    }
    
    public void setNiceNames(List<String> niceNames) {
    	this.niceNames = niceNames;
    }
}
