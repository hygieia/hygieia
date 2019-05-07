package com.capitalone.dashboard.webhook.github;

import com.capitalone.dashboard.repository.CollectorItemRepository;
import com.capitalone.dashboard.settings.ApiSettings;
import com.capitalone.dashboard.client.RestClient;
import com.capitalone.dashboard.model.webhook.github.GitHubParsed;
import com.capitalone.dashboard.misc.HygieiaException;
import com.capitalone.dashboard.model.CollectorItem;
import com.capitalone.dashboard.model.Comment;
import com.capitalone.dashboard.model.Commit;
import com.capitalone.dashboard.model.CommitStatus;
import com.capitalone.dashboard.model.GitRequest;
import com.capitalone.dashboard.model.Review;
import com.capitalone.dashboard.repository.CommitRepository;
import com.capitalone.dashboard.repository.GitRequestRepository;
import com.capitalone.dashboard.service.CollectorService;
import com.capitalone.dashboard.webhook.settings.GitHubWebHookSettings;
import com.capitalone.dashboard.webhook.settings.WebHookSettings;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.DateTime;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.List;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Collections;

public class GitHubPullRequestV3 extends GitHubV3 {
    private static final Log LOG = LogFactory.getLog(GitHubPullRequestV3.class);

    private final GitRequestRepository gitRequestRepository;
    private final CollectorItemRepository collectorItemRepository;
    private final CommitRepository commitRepository;

    private Map<String, String> ldapDNMap = new HashMap<>();

    public GitHubPullRequestV3(CollectorService collectorService,
                               RestClient restClient,
                               GitRequestRepository gitRequestRepository,
                               CommitRepository commitRepository,
                               CollectorItemRepository collectorItemRepository,
                               ApiSettings apiSettings) {
        super(collectorService, restClient, apiSettings);

        this.gitRequestRepository = gitRequestRepository;
        this.collectorItemRepository = collectorItemRepository;
        this.commitRepository = commitRepository;
    }

    @Override
    public CollectorItemRepository getCollectorItemRepository() { return this.collectorItemRepository; }

    @Override
    public String process(JSONObject prJsonObject) throws MalformedURLException, HygieiaException, ParseException {
        Object pullRequestObject = restClient.getAsObject(prJsonObject, "pull_request");
        if (pullRequestObject == null) return "Pull Request Data Not Available";

        int prNumber = restClient.getInteger(pullRequestObject,"number");
        if (prNumber == 0) return "Pull Request Number Not Available";

        Object repoMap = prJsonObject.get("repository");
        if (repoMap == null) { return "Repository Data Not Available"; }

        String repoUrl = restClient.getString(repoMap, "html_url");
        GitHubParsed gitHubParsed = new GitHubParsed(repoUrl);

        boolean isPrivate = restClient.getBoolean(repoMap, "private");

        JSONObject postBody = buildGraphQLQuery(gitHubParsed, pullRequestObject);

        if (postBody == null) { return "No Commits found on the PR. Returning ...";}

        WebHookSettings webHookSettings = apiSettings.getWebHook();

        if (webHookSettings == null) {
            return "Github Webhook properties not set on the properties file";
        }

        GitHubWebHookSettings gitHubWebHookSettings = webHookSettings.getGitHub();

        if (gitHubWebHookSettings == null) {
            return "Github Webhook properties not set on the properties file";
        }

        String gitHubWebHookToken = gitHubWebHookSettings.getToken();

        long start = System.currentTimeMillis();

        String repoToken = getRepositoryToken(gitHubParsed.getUrl());

        long end = System.currentTimeMillis();
        LOG.debug("Time to make collectorItemRepository call to fetch repository token = "+(end-start));

        String token = isPrivate ? RestClient.decryptString(repoToken, apiSettings.getKey()) : gitHubWebHookToken;

        if (StringUtils.isEmpty(token)) {
            throw new HygieiaException("Failed processing payload. Missing Github API token in Hygieia.", HygieiaException.INVALID_CONFIGURATION);
        }

        ResponseEntity<String> response = null;
        try {
            response = restClient.makeRestCallPost(gitHubParsed.getGraphQLUrl(), "token", token, postBody);
        } catch (Exception e) {
            throw new HygieiaException(e);
        }

        JSONObject responseJsonObject = restClient.parseAsObject(response);

        if ((responseJsonObject == null) || responseJsonObject.isEmpty()) { return "GraphQL Response Empty From "+gitHubParsed.getGraphQLUrl(); }

        checkForErrors(responseJsonObject);

        JSONObject prData = (JSONObject) responseJsonObject.get("data");

        if ((prData == null) || prData.isEmpty()) { return "Pull Request Data Empty From "+gitHubParsed.getGraphQLUrl(); }

        Object base = restClient.getAsObject(pullRequestObject, "base");
        String branch = restClient.getString(base, "ref");

        GitRequest pull = buildGitRequestFromPayload(repoUrl, branch, pullRequestObject);

        updateGitRequestWithGraphQLData(pull, repoUrl, branch, prData, token);

        gitRequestRepository.save(pull);

        return "Pull Request Processed Successfully";
    }

