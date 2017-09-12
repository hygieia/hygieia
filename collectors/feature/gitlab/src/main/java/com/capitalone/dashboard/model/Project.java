package com.capitalone.dashboard.model;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class Project {

    private String teamId;
    private String projectId;
    
    public Project(String teamId, String projectId) {
        this.teamId = teamId;
        this.projectId = projectId;
    }
    
    public String getTeamId() {
        return teamId;
    }
    public void setTeamId(String teamId) {
        this.teamId = teamId;
    }
    public String getProjectId() {
        return projectId;
    }
    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;

        Project that = (Project) obj;
        EqualsBuilder builder = new EqualsBuilder();
        return builder.append(teamId, that.teamId).append(projectId, that.projectId).build();
    }
    
    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(getTeamId()).append(getProjectId()).toHashCode();
    }
}
