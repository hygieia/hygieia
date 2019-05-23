package com.capitalone.dashboard.collector;

import com.capitalone.dashboard.misc.HygieiaException;
import com.capitalone.dashboard.model.Comment;
import com.capitalone.dashboard.model.Commit;
import com.capitalone.dashboard.model.CommitStatus;
import com.capitalone.dashboard.model.CommitType;
import com.capitalone.dashboard.model.GitHubParsed;
import com.capitalone.dashboard.model.GitHubRepo;
import com.capitalone.dashboard.model.GitRequest;
import com.capitalone.dashboard.model.Review;
import com.capitalone.dashboard.util.Encryption;
import com.capitalone.dashboard.util.EncryptionException;
import com.capitalone.dashboard.util.Supplier;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
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

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.regex.Pattern;

/**
 * GitHubClient implementation that uses SVNKit to fetch information about
 * Subversion repositories.
 */

@Component
public class DefaultGitHubClient implements GitHubClient {
    private static final Log LOG = LogFactory.getLog(DefaultGitHubClient.class);

    private final GitHubSettings settings;

    private final RestOperations restOperations;

    private static final int FIRST_RUN_HISTORY_DEFAULT = 14;

    @Autowired
    public DefaultGitHubClient(GitHubSettings settings,
                               Supplier<RestOperations> restOperationsSupplier) {
        this.settings = settings;
        this.restOperations = restOperationsSupplier.get();
    }

    /**
     * Gets commits for a given repo
     * @param repo
     * @param firstRun
     * @return list of commits
     * @throws RestClientException
     * @throws MalformedURLException
     * @throws HygieiaException
     */
    @Override
    public List<Commit> getCommits(GitHubRepo repo, boolean firstRun, List<Pattern> commitExclusionPatterns) throws RestClientException, MalformedURLException, HygieiaException {

        List<Commit> commits = new ArrayList<>();

        // format URL
        String repoUrl = (String) repo.getOptions().get("url");
        GitHubParsed gitHubParsed = new GitHubParsed(repoUrl);
        String apiUrl = gitHubParsed.getApiUrl();

        String queryUrl = apiUrl.concat("/commits?sha=" + repo.getBranch()
                + "&since=" + getTimeForApi(getRunDate(repo, firstRun)));
        String decryptedPassword = decryptString(repo.getPassword(), settings.getKey());
        String personalAccessToken = (String) repo.getOptions().get("personalAccessToken");
        String decryptedPersonalAccessToken = decryptString(personalAccessToken, settings.getKey());
        boolean lastPage = false;
        String queryUrlPage = queryUrl;
        while (!lastPage) {
            LOG.info("Executing " + queryUrlPage);
            ResponseEntity<String> response = makeRestCall(queryUrlPage, repo.getUserId(), decryptedPassword,decryptedPersonalAccessToken);
            JSONArray jsonArray = parseAsArray(response);
            for (Object item : jsonArray) {
                JSONObject jsonObject = (JSONObject) item;
                String sha = str(jsonObject, "sha");
                JSONObject commitObject = (JSONObject) jsonObject.get("commit");
                JSONObject commitAuthorObject = (JSONObject) commitObject.get("author");
                String message = str(commitObject, "message");
                String author = str(commitAuthorObject, "name");
                long timestamp = new DateTime(str(commitAuthorObject, "date"))
                        .getMillis();
                JSONObject authorObject = (JSONObject) jsonObject.get("author");
                String authorLogin = "";
                if (authorObject != null) {
                    authorLogin = str(authorObject, "login");
                }
                JSONArray parents = (JSONArray) jsonObject.get("parents");
                List<String> parentShas = new ArrayList<>();
                if (parents != null) {
                    for (Object parentObj : parents) {
                        parentShas.add(str((JSONObject) parentObj, "sha"));
                    }
                }

                Commit commit = new Commit();
                commit.setTimestamp(System.currentTimeMillis());
                commit.setScmUrl(repo.getRepoUrl());
                commit.setScmBranch(repo.getBranch());
                commit.setScmRevisionNumber(sha);
                commit.setScmParentRevisionNumbers(parentShas);
                commit.setScmAuthor(author);
                commit.setScmAuthorLogin(authorLogin);
                commit.setScmCommitLog(message);
                commit.setScmCommitTimestamp(timestamp);
                commit.setNumberOfChanges(1);
                commit.setType(getCommitType(CollectionUtils.size(parents), message, commitExclusionPatterns));
                commits.add(commit);
            }
            if (CollectionUtils.isEmpty(jsonArray)) {
                lastPage = true;
            } else {
                if (isThisLastPage(response)) {
                    lastPage = true;
                } else {
                    lastPage = false;
                    queryUrlPage = getNextPageUrl(response);
                }
            }
        }
        return commits;
    }

