package com.capitalone.dashboard.collector;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.capitalone.dashboard.model.Commit;
import com.capitalone.dashboard.model.CommitType;
import com.capitalone.dashboard.model.GitRepo;
import com.capitalone.dashboard.model.pullrequest.PullRequest;
import com.capitalone.dashboard.util.Encryption;
import com.capitalone.dashboard.util.EncryptionException;
import com.capitalone.dashboard.util.Supplier;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.client.utils.URIBuilder;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestOperations;

/**
 * Implementation of a git client to connect to an Atlassian Bitbucket <i>Server</i> product. 
 * <p>
 * Note about naming scheme: Atlassian has two different Bitbucket products that use different
 * rest API's: Bitbucket <i>Cloud</i> (formerly known as Bitbucket) and Bitbucket <i>Server</i> (formerly known as Stash).
 * <p>
 * Rest API's:
 * <ul>
 * <li><b>Bitbucket Cloud:</b> https://confluence.atlassian.com/bitbucket/version-2-423626329.html</li>
 * <li><b>Bitbucket Server:</b> https://developer.atlassian.com/static/rest/stash/3.11.3/stash-rest.html</li>
 * </ul>
 * <b>
 * @see <a href="https://confluence.atlassian.com/bitbucketserver/bitbucket-rebrand-faq-779298912.html">Bitbucket rebrand FAQ</a>
 * @see <a href="https://github.com/capitalone/Hygieia/issues/609">Confusion on Stash/Bitbucket implementations #609</a>
 */
@Component("bitbucket-server")
@ConditionalOnProperty(prefix = "git", name = "product", havingValue = "server")
public class DefaultBitbucketServerClient implements GitClient {
    private static final Log LOG = LogFactory.getLog(DefaultBitbucketServerClient.class);

    private final GitSettings settings;

    private final RestOperations restOperations;

    @Autowired
    public DefaultBitbucketServerClient(GitSettings settings,
                                        Supplier<RestOperations> restOperationsSupplier) {
        this.settings = settings;
        this.restOperations = restOperationsSupplier.get();
    }

    @SuppressWarnings("PMD.NPathComplexity")
    @Override
    public List<Commit> getCommits(GitRepo repo, boolean firstRun) {
        List<Commit> commits = new ArrayList<>();
        URI queryUriPage = null;

        try {
            URI queryUri = buildUri((String) repo.getOptions().get("url"), repo.getBranch(), repo.getLastUpdateCommit(), "commit");
            if (LOG.isDebugEnabled()) {
                LOG.debug("Rest Url: " + queryUri);
            }

            // decrypt password
            String decryptedPassword = "";
            if (repo.getPassword() != null && !repo.getPassword().isEmpty()) {
                try {
                    decryptedPassword = Encryption.decryptString(repo.getPassword(), settings.getKey());
                } catch (EncryptionException e) {
                    LOG.error(e.getMessage());
                }
            }

            boolean lastPage = false;
            queryUriPage = queryUri;
            while (!lastPage) {
                ResponseEntity<String> response = makeRestCall(queryUriPage, repo.getUserId(), decryptedPassword);
                JSONObject jsonParentObject = paresAsObject(response);
                JSONArray jsonArray = (JSONArray) jsonParentObject.get("values");

                for (Object item : jsonArray) {
                    JSONObject jsonObject = (JSONObject) item;
                    String sha = str(jsonObject, "id");
                    JSONObject authorObject = (JSONObject) jsonObject.get("author");
                    String message = str(jsonObject, "message");
                    String author = str(authorObject, "name");
                    long timestamp = Long.valueOf(str(jsonObject, "authorTimestamp"));
                    JSONArray parents = (JSONArray) jsonObject.get("parents");
                    List<String> parentShas = new ArrayList<>();
                    if (parents != null) {
                        for (Object parentObj : parents) {
                            parentShas.add(str((JSONObject) parentObj, "id"));
                        }
                    }

                    Commit commit = new Commit();
                    commit.setTimestamp(System.currentTimeMillis());
                    commit.setScmUrl(repo.getRepoUrl());
                    commit.setScmBranch(repo.getBranch());
                    commit.setScmRevisionNumber(sha);
                    commit.setScmParentRevisionNumbers(parentShas);
                    commit.setScmAuthor(author);
                    commit.setScmCommitLog(message);
                    commit.setScmCommitTimestamp(timestamp);
                    commit.setType(parentShas.size() > 1 ? CommitType.Merge : CommitType.New);
                    commit.setNumberOfChanges(1);
                    commits.add(commit);
                }
                if (jsonArray == null || jsonArray.isEmpty()) {
                    lastPage = true;
                } else {
                    String isLastPage = str(jsonParentObject, "isLastPage");
                    lastPage = isLastPage == null || Boolean.valueOf(isLastPage);

                    String nextPageStart = str(jsonParentObject, "nextPageStart");
                    if (nextPageStart != null && !"null".equals(nextPageStart)) {
                        queryUriPage = new URIBuilder(queryUri).addParameter("start", nextPageStart).build();
                    }
                }
            }

            repo.setLastUpdated(System.currentTimeMillis());
        } catch (URISyntaxException e) {
            LOG.error("Invalid uri: " + e.getMessage());
        } catch (RestClientException re) {
            LOG.error("Failed to obtain commits from " + queryUriPage, re);
        }

        return commits;
    }

