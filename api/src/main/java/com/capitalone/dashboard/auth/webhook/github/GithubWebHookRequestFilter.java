package com.capitalone.dashboard.auth.webhook.github;

import com.capitalone.dashboard.auth.AuthenticationResultHandler;
import com.capitalone.dashboard.settings.ApiSettings;
import com.capitalone.dashboard.webhook.settings.GitHubWebHookSettings;
import com.capitalone.dashboard.webhook.settings.WebHookSettings;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class GithubWebHookRequestFilter extends UsernamePasswordAuthenticationFilter {

    private final GithubWebHookAuthService githubWebHookAuthService;
    private final ApiSettings apiSettings;

    protected boolean authenticated = false; // This is to support the unit tests

    public GithubWebHookRequestFilter(String path, AuthenticationManager authManager,
                                      GithubWebHookAuthService githubWebHookAuthService,
                                      ApiSettings apiSettings,
                                      AuthenticationResultHandler authenticationResultHandler) {
        super();
        setAuthenticationManager(authManager);
        setAuthenticationSuccessHandler(authenticationResultHandler);
        setFilterProcessesUrl(path);
        this.githubWebHookAuthService = githubWebHookAuthService;
        this.apiSettings = apiSettings;
    }

    public void doFilter(ServletRequest req, ServletResponse res, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest)req;
        HttpServletResponse response = (HttpServletResponse)res;

        String userAgent = request.getHeader("User-Agent");
        String githubEnterpriseHost = request.getHeader("X-GitHub-Enterprise-Host");

        WebHookSettings webHookSettings = apiSettings.getWebHook();
        if (webHookSettings == null) { // Authentication Failure
            authenticated = false;
            filterChain.doFilter(request, response);
            return;
        }
        GitHubWebHookSettings gitHubWebHookSettings = webHookSettings.getGitHub();
        if (gitHubWebHookSettings == null) { // Authentication Failure
            authenticated = false;
            filterChain.doFilter(request, response);
            return;
        }

        String userAgentExpectedValue = gitHubWebHookSettings.getUserAgent();
        List<String> githubEnterpriseHostExpectedValues = gitHubWebHookSettings.getGithubEnterpriseHosts();

        if (checkForEmptyValues(userAgent, githubEnterpriseHost, userAgentExpectedValue, githubEnterpriseHostExpectedValues)
                || !userAgent.contains(userAgentExpectedValue)
                || !githubEnterpriseHostExpectedValues.contains(githubEnterpriseHost)) {
            authenticated = false;
            filterChain.doFilter(request, response);
        } else {
            authenticated = true;
            super.doFilter(req, res, filterChain);
        }
    }

    protected boolean checkForEmptyValues(String userAgent, String githubEnterpriseHost, String userAgentExpectedValue,
                                List<String> githubEnterpriseHostExpectedValues) {
        boolean result = false;

        if (checkForEmptyStringValues(userAgent, githubEnterpriseHost, userAgentExpectedValue)
                || CollectionUtils.isEmpty(githubEnterpriseHostExpectedValues)) {
            result = true;
        }

        return result;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws AuthenticationException {

        Authentication authentication = githubWebHookAuthService.getAuthentication(httpServletRequest);

        SecurityContextHolder.getContext().setAuthentication(authentication);

        return authentication;
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
                                            Authentication authResult) throws IOException, ServletException {
        SecurityContextHolder.getContext().setAuthentication(authResult);
        chain.doFilter(request, response);
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                              AuthenticationException failed) throws IOException, ServletException {

        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Github Webhook Authentication Failed");
    }

    private boolean checkForEmptyStringValues(String... values) {
        return Arrays.stream(values, 0, values.length).anyMatch(StringUtils::isEmpty);
    }
}