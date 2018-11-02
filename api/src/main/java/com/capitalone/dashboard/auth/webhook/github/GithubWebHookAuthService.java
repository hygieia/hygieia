package com.capitalone.dashboard.auth.webhook.github;

import org.springframework.security.core.Authentication;
import javax.servlet.http.HttpServletRequest;

public interface GithubWebHookAuthService {
    Authentication getAuthentication(HttpServletRequest request);
}