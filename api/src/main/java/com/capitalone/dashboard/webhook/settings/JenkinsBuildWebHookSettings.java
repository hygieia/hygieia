package com.capitalone.dashboard.webhook.settings;

import java.util.List;

public class JenkinsBuildWebHookSettings {

    private List<String> excludeCodeReposInBuild;

    public List<String> getExcludeCodeReposInBuild() {
        return excludeCodeReposInBuild;
    }

    public void setExcludeCodeReposInBuild(List<String> excludeCodeReposInBuild) {
        this.excludeCodeReposInBuild = excludeCodeReposInBuild;
    }
}
