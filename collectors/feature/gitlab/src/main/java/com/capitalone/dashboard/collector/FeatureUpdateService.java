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

import com.capitalone.dashboard.model.GitlabIssue;
import com.capitalone.dashboard.model.GitlabLabel;
import com.capitalone.dashboard.model.GitlabProject;
import com.capitalone.dashboard.model.GitlabTeam;
import com.capitalone.dashboard.model.ScopeOwnerCollectorItem;

@Service
public class FeatureUpdateService {
	private static final Logger LOGGER = LoggerFactory.getLogger(FeatureUpdateService.class);
	
	private final GitlabClient gitlabClient;
	private final FeatureDataClient featureDataClient;
	
	@Autowired
	public FeatureUpdateService(GitlabClient gitlabClient, FeatureDataClient featureDataClient) {
		this.gitlabClient = gitlabClient;
		this.featureDataClient = featureDataClient;
	}
	
	public void updateSelectableTeams() {
		List<GitlabTeam> teams = gitlabClient.getTeams();
        featureDataClient.updateTeams(teams);
	}
	
	public List<GitlabProject> updateProjectsForEnabledTeams(ObjectId collectorId) {
		List<ScopeOwnerCollectorItem> enabledTeams = featureDataClient.findEnabledTeams(collectorId);
        List<GitlabProject> projects = new ArrayList<>();
        for(ScopeOwnerCollectorItem enabledTeam : enabledTeams) {
        	projects.addAll(gitlabClient.getProjects(enabledTeam));
        }
        featureDataClient.updateProjects(projects);
		return projects;
	}

    @Async
	public Future<String> updateIssuesForProject(GitlabProject project) {
    	LOGGER.info("Updating issues for {}", project.getName());
		List<GitlabLabel> inProgressLabelsForProject = gitlabClient.getInProgressLabelsForProject(project.getId());
		List<GitlabIssue> issues = gitlabClient.getIssuesForProject(project);
		featureDataClient.updateIssues(issues, inProgressLabelsForProject);
		
		return null;
	}
    
}
