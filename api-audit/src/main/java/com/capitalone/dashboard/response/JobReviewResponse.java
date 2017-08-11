package com.capitalone.dashboard.response;

import com.capitalone.dashboard.model.CollItemCfgHist;

import java.util.List;

public class JobReviewResponse extends AuditReviewResponse {
    private String environment;
    private List<CollItemCfgHist> configHistory;

    public String getEnvironment() {
        return environment;
    }

    public void setEnvironment(String environment) {
        this.environment = environment;
    }

    public List<CollItemCfgHist> getConfigHistory() {
        return configHistory;
    }

    public void setConfigHistory(List<CollItemCfgHist> configHistory) {
        this.configHistory = configHistory;
    }
}
