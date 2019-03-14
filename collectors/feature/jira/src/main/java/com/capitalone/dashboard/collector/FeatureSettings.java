
package com.capitalone.dashboard.collector;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Bean to hold settings specific to the Feature collector.
 */
@Component
@ConfigurationProperties(prefix = "feature")
public class FeatureSettings {
	private String cron;
	private int pageSize;
	@Value("${feature.firstRunHistoryDays:30}")
	private int firstRunHistoryDays;
	// After this number of hours since the last run, we will refresh boards/teams and projects
	@Value("${feature.refreshTeamAndProjectHours:24}")
	private int refreshTeamAndProjectHours;
	// Jira-connection details
	private String jiraBaseUrl;
	private String jiraQueryEndpoint;
	private String jiraCredentials;
	private String jiraOauthAuthtoken;
	private String jiraOauthRefreshtoken;
	private String jiraOauthRedirecturi;
	private String jiraOauthExpiretime;
	private String jiraProxyUrl;
	private String jiraProxyPort;
	/**
	 * In Jira, general IssueType IDs are associated to various "issue"
	 * attributes. However, there is one attribute which this collector's
	 * queries rely on that change between different instantiations of Jira.
	 * Please provide a numerical ID reference to your instance's IssueType for
	 * the lowest level of Issues (e.g., "user story") specific to your Jira
	 * instance.
	 * <p>
	 * </p>
	 * <strong>Note:</strong> You can retrieve your instance's IssueType ID
	 * listings via the following URI:
	 * https://[your-jira-domain-name]/rest/api/2/issuetype/
	 * Multiple comma-separated values can be specified.
	 */
	private String[] jiraIssueTypeNames;
	/**
	 * In Jira, your instance will have its own custom field created for "sprint" or "timebox" details, which includes a list of information.  This field allows you to specify that data field for your instance of Jira.
	 * <p>
	 * </p>
	 * <strong>Note:</strong> You can retrieve your instance's sprint data field name
	 * via the following URI, and look for a package name <em>com.atlassian.greenhopper.service.sprint.Sprint</em>; your custom field name describes the values in this field:
	 * https://[your-jira-domain-name]/rest/api/2/issue/[some-issue-name]
	 */
	private String jiraSprintDataFieldName;
	/**
	 * In Jira, your instance will have its own custom field created for "super story" or "epic" back-end ID, which includes a list of information.  This field allows you to specify that data field for your instance of Jira.
	 * <p>
	 * </p>
     * <strong>Note:</strong> You can retrieve your instance's epic ID field name
	 * via the following URI where your queried user story issue has a super issue (e.g., epic) tied to it; your custom field name describes the epic value you expect to see, and is the only field that does this for a given issue:
	 *  https://[your-jira-domain-name]/rest/api/2/issue/[some-issue-name]
	 */

    private String jiraEpicIdFieldName;

    private String jiraStoryPointsFieldName;

	/**
	 * Its a custom field in JIRA, set it here
	 */
	private String jiraTeamFieldName;

	/**
	 * If you want to select boards in the Hygieia UI
	 */
	private boolean jiraBoardAsTeam;

	/**
	 * Defines the maximum number of features allow per board. If limit is reach collection will not happen for given board
	 */
	@Value("${feature.maxNumberOfFeaturesPerBoard:2000}")
	private int maxNumberOfFeaturesPerBoard;

	/**
	 *  Defines how to update features per board. If true then only update based on enabled collectorItems otherwise full update
	 */
	@Value("${feature.collectorItemOnlyUpdate:true}")
	private boolean collectorItemOnlyUpdate;
	/**
	 * In Jira, your instance will have its own Id for the Story
	 * <p>
	 * </p>
	 * <strong>Note:</strong> You can retrieve your instance's Story ID
	 * via the following URI
	 *  https://[your-jira-domain-name]/rest/api/2/issuetype
	 */
	private String jiraStoryId;
	/**
	 * In Jira, your instance will have its own Id for the Epic
	 * <p>
	 * </p>
	 * <strong>Note:</strong> You can retrieve your instance's Epic ID
	 * via the following URI
	 *  https://[your-jira-domain-name]/rest/api/2/issuetype
	 */
	private String jiraEpicId;

	public boolean isCollectorItemOnlyUpdate() {
		return collectorItemOnlyUpdate;
	}

	public void setCollectorItemOnlyUpdate(boolean collectorItemOnlyUpdate) {
		this.collectorItemOnlyUpdate = collectorItemOnlyUpdate;
	}

	public int getMaxNumberOfFeaturesPerBoard() {
		return maxNumberOfFeaturesPerBoard;
	}

	public void setMaxNumberOfFeaturesPerBoard(int maxNumberOfFeaturesPerBoard) {
		this.maxNumberOfFeaturesPerBoard = maxNumberOfFeaturesPerBoard;
	}

