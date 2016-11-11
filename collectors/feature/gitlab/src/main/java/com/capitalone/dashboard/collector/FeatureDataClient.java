package com.capitalone.dashboard.collector;

import java.util.List;

import com.capitalone.dashboard.model.GitlabProject;
import com.capitalone.dashboard.model.GitlabTeam;

public interface FeatureDataClient {
	
	void updateTeams(List<GitlabTeam> teams);

	void updateProjects(List<GitlabProject> projects);
	
}
