package com.capitalone.dashboard.collector;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Bean to hold settings specific to the Sonar collector.
 */
@Component
@ConfigurationProperties(prefix = "reportportal")
public class ReportPortalSettings {
    private String cron;
    private String username;
    private String password;
    private List<String> servers;
    private String bearerToken;
    private List<String> niceNames;
    private String projectName;
	//private String instanceUrl;
    
    public String getProjectName() {
    	return projectName;
    }
    public void setProjectName(String projectName) {
    	this.projectName=projectName;
    }
    
    public String getBearerToken() {
    	return bearerToken;
    }
    public void setBearerToken(String bearerToken) {
    	this.bearerToken=bearerToken;
    }
    public String getCron() {
        return cron;
    }

    public void setCron(String cron) {
        this.cron = cron;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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
