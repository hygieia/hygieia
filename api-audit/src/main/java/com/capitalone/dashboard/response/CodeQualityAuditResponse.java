package com.capitalone.dashboard.response;

import com.capitalone.dashboard.model.CodeQuality;
import com.capitalone.dashboard.model.CollectorItemConfigHistory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class CodeQualityAuditResponse extends AuditReviewResponse {
    private CodeQuality codeQuality;
    private Set<String> codeAuthors = new HashSet<>();
    private List<CollectorItemConfigHistory> configChanges = new ArrayList<>();

    public CodeQuality getCodeQuality() {
        return codeQuality;
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
}
