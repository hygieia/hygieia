package com.capitalone.dashboard.collector;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import java.util.List;

/**
 * Bean to hold settings specific to the Jenkins collector.
 *
 * Created by ltm688 on 2/12/15.
 */
@Component
@ConfigurationProperties(prefix = "jenkins-cucumber")
public class JenkinsSettings {

    private String cron;
    private List<String> servers;
    private String cucumberJsonRegex = "cucumber.json";
    private String username;
    private String apiKey;
    private String dockerLocalHostIP; //null if not running in docker on http://localhost
    
    public String getCron() {
        return cron;
    }

    public void setCron(String cron) {
        this.cron = cron;
    }

    public List<String> getServers() {
        return servers;
    }

    public void setServers(List<String> servers) {
        this.servers = servers;
    }

    public String getCucumberJsonRegex() {
        return cucumberJsonRegex;
    }

    public void setCucumberJsonRegex(String cucumberJsonRegex) {
        this.cucumberJsonRegex = cucumberJsonRegex;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }
    
    public void setDockerLocalHostIP(String dockerLocalHostIP) {
        this.dockerLocalHostIP = dockerLocalHostIP;
    }

	//Docker NATs the real host localhost to 10.0.2.2 when running in docker
	//as localhost is stored in the JSON payload from jenkins we need
	//this hack to fix the addresses
    public String getDockerLocalHostIP() {
    	
    		//we have to do this as spring will return NULL if the value is not set vs and empty string
    	String localHostOverride = "";
    	if (dockerLocalHostIP != null) {
    		localHostOverride = dockerLocalHostIP;
    	}
        return localHostOverride;
    }

}
