package com.capitalone.dashboard.model;

import java.util.List;

/**
 * Class representing any team members of a {@link TeamInventory}
 */
public class TeamMember {

    private String orgId;
    private String allocation;
    private String teamId;
    private String name;
    private List<TeamLevelDetails> teams;


    private String regOrTemp ;

    public String getOrgId() {
        return orgId;
    }

    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }

    public String getAllocation() {
        return allocation;
    }

    public void setAllocation(String allocation) {
        this.allocation = allocation;
    }

    public String getTeamId() {
        return teamId;
    }

    public void setTeamId(String teamId) {
        this.teamId = teamId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getRegOrTemp() {
        return regOrTemp;
    }

    public void setRegOrTemp(String regOrTemp) {
        this.regOrTemp = regOrTemp;
    }


    public List<TeamLevelDetails> getTeams() {
        return teams;
    }

    public void setTeams(List<TeamLevelDetails> teams) {
        this.teams = teams;
    }


}
