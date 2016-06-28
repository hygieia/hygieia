/*************************DA-BOARD-LICENSE-START*********************************
 * Copyright 2014 CapitalOne, LLC.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *************************DA-BOARD-LICENSE-END*********************************/

package com.capitalone.dashboard.client.story;

import com.atlassian.jira.rest.client.api.domain.BasicProject;
import com.atlassian.jira.rest.client.api.domain.Issue;
import com.atlassian.jira.rest.client.api.domain.IssueField;
import com.atlassian.jira.rest.client.api.domain.IssueType;
import com.atlassian.jira.rest.client.api.domain.User;
import com.capitalone.dashboard.client.JiraClient;
import com.capitalone.dashboard.model.Feature;
import com.capitalone.dashboard.model.FeatureStatus;
import com.capitalone.dashboard.repository.FeatureCollectorRepository;
import com.capitalone.dashboard.repository.FeatureRepository;
import com.capitalone.dashboard.util.ClientUtil;
import com.capitalone.dashboard.util.FeatureCollectorConstants;
import com.capitalone.dashboard.util.CoreFeatureSettings;
import com.capitalone.dashboard.util.DateUtil;
import com.capitalone.dashboard.util.FeatureSettings;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * This is the primary implemented/extended data collector for the feature
 * collector. This will get data from the source system, but will grab the
 * majority of needed data and aggregate it in a single, flat MongoDB collection
 * for consumption.
 * 
 * @author kfk884
 * 
 */
public class StoryDataClientImpl implements StoryDataClient {
	private static final Logger LOGGER = LoggerFactory.getLogger(StoryDataClientImpl.class);
	private static final ClientUtil TOOLS = ClientUtil.getInstance();
	
	// works with ms too (just ignores them)
	private final DateFormat SETTINGS_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

	private final FeatureSettings featureSettings;
	private final FeatureRepository featureRepo;
	private final FeatureCollectorRepository featureCollectorRepository;
	private final JiraClient jiraClient;
	
	// epicId : list of epics
	private Map<String, Issue> epicCache;
	private Set<String> todoCache;
	private Set<String> inProgressCache;
	private Set<String> doneCache;

