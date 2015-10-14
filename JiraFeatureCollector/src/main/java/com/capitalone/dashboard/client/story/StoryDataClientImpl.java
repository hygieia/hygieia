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

import com.capitalone.dashboard.datafactory.jira.JiraDataFactoryImpl;
import com.capitalone.dashboard.model.Feature;
import com.capitalone.dashboard.repository.FeatureCollectorRepository;
import com.capitalone.dashboard.repository.FeatureRepository;
import com.capitalone.dashboard.util.ClientUtil;
import com.capitalone.dashboard.util.FeatureSettings;
import com.capitalone.dashboard.util.FeatureWidgetQueries;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This is the primary implemented/extended data collector for the feature
 * collector. This will get data from the source system, but will grab the
 * majority of needed data and aggregate it in a single, flat MongoDB collection
 * for consumption.
 *
 * @author kfk884
 *
 */
public class StoryDataClientImpl extends FeatureDataClientSetupImpl implements
		StoryDataClient {
	private static Log logger = LogFactory.getLog(StoryDataClientImpl.class);

	private final FeatureSettings featureSettings;
	private final FeatureWidgetQueries featureWidgetQueries;
	private final FeatureRepository featureRepo;
	private final ClientUtil tools;

	/**
	 * Extends the constructor from the super class.
	 *
	 * @param teamRepository
	 */
	public StoryDataClientImpl(FeatureSettings featureSettings,
			FeatureRepository featureRepository,
			FeatureCollectorRepository featureCollectorRepository) {
		super(featureSettings, featureRepository, featureCollectorRepository);
		logger.debug("Constructing data collection for the feature widget, story-level data...");

		this.featureSettings = featureSettings;
		this.featureRepo = featureRepository;
		this.featureWidgetQueries = new FeatureWidgetQueries(
				this.featureSettings);
		tools = new ClientUtil();
	}

	/**
	 * Updates the MongoDB with a JSONArray received from the source system
	 * back-end with story-based data.
	 *
	 * @param tmpMongoDetailArray
	 *            A JSON response in JSONArray format from the source system
	 * @param featureCollector
	 * @return
	 * @return
	 */
	protected void updateMongoInfo(JSONArray tmpMongoDetailArray) {
		try {
			JSONObject dataMainObj = new JSONObject();
			logger.debug("Size of PagingJSONArray: "
					+ tmpMongoDetailArray.size());
			for (int i = 0; i < tmpMongoDetailArray.size(); i++) {
				try {
					if (dataMainObj != null) {
						dataMainObj.clear();
					}
					dataMainObj = (JSONObject) tmpMongoDetailArray.get(i);
					JSONObject fields = (JSONObject) dataMainObj.get("fields");
					JSONObject issueType = (JSONObject) fields.get("issuetype");
					JSONObject project = (JSONObject) fields.get("project");
					JSONObject assignee = (JSONObject) fields.get("assignee");
					JSONObject status = (JSONObject) fields.get("status");
					JSONObject statusCategory = (JSONObject) status
							.get("statusCategory");
					JSONArray sprint = (JSONArray) fields
							.get("customfield_10007");
					Feature feature = new Feature();

					@SuppressWarnings("unused")
					boolean deleted = this.removeExistingEntity(tools
							.sanitizeResponse(dataMainObj.get("id")));
					if (!tools.sanitizeResponse(issueType.get("name"))
							.equalsIgnoreCase("Story")) {
						throw new IllegalArgumentException();
					}

					// collectorId
					feature.setCollectorId(featureCollectorRepository
							.findByName("Jira").getId());

					// ID
					feature.setsId(tools.sanitizeResponse(dataMainObj.get("id")));

					// sNumber
					feature.setsNumber(tools.sanitizeResponse(dataMainObj
							.get("key")));

					// sName
					feature.setsName(tools.sanitizeResponse(fields
							.get("summary")));

					// sStatus
					feature.setsStatus(tools.sanitizeResponse(statusCategory
							.get("name")));

					// sState
					feature.setsState(tools.sanitizeResponse(statusCategory
							.get("name")));

					// sSoftwareTesting - does not exist for Jira
					feature.setsSoftwareTesting("");

					// sEstimate, in seconds, converted to hours, rounded down:
					// 8 hours = 1 day; 5 days = 1 week
					String hours = "";
					long seconds = 0;
					try {
						seconds = (long) fields.get("timeestimate");
						hours = Integer.toString((int) (seconds / 3600));
						feature.setsEstimate(tools.sanitizeResponse(hours));
					} catch (Exception e) {
						feature.setsEstimate("0");
					}

					// sChangeDate
					feature.setChangeDate(tools.toCanonicalDate(tools
							.sanitizeResponse(fields.get("updated"))));

					// IsDeleted - does not exist for Jira
					feature.setIsDeleted("False");

					// sProjectID
					feature.setsProjectID(tools.sanitizeResponse(project
							.get("id")));

					// sProjectName
					feature.setsProjectName(tools.sanitizeResponse(project
							.get("name")));

					// sProjectBeginDate - does not exist in Jira
					feature.setsProjectBeginDate("");

					// sProjectEndDate - does not exist in Jira
					feature.setsProjectEndDate("");

					// sProjectChangeDate - does not exist for this asset level
					// in
					// Jira
					feature.setsProjectChangeDate("");

					// sProjectState - does not exist in Jira
					feature.setsProjectState("");

					// sProjectIsDeleted - does not exist in Jira
					feature.setsProjectIsDeleted("False");

					// sProjectPath - does not exist in Jira
					feature.setsProjectPath("");

					/*
					 * Epic Data
					 */
					try {
						String epicKey = tools.sanitizeResponse(fields
								.get("customfield_10400"));
						if (epicKey == null || epicKey.isEmpty()) {
							throw new NullPointerException();
						}
						JSONObject epicData = this.getEpicData(epicKey);

						// sEpicID
						feature.setsEpicID(tools.sanitizeResponse(epicData
								.get("id")));

						// sEpicNumber
						feature.setsEpicNumber(tools.sanitizeResponse(epicData
								.get("key")));

						// sEpicName
						feature.setsEpicName(tools.sanitizeResponse(epicData
								.get("name")));

						// sEpicBeginDate - mapped to create date
						feature.setsEpicBeginDate(tools.toCanonicalDate(tools
								.sanitizeResponse(epicData.get("created"))));

						// sEpicEndDate
						feature.setsEpicEndDate(tools.toCanonicalDate(tools
								.sanitizeResponse(epicData.get("dueDate"))));

						// sEpicAssetState
						feature.setsEpicAssetState(tools
								.sanitizeResponse(epicData.get("status")));
					} catch (ArrayIndexOutOfBoundsException
							| NullPointerException e) {
						feature.setsEpicID("");
						feature.setsEpicNumber("");
						feature.setsEpicName("");
						feature.setsEpicBeginDate("");
						feature.setsEpicEndDate("");
						feature.setsEpicType("");
						feature.setsEpicAssetState("");
						feature.setsEpicChangeDate("");
					}
					// sEpicPDD - does not exist in Jira
					feature.setsEpicPDD("");

					// sEpicHPSMReleaseID - does not exist in Jira
					feature.setsEpicHPSMReleaseID("");

					// sEpicType - does not exist in jira
					feature.setsEpicType("");

					// sEpicChangeDate - does not exist in jira
					feature.setsEpicChangeDate("");

					// sEpicIsDeleted - does not exist in Jira
					feature.setsEpicIsDeleted("False");

					/*
					 * Sprint Data
					 */
					try {
						JSONObject canonicalSprint = tools
								.toCanonicalSprint(sprint.get(0).toString());

						// sSprintID
						try {
							feature.setsSprintID(canonicalSprint.get("id")
									.toString());
						} catch (NullPointerException e) {
							feature.setsSprintID("");
						}

						// sSprintName
						try {
							feature.setsSprintName(canonicalSprint.get("name")
									.toString());
						} catch (NullPointerException e) {
							feature.setsSprintName("");
						}

						// sSprintBeginDate
						try {
							feature.setsSprintBeginDate(tools
									.toCanonicalDate(canonicalSprint.get(
											"startDate").toString()));
						} catch (NullPointerException e) {
							feature.setsSprintBeginDate("");
						}

						// sSprintEndDate
						try {
							feature.setsSprintEndDate(tools
									.toCanonicalDate(canonicalSprint.get(
											"endDate").toString()));
						} catch (NullPointerException e) {
							feature.setsSprintEndDate("");
						}

						// sSprintAssetState
						try {
							feature.setsSprintAssetState(canonicalSprint.get(
									"state").toString());
						} catch (NullPointerException e) {
							feature.setsSprintAssetState("");
						}
					} catch (ArrayIndexOutOfBoundsException
							| NullPointerException e) {
						feature.setsSprintID("");
						feature.setsSprintName("");
						feature.setsSprintBeginDate("");
						feature.setsSprintEndDate("");
						feature.setsSprintAssetState("");
					}

					// sSprintChangeDate - does not exist in Jira at this asset
					// level
					feature.setsSprintChangeDate("");

					// sSprintIsDeleted - does not exist in Jira
					feature.setsSprintIsDeleted("False");

					// sTeamID
					feature.setsTeamID(tools.sanitizeResponse(project.get("id")));

					// sTeamName
					feature.setsTeamName(tools.sanitizeResponse(project
							.get("name")));

					// sTeamChangeDate - not able to retrieve at this asset
					// level
					// from Jira
					feature.setsTeamChangeDate("");

					// sTeamAssetState
					feature.setsTeamAssetState("");

					// sTeamIsDeleted
					feature.setsTeamIsDeleted("False");

					// sOwnersID
					List<String> assigneeKey = new ArrayList<String>();
					try {
						assigneeKey.add(tools.sanitizeResponse(assignee
								.get("key")));
					} catch (NullPointerException e) {
						assigneeKey.add("");
					}
					feature.setsOwnersID(assigneeKey);

					// sOwnersShortName
					List<String> assigneeName = new ArrayList<String>();
					try {
						assigneeName.add(tools.sanitizeResponse(assignee
								.get("name")));
					} catch (NullPointerException e) {
						assigneeName.add("");
					}
					feature.setsOwnersShortName(assigneeName);

					// sOwnersFullName
					List<String> assigneeDisplayName = new ArrayList<String>();
					try {
						assigneeDisplayName.add(tools.sanitizeResponse(assignee
								.get("displayName")));
					} catch (NullPointerException e) {
						assigneeDisplayName.add("");
					}
					feature.setsOwnersFullName(assigneeDisplayName);

					// sOwnersUsername
					feature.setsOwnersUsername(assigneeName);

					// sOwnersState
					List<String> assigneeActive = new ArrayList<String>();
					try {
						assigneeActive.add(tools.sanitizeResponse(assignee
								.get("active")));
					} catch (NullPointerException e) {
						assigneeActive.add("");
					}
					feature.setsOwnersState(assigneeActive);

					// sOwnersChangeDate - does not exist in Jira
					List<String> temp = null;
					feature.setsOwnersChangeDate(tools.toCanonicalList(temp));

					// sOwnersIsDeleted - does not exist in Jira
					feature.setsOwnersIsDeleted(tools.toCanonicalList(temp));

					try {
						featureRepo.save(feature);
					} catch (Exception e) {
						logger.error("Unexpected error caused when attempting to save data\nCaused by:\n"
								+ e.getMessage()
								+ " : "
								+ e.getCause()
								+ "\n"
								+ Arrays.toString(e.getStackTrace()));
					}
				} catch (IllegalArgumentException e) {
					logger.info("Ignoring data entry for non-feature entity");
				}
			}
		} catch (NullPointerException e) {
			logger.error("A object, likely an array, was found to be null upon sanitization and thus the main object was skipped and not added to the source system.\nThis is an error that should be resolved by a developer if it continues to occur (essentially, you are now missing data):\n"
					+ e.getMessage()
					+ " : "
					+ e.getCause()
					+ "\n"
					+ Arrays.toString(e.getStackTrace()));
		} catch (Exception e) {
			logger.error("Unexpected error caused while mapping data from source system to local data store:\n"
					+ e.getMessage()
					+ " : "
					+ e.getCause()
					+ "\n"
					+ Arrays.toString(e.getStackTrace()));
		}
	}

	/**
	 * Retrieves the related Epic to the current issue from Jira. To make this
	 * thread-safe, please synchronize and lock on the result of this method.
	 *
	 * @param epicId
	 *            A given Epic Key
	 * @return A valid JSONObject with Epic data held within
	 */
	@SuppressWarnings("unchecked")
	protected JSONObject getEpicData(String epicKey) {
		JSONObject canonicalRs = new JSONObject();
		// JSONObject interrimRs = new JSONObject();
		JSONArray nativeRs = new JSONArray();
		JSONObject innerRs = new JSONObject();
		String jiraCredentials = this.featureSettings.getJiraCredentials();
		String jiraBaseUrl = this.featureSettings.getJiraBaseUrl();
		String jiraQueryEndpoint = this.featureSettings.getJiraQueryEndpoint();
		String query = this.featureWidgetQueries.getEpicQuery(epicKey, "epic");

		try {
			JiraDataFactoryImpl jiraConnect = new JiraDataFactoryImpl(
					jiraCredentials, jiraBaseUrl, jiraQueryEndpoint);
			jiraConnect.buildBasicQuery(query);
			nativeRs = jiraConnect.getEpicQueryResponse();

			try {
				innerRs = (JSONObject) nativeRs.get(0);
				JSONObject fields = (JSONObject) innerRs.get("fields");
				JSONObject status = (JSONObject) fields.get("status");
				canonicalRs.put("id", innerRs.get("id"));
				canonicalRs.put("key", innerRs.get("key"));
				canonicalRs.put("name", fields.get("summary"));
				canonicalRs.put("created", fields.get("created"));
				canonicalRs.put("dueDate", fields.get("dueDate"));
				canonicalRs.put("status", status.get("name"));
				canonicalRs.put("updated", fields.get("updated"));
			} catch (NullPointerException | StringIndexOutOfBoundsException
					| ArrayIndexOutOfBoundsException e) {
				canonicalRs.clear();
			}
		} catch (Exception e) {
			logger.error("There was a problem connecting to Jira while getting sub-relationships to epics:"
					+ e.getMessage()
					+ " : "
					+ e.getCause()
					+ "\n"
					+ Arrays.toString(e.getStackTrace()));
			canonicalRs.clear();
		}

		return canonicalRs;
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
		super.returnDate = tools.toNativeDate(super.returnDate);
		String queryName = this.featureSettings.getStoryQuery();
		super.query = this.featureWidgetQueries.getQuery(returnDate, queryName);
		logger.debug("updateStoryInformation: queryName = " + query + "; query = " + query);
		updateObjectInformation();

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
				logger.debug("Removed existing entities that will be replaced by newer instances");
			}
		} catch (IndexOutOfBoundsException ioobe) {
			logger.debug("Nothing matched the redundancy checking from the database");
		} catch (Exception e) {
			logger.error("There was a problem validating the redundancy of the data model");
			e.printStackTrace();
		}

		return deleted;
	}
}
