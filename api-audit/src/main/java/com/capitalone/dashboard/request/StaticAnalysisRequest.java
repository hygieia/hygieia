package com.capitalone.dashboard.request;

import javax.validation.constraints.NotNull;

public class StaticAnalysisRequest extends AuditReviewRequest {

   
    
    @NotNull
    private String artifactGroup;

    @NotNull
    private String artifactName;

    @NotNull
    private String artifactVersion;

   
    public String getArtifactGroup() {
        return artifactGroup;
    }

    public void setArtifactGroup(String artifactGroup) {
        this.artifactGroup = artifactGroup;
    }

    public String getArtifactName() {
        return artifactName;
    }

    public void setArtifactName(String artifactName) {
        this.artifactName = artifactName;
    }

    public String getArtifactVersion() {
        return artifactVersion;
    }

    public void setArtifactVersion(String artifactVersion) {
        this.artifactVersion = artifactVersion;
    }
}