    protected JSONObject buildGraphQLQuery(GitHubParsed gitHubParsed, Object pullRequestObject) {
        StringBuilder queryBuilder = new StringBuilder("");

        int pullNumber = restClient.getInteger(pullRequestObject,"number");
        int commitsCount = restClient.getInteger(pullRequestObject, "commits");
        int commentsCount = restClient.getInteger(pullRequestObject, "comments");

        if (commitsCount == 0) { return null; }

        JSONObject variableJSON = new JSONObject();
        variableJSON.put("owner", gitHubParsed.getOrgName());
        variableJSON.put("name", gitHubParsed.getRepoName());
        variableJSON.put("number", pullNumber);

        queryBuilder.append(GraphQLQuery.PR_GRAPHQL_BEGIN_PRE);
        if (commitsCount > 0) {
            queryBuilder.append(GraphQLQuery.PR_GRAPHQL_COMMITS_BEGIN);
            variableJSON.put("commits", commitsCount);
        }
        if (commentsCount > 0) {
            queryBuilder.append(GraphQLQuery.PR_GRAPHQL_COMMENTS_BEGIN);
            variableJSON.put("comments", commentsCount);
        }
        queryBuilder.append(GraphQLQuery.PR_GRAPHQL_BEGIN_POST);

        if (commitsCount > 0) {
            queryBuilder.append(GraphQLQuery.PR_GRAPHQL_COMMITS);
        }
        if (commentsCount > 0) {
            queryBuilder.append(GraphQLQuery.PR_GRAPHQL_COMMENTS);
        }

        queryBuilder.append(GraphQLQuery.PR_GRAPHQL_REVIEWS);
        queryBuilder.append(GraphQLQuery.PR_GRAPHQL_END);

        JSONObject query = new JSONObject();

        query.put("query", queryBuilder.toString());
        query.put("variables", variableJSON.toString());

        return query;
    }

    protected GitRequest buildGitRequestFromPayload(String repoUrl, String branch, Object pullRequestObject) throws HygieiaException, MalformedURLException {
        GitRequest pull = new GitRequest();
        GitHubParsed gitHubParsed = new GitHubParsed(repoUrl);

        pull.setRequestType("pull");
        pull.setNumber(restClient.getString(pullRequestObject,"number"));
        Object user = restClient.getAsObject(pullRequestObject, "user");
        pull.setUserId(restClient.getString(user, "login"));
        pull.setScmUrl(repoUrl);
        pull.setScmBranch(branch);
        pull.setOrgName(gitHubParsed.getOrgName());
        pull.setRepoName(gitHubParsed.getRepoName());
        pull.setScmCommitLog(restClient.getString(pullRequestObject, "title"));
        pull.setTimestamp(System.currentTimeMillis());

        String createdTimestampStr = restClient.getString(pullRequestObject, "created_at");
        long createdTimestampMillis = getTimeStampMills(createdTimestampStr);
        pull.setCreatedAt(createdTimestampMillis);

        String updatedTimestampStr = restClient.getString(pullRequestObject, "updated_at");
        pull.setUpdatedAt(getTimeStampMills(updatedTimestampStr));

        String closedTimestampStr = restClient.getString(pullRequestObject, "closed_at");
        pull.setClosedAt(getTimeStampMills(closedTimestampStr));

        String stateStr = restClient.getString(pullRequestObject, "state");
        if (!StringUtils.isEmpty(stateStr)) {
            if ("closed".equalsIgnoreCase(stateStr) || "close".equalsIgnoreCase(stateStr)) {
                stateStr = "merged";
            }
            pull.setState(stateStr.toLowerCase());
        }

        // Source Repo on which the changes/commits have been made.
        Object head = restClient.getAsObject(pullRequestObject, "head");
        pull.setHeadSha(restClient.getString(head, "sha"));
        Object headRepo = restClient.getAsObject(head, "repo");
        pull.setSourceRepo(restClient.getString(headRepo, "full_name"));
        pull.setSourceBranch(restClient.getString(head, "ref"));

        // Target Repo against which the PR has been raised.
        Object base = restClient.getAsObject(pullRequestObject, "base");
        pull.setBaseSha(restClient.getString(base, "sha"));
        pull.setTargetBranch(branch);
        pull.setTargetRepo(!Objects.equals("", gitHubParsed.getOrgName()) ? gitHubParsed.getOrgName() + "/" + gitHubParsed.getRepoName() : gitHubParsed.getRepoName());

        // Total number of commits
        pull.setNumberOfChanges(restClient.getInteger(pullRequestObject, "commits"));

        // Merge Details: From the closed PR
        long mergedTimestampMillis = getTimeStampMills(restClient.getString(pullRequestObject, "merged_at"));

        if (mergedTimestampMillis > 0) {
            if (createdTimestampMillis > 0) {
                pull.setResolutiontime((mergedTimestampMillis - createdTimestampMillis));
            }
            pull.setScmCommitTimestamp(mergedTimestampMillis);
            pull.setMergedAt(mergedTimestampMillis);
            String mergeSha = restClient.getString(pullRequestObject, "merge_commit_sha");
            pull.setScmRevisionNumber(mergeSha);
            pull.setScmMergeEventRevisionNumber(mergeSha);
            Object mergedBy = restClient.getAsObject(pullRequestObject,"merged_by");
            pull.setMergeAuthor(restClient.getString(mergedBy, "login"));
            pull.setMergeAuthorLDAPDN(restClient.getString(mergedBy, "ldap_dn"));
        }

        setCollectorItemId(pull);

        return pull;
    }

