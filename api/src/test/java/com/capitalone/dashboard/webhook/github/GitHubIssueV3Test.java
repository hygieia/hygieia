package com.capitalone.dashboard.webhook.github;

import com.capitalone.dashboard.repository.CollectorItemRepository;
import com.capitalone.dashboard.settings.ApiSettings;
import com.capitalone.dashboard.client.RestClient;
import com.capitalone.dashboard.model.webhook.github.GitHubParsed;
import com.capitalone.dashboard.misc.HygieiaException;
import com.capitalone.dashboard.model.Collector;
import com.capitalone.dashboard.model.CollectorItem;
import com.capitalone.dashboard.model.GitRequest;
import com.capitalone.dashboard.repository.GitRequestRepository;
import com.capitalone.dashboard.service.CollectorService;
import com.capitalone.dashboard.util.Supplier;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bson.types.ObjectId;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.web.client.RestOperations;

import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class GitHubIssueV3Test {
    private static final Log LOG = LogFactory.getLog(GitHubIssueV3Test.class);

    @Mock
    private CollectorService collectorService;
    @Mock
    private GitRequestRepository gitRequestRepository;
    @Mock
    private CollectorItemRepository collectorItemRepository;
    @Mock
    private ApiSettings apiSettings;
    @Mock
    private Supplier<RestOperations> restOperationsSupplier;

    private GitHubIssueV3 gitHubIssueV3;

    @Before
    public void init() {
        RestClient restClient = new RestClient(restOperationsSupplier);
        gitHubIssueV3 = new GitHubIssueV3 (collectorService, restClient, gitRequestRepository, collectorItemRepository, apiSettings);
    }

    @Test
    public void saveCollectorItemIdExistingIssueTest() throws MalformedURLException, HygieiaException {
        GitHubIssueV3 gitHubIssueV3 = Mockito.spy(this.gitHubIssueV3);

        GitRequest existingIssue = new GitRequest();
        String id = createGuid("0123456789abcdef");
        existingIssue.setId(new ObjectId(id));

        String collectorItemId = createGuid("0123456789abcdee");
        existingIssue.setCollectorItemId(new ObjectId(collectorItemId));

        CollectorItem collectorItem = new CollectorItem();
        collectorItem.setId(new ObjectId(collectorItemId));

        GitRequest newIssue = new GitRequest();

        when(gitRequestRepository.findByScmUrlIgnoreCaseAndScmBranchIgnoreCaseAndNumberAndRequestTypeIgnoreCase(anyString(), anyString(), anyString(), anyString())).thenReturn(existingIssue);
        when(collectorService.getCollectorItem(existingIssue.getCollectorItemId())).thenReturn(collectorItem);

        gitHubIssueV3.setCollectorItemId(newIssue);

        Assert.assertEquals(new ObjectId(id), newIssue.getId());
        Assert.assertEquals(new ObjectId(collectorItemId), newIssue.getCollectorItemId());
        Assert.assertTrue(collectorItem.isPushed());
    }

    @Test
    public void saveCollectorItemIdNewIssueTest() throws MalformedURLException, HygieiaException {
        GitHubIssueV3 gitHubIssueV3 = Mockito.spy(this.gitHubIssueV3);

        GitRequest newIssue = new GitRequest();
        String repoUrl = "http://hostName/orgName/repoName";
        String branch = "master";
        newIssue.setScmUrl(repoUrl);
        newIssue.setScmBranch(branch);

        Collector collector = gitHubIssueV3.getCollector();
        String collectorId = createGuid("0123456789abcdef");
        collector.setId(new ObjectId(collectorId));

        CollectorItem collectorItem = gitHubIssueV3.buildCollectorItem(new ObjectId(collectorId), repoUrl, branch);
        String collectorItemId = createGuid("0123456789abcdee");
        collectorItem.setId(new ObjectId(collectorItemId));

        when(gitRequestRepository.findByScmUrlIgnoreCaseAndScmBranchIgnoreCaseAndNumberAndRequestTypeIgnoreCase(anyString(), anyString(), anyString(), anyString())).thenReturn(null);
        when(collectorService.createCollector(anyObject())).thenReturn(collector);
        when(gitHubIssueV3.buildCollectorItem(anyObject(), anyString(), anyString())).thenReturn(collectorItem);
        when(collectorService.createCollectorItem(anyObject())).thenReturn(collectorItem);
        try {
            when(gitHubIssueV3.getCollectorItem(anyString(), anyString())).thenReturn(collectorItem);
        } catch (HygieiaException e) {
            e.printStackTrace();
        }

        gitHubIssueV3.setCollectorItemId(newIssue);

        Assert.assertEquals(new ObjectId(collectorItemId), newIssue.getCollectorItemId());
    }

    @Test
    public void getIssueTest() throws MalformedURLException, HygieiaException {
        GitHubIssueV3 gitHubIssueV3 = Mockito.spy(this.gitHubIssueV3);

        GitHubParsed gitHubParsed = null;
        try {
            gitHubParsed = new GitHubParsed("http://hostName/orgName/repoName.git");
        } catch (Exception e) {
            LOG.error(e.getMessage());
        }

        String repoUrl = "http://hostName/orgName/repoName";
        String branch = "master";

        Collector collector = gitHubIssueV3.getCollector();
        String collectorId = createGuid("0123456789abcdef");
        collector.setId(new ObjectId(collectorId));

        CollectorItem collectorItem = gitHubIssueV3.buildCollectorItem(new ObjectId(collectorId), repoUrl, branch);
        String collectorItemId = createGuid("0123456789abcdee");
        collectorItem.setId(new ObjectId(collectorItemId));

        when(collectorService.createCollector(anyObject())).thenReturn(collector);
        when(gitHubIssueV3.buildCollectorItem(anyObject(), anyString(), anyString())).thenReturn(collectorItem);
        when(collectorService.createCollectorItem(anyObject())).thenReturn(collectorItem);

        GitRequest issue = gitHubIssueV3.getIssue(makeIssue(), gitHubParsed, "master");

        Assert.assertEquals("1", issue.getNumber());
        Assert.assertEquals("orgName", issue.getOrgName());
        Assert.assertEquals("repoName", issue.getRepoName());
        Assert.assertEquals("master", issue.getScmBranch());
        Assert.assertEquals("http://hostName/orgName/repoName", issue.getScmUrl());
        Assert.assertEquals("userLoginID", issue.getUserId());
        Assert.assertEquals("closed", issue.getState());
        Assert.assertEquals("issue", issue.getRequestType());
        Assert.assertEquals(1537546736000L, issue.getCreatedAt());
        Assert.assertEquals(1537633136000L, issue.getUpdatedAt());
        Assert.assertEquals(1537719536000L, issue.getScmCommitTimestamp());
    }

    private Map makeIssue() {
        Map issueMap = new HashMap<>();

        issueMap.put("number", "1");
        issueMap.put("title", "Issue Title");
        issueMap.put("created_at", "2018-09-21T11:18:56-05:00");
        issueMap.put("updated_at", "2018-09-22T11:18:56-05:00");
        issueMap.put("closed_at", "2018-09-23T11:18:56-05:00");
        issueMap.put("state", "closed");

        Map userMap = new HashMap<>();
        issueMap.put("user", userMap);

        userMap.put("login", "userLoginID");

        return issueMap;
    }

    private static String createGuid(String hex) {
        byte[]  bytes = new byte[12];
        new Random().nextBytes(bytes);

        char[] hexArray = hex.toCharArray();
        char[] hexChars = new char[bytes.length * 2];
        for ( int j = 0; j < bytes.length; j++ ) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }
}
