package com.capitalone.dashboard.collector;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Future;

import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

import com.capitalone.dashboard.data.FeatureDataClient;
import com.capitalone.dashboard.gitlab.GitlabClient;
import com.capitalone.dashboard.gitlab.model.GitlabIssue;
import com.capitalone.dashboard.gitlab.model.GitlabLabel;
import com.capitalone.dashboard.gitlab.model.GitlabProject;
import com.capitalone.dashboard.model.Collector;
import com.capitalone.dashboard.model.CollectorItem;
import com.capitalone.dashboard.model.Project;
import com.capitalone.dashboard.model.UpdateResult;
import com.google.common.collect.Sets;

@Service
public class FeatureService {

	private final GitlabClient gitlabClient;
	private final FeatureDataClient featureDataClient;

	@Autowired
	public FeatureService(GitlabClient gitlabClient, FeatureDataClient featureDataClient) {
		this.gitlabClient = gitlabClient;
		this.featureDataClient = featureDataClient;
	}
    
    @Async
    public Future<UpdateResult> updateIssuesForProject(Collector collector, Project project) {
        List<GitlabLabel> inProgressLabelsForProject = gitlabClient.getInProgressLabelsForProject(project);
        List<GitlabIssue> issues = gitlabClient.getIssuesForProject(project);
        UpdateResult result = featureDataClient.updateIssues(collector, project, issues, inProgressLabelsForProject);
        
        return new AsyncResult<UpdateResult>(result);
    }
    
    public Set<Project> getProjectsToUpdate(ObjectId collectorId) {
        List<CollectorItem> widgets = featureDataClient.getEnabledWidgets(collectorId);
        Set<Project> projects = new HashSet<>();
        for (CollectorItem widget : widgets) {
            projects.addAll(getProjectsForWidget(widget));
        }
        
        return projects;
    }

    private Collection<Project> getProjectsForWidget(CollectorItem widget) {
        String teamId = (String) widget.getOptions().get("teamId");
        String projectId = (String) widget.getOptions().get("projectId");
        
        if(StringUtils.isNotBlank(projectId)) {
            return Sets.newHashSet(new Project(teamId, projectId));
        }
        
        Collection<Project> projects = Sets.newHashSet();
        List<GitlabProject> gitlabProjects = gitlabClient.getProjectsForTeam(teamId);
        for(GitlabProject gitlabProject : gitlabProjects) {
            projects.add(new Project(gitlabProject.getNamespace().getName(), gitlabProject.getName()));
        }
        
        return projects;
    }

}
