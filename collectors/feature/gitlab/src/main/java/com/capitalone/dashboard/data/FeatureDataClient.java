package com.capitalone.dashboard.data;

import java.util.List;

import org.bson.types.ObjectId;

import com.capitalone.dashboard.gitlab.model.GitlabIssue;
import com.capitalone.dashboard.gitlab.model.GitlabLabel;
import com.capitalone.dashboard.gitlab.model.GitlabProject;
import com.capitalone.dashboard.gitlab.model.GitlabTeam;
import com.capitalone.dashboard.model.CollectorItem;
import com.capitalone.dashboard.model.UpdateResult;

public interface FeatureDataClient {
	
	UpdateResult updateTeams(ObjectId collectorId, List<GitlabTeam> teams);

	UpdateResult updateProjects(ObjectId collectorId, List<GitlabProject> projects);

	UpdateResult updateIssues(ObjectId collectorId, long lastExecuted, String projectId, List<GitlabIssue> issues, List<GitlabLabel> inProgressLabelsForProject);

    List<CollectorItem> getEnabledWidgets(ObjectId collectorId);
	
}
