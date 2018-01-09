package com.capitalone.dashboard.request;

import javax.validation.constraints.NotNull;

public class CodeQualityAuditRequest extends AuditReviewRequest {

   
    
    @NotNull
    private String projectName;

	@NotNull
    private String artifactVersion;
	
    
	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

    public String getArtifactVersion() {
        return artifactVersion;
    }

    public void setArtifactVersion(String artifactVersion) {
        this.artifactVersion = artifactVersion;
    }
}
