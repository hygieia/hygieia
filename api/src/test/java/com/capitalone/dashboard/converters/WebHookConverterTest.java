package com.capitalone.dashboard.converters;

import com.capitalone.dashboard.webhook.settings.GitHubWebHookSettings;
import com.capitalone.dashboard.webhook.settings.WebHookSettings;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class WebHookConverterTest {
    private WebHookConverter webHookConverter;

    @Before
    public void setup() {
        webHookConverter = new WebHookConverter();
    }

    @Test
    public void convertTest() {
        String jsonString = "{\"gitHub\" : {\"token\" : \"c74782b3ca2b57a5230ae7812a\", \"commitTimestampOffset\" : \"5\", \"userAgent\" : \"GitHub-Hookshot\", \"githubEnterpriseHosts\" : [\"github.com\"]}}";
        WebHookSettings webHookSettings = webHookConverter.convert(jsonString);
        Assert.assertNotNull(webHookSettings);

        GitHubWebHookSettings gitHubWebHookSettings = webHookSettings.getGitHubWebHookSettings();
        Assert.assertNotNull(gitHubWebHookSettings);

        Assert.assertEquals("c74782b3ca2b57a5230ae7812a", gitHubWebHookSettings.getToken());
        Assert.assertEquals(5, gitHubWebHookSettings.getCommitTimestampOffset());
        Assert.assertEquals("GitHub-Hookshot", gitHubWebHookSettings.getUserAgent());
        Assert.assertTrue(gitHubWebHookSettings.getGithubEnterpriseHosts().contains("github.com"));
    }
}
