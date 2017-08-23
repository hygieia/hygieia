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
import com.capitalone.dashboard.client.Sprint;
import com.capitalone.dashboard.model.Feature;
import com.capitalone.dashboard.model.FeatureStatus;
import com.capitalone.dashboard.model.Team;
import com.capitalone.dashboard.repository.FeatureCollectorRepository;
import com.capitalone.dashboard.repository.FeatureRepository;
import com.capitalone.dashboard.repository.TeamRepository;
import com.capitalone.dashboard.util.ClientUtil;
import com.capitalone.dashboard.util.FeatureCollectorConstants;
import com.capitalone.dashboard.util.CoreFeatureSettings;
import com.capitalone.dashboard.util.DateUtil;
import com.capitalone.dashboard.util.FeatureSettings;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
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
	private static final String TO_DO = "To Do";
	private static final String IN_PROGRESS = "In Progress";
	private static final String DONE = "Done";
	
	private static final Comparator<Sprint> SPRINT_COMPARATOR = new Comparator<Sprint>() {
		@Override
		public int compare(Sprint o1, Sprint o2) {
			int cmp1 = ObjectUtils.compare(o1.getStartDateStr(), o2.getStartDateStr());
			
			if (cmp1 != 0) {
				return cmp1;
			}
			
			return ObjectUtils.compare(o1.getEndDateStr(), o2.getEndDateStr());
		}
	};
	
	// works with ms too (just ignores them)
	private final DateFormat SETTINGS_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

	private final FeatureSettings featureSettings;
	private final FeatureRepository featureRepo;
	private final FeatureCollectorRepository featureCollectorRepository;
	private final TeamRepository teamRepository;
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
			FeatureRepository featureRepository, FeatureCollectorRepository featureCollectorRepository, TeamRepository teamRepository,
			JiraClient jiraClient) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Constructing data collection for the feature widget, story-level data...");
		}

		this.featureSettings = featureSettings;
		this.featureRepo = featureRepository;
		this.featureCollectorRepository = featureCollectorRepository;
		this.teamRepository = teamRepository;
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
				
		updateStatuses();

		boolean hasMore = true;
		for (int i = 0; hasMore; i += pageSize) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Obtaining story information starting at index " + i + "...");
			}
			long queryStart = System.currentTimeMillis();
			List<Issue> issues = jiraClient.getIssues(startTime, i);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Story information query took " + (System.currentTimeMillis() - queryStart) + " ms");
			}
			
			if (issues != null && !issues.isEmpty()) {
				updateMongoInfo(issues);
				count += issues.size();
			}

			LOGGER.info("Loop i " + i + " pageSize " + issues.size());
			
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
	@SuppressWarnings({ "PMD.AvoidDeeplyNestedIfStmts", "PMD.NPathComplexity" })
	private void updateMongoInfo(List<Issue> currentPagedJiraRs) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Size of paged Jira response: " + (currentPagedJiraRs == null? 0 : currentPagedJiraRs.size()));
		}
		
		if (currentPagedJiraRs != null) {
			List<Feature> featuresToSave = new ArrayList<>();
			
			Map<String, String> issueEpics = new HashMap<>();
			ObjectId jiraFeatureId = featureCollectorRepository.findByName(FeatureCollectorConstants.JIRA).getId();
			Set<String> issueTypeNames = new HashSet<>();
			for (String issueTypeName : featureSettings.getJiraIssueTypeNames()) {
				issueTypeNames.add(issueTypeName.toLowerCase(Locale.getDefault()));
			}
			
			
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
				
				if (issueTypeNames.contains(TOOLS.sanitizeResponse(issueType.getName()).toLowerCase(Locale.getDefault()))) {
					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug(String.format("[%-12s] %s", 
								TOOLS.sanitizeResponse(issue.getKey()),
								TOOLS.sanitizeResponse(issue.getSummary())));
					}
					
					// collectorId
					feature.setCollectorId(jiraFeatureId);
					
					// ID
					feature.setsId(TOOLS.sanitizeResponse(issue.getId()));
					
					// Type
					feature.setsTypeId(TOOLS.sanitizeResponse(issueType.getId()));
					feature.setsTypeName(TOOLS.sanitizeResponse(issueType.getName()));

					processFeatureData(feature, issue, fields);
					
					// delay processing epic data for performance
					if (epic != null && epic.getValue() != null && !TOOLS.sanitizeResponse(epic.getValue()).isEmpty()) {
						issueEpics.put(feature.getsId(), TOOLS.sanitizeResponse(epic.getValue()));
					}

					
					processSprintData(feature, sprint);
					
					processAssigneeData(feature, assignee);
					
					featuresToSave.add(feature);
				}
			}
			
			// Load epic data into cache
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Processing epic data");
			}
			
			long epicStartTime = System.currentTimeMillis();
			Collection<String> epicsToLoad = issueEpics.values();
			loadEpicData(epicsToLoad);
			
			for (Feature feature : featuresToSave) {
				String epicKey = issueEpics.get(feature.getsId());
				
				processEpicData(feature, epicKey);
			}
			
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Processing epic data took " + (System.currentTimeMillis() - epicStartTime) + " ms");
			}
			
			// Saving back to MongoDB
			featureRepo.save(featuresToSave);
		}
	}
	
	@SuppressWarnings({"PMD.ExcessiveMethodLength", "PMD.NPathComplexity"})
	private void processFeatureData(Feature feature, Issue issue, Map<String, IssueField> fields) {
		BasicProject project = issue.getProject();
		String status = this.toCanonicalFeatureStatus(issue.getStatus().getName());
		String changeDate = issue.getUpdateDate().toString();
		
		// sNumber
		feature.setsNumber(TOOLS.sanitizeResponse(issue.getKey()));

		// sName
		feature.setsName(TOOLS.sanitizeResponse(issue.getSummary()));

		// sStatus
		feature.setsStatus(TOOLS.sanitizeResponse(status));

		// sState
		feature.setsState(TOOLS.sanitizeResponse(status));
		
		// sUrl (Example: 'http://my.jira.com/browse/KEY-1001')
        feature.setsUrl(featureSettings.getJiraBaseUrl() 
                + (featureSettings.getJiraBaseUrl().substring(featureSettings.getJiraBaseUrl().length()-1).equals("/") ? "" : "/")
                + "browse/" + TOOLS.sanitizeResponse(issue.getKey()));
		
		int originalEstimate = 0;
		
		// Tasks use timetracking, stories use aggregatetimeoriginalestimate and aggregatetimeestimate
		if (issue.getTimeTracking() != null && issue.getTimeTracking().getOriginalEstimateMinutes() != null) {
			originalEstimate = issue.getTimeTracking().getOriginalEstimateMinutes();
		} else if (fields.get("aggregatetimeoriginalestimate") != null
				&& fields.get("aggregatetimeoriginalestimate").getValue() != null) {
			// this value is in seconds
			originalEstimate = ((Integer)fields.get("aggregatetimeoriginalestimate").getValue()) / 60;
		}
		
		feature.setsEstimateTime(originalEstimate);
		
		// sStoryPoints
		IssueField storyPointsField = fields.get(featureSettings.getJiraStoryPointsFieldName());
		if (storyPointsField != null && storyPointsField.getValue() != null && !TOOLS.sanitizeResponse(storyPointsField.getValue()).isEmpty()) {
			Double value = Double.parseDouble(TOOLS.sanitizeResponse(storyPointsField.getValue()));
			feature.setsEstimate(String.valueOf(value.intValue()));
		} else {
			feature.setsEstimate("0");
		}

		// sChangeDate
		feature.setChangeDate(TOOLS.toCanonicalDate(TOOLS.sanitizeResponse(changeDate)));

		// IsDeleted - does not exist for Jira
		feature.setIsDeleted("False");

		// sProjectID
		feature.setsProjectID(TOOLS.sanitizeResponse(project.getId()));

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
		

		IssueField team = fields.get(featureSettings.getJiraTeamFieldName());
		if (team != null && team.getValue() != null && !TOOLS.sanitizeResponse(team.getValue()).isEmpty()) {
			Object teamObj = team.getValue();

			String teamID = "";
			Team scopeOwner = null;
			if (teamObj instanceof JSONObject) {
				try {
					String teamName = (String)((JSONObject)teamObj).get("value");
					scopeOwner = teamRepository.findByName(teamName);
					if (scopeOwner != null) {
						teamID = scopeOwner.getTeamId();
					}
				} catch (JSONException e) {
					LOGGER.error("Unable to parse value for " + teamObj);
				}
			} else {
				teamID = TOOLS.sanitizeResponse(team.getValue());
				scopeOwner = teamRepository.findByTeamId(teamID);
			}

			// sTeamID
			feature.setsTeamID(teamID);
			if (scopeOwner != null && StringUtils.isNotEmpty(scopeOwner.getName())) {
			    // sTeamName
				feature.setsTeamName(TOOLS.sanitizeResponse(scopeOwner.getName()));
			}
		}
		
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
	
	private void processEpicData(Feature feature, String epicKey) {
		if (epicKey != null && !epicKey.isEmpty()) {
			Issue epicData = getEpicData(epicKey);
			if (epicData != null) {
				String epicId = epicData.getId().toString();
				String epicNumber = epicData.getKey().toString();
				String epicName = epicData.getSummary().toString();
				String epicBeginDate = epicData.getCreationDate().toString();
				String epicStatus = epicData.getStatus().getName();
	
				// sEpicID
				feature.setsEpicID(TOOLS.sanitizeResponse(epicId));
	
				// sEpicNumber
				feature.setsEpicNumber(TOOLS.sanitizeResponse(epicNumber));
	
				// sEpicName
				feature.setsEpicName(TOOLS.sanitizeResponse(epicName));
				
				// sEpicUrl (Example: 'http://my.jira.com/browse/KEY-1001')
		        feature.setsEpicUrl(featureSettings.getJiraBaseUrl() 
		                + (featureSettings.getJiraBaseUrl().substring(featureSettings.getJiraBaseUrl().length()-1).equals("/") ? "" : "/")
		                + "browse/" + TOOLS.sanitizeResponse(epicNumber));
	
				// sEpicBeginDate - mapped to create date
				if ((epicBeginDate != null) && !(epicBeginDate.isEmpty())) {
					feature.setsEpicBeginDate(TOOLS.toCanonicalDate(
							TOOLS.sanitizeResponse(epicBeginDate)));
				} else {
					feature.setsEpicBeginDate("");
				}
	
				// sEpicEndDate
				if (epicData.getDueDate() != null) {
					feature.setsEpicEndDate(TOOLS.toCanonicalDate(
							TOOLS.sanitizeResponse(epicData.getDueDate())));
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
	
	@SuppressWarnings("PMD.NPathComplexity")
	private void processSprintData(Feature feature, IssueField sprintField) {
		if (sprintField != null && sprintField.getValue() != null && !"".equals(sprintField.getValue())) {
			Object sValue = sprintField.getValue();
			
			try {
				List<Sprint> sprints = TOOLS.parseSprints(sValue);

				// Now sort so we can use the most recent one
				// yyyy-MM-dd'T'HH:mm:ss format so string compare will be fine
				Collections.sort(sprints, SPRINT_COMPARATOR);
				
				if (!sprints.isEmpty()) {
					// Use the latest sprint
					Sprint sprint = sprints.get(sprints.size() - 1);
					
					// sSprintID
					if (sprint.getId() != null) {
						feature.setsSprintID(String.valueOf(sprint.getId()));
					} else {
						feature.setsSprintID("");
					}
					
					// sSprintName
					if (sprint.getName() != null) {
						feature.setsSprintName(sprint.getName());
					} else {
						feature.setsSprintName("");
					}
					
					// sSprintUrl (Example: 'http://my.jira.com/secure/RapidBoard.jspa?rapidView=123&view=reporting&chart=sprintRetrospective&sprint=1597' where sprintID = 1597 and rapidViewID = 123)
					if (StringUtils.isNotEmpty(feature.getsSprintID()) && sprint.getRapidViewId() != null) {
    			        feature.setsSprintUrl(featureSettings.getJiraBaseUrl() 
    			                + (featureSettings.getJiraBaseUrl().substring(featureSettings.getJiraBaseUrl().length()-1).equals("/") ? "" : "/")
    			                + "secure/RapidBoard.jspa?rapidView=" + sprint.getRapidViewId()
    			                + "&view=reporting&chart=sprintRetrospective&sprint=" + feature.getsSprintID());
					}
	
					// sSprintBeginDate
					if (sprint.getStartDateStr() != null) {
						feature.setsSprintBeginDate(TOOLS.toCanonicalDate(sprint.getStartDateStr()));
					} else {
						feature.setsSprintBeginDate("");
					}
	
					// sSprintEndDate
					if (sprint.getEndDateStr() != null) {
						feature.setsSprintEndDate(TOOLS.toCanonicalDate(sprint.getEndDateStr()));
					} else {
						feature.setsSprintEndDate("");
					}
	
					// sSprintAssetState
					if (sprint.getState() != null) {
						feature.setsSprintAssetState(sprint.getState());
					} else {
						feature.setsSprintAssetState("");
					}
				} else {
					LOGGER.error("Failed to obtain sprint data from " + sValue);
				}
			} catch (ParseException | RuntimeException e) {
				LOGGER.error("Failed to obtain sprint data from " + sValue, e);
			}
		} else {
			// Issue #678 - leave sprint blank. Not having a sprint does not imply kanban
			// as a story on a scrum board without a sprint is really on the backlog
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
	
	private void loadEpicData(Collection<String> epicKeys) {
		// No need to lookup items that are already cached
		Set<String> epicsToLookup = new HashSet<>();
		epicsToLookup.addAll(epicKeys);
		epicsToLookup.removeAll(epicCache.keySet());
		
		List<String> epicsToLookupL = new ArrayList<>(epicsToLookup);
		
		if (!epicsToLookupL.isEmpty()) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Obtaining epic information for epics: " + epicsToLookupL);
			}
			
			// Do this at most 50 at a time as jira doesn't seem to always work when there are a lot of items in an in clause
			int maxEpicsToLookup = Math.min(featureSettings.getPageSize(), 50);
			
			for (int i = 0; i < epicsToLookupL.size(); i += maxEpicsToLookup) {
				int endIdx = Math.min(i + maxEpicsToLookup, epicsToLookupL.size());
				
				List<String> epicKeysSub = epicsToLookupL.subList(i, endIdx);
				
				List<Issue> epics = jiraClient.getEpics(epicKeysSub);
				
				for (Issue epic : epics) {
					String epicKey = epic.getKey();
					
					epicCache.put(epicKey, epic);
				}
			}
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
	private Issue getEpicData(String epicKey) {
		if (epicCache.containsKey(epicKey)) {
			return epicCache.get(epicKey);
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
	
	private void updateStatuses() {
		Map<String, String> statusMap = jiraClient.getStatusMapping();
		for (String status : statusMap.keySet()) {
			String statusCategory = statusMap.get(status);
			if (TO_DO.equals(statusCategory)) {
				todoCache.add(status.toLowerCase(Locale.getDefault()));
			} else if (IN_PROGRESS.equals(statusCategory)) {
				inProgressCache.add(status.toLowerCase(Locale.getDefault()));
			} else if (DONE.equals(statusCategory)) {
				doneCache.add(status.toLowerCase(Locale.getDefault()));
			}
		}
	}
}
