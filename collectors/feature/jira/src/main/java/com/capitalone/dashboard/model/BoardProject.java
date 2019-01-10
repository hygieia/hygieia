package com.capitalone.dashboard.model;

import java.util.HashSet;
import java.util.Set;

public class BoardProject {
    private Team team;
    private Set<Scope> projects = new HashSet<>();


    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    public Set<Scope> getProjects() {
        return projects;
    }

    public void setProjects(Set<Scope> projects) {
        this.projects = projects;
    }
}
