package com.capitalone.dashboard.collector;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.capitalone.dashboard.data.FeatureDataClient;
import com.capitalone.dashboard.gitlab.GitlabClient;
import com.capitalone.dashboard.gitlab.model.GitlabIssue;
import com.capitalone.dashboard.gitlab.model.GitlabLabel;
import com.capitalone.dashboard.gitlab.model.GitlabProject;
import com.capitalone.dashboard.gitlab.model.GitlabTeam;
import com.capitalone.dashboard.model.ScopeOwnerCollectorItem;
import com.capitalone.dashboard.model.UpdateResult;

@Service
public class FeatureService {
	private static final Logger LOGGER = LoggerFactory.getLogger(FeatureService.class);
	
	private final GitlabClient gitlabClient;
	private final FeatureDataClient featureDataClient;
	
	@Autowired
	public FeatureService(GitlabClient gitlabClient, FeatureDataClient featureDataClient) {
		this.gitlabClient = gitlabClient;
		this.featureDataClient = featureDataClient;
	}
	
	public List<GitlabProject> getProjectsForEnabledTeams(ObjectId collectorId) {
		List<ScopeOwnerCollectorItem> enabledTeams = featureDataClient.findEnabledTeams(collectorId);
        List<GitlabProject> projects = new ArrayList<>();
        for(ScopeOwnerCollectorItem enabledTeam : enabledTeams) {
        	projects.addAll(gitlabClient.getProjects(enabledTeam));
        }
        
		return projects;
	}
	
	@Async
	public Future<Void> updateSelectableTeams() {
		List<GitlabTeam> teams = gitlabClient.getTeams();
        UpdateResult result = featureDataClient.updateTeams(teams);
        
        LOGGER.info("Added {} new team(s) and deleted {} team(s).", result.getItemsAdded(), result.getItemsDeleted());
        return null;
	}
	
	@Async
	public Future<Void> updateProjects(List<GitlabProject> projects) {
		UpdateResult result = featureDataClient.updateProjects(projects);
		
		LOGGER.info("Added {} new project(s) and deleted {} projects(s).", result.getItemsAdded(), result.getItemsDeleted());
		return null;
	}
	
    @Async
	public Future<Void> updateIssuesForProject(GitlabProject project) {
    	String projectId = String.valueOf(project.getId());
		List<GitlabLabel> inProgressLabelsForProject = gitlabClient.getInProgressLabelsForProject(project.getId());
		List<GitlabIssue> issues = gitlabClient.getIssuesForProject(project);
		UpdateResult result = featureDataClient.updateIssues(projectId, issues, inProgressLabelsForProject);
		
		LOGGER.info("{}: Added/Updated {} issues and deleted {} issues", project.getName(), result.getItemsAdded(), result.getItemsDeleted());
		return null;
	}
    
}
