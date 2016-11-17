package com.capitalone.dashboard.collector;

import java.util.List;

import org.bson.types.ObjectId;

import com.capitalone.dashboard.model.GitlabIssue;
import com.capitalone.dashboard.model.GitlabLabel;
import com.capitalone.dashboard.model.GitlabProject;
import com.capitalone.dashboard.model.GitlabTeam;
import com.capitalone.dashboard.model.ScopeOwnerCollectorItem;
import com.capitalone.dashboard.model.UpdateResult;

public interface FeatureDataClient {
	
	UpdateResult updateTeams(List<GitlabTeam> teams);

	UpdateResult updateProjects(List<GitlabProject> projects);

	UpdateResult updateIssues(String projectId, List<GitlabIssue> issues, List<GitlabLabel> inProgressLabelsForProject);

	List<ScopeOwnerCollectorItem> findEnabledTeams(ObjectId collectorId);
	
}
