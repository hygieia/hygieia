package com.capitalone.dashboard.auth.webhook.github;

import com.capitalone.dashboard.auth.AuthenticationResultHandler;
import com.capitalone.dashboard.model.UserInfo;
import com.capitalone.dashboard.repository.UserInfoRepository;
import com.capitalone.dashboard.settings.ApiSettings;
import com.capitalone.dashboard.webhook.github.GitHubWebHookSettings;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class GithubWebHookRequestFilterTest {
    @Mock
    private AuthenticationManager manager;

    @Mock
    private AuthenticationResultHandler resultHandler;

    @Mock
    private UserInfoRepository userInfoRepository;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @Mock
    private ApiSettings apiSettings;

    private GithubWebHookAuthService githubWebHookAuthService;
    private GithubWebHookRequestFilter filter;
    private String path;

    @Before
    public void setup() {
        path = "/commit/github/v3";
        githubWebHookAuthService = new GithubWebHookAuthServiceImpl();
        filter = new GithubWebHookRequestFilter(path, manager, githubWebHookAuthService, apiSettings, resultHandler);
    }

    @Test
    public void shouldCreateFilter() {
        assertNotNull(filter);
    }

    @Test
    public void doFilterTest_shouldAuthenticate () {
        String userAgentHeader = "GitHub-Hookshot/cc39a0c";
        String githubEnterpriseHostHeader = "github.com";
        String jsonString = "{\"token\" : \"c74782b3ca2b57a5230ae7812a\", \"commitTimestampOffset\" : \"5\", \"delimiter\" : \";\", \"userAgent\" : \"GitHub-Hookshot\", \"githubEnterpriseHost\" : \"github.com\"}";

        when(request.getMethod()).thenReturn("POST");
        when(request.getHeader("User-Agent")).thenReturn(userAgentHeader);
        when(request.getHeader("X-GitHub-Enterprise-Host")).thenReturn(githubEnterpriseHostHeader);
        when(apiSettings.getGitHubWebHook()).thenReturn(jsonString);

        try {
            filter.doFilter(request, response, filterChain);
        } catch(Exception e) {
            // This is expected, but the intent was to check if super.doFilter() is being invoked
        }

        Assert.assertTrue(filter.authenticated);
    }

    @Test
    public void doFilterTest_shouldNotAuthenticate () {
        String githubEnterpriseHostHeader = "github.com";
        String jsonString = "{\"token\" : \"c74782b3ca2b57a5230ae7812a\", \"commitTimestampOffset\" : \"5\", \"delimiter\" : \";\", \"userAgent\" : \"GitHub-Hookshot\", \"githubEnterpriseHost\" : \"github.com\"}";

        when(request.getMethod()).thenReturn("POST");
        when(request.getHeader("User-Agent")).thenReturn("something");
        when(request.getHeader("X-GitHub-Enterprise-Host")).thenReturn(githubEnterpriseHostHeader);
        when(apiSettings.getGitHubWebHook()).thenReturn(jsonString);

        try {
            filter.doFilter(request, response, filterChain);
        } catch(Exception e) {
            // This is expected, but the intent was to check if filterChain.doFilter() is being invoked
        }

        Assert.assertFalse(filter.authenticated);
    }

    @Test
    public void shouldAuthenticate() {
        String jsonString = "{\"token\" : \"c74782b3ca2b57a5230ae7812a\", \"commitTimestampOffset\" : \"5\", \"delimiter\" : \";\", \"userAgent\" : \"GitHub-Hookshot\", \"githubEnterpriseHost\" : \"github.com\", \"databaseUserAccount\" : \"gitHubWebHookUser\"}";
        UserInfo user = new UserInfo();
        when(userInfoRepository.findByUsername(anyString())).thenReturn(user);
        when(apiSettings.getGitHubWebHook()).thenReturn(jsonString);
        Authentication result = filter.attemptAuthentication(request, response);

        Assert.assertNotNull(result);
    }

    @Test
    public void parseAsGitHubWebHookTest() {
        String jsonString = "{\"token\" : \"c74782b3ca2b57a5230ae7812a\", \"commitTimestampOffset\" : \"5\", \"delimiter\" : \";\", \"userAgent\" : \"GitHub-Hookshot\", \"githubEnterpriseHost\" : \"github.com\"}";
        GitHubWebHookSettings gitHubWebHookSettings = filter.parseAsGitHubWebHook(jsonString);

        Assert.assertNotNull(gitHubWebHookSettings.getGithubEnterpriseHost());
        Assert.assertEquals("GitHub-Hookshot", gitHubWebHookSettings.getUserAgent());
    }

    @Test
    public void getGithubEnterpriseHostExpectedValues() {
        String jsonString = "{\"token\" : \"c74782b3ca2b57a5230ae7812a\", \"commitTimestampOffset\" : \"5\", \"delimiter\" : \";\", \"userAgent\" : \"GitHub-Hookshot\", \"githubEnterpriseHost\" : \"github.com\"}";
        GitHubWebHookSettings gitHubWebHookSettings = filter.parseAsGitHubWebHook(jsonString);

        List<String> result = filter.getGithubEnterpriseHostExpectedValues(gitHubWebHookSettings);
        Assert.assertEquals(1, result.size());
        Assert.assertEquals("github.com", result.get(0));
    }

    @Test
    public void checkGithubEnterpriseHost() {
        String jsonString = "{\"token\" : \"c74782b3ca2b57a5230ae7812a\", \"commitTimestampOffset\" : \"5\", \"delimiter\" : \";\", \"userAgent\" : \"GitHub-Hookshot\", \"githubEnterpriseHost\" : \"github.com\"}";
        GitHubWebHookSettings gitHubWebHookSettings = filter.parseAsGitHubWebHook(jsonString);

        List<String> githubHostExpectedValues = filter.getGithubEnterpriseHostExpectedValues(gitHubWebHookSettings);
        boolean result = filter.checkGithubEnterpriseHost("github.com", githubHostExpectedValues);
        Assert.assertTrue(result);

        result = filter.checkGithubEnterpriseHost("something", githubHostExpectedValues);
        Assert.assertFalse(result);
    }
}
