package com.capitalone.dashboard.collector;

import com.capitalone.dashboard.misc.HygieiaException;
import com.capitalone.dashboard.model.CollectionMode;
import com.capitalone.dashboard.model.Comment;
import com.capitalone.dashboard.model.Commit;
import com.capitalone.dashboard.model.CommitStatus;
import com.capitalone.dashboard.model.CommitType;
import com.capitalone.dashboard.model.GitHubGraphQLQuery;
import com.capitalone.dashboard.model.GitHubPaging;
import com.capitalone.dashboard.model.GitHubParsed;
import com.capitalone.dashboard.model.GitHubRateLimit;
import com.capitalone.dashboard.model.GitHubRepo;
import com.capitalone.dashboard.model.GitRequest;
import com.capitalone.dashboard.model.MergeEvent;
import com.capitalone.dashboard.model.Review;
import com.capitalone.dashboard.util.Encryption;
import com.capitalone.dashboard.util.EncryptionException;
import com.capitalone.dashboard.util.Supplier;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.DateTime;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestOperations;

import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * GitHubClient implementation that uses SVNKit to fetch information about
 * Subversion repositories.
 */
@Component
@SuppressWarnings("PMD.ExcessiveClassLength")
public class DefaultGitHubClient implements GitHubClient {
    private static final Log LOG = LogFactory.getLog(DefaultGitHubClient.class);

    private final GitHubSettings settings;

    private final RestOperations restOperations;

    private List<Commit> commits;
    private List<GitRequest> pullRequests;
    private List<GitRequest> issues;
    private Map<String, String> ldapMap;
    private final List<Pattern> commitExclusionPatterns = new ArrayList<>();


    private static final int FIRST_RUN_HISTORY_DEFAULT = 14;

    public class RedirectedStatus {
        private boolean isRedirected = false;
        private String redirectedUrl = null;

        RedirectedStatus() {}

        RedirectedStatus(boolean isRedirected, String redirectedUrl) {
            this.isRedirected = isRedirected;
            this.redirectedUrl = redirectedUrl;
        }

        String getRedirectedUrl() {
            return this.redirectedUrl;
        }

        boolean isRedirected() {
            return this.isRedirected;
        }
    }

    @Autowired
    public DefaultGitHubClient(GitHubSettings settings,
                               Supplier<RestOperations> restOperationsSupplier) {
        this.settings = settings;
        this.restOperations = restOperationsSupplier.get();

        if (!CollectionUtils.isEmpty(settings.getNotBuiltCommits())) {
            settings.getNotBuiltCommits().stream().map(regExStr -> Pattern.compile(regExStr, Pattern.CASE_INSENSITIVE)).forEach(commitExclusionPatterns::add);
        }
    }

    private int getFetchCount(boolean firstRun) {
        if (firstRun) return 100;
        return settings.getFetchCount();
    }

    @Override
    public List<Commit> getCommits() {
        return commits;
    }

    @Override
    public List<GitRequest> getPulls() {
        return pullRequests;
    }

    @Override
    public List<GitRequest> getIssues() {
        return issues;
    }


    protected void setLdapMap(Map<String, String> ldapMap) {
        this.ldapMap = ldapMap;
    }

    protected Map<String, String> getLdapMap() {
        return ldapMap;
    }

    @Override
    @SuppressWarnings("PMD.ExcessiveMethodLength")
    public void fireGraphQL(GitHubRepo repo, boolean firstRun, Map<Long, String> existingPRMap, Map<Long, String> existingIssueMap) throws RestClientException, MalformedURLException, HygieiaException {
        // format URL
        String repoUrl = (String) repo.getOptions().get("url");
        GitHubParsed gitHubParsed = new GitHubParsed(repoUrl);


        commits = new LinkedList<>();
        pullRequests = new LinkedList<>();
        issues = new LinkedList<>();
        ldapMap = new HashMap<>();
        long historyTimeStamp = getTimeStampMills(getRunDate(repo, firstRun, false));

        String decryptedPassword = decryptString(repo.getPassword(), settings.getKey());
        String personalAccessToken = (String) repo.getOptions().get("personalAccessToken");
        String decryptPersonalAccessToken = decryptString(personalAccessToken, settings.getKey());
        boolean alldone = false;

        GitHubPaging dummyPRPaging = isThereNewPRorIssue(gitHubParsed, repo, decryptedPassword, decryptPersonalAccessToken, existingPRMap, "pull", firstRun);
        GitHubPaging dummyIssuePaging = isThereNewPRorIssue(gitHubParsed, repo, decryptedPassword, decryptPersonalAccessToken, existingIssueMap, "issue", firstRun);
        GitHubPaging dummyCommitPaging = new GitHubPaging();
        dummyCommitPaging.setLastPage(false);

        JSONObject query = buildQuery(true, firstRun, false, gitHubParsed, repo, dummyCommitPaging, dummyPRPaging, dummyIssuePaging);
        int loopCount = 1;
        while (!alldone) {
            LOG.debug("Executing loop " + loopCount + " for " + gitHubParsed.getOrgName() + "/" + gitHubParsed.getRepoName());
            JSONObject data = getDataFromRestCallPost(gitHubParsed, repo, decryptedPassword, decryptPersonalAccessToken, query);

            if (data != null) {
                JSONObject repository = (JSONObject) data.get("repository");

                GitHubPaging pullPaging = processPullRequest((JSONObject) repository.get("pullRequests"), repo, existingPRMap, historyTimeStamp);
                LOG.debug("--- Processed " + pullPaging.getCurrentCount() + " of total " + pullPaging.getTotalCount() + " pull requests");

                GitHubPaging issuePaging = processIssues((JSONObject) repository.get("issues"), gitHubParsed, existingIssueMap, historyTimeStamp);
                LOG.debug("--- Processed " + issuePaging.getCurrentCount() + " of total " + issuePaging.getTotalCount() + " issues");

                GitHubPaging commitPaging = processCommits((JSONObject) repository.get("ref"), repo);
                LOG.debug("--- Processed " + commitPaging.getCurrentCount() + " commits");

                alldone = pullPaging.isLastPage() && commitPaging.isLastPage() && issuePaging.isLastPage();

                query = buildQuery(false, firstRun, false, gitHubParsed, repo, commitPaging, pullPaging, issuePaging);

                loopCount++;
            }
        }

        LOG.info("-- Collected " + commits.size() + " Commits, " + pullRequests.size() + " Pull Requests, " + issues.size() + " Issues since " + getRunDate(repo, firstRun, false));

        if (firstRun) {
            connectCommitToPulls();
            return;
        }

        List<GitRequest> allMergedPrs = pullRequests.stream().filter(pr -> "merged".equalsIgnoreCase(pr.getState())).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(allMergedPrs)) {
            connectCommitToPulls();
            return;
        }

        //find missing commits for subsequent runs
        alldone = false;