    @Override
    public List<PullRequest> getPullRequests(GitRepo repo, boolean firstRun) {
        List<PullRequest> pullRequests = new ArrayList<>();
        URI queryUriPage = null;

        try {
            URI queryUri = buildUri((String) repo.getOptions().get("url"), repo.getBranch(), repo.getLastUpdatePullRequest(), "pull-request");
            if (LOG.isDebugEnabled()) {
                LOG.debug("Rest Url: " + queryUri);
            }

            // decrypt password
            String decryptedPassword = "";
            if (repo.getPassword() != null && !repo.getPassword().isEmpty()) {
                try {
                    decryptedPassword = Encryption.decryptString(repo.getPassword(), settings.getKey());
                } catch (EncryptionException e) {
                    LOG.error(e.getMessage());
                }
            }

            boolean lastPage = false;
            queryUriPage = queryUri;

            while (!lastPage) {
                ResponseEntity<String> response = makeRestCall(queryUriPage, repo.getUserId(), decryptedPassword);
                JSONObject jsonParentObject = paresAsObject(response);
                JSONArray jsonArray = (JSONArray) jsonParentObject.get("values");

                ObjectMapper mapper = new ObjectMapper();
                pullRequests = mapper.readValue(jsonArray.toJSONString(), new TypeReference<List<PullRequest>>() {
                });

                if (jsonArray == null || jsonArray.isEmpty()) {
                    lastPage = true;
                } else {
                    String isLastPage = str(jsonParentObject, "isLastPage");
                    lastPage = isLastPage == null || Boolean.valueOf(isLastPage);

                    String nextPageStart = str(jsonParentObject, "nextPageStart");
                    if (nextPageStart != null && !"null".equals(nextPageStart)) {
                        queryUriPage = new URIBuilder(queryUri).addParameter("start", nextPageStart).build();
                    }
                }
            }


        } catch (URISyntaxException e) {
            LOG.error("Invalid uri " + repo.getOptions().get("uri") + " " + e.getMessage());
        } catch (RestClientException re) {
            LOG.error("Failed to obtain commits from " + queryUriPage, re);
        } catch (IOException e) {
            LOG.error("Error when parsing JSON while getting pull requests", e);
        }

        return pullRequests;
    }

    public List<Long> getMergedPullRequests(GitRepo repo) {
        List<Long> mergedPullRequests = new ArrayList<>();
        URI queryUriPage = null;

        try {
            URI queryUri = buildUri((String) repo.getOptions().get("url"), repo.getBranch(), repo.getLastUpdatePullRequest(), "merged-pull-request");
            if (LOG.isDebugEnabled()) {
                LOG.debug("Rest Url: " + queryUri);
            }

            // decrypt password
            String decryptedPassword = "";
            if (repo.getPassword() != null && !repo.getPassword().isEmpty()) {
                try {
                    decryptedPassword = Encryption.decryptString(repo.getPassword(), settings.getKey());
                } catch (EncryptionException e) {
                    LOG.error(e.getMessage());
                }
            }

            queryUriPage = queryUri;

            ResponseEntity<String> response = makeRestCall(queryUriPage, repo.getUserId(), decryptedPassword);
            JSONObject jsonParentObject = paresAsObject(response);
            JSONArray jsonArray = (JSONArray) jsonParentObject.get("values");

            mergedPullRequests = ((List<Object>) jsonArray).stream()
                    .map(item -> Long.valueOf(str((JSONObject) item, "id")))
                    .collect(Collectors.toList());

        } catch (URISyntaxException e) {
            LOG.error("Invalid uri " + repo.getOptions().get("uri") + " " + e.getMessage());
        }

        return mergedPullRequests;
    }

