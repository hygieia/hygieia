package com.capitalone.dashboard.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.capitalone.dashboard.gitlab.model.GitlabIssue;
import com.capitalone.dashboard.gitlab.model.GitlabLabel;
import com.capitalone.dashboard.gitlab.model.GitlabProject;
import com.capitalone.dashboard.gitlab.model.GitlabTeam;
import com.capitalone.dashboard.model.BaseModel;
import com.capitalone.dashboard.model.CollectorItem;
import com.capitalone.dashboard.model.Feature;
import com.capitalone.dashboard.model.Scope;
import com.capitalone.dashboard.model.Team;
import com.capitalone.dashboard.model.UpdateResult;
import com.capitalone.dashboard.repository.FeatureRepository;
import com.capitalone.dashboard.repository.IssueItemRepository;
import com.capitalone.dashboard.repository.ProjectItemRepository;
import com.capitalone.dashboard.repository.TeamRepository;
import com.capitalone.dashboard.repository.WidgetRepository;

@Component
public class DefaultFeatureDataClient implements FeatureDataClient {
	private static final Logger LOGGER = LoggerFactory.getLogger(DefaultFeatureDataClient.class);
	
	private final TeamRepository teamRepo;
	private final ProjectItemRepository projectRepo;
	private final IssueItemRepository issueItemRepo;
	private final FeatureRepository featureRepo;
	private final FeatureDataMapper featureDataMapper;
	private final WidgetRepository widgetRepo;
	
	@Autowired
	public DefaultFeatureDataClient(TeamRepository teamRepo, ProjectItemRepository scopeRepo, IssueItemRepository issueRepo,
	        FeatureDataMapper featureDataMapper, FeatureRepository featureRepo, WidgetRepository widgetRepo) {
		this.teamRepo = teamRepo;
		this.projectRepo = scopeRepo;
		this.issueItemRepo = issueRepo;
		this.featureDataMapper = featureDataMapper;
		this.featureRepo = featureRepo;
		this.widgetRepo = widgetRepo;
	}

	@SuppressWarnings("unchecked")
	@Override
	public UpdateResult updateTeams(ObjectId collectorId, List<GitlabTeam> gitlabTeams) {
		List<Team> currentTeams = new ArrayList<>();
		for(GitlabTeam team : gitlabTeams) {
			String teamId = String.valueOf(team.getId());
			Team existingTeam = teamRepo.findByTeamId(teamId);
			ObjectId existingId = null;
			if(existingTeam != null) {
			    existingId = existingTeam.getId();
			}
			Team scopeOwnerCollectorItem = featureDataMapper.mapToTeam(team, existingId, collectorId);
			currentTeams.add(scopeOwnerCollectorItem);
		}
		
//		List<Team> savedTeams = teamRepo.findByCollectorIdIn(Lists.newArrayList(gitlabFeatureCollectorId));
//		
//		Collection<Team> teamsToAdd = CollectionUtils.subtract(currentTeams, savedTeams);
//		teamRepo.save(teamsToAdd);
//		
//		Collection<Team> teamsToDelete = CollectionUtils.subtract(savedTeams, currentTeams);
//		teamRepo.delete(teamsToDelete);
		teamRepo.save(currentTeams);
        
        return new UpdateResult(currentTeams.size(), 0);
	}

	@SuppressWarnings("unchecked")
	@Override
	public UpdateResult updateProjects(ObjectId collectorId, List<GitlabProject> projects) {
		List<Scope> currentProjects = new ArrayList<>();
		for(GitlabProject project : projects) {
			String projectId = String.valueOf(project.getId());
			ObjectId existingId = getExistingId(projectRepo.getScopeIdById(projectId));
			Scope scope = featureDataMapper.mapToScopeItem(project, existingId, collectorId);
			currentProjects.add(scope);
		}
		List<Scope> savedProjects = projectRepo.findScopeByCollectorId(collectorId);
		
		Collection<Scope> projectsToAdd = CollectionUtils.subtract(currentProjects, savedProjects);
		projectRepo.save(projectsToAdd);
		
		Collection<Scope> projectsToDelete = CollectionUtils.subtract(savedProjects, currentProjects);
		projectRepo.delete(projectsToDelete);
		
		return new UpdateResult(projectsToAdd.size(), projectsToDelete.size());
	}

	@Override
	public UpdateResult updateIssues(ObjectId collectorId, String projectId, List<GitlabIssue> issues, List<GitlabLabel> inProgressLabelsForProject) {
		List<String> inProgressLabels = new ArrayList<>();
		for(GitlabLabel label : inProgressLabelsForProject) {
			inProgressLabels.add(label.getName());
		}
		
		List<Feature> savedFeatures = issueItemRepo.getFeaturesByCollectorAndProjectId(collectorId, projectId);
		
		return updateAll(issues, collectorId, inProgressLabels, savedFeatures);
	}

	@SuppressWarnings("unchecked")
	private UpdateResult updateAll(List<GitlabIssue> gitlabIssues, ObjectId collectorId,
			List<String> inProgressLabels, List<Feature> savedFeatures) {
		
		List<Feature> updatedFeatures = new ArrayList<>();
		for(GitlabIssue issue : gitlabIssues) {
			String issueId = String.valueOf(issue.getId());
			ObjectId existingId = getExistingId(featureRepo.getFeatureIdById(issueId));
			Feature feature = featureDataMapper.mapToFeatureItem(issue, inProgressLabels, existingId, collectorId);
			updatedFeatures.add(feature);
		}
		
		Collection<Feature> deletedFeatures = CollectionUtils.subtract(savedFeatures, updatedFeatures);
		
		issueItemRepo.save(updatedFeatures);
		issueItemRepo.delete(deletedFeatures);
		UpdateResult updateResult = new UpdateResult(updatedFeatures.size(), deletedFeatures.size());
				
		return updateResult;
	}

//	@Override
//	public List<ScopeOwnerCollectorItem> findEnabledTeams(ObjectId collectorId) {
//		return teamRepo.findEnabledTeams(collectorId);
//	}
	
    @Override
    public List<CollectorItem> getEnabledWidgets(ObjectId collectorId) {
        return widgetRepo.findByCollectorIdAndEnabled(collectorId, true);
    }
	
	private ObjectId getExistingId(List<? extends BaseModel> list) {
		if(list.size() > 1) {
			LOGGER.warn("More than one collector item found for the given Id");
		}
		
		if(!list.isEmpty()) {
			return list.get(0).getId();
		}
		
		return null;
	}

}