        dummyPRPaging = new GitHubPaging();
        dummyPRPaging.setLastPage(true);
        dummyIssuePaging = new GitHubPaging();
        dummyIssuePaging.setLastPage(true);
        dummyCommitPaging = new GitHubPaging();
        dummyCommitPaging.setLastPage(false);

        query = buildQuery(true, firstRun, true, gitHubParsed, repo, dummyCommitPaging, dummyPRPaging, dummyIssuePaging);

        loopCount = 1;
        int missingCommitCount = 0;
        while (!alldone) {
            JSONObject data = getDataFromRestCallPost(gitHubParsed, repo, decryptedPassword, decryptPersonalAccessToken, query);
            if (data != null) {
                JSONObject repository = (JSONObject) data.get("repository");

                GitHubPaging commitPaging = processCommits((JSONObject) repository.get("ref"), repo);
                LOG.debug("--- Processed " + commitPaging.getCurrentCount() + " commits");

                alldone = commitPaging.isLastPage();
                missingCommitCount += commitPaging.getCurrentCount();

                query = buildQuery(false, firstRun, true, gitHubParsed, repo, commitPaging, dummyPRPaging, dummyIssuePaging);

                loopCount++;
            }
        }
        LOG.info("-- Collected " + missingCommitCount + " Missing Commits, since " + getRunDate(repo, firstRun, true));