    protected void setCollectorItemId (GitRequest pull) throws MalformedURLException, HygieiaException {
        long start = System.currentTimeMillis();

        GitRequest existingPR
                = gitRequestRepository.findByScmUrlIgnoreCaseAndScmBranchIgnoreCaseAndNumberAndRequestTypeIgnoreCase(pull.getScmUrl(), pull.getScmBranch(), pull.getNumber(), "pull");

        if (existingPR != null) {
            pull.setId(existingPR.getId());
            pull.setCollectorItemId(existingPR.getCollectorItemId());
            CollectorItem collectorItem = collectorService.getCollectorItem(existingPR.getCollectorItemId());
            collectorItem.setEnabled(true);
            collectorItem.setPushed(true);
            collectorItemRepository.save(collectorItem);
        } else {
            GitHubParsed gitHubParsed = new GitHubParsed(pull.getScmUrl());
            CollectorItem collectorItem = getCollectorItem(gitHubParsed.getUrl(), pull.getScmBranch());
            pull.setCollectorItemId(collectorItem.getId());
        }

        long end = System.currentTimeMillis();

        LOG.debug("Time to make gitRequestRepository call to create the collector item = "+(end-start));
    }

    protected void updateGitRequestWithGraphQLData(GitRequest pull, String repoUrl,
                                                   String branch, JSONObject prData,
                                                   String token)  {
        LOG.debug("prData = "+prData.toJSONString());

        Object repoObject = restClient.getAsObject(prData, "repository");
        if (repoObject == null) {
            LOG.info("No Repository Data Available For "+repoUrl+" ; Branch "+branch+". Returning ...");
            return;
        }

        Object pullRequestObject = restClient.getAsObject(repoObject, "pullRequest");

        if (pullRequestObject == null) {
            LOG.info("No Pull Request Data Available For "+repoUrl+" ; Branch "+branch+". Returning ...");
            return;
        }

        if (pull.getMergedAt() > 0) {
            Object commitsObject = restClient.getAsObject(pullRequestObject, "commits");
            pull.setNumberOfChanges(restClient.getInteger(commitsObject, "totalCount"));

            List<Commit> prCommits = getPRCommits(repoUrl, commitsObject, pull, token);
            pull.setCommits(prCommits);

            Object commentsObject = restClient.getAsObject(pullRequestObject,"comments");
            List<Comment> comments = getComments(repoUrl, commentsObject, token);
            pull.setComments(comments);

            Object reviewsObject = restClient.getAsObject(pullRequestObject,"reviews");
            List<Review> reviews = getReviews(repoUrl, reviewsObject, token);
            pull.setReviews(reviews);
        }
    }

