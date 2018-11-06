package com.capitalone.dashboard.webhook.settings;

import org.springframework.boot.context.properties.ConfigurationPropertiesBinding;
import org.springframework.stereotype.Component;

@Component
@ConfigurationPropertiesBinding
public class WebHookSettings {
    GitHubWebHookSettings gitHubWebHookSettings;

    public GitHubWebHookSettings getGitHubWebHookSettings() { return gitHubWebHookSettings; }

    public void setGitHubWebHookSettings(GitHubWebHookSettings gitHubWebHookSettings) {
        this.gitHubWebHookSettings = gitHubWebHookSettings;
    }
}
