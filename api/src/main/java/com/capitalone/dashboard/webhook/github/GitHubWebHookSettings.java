package com.capitalone.dashboard.webhook.github;

public class GitHubWebHookSettings {
    private String token;
    private int commitTimestampOffset;
    private String notBuiltCommits;
    private String delimiter;
    private String userAgent;
    private String githubEnterpriseHost;
    private String databaseUserAccount;

    public String getDatabaseUserAccount() { return databaseUserAccount; }
    public void setDatabaseUserAccount(String databaseUserAccount) { this.databaseUserAccount = databaseUserAccount; }

    public String getGithubEnterpriseHost() { return githubEnterpriseHost; }
    public void setGithubEnterpriseHost(String githubEnterpriseHost) { this.githubEnterpriseHost = githubEnterpriseHost; }

    public String getUserAgent() { return userAgent; }
    public void setUserAgent(String userAgent) { this.userAgent = userAgent; }

    public String getDelimiter() { return delimiter; }
    public void setDelimiter(String delimiter) { this.delimiter = delimiter; }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public int getCommitTimestampOffset() { return commitTimestampOffset; }
    public void setCommitTimestampOffset(int commitTimestampOffset) { this.commitTimestampOffset = commitTimestampOffset; }

    public String getNotBuiltCommits() { return notBuiltCommits; }
    public void setNotBuiltCommits(String notBuiltCommits) { this.notBuiltCommits = notBuiltCommits; }
}

