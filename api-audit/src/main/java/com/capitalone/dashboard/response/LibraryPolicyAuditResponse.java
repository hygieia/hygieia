package com.capitalone.dashboard.response;

import com.capitalone.dashboard.model.LibraryPolicyResult;
import com.capitalone.dashboard.status.LibraryPolicyAuditStatus;

public class LibraryPolicyAuditResponse extends AuditReviewResponse<LibraryPolicyAuditStatus> {

    private String url;
    private long lastExecutionTime;
    private LibraryPolicyResult libraryPolicyResult;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public long getLastExecutionTime() {
        return lastExecutionTime;
    }

    public void setLastExecutionTime(long lastExecutionTime) {
        this.lastExecutionTime = lastExecutionTime;
    }

    public LibraryPolicyResult getLibraryPolicyResult() {
        return libraryPolicyResult;
    }

    public void setLibraryPolicyResult(LibraryPolicyResult libraryPolicyResult) {
        this.libraryPolicyResult = libraryPolicyResult;
    }
}
