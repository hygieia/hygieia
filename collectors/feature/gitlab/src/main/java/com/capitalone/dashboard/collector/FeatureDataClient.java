package com.capitalone.dashboard.collector;

import com.capitalone.dashboard.model.GitlabTeam;

public interface FeatureDataClient {
	
	void updateTeams(GitlabTeam[] teams);
	
	

}