        connectCommitToPulls();
    }

    public RedirectedStatus checkForRedirectedRepo(GitHubRepo repo) throws MalformedURLException, HygieiaException {
        GitHubParsed gitHubParsed = new GitHubParsed(repo.getRepoUrl());
        String query = gitHubParsed.getBaseApiUrl() + "repos/" + gitHubParsed.getOrgName() + "/" + gitHubParsed.getRepoName();

        ResponseEntity<String> response = makeRestCallGet(query);

        JSONObject queryJSONBody = (JSONObject) parseAsObject(response);
        String repoUrl = str(queryJSONBody, "html_url");
        if (!repoUrl.equals(repo.getRepoUrl())) {
            LOG.info("original url: " + repo.getRepoUrl() + " is redirected to new url: " + repoUrl);
            return new RedirectedStatus(true, repoUrl);
        }
        return new RedirectedStatus();
    }

    @SuppressWarnings("PMD.NPathComplexity")
    private GitHubPaging isThereNewPRorIssue(GitHubParsed gitHubParsed, GitHubRepo repo, String decryptedPassword, String personalAccessToken, Map<Long, String> existingMap, String type, boolean firstRun) throws MalformedURLException, HygieiaException {

        GitHubPaging paging = new GitHubPaging();
        paging.setLastPage(true);

        if (firstRun) {
            paging.setLastPage(false);
            return paging;
        }

        String queryString = "pull".equalsIgnoreCase(type) ? GitHubGraphQLQuery.QUERY_NEW_PR_CHECK : GitHubGraphQLQuery.QUERY_NEW_ISSUE_CHECK;
        JSONObject query = new JSONObject();
        JSONObject variableJSON = new JSONObject();
        variableJSON.put("owner", gitHubParsed.getOrgName());
        variableJSON.put("name", gitHubParsed.getRepoName());
        if ("pull".equalsIgnoreCase(type)) {
            variableJSON.put("branch", repo.getBranch());
        }
        query.put("query", queryString);
        query.put("variables", variableJSON.toString());

        JSONObject data = getDataFromRestCallPost(gitHubParsed, repo, decryptedPassword, personalAccessToken, query);

        if (data == null) return paging;
        JSONObject repository = (JSONObject) data.get("repository");
        JSONObject requestObject = "pull".equalsIgnoreCase(type) ? (JSONObject) repository.get("pullRequests") : (JSONObject) repository.get("issues");
        if (requestObject == null) return paging;

        JSONArray edges = getArray(requestObject, "edges");
        if (CollectionUtils.isEmpty(edges)) return paging;

        int index = 0;
        for (Object o : edges) {
            JSONObject node = (JSONObject) ((JSONObject) o).get("node");
            if (node == null) return paging;
            String updated = str(node, "updatedAt");
            long updatedTimestamp = getTimeStampMills(updated);
            String number = str(node, "number");
            boolean stop =
                    ((!MapUtils.isEmpty(existingMap) && existingMap.get(updatedTimestamp) != null) && (Objects.equals(existingMap.get(updatedTimestamp), number)));
            if (stop) {
                break;
            }
            index++;
        }
        paging.setLastPage(index == 0);
        return paging;
    }

    /**
     * Normal merge: Match PR's commit sha's with commit list
     * Squash merge: Match PR's merge sha's with commit list
     * Rebase merge: Match PR's commit's "message"+"author name"+"date" with commit list
     * <p>
     * If match found, set the commit's PR number and possibly set the PR merge type
     * <p>
     * For setting type:
     * If PR commit's SHAs are all found in commit stream, then the commit for the merge sha is a merge commit.
     * In all other cases it is a new commit
     */

    private void connectCommitToPulls() {
        commits = CommitPullMatcher.matchCommitToPulls(commits, pullRequests);
    }

    @SuppressWarnings({"PMD.ExcessiveMethodLength", "PMD.NcssMethodCount"})
    private JSONObject buildQuery(boolean firstTime, boolean firstRun, boolean missingCommits, GitHubParsed gitHubParsed, GitHubRepo repo, GitHubPaging commitPaging, GitHubPaging pullPaging, GitHubPaging issuePaging) {
        CollectionMode mode = getCollectionMode(firstTime, commitPaging, pullPaging, issuePaging);
        JSONObject jsonObj = new JSONObject();
        String query;
        JSONObject variableJSON = new JSONObject();
        variableJSON.put("owner", gitHubParsed.getOrgName());
        variableJSON.put("name", gitHubParsed.getRepoName());
        variableJSON.put("fetchCount", getFetchCount(firstRun));

        LOG.debug("Collection Mode =" + mode.toString());
        switch (mode) {
            case FirstTimeAll:
                query = GitHubGraphQLQuery.QUERY_BASE_ALL_FIRST + GitHubGraphQLQuery.QUERY_PULL_HEADER_FIRST + GitHubGraphQLQuery.QUERY_PULL_MAIN + GitHubGraphQLQuery.QUERY_COMMIT_HEADER_FIRST + GitHubGraphQLQuery.QUERY_COMMIT_MAIN + GitHubGraphQLQuery.QUERY_ISSUES_HEADER_FIRST + GitHubGraphQLQuery.QUERY_ISSUE_MAIN + GitHubGraphQLQuery.QUERY_END;
                variableJSON.put("since", getRunDate(repo, firstRun, missingCommits));
                variableJSON.put("branch", repo.getBranch());
                jsonObj.put("query", query);
                jsonObj.put("variables", variableJSON.toString());
                break;


            case FirstTimeCommitOnly:
                query = GitHubGraphQLQuery.QUERY_BASE_ALL_FIRST + GitHubGraphQLQuery.QUERY_COMMIT_HEADER_FIRST + GitHubGraphQLQuery.QUERY_COMMIT_MAIN + GitHubGraphQLQuery.QUERY_END;
                variableJSON.put("since", getRunDate(repo, firstRun, missingCommits));
                variableJSON.put("branch", repo.getBranch());
                jsonObj.put("query", query);
                jsonObj.put("variables", variableJSON.toString());
                break;

            case FirstTimeCommitAndIssue:
                query = GitHubGraphQLQuery.QUERY_BASE_ALL_FIRST + GitHubGraphQLQuery.QUERY_COMMIT_HEADER_FIRST + GitHubGraphQLQuery.QUERY_COMMIT_MAIN + GitHubGraphQLQuery.QUERY_ISSUES_HEADER_FIRST + GitHubGraphQLQuery.QUERY_ISSUE_MAIN + GitHubGraphQLQuery.QUERY_END;
                variableJSON.put("since", getRunDate(repo, firstRun, missingCommits));
                variableJSON.put("branch", repo.getBranch());
                jsonObj.put("query", query);
                jsonObj.put("variables", variableJSON.toString());
                break;

            case FirstTimeCommitAndPull:
                query = GitHubGraphQLQuery.QUERY_BASE_ALL_FIRST + GitHubGraphQLQuery.QUERY_PULL_HEADER_FIRST + GitHubGraphQLQuery.QUERY_PULL_MAIN + GitHubGraphQLQuery.QUERY_COMMIT_HEADER_FIRST + GitHubGraphQLQuery.QUERY_COMMIT_MAIN + GitHubGraphQLQuery.QUERY_END;
                variableJSON.put("since", getRunDate(repo, firstRun, missingCommits));
                variableJSON.put("branch", repo.getBranch());
                jsonObj.put("query", query);
                jsonObj.put("variables", variableJSON.toString());
                break;

            case CommitOnly:
                query = GitHubGraphQLQuery.QUERY_BASE_COMMIT_ONLY_AFTER + GitHubGraphQLQuery.QUERY_COMMIT_HEADER_AFTER + GitHubGraphQLQuery.QUERY_COMMIT_MAIN + GitHubGraphQLQuery.QUERY_END;
                variableJSON.put("since", getRunDate(repo, firstRun, missingCommits));
                variableJSON.put("afterCommit", commitPaging.getCursor());
                variableJSON.put("branch", repo.getBranch());

                jsonObj.put("query", query);
                jsonObj.put("variables", variableJSON.toString());
                break;


            case PullOnly:
                query = GitHubGraphQLQuery.QUERY_BASE_PULL_ONLY_AFTER + GitHubGraphQLQuery.QUERY_PULL_HEADER_AFTER + GitHubGraphQLQuery.QUERY_PULL_MAIN + GitHubGraphQLQuery.QUERY_END;
                variableJSON.put("afterPull", pullPaging.getCursor());
                variableJSON.put("branch", repo.getBranch());
                jsonObj.put("query", query);
                jsonObj.put("variables", variableJSON.toString());
                break;


            case IssueOnly:
                query = GitHubGraphQLQuery.QUERY_BASE_ISSUE_ONLY_AFTER + GitHubGraphQLQuery.QUERY_ISSUES_HEADER_AFTER + GitHubGraphQLQuery.QUERY_ISSUE_MAIN + GitHubGraphQLQuery.QUERY_END;
                variableJSON.put("afterIssue", issuePaging.getCursor());
                jsonObj.put("query", query);
                jsonObj.put("variables", variableJSON.toString());
                break;


            case CommitAndIssue:
                query = GitHubGraphQLQuery.QUERY_BASE_COMMIT_AND_ISSUE_AFTER + GitHubGraphQLQuery.QUERY_COMMIT_HEADER_AFTER + GitHubGraphQLQuery.QUERY_COMMIT_MAIN + GitHubGraphQLQuery.QUERY_ISSUES_HEADER_AFTER + GitHubGraphQLQuery.QUERY_ISSUE_MAIN + GitHubGraphQLQuery.QUERY_END;
                variableJSON.put("afterIssue", issuePaging.getCursor());
                variableJSON.put("afterCommit", commitPaging.getCursor());
                variableJSON.put("since", getRunDate(repo, firstRun, missingCommits));
                variableJSON.put("branch", repo.getBranch());

                jsonObj.put("query", query);
                jsonObj.put("variables", variableJSON.toString());
                break;


            case CommitAndPull:
                query = GitHubGraphQLQuery.QUERY_BASE_COMMIT_AND_PULL_AFTER + GitHubGraphQLQuery.QUERY_PULL_HEADER_AFTER + GitHubGraphQLQuery.QUERY_PULL_MAIN + GitHubGraphQLQuery.QUERY_COMMIT_HEADER_AFTER + GitHubGraphQLQuery.QUERY_COMMIT_MAIN + GitHubGraphQLQuery.QUERY_END;
                variableJSON.put("since", getRunDate(repo, firstRun, missingCommits));
                variableJSON.put("afterPull", pullPaging.getCursor());
                variableJSON.put("afterCommit", commitPaging.getCursor());
                variableJSON.put("branch", repo.getBranch());
                jsonObj.put("query", query);
                jsonObj.put("variables", variableJSON.toString());
                break;


            case PullAndIssue:
                query = GitHubGraphQLQuery.QUERY_BASE_ISSUE_AND_PULL_AFTER + GitHubGraphQLQuery.QUERY_PULL_HEADER_AFTER + GitHubGraphQLQuery.QUERY_PULL_MAIN + GitHubGraphQLQuery.QUERY_ISSUES_HEADER_AFTER + GitHubGraphQLQuery.QUERY_ISSUE_MAIN + GitHubGraphQLQuery.QUERY_END;
                variableJSON.put("afterPull", pullPaging.getCursor());
                variableJSON.put("afterIssue", issuePaging.getCursor());
                variableJSON.put("branch", repo.getBranch());
                jsonObj.put("query", query);
                jsonObj.put("variables", variableJSON.toString());
                break;


            case All:
                query = GitHubGraphQLQuery.QUERY_BASE_ALL_AFTER + GitHubGraphQLQuery.QUERY_COMMIT_HEADER_AFTER + GitHubGraphQLQuery.QUERY_COMMIT_MAIN + GitHubGraphQLQuery.QUERY_PULL_HEADER_AFTER + GitHubGraphQLQuery.QUERY_PULL_MAIN + GitHubGraphQLQuery.QUERY_ISSUES_HEADER_AFTER + GitHubGraphQLQuery.QUERY_ISSUE_MAIN + GitHubGraphQLQuery.QUERY_END;
                variableJSON.put("since", getRunDate(repo, firstRun, missingCommits));
                variableJSON.put("afterPull", pullPaging.getCursor());
                variableJSON.put("afterCommit", commitPaging.getCursor());
                variableJSON.put("afterIssue", issuePaging.getCursor());
                variableJSON.put("branch", repo.getBranch());
                jsonObj.put("query", query);
                jsonObj.put("variables", variableJSON.toString());
                break;


            case None:
                jsonObj = null;
                break;


            default:
                jsonObj = null;
                break;

        }
        return jsonObj;
    }

    @SuppressWarnings({"PMD.NPathComplexity", "PMD.ExcessiveMethodLength", "PMD.AvoidBranchingStatementAsLastInLoop", "PMD.EmptyIfStmt"})
    private CollectionMode getCollectionMode(boolean firstTime, GitHubPaging commitPaging, GitHubPaging pullPaging, GitHubPaging issuePaging) {
        if (firstTime) {
            if (!pullPaging.isLastPage() && !issuePaging.isLastPage()) return CollectionMode.FirstTimeAll;
            if (pullPaging.isLastPage() && !issuePaging.isLastPage()) return CollectionMode.FirstTimeCommitAndIssue;
            if (!pullPaging.isLastPage() && issuePaging.isLastPage()) return CollectionMode.FirstTimeCommitAndPull;
            if (pullPaging.isLastPage() && issuePaging.isLastPage()) return CollectionMode.FirstTimeCommitOnly;
        }

        if (commitPaging.isLastPage() && pullPaging.isLastPage() && issuePaging.isLastPage())
            return CollectionMode.None;
        if (commitPaging.isLastPage() && pullPaging.isLastPage() && !issuePaging.isLastPage())
            return CollectionMode.IssueOnly;
        if (commitPaging.isLastPage() && !pullPaging.isLastPage() && !issuePaging.isLastPage())
            return CollectionMode.PullAndIssue;
        if (!commitPaging.isLastPage() && pullPaging.isLastPage() && issuePaging.isLastPage())
            return CollectionMode.CommitOnly;
        if (!commitPaging.isLastPage() && !pullPaging.isLastPage() && issuePaging.isLastPage())
            return CollectionMode.CommitAndPull;
        if (commitPaging.isLastPage() && !pullPaging.isLastPage() && issuePaging.isLastPage())
            return CollectionMode.PullOnly;
        if (!commitPaging.isLastPage() && pullPaging.isLastPage() && !issuePaging.isLastPage())
            return CollectionMode.CommitAndIssue;
        if (!commitPaging.isLastPage() && !pullPaging.isLastPage() && !issuePaging.isLastPage())
            return CollectionMode.All;
        return CollectionMode.None;
    }

    @SuppressWarnings({"PMD.NPathComplexity"})
    private GitHubPaging processPullRequest(JSONObject pullObject, GitHubRepo repo, Map<Long, String> prMap, long historyTimeStamp) throws MalformedURLException, HygieiaException {
        GitHubPaging paging = new GitHubPaging();
        paging.setLastPage(true);
        if (pullObject == null) return paging;
        paging.setTotalCount(asInt(pullObject, "totalCount"));
        JSONObject pageInfo = (JSONObject) pullObject.get("pageInfo");
        paging.setCursor(str(pageInfo, "endCursor"));
        paging.setLastPage(!(Boolean) pageInfo.get("hasNextPage"));
        JSONArray edges = getArray(pullObject, "edges");
        if (CollectionUtils.isEmpty(edges)) {
            return paging;
        }
        int localCount = 0;
        for (Object o : edges) {
            JSONObject node = (JSONObject) ((JSONObject) o).get("node");
            if (node == null) break;
            JSONObject userObject = (JSONObject) node.get("author");
            String merged = str(node, "mergedAt");
            String closed = str(node, "closedAt");
            String updated = str(node, "updatedAt");
            String created = str(node, "createdAt");
            long createdTimestamp = getTimeStampMills(created);
            long mergedTimestamp = getTimeStampMills(merged);
            long closedTimestamp = getTimeStampMills(closed);
            long updatedTimestamp = getTimeStampMills(updated);
            GitHubParsed gitHubParsed = new GitHubParsed(repo.getRepoUrl());
            GitRequest pull = new GitRequest();
            //General Info
            pull.setRequestType("pull");
            pull.setNumber(str(node, "number"));
            pull.setUserId(str(userObject, "login"));
            pull.setScmUrl(repo.getRepoUrl());
            pull.setScmBranch(repo.getBranch());
            pull.setOrgName(gitHubParsed.getOrgName());
            pull.setRepoName(gitHubParsed.getRepoName());
            pull.setScmCommitLog(str(node, "title"));
            pull.setTimestamp(System.currentTimeMillis());
            pull.setCreatedAt(createdTimestamp);
            pull.setClosedAt(closedTimestamp);
            pull.setUpdatedAt(updatedTimestamp);
            //Status
            pull.setState(str(node, "state").toLowerCase());
            JSONObject headrefJson = (JSONObject) node.get("headRef");
            if (headrefJson != null) {
                JSONObject targetJson = (JSONObject) headrefJson.get("target");
                pull.setHeadSha(str(targetJson, "oid"));
            }
            if (!StringUtils.isEmpty(merged)) {
                pull.setScmRevisionNumber(str((JSONObject) node.get("mergeCommit"), "oid"));
                pull.setResolutiontime((mergedTimestamp - createdTimestamp));
                pull.setScmCommitTimestamp(mergedTimestamp);
                pull.setMergedAt(mergedTimestamp);
                JSONObject commitsObject = (JSONObject) node.get("commits");
                pull.setNumberOfChanges(commitsObject != null ? asInt(commitsObject, "totalCount") : 0);
                List<Commit> prCommits = getPRCommits(repo, commitsObject, pull);
                pull.setCommits(prCommits);
                List<Comment> comments = getComments(repo, (JSONObject) node.get("comments"));
                pull.setComments(comments);
                List<Review> reviews = getReviews(repo, (JSONObject) node.get("reviews"));
                pull.setReviews(reviews);
                MergeEvent mergeEvent = getMergeEvent(repo, pull, (JSONObject) node.get("timeline"));
                if (mergeEvent != null) {
                    pull.setScmMergeEventRevisionNumber(mergeEvent.getMergeSha());
                    pull.setMergeAuthor(mergeEvent.getMergeAuthor());
                    pull.setMergeAuthorLDAPDN(mergeEvent.getMergeAuthorLDAPDN());
                }
            }
            // commit etc details
            pull.setSourceBranch(str(node, "headRefName"));
            if (node.get("headRepository") != null) {
                JSONObject headObject = (JSONObject) node.get("headRepository");
                GitHubParsed sourceRepoUrlParsed = new GitHubParsed(str(headObject, "url"));
                pull.setSourceRepo(!Objects.equals("", sourceRepoUrlParsed.getOrgName()) ? sourceRepoUrlParsed.getOrgName() + "/" + sourceRepoUrlParsed.getRepoName() : sourceRepoUrlParsed.getRepoName());
            }
            if (node.get("baseRef") != null) {
                pull.setBaseSha(str((JSONObject) ((JSONObject) node.get("baseRef")).get("target"), "oid"));
            }
            pull.setTargetBranch(str(node, "baseRefName"));
            //pull.setTargetRepo(gitHubParsed.getUrl());
            pull.setTargetRepo(!Objects.equals("", gitHubParsed.getOrgName()) ? gitHubParsed.getOrgName() + "/" + gitHubParsed.getRepoName() : gitHubParsed.getRepoName());

//            LOG.debug("pr " + pull.getNumber() + " mergedAt " + new DateTime(pull.getMergedAt()).toString("yyyy-MM-dd hh:mm:ss.SSa")
//                                              + " updateAt " + new DateTime(pull.getUpdatedAt()).toString("yyyy-MM-dd hh:mm:ss.SSa")
//                                              + " historAt " + new DateTime(historyTimeStamp).toString("yyyy-MM-dd hh:mm:ss.SSa"));

            boolean stop = (pull.getUpdatedAt() < historyTimeStamp) ||
                    ((!MapUtils.isEmpty(prMap) && prMap.get(pull.getUpdatedAt()) != null) && (Objects.equals(prMap.get(pull.getUpdatedAt()), pull.getNumber())));
            if (stop) {
                paging.setLastPage(true);
                LOG.debug("------ Stopping pull request processing. History check is met OR Found matching entry in existing pull requests. Pull Request#" + pull.getNumber());
                break;
            } else {
                //add to the list
                localCount++;
                pullRequests.add(pull);
            }
        }
        paging.setCurrentCount(localCount);
        return paging;
    }

    @SuppressWarnings("PMD.NPathComplexity")
    private GitHubPaging processCommits(JSONObject refObject, GitHubRepo repo) {
        GitHubPaging paging = new GitHubPaging();
        paging.setLastPage(true); //initialize

        if (refObject == null) return paging;

        JSONObject target = (JSONObject) refObject.get("target");

        if (target == null) return paging;

        JSONObject history = (JSONObject) target.get("history");

        JSONObject pageInfo = (JSONObject) history.get("pageInfo");

        paging.setCursor(str(pageInfo, "endCursor"));
        paging.setLastPage(!(Boolean) pageInfo.get("hasNextPage"));

        JSONArray edges = (JSONArray) history.get("edges");

        if (CollectionUtils.isEmpty(edges)) {
            return paging;
        }

        paging.setCurrentCount(edges.size());

        for (Object o : edges) {
            JSONObject node = (JSONObject) ((JSONObject) o).get("node");
            JSONObject authorJSON = (JSONObject) node.get("author");
            JSONObject authorUserJSON = (JSONObject) authorJSON.get("user");

            String sha = str(node, "oid");
            int changedFiles = NumberUtils.toInt(str(node, "changedFiles"));
            int deletions = NumberUtils.toInt(str(node, "deletions"));
            int additions = NumberUtils.toInt(str(node, "additions"));
            String message = str(node, "message");
            String authorName = str(authorJSON, "name");
            String authorLogin = authorUserJSON == null ? "unknown" : str(authorUserJSON, "login");
            String authorLDAPDN = "unknown".equalsIgnoreCase(authorLogin) ? null : getLDAPDN(repo, authorLogin);
            Commit commit = new Commit();
            commit.setTimestamp(System.currentTimeMillis());
            commit.setScmUrl(repo.getRepoUrl());
            commit.setScmBranch(repo.getBranch());
            commit.setScmRevisionNumber(sha);
            commit.setScmAuthor(authorName);
            commit.setScmAuthorLogin(authorLogin);
            if (!StringUtils.isEmpty(authorLDAPDN)) {
                commit.setScmAuthorLDAPDN(authorLDAPDN);
            }
            commit.setScmCommitLog(message);
            commit.setScmCommitTimestamp(getTimeStampMills(str(authorJSON, "date")));
            commit.setNumberOfChanges(changedFiles+deletions+additions);
            List<String> parentShas = getParentShas(node);
            commit.setScmParentRevisionNumbers(parentShas);
            commit.setFirstEverCommit(CollectionUtils.isEmpty(parentShas));
            commit.setType(getCommitType(CollectionUtils.size(parentShas), message));
            commits.add(commit);
        }
        return paging;
    }


    private GitHubPaging processIssues(JSONObject issueObject, GitHubParsed gitHubParsed, Map<Long, String> issuesMap, long historyTimeStamp) {
        GitHubPaging paging = new GitHubPaging();
        paging.setLastPage(true);

        if (issueObject == null) return paging;

        paging.setTotalCount(asInt(issueObject, "totalCount"));
        JSONObject pageInfo = (JSONObject) issueObject.get("pageInfo");
        paging.setCursor(str(pageInfo, "endCursor"));
        paging.setLastPage(!(Boolean) pageInfo.get("hasNextPage"));
        JSONArray edges = getArray(issueObject, "edges");

        if (CollectionUtils.isEmpty(edges)) {
            return paging;
        }

        int localCount = 0;
        for (Object o : edges) {
            JSONObject node = (JSONObject) ((JSONObject) o).get("node");
            if (node == null) break;

            String message = str(node, "title");
            String number = str(node, "number");

            JSONObject userObject = (JSONObject) node.get("author");
            String name = str(userObject, "login");
            String created = str(node, "createdAt");
            String updated = str(node, "updatedAt");
            long createdTimestamp = new DateTime(created).getMillis();
            long updatedTimestamp = new DateTime(updated).getMillis();

            GitRequest issue = new GitRequest();
            String state = str(node, "state");

            issue.setClosedAt(0);
            issue.setResolutiontime(0);
            issue.setMergedAt(0);
            if (Objects.equals("CLOSED", state)) {
                //ideally should be checking closedAt field. But it's not yet available in graphQL schema
                issue.setScmCommitTimestamp(updatedTimestamp);
                issue.setClosedAt(updatedTimestamp);
                issue.setMergedAt(updatedTimestamp);
                issue.setResolutiontime((updatedTimestamp - createdTimestamp));
            }
            issue.setUserId(name);
            issue.setScmUrl(gitHubParsed.getUrl());
            issue.setTimestamp(System.currentTimeMillis());
            issue.setScmRevisionNumber(number);
            issue.setNumber(number);
            issue.setScmCommitLog(message);
            issue.setCreatedAt(createdTimestamp);
            issue.setUpdatedAt(updatedTimestamp);

            issue.setNumber(number);
            issue.setRequestType("issue");
            if (Objects.equals("CLOSED", state)) {
                issue.setState("closed");
            } else {
                issue.setState("open");
            }
            issue.setOrgName(gitHubParsed.getOrgName());
            issue.setRepoName(gitHubParsed.getRepoName());

            boolean stop = (issue.getUpdatedAt() < historyTimeStamp) ||
                    ((!MapUtils.isEmpty(issuesMap) && issuesMap.get(issue.getUpdatedAt()) != null) && (Objects.equals(issuesMap.get(issue.getUpdatedAt()), issue.getNumber())));
            if (stop) {
                paging.setLastPage(true);
                LOG.debug("------ Stopping issue processing. History check is met OR Found matching entry in existing issues. Issue#" + issue.getNumber());
                break;
            } else {
                //add to the list
                issues.add(issue);
                localCount++;
            }
        }
        paging.setCurrentCount(localCount);
        return paging;
    }

    private List<Comment> getComments(GitHubRepo repo, JSONObject commentsJSON) throws RestClientException {

        List<Comment> comments = new ArrayList<>();
        if (commentsJSON == null) {
            return comments;
        }
        JSONArray nodes = getArray(commentsJSON, "nodes");
        if (CollectionUtils.isEmpty(nodes)) {
            return comments;
        }
        for (Object n : nodes) {
            JSONObject node = (JSONObject) n;
            Comment comment = new Comment();
            comment.setBody(str(node, "bodyText"));
            comment.setUser(str((JSONObject) node.get("author"), "login"));
            String userLDAP = getLDAPDN(repo, comment.getUser());
            if (!StringUtils.isEmpty(userLDAP)) {
                comment.setUserLDAPDN(userLDAP);
            }
            comment.setCreatedAt(getTimeStampMills(str(node, "createdAt")));
            comment.setUpdatedAt(getTimeStampMills(str(node, "updatedAt")));
            comment.setStatus(str(node, "state"));
            comments.add(comment);
        }
        return comments;
    }

    @SuppressWarnings({"PMD.NPathComplexity"})
    private List<Commit> getPRCommits(GitHubRepo repo, JSONObject commits, GitRequest pull) {
        List<Commit> prCommits = new ArrayList<>();

        if (commits == null) {
            return prCommits;
        }

        JSONArray nodes = (JSONArray) commits.get("nodes");
        if (CollectionUtils.isEmpty(nodes)) {
            return prCommits;
        }
        JSONObject lastCommitStatusObject = null;
        long lastCommitTime = 0L;
        for (Object n : nodes) {
            JSONObject c = (JSONObject) n;
            JSONObject commit = (JSONObject) c.get("commit");
            Commit newCommit = new Commit();
            newCommit.setScmRevisionNumber(str(commit, "oid"));
            newCommit.setScmCommitLog(str(commit, "message"));
            JSONObject author = (JSONObject) commit.get("author");
            JSONObject authorUserJSON = (JSONObject) author.get("user");
            newCommit.setScmAuthor(str(author, "name"));
            newCommit.setScmAuthorLogin(authorUserJSON == null ? "unknown" : str(authorUserJSON, "login"));
            String authorLDAPDN = "unknown".equalsIgnoreCase(newCommit.getScmAuthorLogin()) ? null : getLDAPDN(repo, newCommit.getScmAuthorLogin());
            if (!StringUtils.isEmpty(authorLDAPDN)) {
                newCommit.setScmAuthorLDAPDN(authorLDAPDN);
            }
            newCommit.setScmCommitTimestamp(getTimeStampMills(str(author, "date")));
            JSONObject statusObj = (JSONObject) commit.get("status");

            if (statusObj != null) {
                if (lastCommitTime <= newCommit.getScmCommitTimestamp()) {
                    lastCommitTime = newCommit.getScmCommitTimestamp();
                    lastCommitStatusObject = statusObj;
                }

                if (Objects.equals(newCommit.getScmRevisionNumber(), pull.getHeadSha())) {
                    List<CommitStatus> commitStatuses = getCommitStatuses(statusObj);
                    if (!CollectionUtils.isEmpty(commitStatuses)) {
                        pull.setCommitStatuses(commitStatuses);
                    }
                }
            }
            int changedFiles = NumberUtils.toInt(str(commit, "changedFiles"));
            int deletions = NumberUtils.toInt(str(commit, "deletions"));
            int additions = NumberUtils.toInt(str(commit, "additions"));
            newCommit.setNumberOfChanges(changedFiles+deletions+additions);
            prCommits.add(newCommit);
        }

        if (StringUtils.isEmpty(pull.getHeadSha()) || CollectionUtils.isEmpty(pull.getCommitStatuses())) {
            List<CommitStatus> commitStatuses = getCommitStatuses(lastCommitStatusObject);
            if (!CollectionUtils.isEmpty(commitStatuses)) {
                pull.setCommitStatuses(commitStatuses);
            }
        }
        return prCommits;
    }

    private List<CommitStatus> getCommitStatuses(JSONObject statusObject) throws RestClientException {

        Map<String, CommitStatus> statuses = new HashMap<>();

        if (statusObject == null) {
            return new ArrayList<>();
        }

        JSONArray contexts = (JSONArray) statusObject.get("contexts");

        if (CollectionUtils.isEmpty(contexts)) {
            return new ArrayList<>();
        }
        for (Object ctx : contexts) {
            String ctxStr = str((JSONObject) ctx, "context");
            if ((ctxStr != null) && !statuses.containsKey(ctxStr)) {
                CommitStatus status = new CommitStatus();
                status.setContext(ctxStr);
                status.setDescription(str((JSONObject) ctx, "description"));
                status.setState(str((JSONObject) ctx, "state"));
                statuses.put(ctxStr, status);
            }
        }
        return new ArrayList<>(statuses.values());
    }

    private List<Review> getReviews(GitHubRepo repo, JSONObject reviewObject) throws RestClientException {

        List<Review> reviews = new ArrayList<>();

        if (reviewObject == null) {
            return reviews;
        }

        JSONArray nodes = (JSONArray) reviewObject.get("nodes");

        if (CollectionUtils.isEmpty(nodes)) {
            return reviews;
        }

        for (Object n : nodes) {
            JSONObject node = (JSONObject) n;
            Review review = new Review();
            review.setState(str(node, "state"));
            review.setBody(str(node, "bodyText"));
            JSONObject authorObj = (JSONObject) node.get("author");
            review.setAuthor(str(authorObj, "login"));
            String authorLDAPDN = getLDAPDN(repo, review.getAuthor());
            if (!StringUtils.isEmpty(authorLDAPDN)) {
                review.setAuthorLDAPDN(authorLDAPDN);
            }
            review.setCreatedAt(getTimeStampMills(str(node, "createdAt")));
            review.setUpdatedAt(getTimeStampMills(str(node, "updatedAt")));
            reviews.add(review);
        }
        return reviews;
    }

    private MergeEvent getMergeEvent(GitHubRepo repo, GitRequest pr, JSONObject timelineObject) throws RestClientException {
        if (timelineObject == null) {
            return null;
        }
        JSONArray edges = (JSONArray) timelineObject.get("edges");
        if (CollectionUtils.isEmpty(edges)) {
            return null;
        }

        for (Object e : edges) {
            JSONObject edge = (JSONObject) e;
            JSONObject node = (JSONObject) edge.get("node");
            if (node != null) {
                String typeName = str(node, "__typename");
                if ("MergedEvent".equalsIgnoreCase(typeName)) {
                    JSONObject timelinePrNbrObj = (JSONObject) node.get("pullRequest");
                    if (timelinePrNbrObj != null && pr.getNumber().equals(str(timelinePrNbrObj, "number"))) {
                        MergeEvent mergeEvent = new MergeEvent();
                        JSONObject commit = (JSONObject) node.get("commit");
                        mergeEvent.setMergeSha(str(commit, "oid"));
                        mergeEvent.setMergedAt(getTimeStampMills(str(node, "createdAt")));
                        JSONObject author = (JSONObject) node.get("actor");
                        if (author != null) {
                            mergeEvent.setMergeAuthor(str(author, "login"));
                            String mergeAuthorLDAPDN = getLDAPDN(repo, mergeEvent.getMergeAuthor());
                            if (!StringUtils.isEmpty(mergeAuthorLDAPDN)) {
                                mergeEvent.setMergeAuthorLDAPDN(mergeAuthorLDAPDN);
                            }
                        }
                        return mergeEvent;
                    }
                }
            }
        }
        return null;
    }

    private String getMergeEventSha(GitRequest pr, JSONObject timelineObject) throws RestClientException {
        String mergeEventSha = "";
        if (timelineObject == null) {
            return mergeEventSha;
        }
        JSONArray edges = (JSONArray) timelineObject.get("edges");
        if (CollectionUtils.isEmpty(edges)) {
            return mergeEventSha;
        }

        for (Object e : edges) {
            JSONObject edge = (JSONObject) e;
            JSONObject node = (JSONObject) edge.get("node");
            if (node != null) {
                String typeName = str(node, "__typename");
                if ("MergedEvent".equalsIgnoreCase(typeName)) {
                    JSONObject timelinePrNbrObj = (JSONObject) node.get("pullRequest");
                    if (timelinePrNbrObj != null && pr.getNumber().equals(str(timelinePrNbrObj, "number"))) {
                        JSONObject commit = (JSONObject) node.get("commit");
                        mergeEventSha = str(commit, "oid");
                        break;
                    }
                }
            }
        }
        return mergeEventSha;
    }

    private CommitType getCommitType(int parentSize, String commitMessage) {
        if (parentSize > 1) return CommitType.Merge;
        if (settings.getNotBuiltCommits() == null) return CommitType.New;
        if (!CollectionUtils.isEmpty(commitExclusionPatterns)) {
            for (Pattern pattern : commitExclusionPatterns) {
                if (pattern.matcher(commitMessage).matches()) {
                    return CommitType.NotBuilt;
                }
            }
        }
        return CommitType.New;
    }


    @Override
    public GitHubRateLimit getRateLimit(GitHubRepo repo) throws MalformedURLException, HygieiaException {
        GitHubParsed gitHubParsed = new GitHubParsed(repo.getRepoUrl());
        String decryptedPassword = decryptString(repo.getPassword(), settings.getKey());
        String personalAccessToken = (String) repo.getOptions().get("personalAccessToken");
        String decryptPersonalAccessToken = decryptString(personalAccessToken, settings.getKey());
        JSONObject query = new JSONObject();
        query.put("query", GitHubGraphQLQuery.QUERY_RATE_LIMIT);
        ResponseEntity<String> response = null;
        response = makeRestCallPost(gitHubParsed.getGraphQLUrl(), repo.getUserId(), decryptedPassword, decryptPersonalAccessToken, query);
        JSONObject data = (JSONObject) parseAsObject(response).get("data");
        JSONArray errors = getArray(parseAsObject(response), "errors");
        if (data == null) return null;
        if (!CollectionUtils.isEmpty(errors)) {
            throw new HygieiaException("Error in GraphQL query:" + errors.toJSONString(), HygieiaException.BAD_DATA);
        }
        JSONObject rateLimitJSON = (JSONObject) data.get("rateLimit");
        if (rateLimitJSON == null) return null;
        int limit = asInt(rateLimitJSON, "limit");
        int remaining = asInt(rateLimitJSON, "remaining");
        long rateLimitResetAt = getTimeStampMills(str(rateLimitJSON, "resetAt"));
        GitHubRateLimit rateLimit = new GitHubRateLimit();
        rateLimit.setLimit(limit);
        rateLimit.setRemaining(remaining);
        rateLimit.setResetTime(rateLimitResetAt);

        return rateLimit;
    }

    @Override
    public String getLDAPDN(GitHubRepo repo, String user) {
        if (StringUtils.isEmpty(user)) return null;
        //This is weird. Github does replace the _ in commit author with - in the user api!!!
        String formattedUser = user.replace("_", "-");
        if (ldapMap.get(formattedUser) != null) {
            return ldapMap.get(formattedUser);
        }
        String repoUrl = (String) repo.getOptions().get("url");
        try {
            GitHubParsed gitHubParsed = new GitHubParsed(repoUrl);
            String apiUrl = gitHubParsed.getBaseApiUrl();

            String queryUrl = apiUrl.concat("users/").concat(formattedUser);

            ResponseEntity<String> response = makeRestCallGet(queryUrl);
            JSONObject jsonObject = parseAsObject(response);
            String ldapDN = str(jsonObject, "ldap_dn");
            if (!StringUtils.isEmpty(ldapDN)) {
                ldapMap.put(formattedUser, ldapDN);
            }
            return str(jsonObject, "ldap_dn");
        } catch (MalformedURLException | HygieiaException | RestClientException e) {
            LOG.error("Error getting LDAP_DN For user " + user, e);
        }
        return null;
    }

    /// Utility Methods
    private int asInt(JSONObject json, String key) {
        String val = str(json, key);
        try {
            if (val != null) {
                return Integer.parseInt(val);
            }
        } catch (NumberFormatException ex) {
            LOG.error("Invalid number format: " + ex.getMessage());
        }
        return 0;
    }

    private long getTimeStampMills(String dateTime) {
        return StringUtils.isEmpty(dateTime) ? 0 : new DateTime(dateTime).getMillis();
    }

    /**
     * Get run date based off of firstRun boolean
     *
     * @param repo
     * @param firstRun
     * @return
     */
    private String getRunDate(GitHubRepo repo, boolean firstRun, boolean missingCommits) {
        if (missingCommits) {
            long repoOffsetTime = getRepoOffsetTime(repo);
            if (repoOffsetTime > 0) {
                return getDate(new DateTime(getRepoOffsetTime(repo)), 0, settings.getOffsetMinutes()).toString();
            } else {
                return getDate(new DateTime(repo.getLastUpdated()), 0, settings.getOffsetMinutes()).toString();
            }
        }
        if (firstRun) {
            int firstRunDaysHistory = settings.getFirstRunHistoryDays();
            if (firstRunDaysHistory > 0) {
                return getDate(new DateTime(), firstRunDaysHistory, 0).toString();
            } else {
                return getDate(new DateTime(), FIRST_RUN_HISTORY_DEFAULT, 0).toString();
            }
        } else {
            return getDate(new DateTime(repo.getLastUpdated()), 0, settings.getOffsetMinutes()).toString();
        }
    }

    public long getRepoOffsetTime(GitHubRepo repo) {
        List<Commit> allPrCommits = new ArrayList<>();
        pullRequests.stream().filter(pr -> "merged".equalsIgnoreCase(pr.getState())).forEach(pr -> {
            allPrCommits.addAll(pr.getCommits().stream().collect(Collectors.toList()));
        });
        if (CollectionUtils.isEmpty(allPrCommits)) {
            return 0;
        }
        Commit oldestPrCommit = allPrCommits.stream().min(Comparator.comparing(Commit::getScmCommitTimestamp)).orElse(null);
        return (oldestPrCommit != null) ? oldestPrCommit.getScmCommitTimestamp() : 0;
    }

    /**
     * Date utility
     *
     * @param dateInstance
     * @param offsetDays
     * @param offsetMinutes
     * @return
     */
    private static DateTime getDate(DateTime dateInstance, int offsetDays, int offsetMinutes) {
        return dateInstance.minusDays(offsetDays).minusMinutes(offsetMinutes);
    }

    // Makes use of the graphQL endpoint, will not work for REST api
    private JSONObject getDataFromRestCallPost(GitHubParsed gitHubParsed, GitHubRepo repo, String password, String personalAccessToken, JSONObject query) throws MalformedURLException, HygieiaException {
        ResponseEntity<String> response = makeRestCallPost(gitHubParsed.getGraphQLUrl(), repo.getUserId(), password, personalAccessToken, query);
        JSONObject data = (JSONObject) parseAsObject(response).get("data");
        JSONArray errors = getArray(parseAsObject(response), "errors");

        if (CollectionUtils.isEmpty(errors)) {
            return data;
        }

        JSONObject error = (JSONObject) errors.get(0);

        if (!error.containsKey("type") || !error.get("type").equals("NOT_FOUND")) {
            throw new HygieiaException("Error in GraphQL query:" + errors.toJSONString(), HygieiaException.JSON_FORMAT_ERROR);
        }

        RedirectedStatus redirectedStatus = checkForRedirectedRepo(repo);

        if (!redirectedStatus.isRedirected()) {
            throw new HygieiaException("Error in GraphQL query:" + errors.toJSONString(), HygieiaException.JSON_FORMAT_ERROR);
        }

        String redirectedUrl = redirectedStatus.getRedirectedUrl();
        LOG.debug("Repo was redirected from: " + repo.getRepoUrl() + " to " + redirectedUrl);
        repo.setRepoUrl(redirectedUrl);
        gitHubParsed.updateForRedirect(redirectedUrl);

        JSONParser parser = new JSONParser();
        try {
            JSONObject variableJSON = (JSONObject) parser.parse(str(query, "variables"));
            variableJSON.put("name", gitHubParsed.getRepoName());
            variableJSON.put("owner", gitHubParsed.getOrgName());
            query.put("variables", variableJSON.toString());
        } catch (ParseException e) {
            LOG.error("Could not parse JSON String", e);
        }
        return getDataFromRestCallPost(gitHubParsed, repo, password, personalAccessToken, query);
    }

    private ResponseEntity<String> makeRestCallPost(String url, String userId, String password, String personalAccessToken, JSONObject query) {
        // Basic Auth only.
        if (!Objects.equals("", userId) && !Objects.equals("", password)) {
            return restOperations.exchange(url, HttpMethod.POST, new HttpEntity<Object>(query, createHeaders(userId, password)), String.class);
        }else if (personalAccessToken != null && !Objects.equals("", personalAccessToken)) {
            return restOperations.exchange(url, HttpMethod.POST, new HttpEntity<Object>(query, createHeaders(personalAccessToken)), String.class);
        }else if (settings.getPersonalAccessToken() != null && !Objects.equals("", settings.getPersonalAccessToken())) {
            return restOperations.exchange(url, HttpMethod.POST, new HttpEntity<Object>(query, createHeaders(settings.getPersonalAccessToken())), String.class);
        } else {
            return restOperations.exchange(url, HttpMethod.POST, new HttpEntity<Object>(query, null), String.class);
        }
    }

    private ResponseEntity<String> makeRestCallGet(String url) throws RestClientException {
        // Basic Auth only.
        if (settings.getPersonalAccessToken() != null && !Objects.equals("", settings.getPersonalAccessToken())) {
            return restOperations.exchange(url, HttpMethod.GET,
                    new HttpEntity<>(createHeaders(settings.getPersonalAccessToken())),
                    String.class);
        } else {
            return restOperations.exchange(url, HttpMethod.GET, null,
                    String.class);
        }

    }


    private HttpHeaders createHeaders(final String userId, final String password) {
        String auth = userId + ":" + password;
        byte[] encodedAuth = Base64.encodeBase64(auth.getBytes(StandardCharsets.US_ASCII));
        String authHeader = "Basic " + new String(encodedAuth);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", authHeader);
        return headers;
    }

    private HttpHeaders createHeaders(final String token) {
        String authHeader = "token " + token;
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", authHeader);
        return headers;
    }

    private JSONObject parseAsObject(ResponseEntity<String> response) {
        try {
            return (JSONObject) new JSONParser().parse(response.getBody());
        } catch (ParseException pe) {
            LOG.error(pe.getMessage());
        }
        return new JSONObject();
    }

    private String str(JSONObject json, String key) {
        if (json == null) return "";
        Object value = json.get(key);
        return (value == null) ? "" : value.toString();
    }

    private JSONArray getArray(JSONObject json, String key) {
        if (json == null) return new JSONArray();
        if (json.get(key) == null) return new JSONArray();
        return (JSONArray) json.get(key);
    }

    private List<String> getParentShas(JSONObject commit) {
        JSONObject parents = (JSONObject) commit.get("parents");
        JSONArray parentNodes = (JSONArray) parents.get("nodes");
        List<String> parentShas = new ArrayList<>();
        if (!CollectionUtils.isEmpty(parentNodes)) {
            for (Object parentObj : parentNodes) {
                parentShas.add(str((JSONObject) parentObj, "oid"));
            }
        }
        return parentShas;
    }

    /**
     * Decrypt string
     *
     * @param string
     * @param key
     * @return String
     */
    private static String decryptString(String string, String key) {
        if (!StringUtils.isEmpty(string)) {
            try {
                return Encryption.decryptString(
                        string, key);
            } catch (EncryptionException e) {
                LOG.error(e.getMessage());
            }
        }
        return "";
    }
}