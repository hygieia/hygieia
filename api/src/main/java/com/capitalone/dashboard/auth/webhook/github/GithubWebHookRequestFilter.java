package com.capitalone.dashboard.auth.webhook.github;

import com.capitalone.dashboard.auth.AuthenticationResultHandler;
import com.capitalone.dashboard.settings.ApiSettings;
import com.capitalone.dashboard.webhook.github.GitHubWebHookSettings;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class GithubWebHookRequestFilter extends UsernamePasswordAuthenticationFilter {
    private static final Log LOG = LogFactory.getLog(GithubWebHookRequestFilter.class);

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

        GitHubWebHookSettings gitHubWebHookSettings = parseAsGitHubWebHook(apiSettings.getGitHubWebHook());

        String userAgentExpectedValue = null;
        List<String> githubEnterpriseHostExpectedValues = new ArrayList<>();
        if (gitHubWebHookSettings != null) {
            userAgentExpectedValue = gitHubWebHookSettings.getUserAgent();
            githubEnterpriseHostExpectedValues = getGithubEnterpriseHostExpectedValues(gitHubWebHookSettings);
        }

        if (!apiSettings.isGithubWebhookEnabled()
                || StringUtils.isEmpty(userAgent)
                || StringUtils.isEmpty(githubEnterpriseHost)
                || StringUtils.isEmpty(userAgentExpectedValue)
                || CollectionUtils.isEmpty(githubEnterpriseHostExpectedValues)
                || !userAgent.contains(userAgentExpectedValue)
                || !checkGithubEnterpriseHost(githubEnterpriseHost, githubEnterpriseHostExpectedValues)) {
            authenticated = false;
            filterChain.doFilter(request, response);
        } else {
            authenticated = true;
            super.doFilter(req, res, filterChain);
        }
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws AuthenticationException {

        GitHubWebHookSettings gitHubWebHookSettings = parseAsGitHubWebHook(apiSettings.getGitHubWebHook());

        Authentication authentication = githubWebHookAuthService.getAuthentication(httpServletRequest, gitHubWebHookSettings);

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

    protected List<String> getGithubEnterpriseHostExpectedValues(GitHubWebHookSettings gitHubWebHookSettings) {
        List<String> githubEnterpriseHostExpectedValues = new ArrayList<>();

        if (gitHubWebHookSettings != null) {
            if (!StringUtils.isEmpty(gitHubWebHookSettings.getGithubEnterpriseHost())
                    && !StringUtils.isEmpty(gitHubWebHookSettings.getDelimiter())) {
                String[] hostValues = gitHubWebHookSettings.getGithubEnterpriseHost().split(gitHubWebHookSettings.getDelimiter());
                Arrays.stream(hostValues).forEach(githubEnterpriseHostExpectedValues::add);
            }
        }

        return githubEnterpriseHostExpectedValues;
    }

    protected boolean checkGithubEnterpriseHost(String githubEnterpriseHost, List<String> githubEnterpriseHostExpectedValues) {
        String value = Optional.ofNullable(githubEnterpriseHostExpectedValues)
                .orElseGet(Collections::emptyList).stream()
                .filter(expectedValue -> githubEnterpriseHost.contains(expectedValue))
                .findFirst().orElse(null);

        return !StringUtils.isEmpty(value);
    }

    protected GitHubWebHookSettings parseAsGitHubWebHook(String jsonString) {
        GitHubWebHookSettings gitHubWebHookSettings = null;

        if (org.apache.commons.lang.StringUtils.isEmpty(jsonString)) { return gitHubWebHookSettings; }

        try {
            gitHubWebHookSettings = new ObjectMapper().readValue(jsonString, GitHubWebHookSettings.class);
        } catch (IOException e) {
            LOG.info("Could not be converted into "+GitHubWebHookSettings.class.getSimpleName()+": "+jsonString);
        }
        return gitHubWebHookSettings;
    }
}