    protected List<Review> getReviews(String repoUrl, Object reviewObject, String token) throws RestClientException {
        List<Review> reviews = new ArrayList<>();

        if (reviewObject == null) { return reviews; }

        JSONArray nodes = (JSONArray) restClient.getAsObject(reviewObject, "nodes");

        if (CollectionUtils.isEmpty(nodes)) { return reviews; }

        for (Object n : nodes) {
            JSONObject node = (JSONObject) n;
            Review review = new Review();
            review.setState(restClient.getString(node, "state"));
            review.setBody(restClient.getString(node, "bodyText"));
            JSONObject authorObj = (JSONObject) node.get("author");
            review.setAuthor(restClient.getString(authorObj, "login"));
            String key = repoUrl+review.getAuthor();
            String authorLDAPDN = ldapDNMap.get(key);
            review.setAuthorLDAPDN(authorLDAPDN);
            if (StringUtils.isEmpty(authorLDAPDN)) {
                long start = System.currentTimeMillis();

                authorLDAPDN = getLDAPDN(repoUrl, review.getAuthor(), token);

                long end = System.currentTimeMillis();
                LOG.info("Time to make the LDAP call = "+(end-start));

                if (!StringUtils.isEmpty(authorLDAPDN)) {
                    ldapDNMap.put(key, authorLDAPDN);
                    review.setAuthorLDAPDN(authorLDAPDN);
                }
            }
            review.setCreatedAt(getTimeStampMills(restClient.getString(node, "createdAt")));
            review.setUpdatedAt(getTimeStampMills(restClient.getString(node, "updatedAt")));
            reviews.add(review);
        }

        return reviews;
    }

    protected List<Comment> getComments(String repoUrl, Object commentsObject, String token) throws RestClientException {
        List<Comment> comments = new ArrayList<>();
        if (commentsObject == null) {
            return comments;
        }
        JSONArray nodes = (JSONArray) restClient.getAsObject(commentsObject, "nodes");
        if (CollectionUtils.isEmpty(nodes)) { return comments; }

        for (Object n : nodes) {
            JSONObject node = (JSONObject) n;
            Comment comment = new Comment();
            comment.setBody(restClient.getString(node, "bodyText"));
            comment.setUser(restClient.getString((JSONObject) node.get("author"), "login"));
            String key = repoUrl+comment.getUser();
            String userLDAP = ldapDNMap.get(key);
            comment.setUserLDAPDN(userLDAP);
            if (StringUtils.isEmpty(userLDAP)) {
                long start = System.currentTimeMillis();

                userLDAP = getLDAPDN(repoUrl, comment.getUser(), token);

                long end = System.currentTimeMillis();
                LOG.debug("Time to make the LDAP call = "+(end-start));

                if (!StringUtils.isEmpty(userLDAP)) {
                    ldapDNMap.put(key, userLDAP);
                    comment.setUserLDAPDN(userLDAP);
                }
            }
            comment.setCreatedAt(getTimeStampMills(restClient.getString(node, "createdAt")));
            comment.setUpdatedAt(getTimeStampMills(restClient.getString(node, "updatedAt")));
            comment.setStatus(restClient.getString(node, "state"));
            comments.add(comment);
        }

        return comments;
    }

