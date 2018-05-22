package com.capitalone.dashboard.response;

import com.capitalone.dashboard.model.CodeQuality;
import com.capitalone.dashboard.status.CodeQualityAuditStatus;

public class SecurityReviewAuditResponse extends AuditReviewResponse<CodeQualityAuditStatus> {

    private String url;
    private long lastExecutionTime;
    private CodeQuality codeQuality;

    public CodeQuality getCodeQuality() {
        return codeQuality;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setCodeQuality(CodeQuality codeQuality) {
        this.codeQuality = codeQuality;
    }

    public long getLastExecutionTime() {
        return lastExecutionTime;
    }

    public void setLastExecutionTime(long lastExecutionTime) {
        this.lastExecutionTime = lastExecutionTime;
    }
}