	/**
	 * Extends the constructor from the super class.
	 */
	public StoryDataClientImpl(CoreFeatureSettings coreFeatureSettings, FeatureSettings featureSettings, 
			FeatureRepository featureRepository, FeatureCollectorRepository featureCollectorRepository,
			JiraClient jiraClient) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Constructing data collection for the feature widget, story-level data...");
		}

		this.featureSettings = featureSettings;
		this.featureRepo = featureRepository;
		this.featureCollectorRepository = featureCollectorRepository;
		this.jiraClient = jiraClient;
		
		this.epicCache = new HashMap<>();
		
		todoCache = buildStatusCache(coreFeatureSettings.getTodoStatuses());
		inProgressCache = buildStatusCache(coreFeatureSettings.getDoingStatuses());
		doneCache = buildStatusCache(coreFeatureSettings.getDoneStatuses());
	}

	/**
	 * Explicitly updates queries for the source system, and initiates the
	 * update to MongoDB from those calls.
	 */
	public int updateStoryInformation() {
		int count = 0;
		epicCache.clear(); // just in case class is made static w/ spring in future
		
		//long startDate = featureCollectorRepository.findByName(FeatureCollectorConstants.JIRA).getLastExecuted();
		
		String startDateStr = featureSettings.getDeltaStartDate();
		String maxChangeDate = getMaxChangeDate();
		if (maxChangeDate != null) {
			startDateStr = maxChangeDate;
		}
		
		startDateStr = getChangeDateMinutePrior(startDateStr);
		long startTime;
		try {
			startTime = SETTINGS_DATE_FORMAT.parse(startDateStr).getTime();
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
		
		int pageSize = jiraClient.getPageSize();
		
		boolean hasMore = true;
		for (int i = 0; hasMore; i += pageSize) {
			List<Issue> issues = jiraClient.getIssues(startTime, i);
			
			if (issues != null && !issues.isEmpty()) {
				updateMongoInfo(issues);
				count += issues.size();
			}
			
			// will result in an extra call if number of results == pageSize
			// but I would rather do that then complicate the jira client implementation
			if (issues == null || issues.size() < pageSize) {
				hasMore = false;
				break;
			}
		}
		
		return count;
	}

	/**
	 * Updates the MongoDB with a JSONArray received from the source system
	 * back-end with story-based data.
	 * 
	 * @param currentPagedJiraRs
	 *            A list response of Jira issues from the source system
	 */
	@SuppressWarnings({ "PMD.AvoidDeeplyNestedIfStmts" })
	private void updateMongoInfo(List<Issue> currentPagedJiraRs) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Size of paged Jira response: " + (currentPagedJiraRs == null? 0 : currentPagedJiraRs.size()));
		}
		
		if (currentPagedJiraRs != null) {
			ObjectId jiraFeatureId = featureCollectorRepository.findByName(FeatureCollectorConstants.JIRA).getId();
			String issueTypeName = featureSettings.getJiraIssueTypeId();
			
			for (Issue issue : currentPagedJiraRs) {
				String issueId = TOOLS.sanitizeResponse(issue.getId());
				
				Feature feature = findOneFeature(issueId);
				if (feature == null) {
					 feature = new Feature();
				}
				
				Map<String, IssueField> fields = buildFieldMap(issue.getFields());
				IssueType issueType = issue.getIssueType();
				User assignee = issue.getAssignee();
				IssueField epic = fields.get(featureSettings.getJiraEpicIdFieldName());
				IssueField sprint = fields.get(featureSettings.getJiraSprintDataFieldName());
				
				if (TOOLS.sanitizeResponse(issueType.getName()).equalsIgnoreCase(issueTypeName)) {
					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug("Processing " + TOOLS.sanitizeResponse(issue.getKey()));
					}
					
					// collectorId
					feature.setCollectorId(jiraFeatureId);

					processFeatureData(feature, issue);

					processEpicData(feature, epic);
					
					processSprintData(feature, sprint);
					
					processAssigneeData(feature, assignee);
				}

				// Saving back to MongoDB
				featureRepo.save(feature);
			}
		}
	}
	
	private void processFeatureData(Feature feature, Issue issue) {
		BasicProject project = issue.getProject();
		String status = this.toCanonicalFeatureStatus(issue.getStatus().getName());
		String estimate = String.valueOf(issue.getTimeTracking().getRemainingEstimateMinutes());
		String changeDate = issue.getUpdateDate().toString();

		// ID
		feature.setsId(TOOLS.sanitizeResponse(issue.getId()));
		
		// sNumber
		feature.setsNumber(TOOLS.sanitizeResponse(issue.getKey()));

		// sName
		feature.setsName(TOOLS.sanitizeResponse(issue.getSummary()));

		// sStatus
		feature.setsStatus(TOOLS.sanitizeResponse(status));

		// sState
		feature.setsState(TOOLS.sanitizeResponse(status));

		// sEstimate,
		feature.setsEstimate(TOOLS.toHours(estimate));

		// sChangeDate
		feature.setChangeDate(TOOLS.toCanonicalDate(TOOLS.sanitizeResponse(changeDate)));

		// IsDeleted - does not exist for Jira
		feature.setIsDeleted("False");

		// sProjectID
		feature.setsProjectID(TOOLS.sanitizeResponse(project.getKey()));

		// sProjectName
		feature.setsProjectName(TOOLS.sanitizeResponse(project.getName()));

		// sProjectBeginDate - does not exist in Jira
		feature.setsProjectBeginDate("");

		// sProjectEndDate - does not exist in Jira
		feature.setsProjectEndDate("");

		// sProjectChangeDate - does not exist for this asset level in Jira
		feature.setsProjectChangeDate("");

		// sProjectState - does not exist in Jira
		feature.setsProjectState("");

		// sProjectIsDeleted - does not exist in Jira
		feature.setsProjectIsDeleted("False");

		// sProjectPath - does not exist in Jira
		feature.setsProjectPath("");
		
		// sTeamChangeDate - not able to retrieve at this asset level from Jira
		feature.setsTeamChangeDate("");

		// sTeamAssetState
		feature.setsTeamAssetState("");

		// sTeamIsDeleted
		feature.setsTeamIsDeleted("False");

		// sOwnersState - does not exist in Jira at this level
		feature.setsOwnersState(Arrays.asList("Active"));

		// sOwnersChangeDate - does not exist in Jira
		feature.setsOwnersChangeDate(TOOLS.toCanonicalList(Collections.<String>emptyList()));

		// sOwnersIsDeleted - does not exist in Jira
		feature.setsOwnersIsDeleted(TOOLS.toCanonicalList(Collections.<String>emptyList()));
	}
	
	private void processEpicData(Feature feature, IssueField epic) {
		if (epic != null && epic.getValue() != null && !TOOLS.sanitizeResponse(epic.getValue()).isEmpty()) {
			Issue epicData = getEpicData(TOOLS.sanitizeResponse(epic.getValue()));
			if (epicData != null) {
				Map<String, IssueField> epicFields = buildFieldMap(epicData.getFields());
				String epicId = epicData.getId().toString();
				String epicNumber = epicData.getKey().toString();
				String epicName = epicData.getSummary().toString();
				String epicBeginDate = epicData.getCreationDate().toString();
				IssueField epicEndDate = epicFields.get("duedate");
				String epicStatus = epicData.getStatus().getName();
	
				// sEpicID
				feature.setsEpicID(TOOLS.sanitizeResponse(epicId));
	
				// sEpicNumber
				feature.setsEpicNumber(TOOLS.sanitizeResponse(epicNumber));
	
				// sEpicName
				feature.setsEpicName(TOOLS.sanitizeResponse(epicName));
	
				// sEpicBeginDate - mapped to create date
				if ((epicBeginDate != null) && !(epicBeginDate.isEmpty())) {
					feature.setsEpicBeginDate(TOOLS.toCanonicalDate(
							TOOLS.sanitizeResponse(epicBeginDate)));
				} else {
					feature.setsEpicBeginDate("");
				}
	
				// sEpicEndDate
				if (epicEndDate != null) {
					feature.setsEpicEndDate(TOOLS.toCanonicalDate(
							TOOLS.sanitizeResponse(epicEndDate.getValue())));
				} else {
					feature.setsEpicEndDate("");
				}
	
				// sEpicAssetState
				if (epicStatus != null) {
					feature.setsEpicAssetState(TOOLS.sanitizeResponse(epicStatus));
				} else {
					feature.setsEpicAssetState("");
				}
			} else {
				feature.setsEpicID("");
				feature.setsEpicNumber("");
				feature.setsEpicName("");
				feature.setsEpicBeginDate("");
				feature.setsEpicEndDate("");
				feature.setsEpicType("");
				feature.setsEpicAssetState("");
				feature.setsEpicChangeDate("");
			}
		} else {
			feature.setsEpicID("");
			feature.setsEpicNumber("");
			feature.setsEpicName("");
			feature.setsEpicBeginDate("");
			feature.setsEpicEndDate("");
			feature.setsEpicType("");
			feature.setsEpicAssetState("");
			feature.setsEpicChangeDate("");
		}
		
		// sEpicType - does not exist in jira
		feature.setsEpicType("");

		// sEpicChangeDate - does not exist in jira
		feature.setsEpicChangeDate("");

		// sEpicIsDeleted - does not exist in Jira
		feature.setsEpicIsDeleted("False");
	}
	
	private void processSprintData(Feature feature, IssueField sprint) {
		if (sprint != null && sprint.getValue() != null && !TOOLS.sanitizeResponse(sprint.getValue()).isEmpty()) {
			Map<String, Object> canonicalSprint = TOOLS.toCanonicalSprintPOJO(sprint.getValue().toString());
			// sSprintID
			if (canonicalSprint.get("id") != null) {
				feature.setsSprintID(canonicalSprint.get("id").toString());
			} else {
				feature.setsSprintID("");
			}

			// sSprintName
			if (canonicalSprint.get("name") != null) {
				feature.setsSprintName(canonicalSprint.get("name").toString());
			} else {
				feature.setsSprintName("");
			}

			// sSprintBeginDate
			if (canonicalSprint.get("startDate") != null) {
				feature.setsSprintBeginDate(TOOLS.toCanonicalDate(canonicalSprint.get("startDate").toString()));
			} else {
				feature.setsSprintBeginDate("");
			}

			// sSprintEndDate
			if (canonicalSprint.get("endDate") != null) {
				feature.setsSprintEndDate(TOOLS.toCanonicalDate(canonicalSprint.get("endDate").toString()));
			} else {
				feature.setsSprintEndDate("");
			}

			// sSprintAssetState
			if (canonicalSprint.get("state") != null) {
				feature.setsSprintAssetState(canonicalSprint.get("state").toString());
			} else {
				feature.setsSprintAssetState("");
			}
		} else {
			/*
			 * For Kanban, associate a generic, never-ending
			 * kanban 'sprint'
			 */
			feature.setsSprintID(FeatureCollectorConstants.KANBAN_SPRINT_ID);
			feature.setsSprintName(FeatureCollectorConstants.KANBAN_SPRINT_ID);
			feature.setsSprintBeginDate(FeatureCollectorConstants.KANBAN_START_DATE);
			feature.setsSprintEndDate(FeatureCollectorConstants.KANBAN_END_DATE);
			feature.setsSprintAssetState("Active");
		}

		// sSprintChangeDate - does not exist in Jira
		feature.setsSprintChangeDate("");

		// sSprintIsDeleted - does not exist in Jira
		feature.setsSprintIsDeleted("False");
	}
	
	private void processAssigneeData(Feature feature, User assignee) {
		if (assignee != null) {
			// sOwnersID
			List<String> assigneeKey = new ArrayList<String>();
			// sOwnersShortName
			// sOwnersUsername
			List<String> assigneeName = new ArrayList<String>();
			if (!assignee.getName().isEmpty() && (assignee.getName() != null)) {
				assigneeKey.add(TOOLS.sanitizeResponse(assignee.getName()));
				assigneeName.add(TOOLS.sanitizeResponse(assignee.getName()));

			} else {
				assigneeKey = new ArrayList<String>();
				assigneeName = new ArrayList<String>();
			}
			feature.setsOwnersShortName(assigneeName);
			feature.setsOwnersUsername(assigneeName);
			feature.setsOwnersID(assigneeKey);

			// sOwnersFullName
			List<String> assigneeDisplayName = new ArrayList<String>();
			if (!assignee.getDisplayName().isEmpty() && (assignee.getDisplayName() != null)) {
				assigneeDisplayName.add(TOOLS.sanitizeResponse(assignee.getDisplayName()));
			} else {
				assigneeDisplayName.add("");
			}
			feature.setsOwnersFullName(assigneeDisplayName);
		} else {
			feature.setsOwnersUsername(new ArrayList<String>());
			feature.setsOwnersShortName(new ArrayList<String>());
			feature.setsOwnersID(new ArrayList<String>());
			feature.setsOwnersFullName(new ArrayList<String>());
		}
	}

	/**
	 * ETL for converting any number of custom Jira statuses to a reduced list
	 * of generally logical statuses used by Hygieia
	 * 
	 * @param nativeStatus
	 *            The status label as native to Jira
	 * @return A Hygieia-canonical status, as defined by a Core enum
	 */
	private String toCanonicalFeatureStatus(String nativeStatus) {
		// default to backlog
		String canonicalStatus = FeatureStatus.BACKLOG.getStatus();
		
		if (nativeStatus != null) {
			String nsLower = nativeStatus.toLowerCase(Locale.getDefault());
			
			if (todoCache.contains(nsLower)) {
				canonicalStatus = FeatureStatus.BACKLOG.getStatus();
			} else if (inProgressCache.contains(nsLower)) {
				canonicalStatus = FeatureStatus.IN_PROGRESS.getStatus();
			} else if (doneCache.contains(nsLower)) {
				canonicalStatus = FeatureStatus.DONE.getStatus();
			}
		}
		
		return canonicalStatus;
	}
	
	/**
	 * Retrieves the maximum change date for a given query.
	 * 
	 * @return A list object of the maximum change date
	 */
	public String getMaxChangeDate() {
		String data = null;

		try {
			List<Feature> response = featureRepo
					.findTopByCollectorIdAndChangeDateGreaterThanOrderByChangeDateDesc(
							featureCollectorRepository.findByName(FeatureCollectorConstants.JIRA).getId(),
							featureSettings.getDeltaStartDate());
			if ((response != null) && !response.isEmpty()) {
				data = response.get(0).getChangeDate();
			}
		} catch (Exception e) {
			LOGGER.error("There was a problem retrieving or parsing data from the local "
					+ "repository while retrieving a max change date\nReturning null", e);
		}

		return data;
	}

	/**
	 * Retrieves the related Epic to the current issue from Jira. To make this
	 * thread-safe, please synchronize and lock on the result of this method.
	 * 
	 * @param epicKey
	 *            A given Epic Key
	 * @return A valid Jira Epic issue object
	 */
	private Issue getEpicData(String epicKey) {
		if (epicCache.containsKey(epicKey)) {
			return epicCache.get(epicCache);
		} else {
			Issue epic = jiraClient.getEpic(epicKey);
			epicCache.put(epicKey, epic);
			
			return epic;
		}
	}
	
	private String getChangeDateMinutePrior(String changeDateISO) {
		int priorMinutes = this.featureSettings.getScheduledPriorMin();
		return DateUtil.toISODateRealTimeFormat(DateUtil.getDatePriorToMinutes(
				DateUtil.fromISODateTimeFormat(changeDateISO), priorMinutes));
	}
	
	private Feature findOneFeature(String featureId) {
		List<Feature> features = featureRepo.getFeatureIdById(featureId);
		
		// Not sure of the state of the data
		if (features.size() > 1) {
			LOGGER.warn("More than one collector item found for scopeId " + featureId);
		}
		
		if (!features.isEmpty()) {
			return features.get(0);
		}
		
		return null;
	}
	
	private Map<String, IssueField> buildFieldMap(Iterable<IssueField> fields) {
		Map<String, IssueField> rt = new HashMap<String, IssueField>();
		
		if (fields != null) {
			for (IssueField issueField : fields) {
				rt.put(issueField.getId(), issueField);
			}
		}
		
		return rt;
	}
	
	private Set<String> buildStatusCache(List<String> statuses) {
		Set<String> rt = new HashSet<>();
		
		if (statuses != null) {
			for (String status : statuses) {
				rt.add(status.toLowerCase(Locale.getDefault()));
			}
		}
		
		return rt;
	}
}
