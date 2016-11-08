package com.capitalone.dashboard.collector;

import com.capitalone.dashboard.model.GitlabTeam;

public interface GitlabClient {
	
	GitlabTeam[] getTeams();

}