	public String getCron() {
		return cron;
	}

	public void setCron(String cron) {
		this.cron = cron;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public int getFirstRunHistoryDays() {
		return firstRunHistoryDays;
	}

	public void setFirstRunHistoryDays(int firstRunHistoryDays) {
		this.firstRunHistoryDays = firstRunHistoryDays;
	}

	public String getJiraBaseUrl() {
		return jiraBaseUrl;
	}

	public void setJiraBaseUrl(String jiraBaseUrl) {
		this.jiraBaseUrl = jiraBaseUrl;
	}

	public String getJiraQueryEndpoint() {
		return jiraQueryEndpoint;
	}

	public void setJiraQueryEndpoint(String jiraQueryEndpoint) {
		this.jiraQueryEndpoint = jiraQueryEndpoint;
	}

	public String getJiraCredentials() {
		return jiraCredentials;
	}

	public void setJiraCredentials(String jiraCredentials) {
		this.jiraCredentials = jiraCredentials;
	}

	public String getJiraOauthAuthtoken() {
		return jiraOauthAuthtoken;
	}

	public void setJiraOauthAuthtoken(String jiraOauthAuthtoken) {
		this.jiraOauthAuthtoken = jiraOauthAuthtoken;
	}

	public String getJiraOauthRefreshtoken() {
		return jiraOauthRefreshtoken;
	}

	public void setJiraOauthRefreshtoken(String jiraOauthRefreshtoken) {
		this.jiraOauthRefreshtoken = jiraOauthRefreshtoken;
	}

	public String getJiraOauthRedirecturi() {
		return jiraOauthRedirecturi;
	}

	public void setJiraOauthRedirecturi(String jiraOauthRedirecturi) {
		this.jiraOauthRedirecturi = jiraOauthRedirecturi;
	}

	public String getJiraOauthExpiretime() {
		return jiraOauthExpiretime;
	}

	public void setJiraOauthExpiretime(String jiraOauthExpiretime) {
		this.jiraOauthExpiretime = jiraOauthExpiretime;
	}

	public String getJiraProxyUrl() {
		return jiraProxyUrl;
	}

	public void setJiraProxyUrl(String jiraProxyUrl) {
		this.jiraProxyUrl = jiraProxyUrl;
	}

	public String getJiraProxyPort() {
		return jiraProxyPort;
	}

	public void setJiraProxyPort(String jiraProxyPort) {
		this.jiraProxyPort = jiraProxyPort;
	}

	public String[] getJiraIssueTypeNames() {
		return jiraIssueTypeNames;
	}

	public void setJiraIssueTypeNames(String[] jiraIssueTypeNames) {
		this.jiraIssueTypeNames = jiraIssueTypeNames;
	}

	public String getJiraSprintDataFieldName() {
		return jiraSprintDataFieldName;
	}

	public void setJiraSprintDataFieldName(String jiraSprintDataFieldName) {
		this.jiraSprintDataFieldName = jiraSprintDataFieldName;
	}

    public String getJiraEpicIdFieldName() {
        return jiraEpicIdFieldName;
    }

    public void setJiraEpicIdFieldName(String jiraEpicIdFieldName) {
        this.jiraEpicIdFieldName = jiraEpicIdFieldName;
    }

    public String getJiraStoryPointsFieldName() {
        return jiraStoryPointsFieldName;
    }

    public void setJiraStoryPointsFieldName(String jiraStoryPointsFieldName) {
        this.jiraStoryPointsFieldName = jiraStoryPointsFieldName;
    }

    public String getJiraTeamFieldName() {
        return jiraTeamFieldName;
    }

    public void setJiraTeamFieldName(String jiraTeamFieldName) {
        this.jiraTeamFieldName = jiraTeamFieldName;
    }

    public int getRefreshTeamAndProjectHours() {
        return refreshTeamAndProjectHours;
    }

    public void setRefreshTeamAndProjectHours(int refreshTeamAndProjectHours) {
        this.refreshTeamAndProjectHours = refreshTeamAndProjectHours;
    }

	public boolean isJiraBoardAsTeam() {
		return jiraBoardAsTeam;
	}

	public void setJiraBoardAsTeam(boolean jiraBoardAsTeam) {
		this.jiraBoardAsTeam = jiraBoardAsTeam;
	}

	public String getJiraStoryId() {
		return jiraStoryId;
	}

	public void setJiraStoryId(String jiraStoryId) {
		this.jiraStoryId = jiraStoryId;
	}

	public String getJiraEpicId() {
		return jiraEpicId;
	}

	public void setJiraEpicId(String jiraEpicId) {
		this.jiraEpicId = jiraEpicId;
	}
}
