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

package com.capitalone.dashboard.util;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Bean to hold settings specific to the Feature collector.
 * 
 * @author KFK884
 */
@Component
@ConfigurationProperties(prefix = "feature")
public class FeatureSettings {
	private String cron;
	private int pageSize;
	private String deltaStartDate;
	private String deltaCollectorItemStartDate;
	private String masterStartDate;
	private String queryFolder;
	private String storyQuery;
	private String epicQuery;
	private String projectQuery;
	private String memberQuery;
	private String sprintQuery;
	private String teamQuery;
	private String trendingQuery;
	private int sprintDays;
	private int sprintEndPrior;
	private int scheduledPriorMin;
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
	 */
	private String jiraIssueTypeId;
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

	public String getCron() {
		return this.cron;
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

	public String getDeltaStartDate() {
		return this.deltaStartDate;
	}

	public void setDeltaStartDate(String deltaStartDate) {
		this.deltaStartDate = deltaStartDate;
	}

	public void setDeltaCollectorItemStartDate(String deltaCollectorItemStartDate) {
		this.deltaCollectorItemStartDate = deltaCollectorItemStartDate;
	}

	public String getDeltaCollectorItemStartDate() {
		return this.deltaCollectorItemStartDate;
	}

	public String getMasterStartDate() {
		return this.masterStartDate;
	}

	public void setMasterStartDate(String masterStartDate) {
		this.masterStartDate = masterStartDate;
	}

	public String getQueryFolder() {
		return this.queryFolder;
	}

	public void setQueryFolder(String queryFolder) {
		this.queryFolder = queryFolder;
	}

	public String getStoryQuery() {
		return this.storyQuery;
	}

	public void setStoryQuery(String storyQuery) {
		this.storyQuery = storyQuery;
	}

	public String getEpicQuery() {
		return this.epicQuery;
	}

	public void setEpicQuery(String epicQuery) {
		this.epicQuery = epicQuery;
	}

	public String getProjectQuery() {
		return this.projectQuery;
	}

	public void setProjectQuery(String projectQuery) {
		this.projectQuery = projectQuery;
	}

	public String getMemberQuery() {
		return this.memberQuery;
	}

	public void setMemberQuery(String memberQuery) {
		this.memberQuery = memberQuery;
	}

	public String getSprintQuery() {
		return this.sprintQuery;
	}

	public void setSprintQuery(String sprintQuery) {
		this.sprintQuery = sprintQuery;
	}

	public String getTeamQuery() {
		return this.teamQuery;
	}

	public void setTeamQuery(String teamQuery) {
		this.teamQuery = teamQuery;
	}

	public String getTrendingQuery() {
		return this.trendingQuery;
	}

	public void setTrendingQuery(String trendingQuery) {
		this.trendingQuery = trendingQuery;
	}

	public int getSprintDays() {
		return this.sprintDays;
	}

	public void setSprintDays(int sprintDays) {
		this.sprintDays = sprintDays;
	}

	public int getSprintEndPrior() {
		return this.sprintEndPrior;
	}

	public void setSprintEndPrior(int sprintEndPrior) {
		this.sprintEndPrior = sprintEndPrior;
	}

	public int getScheduledPriorMin() {
		return this.scheduledPriorMin;
	}

	public void setScheduledPriorMin(int scheduledPriorMin) {
		this.scheduledPriorMin = scheduledPriorMin;
	}

	public String getJiraBaseUrl() {
		return this.jiraBaseUrl;
	}

	public void setJiraBaseUrl(String jiraBaseUrl) {
		this.jiraBaseUrl = jiraBaseUrl;
	}
	
	public String getJiraQueryEndpoint() {
		return this.jiraQueryEndpoint;
	}

	public void setJiraQueryEndpoint(String jiraQueryEndpoint) {
		this.jiraQueryEndpoint = jiraQueryEndpoint;
	}

	public String getJiraCredentials() {
		return this.jiraCredentials;
	}

	public void setJiraCredentials(String jiraCredentials) {
		this.jiraCredentials = jiraCredentials;
	}

	public String getJiraOauthAuthtoken() {
		return this.jiraOauthAuthtoken;
	}

	public void setJiraOauthAuthtoken(String jiraOauthAuthtoken) {
		this.jiraOauthAuthtoken = jiraOauthAuthtoken;
	}

	public String getJiraOauthRefreshtoken() {
		return this.jiraOauthRefreshtoken;
	}

	public void setJiraOauthRefreshtoken(String jiraOauthRefreshtoken) {
		this.jiraOauthRefreshtoken = jiraOauthRefreshtoken;
	}

	public String getJiraOauthRedirecturi() {
		return this.jiraOauthRedirecturi;
	}

	public void setJiraOauthRedirecturi(String jiraOauthRedirecturi) {
		this.jiraOauthRedirecturi = jiraOauthRedirecturi;
	}

	public String getJiraOauthExpiretime() {
		return this.jiraOauthExpiretime;
	}

	public void setJiraOauthExpiretime(String jiraOauthExpiretime) {
		this.jiraOauthExpiretime = jiraOauthExpiretime;
	}

	public String getJiraProxyUrl() {
		return this.jiraProxyUrl;
	}

	public void setJiraProxyUrl(String jiraProxyUrl) {
		this.jiraProxyUrl = jiraProxyUrl;
	}

	public String getJiraProxyPort() {
		return this.jiraProxyPort;
	}

	public void setJiraProxyPort(String jiraProxyPort) {
		this.jiraProxyPort = jiraProxyPort;
	}
	
	public String getJiraIssueTypeId() {
		return jiraIssueTypeId;
	}

	public void setJiraIssueTypeId(String jiraIssueTypeId) {
		this.jiraIssueTypeId = jiraIssueTypeId;
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
}
