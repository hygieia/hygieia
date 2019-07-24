package com.capitalone.dashboard.collector;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Bean to hold settings specific to the UDeploy collector.
 */
@Component
@ConfigurationProperties(prefix = "udeploy")
public class UDeploySettings {
    private String cron;
    private String token;
    private List<String> usernames = new ArrayList<>();
    private List<String> passwords = new ArrayList<>();
    private List<String> servers = new ArrayList<>();
    private List<String> niceNames = new ArrayList<>();

    public String getCron() {
        return cron;
    }

    public void setCron(String cron) {
        this.cron = cron;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public List<String> getUsernames() {
        return usernames;
    }

    public void setUsername(List<String> usernames) {
        this.usernames = usernames;
    }

    public List<String> getPasswords() {
        return passwords;
    }

    public void setPassword(List<String> passwords) {
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
