package com.capitalone.dashboard.data;

import java.util.List;

import com.capitalone.dashboard.gitlab.model.GitlabIssue;
import com.capitalone.dashboard.gitlab.model.GitlabLabel;
import com.capitalone.dashboard.gitlab.model.GitlabProject;
import com.capitalone.dashboard.gitlab.model.GitlabTeam;
import com.capitalone.dashboard.model.UpdateResult;

public interface FeatureDataClient {
	
	UpdateResult updateTeams(List<GitlabTeam> teams);

	UpdateResult updateProjects(List<GitlabProject> projects);

	UpdateResult updateIssues(String projectId, List<GitlabIssue> issues, List<GitlabLabel> inProgressLabelsForProject);

//	List<ScopeOwnerCollectorItem> findEnabledTeams(ObjectId collectorId);
	
}
