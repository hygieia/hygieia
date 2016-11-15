package com.capitalone.dashboard.collector;

import java.util.List;

import com.capitalone.dashboard.model.GitlabIssue;
import com.capitalone.dashboard.model.GitlabProject;
import com.capitalone.dashboard.model.GitlabTeam;
import com.capitalone.dashboard.model.ScopeOwnerCollectorItem;

public interface GitlabClient {
	
	List<GitlabTeam> getTeams();

	List<GitlabProject> getProjects(ScopeOwnerCollectorItem team);

	List<GitlabIssue> getIssuesInProgress(GitlabProject project);

}
