package com.capitalone.dashboard.model;

public class FeatureBoard extends CollectorItem {

    public static final String TOOL_TYPE = "featureTool";
    public static final String PROJECT_NAME = "projectName";
    public static final String PROJECT_ID = "projectId";
    public static final String TEAM_NAME = "teamName";
    public static final String TEAM_ID = "teamId";
    public static final String ESTIMATE_METRIC_TYPE = "estimateMetricType";
    public static final String SPRINT_TYPE = "sprintType";
    public static final String LIST_TYPE = "listType";
    public static final String SHOW_STATUS = "showStatus";


    public String getToolType() {
        return (String) getOptions().get(TOOL_TYPE);
    }
    public void setToolType(String featureTool) {
        getOptions().put(TOOL_TYPE, featureTool);
    }

    public String getProjectName() {
        return (String) getOptions().get(PROJECT_NAME);
    }
    public void setProjectName(String projectName) {
        getOptions().put(PROJECT_NAME, projectName);
    }

    public String getProjectId() {
        return (String) getOptions().get(PROJECT_ID);
    }
    public void setProjectId(String projectId) {
        getOptions().put(PROJECT_ID, projectId);
    }

    public String getTeamName() {
        return (String) getOptions().get(TEAM_NAME);
    }
    public void setTeamName(String teamName) {
        getOptions().put(TEAM_NAME, teamName);
    }

    public String getTeamId() {
        return (String) getOptions().get(TEAM_ID);
    }
    public void setTeamId(String teamId) {
        getOptions().put(TEAM_ID, teamId);
    }

    public String getEstimateMetricType() {
        return (String) getOptions().get(ESTIMATE_METRIC_TYPE);
    }
    public void setEstimateMetricType(String estimateMetricType) {
        getOptions().put(ESTIMATE_METRIC_TYPE, estimateMetricType);
    }
    public String getSprintType() {
        return (String) getOptions().get(SPRINT_TYPE);
    }
    public void setSprintType(String sprintType) {
        getOptions().put(SPRINT_TYPE, sprintType);
    }
    public String getListType() {
        return (String) getOptions().get(LIST_TYPE);
    }
    public void setListType(String listType) {
        getOptions().put(LIST_TYPE, listType);
    }
    public String getShowStatus() {
        return (String) getOptions().get(SHOW_STATUS);
    }
    public void setShowStatus(String showStatus) {
        getOptions().put(SHOW_STATUS, showStatus);
    }
}
