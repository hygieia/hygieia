package com.capitalone.dashboard.collector;

import com.capitalone.dashboard.model.Epic;
import com.capitalone.dashboard.model.Feature;
import com.capitalone.dashboard.model.Scope;
import com.capitalone.dashboard.model.Team;

import java.util.List;
import java.util.Set;

public interface JiraClient {

    List<Feature> getIssues(Team board);

    List<Feature> getIssues(Scope project);

	Set<Scope> getProjects();

	List<Team> getBoards();

	List<Team> getTeams();
	
	Epic getEpic(String epicId);

}
