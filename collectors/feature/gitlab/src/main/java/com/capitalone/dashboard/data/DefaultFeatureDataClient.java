package com.capitalone.dashboard.data;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
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
			ScopeOwnerCollectorItem scopeOwnerCollectorItem = featureDataMapper.mapToScopeOwnerCollectorItem(team, findExistingTeamId(teamId), gitlabFeatureCollectorId);
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
			Scope scope = featureDataMapper.mapToScopeItem(project, findExistingProjectId(projectId), gitlabFeatureCollectorId);
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
		
		UpdateResult updateResult;
		if(shouldUpdateAll(gitlabCollector)) {
			updateResult = updateAll(issues, gitlabCollector, inProgressLabels, savedFeatures);
		}
		else {
			updateResult = updateChanged(issues, gitlabCollector, inProgressLabels, savedFeatures);
		}
		
		return updateResult;
	}

	private boolean shouldUpdateAll(FeatureCollector gitlabCollector) {
		return true;
	}

	@SuppressWarnings("unchecked")
	private UpdateResult updateAll(List<GitlabIssue> gitlabIssues, FeatureCollector gitlabCollector,
			List<String> inProgressLabels, List<Feature> savedFeatures) {
		
		List<Feature> updatedFeatures = new ArrayList<>();
		for(GitlabIssue issue : gitlabIssues) {
			String issueId = String.valueOf(issue.getId());
			Feature feature = featureDataMapper.mapToFeatureItem(issue, inProgressLabels, findExistingIssueId(issueId), gitlabCollector.getId());
			updatedFeatures.add(feature);
		}
		
		Collection<Feature> deletedFeatures = CollectionUtils.subtract(savedFeatures, updatedFeatures);
		
		featureRepo.save(updatedFeatures);
		featureRepo.delete(deletedFeatures);
		UpdateResult updateResult = new UpdateResult(updatedFeatures.size(), deletedFeatures.size());
				
		return updateResult;
	}

	@SuppressWarnings("unchecked")
	private UpdateResult updateChanged(List<GitlabIssue> issues, FeatureCollector gitlabCollector,
			List<String> inProgressLabels, List<Feature> savedFeatures) {
		Collection<GitlabIssue> newIssues = findNewIssues(issues, savedFeatures);
		Collection<GitlabIssue> currentIssues = CollectionUtils.subtract(issues, newIssues);
		Collection<GitlabIssue> issuesToUpdate = filterByUpdatedDate(gitlabCollector.getLastExecuted(), currentIssues);
		issuesToUpdate.addAll(newIssues);
		
		Collection<Feature> updatedFeatures = new ArrayList<>();
		for(GitlabIssue issue : issuesToUpdate) {
			String issueId = String.valueOf(issue.getId());
			Feature feature = featureDataMapper.mapToFeatureItem(issue, inProgressLabels, findExistingIssueId(issueId), gitlabCollector.getId());
			updatedFeatures.add(feature);
		}
		Collection<Feature> deletedFeatures = findDeletedFeatures(savedFeatures, issues);
		
		featureRepo.save(updatedFeatures);
		featureRepo.delete(deletedFeatures);

		UpdateResult updateResult = new UpdateResult(updatedFeatures.size(), deletedFeatures.size());
		
		return updateResult;
	}

	private List<Feature> findDeletedFeatures(List<Feature> savedFeatures, List<GitlabIssue> issues) {
		List<Feature> deletedFeatures = new ArrayList<>();
		for(Feature feature : savedFeatures) {
			if(isNotPresent(feature.getsId(), issues)) {
				deletedFeatures.add(feature);
			}
		}
		return deletedFeatures;
	}

	private boolean isNotPresent(String featureId, List<GitlabIssue> issues) {
		for(GitlabIssue issue : issues) {
			String issueId = String.valueOf(issue.getId());
			if(featureId.equals(issueId)) {
				return false;
			}
		}
		return true;
	}

	private List<GitlabIssue> findNewIssues(List<GitlabIssue> issues, List<Feature> savedIssues) {
		List<GitlabIssue> newIssues = new ArrayList<>();
		for(GitlabIssue issue : issues) {
			if(issueIsNew(savedIssues, String.valueOf(issue.getId()))) {
				newIssues.add(issue);
			}
			
		}
		return newIssues;
	}

	private boolean issueIsNew(List<Feature> savedIssues, String issueId) {
		for(Feature feature : savedIssues) {
			if(issueId.equals(feature.getsId())) {
				return false;
			}
		}
		return true;
	}

	private List<GitlabIssue> filterByUpdatedDate(long lastExecuted, Collection<GitlabIssue> issues) {
		Date lastCollectionDate = new Date(lastExecuted);
		OffsetDateTime lastCollection = OffsetDateTime.ofInstant(lastCollectionDate.toInstant(), ZoneId.systemDefault());
		List<GitlabIssue> filteredIssues = new ArrayList<>();
		for(GitlabIssue issue : issues) {
			OffsetDateTime lastUpdated = OffsetDateTime.parse(issue.getUpdated_at());
			if(lastCollection.isBefore(lastUpdated)) {
				filteredIssues.add(issue);
			}
		}
		return filteredIssues;
	}

	@Override
	public List<ScopeOwnerCollectorItem> findEnabledTeams(ObjectId collectorId) {
		return teamRepo.findEnabledTeams(collectorId);
	}

	private ObjectId findExistingTeamId(String teamId) {
		List<ScopeOwnerCollectorItem> savedTeams = teamRepo.getTeamIdById(teamId);

		// Not sure of the state of the data
		if (savedTeams.size() > 1) {
			LOGGER.warn("More than one collector item found for teamId " + teamId);
		}

		if (!savedTeams.isEmpty()) {
			return savedTeams.get(0).getId();
		}

		return null;
	}
	
	private ObjectId findExistingProjectId(String projectId) {
		List<Scope> existingProjects = projectRepo.getScopeById(projectId);
		
		if(existingProjects.size() > 1) {
			LOGGER.warn("More than one collector item found for projectId " + projectId);
		}
		
		if(!existingProjects.isEmpty()) {
			return existingProjects.get(0).getId();
		}
		
		return null;
	}
	
	private ObjectId findExistingIssueId(String id) {
		List<Feature> existing = featureRepo.getFeatureIdById(id);
		
		if(existing.size() > 1) {
			LOGGER.warn("More than one collector item found for featureId " + id);
		}
		
		if(!existing.isEmpty()) {
			return existing.get(0).getId();
		}
		
		return null;
	}

}