    protected List<Commit> getPRCommits(String repoUrl, Object commitsObject, GitRequest pull, String token) {
        List<Commit> prCommits = new ArrayList<>();

        if (commitsObject == null) { return prCommits; }

        String prHeadSha = pull.getHeadSha();

        JSONArray nodes = (JSONArray) restClient.getAsObject(commitsObject, "nodes");

        if (CollectionUtils.isEmpty(nodes)) { return prCommits; }

        JSONObject lastCommitStatusObject = null;
        long lastCommitTime = 0L;
        for (Object n : nodes) {
            JSONObject c = (JSONObject) n;
            JSONObject commit = (JSONObject) c.get("commit");
            String commitOid = restClient.getString(commit, "oid");

            Commit newCommit = new Commit();
            newCommit.setScmRevisionNumber(commitOid);
            newCommit.setScmCommitLog(restClient.getString(commit, "message"));
            JSONObject author = (JSONObject) commit.get("author");
            JSONObject authorUserJSON = (JSONObject) author.get("user");
            newCommit.setScmAuthor(restClient.getString(author, "name"));
            newCommit.setScmAuthorLogin((authorUserJSON == null) ? "unknown" : restClient.getString(authorUserJSON, "login"));

            if (!"unknown".equalsIgnoreCase(newCommit.getScmAuthorLogin())) {
                String key = repoUrl+newCommit.getScmAuthorLogin();
                String authorLDAPDN = ldapDNMap.get(key);
                newCommit.setScmAuthorLDAPDN(authorLDAPDN);
                if (StringUtils.isEmpty(authorLDAPDN)) {
                    long start = System.currentTimeMillis();

                    authorLDAPDN = getLDAPDN(repoUrl, newCommit.getScmAuthorLogin(), token);

                    long end = System.currentTimeMillis();
                    LOG.debug("Time to make the LDAP call = "+(end-start));

                    if (!StringUtils.isEmpty(authorLDAPDN)) {
                        ldapDNMap.put(key, authorLDAPDN);
                        newCommit.setScmAuthorLDAPDN(authorLDAPDN);
                    }
                }
            }
            newCommit.setScmCommitTimestamp(getTimeStampMills(restClient.getString(author, "date")));
            JSONObject statusObj = (JSONObject) commit.get("status");

            if (statusObj != null) {
                if (lastCommitTime <= newCommit.getScmCommitTimestamp()) {
                    lastCommitTime = newCommit.getScmCommitTimestamp();
                    lastCommitStatusObject = statusObj;
                }
                if (Objects.equals(newCommit.getScmRevisionNumber(), prHeadSha)) {
                    List<CommitStatus> commitStatuses = getCommitStatuses(statusObj);
                    List<CommitStatus> existingCommitStatusList = pull.getCommitStatuses();
                    if (!CollectionUtils.isEmpty(commitStatuses) && !CollectionUtils.isEmpty(existingCommitStatusList)) {
                        existingCommitStatusList.addAll(commitStatuses);
                    } else {
                        pull.setCommitStatuses(commitStatuses);
                    }
                }
            }

            // Relies mostly on an open pr to find commits from other repos, branches in the database.
            updateMatchingCommitsInDb(newCommit, pull);

            prCommits.add(newCommit);
        }

        if (StringUtils.isEmpty(prHeadSha) || CollectionUtils.isEmpty(pull.getCommitStatuses())) {
            List<CommitStatus> commitStatuses = getCommitStatuses(lastCommitStatusObject);
            List<CommitStatus> existingCommitStatusList = pull.getCommitStatuses();
            if (!CollectionUtils.isEmpty(commitStatuses) && !CollectionUtils.isEmpty(existingCommitStatusList)) {
                existingCommitStatusList.addAll(commitStatuses);
            } else {
                pull.setCommitStatuses(commitStatuses);
            }
        }

        return prCommits;
    }

    protected void updateMatchingCommitsInDb(Commit commit, GitRequest pull) {
        long start = System.currentTimeMillis();

        List<Commit> commitsInDb
                = commitRepository.findAllByScmRevisionNumberAndScmAuthorIgnoreCaseAndScmCommitLogAndScmCommitTimestamp(commit.getScmRevisionNumber(), commit.getScmAuthor(), commit.getScmCommitLog(), commit.getScmCommitTimestamp());

        Optional.ofNullable(commitsInDb)
            .orElseGet(Collections::emptyList)
            .forEach(commitInDb -> {
                commitInDb.setPullNumber(pull.getNumber());
                commitRepository.save(commitInDb);
            });

        long end = System.currentTimeMillis();

        LOG.debug("Time to make commitRepository call = "+(end-start));
    }

    protected List<CommitStatus> getCommitStatuses(JSONObject statusObject) throws RestClientException {
        Map<String, CommitStatus> statuses = new HashMap<>();

        if (statusObject == null) { return new ArrayList<>(); }

        JSONArray contexts = (JSONArray) statusObject.get("contexts");

        if (CollectionUtils.isEmpty(contexts)) { return new ArrayList<>(); }

        for (Object ctx : contexts) {
            String ctxStr = restClient.getString((JSONObject) ctx, "context");
            if ((ctxStr != null) && !statuses.containsKey(ctxStr)) {
                CommitStatus status = new CommitStatus();
                status.setContext(ctxStr);
                status.setDescription(restClient.getString((JSONObject) ctx, "description"));
                status.setState(restClient.getString((JSONObject) ctx, "state"));
                statuses.put(ctxStr, status);
            }
        }

        return new ArrayList<>(statuses.values());
    }

    private long getTimeStampMills(String dateTime) {
        return StringUtils.isEmpty(dateTime) ? 0 : new DateTime(dateTime).getMillis();
    }
}