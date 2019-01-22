package com.capitalone.dashboard.webhook.github;

import com.capitalone.dashboard.repository.CollectorItemRepository;
import com.capitalone.dashboard.settings.ApiSettings;
import com.capitalone.dashboard.client.RestClient;
import com.capitalone.dashboard.model.webhook.github.GitHubParsed;
import com.capitalone.dashboard.misc.HygieiaException;
import com.capitalone.dashboard.model.Collector;
import com.capitalone.dashboard.model.CollectorItem;
import com.capitalone.dashboard.model.Comment;
import com.capitalone.dashboard.model.Commit;
import com.capitalone.dashboard.model.CommitStatus;
import com.capitalone.dashboard.model.GitRequest;
import com.capitalone.dashboard.model.Review;
import com.capitalone.dashboard.repository.CommitRepository;
import com.capitalone.dashboard.repository.GitRequestRepository;
import com.capitalone.dashboard.service.CollectorService;
import com.capitalone.dashboard.util.Supplier;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bson.types.ObjectId;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.web.client.RestOperations;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class GitHubPullRequestV3Test {
    private static final Log LOG = LogFactory.getLog(GitHubPullRequestV3Test.class);

    @Mock
    private CollectorService collectorService;
    @Mock
    private GitRequestRepository gitRequestRepository;
    @Mock
    private CollectorItemRepository collectorItemRepository;
    @Mock
    private CommitRepository commitRepository;
    @Mock
    private ApiSettings apiSettings;
    @Mock
    private Supplier<RestOperations> restOperationsSupplier;

    private GitHubPullRequestV3 gitHubPullRequestV3;
    private RestClient restClient;
    private JSONObject payLoadJsonObject;

    @Before
    public void init() {
        restClient = new RestClient(restOperationsSupplier);
        gitHubPullRequestV3 = new GitHubPullRequestV3 (collectorService, restClient, gitRequestRepository, commitRepository, collectorItemRepository, apiSettings);
        payLoadJsonObject = makePullRequestPayloadObject();
    }

    @Test
    public void buildGraphQLQueryTest_ALL() {
        String repoUrl = "https://hostName/orgName/repoName";
        GitHubParsed gitHubParsed = null;
        try {
            gitHubParsed = new GitHubParsed(repoUrl);
        } catch (Exception e) {
            LOG.error(e);
        }
        Object repository = payLoadJsonObject.get("repository");
        Object pullRequest = restClient.getAsObject(repository,"pullRequest");

        StringBuilder queryBuilder = new StringBuilder("");
        queryBuilder.append(GraphQLQuery.PR_GRAPHQL_BEGIN_PRE);
        queryBuilder.append(GraphQLQuery.PR_GRAPHQL_COMMITS_BEGIN);
        queryBuilder.append(GraphQLQuery.PR_GRAPHQL_COMMENTS_BEGIN);
        queryBuilder.append(GraphQLQuery.PR_GRAPHQL_BEGIN_POST);
        queryBuilder.append(GraphQLQuery.PR_GRAPHQL_COMMITS);
        queryBuilder.append(GraphQLQuery.PR_GRAPHQL_COMMENTS);
        queryBuilder.append(GraphQLQuery.PR_GRAPHQL_REVIEWS);
        queryBuilder.append(GraphQLQuery.PR_GRAPHQL_END);

        JSONObject result = gitHubPullRequestV3.buildGraphQLQuery(gitHubParsed, pullRequest);

        String variablesJSONString = String.valueOf(result.get("variables"));

        JSONObject variableJSONObject = null;
        try {
            JSONParser parser = new JSONParser();
            variableJSONObject = (JSONObject) parser.parse(variablesJSONString);
        } catch (Exception e) {
            LOG.error(e);
        }

        Assert.assertEquals(queryBuilder.toString(), result.get("query"));
        Assert.assertEquals(gitHubParsed.getOrgName(), variableJSONObject.get("owner"));
        Assert.assertEquals(gitHubParsed.getRepoName(), variableJSONObject.get("name"));
        Assert.assertEquals(restClient.getString(pullRequest, "number"), String.valueOf(variableJSONObject.get("number")));
        Assert.assertEquals(restClient.getString(pullRequest, "commits"), String.valueOf(variableJSONObject.get("commits")));
        Assert.assertEquals(restClient.getString(pullRequest, "comments"), String.valueOf(variableJSONObject.get("comments")));
    }

    @Test
    public void buildGraphQLQueryTest_Commits_Only() {
        String repoUrl = "https://hostName/orgName/repoName";
        GitHubParsed gitHubParsed = null;
        try {
            gitHubParsed = new GitHubParsed(repoUrl);
        } catch (Exception e) {
            LOG.error(e);
        }
        Object repository = payLoadJsonObject.get("repository");
        JSONObject pullRequest = (JSONObject)restClient.getAsObject(repository,"pullRequest");
        pullRequest.put("comments", "0");

        StringBuilder queryBuilder = new StringBuilder("");
        queryBuilder.append(GraphQLQuery.PR_GRAPHQL_BEGIN_PRE);
        queryBuilder.append(GraphQLQuery.PR_GRAPHQL_COMMITS_BEGIN);
        queryBuilder.append(GraphQLQuery.PR_GRAPHQL_BEGIN_POST);
        queryBuilder.append(GraphQLQuery.PR_GRAPHQL_COMMITS);
        queryBuilder.append(GraphQLQuery.PR_GRAPHQL_REVIEWS);
        queryBuilder.append(GraphQLQuery.PR_GRAPHQL_END);

        JSONObject result = gitHubPullRequestV3.buildGraphQLQuery(gitHubParsed, pullRequest);

        String variablesJSONString = String.valueOf(result.get("variables"));

        JSONObject variableJSONObject = null;
        try {
            JSONParser parser = new JSONParser();
            variableJSONObject = (JSONObject) parser.parse(variablesJSONString);
        } catch (Exception e) {
            LOG.error(e);
        }

        Assert.assertEquals(queryBuilder.toString(), result.get("query"));
        Assert.assertEquals(gitHubParsed.getOrgName(), variableJSONObject.get("owner"));
        Assert.assertEquals(gitHubParsed.getRepoName(), variableJSONObject.get("name"));
        Assert.assertEquals(restClient.getString(pullRequest, "number"), String.valueOf(variableJSONObject.get("number")));
        Assert.assertEquals(restClient.getString(pullRequest, "commits"), String.valueOf(variableJSONObject.get("commits")));
        Assert.assertNull(variableJSONObject.get("comments"));
    }

    @Test
    public void buildGitRequestFromPayloadTest() {
        GitHubPullRequestV3 gitHubPullRequestV3 = Mockito.spy(this.gitHubPullRequestV3);

        String repoUrl = "https://hostName/orgName/repoName";
        String branch = "branch";

        Object repository = payLoadJsonObject.get("repository");
        Object pullRequest = restClient.getAsObject(repository,"pullRequest");

        Collector collector = gitHubPullRequestV3.getCollector();
        String collectorId = createGuid("0123456789abcdef");
        collector.setId(new ObjectId(collectorId));

        CollectorItem collectorItem = gitHubPullRequestV3.buildCollectorItem(new ObjectId(collectorId), repoUrl, branch);
        String collectorItemId = createGuid("0123456789abcdee");
        collectorItem.setId(new ObjectId(collectorItemId));

        when(collectorService.createCollector(anyObject())).thenReturn(collector);
        when(gitHubPullRequestV3.buildCollectorItem(anyObject(), anyString(), anyString())).thenReturn(collectorItem);
        when(collectorService.createCollectorItem(anyObject())).thenReturn(collectorItem);

        GitRequest pull = null;
        try {
            pull = gitHubPullRequestV3.buildGitRequestFromPayload(repoUrl, branch, pullRequest);
        } catch(Exception e) {
            LOG.error(e.getMessage());
        }

        Assert.assertEquals("3", pull.getNumber());
        Assert.assertEquals("AuthorLogin", pull.getUserId());
        Assert.assertEquals("https://hostName/orgName/repoName", pull.getScmUrl());
        Assert.assertEquals("branch", pull.getScmBranch());
        Assert.assertEquals("orgName", pull.getOrgName());
        Assert.assertEquals("repoName", pull.getRepoName());
        Assert.assertEquals("GithubWebhook Commit 2", pull.getScmCommitLog());
        Assert.assertEquals(1537476347000L, pull.getCreatedAt());
        Assert.assertEquals(1537480100000L, pull.getClosedAt());
        Assert.assertEquals(1537476500000L, pull.getMergedAt());
        Assert.assertEquals(1537476500000L, pull.getScmCommitTimestamp());
        Assert.assertEquals("merged", pull.getState());
        Assert.assertEquals("headRefOid", pull.getHeadSha());
        Assert.assertEquals("sourceRepo", pull.getSourceRepo());
        Assert.assertEquals("sourceBranch", pull.getSourceBranch());
        Assert.assertEquals("baseRefOid", pull.getBaseSha());
        Assert.assertEquals("branch", pull.getTargetBranch());
        Assert.assertEquals("orgName/repoName", pull.getTargetRepo());
        Assert.assertEquals(153000, pull.getResolutiontime());
        Assert.assertEquals("mergeCommitOid", pull.getScmRevisionNumber());
    }

    @Test
    public void updateGitRequestWithGraphQLDataTest() {
        GitHubPullRequestV3 gitHubPullRequestV3 = Mockito.spy(this.gitHubPullRequestV3);

        String repoUrl = "https://hostName/orgName/repoName";
        String branch = "branch";
        String token = "token";

        Object repoObject = restClient.getAsObject(payLoadJsonObject, "repository");
        JSONObject pullRequestObject = (JSONObject)restClient.getAsObject(repoObject, "pullRequest");

        JSONObject statusJsonObject = makeStatusObject();
        JSONObject commitsJsonObject = makeCommitsObject();
        commitsJsonObject.put("status", statusJsonObject);
        pullRequestObject.put("commits", commitsJsonObject);

        JSONObject commentsJsonObject = makeCommentsObject();
        pullRequestObject.put("comments", commentsJsonObject);

        JSONObject reviewsJsonObject = makeReviewsObject();
        pullRequestObject.put("reviews", reviewsJsonObject);

        GitRequest pull = new GitRequest();
        pull.setMergedAt(12345L);

        gitHubPullRequestV3.updateGitRequestWithGraphQLData(pull, repoUrl, branch, payLoadJsonObject, token);

        verify(gitHubPullRequestV3).getPRCommits(repoUrl, commitsJsonObject, pull, token);
        verify(gitHubPullRequestV3).getComments(repoUrl, commentsJsonObject, token);
        verify(gitHubPullRequestV3).getReviews(repoUrl, reviewsJsonObject, token);
    }

    @Test
    public void getPRCommitsTest() {
        GitHubPullRequestV3 gitHubPullRequestV3 = Mockito.spy(this.gitHubPullRequestV3);
        when(gitHubPullRequestV3.getLDAPDN(anyString(), anyString(), anyString())).thenReturn("authorLDAPDN");

        List<Commit> dbCommit = new ArrayList<>();
        dbCommit.add(new Commit());
        when(commitRepository.findAllByScmRevisionNumberAndScmAuthorIgnoreCaseAndScmCommitLogAndScmCommitTimestamp(anyString(), anyString(), anyString(), anyLong())).thenReturn(dbCommit);

        JSONObject statusJsonObject = makeStatusObject();
        JSONObject commitsJsonObject = makeCommitsObject();
        commitsJsonObject.put("status", statusJsonObject);
        GitRequest pull = new GitRequest();

        List<Commit> commitList = gitHubPullRequestV3.getPRCommits("repoUrl", commitsJsonObject, pull, "token");
        Assert.assertEquals(1, commitList.size());

        Commit commit = commitList.get(0);
        Assert.assertEquals("Author1",commit.getScmAuthor());
        Assert.assertEquals("Author1Login",commit.getScmAuthorLogin());
        Assert.assertEquals("authorLDAPDN", commit.getScmAuthorLDAPDN());
        Assert.assertEquals("Author1Oid", commit.getScmRevisionNumber());
        Assert.assertEquals("GithubWebhook Commit 2", commit.getScmCommitLog());
        verify(commitRepository).save(dbCommit.get(0));
    }

    @Test
    public void getCommentsTest() {
        GitHubPullRequestV3 gitHubPullRequestV3 = Mockito.spy(this.gitHubPullRequestV3);
        when(gitHubPullRequestV3.getLDAPDN(anyString(), anyString(), anyString())).thenReturn("authorLDAPDN");

        JSONObject commentsJsonObject = makeCommentsObject();

        List<Comment> commentsList = gitHubPullRequestV3.getComments("repoUrl", commentsJsonObject, "token");
        int size = commentsList.size();
        Assert.assertEquals(1, size);

        Comment comment = commentsList.get(0);
        Assert.assertEquals("AuthorLogin", comment.getUser());
        Assert.assertEquals("authorLDAPDN", comment.getUserLDAPDN());
        Assert.assertEquals(1537476454000L, comment.getCreatedAt());
        Assert.assertEquals(1537476454000L, comment.getUpdatedAt());
        Assert.assertEquals("Comments Text", comment.getBody());
    }

    @Test
    public void getReviewsTest() {
        GitHubPullRequestV3 gitHubPullRequestV3 = Mockito.spy(this.gitHubPullRequestV3);
        when(gitHubPullRequestV3.getLDAPDN(anyString(), anyString(), anyString())).thenReturn("authorLDAPDN");

        JSONObject reviewsJsonObject = makeReviewsObject();
        List<Review> reviewList = gitHubPullRequestV3.getReviews("repoUrl", reviewsJsonObject, "token");
        int size = reviewList.size();
        Assert.assertEquals(1, size);

        Review review = reviewList.get(0);
        Assert.assertEquals("Review Comment", review.getBody());
        Assert.assertEquals("APPROVED", review.getState());
        Assert.assertEquals("AuthorLogin", review.getAuthor());
        Assert.assertEquals("authorLDAPDN", review.getAuthorLDAPDN());
        Assert.assertEquals(1537476487000L, review.getCreatedAt());
        Assert.assertEquals(1537476487000L, review.getUpdatedAt());
    }

    @Test
    public void updateMatchingCommitsInDbTest() {
        Commit commit = new Commit();
        commit.setScmRevisionNumber("1");
        commit.setScmAuthor("scmAuthor");
        commit.setScmCommitLog("scmCommitLog");
        commit.setScmCommitTimestamp(1234L);

        GitRequest pull = new GitRequest();
        pull.setNumber("2");

        List<Commit> dbCommitList = new ArrayList<>();
        dbCommitList.add(new Commit());
        when(commitRepository.findAllByScmRevisionNumberAndScmAuthorIgnoreCaseAndScmCommitLogAndScmCommitTimestamp(anyString(), anyString(), anyString(), anyLong())).thenReturn(dbCommitList);

        gitHubPullRequestV3.updateMatchingCommitsInDb(commit, pull);

        Assert.assertEquals("2", dbCommitList.get(0).getPullNumber());
        verify(commitRepository).save(dbCommitList.get(0));
    }

    @Test
    public void getCommitStatusesTest() {
        JSONObject statusJsonObject = makeStatusObject();
        List<CommitStatus> commitStatusList = gitHubPullRequestV3.getCommitStatuses(statusJsonObject);
        int size = commitStatusList.size();
        Assert.assertEquals(2, size);
    }

    @Test
    public void setCollectorItemIdExistingPullRequestTest() throws MalformedURLException, HygieiaException {
        GitHubPullRequestV3 gitHubPullRequestV3 = Mockito.spy(this.gitHubPullRequestV3);

        GitRequest existingPullRequest = new GitRequest();
        String id = createGuid("0123456789abcdef");
        existingPullRequest.setId(new ObjectId(id));

        String collectorItemId = createGuid("0123456789abcdee");
        existingPullRequest.setCollectorItemId(new ObjectId(collectorItemId));

        CollectorItem collectorItem = new CollectorItem();
        collectorItem.setId(new ObjectId(collectorItemId));

        GitRequest newPullRequest = new GitRequest();

        when(gitRequestRepository.findByScmUrlIgnoreCaseAndScmBranchIgnoreCaseAndNumberAndRequestTypeIgnoreCase(anyString(), anyString(), anyString(), anyString())).thenReturn(existingPullRequest);
        when(collectorService.getCollectorItem(existingPullRequest.getCollectorItemId())).thenReturn(collectorItem);

        gitHubPullRequestV3.setCollectorItemId(newPullRequest);

        Assert.assertEquals(new ObjectId(id), newPullRequest.getId());
        Assert.assertEquals(new ObjectId(collectorItemId), newPullRequest.getCollectorItemId());
        Assert.assertTrue(collectorItem.isPushed());
    }

    @Test
    public void setCollectorItemIdNewCommitTest() throws MalformedURLException, HygieiaException {
        GitHubPullRequestV3 gitHubPullRequestV3 = Mockito.spy(this.gitHubPullRequestV3);

        GitRequest newPullRequest = new GitRequest();
        String repoUrl = "http://hostName/orgName/repoName";
        String branch = "master";
        newPullRequest.setScmUrl(repoUrl);
        newPullRequest.setScmBranch(branch);

        Collector collector = gitHubPullRequestV3.getCollector();
        String collectorId = createGuid("0123456789abcdef");
        collector.setId(new ObjectId(collectorId));

        CollectorItem collectorItem = gitHubPullRequestV3.buildCollectorItem(new ObjectId(collectorId), repoUrl, branch);
        String collectorItemId = createGuid("0123456789abcdee");
        collectorItem.setId(new ObjectId(collectorItemId));

        when(commitRepository.findAllByScmRevisionNumberAndScmUrlIgnoreCaseAndScmBranchIgnoreCaseOrderByTimestampAsc(anyString(), anyString(), anyString())).thenReturn(null);
        when(collectorService.createCollector(anyObject())).thenReturn(collector);
        when(gitHubPullRequestV3.buildCollectorItem(anyObject(), anyString(), anyString())).thenReturn(collectorItem);
        when(collectorService.createCollectorItem(anyObject())).thenReturn(collectorItem);
        try {
            when(gitHubPullRequestV3.getCollectorItem(anyString(), anyString())).thenReturn(collectorItem);
        } catch (HygieiaException e) {
            LOG.info(e.getMessage());
        }

        gitHubPullRequestV3.setCollectorItemId(newPullRequest);

        Assert.assertEquals(new ObjectId(collectorItemId), newPullRequest.getCollectorItemId());
    }

    private JSONObject makePullRequestPayloadObject() {
        JSONObject pullRequestPayloadObject = new JSONObject();

        JSONObject repository = new JSONObject();
        pullRequestPayloadObject.put("repository", repository);

        JSONObject pullRequest = new JSONObject();
        repository.put("pullRequest", pullRequest);

        pullRequest.put("number", "3");

        JSONObject user = new JSONObject();
        pullRequest.put("user", user);
        user.put("login", "AuthorLogin");

        pullRequest.put("title", "GithubWebhook Commit 2");

        pullRequest.put("created_at", "2018-09-20T20:45:47Z");
        pullRequest.put("update_at", "2018-09-20T20:48:20Z");
        pullRequest.put("closed_at", "2018-09-20T21:48:20Z");
        pullRequest.put("merged_at", "2018-09-20T20:48:20Z");

        pullRequest.put("state", "MERGED");

        JSONObject head = new JSONObject();
        pullRequest.put("head", head);
        head.put("sha", "headRefOid");
        head.put("ref", "sourceBranch");

        JSONObject repo = new JSONObject();
        head.put("repo", repo);
        repo.put("full_name", "sourceRepo");

        JSONObject base = new JSONObject();
        pullRequest.put("base", base);
        base.put("sha", "baseRefOid");

        pullRequest.put("commits", "2");
        pullRequest.put("comments", "2");
        pullRequest.put("merge_commit_sha", "mergeCommitOid");

        return pullRequestPayloadObject;
    }

    private JSONObject makeReviewsObject() {
        JSONObject reviewsJsonObject = new JSONObject();

        JSONArray nodes = new JSONArray();
        reviewsJsonObject.put("nodes", nodes);

        JSONObject node = new JSONObject();
        nodes.add(node);

        JSONObject author = new JSONObject();
        node.put("author", author);
        author.put("login", "AuthorLogin");

        node.put("createdAt", "2018-09-20T20:48:07Z");
        node.put("updatedAt", "2018-09-20T20:48:07Z");
        node.put("bodyText", "Review Comment");
        node.put("state", "APPROVED");

        return reviewsJsonObject;
    }

    private JSONObject makeCommentsObject() {
        JSONObject commentsJsonObject = new JSONObject();

        JSONArray nodes = new JSONArray();
        commentsJsonObject.put("nodes", nodes);

        JSONObject node = new JSONObject();
        nodes.add(node);

        node.put("createdAt", "2018-09-20T20:47:34Z");
        node.put("bodyText", "Comments Text");
        node.put("updatedAt", "2018-09-20T20:47:34Z");

        JSONObject author = new JSONObject();
        node.put("author", author);

        author.put("login", "AuthorLogin");

        return commentsJsonObject;
    }

    private JSONObject makeCommitsObject() {
        JSONObject commitsJsonObject = new JSONObject();
        JSONArray nodes = new JSONArray();

        JSONObject node = new JSONObject();
        JSONObject commit = new JSONObject();
        node.put("commit", commit);
        nodes.add(node);
        commitsJsonObject.put("nodes", nodes);

        commit.put("committedDate", "2018-09-20T20:43:22Z");
        commit.put("oid", "Author1Oid");
        commit.put("message", "GithubWebhook Commit 2");

        JSONObject statusJsonObject = makeStatusObject();
        commit.put("status", statusJsonObject);

        JSONObject author = new JSONObject();
        commit.put("author", author);
        author.put("date", "2018-09-20T15:43:22-05:00");
        author.put("name", "Author1");

        JSONObject userJson = new JSONObject();
        author.put("user", userJson);
        userJson.put("login", "Author1Login");

        return commitsJsonObject;
    }

    private JSONObject makeStatusObject() {
        JSONArray contextArray = new JSONArray();

        JSONObject context1 = new JSONObject();
        context1.put("context", "context1");
        context1.put("description", "context1 description");
        context1.put("state", "PEER_REVIEW_LGTM_SUCCESS");

        contextArray.add(context1);

        JSONObject context2 = new JSONObject();
        context2.put("context", "context2");
        context2.put("description", "context2 description");
        context2.put("state", "PEER_REVIEW_LGTM_PENDING");

        contextArray.add(context2);

        JSONObject statusObject = new JSONObject();
        statusObject.put("contexts", contextArray);

        return statusObject;
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