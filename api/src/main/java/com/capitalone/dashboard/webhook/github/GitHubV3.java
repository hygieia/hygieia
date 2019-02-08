package com.capitalone.dashboard.webhook.github;

import com.capitalone.dashboard.repository.CollectorItemRepository;
import com.capitalone.dashboard.settings.ApiSettings;
import com.capitalone.dashboard.client.RestClient;
import com.capitalone.dashboard.model.webhook.github.GitHubParsed;
import com.capitalone.dashboard.misc.HygieiaException;
import com.capitalone.dashboard.model.Collector;
import com.capitalone.dashboard.model.CollectorItem;
import com.capitalone.dashboard.model.CollectorType;
import com.capitalone.dashboard.service.CollectorService;
import com.capitalone.dashboard.util.HygieiaUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bson.types.ObjectId;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class GitHubV3 {
    private static final Log LOG = LogFactory.getLog(GitHubV3.class);

    private static final String REPO_URL = "url";
    private static final String BRANCH = "branch";
    private static final String USER_ID = "userID";
    private static final String PASSWORD = "password";
    private static final String TOKEN = "personalAccessToken";

    protected final CollectorService collectorService;
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
    public abstract CollectorItemRepository getCollectorItemRepository();

    protected CollectorItem buildCollectorItem (ObjectId collectorId, String repoUrl, String branch) {
        if (HygieiaUtils.checkForEmptyStringValues(repoUrl, branch)) { return null; }

        CollectorItem collectorItem = new CollectorItem();
        collectorItem.setCollectorId(collectorId);
        collectorItem.setEnabled(true);
        collectorItem.setPushed(true);
        collectorItem.setLastUpdated(System.currentTimeMillis());
        collectorItem.getOptions().put(REPO_URL, repoUrl);
        collectorItem.getOptions().put(BRANCH, branch);

        return collectorItem;
    }

    private boolean checkForEmptyStringValues(String ... values) {
        for (String value: values) {
            if (StringUtils.isEmpty(value)) { return true; }
        }

        return false;
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

        List<ObjectId> collectorIdList = new ArrayList<>();
        collectorIdList.add(collector.getId());

        Iterable<CollectorItem> collectorItemIterable
                = getCollectorItemRepository().findAllByOptionNameValueAndCollectorIdsIn(REPO_URL, scmUrl, collectorIdList);
        if (collectorItemIterable == null) { return null; }

        String tokenValue = null;
        for (CollectorItem collectorItem : collectorItemIterable) {
            String collectorItemTokenValue = String.valueOf(collectorItem.getOptions().get(TOKEN));
            if (!StringUtils.isEmpty(collectorItemTokenValue)
                    && !"null".equalsIgnoreCase(collectorItemTokenValue)) {
                tokenValue = collectorItemTokenValue;
                break;
            }
        }
        return tokenValue;
    }

    protected String getLDAPDN(String repoUrl, String user, String token) {
        if (StringUtils.isEmpty(user)) return null;
        // This is weird. Github does replace the _ in commit author with - in the user api!!!
        String formattedUser = user.replace("_", "-");
        String ldapLdn = null;
        try {
            GitHubParsed gitHubParsed = new GitHubParsed(repoUrl);
            String apiUrl = gitHubParsed.getBaseApiUrl();
            String queryUrl = apiUrl.concat("users/").concat(formattedUser);

            ResponseEntity<String> response = restClient.makeRestCallGet(queryUrl, "token", token);
            try {
                JSONObject jsonObject = restClient.parseAsObject(response);
                ldapLdn = restClient.getString(jsonObject, "ldap_dn");
            } catch (ParseException e) {
                LOG.info("Unable to get user information for "+queryUrl,e);
            }
        } catch (MalformedURLException | HygieiaException | RestClientException e) {
            LOG.error("Error getting LDAP_DN For user " + user, e);
        }

        return ldapLdn;
    }

    protected void checkForErrors(JSONObject responseJsonObject) throws HygieiaException, ParseException {
        JSONArray errors = restClient.getArray(responseJsonObject, "errors");

        if (!CollectionUtils.isEmpty(errors)) {
            throw new HygieiaException("Error in GraphQL query:" + errors.toJSONString(), HygieiaException.JSON_FORMAT_ERROR);
        }
    }
}