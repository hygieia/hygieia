package com.capitalone.dashboard.webhook.settings;

import org.springframework.boot.context.properties.ConfigurationPropertiesBinding;
import org.springframework.stereotype.Component;

@Component
@ConfigurationPropertiesBinding
public class WebHookSettings {
    GitHubWebHookSettings gitHub;

    public GitHubWebHookSettings getGitHub() { return gitHub; }

    public void setGitHub(GitHubWebHookSettings gitHub) { this.gitHub = gitHub; }
}
