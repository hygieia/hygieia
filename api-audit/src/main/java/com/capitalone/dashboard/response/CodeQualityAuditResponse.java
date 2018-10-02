package com.capitalone.dashboard.response;

import com.capitalone.dashboard.model.CodeQuality;
import com.capitalone.dashboard.model.CollectorItemConfigHistory;
import com.capitalone.dashboard.status.CodeQualityAuditStatus;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class CodeQualityAuditResponse extends AuditReviewResponse<CodeQualityAuditStatus> {
    private String url;
    private String name;
    private String message;
    private long lastExecutionTime;
    private CodeQuality codeQuality;
    private Set<String> codeAuthors = new HashSet<>();
    private List<CollectorItemConfigHistory> configChanges = new ArrayList<>();

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

    public Set<String> getCodeAuthors() {
        return codeAuthors;
    }

    public void setCodeAuthors(Set<String> codeAuthors) {
        this.codeAuthors = codeAuthors;
    }

    public List<CollectorItemConfigHistory> getConfigChanges() {
        return configChanges;
    }

    public void setConfigChanges(List<CollectorItemConfigHistory> configChanges) {
        this.configChanges = configChanges;
    }

    public long getLastExecutionTime() {
        return lastExecutionTime;
    }

    public void setLastExecutionTime(long lastExecutionTime) {
        this.lastExecutionTime = lastExecutionTime;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}