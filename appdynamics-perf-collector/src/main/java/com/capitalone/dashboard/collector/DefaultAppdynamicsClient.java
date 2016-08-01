package com.capitalone.dashboard.collector;

import com.capitalone.dashboard.model.AppdynamicsApplication;
import com.capitalone.dashboard.model.PerformanceMetric;
import com.capitalone.dashboard.util.Supplier;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
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
    private static final String OVERALL_METRIC_PATH = "/controller/rest/applications/%s/metric-data?metric-path=%s&time-range-type=BEFORE_NOW&duration-in-mins=60&output=json";
    private static final String HEALTH_VIOLATIONS_PATH = "/controller/rest/applications/%s/problems/healthrule-violations?time-range-type=BEFORE_NOW&duration-in-mins=60&output=json";
    private static final String YOLO_JSON_PATH = "/controller/rest/applications/%s/problems/healthrule-violations?time-range-type=BEFORE_NOW&duration-in-mins=10&output=json";
    private static final String NODE_LIST_PATH = "/controller/rest/applications/%s/nodes?output=json";
    private static final String BUSINESS_TRANSACTION_LIST_PATH = "/controller/rest/applications/%s/business-transactions?output=json";
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
    public List<PerformanceMetric> getPerformanceMetrics(AppdynamicsApplication application) {
        List<PerformanceMetric> metrics = new ArrayList<>();

        metrics.addAll(getOverallMetrics(application));
        metrics.addAll(getHealthMetrics(application));
        metrics.addAll(getCalculatedMetrics(metrics));
        metrics.addAll(getSeverityMetrics(application));
        metrics.addAll(getYOLOJSONObj(application));
        return metrics;
    }

    private List<PerformanceMetric> getYOLOJSONObj(AppdynamicsApplication application) {
        List<PerformanceMetric> yoloJSONs = new ArrayList<>();

        try {
            String url = joinURL(settings.getInstanceUrl(), String.format(YOLO_JSON_PATH, application.getAppID()));
            ResponseEntity<String> responseEntity = makeRestCall(url);
            String returnJSON = responseEntity.getBody();
            JSONParser parser = new JSONParser();

            JSONArray array = (JSONArray) parser.parse(returnJSON);

            PerformanceMetric yoloJson = new PerformanceMetric();
            yoloJson.setName("Yolo JSON Object");
            // Right now the timeframe is hard-coded to 60 min. Change this if that changes.
            yoloJson.setValue(array);
            yoloJSONs.add(yoloJson);

        } catch (MalformedURLException e) {
            LOG.error("client exception loading applications", e);
        } catch (ParseException e) {
            LOG.error("client exception loading applications", e);
        }

        return yoloJSONs;

    }

    private List<PerformanceMetric> getOverallMetrics(AppdynamicsApplication application) {
        List<PerformanceMetric> overallMetrics = new ArrayList<>();
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
                    overallMetrics.add(metric);
                }
            } catch (ParseException | RestClientException e) {
                LOG.error("Parsing metrics for : " + settings.getInstanceUrl() + ". Application =" + application.getAppName(), e);
            }
        } catch (MalformedURLException | UnsupportedEncodingException mfe) {
            LOG.error("malformed url for loading jobs", mfe);
        }
        return overallMetrics;
    }

    private List<PerformanceMetric> getSeverityMetrics(AppdynamicsApplication application) {

        long responseTimeSeverity = 0;
        long errorRateSeverity = 0;

        List<PerformanceMetric> severityMetrics = new ArrayList<>();

        try {
            // NUMBER OF VIOLATIONS
            String url = joinURL(settings.getInstanceUrl(), String.format(HEALTH_VIOLATIONS_PATH, application.getAppID()));
            ResponseEntity<String> responseEntity = makeRestCall(url);
            String returnJSON = responseEntity.getBody();
            JSONParser parser = new JSONParser();

            JSONArray array = (JSONArray) parser.parse(returnJSON);


            for (Object entry : array) {
                JSONObject jsonEntry = (JSONObject) entry;
                JSONObject affEntityObj = (JSONObject) jsonEntry.get("affectedEntityDefinition");

                String entityType = getString(affEntityObj, "entityType");


                if (entityType.equals("BUSINESS_TRANSACTION")) {

                    long severity = getString(jsonEntry, "severity").equals("CRITICAL") ? 2 : 1;

                    if (getString(jsonEntry, "name").equals("Business Transaction error rate is much higher than normal")) {
                        errorRateSeverity = Math.max(errorRateSeverity, severity);
                    } else if (getString(jsonEntry, "name").equals("Business Transaction response time is much higher than normal")) {
                        responseTimeSeverity = Math.max(responseTimeSeverity, severity);
                    }
                }
            }
        } catch (MalformedURLException e) {
            LOG.error("client exception loading applications", e);
        } catch (ParseException e) {
            LOG.error("client exception loading applications", e);
        }

        PerformanceMetric metric = new PerformanceMetric();
        metric.setName("Error Rate Severity");
        // Right now the timeframe is hard-coded to 60 min. Change this if that changes.
        metric.setValue(errorRateSeverity);
        severityMetrics.add(metric);

        metric = new PerformanceMetric();
        metric.setName("Response Time Severity");
        // Right now the timeframe is hard-coded to 60 min. Change this if that changes.
        metric.setValue(responseTimeSeverity);
        severityMetrics.add(metric);


        return severityMetrics;

    }

    private List<PerformanceMetric> getHealthMetrics(AppdynamicsApplication application) {
        // business health percent
        long numNodeViolations = 0;
        long numBusinessViolations = 0;
        long numNodes = 0;
        long numBusinessTransactions = 0;
        double nodeHealthPercent = 0.0;
        double businessHealthPercent = 0.0;

        List<PerformanceMetric> heathMetrics = new ArrayList<>();

        try {
            // NUMBER OF VIOLATIONS
            String url = joinURL(settings.getInstanceUrl(), String.format(HEALTH_VIOLATIONS_PATH, application.getAppID()));
            ResponseEntity<String> responseEntity = makeRestCall(url);
            String returnJSON = responseEntity.getBody();
            JSONParser parser = new JSONParser();

            JSONArray array = (JSONArray) parser.parse(returnJSON);

            for (Object entry : array) {
                JSONObject jsonEntry = (JSONObject) entry;
                JSONObject affEntityObj = (JSONObject) jsonEntry.get("affectedEntityDefinition");

                String entityType = getString(affEntityObj, "entityType");

                if (entityType.equals("APPLICATION_COMPONENT_NODE")) {
                    numNodeViolations++;

                } else if (entityType.equals("BUSINESS_TRANSACTION")) {
                    numBusinessViolations++;

                }
            }

            // NUMBER OF NODES
            url = joinURL(settings.getInstanceUrl(), String.format(NODE_LIST_PATH, application.getAppID()));
            responseEntity = makeRestCall(url);
            returnJSON = responseEntity.getBody();
            parser = new JSONParser();
            array = (JSONArray) parser.parse(returnJSON);

            numNodes = array.size();

            // NUMBER OF TRANSACTIONS
            url = joinURL(settings.getInstanceUrl(), String.format(BUSINESS_TRANSACTION_LIST_PATH, application.getAppID()));
            responseEntity = makeRestCall(url);
            returnJSON = responseEntity.getBody();
            parser = new JSONParser();
            array = (JSONArray) parser.parse(returnJSON);

            numBusinessTransactions = array.size();

        } catch (MalformedURLException e) {
            LOG.error("client exception loading applications", e);
        } catch (ParseException e) {
            LOG.error("client exception loading applications", e);
        }

        if (numNodes != 0)
            nodeHealthPercent = Math.floor(100.0 * (1.0 - ((double) (numNodeViolations) / (double) (numNodes)))) / 100.0;

        PerformanceMetric metric = new PerformanceMetric();
        metric.setName("Node Health Percent");
        // Right now the timeframe is hard-coded to 60 min. Change this if that changes.
        metric.setValue(nodeHealthPercent);
        heathMetrics.add(metric);

        if (numBusinessTransactions != 0)
            businessHealthPercent = Math.floor(100.0 * (1.0 - ((double) (numBusinessViolations) / (double) (numBusinessTransactions)))) / 100.0;

        metric = new PerformanceMetric();
        metric.setName("Business Transaction Health Percent");
        // Right now the timeframe is hard-coded to 60 min. Change this if that changes.
        metric.setValue(businessHealthPercent);
        heathMetrics.add(metric);

        return heathMetrics;
    }

    private List<PerformanceMetric> getCalculatedMetrics(List<PerformanceMetric> metrics) {

        long errorsPerMinVal = 0;
        long callsPerMinVal = 0;
        List<PerformanceMetric> calculatedMetrics = new ArrayList<>();
        for (PerformanceMetric cm : metrics) {
            if (cm.getName().equals("Errors per Minute")) {
                errorsPerMinVal = (long) cm.getValue();
            }
            if (cm.getName().equals("Calls per Minute")) {
                callsPerMinVal = (long) cm.getValue();
            }
        }

        // Total Errors
        PerformanceMetric metric = new PerformanceMetric();
        metric.setName("Total Errors");
        // Right now the timeframe is hard-coded to 60 min. Change this if that changes.
        metric.setValue(errorsPerMinVal * 60);
        calculatedMetrics.add(metric);

        // Total Calls
        metric = new PerformanceMetric();
        metric.setName("Total Calls");
        // Right now the timeframe is hard-coded to 60 min. Change this if that changes.
        metric.setValue(callsPerMinVal * 60);
        calculatedMetrics.add(metric);


        return calculatedMetrics;
    }

    private String parseMetricName(String metricPath) {
        String[] arr = metricPath.split(METRIC_PATH_DELIMITER);
        if (arr == null) return "";
        return arr[arr.length - 1];
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
