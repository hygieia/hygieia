package com.capitalone.dashboard.collector;

import com.capitalone.dashboard.misc.HygieiaException;
import com.capitalone.dashboard.model.Epic;
import com.capitalone.dashboard.model.Feature;
import com.capitalone.dashboard.model.FeatureEpicResult;
import com.capitalone.dashboard.model.FeatureIssueLink;
import com.capitalone.dashboard.model.FeatureStatus;
import com.capitalone.dashboard.model.IssueResult;
import com.capitalone.dashboard.model.JiraMode;
import com.capitalone.dashboard.model.Scope;
import com.capitalone.dashboard.model.Sprint;
import com.capitalone.dashboard.model.Team;
import com.capitalone.dashboard.util.Supplier;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestOperations;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import static com.capitalone.dashboard.utils.Utilities.getLong;
import static com.capitalone.dashboard.utils.Utilities.getString;

/**
 * A client that communicates via REST API calls to jira.
 * <p>
 * Latest REST API: https://docs.atlassian.com/jira/REST/latest/
 * <br>
 * Created against API for Jira 7.x. Should work with 6.x and 5.x.
 *
 * @author <a href="mailto:MarkRx@users.noreply.github.com">MarkRx</a>
 */
@Component
public class DefaultJiraClient implements JiraClient {
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultJiraClient.class);

    private static final String TEMPO_TEAMS_REST_SUFFIX = "rest/tempo-teams/1/team";
    private static final String BOARD_TEAMS_REST_SUFFIX = "rest/agile/1.0/board";
    private static final String PROJECT_REST_SUFFIX = "rest/api/2/project";
    private static final String ISSUE_BY_PROJECT_REST_SUFFIX_BY_DATE = "rest/api/2/search?jql=project=%s and issueType in ('%s') and updatedDate>='%s'&fields=%s&startAt=%s";
    private static final String ISSUE_BY_BOARD_REST_SUFFIX_BY_DATE = "rest/agile/1.0/board/%s/issue?jql=issueType in ('%s') and updatedDate>='%s'&fields=%s&startAt=%s";
    private static final String EPIC_REST_SUFFIX = "rest/agile/1.0/issue/%s";

    private static final String ISSUE_BY_BOARD_FULL_REFRESH_REST_SUFFIX = "rest/agile/1.0/%s/%s/issue?jql=issueType in ('%s') and updatedDate>='%s'&fields=id&startAt=%s";

    private static final String STATIC_ISSUE_FIELDS = "id,key,issuetype,status,summary,created,updated,project,issuelinks,assignee,sprint,epic,aggregatetimeoriginalestimate,timeoriginalestimate";

    private static final String DEFAULT_ISSUE_TYPES = "Story,Epic";
    private static final int JIRA_BOARDS_PAGING = 50;
    private final FeatureSettings featureSettings;
    private final RestOperations restOperations;
    private String issueFields;
    private static JSONParser parser = new JSONParser();

    @Autowired
    public DefaultJiraClient(FeatureSettings featureSettings, Supplier<RestOperations> restOperationsSupplier) {
        this.featureSettings = featureSettings;
        this.restOperations = restOperationsSupplier.get();
        issueFields = STATIC_ISSUE_FIELDS + ','
                + featureSettings.getJiraTeamFieldName() + ','
                + featureSettings.getJiraSprintDataFieldName() + ','
                + featureSettings.getJiraEpicIdFieldName() + ','
                + featureSettings.getJiraStoryPointsFieldName();
    }


    /**
     * Get all the Scope (Project in Jira terms)
     *
     * @return List of Scope
     */
    @Override
    public Set<Scope> getProjects() {

        try {
            String url = featureSettings.getJiraBaseUrl() + (featureSettings.getJiraBaseUrl().endsWith("/") ? "" : "/")
                    + PROJECT_REST_SUFFIX;
            ResponseEntity<String> responseEntity = makeRestCall(url);
            String responseBody = responseEntity.getBody();

            JSONArray projects = (JSONArray) parser.parse(responseBody);

            return parseAsScopes(projects);

        } catch (ParseException pe) {
            LOGGER.error("Parser exception when parsing teams", pe);
        } catch (HygieiaException e) {
            LOGGER.error("Error in calling JIRA API", e);
        }
        return Collections.EMPTY_SET;
    }

    protected static Set<Scope> parseAsScopes(JSONArray projects) {
        Set<Scope> result = new HashSet<>();
        if (CollectionUtils.isEmpty(projects)) {
            return Collections.EMPTY_SET;
        }

        for (Object obj : projects) {
            JSONObject jo = (JSONObject) obj;
            String pId = getString(jo, "id");
            String pName = getString(jo, "name").trim();
            if (!StringUtils.isEmpty(pName)) {
                Scope scope = new Scope();
                scope.setpId(pId);
                scope.setName(pName);
                scope.setProjectPath(pName);
                scope.setBeginDate("");
                // endDate - does not exist for jira
                scope.setEndDate("");
                // changeDate - does not exist for jira
                scope.setChangeDate("");
                // assetState - does not exist for jira
                // isDeleted - does not exist for jira
                scope.setIsDeleted("False");
                result.add(scope);
            }
        }

        return result;
    }

    /**
     * Get a list of Boards. It's saved as Team in Hygieia
     *
     * @return List of Team
     */
    @Override
    public List<Team> getBoards() {
        int count = 0;
        int startAt = 0;
        boolean isLast = false;
        List<Team> result = new ArrayList<>();
        while (!isLast) {
            String url = featureSettings.getJiraBaseUrl() + (featureSettings.getJiraBaseUrl().endsWith("/") ? "" : "/")
                    + BOARD_TEAMS_REST_SUFFIX + "?startAt=" + startAt;
            try {
                ResponseEntity<String> responseEntity = makeRestCall(url);
                String responseBody = responseEntity.getBody();
                JSONObject teamsJson = (JSONObject) parser.parse(responseBody);

                if (teamsJson != null) {
                    JSONArray valuesArray = (JSONArray) teamsJson.get("values");
                    if (!CollectionUtils.isEmpty(valuesArray)) {
                        for (Object obj : valuesArray) {
                            JSONObject jo = (JSONObject) obj;
                            String teamId = getString(jo, "id");
                            String teamName = getString(jo, "name");
                            String teamType = getString(jo, "type");
                            Team team = new Team(teamId, teamName);
                            team.setTeamType(teamType);
                            team.setChangeDate("");
                            team.setAssetState("Active");
                            team.setIsDeleted("False");
                            result.add(team);
                            count = count + 1;
                        }
                        isLast = (boolean) teamsJson.get("isLast");
                        LOGGER.info("JIRA Collector collected " + count + " boards");
                        if (!isLast) {
                            startAt += JIRA_BOARDS_PAGING;
                        }
                    } else {
                        isLast = true;
                    }
                } else {
                    isLast = true;
                }
            } catch (ParseException pe) {
                isLast = true;
                LOGGER.error("Parser exception when parsing teams", pe);
            } catch (HygieiaException | HttpClientErrorException | HttpServerErrorException e) {
                isLast = true;
                LOGGER.error("Error in calling JIRA API: " + url , e.getMessage());
            }
        }
        return result;
    }

    /**
     * Get all the teams using Jira Rest API
     *
     * @return List of Teams
     */
    @Override
    public List<Team> getTeams() {
        List<Team> result = new ArrayList<>();
        try {
            String url = featureSettings.getJiraBaseUrl() + (featureSettings.getJiraBaseUrl().endsWith("/") ? "" : "/")
                    + TEMPO_TEAMS_REST_SUFFIX;
            ResponseEntity<String> responseEntity = makeRestCall(url);
            String responseBody = responseEntity.getBody();
            JSONArray teamsJson = (JSONArray) parser.parse(responseBody);
            if (teamsJson != null) {
                for (Object obj : teamsJson) {
                    JSONObject jo = (JSONObject) obj;
                    String teamId = getString(jo, "id");
                    String teamName = getString(jo, "name");
                    Team team = new Team(teamId, teamName);
                    team.setChangeDate("");
                    team.setTeamType("");
                    team.setAssetState("Active");
                    team.setIsDeleted("False");
                    result.add(team);
                }
            }
        } catch (ParseException pe) {
            LOGGER.error("Parser exception when parsing teams", pe);
        } catch (HygieiaException e) {
            LOGGER.error("Error in calling JIRA API", e);
        }
        return result;
    }

    /**
     * Get list of Features (Issues in Jira terms) given a project.
     *
     * @param board
     * @return List of Feature
     */
    @Override
    public FeatureEpicResult getIssues(Team board) {
        Map<String, Epic> epicMap = new HashMap<>();
        FeatureEpicResult featureEpicResult = new FeatureEpicResult();

        String lookBackDate = getUpdatedSince(board.getLastCollected());

        String issueTypes = featureSettings.getJiraIssueTypeNames() == null ? DEFAULT_ISSUE_TYPES : String.join(",", featureSettings.getJiraIssueTypeNames());

        List<Feature> features = new ArrayList<>();
        boolean isLast = false;
        long startAt = 0;

        while (!isLast) {
            String url = featureSettings.getJiraBaseUrl() + (featureSettings.getJiraBaseUrl().endsWith("/") ? "" : "/")
                    + ISSUE_BY_BOARD_REST_SUFFIX_BY_DATE;
            url = String.format(url, board.getTeamId(), issueTypes.replaceAll(",", "','"), lookBackDate, issueFields, startAt);
            try {
                IssueResult temp = getFeaturesFromQueryURL(url, epicMap, board);

                if (temp.getTotal() >= featureSettings.getMaxNumberOfFeaturesPerBoard()){
                    LOGGER.info("Board: \'" + board.getName() + "\' passes the feature max limit at " + temp.getTotal() + " features. Skipping..");
                    isLast = true;
                    continue;
                }

                //For issues collected in board mode, overwrite the team information
                temp.getFeatures().forEach(f -> {
                    f.setsTeamID(board.getTeamId());
                    f.setsTeamName(board.getName());
                });
                features.addAll(temp.getFeatures());
                isLast = temp.getTotal() < startAt ||  temp.getTotal() < JIRA_BOARDS_PAGING;
                startAt += temp.getPageSize();
            } catch (ParseException pe) {
                isLast = true;
                LOGGER.error("Parser exception when parsing issue", pe);
            } catch (HygieiaException | HttpClientErrorException | HttpServerErrorException e) {
                isLast = true;
                LOGGER.error("Error in calling JIRA API: " + url , e.getMessage());
            }
        }
        featureEpicResult.setFeatureList(features);
        featureEpicResult.getEpicList().addAll(epicMap.values());
        return featureEpicResult;
    }

    /**
     * Get list of Features (Issues in Jira terms) given a project.
     *
     * @param project
     * @return List of Feature
     */
    @Override
    public FeatureEpicResult getIssues(Scope project) {
        Map<String, Epic> epicMap = new HashMap<>();
        FeatureEpicResult featureEpicResult = new FeatureEpicResult();

        String lookBackDate = getUpdatedSince(project.getLastCollected());

        String issueTypes = featureSettings.getJiraIssueTypeNames() == null ? DEFAULT_ISSUE_TYPES : String.join(",", featureSettings.getJiraIssueTypeNames());

        List<Feature> features = new ArrayList<>();

        boolean isLast = false;
        long startAt = 0;

        while (!isLast) {
            try {
                String url = featureSettings.getJiraBaseUrl() + (featureSettings.getJiraBaseUrl().endsWith("/") ? "" : "/")
                        + ISSUE_BY_PROJECT_REST_SUFFIX_BY_DATE;
                url = String.format(url, project.getpId(), issueTypes.replaceAll(",", "','"), lookBackDate, issueFields, startAt);

                IssueResult temp = getFeaturesFromQueryURL(url, epicMap, null);

                features.addAll(temp.getFeatures());
                isLast = temp.getTotal() == features.size() || CollectionUtils.isEmpty(temp.getFeatures());
                startAt += temp.getPageSize();
            } catch (ParseException pe) {
                LOGGER.error("Parser exception when parsing issue", pe);
            } catch (HygieiaException e) {
                LOGGER.error("Error in calling JIRA API", e);
            }
        }
        featureEpicResult.setFeatureList(features);
        featureEpicResult.getEpicList().addAll(epicMap.values());
        return featureEpicResult;
    }


    private IssueResult getFeaturesFromQueryURL(String url, Map<String, Epic> epicMap, Team board) throws HygieiaException, ParseException {
        IssueResult result = new IssueResult();
        try {
            ResponseEntity<String> responseEntity = makeRestCall(url);
            String responseBody = responseEntity.getBody();
            JSONObject bodyObject = (JSONObject) parser.parse(responseBody);

            if (bodyObject != null) {
                long pageSize = getLong(bodyObject, "maxResults");
                long total = getLong(bodyObject, "total");
                result.setPageSize(pageSize);
                result.setTotal(total);
                JSONArray issueArray = (JSONArray) bodyObject.get("issues");
                if (CollectionUtils.isEmpty(issueArray)) {
                    return result;
                }

                issueArray.forEach(issue -> {
                    JSONObject issueJson = (JSONObject) issue;
                    String type = getIssueType(issueJson);

                    if (!StringUtils.isEmpty(featureSettings.getJiraEpicId()) && featureSettings.getJiraEpicId().equals(type)) {
                        saveEpic(issueJson, epicMap, true);
                        return;
                    }

                    if (!StringUtils.isEmpty(featureSettings.getJiraStoryId()) && featureSettings.getJiraStoryId().equals(type)) {
                        Feature feature = getFeature((JSONObject) issue, board);
                        String epicId = feature.getsEpicID();
                        if (!StringUtils.isEmpty(epicId)) {
                            Epic epic = epicMap.containsKey(epicId) ? epicMap.get(epicId) : getEpic(epicId, epicMap);
                            processEpicData(feature, epic);
                        }
                        result.getFeatures().add(feature);
                    }
                });
            }
        } catch (HttpClientErrorException | HttpServerErrorException he) {
            LOGGER.error("ERROR collecting issues. " + he.getResponseBodyAsString() + ". Url = " + url);
        }
        return result;
    }

    private static String getIssueType(JSONObject issueJson) {
        JSONObject fields = (JSONObject) issueJson.get("fields");
        JSONObject issueType = (JSONObject) fields.get("issuetype");
        return getString(issueType, "id");
    }


    /**
     * Construct Feature object
     *
     * @param issue
     * @return Feature
     */
    @SuppressWarnings("PMD.NPathComplexity")
    protected Feature getFeature(JSONObject issue, Team board) {
        Feature feature = new Feature();
        feature.setsId(getString(issue, "id"));
        feature.setsNumber(getString(issue, "key"));

        JSONObject fields = (JSONObject) issue.get("fields");

        JSONObject epic = (JSONObject) fields.get("epic");
        String epicId = getString(fields, featureSettings.getJiraEpicIdFieldName());
        feature.setsEpicID(epic != null ? getString(epic, "id") : epicId);

        JSONObject issueType = (JSONObject) fields.get("issuetype");
        if (issueType != null) {
            feature.setsTypeId(getString(issueType, "id"));
            feature.setsTypeName(getString(issueType, "name"));
        }

        JSONObject status = (JSONObject) fields.get("status");
        String sStatus = getStatus(status);
        feature.setsState(sStatus);
        feature.setsStatus(feature.getsState());

        String summary = getString(fields, "summary");
        feature.setsName(summary);

        feature.setsUrl(featureSettings.getJiraBaseUrl() + (featureSettings.getJiraBaseUrl().endsWith("/") ? "" : "/") + "browse/" + feature.getsNumber());

        long aggEstimate = getLong(fields, "aggregatetimeoriginalestimate");
        Long estimate = getLong(fields, "timeoriginalestimate");

        int originalEstimate = 0;
        // Tasks use timetracking, stories use aggregatetimeoriginalestimate and aggregatetimeestimate
        if (estimate != 0) {
            originalEstimate = estimate.intValue();
        } else if (aggEstimate != 0) {
            // this value is in seconds
            originalEstimate = Math.round((float) aggEstimate / 3600);
        }

        feature.setsEstimateTime(originalEstimate);

        String storyPoints = getString(fields, featureSettings.getJiraStoryPointsFieldName());

        feature.setsEstimate(storyPoints);

        feature.setChangeDate(getString(fields, "updated"));
        feature.setIsDeleted("False");

        JSONObject project = (JSONObject) fields.get("project");
        feature.setsProjectID(project != null ? getString(project, "id") : "");
        feature.setsProjectName(project != null ? getString(project, "name") : "");
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

        if(board != null){
            feature.setsTeamID(board.getTeamId());
            feature.setsTeamName(board.getName());
        } else {
            JSONObject team = (JSONObject) fields.get(featureSettings.getJiraTeamFieldName());
            if (team != null) {
                feature.setsTeamID(getString(team, "id"));
                feature.setsTeamName(getString(team, "value"));
            }
        }
        // sTeamChangeDate - not able to retrieve at this asset level from Jira
        feature.setsTeamChangeDate("");
        // sTeamAssetState
        feature.setsTeamAssetState("");
        // sTeamIsDeleted
        feature.setsTeamIsDeleted("False");
        // sOwnersState - does not exist in Jira at this level
        feature.setsOwnersState(Collections.singletonList("Active"));
        // sOwnersChangeDate - does not exist in Jira
        feature.setsOwnersChangeDate(Collections.EMPTY_LIST);
        // sOwnersIsDeleted - does not exist in Jira
        feature.setsOwnersIsDeleted(Collections.EMPTY_LIST);
        // issueLinks
        JSONArray issueLinkArray = (JSONArray) fields.get("issuelinks");
        feature.setIssueLinks(getIssueLinks(issueLinkArray));

        Sprint sprint = getSprint(fields);
        if (sprint != null) {
            processSprintData(feature, sprint);
        }
        JSONObject assignee = (JSONObject) fields.get("assignee");
        processAssigneeData(feature, assignee);
        return feature;
    }

    /**
     * Get status
     *
     * @param status
     * @return status
     */
    private static String getStatus(JSONObject status) {
        if (status == null) {
            return "";
        }
        JSONObject statusCategory = (JSONObject) status.get("statusCategory");
        if (statusCategory == null) {
            return "";
        }

        String statusString = getString(statusCategory, "name");
        FeatureStatus normalizedStatus;
        switch (statusString) {
            case "To Do":
                normalizedStatus = FeatureStatus.BACKLOG;
                break;
            case "Done":
                normalizedStatus = FeatureStatus.DONE;
                break;
            case "In Progress":
                normalizedStatus = FeatureStatus.IN_PROGRESS;
                break;
            default:
                normalizedStatus = FeatureStatus.BACKLOG;
                break;

        }
        return normalizedStatus.getStatus();
    }


    /**
     * Process Epic data for a feature, updating the passed in feature
     *
     * @param feature
     * @param epic
     */

    protected static void processEpicData(Feature feature, Epic epic) {
        feature.setsEpicID(epic.getId());
        feature.setsEpicIsDeleted("false");
        feature.setsEpicName(epic.getName());
        feature.setsEpicNumber(epic.getNumber());
        feature.setsEpicType("");
        feature.setsEpicAssetState(epic.getStatus());
        feature.setsEpicBeginDate(epic.getBeginDate());
        feature.setsEpicChangeDate(epic.getChangeDate());
        feature.setsEpicEndDate(epic.getEndDate());
        feature.setsEpicUrl(epic.getUrl());
    }

    protected Sprint getSprint(JSONObject fields) {
        JSONObject sprintJson = (JSONObject) fields.get("sprint");
        if (sprintJson != null) {
            Sprint sprint = new Sprint();
            sprint.setId(getString(sprintJson, "id"));
            sprint.setName(getString(sprintJson, "name"));
            sprint.setStartDateStr(getString(sprintJson, "startDate"));
            sprint.setEndDateStr(getString(sprintJson, "endDate"));
            sprint.setState(getString(sprintJson, "state"));
            sprint.setRapidViewId(getString(sprintJson, "originBoardId"));
            return sprint;
        } else {
            JSONArray sprintCustom = (JSONArray) fields.get(featureSettings.getJiraSprintDataFieldName());
            return SprintFormatter.parseSprint(sprintCustom);
        }
    }

    /**
     * Process Sprint data for a feature, updating the passed in feature
     *
     * @param feature
     * @param sprint
     */
    protected void processSprintData(Feature feature, Sprint sprint) {

        // sSprintChangeDate - does not exist in Jira
        feature.setsSprintChangeDate("");
        // sSprintIsDeleted - does not exist in Jira
        feature.setsSprintIsDeleted("False");

        feature.setsSprintID(sprint.getId());
        feature.setsSprintName(sprint.getName());
        feature.setsSprintBeginDate(sprint.getStartDateStr());
        feature.setsSprintEndDate(sprint.getEndDateStr());
        feature.setsSprintAssetState(sprint.getState());
        String rapidViewId = sprint.getRapidViewId();
        if (!StringUtils.isEmpty(rapidViewId) && !StringUtils.isEmpty(feature.getsSprintID())) {
            feature.setsSprintUrl(featureSettings.getJiraBaseUrl()
                    + (Objects.equals(featureSettings.getJiraBaseUrl().substring(featureSettings.getJiraBaseUrl().length() - 1), "/") ? "" : "/")
                    + "secure/RapidBoard.jspa?rapidView=" + rapidViewId
                    + "&view=reporting&chart=sprintRetrospective&sprint=" + feature.getsSprintID());
        }
    }


    /**
     * Process Assignee data for a feature, updating the passed in feature
     *
     * @param feature
     * @param assignee
     */
    @SuppressWarnings("PMD.NPathComplexity")
    protected static void processAssigneeData(Feature feature, JSONObject assignee) {
        if (assignee == null) {
            return;
        }
        String key = getString(assignee, "key");
        String name = getString(assignee, "name");
        String displayName = getString(assignee, "displayName");
        feature.setsOwnersID(key != null ? Collections.singletonList(key) : Collections.EMPTY_LIST);
        feature.setsOwnersUsername(name != null ? Collections.singletonList(name) : Collections.EMPTY_LIST);
        feature.setsOwnersShortName(feature.getsOwnersUsername());
        feature.setsOwnersFullName(displayName != null ? Collections.singletonList(displayName) : Collections.EMPTY_LIST);
    }


    /**
     * Get Array of issuesLinks
     *
     * @param issueLinkArray
     * @return List of FeatureIssueLink
     */
    protected static List<FeatureIssueLink> getIssueLinks(JSONArray issueLinkArray) {
        if (issueLinkArray == null) {
            return Collections.EMPTY_LIST;
        }
        List<FeatureIssueLink> jiraIssueLinks = new ArrayList<>();

        issueLinkArray.forEach(issueLink -> {
            JSONObject outward = (JSONObject) ((JSONObject) issueLink).get("outwardIssue");
            JSONObject inward = (JSONObject) ((JSONObject) issueLink).get("inwardIssue");
            JSONObject type = (JSONObject) ((JSONObject) issueLink).get("type");
            String targetKey = "";
            String direction = "";
            String name = "";
            String description = "";
            String targetUrl = "";

            if (outward != null) {
                targetKey = getString(outward, "key");
                direction = "OUTBOUND";
                name = getString(type, "name");
                description = getString(type, "outward");
                targetUrl = getString(outward, "self");

            } else if (inward != null) {
                targetKey = getString(inward, "key");
                direction = "INBOUND";
                name = getString(type, "name");
                description = getString(type, "inward");
                targetUrl = getString(inward, "self");
            }
            FeatureIssueLink jiraIssueLink = new FeatureIssueLink();
            // story number of the linked issue
            jiraIssueLink.setTargetIssueKey(targetKey);
            // name of the linked issue
            jiraIssueLink.setIssueLinkName(name);
            // type of the linked issue
            jiraIssueLink.setIssueLinkType(description);
            // direction of the linked issue (inbount/outbound)
            jiraIssueLink.setIssueLinkDirection(direction);
            // uri of the linked issue
            jiraIssueLink.setTargetIssueUri(targetUrl);
            jiraIssueLinks.add(jiraIssueLink);
        });
        return jiraIssueLinks;
    }


    /**
     * Get Epic using Jira API
     *
     * @param epicKey
     * @return epic
     */
    @Override
    public Epic getEpic(String epicKey, Map<String, Epic> epicMap) {
        try {
            String url = featureSettings.getJiraBaseUrl() + (featureSettings.getJiraBaseUrl().endsWith("/") ? "" : "/") + String.format(EPIC_REST_SUFFIX, epicKey);
            ResponseEntity<String> responseEntity = makeRestCall(url);
            String responseBody = responseEntity.getBody();
            JSONObject issue = (JSONObject) parser.parse(responseBody);

            if (issue == null) {
                return null;
            }

            return saveEpic(issue, epicMap, false);
        } catch (ParseException pe) {
            LOGGER.error("Parser exception when parsing teams", pe);
        } catch (HygieiaException e) {
            LOGGER.error("Error in calling JIRA API", e);
        }
        return null;
    }

    private Epic saveEpic(JSONObject issueJson, Map<String, Epic> epicMap, boolean recentUpdate) {
        Epic epic = new Epic();
        epic.setId(getString(issueJson, "id"));
        epic.setNumber(getString(issueJson, "key"));
        JSONObject fields = (JSONObject) issueJson.get("fields");
        epic.setName(getString(fields, "summary"));
        epic.setChangeDate(getString(fields, "updated"));
        epic.setBeginDate(getString(fields, "created"));
        epic.setEndDate(getString(fields, "resolutiondate"));
        JSONObject status = (JSONObject) fields.get("status");
        epic.setStatus(getString(status, "name"));
        epic.setRecentUpdate(recentUpdate);
        epic.setUrl(featureSettings.getJiraBaseUrl() + (featureSettings.getJiraBaseUrl().endsWith("/") ? "" : "/") + "browse/" + epic.getNumber());
        if (epicMap.containsKey(epic.getId())) {
            if (recentUpdate) {
                epicMap.put(epic.getId(), epic);
            }
        } else {
            epicMap.put(epic.getId(), epic);
        }
        return epic;
    }

    @Override
    public List<String> getAllIssueIds(String id, JiraMode mode) {
        int count = 0;
        int startAt = 0;
        boolean isLast = false;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");
        String updatedDate = LocalDateTime.now().minusDays(featureSettings.getFirstRunHistoryDays()).format(formatter);
        String issueTypes = featureSettings.getJiraIssueTypeNames() == null ? DEFAULT_ISSUE_TYPES : String.join(",", featureSettings.getJiraIssueTypeNames());
        List<String> result = new ArrayList<>();
        while (!isLast) {

            String url = featureSettings.getJiraBaseUrl() + (featureSettings.getJiraBaseUrl().endsWith("/") ? "" : "/") + ISSUE_BY_BOARD_FULL_REFRESH_REST_SUFFIX;
            url = String.format(url, mode.toString().toLowerCase(), id, issueTypes.replaceAll(",", "','"), updatedDate, startAt);
            try {
                ResponseEntity<String> responseEntity = makeRestCall(url);

                String responseBody = responseEntity.getBody();
                JSONObject jsonObject = (JSONObject) parser.parse(responseBody);

                if (jsonObject != null) {
                    JSONArray valuesArray = (JSONArray) jsonObject.get("issues");
                    if (!CollectionUtils.isEmpty(valuesArray)) {
                        for (Object obj : valuesArray) {
                            String sId = getString((JSONObject) obj, "id");
                            result.add(sId);
                            count = count + 1;
                        }
                        long total = getLong(jsonObject, "total");
                        long maxResults = getLong(jsonObject, "maxResults");
                        isLast = total == result.size();

                        startAt += maxResults;
                    } else {
                        isLast = true;
                    }

                } else {
                    isLast = true;
                }
            } catch (ParseException pe) {
                isLast = true;
                LOGGER.error("Parser exception when parsing issue refresh json", pe);
            } catch (HygieiaException | HttpClientErrorException | HttpServerErrorException e) {
                isLast = true;
                LOGGER.error("Error in calling JIRA API: " + url , e.getMessage());
            }
        }
        return result;
    }


    ////////////////// Helper Methods ////////////////////


    private ResponseEntity<String> makeRestCall(String url) throws HygieiaException {
        String jiraAccess = featureSettings.getJiraCredentials();
        if (StringUtils.isEmpty(jiraAccess)) {
            return restOperations.exchange(url, HttpMethod.GET, null, String.class);
        } else {
            String jiraAccessBase64 = new String(Base64.decodeBase64(jiraAccess));
            String[] parts = jiraAccessBase64.split(":");
            if (parts.length != 2) {
                throw new HygieiaException("Invalid Jira credentials", HygieiaException.INVALID_CONFIGURATION);
            }
            return restOperations.exchange(url, HttpMethod.GET, new HttpEntity<>(createHeaders(parts[0], parts[1])), String.class);
        }
    }

    private static HttpHeaders createHeaders(final String userId, final String password) {
        String auth = userId + ':' + password;
        byte[] encodedAuth = Base64.encodeBase64(auth.getBytes(StandardCharsets.US_ASCII));
        String authHeader = "Basic " + new String(encodedAuth);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", authHeader);
        return headers;
    }

    protected String getUpdatedSince(long lastCollected) {
        LocalDateTime since = LocalDateTime.now();
        if (lastCollected == 0) {
            since = since.minusDays(featureSettings.getFirstRunHistoryDays());
        } else {
            since = LocalDateTime.ofInstant(Instant.ofEpochMilli(lastCollected), ZoneId.systemDefault());
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");
        return since.format(formatter);
    }
}
