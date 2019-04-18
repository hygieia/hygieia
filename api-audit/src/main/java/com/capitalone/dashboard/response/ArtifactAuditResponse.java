package com.capitalone.dashboard.response;

import com.capitalone.dashboard.model.BinaryArtifact;
import com.capitalone.dashboard.status.ArtifactAuditStatus;

public class ArtifactAuditResponse extends AuditReviewResponse<ArtifactAuditStatus> {

    private String artifactName;
    private long lastExecutionTime;
    private BinaryArtifact binaryArtifact;

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

    public BinaryArtifact getBinaryArtifact() {
        return binaryArtifact;
    }

    public void setBinaryArtifact(BinaryArtifact binaryArtifact) {
        this.binaryArtifact = binaryArtifact;
    }


}
