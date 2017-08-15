package com.capitalone.dashboard.collector;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Bean to hold settings specific to the Hudson collector.
 */
@Component
@ConfigurationProperties(prefix = "jenkins")
public class HudsonSettings {

	
    private String cron;
    private boolean saveLog = false;
    private List<String> servers;
    private List<String> niceNames;
    //eg. DEV, QA, PROD etc
    private List<String> environments;
    private List<String> usernames;
    private List<String> apiKeys;
    private String dockerLocalHostIP; //null if not running in docker on http://localhost
    private int pageSize;
    @Value("${folderDepth:10}")
    private int folderDepth;

    public String getCron() {
        return cron;
    }

    public void setCron(String cron) {
        this.cron = cron;
    }

    public boolean isSaveLog() {
        return saveLog;
    }

    public void setSaveLog(boolean saveLog) {
        this.saveLog = saveLog;
    }

    public List<String> getServers() {
        return servers;
    }

    public void setServers(List<String> servers) {
        this.servers = servers;
    }
    
    public List<String> getUsernames() {
        return usernames;
    }

    public void setUsernames(List<String> usernames) {
        this.usernames = usernames;
    }
    
    public List<String> getApiKeys() {
        return apiKeys;
    }

    public void setApiKeys(List<String> apiKeys) {
        this.apiKeys = apiKeys;
    }
    
    public void setDockerLocalHostIP(String dockerLocalHostIP) {
        this.dockerLocalHostIP = dockerLocalHostIP;
    }

    public List<String> getNiceNames() {
        return niceNames;
    }

    public void setNiceNames(List<String> niceNames) {
        this.niceNames = niceNames;
    }

    public List<String> getEnvironments() {
        return environments;
    }

    public void setEnvironments(List<String> environments) {
        this.environments = environments;
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
    
    public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}
    
    public int getPageSize() {
		return pageSize;
	}

    public void setFolderDepth(int folderDepth) {
        this.folderDepth = folderDepth;
    }

    public int getFolderDepth() {
        return folderDepth;
    }
}
