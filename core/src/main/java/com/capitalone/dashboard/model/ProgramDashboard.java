package com.capitalone.dashboard.model;

import java.util.ArrayList;
import java.util.List;

/**
 * A representation of a {@link Dashboard} of {@link DashboardType} Program which is a collection of {@link Dashboard}
 */

public class ProgramDashboard extends Dashboard{
    List<TeamDashboard> teamDashboards = new ArrayList<>();

    public ProgramDashboard(String template, String title, Application application,String owner, List<TeamDashboard> teamDashboards, DashboardType type){
        super(template, title, application, owner, type);
        this.teamDashboards = teamDashboards;
    }

    public List<TeamDashboard> getTeamDashboards() { return this.teamDashboards; }

    public void setTeamDashboards(List<TeamDashboard> teamDashboards) { this.teamDashboards = teamDashboards; }

    public void addTeamDashboard(TeamDashboard teamDashboard){
        this.teamDashboards.add(teamDashboard);
    }


}
