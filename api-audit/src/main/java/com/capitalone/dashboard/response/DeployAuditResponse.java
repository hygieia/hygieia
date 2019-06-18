package com.capitalone.dashboard.response;

import com.capitalone.dashboard.model.Build;
import com.capitalone.dashboard.status.DeployAuditStatus;

public class DeployAuditResponse extends AuditReviewResponse<DeployAuditStatus> {
    private String url;
    private long lastBuildTime;
    private Build build;


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

    public Build getBuild() {
        return build;
    }

    public void setBuild(Build build) {
        this.build = build;
    }


}
