package com.capitalone.dashboard.collector;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Bean to hold settings specific to the UDeploy collector.
 */
@Component
@ConfigurationProperties(prefix = "github")
public class GitHubSettings {
	private String cron;
	private String host;
	private String key;
	@Value("${github.firstRunHistoryDays:14}")
	private int firstRunHistoryDays;
	private List<String> notBuiltCommits;
	@Value("${github.errorThreshold:2}")
	private int errorThreshold;
	@Value("${github.errorResetWindow:3600000}")
	private int errorResetWindow;
	@Value("${github.rateLimitThreshold:10}")
	private int rateLimitThreshold;
	// GitHub Enterprise does not have ratelimit
	// set to false to skip this check
	@Value("${github.checkRateLimit:true}")
	private boolean checkRateLimit;
	@Value("${github.commitPullSyncTime:86400000}") // 1 day in milliseconds
	private long commitPullSyncTime;
	@Value("${github.offsetMinutes:10}") // 10 mins default
	private int offsetMinutes;
	@Value("${github.fetchCount:100}")
	private int fetchCount;

	private String personalAccessToken;

	@Value("${github.connectTimeout:20000}")
	private int connectTimeout;

	@Value("${github.readTimeout:20000}")
	private int readTimeout;

	private String proxyUrl;

	private String proxyPort;

	private String proxyUser;

	private String proxyPassword;

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getCron() {
		return cron;
	}

	public void setCron(String cron) {
		this.cron = cron;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public int getFirstRunHistoryDays() {
		return firstRunHistoryDays;
	}

	public void setFirstRunHistoryDays(int firstRunHistoryDays) {
		this.firstRunHistoryDays = firstRunHistoryDays;
	}

	public List<String> getNotBuiltCommits() {
		return notBuiltCommits;
	}

	public void setNotBuiltCommits(List<String> notBuiltCommits) {
		this.notBuiltCommits = notBuiltCommits;
	}

	public int getErrorThreshold() {
		return errorThreshold;
	}

	public void setErrorThreshold(int errorThreshold) {
		this.errorThreshold = errorThreshold;
	}

	public int getRateLimitThreshold() {
		return rateLimitThreshold;
	}

	public void setRateLimitThreshold(int rateLimitThreshold) {
		this.rateLimitThreshold = rateLimitThreshold;
	}

	public boolean isCheckRateLimit() {
		return checkRateLimit;
	}

	public void setCheckRateLimit(boolean checkRateLimit) {
		this.checkRateLimit = checkRateLimit;
	}

	public String getPersonalAccessToken() {
		return personalAccessToken;
	}

	public void setPersonalAccessToken(String personalAccessToken) {
		this.personalAccessToken = personalAccessToken;
	}

	public int getErrorResetWindow() {
		return errorResetWindow;
	}

	public void setErrorResetWindow(int errorResetWindow) {
		this.errorResetWindow = errorResetWindow;
	}

	public long getCommitPullSyncTime() {
		return commitPullSyncTime;
	}

	public void setCommitPullSyncTime(long commitPullSyncTime) {
		this.commitPullSyncTime = commitPullSyncTime;
	}

	public int getOffsetMinutes() {
		return offsetMinutes;
	}

	public void setOffsetMinutes(int offsetMinutes) {
		this.offsetMinutes = offsetMinutes;
	}

	public int getFetchCount() {
		return fetchCount;
	}

	public void setFetchCount(int fetchCount) {
		this.fetchCount = fetchCount;
	}

	public int getReadTimeout() { return readTimeout; }

	public void setReadTimeout(int readTimeout) { this.readTimeout = readTimeout; }

	public int getConnectTimeout() { return connectTimeout; }

	public void setConnectTimeout(int connectTimeout) { this.connectTimeout = connectTimeout; }

	public String getProxyUrl() { return proxyUrl; }

	public void setProxyUrl(String proxyUrl) { this.proxyUrl = proxyUrl; }

	public String getProxyPort() { return proxyPort; }

	public void setProxyPort(String proxyPort) { this.proxyPort = proxyPort; }

	public String getProxyUser() { return proxyUser; }

	public void setProxyUser(String proxyUser) { this.proxyUser = proxyUser; }

	public String getProxyPassword() { return proxyPassword; }

	public void setProxyPassword(String proxyPassword) { this.proxyPassword = proxyPassword; }
}