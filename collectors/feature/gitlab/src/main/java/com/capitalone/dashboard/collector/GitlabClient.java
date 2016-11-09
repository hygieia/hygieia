package com.capitalone.dashboard.collector;

import java.util.List;

import com.capitalone.dashboard.model.GitlabTeam;

public interface GitlabClient {
	
	List<GitlabTeam> getTeams();

}
