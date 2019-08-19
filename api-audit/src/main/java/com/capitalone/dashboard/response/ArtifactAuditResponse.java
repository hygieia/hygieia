package com.capitalone.dashboard.response;

import com.capitalone.dashboard.model.BinaryArtifact;
import com.capitalone.dashboard.status.ArtifactAuditStatus;

import java.util.List;

public class ArtifactAuditResponse extends AuditReviewResponse<ArtifactAuditStatus> {

    private String artifactName;
    private long lastExecutionTime;
    private List<BinaryArtifact> binaryArtifacts;

    public String getArtifactName() {
        return artifactName;
    }

    public void setArtifactName(String artifactName) {
        this.artifactName = artifactName;
    }

    public long getLastExecutionTime() {
        return lastExecutionTime;
    }

    public void setLastExecutionTime(long lastExecutionTime) {
        this.lastExecutionTime = lastExecutionTime;
    }

    public List<BinaryArtifact> getBinaryArtifacts() {
        return binaryArtifacts;
    }

    public void setBinaryArtifacts(List<BinaryArtifact> binaryArtifacts) {
        this.binaryArtifacts = binaryArtifacts;
    }


}
