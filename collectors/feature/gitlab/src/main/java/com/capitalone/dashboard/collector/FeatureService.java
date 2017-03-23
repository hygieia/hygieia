package com.capitalone.dashboard.collector;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;

import com.capitalone.dashboard.data.FeatureDataClient;
import com.capitalone.dashboard.gitlab.GitlabClient;
import com.capitalone.dashboard.gitlab.model.GitlabIssue;
import com.capitalone.dashboard.gitlab.model.GitlabLabel;
import com.capitalone.dashboard.gitlab.model.GitlabProject;
import com.capitalone.dashboard.gitlab.model.GitlabTeam;
import com.capitalone.dashboard.model.CollectorItem;
import com.capitalone.dashboard.model.UpdateResult;
import com.google.common.collect.Sets;

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

	@Async
	public ListenableFuture<UpdateResult> updateSelectableTeams(ObjectId collectorId) {
		List<GitlabTeam> teams = gitlabClient.getTeams();
		UpdateResult updateResult = featureDataClient.updateTeams(collectorId, teams);

		return new AsyncResult<>(updateResult);
	}

	@Async
	public ListenableFuture<UpdateResult> updateProjects(ObjectId collectorId) {
	    List<GitlabProject> projects = gitlabClient.getProjects();
		UpdateResult result = featureDataClient.updateProjects(collectorId, projects);

		return new AsyncResult<>(result);
	}

	@Async
	public ListenableFuture<UpdateResult> updateIssuesForProject(ObjectId collectorId, long lastExecuted, GitlabProject project) {
		String projectId = String.valueOf(project.getId());
		List<GitlabLabel> inProgressLabelsForProject = gitlabClient.getInProgressLabelsForProject(project.getId());
		List<GitlabIssue> issues = gitlabClient.getIssuesForProject(project);
		UpdateResult result = featureDataClient.updateIssues(collectorId, lastExecuted, projectId, issues, inProgressLabelsForProject);

		LOGGER.debug("{}: Added/Updated {} issues and deleted {} issues", project.getName(), result.getItemsAdded(),
				result.getItemsDeleted());
		return new AsyncResult<UpdateResult>(result);
	}
	
	@SuppressWarnings("unchecked")
    public Collection<GitlabProject> getEnabledProjects(ObjectId collectorId) {
	    List<CollectorItem> enabledWidgets = featureDataClient.getEnabledWidgets(collectorId);
        //Get teams
        Collection<CollectorItem> widgetsWithTeamsSelected = filterWidgetsWithAnyTeamId(enabledWidgets);
        Collection<CollectorItem> widgetsToFindByProject = CollectionUtils.subtract(enabledWidgets, widgetsWithTeamsSelected);
        //if team is any, get issues for project, otherwise get all issues for team
        Set<GitlabProject> projects = Sets.newHashSet();
        
        //first create a list of all the projects needed to update
        for(CollectorItem widget : widgetsWithTeamsSelected) {
            projects.addAll(gitlabClient.getProjectsForTeam((String)widget.getOptions().get("teamId")));
        }
        for(CollectorItem widget : widgetsToFindByProject) {
            projects.add(gitlabClient.getProjectById((String)widget.getOptions().get("projectId")));
        }
        
        return projects;
	}

    private Collection<CollectorItem> filterWidgetsWithAnyTeamId(List<CollectorItem> enabledWidgets) {
        Set<CollectorItem> filteredTeams = Sets.newHashSet();
        for(CollectorItem widget : enabledWidgets) {
            if(!"Any".equals(widget.getOptions().get("teamId"))) {
                filteredTeams.add(widget);
            }
        }
        return filteredTeams;
    }

}
