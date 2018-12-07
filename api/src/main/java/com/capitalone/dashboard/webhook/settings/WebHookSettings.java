package com.capitalone.dashboard.webhook.settings;

import org.springframework.boot.context.properties.ConfigurationPropertiesBinding;
import org.springframework.stereotype.Component;

@Component
@ConfigurationPropertiesBinding
public class WebHookSettings {
    GitHubWebHookSettings gitHub;

    JenkinsBuildWebHookSettings jenkinsBuild;

    public GitHubWebHookSettings getGitHub() { return gitHub; }

    public void setGitHub(GitHubWebHookSettings gitHub) { this.gitHub = gitHub; }

    public JenkinsBuildWebHookSettings getJenkinsBuild() { return jenkinsBuild; }

    public void setJenkinsBuild(JenkinsBuildWebHookSettings jenkinsBuild) { this.jenkinsBuild = jenkinsBuild; }
}
