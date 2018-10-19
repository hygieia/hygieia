package com.capitalone.dashboard.collector;

import com.capitalone.dashboard.model.ArtifactoryCollectionMode;
import com.capitalone.dashboard.model.ServerSetting;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Bean to hold settings specific to the Artifactory collector.
 */
@Component
@ConfigurationProperties(prefix = "artifactory")
public class ArtifactorySettings {
    private String cron;
    List<ServerSetting> servers;
    ArtifactoryCollectionMode mode;
    String endpoint;
    long offSet;


    public String getCron() {
        return cron;
    }

    public void setCron(String cron) {
        this.cron = cron;
    }

    public List<ServerSetting> getServers() {
        return servers;
    }

    public void setServers(List<ServerSetting> servers) {
        this.servers = servers;
    }

    public ArtifactoryCollectionMode getMode() {
        return mode;
    }

    public void setMode(ArtifactoryCollectionMode mode) {
        this.mode = mode;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }


    public long getOffSet() {
        return offSet;
    }

    public void setOffSet(long offSet) {
        this.offSet = offSet;
    }


}
