package com.capitalone.dashboard.model;

public class TeamLevelDetails extends CollectorItem {


    public String getInstanceUrl() {
        return (String) getOptions().get("instanceUrl");
    }

    public void setInstanceUrl(String url) {
        getOptions().put("instanceUrl", url);
    }

    public String getTeamId() {
        return (String) getOptions().get("teamId");
    }

    public void setTeamId(String teamId) {
        getOptions().put("teamId", teamId);
    }

    public String getTeamName() {
        return (String) getOptions().get("teamName");
    }

    public void setTeamName(String teamName) {
        getOptions().put("teamName", teamName);
    }

    public String getUrl() {
        return (String) getOptions().get("url");
    }

    public void setUrl(String url) {
        getOptions().put("url", url);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TeamLevelDetails that = (TeamLevelDetails) o;
        return getTeamId().equals(that.getTeamId()) && getTeamName().equals(that.getTeamName());
    }

    @Override
    public int hashCode() {
        int result = getTeamId().hashCode();
        result = 31 * result + getTeamName().hashCode();
        return result;
    }


}
