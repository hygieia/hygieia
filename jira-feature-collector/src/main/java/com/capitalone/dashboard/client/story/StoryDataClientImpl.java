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
import com.capitalone.dashboard.datafactory.jira.JiraDataFactoryImpl;
import com.capitalone.dashboard.model.Feature;
import com.capitalone.dashboard.repository.FeatureCollectorRepository;
import com.capitalone.dashboard.repository.FeatureRepository;
import com.capitalone.dashboard.util.ClientUtil;
import com.capitalone.dashboard.util.Constants;
import com.capitalone.dashboard.util.FeatureSettings;
import com.capitalone.dashboard.util.FeatureWidgetQueries;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * This is the primary implemented/extended data collector for the feature
 * collector. This will get data from the source system, but will grab the
 * majority of needed data and aggregate it in a single, flat MongoDB collection
 * for consumption.
 * 
 * @author kfk884
 * 
 */
public class StoryDataClientImpl extends FeatureDataClientSetupImpl implements StoryDataClient {
	private static final Logger LOGGER = LoggerFactory.getLogger(StoryDataClientImpl.class);

	private final FeatureSettings featureSettings;
	private final FeatureWidgetQueries featureWidgetQueries;
	private final FeatureRepository featureRepo;
	private final static ClientUtil TOOLS = new ClientUtil();

	/**
	 * Extends the constructor from the super class.
	 * 
	 * @param teamRepository
	 */
	public StoryDataClientImpl(FeatureSettings featureSettings,
			FeatureRepository featureRepository,
			FeatureCollectorRepository featureCollectorRepository) {
		super(featureSettings, featureRepository, featureCollectorRepository);
		LOGGER.debug("Constructing data collection for the feature widget, story-level data...");

		this.featureSettings = featureSettings;
		this.featureRepo = featureRepository;
		this.featureWidgetQueries = new FeatureWidgetQueries(this.featureSettings);
	}

