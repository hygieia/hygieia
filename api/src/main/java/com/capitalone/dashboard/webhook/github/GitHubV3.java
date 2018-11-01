package com.capitalone.dashboard.webhook.github;

import com.capitalone.dashboard.settings.ApiSettings;
import com.capitalone.dashboard.client.RestClient;
import com.capitalone.dashboard.model.webhook.github.GitHubParsed;
import com.capitalone.dashboard.misc.HygieiaException;
import com.capitalone.dashboard.model.Collector;
import com.capitalone.dashboard.model.CollectorItem;
import com.capitalone.dashboard.model.CollectorType;
import com.capitalone.dashboard.model.QCollectorItem;
import com.capitalone.dashboard.model.webhook.github.GitHubRepo;
import com.capitalone.dashboard.service.CollectorService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mysema.query.BooleanBuilder;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bson.types.ObjectId;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public abstract class GitHubV3 {
    private static final Log LOG = LogFactory.getLog(GitHubV3.class);

    private static final String REPO_URL = "url";
    private static final String BRANCH = "branch";
    private static final String USER_ID = "userID";
    private static final String PASSWORD = "password";
    private static final String TOKEN = "personalAccessToken";

    private final CollectorService collectorService;
    protected final RestClient restClient;
    protected final ApiSettings apiSettings;

    public GitHubV3(CollectorService collectorService,
                    RestClient restClient,
                    ApiSettings apiSettings) {
        this.collectorService = collectorService;
        this.restClient = restClient;
        this.apiSettings = apiSettings;
    }

    protected Collector getCollector() {
        Collector collector = new Collector();
        collector.setCollectorType(CollectorType.SCM);
        collector.setLastExecuted(System.currentTimeMillis());
        collector.setOnline(true);
        collector.setEnabled(true);
        collector.setName("GitHub");
        Map<String, Object> allOptions = new HashMap<>();
        allOptions.put(REPO_URL, "");
        allOptions.put(BRANCH, "");
        allOptions.put(USER_ID, "");
        allOptions.put(PASSWORD, "");
        allOptions.put(TOKEN, "");
        collector.setAllFields(allOptions);

        Map<String, Object> uniqueOptions = new HashMap<>();
        uniqueOptions.put(REPO_URL, "");
        uniqueOptions.put(BRANCH, "");
        collector.setUniqueFields(uniqueOptions);

        return collector;
    }

    public abstract String process(JSONObject jsonObject) throws MalformedURLException, HygieiaException, ParseException;
    public abstract QueryDslPredicateExecutor<GitHubRepo> getGitHubRepoRepository();

    protected CollectorItem buildCollectorItem (ObjectId collectorId, String repoUrl, String branch) {
        if (!StringUtils.isEmpty(repoUrl) && !StringUtils.isEmpty(branch)) {
            CollectorItem collectorItem = new CollectorItem();
            collectorItem.setCollectorId(collectorId);
            collectorItem.setEnabled(true);
            collectorItem.setPushed(true);
            collectorItem.setLastUpdated(System.currentTimeMillis());
            collectorItem.getOptions().put(REPO_URL, repoUrl);
            collectorItem.getOptions().put(BRANCH, branch);

            return collectorItem;
        }

        return null;
    }

    protected CollectorItem getCollectorItem(String repoUrl, String branch) throws HygieiaException {
        Collector col = collectorService.createCollector(getCollector());

        if (col == null)
            throw new HygieiaException("Failed creating collector.", HygieiaException.COLLECTOR_CREATE_ERROR);

        CollectorItem item = buildCollectorItem(col.getId(), repoUrl, branch);
        if (item == null)
            throw new HygieiaException("Failed creating collector item. Invalid repo url and/or branch", HygieiaException.COLLECTOR_ITEM_CREATE_ERROR);

        CollectorItem colItem = collectorService.createCollectorItem(item);

        if (colItem == null)
            throw new HygieiaException("Failed creating collector item.", HygieiaException.COLLECTOR_ITEM_CREATE_ERROR);

        return colItem;
    }

    protected String getRepositoryToken(String scmUrl) {
        Collector collector = collectorService.createCollector(getCollector());

        Map<String, Object> options = new HashMap<>();
        options.put(REPO_URL, scmUrl);

        GitHubRepo gitHubRepo = findByCollectorIdAndOptions(collector.getId(), options);

        if (gitHubRepo != null) {
            return String.valueOf(gitHubRepo.getOptions().get(TOKEN));
        }

        return null;
    }

    protected GitHubRepo findByCollectorIdAndOptions( ObjectId collectorId, Map<String, Object> options) {
        QCollectorItem item = new QCollectorItem("collectorItem");
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(item.collectorId.eq(collectorId));
        options.keySet().forEach(k -> {
            builder.and(item.options.get(k).eq(options.get(k)));
        });

        Iterable<GitHubRepo> gitHubRepoIterable = getGitHubRepoRepository().findAll(builder.getValue());

        if (gitHubRepoIterable != null) {
            for (GitHubRepo gitHubRepo : gitHubRepoIterable) {
                if (!StringUtils.isEmpty(gitHubRepo.getPersonalAccessToken())
                        && !"null".equalsIgnoreCase(gitHubRepo.getPersonalAccessToken().trim())) {
                    return gitHubRepo;
                }
            }
        }
        return null;
    }

    protected String getLDAPDN(String repoUrl, String user, String token) {
        if (StringUtils.isEmpty(user)) return null;
        // This is weird. Github does replace the _ in commit author with - in the user api!!!
        String formattedUser = user.replace("_", "-");
        try {
            GitHubParsed gitHubParsed = new GitHubParsed(repoUrl);
            String apiUrl = gitHubParsed.getBaseApiUrl();
            String queryUrl = apiUrl.concat("users/").concat(formattedUser);

            ResponseEntity<String> response = restClient.makeRestCallGet(queryUrl, "token", token);
            JSONObject jsonObject = null;
            try {
                jsonObject = restClient.parseAsObject(response);
            } catch (ParseException e) {
                LOG.info("Unable to get user information for "+queryUrl,e);
            }

            return restClient.getString(jsonObject, "ldap_dn");
        } catch (MalformedURLException | HygieiaException | RestClientException e) {
            LOG.error("Error getting LDAP_DN For user " + user, e);
        }

        return null;
    }

    protected GitHubWebHookSettings parseAsGitHubWebHook(String jsonString) {
        GitHubWebHookSettings gitHubWebHookSettings = null;

        if (StringUtils.isEmpty(jsonString)) { return gitHubWebHookSettings; }

        try {
            gitHubWebHookSettings = new ObjectMapper().readValue(jsonString, GitHubWebHookSettings.class);
        } catch (IOException e) {
            LOG.info("Could not be converted into "+GitHubWebHookSettings.class.getSimpleName()+": "+jsonString);
        }
        return gitHubWebHookSettings;
    }

    protected void checkForErrors(JSONObject responseJsonObject) throws HygieiaException, ParseException {
        JSONArray errors = restClient.getArray(responseJsonObject, "errors");

        if (!CollectionUtils.isEmpty(errors)) {
            throw new HygieiaException("Error in GraphQL query:" + errors.toJSONString(), HygieiaException.JSON_FORMAT_ERROR);
        }
    }
}