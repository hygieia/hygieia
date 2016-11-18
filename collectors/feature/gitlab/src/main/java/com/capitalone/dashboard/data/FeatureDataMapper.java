package com.capitalone.dashboard.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Component;

import com.capitalone.dashboard.gitlab.model.GitlabIssue;
import com.capitalone.dashboard.gitlab.model.GitlabProject;
import com.capitalone.dashboard.gitlab.model.GitlabTeam;
import com.capitalone.dashboard.model.Feature;
import com.capitalone.dashboard.model.Scope;
import com.capitalone.dashboard.model.ScopeOwnerCollectorItem;
import com.capitalone.dashboard.util.FeatureCollectorConstants;

@Component
public class FeatureDataMapper {
	
	public ScopeOwnerCollectorItem mapToScopeOwnerCollectorItem(GitlabTeam gitlabTeam, ObjectId existingTeamId, ObjectId gitlabFeatureCollectorId) {
		String teamId = String.valueOf(gitlabTeam.getId());
		
		ScopeOwnerCollectorItem team = new ScopeOwnerCollectorItem();
		team.setId(existingTeamId);
		team.setCollectorId(gitlabFeatureCollectorId);
		team.setTeamId(teamId);
		team.setName(gitlabTeam.getName());
		team.setChangeDate("");
		team.setAssetState("Active");
		team.setIsDeleted("False");
		
		return team;
	}
	
	public Scope mapToScopeItem(GitlabProject gitlabProject, ObjectId existingProjectId, ObjectId gitlabFeatureCollectorId) {
		String projectId = String.valueOf(gitlabProject.getId());
		
		Scope project = new Scope();
		project.setId(existingProjectId);
		project.setCollectorId(gitlabFeatureCollectorId);
		project.setpId(projectId);
		project.setName(gitlabProject.getName());
		project.setBeginDate("");
		project.setEndDate("");
		project.setChangeDate("");
		project.setAssetState("Active");
		project.setIsDeleted("False");
		project.setProjectPath(gitlabProject.getPath());
		
		return project;
	}
	
	public Feature mapToFeatureItem(GitlabIssue gitlabIssue, List<String> inProgressLabelsForProject, ObjectId existingIssueId, ObjectId gitlabCollectorId) {
		String issueId = String.valueOf(gitlabIssue.getId());
		String storyNumber = String.valueOf(gitlabIssue.getIid());
		String projectId = String.valueOf(gitlabIssue.getProject_id());
		String teamId = String.valueOf(gitlabIssue.getProject().getNamespace().getId());
		
		Feature issue = new Feature();
		issue.setId(existingIssueId);
		issue.setsNumber(storyNumber);
		issue.setsId(issueId);
		issue.setCollectorId(gitlabCollectorId);
		issue.setIsDeleted("False");
		issue.setsName(gitlabIssue.getTitle());
		issue.setsStatus(determineStoryStatus(gitlabIssue, inProgressLabelsForProject));
		issue.setsState("Active");
		issue.setsEstimate("1");
		issue.setChangeDate(gitlabIssue.getUpdated_at());
		
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

		//Set Owners Data
		issue.setsOwnersChangeDate(new ArrayList<String>());
		issue.setsOwnersState(Arrays.asList("Active"));
		issue.setsOwnersIsDeleted(new ArrayList<String>());
		
		setEpicData(gitlabIssue, issue);
		setSprintData(gitlabIssue, issue);
		
		return issue;
	}

	private void setEpicData(GitlabIssue gitlabIssue, Feature issue) {
		String issueId = String.valueOf(gitlabIssue.getId());
		String storyNumber = String.valueOf(gitlabIssue.getIid());
		
		issue.setsEpicID(issueId);
		issue.setsEpicNumber(storyNumber);
		issue.setsEpicName(gitlabIssue.getTitle());
		issue.setsEpicBeginDate("");
		issue.setsEpicEndDate("");
		issue.setsEpicType("");
		issue.setsEpicAssetState("");
		issue.setsEpicChangeDate("");
		issue.setsEpicIsDeleted("False");
	}

	private void setSprintData(GitlabIssue gitlabIssue, Feature issue) {
		if (gitlabIssue.getMilestone() != null && StringUtils.isNotBlank(gitlabIssue.getMilestone().getDue_date())) {
			issue.setsSprintID(String.valueOf(gitlabIssue.getMilestone().getId()));
			issue.setsSprintName(gitlabIssue.getMilestone().getTitle());
			issue.setsSprintBeginDate(gitlabIssue.getMilestone().getCreated_at());
			issue.setsSprintEndDate(gitlabIssue.getMilestone().getDue_date());
			issue.setsSprintAssetState("Active");
			issue.setsSprintChangeDate(gitlabIssue.getMilestone().getUpdated_at());
			issue.setsSprintIsDeleted("False");
		} 
		else {
			issue.setsSprintID(FeatureCollectorConstants.SPRINT_KANBAN);
			issue.setsSprintName(FeatureCollectorConstants.SPRINT_KANBAN);
			issue.setsSprintBeginDate("");
			issue.setsSprintEndDate("");
			issue.setsSprintAssetState("Active");
			issue.setsSprintChangeDate("");
			issue.setsSprintIsDeleted("False");
		}
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

}