	/**
	 * Updates the MongoDB with a JSONArray received from the source system
	 * back-end with story-based data.
	 * 
	 * @param currentPagedJiraRs
	 *            A list response of Jira issues from the source system
	 */
	@Override
	@SuppressWarnings({ "PMD.ExcessiveMethodLength", "PMD.NcssMethodCount", "PMD.NPathComplexity",
			"PMD.AvoidDeeplyNestedIfStmts" })
	protected void updateMongoInfo(List<Issue> currentPagedJiraRs) {
		try {
			LOGGER.debug("Size of paged Jira response: ", currentPagedJiraRs.size());
			if ((currentPagedJiraRs != null) && !(currentPagedJiraRs.isEmpty())) {
				Iterator<Issue> globalResponseItr = currentPagedJiraRs.iterator();
				while (globalResponseItr.hasNext()) {
					/*
					 * Initialize DOMs
					 */
					Feature feature = new Feature();
					Issue issue = globalResponseItr.next();
					Iterable<IssueField> rawFields = issue.getFields();
					HashMap<String, IssueField> fields = new LinkedHashMap<String, IssueField>();
					if (rawFields != null) {
						Iterator<IssueField> itr = rawFields.iterator();
						while (itr.hasNext()) {
							IssueField field = itr.next();
							fields.put(field.getId(), field);
						}
					}
					IssueType issueType = issue.getIssueType();
					BasicProject project = issue.getProject();
					User assignee = issue.getAssignee();
					String status = issue.getStatus().getName();
					String estimate = String.valueOf(issue.getTimeTracking()
							.getRemainingEstimateMinutes());
					IssueField epic = fields.get(super.featureSettings.getJiraEpicIdFieldName());
					String changeDate = issue.getUpdateDate().toString();
					IssueField sprint = fields.get(super.featureSettings
							.getJiraSprintDataFieldName());
					/*
					 * Removing any existing entities where they exist in the
					 * local DB store...
					 */
					@SuppressWarnings("unused")
					boolean deleted = this.removeExistingEntity(TOOLS.sanitizeResponse(issue
							.getId()));
					if (TOOLS.sanitizeResponse(issueType.getName()).equalsIgnoreCase(
							super.featureSettings.getJiraIssueTypeId())) {
						// collectorId
						feature.setCollectorId(featureCollectorRepository
								.findByName(Constants.JIRA).getId());

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
						feature.setsEstimate(this.toHours(estimate));

						// sChangeDate
						feature.setChangeDate(TOOLS.toCanonicalDate(TOOLS
								.sanitizeResponse(changeDate)));

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

						// sProjectChangeDate - does not exist for this asset
						// level in Jira
						feature.setsProjectChangeDate("");

						// sProjectState - does not exist in Jira
						feature.setsProjectState("");

						// sProjectIsDeleted - does not exist in Jira
						feature.setsProjectIsDeleted("False");

						// sProjectPath - does not exist in Jira
						feature.setsProjectPath("");

						/*
						 * Epic Data - Note: Will only grab first epic
						 * associated
						 */
						if ((epic.getName() != null) && !(epic.getName().isEmpty())) {
							List<Issue> epicData = this.getEpicData(TOOLS.sanitizeResponse(epic
									.getValue()));
							Iterable<IssueField> rawEpicFields = epicData.get(0).getFields();
							HashMap<String, IssueField> epicFields = new LinkedHashMap<String, IssueField>();
							if (rawEpicFields != null) {
								Iterator<IssueField> itr = rawFields.iterator();
								while (itr.hasNext()) {
									IssueField epicField = itr.next();
									epicFields.put(epicField.getId(), epicField);
								}
							}
							String epicId = epicData.get(0).getId().toString();
							String epicNumber = epicData.get(0).getKey().toString();
							String epicName = epicData.get(0).getSummary().toString();
							String epicBeginDate = epicData.get(0).getCreationDate().toString();
							IssueField epicEndDate = epicFields.get("duedate");
							String epicStatus = epicData.get(0).getStatus().getName();

							// sEpicID
							feature.setsEpicID(TOOLS.sanitizeResponse(epicId));

							// sEpicNumber
							feature.setsEpicNumber(TOOLS.sanitizeResponse(epicNumber));

							// sEpicName
							feature.setsEpicName(TOOLS.sanitizeResponse(epicName));

							// sEpicBeginDate - mapped to create date
							if ((epicBeginDate != null) && !(epicBeginDate.isEmpty())) {
								feature.setsEpicBeginDate(TOOLS.toCanonicalDate(TOOLS
										.sanitizeResponse(epicBeginDate)));
							} else {
								feature.setsEpicBeginDate("");
							}

							// sEpicEndDate
							if (epicEndDate != null) {
								feature.setsEpicEndDate(TOOLS.toCanonicalDate(TOOLS
										.sanitizeResponse(epicEndDate.getValue())));
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

						// sEpicType - does not exist in jira
						feature.setsEpicType("");

						// sEpicChangeDate - does not exist in jira
						feature.setsEpicChangeDate("");

						// sEpicIsDeleted - does not exist in Jira
						feature.setsEpicIsDeleted("False");

						/*
						 * Sprint Data
						 */
						Map<String, Object> canonicalSprint = TOOLS.toCanonicalSprintPOJO(sprint
								.getValue().toString());
						if (canonicalSprint != null && !(canonicalSprint.isEmpty())) {
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
								feature.setsSprintBeginDate(TOOLS.toCanonicalDate(canonicalSprint
										.get("startDate").toString()));
							} else {
								feature.setsSprintBeginDate("");
							}

							// sSprintEndDate
							if (canonicalSprint.get("endDate") != null) {
								feature.setsSprintEndDate(TOOLS.toCanonicalDate(canonicalSprint
										.get("endDate").toString()));
							} else {
								feature.setsSprintEndDate("");
							}

							// sSprintAssetState
							if (canonicalSprint.get("state") != null) {
								feature.setsSprintAssetState(canonicalSprint.get("state")
										.toString());
							} else {
								feature.setsSprintAssetState("");
							}
						} else {
							feature.setsSprintID("");
							feature.setsSprintName("");
							feature.setsSprintBeginDate("");
							feature.setsSprintEndDate("");
							feature.setsSprintAssetState("");
						}

						// sSprintChangeDate - does not exist in Jira
						feature.setsSprintChangeDate("");

						// sSprintIsDeleted - does not exist in Jira
						feature.setsSprintIsDeleted("False");

						// sTeamID
						feature.setsTeamID(TOOLS.sanitizeResponse(project.getId()));

						// sTeamName
						feature.setsTeamName(TOOLS.sanitizeResponse(project.getName()));

						// sTeamChangeDate - not able to retrieve at this asset
						// level
						// from Jira
						feature.setsTeamChangeDate("");

						// sTeamAssetState
						feature.setsTeamAssetState("");

						// sTeamIsDeleted
						feature.setsTeamIsDeleted("False");

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
							if (!assignee.getDisplayName().isEmpty()
									&& (assignee.getDisplayName() != null)) {
								assigneeDisplayName.add(TOOLS.sanitizeResponse(assignee
										.getDisplayName()));
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

						// sOwnersState - does not exist in Jira at this level
						List<String> assigneeActive = new ArrayList<String>();
						assigneeActive.add("Active");
						feature.setsOwnersState(assigneeActive);

						// sOwnersChangeDate - does not exist in Jira
						feature.setsOwnersChangeDate(TOOLS.toCanonicalList(new ArrayList<String>()));

						// sOwnersIsDeleted - does not exist in Jira
						feature.setsOwnersIsDeleted(TOOLS.toCanonicalList(new ArrayList<String>()));
					}

					// Saving back to MongoDB
					featureRepo.save(feature);
				}
			}
		} catch (Exception e) {
			LOGGER.error(
					"Unexpected error caused while mapping data from source system to local data store:\n"
							+ e.getMessage() + " : " + e.getCause(), e);
		}
	}

	/**
	 * Retrieves the related Epic to the current issue from Jira. To make this
	 * thread-safe, please synchronize and lock on the result of this method.
	 * 
	 * @param epicKey
	 *            A given Epic Key
	 * @return A valid Jira Epic issue object
	 */
	protected List<Issue> getEpicData(String epicKey) {
		List<Issue> epicRs = new ArrayList<Issue>();
		String jiraCredentials = this.featureSettings.getJiraCredentials();
		String jiraBaseUrl = this.featureSettings.getJiraBaseUrl();
		String query = this.featureWidgetQueries.getEpicQuery(epicKey, "epic");
		String proxyUri = null;
		String proxyPort = null;
		try {
			if (!this.featureSettings.getJiraProxyUrl().isEmpty()
					&& (this.featureSettings.getJiraProxyPort() != null)) {
				proxyUri = this.featureSettings.getJiraProxyUrl();
				proxyPort = this.featureSettings.getJiraProxyPort();
			}
			JiraDataFactoryImpl jiraConnect = new JiraDataFactoryImpl(jiraCredentials, jiraBaseUrl,
					proxyUri, proxyPort);
			jiraConnect.setQuery(query);
			epicRs = jiraConnect.getJiraIssues();
		} catch (Exception e) {
			LOGGER.error(
					"There was a problem connecting to Jira while getting sub-relationships to epics:"
							+ e.getMessage() + " : " + e.getCause(), e);
			epicRs = new ArrayList<Issue>();
		}

		return epicRs;
	}

	/**
	 * Explicitly updates queries for the source system, and initiates the
	 * update to MongoDB from those calls.
	 */
	public void updateStoryInformation() {
		super.objClass = Feature.class;
		super.returnDate = this.featureSettings.getDeltaStartDate();
		if (super.getMaxChangeDate() != null) {
			super.returnDate = super.getMaxChangeDate();
		}
		super.returnDate = getChangeDateMinutePrior(super.returnDate);
		super.returnDate = TOOLS.toNativeDate(super.returnDate);
		String queryName = this.featureSettings.getStoryQuery();
		super.query = this.featureWidgetQueries.getStoryQuery(returnDate,
				super.featureSettings.getJiraIssueTypeId(), queryName);
		LOGGER.debug("updateStoryInformation: queryName = " + query + "; query = " + query);
		updateObjectInformation();

	}

	/**
	 * Jira story estimate in minutes, converted to hours, rounded down: For
	 * Jira, 8 hours = 1 day; 5 days = 1 week
	 * 
	 * @param estimate
	 *            Minute representation of estimate content
	 * @return Hour representation of minutes, rounded down
	 */
	protected String toHours(String estimate) {
		String hours = "";
		long minutes = 0;
		if ((estimate != null) && !(estimate.isEmpty()) || estimate.equalsIgnoreCase("null")) {
			minutes = Long.valueOf(estimate);
			hours = TOOLS.sanitizeResponse(Integer.toString((int) (minutes / 60)));
		} else {
			hours = "0";
		}

		return hours;
	}

	/**
	 * Validates current entry and removes new entry if an older item exists in
	 * the repo
	 * 
	 * @param A
	 *            local repository item ID (not the precise mongoID)
	 */
	protected Boolean removeExistingEntity(String localId) {
		boolean deleted = false;

		try {
			List<Feature> listOfFeature = featureRepo.getFeatureIdById(localId);
			for (Feature f : listOfFeature) {
				featureRepo.delete(f.getId());
				deleted = true;
				LOGGER.debug("Removed existing entities that will be replaced by newer instances");
			}
		} catch (IndexOutOfBoundsException ioobe) {
			LOGGER.debug("Nothing matched the redundancy checking from the database", ioobe);
		} catch (Exception e) {
			LOGGER.error("There was a problem validating the redundancy of the data model", e);
		}

		return deleted;
	}
}
