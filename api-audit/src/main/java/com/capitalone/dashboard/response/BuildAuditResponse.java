package com.capitalone.dashboard.response;

import com.capitalone.dashboard.model.CollectorItemConfigHistory;
import com.capitalone.dashboard.status.BuildAuditStatus;

import java.util.List;

public class BuildAuditResponse extends AuditReviewResponse<BuildAuditStatus> {
    private String url;
    private String environment;
    private long lastBuildTime;
    private List<CollectorItemConfigHistory> configHistory;

    public String getEnvironment() {
        return environment;
    }

    public void setEnvironment(String environment) {
        this.environment = environment;
    }

    public List<CollectorItemConfigHistory> getConfigHistory() {
        return configHistory;
    }

    public void setConfigHistory(List<CollectorItemConfigHistory> configHistory) {
        this.configHistory = configHistory;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public long getLastBuildTime() {
        return lastBuildTime;
    }

    public void setLastBuildTime(long lastBuildTime) {
        this.lastBuildTime = lastBuildTime;
    }
}