    private CommitType getCommitType(int parentSize, String commitMessage, List<Pattern> commitExclusionPatterns) {
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


    /**
     * Gets pulls for a given repo
     * @param repo
     * @param status
     * @param firstRun
     * @param prMap
     * @return list of pull request objects
     * @throws MalformedURLException
     * @throws HygieiaException
     */
    @Override
    @SuppressWarnings({"PMD.NPathComplexity", "PMD.ExcessiveMethodLength", "PMD.NcssMethodCount"}) // agreed, fixme
    public List<GitRequest> getPulls(GitHubRepo repo, String status, boolean firstRun, Map<Long, String> prMap) throws MalformedURLException, HygieiaException {

        List<GitRequest> pulls = new ArrayList<>();
        String decryptedPassword = decryptString(repo.getPassword(), settings.getKey());
        GitHubParsed gitHubParsed = new GitHubParsed((String) repo.getOptions().get("url"));
        String branch = (repo.getBranch() != null) ? repo.getBranch() : "master";
        String personalAccessToken = (String) repo.getOptions().get("personalAccessToken");
        String decryptedPersonalAccessToken = decryptString(personalAccessToken, settings.getKey());

        String pageUrl = gitHubParsed.getApiUrl().concat("/pulls?state=" + status + "&base=" + branch + "&sort=updated&direction=desc");

        boolean lastPage = false;
        boolean stop = false;
        String queryUrlPage = pageUrl;

        while (!lastPage && !stop) {
            LOG.info("Executing [" + queryUrlPage);
            ResponseEntity<String> response = makeRestCall(queryUrlPage, repo.getUserId(), decryptedPassword,decryptedPersonalAccessToken);
            JSONArray jsonArray = parseAsArray(response);
            for (Object item : jsonArray) {
                JSONObject jsonObject = (JSONObject) item;
                String message = str(jsonObject, "title");
                String number = str(jsonObject, "number");
                LOG.info("pr " + number + " " + message);
                String sha = str(jsonObject, "merge_commit_sha");

                JSONObject userObject = (JSONObject) jsonObject.get("user");
                String name = str(userObject, "login");
                String created = str(jsonObject, "created_at");
                String merged = str(jsonObject, "merged_at");
                String closed = str(jsonObject, "closed_at");
                String  updated = str(jsonObject, "updated_at");
                long createdTimestamp = new DateTime(created).getMillis();
                String commentsUrl = str(jsonObject, "comments_url");
                String commitStatusesUrl = str(jsonObject, "statuses_url");
                String reviewCommentsUrl = str(jsonObject, "review_comments_url");
                String reviewsUrl = str(jsonObject, "url") + "/reviews";

                GitRequest pull = new GitRequest();

                if (merged != null && merged.length() >= 10) {
                    long mergedTimestamp = new DateTime(merged).getMillis();
                    pull.setScmCommitTimestamp(mergedTimestamp);
                    pull.setResolutiontime((mergedTimestamp - createdTimestamp) / (24 * 3600000));
                }
                pull.setUserId(name);
                pull.setScmUrl(repo.getRepoUrl());
                pull.setScmBranch(branch);
                pull.setTimestamp(createdTimestamp);
                pull.setScmRevisionNumber(sha);
                pull.setScmCommitLog(message);
                pull.setCreatedAt(createdTimestamp);
                pull.setClosedAt(new DateTime(closed).getMillis());
                pull.setMergedAt(new DateTime(merged).getMillis());
                pull.setUpdatedAt(new DateTime(updated).getMillis());
                pull.setNumber(number);
                pull.setRequestType("pull");
                pull.setState("open");
                if (merged != null) {
                    pull.setState("merged");
                } else if (closed != null) {
                    pull.setState("closed");
                }
                pull.setOrgName(gitHubParsed.getOrgName());
                pull.setRepoName(gitHubParsed.getRepoName());

                JSONObject headObject = (JSONObject) jsonObject.get("head");
                if (headObject != null) {
                    String headSha = str(headObject, "sha");
                    pull.setHeadSha(headSha);
                    pull.setSourceBranch(str(headObject, "ref"));
                    JSONObject headRepoObject = (JSONObject) headObject.get("repo");
                    if (headRepoObject != null) {
                        pull.setSourceRepo(str(headRepoObject, "full_name"));
                    }
                }

                JSONObject baseObject = (JSONObject) jsonObject.get("base");
                if (baseObject != null) {
                    pull.setBaseSha(str(baseObject, "sha"));
                    pull.setTargetBranch(str(baseObject, "ref"));
                    JSONObject baseRepoObject = (JSONObject) baseObject.get("repo");
                    if (baseRepoObject != null) {
                        pull.setTargetRepo(str(baseRepoObject, "full_name"));
                    }
                }

                pull.setCommentsUrl(commentsUrl);
                List<Comment> comments = getComments(commentsUrl, repo);
                pull.setComments(comments);
                pull.setReviewCommentsUrl(reviewCommentsUrl);
                List<CommitStatus> commitStatuses = getCommitStatuses(commitStatusesUrl, repo);
                pull.setCommitStatuses(commitStatuses);
                List<Review> reviews = getReviews(reviewsUrl, repo);
                pull.setReviews(reviews);
                pull.setCommitStatuses(commitStatuses);
                pulls.add(pull);
                stop = (!MapUtils.isEmpty(prMap) && prMap.get(pull.getUpdatedAt()) != null) && (prMap.get(pull.getUpdatedAt()).equals(pull.getNumber()));
                if (stop) {
                    break;
                }
            }
            if (CollectionUtils.isEmpty(jsonArray)) {
                lastPage = true;
            } else {
                if (isThisLastPage(response)) {
                    lastPage = true;
                } else {
                    lastPage = false;
                    queryUrlPage = getNextPageUrl(response);
                }
            }
        }
        return pulls;
    }

    /**
     * Gets issues for a repo
     * @param repo
     * @param firstRun
     * @return list of issues
     * @throws MalformedURLException
     * @throws HygieiaException
     */
    @Override
    public List<GitRequest> getIssues(GitHubRepo repo, boolean firstRun) throws
            MalformedURLException, HygieiaException {

        List<GitRequest> issues = new ArrayList<>();

        GitHubParsed gitHubParsed = new GitHubParsed((String) repo.getOptions().get("url"));
        String apiUrl = gitHubParsed.getApiUrl();
        // decrypt password
        String decryptedPassword = decryptString(repo.getPassword(), settings.getKey());
        String queryUrl = apiUrl.concat("/issues?state=all&since=" + getTimeForApi(getRunDate(repo, firstRun)));
        String personalAccessToken = (String) repo.getOptions().get("personalAccessToken");
        String decryptedPersonalAccessToken = decryptString(personalAccessToken, settings.getKey());
        boolean lastPage = false;
        String queryUrlPage = queryUrl;
        while (!lastPage) {
            LOG.info("Executing " + queryUrlPage);
            ResponseEntity<String> response = makeRestCall(queryUrlPage, repo.getUserId(), decryptedPassword,decryptedPersonalAccessToken);
            JSONArray jsonArray = parseAsArray(response);
            for (Object item : jsonArray) {
                JSONObject jsonObject = (JSONObject) item;
                //pull requests are also issues
                if (jsonObject.get("pull_request") != null) {
                    continue;
                }
                String message = str(jsonObject, "title");
                String number = str(jsonObject, "number");

                JSONObject userObject = (JSONObject) jsonObject.get("user");
                String name = str(userObject, "login");
                String created = str(jsonObject, "created_at");
                String closed = str(jsonObject, "closed_at");
                long createdTimestamp = new DateTime(created).getMillis();
                GitRequest issue = new GitRequest();
                if (closed != null && closed.length() >= 10) {
                    long mergedTimestamp = new DateTime(closed).getMillis();
                    issue.setScmCommitTimestamp(mergedTimestamp);
                    issue.setResolutiontime((mergedTimestamp - createdTimestamp) / (24 * 3600000));
                }
                issue.setUserId(name);
                issue.setScmUrl(repo.getRepoUrl());
                issue.setTimestamp(createdTimestamp);
                issue.setScmRevisionNumber(number);
                issue.setScmCommitLog(message);
                issue.setCreatedAt(createdTimestamp);
                issue.setClosedAt(new DateTime(closed).getMillis());
                issue.setNumber(number);
                issue.setRequestType("issue");
                if (closed != null) {
                    issue.setState("closed");
                } else {
                    issue.setState("open");
                }
                issue.setOrgName(gitHubParsed.getOrgName());
                issue.setRepoName(gitHubParsed.getRepoName());
                issues.add(issue);
            }

            if (CollectionUtils.isEmpty(jsonArray)) {
                lastPage = true;
            } else {
                if (isThisLastPage(response)) {
                    lastPage = true;
                } else {
                    lastPage = false;
                    queryUrlPage = getNextPageUrl(response);
                }
            }
        }
        return issues;
    }


    /**
     * Get comments from the given comment url
     * @param commentsUrl
     * @param repo
     * @return
     * @throws RestClientException
     */
    public List<Comment> getComments(String commentsUrl, GitHubRepo repo) throws RestClientException {

        List<Comment> comments = new ArrayList<>();

        // decrypt password
        String decryptedPassword = decryptString(repo.getPassword(), settings.getKey());

        String personalAccessToken = (String) repo.getOptions().get("personalAccessToken");
        String decryptedPersonalAccessToken = decryptString(personalAccessToken, settings.getKey());

        boolean lastPage = false;
        String queryUrlPage = commentsUrl;
        while (!lastPage) {
            ResponseEntity<String> response = makeRestCall(queryUrlPage, repo.getUserId(), decryptedPassword,decryptedPersonalAccessToken);
            JSONArray jsonArray = parseAsArray(response);
            for (Object item : jsonArray) {
                JSONObject jsonObject = (JSONObject) item;

                Comment comment = new Comment();
                JSONObject userJsonObj = (JSONObject) jsonObject.get("user");
                comment.setUser((String) userJsonObj.get("login"));
                long crt = new DateTime(str(jsonObject, "created_at")).getMillis();
                comment.setCreatedAt(crt);
                long upd = new DateTime(str(jsonObject, "updated_at")).getMillis();
                comment.setUpdatedAt(upd);
                comment.setBody(str(jsonObject, "body"));
                comments.add(comment);
            }
            if (CollectionUtils.isEmpty(jsonArray)) {
                lastPage = true;
            } else {
                if (isThisLastPage(response)) {
                    lastPage = true;
                } else {
                    lastPage = false;
                    queryUrlPage = getNextPageUrl(response);
                }
            }
        }
        return comments;
    }

    /**
     * Get commit statuses from the given commit status url
     * Retrieve the most recent status for each unique context.
     *
     * See https://developer.github.com/v3/repos/statuses/#list-statuses-for-a-specific-ref
     * and https://developer.github.com/v3/repos/statuses/#get-the-combined-status-for-a-specific-ref
     *
     * @param statusUrl
     * @param repo
     * @return
     * @throws RestClientException
     */
    public List<CommitStatus> getCommitStatuses(String statusUrl, GitHubRepo repo) throws RestClientException {

        Map<String, CommitStatus> statuses = new HashMap<>();

        // decrypt password
        String decryptedPassword = decryptString(repo.getPassword(), settings.getKey());
        String personalAccessToken = (String) repo.getOptions().get("personalAccessToken");
        String decryptedPersonalAccessToken = decryptString(personalAccessToken, settings.getKey());
        boolean lastPage = false;
        String queryUrlPage = statusUrl;
        while (!lastPage) {
            ResponseEntity<String> response = makeRestCall(queryUrlPage, repo.getUserId(), decryptedPassword,decryptedPersonalAccessToken);
            JSONArray jsonArray = parseAsArray(response);
            for (Object item : jsonArray) {
                JSONObject jsonObject = (JSONObject) item;

                String context = str(jsonObject, "context");
                if ((context != null) && !statuses.containsKey(context)) {
                    CommitStatus status = new CommitStatus();
                    status.setContext(context);
                    status.setDescription(str(jsonObject, "description"));
                    status.setState(str(jsonObject, "state"));
                    statuses.put(context, status);
                }
            }
            if (CollectionUtils.isEmpty(jsonArray)) {
                lastPage = true;
            } else {
                if (isThisLastPage(response)) {
                    lastPage = true;
                } else {
                    lastPage = false;
                    queryUrlPage = getNextPageUrl(response);
                }
            }
        }
        return new ArrayList<>(statuses.values());
    }

    /**
     * Get reviews from the given reviews url
     * @param reviewsUrl
     * @param repo
     * @return
     * @throws RestClientException
     */
    public List<Review> getReviews(String reviewsUrl, GitHubRepo repo) throws RestClientException {

        List<Review> reviews = new ArrayList<>();

        // decrypt password
        String decryptedPassword = decryptString(repo.getPassword(), settings.getKey());
        String personalAccessToken = (String) repo.getOptions().get("personalAccessToken");
        String decryptedPersonalAccessToken = decryptString(personalAccessToken, settings.getKey());
        boolean lastPage = false;
        String queryUrlPage = reviewsUrl;
        while (!lastPage) {
            ResponseEntity<String> response = makeRestCall(queryUrlPage, repo.getUserId(), decryptedPassword,decryptedPersonalAccessToken);
            JSONArray jsonArray = parseAsArray(response);
            for (Object item : jsonArray) {
                JSONObject jsonObject = (JSONObject) item;

                Review review = new Review();
                review.setState(str(jsonObject, "state"));
                review.setBody(str(jsonObject, "body"));
//                review.setId(asInt(jsonObject, "id"));
                reviews.add(review);
            }
            if (CollectionUtils.isEmpty(jsonArray)) {
                lastPage = true;
            } else {
                if (isThisLastPage(response)) {
                    lastPage = true;
                } else {
                    lastPage = false;
                    queryUrlPage = getNextPageUrl(response);
                }
            }
        }
        return reviews;
    }

    // Utilities

    /**
     * See if it is the last page: obtained from the response header
     * @param response
     * @return
     */
    private boolean isThisLastPage(ResponseEntity<String> response) {
        HttpHeaders header = response.getHeaders();
        List<String> link = header.get("Link");
        if (link == null || link.isEmpty()) {
            return true;
        } else {
            for (String l : link) {
                if (l.contains("rel=\"next\"")) {
                    return false;
                }
            }
        }
        return true;
    }

    private String getNextPageUrl(ResponseEntity<String> response) {
        String nextPageUrl = "";
        HttpHeaders header = response.getHeaders();
        List<String> link = header.get("Link");
        if (link == null || link.isEmpty()) {
            return nextPageUrl;
        } else {
            for (String l : link) {
                if (l.contains("rel=\"next\"")) {
                    String[] parts = l.split(",");
                    if (parts != null && parts.length > 0) {
                        for(int i=0; i<parts.length; i++) {
                            if (parts[i].contains("rel=\"next\"")) {
                                nextPageUrl = parts[i].split(";")[0];
                                nextPageUrl = nextPageUrl.replaceFirst("<","");
                                nextPageUrl = nextPageUrl.replaceFirst(">","").trim();
                                // Github Link headers for 'next' and 'last' are URL Encoded
                                String decodedPageUrl;
                                try {
                                    decodedPageUrl = URLDecoder.decode(nextPageUrl, StandardCharsets.UTF_8.name());
                                } catch (UnsupportedEncodingException e) {
                                    decodedPageUrl = URLDecoder.decode(nextPageUrl);
                                }
                                return decodedPageUrl;
                            }
                        }
                    }
                }
            }
        }
        return nextPageUrl;
    }

    /**
     * Checks rate limit
     * @param response
     * @return boolean
     */
    private boolean isRateLimitReached(ResponseEntity<String> response) {
        HttpHeaders header = response.getHeaders();
        List<String> limit = header.get("X-RateLimit-Remaining");
        boolean rateLimitReached =  CollectionUtils.isEmpty(limit) ? false : Integer.valueOf(limit.get(0)) < settings.getRateLimitThreshold();
        if (rateLimitReached) {
            LOG.error("Github rate limit reached. Threshold =" + settings.getRateLimitThreshold() + ". Current remaining ="+Integer.valueOf(limit.get(0)));
        }
        return rateLimitReached;
    }

    private ResponseEntity<String> makeRestCall(String url, String userId,
                                                String password,String personalAccessToken) {
        // Basic Auth only.
        if (!"".equals(userId) && !"".equals(password)) {
            return restOperations.exchange(url, HttpMethod.GET, new HttpEntity<>(createHeaders(userId, password)), String.class);
        } else if ((personalAccessToken!=null && !"".equals(personalAccessToken)) ) {
            return restOperations.exchange(url, HttpMethod.GET,new HttpEntity<>(createHeaders(personalAccessToken)),String.class);
        } else if (settings.getPersonalAccessToken() != null && !"".equals(settings.getPersonalAccessToken())){
            return restOperations.exchange(url, HttpMethod.GET, new HttpEntity<>(createHeaders(settings.getPersonalAccessToken())), String.class);
        }else {
            return restOperations.exchange(url, HttpMethod.GET, null, String.class);
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

    private JSONArray parseAsArray(ResponseEntity<String> response) {
        try {
            return (JSONArray) new JSONParser().parse(response.getBody());
        } catch (ParseException pe) {
            LOG.error(pe.getMessage());
        }
        return new JSONArray();
    }

    private JSONObject parseAsObject(ResponseEntity<String> response) {
        try {
            return (JSONObject) new JSONParser().parse(response.getBody());
        } catch (ParseException pe) {
            LOG.error(pe.getMessage());
        }
        return new JSONObject();
    }

    private int asInt(JSONObject json, String key) {
        String val = str(json, key);
        try {
            if (val != null) {
                return Integer.parseInt(val);
            }
        } catch (NumberFormatException ex) {
            LOG.error(ex.getMessage());
        }
        return 0;
    }

    private String str(JSONObject json, String key) {
        Object value = json.get(key);
        return value == null ? null : value.toString();
    }

    /**
     * Get run date based off of firstRun boolean
     * @param repo
     * @param firstRun
     * @return
     */
    private Date getRunDate(GitHubRepo repo, boolean firstRun) {
        if (firstRun) {
            int firstRunDaysHistory = settings.getFirstRunHistoryDays();
            if (firstRunDaysHistory > 0) {
                return getDate(new Date(), -firstRunDaysHistory, 0);
            } else {
                return getDate(new Date(), -FIRST_RUN_HISTORY_DEFAULT, 0);
            }
        } else {
            return getDate(new Date(repo.getLastUpdated()), 0, -10);
        }
    }


    /**
     * Date utility
     * @param dateInstance
     * @param offsetDays
     * @param offsetMinutes
     * @return
     */
    private Date getDate(Date dateInstance, int offsetDays, int offsetMinutes) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(dateInstance);
        cal.add(Calendar.DATE, offsetDays);
        cal.add(Calendar.MINUTE, offsetMinutes);
        return cal.getTime();
    }

    /**
     * Decrypt string
     * @param string
     * @param key
     * @return String
     */
    public static String decryptString(String string, String key) {
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


    /**
     * Format date the way Github api wants
     * @param dt
     * @return String
     */

    private static String getTimeForApi (Date dt) {
        Calendar calendar = new GregorianCalendar();
        TimeZone timeZone = calendar.getTimeZone();
        Calendar cal = Calendar.getInstance(timeZone);
        cal.setTime(dt);
        return String.format("%tFT%<tRZ", cal);
    }
}

// X-RateLimit-Remaining