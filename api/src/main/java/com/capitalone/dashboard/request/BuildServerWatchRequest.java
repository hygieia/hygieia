package com.capitalone.dashboard.request;

import org.hibernate.validator.constraints.NotEmpty;

public class BuildServerWatchRequest {

    @NotEmpty
    private String buildServerUrl;
    private String collectorName;

    public String getBuildServerUrl() {
        return buildServerUrl;
    }

    public void setBuildServerUrl(String buildServerUrl) {
        this.buildServerUrl = buildServerUrl;
    }

    public String getCollectorName() {
        return collectorName;
    }

    public void setCollectorName(String collectorName) {
        this.collectorName = collectorName;
    }

}
