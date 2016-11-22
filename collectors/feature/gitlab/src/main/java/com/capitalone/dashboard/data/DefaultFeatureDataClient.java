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
import com.capitalone.dashboard.model.Feature;
import com.capitalone.dashboard.model.FeatureCollector;
import com.capitalone.dashboard.model.Scope;
import com.capitalone.dashboard.model.ScopeOwnerCollectorItem;
import com.capitalone.dashboard.model.UpdateResult;
import com.capitalone.dashboard.repository.FeatureCollectorRepository;
import com.capitalone.dashboard.repository.IssueItemRepository;
import com.capitalone.dashboard.repository.ProjectItemRepository;
import com.capitalone.dashboard.repository.TeamItemRepository;
import com.capitalone.dashboard.util.FeatureCollectorConstants;
import com.google.common.collect.Lists;

@Component
public class DefaultFeatureDataClient implements FeatureDataClient {
	private static final Logger LOGGER = LoggerFactory.getLogger(DefaultFeatureDataClient.class);
	
	private final FeatureCollectorRepository featureCollectorRepo;
	private final TeamItemRepository teamRepo;
	private final ProjectItemRepository projectRepo;
	private final IssueItemRepository featureRepo;
	private final FeatureDataMapper featureDataMapper;
	
	@Autowired
	public DefaultFeatureDataClient(FeatureCollectorRepository featureCollectorRepo, TeamItemRepository teamRepo, 
			ProjectItemRepository scopeRepo, IssueItemRepository featureRepo, FeatureDataMapper featureDataMapper) {
		this.featureCollectorRepo = featureCollectorRepo;
		this.teamRepo = teamRepo;
		this.projectRepo = scopeRepo;
		this.featureRepo = featureRepo;
		this.featureDataMapper = featureDataMapper;
	}

	@SuppressWarnings("unchecked")
	@Override
	public UpdateResult updateTeams(List<GitlabTeam> gitlabTeams) {
		ObjectId gitlabFeatureCollectorId = featureCollectorRepo.findByName(FeatureCollectorConstants.GITLAB).getId();
		
		List<ScopeOwnerCollectorItem> currentTeams = new ArrayList<>();
		for(GitlabTeam team : gitlabTeams) {
			String teamId = String.valueOf(team.getId());
			ObjectId existingId = getExistingId(teamRepo.getTeamIdById(teamId));
			ScopeOwnerCollectorItem scopeOwnerCollectorItem = featureDataMapper.mapToScopeOwnerCollectorItem(team, existingId, gitlabFeatureCollectorId);
			currentTeams.add(scopeOwnerCollectorItem);
		}
		
		List<ScopeOwnerCollectorItem> savedTeams = teamRepo.findByCollectorIdIn(Lists.newArrayList(gitlabFeatureCollectorId));
		
		Collection<ScopeOwnerCollectorItem> teamsToAdd = CollectionUtils.subtract(currentTeams, savedTeams);
		teamRepo.save(teamsToAdd);
		
		Collection<ScopeOwnerCollectorItem> teamsToDelete = CollectionUtils.subtract(savedTeams, currentTeams);
		teamRepo.delete(teamsToDelete);
        
        return new UpdateResult(teamsToAdd.size(), teamsToDelete.size());
	}

	@SuppressWarnings("unchecked")
	@Override
	public UpdateResult updateProjects(List<GitlabProject> projects) {
		ObjectId gitlabFeatureCollectorId = featureCollectorRepo.findByName(FeatureCollectorConstants.GITLAB).getId();
		
		List<Scope> currentProjects = new ArrayList<>();
		for(GitlabProject project : projects) {
			String projectId = String.valueOf(project.getId());
			ObjectId existingId = getExistingId(projectRepo.getScopeIdById(projectId));
			Scope scope = featureDataMapper.mapToScopeItem(project, existingId, gitlabFeatureCollectorId);
			currentProjects.add(scope);
		}
		List<Scope> savedProjects = projectRepo.findScopeByCollectorId(gitlabFeatureCollectorId);
		
		Collection<Scope> projectsToAdd = CollectionUtils.subtract(currentProjects, savedProjects);
		projectRepo.save(projectsToAdd);
		
		Collection<Scope> projectsToDelete = CollectionUtils.subtract(savedProjects, currentProjects);
		projectRepo.delete(projectsToDelete);
		
		return new UpdateResult(projectsToAdd.size(), projectsToDelete.size());
	}

	@Override
	public UpdateResult updateIssues(String projectId, List<GitlabIssue> issues, List<GitlabLabel> inProgressLabelsForProject) {
		FeatureCollector gitlabCollector = featureCollectorRepo.findByName(FeatureCollectorConstants.GITLAB);
		List<String> inProgressLabels = new ArrayList<>();
		for(GitlabLabel label : inProgressLabelsForProject) {
			inProgressLabels.add(label.getName());
		}
		
		List<Feature> savedFeatures = featureRepo.getFeaturesByCollectorAndProjectId(gitlabCollector.getId(), projectId);
		
		return updateAll(issues, gitlabCollector, inProgressLabels, savedFeatures);
	}

	@SuppressWarnings("unchecked")
	private UpdateResult updateAll(List<GitlabIssue> gitlabIssues, FeatureCollector gitlabCollector,
			List<String> inProgressLabels, List<Feature> savedFeatures) {
		
		List<Feature> updatedFeatures = new ArrayList<>();
		for(GitlabIssue issue : gitlabIssues) {
			String issueId = String.valueOf(issue.getId());
			ObjectId existingId = getExistingId(featureRepo.getFeatureIdById(issueId));
			Feature feature = featureDataMapper.mapToFeatureItem(issue, inProgressLabels, existingId, gitlabCollector.getId());
			updatedFeatures.add(feature);
		}
		
		Collection<Feature> deletedFeatures = CollectionUtils.subtract(savedFeatures, updatedFeatures);
		
		featureRepo.save(updatedFeatures);
		featureRepo.delete(deletedFeatures);
		UpdateResult updateResult = new UpdateResult(updatedFeatures.size(), deletedFeatures.size());
				
		return updateResult;
	}

	@Override
	public List<ScopeOwnerCollectorItem> findEnabledTeams(ObjectId collectorId) {
		return teamRepo.findEnabledTeams(collectorId);
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
