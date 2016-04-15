package com.capitalone.dashboard.collector;

import com.capitalone.dashboard.model.Commit;
import com.capitalone.dashboard.model.GerritRepo;
import com.capitalone.dashboard.util.Supplier;
import org.apache.commons.codec.binary.Base64;
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

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * GerritClient implementation that uses SVNKit to fetch information about
 * Subversion repositories.
 */

@Component
public class DefaultGerritClient implements GerritClient {
    private static final Log LOG = LogFactory.getLog(DefaultGerritClient.class);

    private final GerritSettings settings;

    private final RestOperations restOperations;
    private static final String SEGMENT_API = "/a/changes/?q=is:open";
    private static final String SEGMENT_PROJECT = "+project:";
    private static final String SEGMENT_BRANCH = "+branch:";

// http://gerrit.com/a/changes/?q=is:open+owner:self+project:demo-ebs+branch:master

    @Autowired
    public DefaultGerritClient(GerritSettings settings,
                               Supplier<RestOperations> restOperationsSupplier) {
        this.settings = settings;
        this.restOperations = restOperationsSupplier.get();
    }

    @Override
    public List<Commit> getCommits(GerritRepo repo, boolean firstRun) {

        List<Commit> commits = new ArrayList<>();
        String apiUrl = settings.getHost() + SEGMENT_API + SEGMENT_PROJECT + repo.getProject() + SEGMENT_BRANCH + repo.getBranch();
        LOG.debug("API URL IS:" + apiUrl);


        try {
            ResponseEntity<String> response = makeRestCall(apiUrl, settings.getUser(), settings.getPassword());
            JSONArray jsonArray = paresAsArray(response);
            for (Object item : jsonArray) {
                JSONObject jsonObject = (JSONObject) item;
                JSONObject authorObject = (JSONObject) jsonObject.get("owner");
                long timestamp = new DateTime(str(jsonObject, "updated"))
                        .getMillis();
                Commit commit = new Commit();
                commit.setTimestamp(System.currentTimeMillis());
                commit.setScmUrl(settings.getHost() + "/" + str(jsonObject, "project") + "/" + str(jsonObject, "branch"));
                commit.setScmRevisionNumber(str(jsonObject, "id"));
                commit.setScmAuthor(str(authorObject, "name"));
                commit.setScmCommitLog(str(jsonObject, "subject"));
                commit.setScmCommitTimestamp(timestamp);
                commit.setNumberOfChanges(1);
                commits.add(commit);
            }

        } catch (RestClientException re) {
            LOG.error(re.getMessage() + ":" + apiUrl);
        }


        return commits;
    }

    private Date getDate(Date dateInstance, int offsetDays, int offsetMinutes) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(dateInstance);
        cal.add(Calendar.DATE, offsetDays);
        cal.add(Calendar.MINUTE, offsetMinutes);
        return cal.getTime();
    }

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

    private ResponseEntity<String> makeRestCall(String url, String userId,
                                                String password) {
        // Basic Auth only.
        if (!"".equals(userId) && !"".equals(password)) {
            return restOperations.exchange(url, HttpMethod.GET,
                    new HttpEntity<>(createHeaders(userId, password)),
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

    private JSONArray paresAsArray(ResponseEntity<String> response) {
        try {
            return (JSONArray) new JSONParser().parse(response.getBody());
        } catch (ParseException pe) {
            LOG.error(pe);
        }
        return new JSONArray();
    }

    private String str(JSONObject json, String key) {
        Object value = json.get(key);
        return value == null ? null : value.toString();
    }

}
