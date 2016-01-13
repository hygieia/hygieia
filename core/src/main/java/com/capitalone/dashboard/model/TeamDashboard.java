package com.capitalone.dashboard.model;

/**
 * Represents a {@link Dashboard} when used in the context of a "Team" Dashboard
 */
public class TeamDashboard {
    String teamName;
    Dashboard dashboard;

    public TeamDashboard(String teamName, Dashboard dashboard) {
        this.teamName = teamName;
        this.dashboard = dashboard;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public Dashboard getDashboard() {
        return dashboard;
    }

    public void setDashboard(Dashboard dashboard) {
        this.dashboard = dashboard;
    }
}
