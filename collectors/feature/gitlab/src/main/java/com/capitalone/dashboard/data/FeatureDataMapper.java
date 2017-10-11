package com.capitalone.dashboard.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Component;

import com.capitalone.dashboard.gitlab.model.GitlabIssue;
import com.capitalone.dashboard.model.Feature;
import com.capitalone.dashboard.model.Project;
import com.capitalone.dashboard.util.FeatureCollectorConstants;

@Component
public class FeatureDataMapper {
	
	private static final String EMPTY_STRING = "";
	private static final String FALSE_DELETED_STATE = "False";
	private static final String ACTIVE_ASSET_STATE = "Active";
	private static final String FEATURE_IN_PROGRESS_STATUS = "In Progress";
	private static final String FEATURE_DONE_STATUS = "Done";
	private static final String GITLAB_DONE_STATUS = "closed";
	
	public Feature mapToFeatureItem(Project project, GitlabIssue gitlabIssue, List<String> inProgressLabelsForProject, ObjectId existingIssueId, ObjectId gitlabCollectorId) {
		String issueId = String.valueOf(gitlabIssue.getId());
		String storyNumber = String.valueOf(gitlabIssue.getIid());
		
		Feature issue = new Feature();
		issue.setId(existingIssueId);
		issue.setsNumber(storyNumber);
		issue.setsId(issueId);
		issue.setCollectorId(gitlabCollectorId);
		issue.setIsDeleted(FALSE_DELETED_STATE);
		issue.setsName(gitlabIssue.getTitle());
		issue.setsStatus(determineStoryStatus(gitlabIssue, inProgressLabelsForProject));
		issue.setsState(ACTIVE_ASSET_STATE);
		issue.setsEstimate(String.valueOf(gitlabIssue.getWeight()));
		issue.setChangeDate(gitlabIssue.getUpdatedAt());
		
		//Project Data
		issue.setsProjectID(project.getProjectId());
		issue.setsProjectName(project.getProjectId());
		issue.setsProjectBeginDate(EMPTY_STRING);
		issue.setsProjectEndDate(EMPTY_STRING);
		issue.setsProjectChangeDate(EMPTY_STRING);
		issue.setsProjectState(EMPTY_STRING);
		issue.setsProjectIsDeleted(FALSE_DELETED_STATE);
		issue.setsProjectPath(EMPTY_STRING);
		
		//Team Data
		issue.setsTeamID(project.getTeamId());
		issue.setsTeamAssetState(EMPTY_STRING);
		issue.setsTeamName(project.getTeamId());
		issue.setsTeamChangeDate(EMPTY_STRING);
		issue.setsTeamIsDeleted(FALSE_DELETED_STATE);

		//Set Owners Data
		issue.setsOwnersChangeDate(new ArrayList<String>());
		issue.setsOwnersState(Arrays.asList(ACTIVE_ASSET_STATE));
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
		issue.setsEpicBeginDate(EMPTY_STRING);
		issue.setsEpicEndDate(EMPTY_STRING);
		issue.setsEpicType(EMPTY_STRING);
		issue.setsEpicAssetState(EMPTY_STRING);
		issue.setsEpicChangeDate(EMPTY_STRING);
		issue.setsEpicIsDeleted(FALSE_DELETED_STATE);
	}

	private void setSprintData(GitlabIssue gitlabIssue, Feature issue) {
		if (gitlabIssue.getMilestone() != null && StringUtils.isNotBlank(gitlabIssue.getMilestone().getDueDate())) {
			issue.setsSprintID(String.valueOf(gitlabIssue.getMilestone().getId()));
			issue.setsSprintName(gitlabIssue.getMilestone().getTitle());
			issue.setsSprintBeginDate(gitlabIssue.getMilestone().getCreatedAt());
			issue.setsSprintEndDate(gitlabIssue.getMilestone().getDueDate());
			issue.setsSprintAssetState(ACTIVE_ASSET_STATE);
			issue.setsSprintChangeDate(gitlabIssue.getMilestone().getUpdatedAt());
			issue.setsSprintIsDeleted(FALSE_DELETED_STATE);
		} 
		else {
			issue.setsSprintID(FeatureCollectorConstants.SPRINT_KANBAN);
			issue.setsSprintName(FeatureCollectorConstants.SPRINT_KANBAN);
			issue.setsSprintBeginDate(EMPTY_STRING);
			issue.setsSprintEndDate(EMPTY_STRING);
			issue.setsSprintAssetState(ACTIVE_ASSET_STATE);
			issue.setsSprintChangeDate(EMPTY_STRING);
			issue.setsSprintIsDeleted(FALSE_DELETED_STATE);
		}
	}
	
	private String determineStoryStatus(GitlabIssue issue, List<String> inProgressLabelsForProject) {
		if(GITLAB_DONE_STATUS.equals(issue.getState())) {
			return FEATURE_DONE_STATUS;
		}
		else if (CollectionUtils.containsAny(inProgressLabelsForProject, issue.getLabels())) {
			return FEATURE_IN_PROGRESS_STATUS;
		}
		
		return EMPTY_STRING;
	}

}
