package com.capitalone.dashboard.webhook.settings;

import java.util.List;

public class GitHubWebHookSettings {
    private String token;
    private int commitTimestampOffset;
    private List<String> notBuiltCommits;
    private String userAgent;
    private List<String> githubEnterpriseHosts;

    public List<String> getGithubEnterpriseHosts() { return githubEnterpriseHosts; }
    public void setGithubEnterpriseHosts(List<String> githubEnterpriseHosts) { this.githubEnterpriseHosts = githubEnterpriseHosts; }

    public String getUserAgent() { return userAgent; }
    public void setUserAgent(String userAgent) { this.userAgent = userAgent; }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public int getCommitTimestampOffset() { return commitTimestampOffset; }
    public void setCommitTimestampOffset(int commitTimestampOffset) { this.commitTimestampOffset = commitTimestampOffset; }

    public List<String> getNotBuiltCommits() { return notBuiltCommits; }
    public void setNotBuiltCommits(List<String> notBuiltCommits) { this.notBuiltCommits = notBuiltCommits; }
}

