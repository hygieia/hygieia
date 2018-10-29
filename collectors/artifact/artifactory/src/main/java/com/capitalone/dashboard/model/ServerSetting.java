package com.capitalone.dashboard.model;

import java.util.List;

public class ServerSetting {
    String url;
    String username;
    String apiKey;
    List<RepoAndPattern> repoAndPatterns;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public List<RepoAndPattern> getRepoAndPatterns() {
        return repoAndPatterns;
    }

    public void setRepoAndPatterns(List<RepoAndPattern> repoAndPatterns) {
        this.repoAndPatterns = repoAndPatterns;
    }
}