    // package for junit
    @SuppressWarnings({"PMD.NPathComplexity"})
    /*package*/ URI buildUri(final String rawUrl, final String branch, final String lastKnownCommit, final String action) throws URISyntaxException {
        URIBuilder builder = new URIBuilder();

        /*
         * Examples:
         *
         * ssh://git@company.com/project/repository.git
         * https://username@company.com/scm/project/repository.git
         * ssh://git@company.com/~username/repository.git
         * https://username@company.com/scm/~username/repository.git
         *
         */

        String repoUrlRaw = rawUrl;
        String repoUrlProcessed = repoUrlRaw;

        if (repoUrlProcessed.endsWith(".git")) {
            repoUrlProcessed = repoUrlProcessed.substring(0, repoUrlProcessed.lastIndexOf(".git"));
        }

        URI uri = URI.create(repoUrlProcessed.replaceAll(" ", "%20"));

        String host = uri.getHost();
        String scheme = "ssh".equalsIgnoreCase(uri.getScheme()) ? "https" : uri.getScheme();
        int port = uri.getPort();
        String path = uri.getPath();
        if ((path.startsWith("scm/") || path.startsWith("/scm")) && path.length() > 4) {
            path = path.substring(4);
        }
        if (path.length() > 0 && path.charAt(0) == '/') {
            path = path.substring(1, path.length());
        }

        String[] splitPath = path.split("/");
        String projectKey = "";
        String repositorySlug = "";

        if (splitPath.length > 1) {
            projectKey = splitPath[0];
            repositorySlug = path.substring(path.indexOf('/') + 1, path.length());
        } else {
            // Shouldn't get to this case
            projectKey = "";
            repositorySlug = path;
        }

        String apiPath = settings.getApi() != null ? settings.getApi() : "";

        if (apiPath.endsWith("/")) {
            apiPath = settings.getApi().substring(0, settings.getApi().length() - 1);
        }

        builder.setScheme(scheme);
        builder.setHost(host);
        if (port != -1) {
            builder.setPort(port);
        }

        switch (action) {
            case "commit":
                builder.setPath(apiPath + "/projects/" + projectKey + "/repos/" + repositorySlug + "/commits");
                if (branch == null || branch.length() == 0) {
                    builder.addParameter("until", "master");
                } else {
                    builder.addParameter("until", branch.replaceAll(" ", "%20"));
                }

                if (lastKnownCommit != null && lastKnownCommit.length() > 0) {
                    builder.addParameter("since", lastKnownCommit);
                }

                if (settings.getPageSize() > 0) {
                    builder.addParameter("limit", String.valueOf(settings.getPageSize()));
                }
                break;
            case "pull-request":
                builder.setPath(apiPath + "/projects/" + projectKey + "/repos/" + repositorySlug + "/pull-requests");
                break;
            case "merged-pull-request":
                builder.setPath(apiPath + "/projects/" + projectKey + "/repos/" + repositorySlug + "/pull-requests")
                        .addParameter("state", "MERGED")
                        .addParameter("limit", "50");
                break;
            default:
                LOG.error("unexisting action");
                break;
        }

        return builder.build();
    }

    private ResponseEntity<String> makeRestCall(URI uri, String userId,
                                                String password) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("GET " + uri);
        }
        LOG.info("GET " + uri);
        // Basic Auth only.
        if (!"".equals(userId) && !"".equals(password)) {
            return restOperations.exchange(uri, HttpMethod.GET,
                    new HttpEntity<>(createHeaders(userId, password)),
                    String.class);

        } else {
            return restOperations.exchange(uri, HttpMethod.GET, null,
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

    private JSONObject paresAsObject(ResponseEntity<String> response) {
        try {
            return (JSONObject) new JSONParser().parse(response.getBody());
        } catch (ParseException pe) {
            LOG.error(pe.getMessage());
        }
        return new JSONObject();
    }

    private String str(JSONObject json, String key) {
        Object value = json.get(key);
        return value == null ? null : value.toString();
    }

}