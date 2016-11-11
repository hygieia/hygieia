package com.capitalone.dashboard.collector;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.capitalone.dashboard.model.GitlabProject;
import com.capitalone.dashboard.model.GitlabTeam;
import com.capitalone.dashboard.model.Scope;
import com.capitalone.dashboard.model.ScopeOwnerCollectorItem;
import com.capitalone.dashboard.repository.FeatureCollectorRepository;
import com.capitalone.dashboard.repository.ProjectItemRepository;
import com.capitalone.dashboard.repository.ScopeOwnerRepository;
import com.capitalone.dashboard.util.FeatureCollectorConstants;
import com.google.common.collect.Lists;

@Component
public class DefaultFeatureDataClient implements FeatureDataClient {
	private static final Logger LOGGER = LoggerFactory.getLogger(DefaultFeatureDataClient.class);
	
	private final FeatureCollectorRepository featureRepo;
	private final ScopeOwnerRepository teamRepo;
	private final ProjectItemRepository projectRepo;
	
	@Autowired
	public DefaultFeatureDataClient(FeatureCollectorRepository featureRepo, ScopeOwnerRepository teamRepo, ProjectItemRepository scopeRepo) {
		this.featureRepo = featureRepo;
		this.teamRepo = teamRepo;
		this.projectRepo = scopeRepo;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void updateTeams(List<GitlabTeam> gitlabTeams) {
		ObjectId gitlabFeatureCollectorId = featureRepo.findByName(FeatureCollectorConstants.GITLAB).getId();
		
		List<ScopeOwnerCollectorItem> currentTeams = convertToCollectorItem(gitlabTeams, gitlabFeatureCollectorId);
		List<ScopeOwnerCollectorItem> savedTeams = teamRepo.findByCollectorIdIn(Lists.newArrayList(gitlabFeatureCollectorId));
		
		Collection<ScopeOwnerCollectorItem> teamsToAdd = CollectionUtils.subtract(currentTeams, savedTeams);
		teamRepo.save(teamsToAdd);
		LOGGER.info("Added {} new teams.", teamsToAdd.size());
		
		Collection<ScopeOwnerCollectorItem> teamsToDelete = CollectionUtils.subtract(savedTeams, currentTeams);
		teamRepo.delete(teamsToDelete);
        LOGGER.info("Deleted {} teams.", teamsToDelete.size());
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void updateProjects(List<GitlabProject> projects) {
		ObjectId gitlabFeatureCollectorId = featureRepo.findByName(FeatureCollectorConstants.GITLAB).getId();
		
		List<Scope> currentProjects = convertToScopeItems(projects, gitlabFeatureCollectorId);
		List<Scope> savedProjects = projectRepo.findScopeByCollectorId(gitlabFeatureCollectorId);
		
		Collection<Scope> projectsToAdd = CollectionUtils.subtract(currentProjects, savedProjects);
		projectRepo.save(projectsToAdd);
		LOGGER.info("Added {} new projects.", projectsToAdd.size());
		
		Collection<Scope> projectsToDelete = CollectionUtils.subtract(savedProjects, currentProjects);
		projectRepo.delete(projectsToDelete);
		LOGGER.info("Deleted {} projects.", projectsToDelete.size());
	}

	private List<Scope> convertToScopeItems(List<GitlabProject> gitlabProjects, ObjectId gitlabFeatureCollectorId) {
		List<Scope> currentProjects = new ArrayList<>();
		
		for(GitlabProject gitlabProject : gitlabProjects) {
			String projectId = String.valueOf(gitlabProject.getId());
			
			Scope project = findExistingProject(projectId);
			
			project.setCollectorId(gitlabFeatureCollectorId);
			project.setpId(projectId);
			project.setName(gitlabProject.getName());
			project.setBeginDate("");
			project.setEndDate("");
			project.setChangeDate("");
			project.setAssetState("Active");
			project.setIsDeleted("False");
			project.setProjectPath(gitlabProject.getPath());
			
			currentProjects.add(project);
		}
		
		return currentProjects;
	}

	private List<ScopeOwnerCollectorItem> convertToCollectorItem(List<GitlabTeam> gitlabTeams, ObjectId gitlabFeatureCollectorId) {
		List<ScopeOwnerCollectorItem> currentTeams = new ArrayList<>();
		for(GitlabTeam gitlabTeam : gitlabTeams) {
			String teamId = String.valueOf(gitlabTeam.getId());
			
			ScopeOwnerCollectorItem team = findExistingTeam(teamId);
			
			team.setCollectorId(gitlabFeatureCollectorId);
			team.setTeamId(teamId);
			team.setName(gitlabTeam.getName());
			team.setChangeDate("");
			team.setAssetState("Active");
			team.setIsDeleted("False");
			
			currentTeams.add(team);
		}
		return currentTeams;
	}

	private ScopeOwnerCollectorItem findExistingTeam(String teamId) {
		List<ScopeOwnerCollectorItem> savedTeams = teamRepo.getTeamIdById(teamId);

		// Not sure of the state of the data
		if (savedTeams.size() > 1) {
			LOGGER.warn("More than one collector item found for teamId " + teamId);
		}

		if (!savedTeams.isEmpty()) {
			return savedTeams.get(0);
		}

		return new ScopeOwnerCollectorItem();
	}
	
	private Scope findExistingProject(String projectId) {
		List<Scope> existingProjects = projectRepo.getScopeById(projectId);
		
		if(existingProjects.size() > 1) {
			LOGGER.warn("More than one collector item found for projectId " + projectId);
		}
		
		if(!existingProjects.isEmpty()) {
			return existingProjects.get(0);
		}
		
		return new Scope();
	}

}
