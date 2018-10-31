package com.capitalone.dashboard.auth.webhook.github;

import com.capitalone.dashboard.webhook.github.GitHubWebHookSettings;
import org.springframework.security.core.Authentication;
import javax.servlet.http.HttpServletRequest;

public interface GithubWebHookAuthService {
    Authentication getAuthentication(HttpServletRequest request, GitHubWebHookSettings gitHubWebHookSettings);
}