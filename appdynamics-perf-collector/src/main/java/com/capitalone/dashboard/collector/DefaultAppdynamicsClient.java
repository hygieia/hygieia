package com.capitalone.dashboard.collector;

import com.capitalone.dashboard.model.AppdynamicsApplication;
import com.capitalone.dashboard.model.Performance;
import com.capitalone.dashboard.model.PerformanceMetric;
import com.capitalone.dashboard.util.Supplier;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.appdynamics.appdrestapi.RESTAccess;
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
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;

@Component
public class DefaultAppdynamicsClient implements AppdynamicsClient {
    private static final Log LOG = LogFactory.getLog(DefaultAppdynamicsClient.class);
    //    private static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ssZ";
    //   private static final int NUM_MINUTES = 600; //14 days
    //  private static final double DEFAULT_VALUE = -1.0;
    //  private Map<String, Double> applicationDataMap = new HashMap<>();
    private final AppdynamicsSettings settings;
    private final RestOperations rest;

    private static final String APPLICATION_LIST_PATH = "/controller/rest/applications?output=json";
    private static final String OVERALL_SUFFIX = "Overall Application Performance|*";
    private static final String OVERALL_METRIC_PATH = "/controller/rest/applications/%s/metric-data?metric-path=%s&time-range-type=BEFORE_NOW&duration-in-mins=%s&output=json";
    private static final String METRIC_PATH_DELIMITER = "\\|";


    // private static final String STATUS_WARN = "WARN";
    // private static final String STATUS_CRITICAL = "CRITICAL";

    @Autowired
    public DefaultAppdynamicsClient(AppdynamicsSettings settings, Supplier<RestOperations> restOperationsSupplier) {
        this.settings = settings;
        this.rest = restOperationsSupplier.get();
    }

    /*private PerformanceMetricStatus metricStatus(String status) {
        if (StringUtils.isBlank(status)) {
            return PerformanceMetricStatus.OK;
        }

        switch(status) {
            case STATUS_WARN:  return PerformanceMetricStatus.WARNING;
            case STATUS_CRITICAL: return PerformanceMetricStatus.CRITICAL;
            default:           return PerformanceMetricStatus.OK;
        }
    }*/


    @Override
    public Set<AppdynamicsApplication> getApplications() {
        Set<AppdynamicsApplication> returnSet = new HashSet<>();
        try {
            String url = joinURL(settings.getInstanceUrl(), APPLICATION_LIST_PATH);
            ResponseEntity<String> responseEntity = makeRestCall(url);
            String returnJSON = responseEntity.getBody();
            JSONParser parser = new JSONParser();

            try {
                JSONArray array = (JSONArray) parser.parse(returnJSON);

                for (Object entry : array) {
                    JSONObject jsonEntry = (JSONObject) entry;

                    String appName = getString(jsonEntry, "name");
                    String appId = String.valueOf(getLong(jsonEntry, "id"));
                    String desc = getString(jsonEntry, "description");
                    if (StringUtils.isEmpty(desc)) {
                        desc = appName;
                    }
                    AppdynamicsApplication app = new AppdynamicsApplication();
                    app.setAppID(appId);
                    app.setAppName(appName);
                    app.setAppDesc(desc);
                    app.setDescription(desc);
                    returnSet.add(app);
                }
            } catch (ParseException e) {
                LOG.error("Parsing applications on instance: " + settings.getInstanceUrl(), e);
            }
        } catch (RestClientException rce) {
            LOG.error("client exception loading applications", rce);
            throw rce;
        } catch (MalformedURLException mfe) {
            LOG.error("malformed url for loading applications", mfe);
        }
        return returnSet;
    }


    @Override
    public Performance getPerformanceMetrics(AppdynamicsApplication application) {
        Performance performance = new Performance();
        try {
            String url = joinURL(settings.getInstanceUrl(), String.format(OVERALL_METRIC_PATH, application.getAppID(), URLEncoder.encode(OVERALL_SUFFIX, "UTF-8"), String.valueOf(settings.getTimeWindow())));
            ResponseEntity<String> responseEntity = makeRestCall(url);
            String returnJSON = responseEntity.getBody();
            JSONParser parser = new JSONParser();

            try {
                JSONArray array = (JSONArray) parser.parse(returnJSON);

                for (Object entry : array) {
                    JSONObject jsonEntry = (JSONObject) entry;
                    String metricPath = getString(jsonEntry, "metricPath");
                    JSONObject mObj = (JSONObject) getJsonArray(jsonEntry, "metricValues").get(0);
                    Long metricValue = getLong(mObj, "value");
                    PerformanceMetric metric = new PerformanceMetric();
                    metric.setName(parseMetricName(metricPath));
                    metric.setValue(metricValue);
                    performance.getMetrics().add(metric);
                }
            } catch (ParseException | RestClientException e) {
                LOG.error("Parsing metircs for : " + settings.getInstanceUrl() + ". Application =" + application.getAppName(), e);
            }
        } catch (MalformedURLException | UnsupportedEncodingException mfe) {
            LOG.error("malformed url for loading jobs", mfe);
        }
        return performance;
    }

    private String parseMetricName(String metricPath) {
        String[] arr = metricPath.split(METRIC_PATH_DELIMITER);
        if (arr == null) return "";
        return arr[arr.length - 1];
    }


    private double getNodeHealthPercent(String appName, RESTAccess access, long start, long end) {

        //get # of violations, divide by # of nodes
        int numNodes = (access.getNodesForApplication(appName).getNodes()).size();
        int numViolations = (access.getHealthRuleViolations(appName, start, end)).getPolicyViolations().size();

        return 100.0 - (numViolations / numNodes);

    }

    //Utils
    protected ResponseEntity<String> makeRestCall(String sUrl) throws MalformedURLException {
        URI thisuri = URI.create(sUrl);
        String userInfo = thisuri.getUserInfo();

        //get userinfo from URI or settings (in spring properties)
        if (StringUtils.isEmpty(userInfo) && (this.settings.getUsername() != null) && (this.settings.getPassword() != null)) {
            userInfo = this.settings.getUsername() + ":" + this.settings.getPassword();
        }
        // Basic Auth only.
        if (StringUtils.isNotEmpty(userInfo)) {
            return rest.exchange(thisuri, HttpMethod.GET,
                    new HttpEntity<>(createHeaders(userInfo)),
                    String.class);
        } else {
            return rest.exchange(thisuri, HttpMethod.GET, null,
                    String.class);
        }

    }

    protected HttpHeaders createHeaders(final String userInfo) {
        byte[] encodedAuth = Base64.encodeBase64(
                userInfo.getBytes(StandardCharsets.US_ASCII));
        String authHeader = "Basic " + new String(encodedAuth);

        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION, authHeader);
        return headers;
    }


    // join a base url to another path or paths - this will handle trailing or non-trailing /'s
    public static String joinURL(String base, String... paths) throws MalformedURLException {
        StringBuilder result = new StringBuilder(base);
        for (String path : paths) {
            String p = path.replaceFirst("^(\\/)+", "");
            if (result.lastIndexOf("/") != result.length() - 1) {
                result.append('/');
            }
            result.append(p);
        }
        return result.toString();
    }

    private String getString(JSONObject json, String key) {
        return (String) json.get(key);
    }

    private Integer getInt(JSONObject json, String key) {
        return (Integer) json.get(key);
    }

    private Long getLong(JSONObject json, String key) {
        return (Long) json.get(key);
    }

    private JSONArray getJsonArray(JSONObject json, String key) {
        Object array = json.get(key);
        return array == null ? new JSONArray() : (JSONArray) array;
    }
}
