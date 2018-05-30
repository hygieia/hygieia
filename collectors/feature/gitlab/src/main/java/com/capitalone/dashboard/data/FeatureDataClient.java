package com.capitalone.dashboard.data;

import java.util.List;

import org.bson.types.ObjectId;

import com.capitalone.dashboard.gitlab.model.GitlabIssue;
import com.capitalone.dashboard.gitlab.model.GitlabLabel;
import com.capitalone.dashboard.model.Collector;
import com.capitalone.dashboard.model.CollectorItem;
import com.capitalone.dashboard.model.Project;
import com.capitalone.dashboard.model.UpdateResult;

public interface FeatureDataClient {

	UpdateResult updateIssues(Collector collector, Project project, List<GitlabIssue> issues, List<GitlabLabel> inProgressLabelsForProject);

    List<CollectorItem> getEnabledWidgets(ObjectId collectorId);
	
}
