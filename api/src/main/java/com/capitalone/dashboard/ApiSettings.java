package com.capitalone.dashboard;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import com.capitalone.dashboard.model.PipelineStage;

@Component
@ConfigurationProperties
public class ApiSettings {
    /**
     * TODO The property name 'key' is too vague. This key is used only for encryption. Would suggest to rename it to
     * encryptionKey to be specific. For now (for backwards compatibility) keeping it as it was.
     */
    private String key;
    private boolean logRequest;
    
    // Start global config
    /*
     * Location to place configurations that are consumed by the UI and API. May be moved into a separate location
     * (such as a collection in mongo) in the future.
     */
    @Value("${systemConfig.multipleDeploymentServers:false}")
    private boolean multipleDeploymentServers;
    
    @Value("${systemConfig.systemStages}")
    private String[] systemStages;
    // End global config
    
    private List<PipelineStage> systemStagesCache;
    
    public String getKey() {
        return key;
    }

    public void setKey(final String key) {
        this.key = key;
    }

    public boolean isLogRequest() {
        return logRequest;
    }

    public void setLogRequest(boolean logRequest) {
        this.logRequest = logRequest;
    }
    
    public boolean isMultipleDeploymentServers() {
    	return multipleDeploymentServers;
    }
    
    public List<PipelineStage> getSystemStages() {
    	return systemStagesCache;
    }
    
    @PostConstruct
    private void populateSystemStagesCache() {
    	List<PipelineStage> tmp = new ArrayList<>();
    	String[] systemStagesTmp = systemStages;
    	
    	if (systemStagesTmp != null) {
    		for (String stageName : systemStagesTmp) {
    			tmp.add(PipelineStage.valueOf(stageName));
    		}
    	}
    	
    	systemStagesCache = tmp;
    }
}
