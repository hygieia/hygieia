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

package com.capitalone.dashboard.collector;

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
	private String versionOneProxyUrl;
	private String versionOneBaseUri;
	private String versionOneAccessToken;
	private int maxKanbanIterationLength;

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

	public String getDeltaStartDate() {
		return deltaStartDate;
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
		return masterStartDate;
	}

	public void setMasterStartDate(String masterStartDate) {
		this.masterStartDate = masterStartDate;
	}

	public String getQueryFolder() {
		return queryFolder;
	}

	public void setQueryFolder(String queryFolder) {
		this.queryFolder = queryFolder;
	}

	public String getStoryQuery() {
		return storyQuery;
	}

	public void setStoryQuery(String storyQuery) {
		this.storyQuery = storyQuery;
	}

	public String getEpicQuery() {
		return epicQuery;
	}

	public void setEpicQuery(String epicQuery) {
		this.epicQuery = epicQuery;
	}

	public String getProjectQuery() {
		return projectQuery;
	}

	public void setProjectQuery(String projectQuery) {
		this.projectQuery = projectQuery;
	}

	public String getMemberQuery() {
		return memberQuery;
	}

	public void setMemberQuery(String memberQuery) {
		this.memberQuery = memberQuery;
	}

	public String getSprintQuery() {
		return sprintQuery;
	}

	public void setSprintQuery(String sprintQuery) {
		this.sprintQuery = sprintQuery;
	}

	public String getTeamQuery() {
		return teamQuery;
	}

	public void setTeamQuery(String teamQuery) {
		this.teamQuery = teamQuery;
	}

	public String getTrendingQuery() {
		return trendingQuery;
	}

	public void setTrendingQuery(String trendingQuery) {
		this.trendingQuery = trendingQuery;
	}

	public int getSprintDays() {
		return sprintDays;
	}

	public void setSprintDays(int sprintDays) {
		this.sprintDays = sprintDays;
	}

	public int getSprintEndPrior() {
		return sprintEndPrior;
	}

	public void setSprintEndPrior(int sprintEndPrior) {
		this.sprintEndPrior = sprintEndPrior;
	}

	public int getScheduledPriorMin() {
		return scheduledPriorMin;
	}

	public void setScheduledPriorMin(int scheduledPriorMin) {
		this.scheduledPriorMin = scheduledPriorMin;
	}

	public String getVersionOneProxyUrl() {
		return this.versionOneProxyUrl;
	}

	public String getVersionOneBaseUri() {
		return this.versionOneBaseUri;
	}

	public String getVersionOneAccessToken() {
		return this.versionOneAccessToken;
	}

	public void setVersionOneProxyUrl(String versionOneProxyUrl) {
		this.versionOneProxyUrl = versionOneProxyUrl;
	}

	public void setVersionOneBaseUri(String versionOneBaseUri) {
		this.versionOneBaseUri = versionOneBaseUri;
	}

	public void setVersionOneAccessToken(String versionOneAccessToken) {
		this.versionOneAccessToken = versionOneAccessToken;
	}

	public int getMaxKanbanIterationLength() {
		return maxKanbanIterationLength;
	}

	public void setMaxKanbanIterationLength(int maxKanbanIterationLength) {
		this.maxKanbanIterationLength = maxKanbanIterationLength;
	}
}
