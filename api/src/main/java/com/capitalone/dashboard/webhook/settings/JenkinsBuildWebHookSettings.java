package com.capitalone.dashboard.webhook.settings;

import java.util.List;

public class JenkinsBuildWebHookSettings {

    private List<String> excludeCodeReposInBuild;

    private boolean enableFilterLibraryRepos;

    private int excludeLibraryRepoThreshold;

    public List<String> getExcludeCodeReposInBuild() {
        return excludeCodeReposInBuild;
    }

    public void setExcludeCodeReposInBuild(List<String> excludeCodeReposInBuild) {
        this.excludeCodeReposInBuild = excludeCodeReposInBuild;
    }

    public boolean isEnableFilterLibraryRepos() {
        return enableFilterLibraryRepos;
    }

    public void setEnableFilterLibraryRepos(boolean enableFilterLibraryRepos) {
        this.enableFilterLibraryRepos = enableFilterLibraryRepos;
    }

    public int getExcludeLibraryRepoThreshold() {
        return excludeLibraryRepoThreshold;
    }

    public void setExcludeLibraryRepoThreshold(int excludeLibraryRepoThreshold) {
        this.excludeLibraryRepoThreshold = excludeLibraryRepoThreshold;
    }
}
