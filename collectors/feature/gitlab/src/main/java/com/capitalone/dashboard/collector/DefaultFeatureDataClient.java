package com.capitalone.dashboard.collector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.capitalone.dashboard.model.Feature;
import com.capitalone.dashboard.model.GitlabIssue;
import com.capitalone.dashboard.model.GitlabLabel;
import com.capitalone.dashboard.model.GitlabProject;
import com.capitalone.dashboard.model.GitlabTeam;
import com.capitalone.dashboard.model.Scope;
import com.capitalone.dashboard.model.ScopeOwnerCollectorItem;
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
	
	@Autowired
	public DefaultFeatureDataClient(FeatureCollectorRepository featureCollectorRepo, TeamItemRepository teamRepo, 
			ProjectItemRepository scopeRepo, IssueItemRepository featureRepo) {
		this.featureCollectorRepo = featureCollectorRepo;
		this.teamRepo = teamRepo;
		this.projectRepo = scopeRepo;
		this.featureRepo = featureRepo;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void updateTeams(List<GitlabTeam> gitlabTeams) {
		ObjectId gitlabFeatureCollectorId = featureCollectorRepo.findByName(FeatureCollectorConstants.GITLAB).getId();
		
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
		ObjectId gitlabFeatureCollectorId = featureCollectorRepo.findByName(FeatureCollectorConstants.GITLAB).getId();
		
		List<Scope> currentProjects = convertToScopeItems(projects, gitlabFeatureCollectorId);
		List<Scope> savedProjects = projectRepo.findScopeByCollectorId(gitlabFeatureCollectorId);
		
		Collection<Scope> projectsToAdd = CollectionUtils.subtract(currentProjects, savedProjects);
		projectRepo.save(projectsToAdd);
		LOGGER.info("Added {} new projects.", projectsToAdd.size());
		
		Collection<Scope> projectsToDelete = CollectionUtils.subtract(savedProjects, currentProjects);
		projectRepo.delete(projectsToDelete);
		LOGGER.info("Deleted {} projects.", projectsToDelete.size());
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void updateIssues(String projectId, List<GitlabIssue> issues, List<GitlabLabel> inProgressLabelsForProject) {
		ObjectId gitlabFeatureCollectorId = featureCollectorRepo.findByName(FeatureCollectorConstants.GITLAB).getId();
		List<String> inProgressLabels = new ArrayList<>();
		for(GitlabLabel label : inProgressLabelsForProject) {
			inProgressLabels.add(label.getName());
		}
		
		List<Feature> savedIssues = featureRepo.getFeaturesByCollectorAndProjectId(gitlabFeatureCollectorId, projectId);
		List<Feature> currentIssues = convertToFeatureItems(issues, gitlabFeatureCollectorId, inProgressLabels);
	
		Collection<Feature> featuresToDelete = CollectionUtils.subtract(savedIssues, currentIssues);
		
		
		featureRepo.delete(featuresToDelete);
		featureRepo.save(currentIssues);
		
	}
	
	@Override
	public List<ScopeOwnerCollectorItem> findEnabledTeams(ObjectId collectorId) {
		return teamRepo.findEnabledTeams(collectorId);
	}

	private List<Feature> convertToFeatureItems(List<GitlabIssue> gitlabIssues, ObjectId gitlabFeatureCollectorId, List<String> inProgressLabelsForProject) {
		List<Feature> issues = new ArrayList<>();
		
		for(GitlabIssue gitlabIssue : gitlabIssues) {
			String issueId = String.valueOf(gitlabIssue.getId());
			String storyNumber = String.valueOf(gitlabIssue.getIid());
			String projectId = String.valueOf(gitlabIssue.getProject_id());
			String teamId = String.valueOf(gitlabIssue.getProject().getNamespace().getId());
			
			Feature issue = findExistingIssue(issueId);
			issue.setsNumber(storyNumber);
			issue.setsId(issueId);
			issue.setCollectorId(gitlabFeatureCollectorId);
			issue.setIsDeleted("False");
			issue.setsName(gitlabIssue.getTitle());
			
			issue.setsStatus(determineStoryStatus(gitlabIssue, inProgressLabelsForProject));
			issue.setsState("Active");
			issue.setIsDeleted("False");
			
			//Made up stuff
			issue.setsEstimate("1");
			issue.setChangeDate("");
			
			//Project Data
			issue.setsProjectID(projectId);
			issue.setsProjectName("");
			issue.setsProjectBeginDate("");
			issue.setsProjectEndDate("");
			issue.setsProjectChangeDate("");
			issue.setsProjectState("");
			issue.setsProjectIsDeleted("False");
			issue.setsProjectPath("");
			
			//Team Data
			issue.setsTeamID(teamId);
			issue.setsTeamAssetState("");
			issue.setsTeamName(gitlabIssue.getProject().getNamespace().getName());
			issue.setsTeamChangeDate("");
			issue.setsTeamIsDeleted("False");
			
			//Owner Data
			issue.setsOwnersChangeDate(new ArrayList<String>());
			issue.setsOwnersState(Arrays.asList("Active"));
			issue.setsOwnersIsDeleted(new ArrayList<String>());
			
			
			//Epic Data
			issue.setsEpicID(issueId);
			issue.setsEpicNumber(storyNumber);
			issue.setsEpicName(gitlabIssue.getTitle());
			issue.setsEpicBeginDate("");
			issue.setsEpicEndDate("");
			issue.setsEpicType("");
			issue.setsEpicAssetState("");
			issue.setsEpicChangeDate("");
			issue.setsEpicIsDeleted("False");
			
			//Sprint data
			if (gitlabIssue.getMilestone() != null) {
				issue.setsSprintID(String.valueOf(gitlabIssue.getMilestone().getId()));
				issue.setsSprintName(gitlabIssue.getMilestone().getTitle());
				issue.setsSprintBeginDate(FeatureCollectorConstants.KANBAN_START_DATE);
				issue.setsSprintEndDate(gitlabIssue.getMilestone().getDue_date());
				if(StringUtils.isBlank(issue.getsSprintEndDate())) {
					issue.setsSprintEndDate("9999-10-14T09:47:38.354-05:00");
				}
				//TODO: map to actual states
				issue.setsSprintAssetState("Active");
				issue.setsSprintChangeDate(gitlabIssue.getMilestone().getUpdated_at());
				issue.setsSprintIsDeleted("False");
			} 
			else {
				issue.setsSprintID(FeatureCollectorConstants.KANBAN_SPRINT_ID);
				issue.setsSprintName(FeatureCollectorConstants.KANBAN_SPRINT_ID);
				issue.setsSprintBeginDate(FeatureCollectorConstants.KANBAN_START_DATE);
				issue.setsSprintEndDate(FeatureCollectorConstants.KANBAN_END_DATE);
				issue.setsSprintAssetState("Active");
				issue.setsSprintChangeDate("");
				issue.setsSprintIsDeleted("False");
			}
			
			
			issues.add(issue);
		}
		
		return issues;
	}

	private String determineStoryStatus(GitlabIssue issue, List<String> inProgressLabelsForProject) {
		if("closed".equals(issue.getState())) {
			return "Done";
		}
		else if (CollectionUtils.containsAny(inProgressLabelsForProject, issue.getLabels())) {
			return "In Progress";
		}
		
		return "";
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
	
	private Feature findExistingIssue(String id) {
		List<Feature> existing = featureRepo.getFeatureIdById(id);
		
		if(existing.size() > 1) {
			LOGGER.warn("More than one collector item found for featureId " + id);
		}
		
		if(!existing.isEmpty()) {
			return existing.get(0);
		}
		
		return new Feature();
	}

}
