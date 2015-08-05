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

	public void setDeltaCollectorItemStartDate(
			String deltaCollectorItemStartDate) {
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
}
