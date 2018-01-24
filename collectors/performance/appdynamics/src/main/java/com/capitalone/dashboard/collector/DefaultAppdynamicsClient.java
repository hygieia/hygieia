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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

@Component
public class DefaultAppdynamicsClient implements AppdynamicsClient {
    private static final Log LOG = LogFactory.getLog(DefaultAppdynamicsClient.class);
    private static final String APPLICATION_LIST_PATH = "/controller/rest/applications?output=json";
    private static final String OVERALL_SUFFIX = "Overall Application Performance|*";
    private static final String OVERALL_METRIC_PATH = "/controller/rest/applications/%s/metric-data?metric-path=%s&time-range-type=BEFORE_NOW&duration-in-mins=15&output=json";
    private static final String HEALTH_VIOLATIONS_PATH = "/controller/rest/applications/%s/problems/healthrule-violations?time-range-type=BEFORE_NOW&duration-in-mins=15&output=json";
    private static final String NODE_LIST_PATH = "/controller/rest/applications/%s/nodes?output=json";
    private static final String BUSINESS_TRANSACTION_LIST_PATH = "/controller/rest/applications/%s/business-transactions?output=json";
    private static final String METRIC_PATH_DELIMITER = "\\|";
    private final AppdynamicsSettings settings;
    private final RestOperations rest;
    //overall metrics
    private static final String ERRORS_PER_MINUTE = "errorsperMinute";
    private static final String AVERAGE_RESPONSE_TIME = "averageResponseTime";
    private static final String CALLS_PER_MINUTE = "callsperMinute";
    private static final String RESPONSE_TIME_SEVERITY = "responseTimeSeverity";
    private static final String BUSINESS_TRANSACTION_HEALTH = "businessTransactionHealthPercent";
    private static final String NODE_HEALTH_PECENT = "nodeHealthPercent";
    private static final String TOTAL_CALLS = "totalCalls";
    private static final String TOTAL_ERRORS = "totalErrors";
    private static final String VIOLATION_OBJECT = "violationObject";
    private static final String ERROR_RATE_SEVERITY = "errorRateSeverity";
    private static final String BUSINESS_TRANSACTION = "BUSINESS_TRANSACTION";

    @Autowired
    public DefaultAppdynamicsClient(AppdynamicsSettings settings, Supplier<RestOperations> restOperationsSupplier) {
        this.settings = settings;
        this.rest = restOperationsSupplier.get();
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

    /**
     * Retrieves a JSON array of all of the applications that are registered in AppDynamics.
     *
     * @return Set of applications used to populate the collector_items database. This data is
     * later used by the front end to populate the dropdown list of applications.
     */
    @Override
    public Set<AppdynamicsApplication> getApplications(String instanceUrl) {
        Set<AppdynamicsApplication> returnSet = new HashSet<>();
        try {
            String url = joinURL(instanceUrl, APPLICATION_LIST_PATH);
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
                LOG.error("Parsing applications on instance: " + instanceUrl, e);
            }
        } catch (RestClientException rce) {
            LOG.error("client exception loading applications", rce);
            throw rce;
        } catch (MalformedURLException mfe) {
            LOG.error("malformed url for loading applications", mfe);
        }
        return returnSet;
    }

    /**
     * Obtains the relevant data via different appdynamics api calls.
     *
     * @param application the current application. Used to provide access to appID/name
     * @return List of PerformanceMetrics used to populate the performance database
     */
    @Override
    public Map<String,Object> getPerformanceMetrics(AppdynamicsApplication application, String instanceUrl ) {
        Map<String,Object> metrics = new HashMap<>();

        metrics.putAll(getOverallMetrics(application, instanceUrl));
        metrics.putAll(getCalculatedMetrics(metrics));
        metrics.putAll(getHealthMetrics(application, instanceUrl));
        metrics.putAll(getViolations(application, instanceUrl));
        metrics.putAll(getSeverityMetrics(application, instanceUrl));
        return metrics;
    }

    /**
     * Obtains the "Overall Application Performance" metrics for the current application from Appdynamics
     * e.g. /controller/#/location=METRIC_BROWSER&timeRange=last_15_minutes.BEFORE_NOW.-1.-1.15&application=<APPID>
     * Currently used by the UI: calls per minute, errors per minute, average response time
     * @param application the current application. Used to provide access to appID/name
     * @return List of PerformanceMetrics used to populate the performance database
     */
    private Map<String,Long> getOverallMetrics(AppdynamicsApplication application, String instanceUrl) {
        Map<String,Long> overallMetrics = new HashMap<>();
        try {
            String url = joinURL(instanceUrl, String.format(OVERALL_METRIC_PATH, application.getAppID(), URLEncoder.encode(OVERALL_SUFFIX, "UTF-8"), String.valueOf(settings.getTimeWindow())));
            ResponseEntity<String> responseEntity = makeRestCall(url);
            String returnJSON = responseEntity.getBody();
            JSONParser parser = new JSONParser();

            try {
                JSONArray array = (JSONArray) parser.parse(returnJSON);

                for (Object entry : array) {
                    JSONObject jsonEntry = (JSONObject) entry;
                    String metricPath = getString(jsonEntry, "metricPath");
                    JSONArray metricValues =  getJsonArray(jsonEntry, "metricValues");
                    if (metricValues.isEmpty()){
                        continue;
                    }
                    JSONObject mObj = (JSONObject) metricValues.get(0);
                    Long metricValue = getLong(mObj, "value");

                    PerformanceMetric metric = new PerformanceMetric();
                    String metricName = parseMetricName(metricPath);
                    if(in(metricName,CALLS_PER_MINUTE,ERRORS_PER_MINUTE,AVERAGE_RESPONSE_TIME)){
                        metric.setName(parseMetricName(metricPath));
                        metric.setValue(metricValue);
                        overallMetrics.put(parseMetricName(metricPath),metricValue);
                    }
                }
            } catch (ParseException | RestClientException e) {
                LOG.error("Parsing metrics for : " + instanceUrl + ". Application =" + application.getAppName(), e);
            }
        } catch (MalformedURLException | UnsupportedEncodingException mfe) {
            LOG.error("malformed url for loading jobs", mfe);
        }
        return overallMetrics;
    }

    /**
     * Some metrics are not immediately available (e.g. Total Calls, Total Errors). We need to calculate them.
     *
     * @param metrics the already-populated list of metrics. We use this data to calculate new values.
     * @return List of PerformanceMetrics used to populate the performance database
     */
    private Map<String,Long> getCalculatedMetrics(Map<String,Object> metrics) {

        long errorsPerMinVal = 0;
        long callsPerMinVal = 0;
        Map<String,Long> calculatedMetrics = new HashMap<>();
        Iterator it = metrics.keySet().iterator();
        while(it.hasNext()){
            String key = (String)it.next();
            if(key.equals(ERRORS_PER_MINUTE)){
                errorsPerMinVal = (long) metrics.get(key);
            }

            if(key.equals(CALLS_PER_MINUTE)){
                callsPerMinVal = (long) metrics.get(key);
            }

        }
        // Total Errors
        // Right now the timeframe is hard-coded to 15 min. Change this if that changes.
        calculatedMetrics.put(TOTAL_ERRORS,errorsPerMinVal * 15);

        // Total Calls
        // Right now the timeframe is hard-coded to 15 min. Change this if that changes.
        calculatedMetrics.put(TOTAL_CALLS,callsPerMinVal * 15);

        return calculatedMetrics;
    }

    /**
     * Calculates the Node Health Percent and Business Health Percent values
     * @param application the current application. Used to provide access to appID/name
     * @return List of two PerformanceMetrics that contain info about the health percents
     */
    private Map<String,Object> getHealthMetrics(AppdynamicsApplication application, String instanceUrl ) {
        // business health percent
        long numNodeViolations = 0;
        long numBusinessViolations = 0;
        long numNodes = 0;
        long numBusinessTransactions = 0;
        double nodeHealthPercent = 0.0;
        double businessHealthPercent = 0.0;

        Map<String,Object> healthMetrics = new HashMap<>();

        try {
            // GET NUMBER OF VIOLATIONS OF EACH TYPE
            String url = joinURL(instanceUrl, String.format(HEALTH_VIOLATIONS_PATH, application.getAppID()));
            ResponseEntity<String> responseEntity = makeRestCall(url);
            String returnJSON = responseEntity.getBody();
            JSONParser parser = new JSONParser();

            JSONArray array = (JSONArray) parser.parse(returnJSON);

            for (Object entry : array) {

                JSONObject jsonEntry = (JSONObject) entry;

                if (getString(jsonEntry, "incidentStatus").equals("RESOLVED"))
                    continue;

                JSONObject affEntityObj = (JSONObject) jsonEntry.get("affectedEntityDefinition");

                String entityType = getString(affEntityObj, "entityType");

                if (entityType.equals("APPLICATION_COMPONENT_NODE")) {
                    numNodeViolations++;

                } else if (entityType.equals(BUSINESS_TRANSACTION)) {
                    numBusinessViolations++;

                }
            }


            // GET NUMBER OF NODES
            url = joinURL(instanceUrl, String.format(NODE_LIST_PATH, application.getAppID()));
            responseEntity = makeRestCall(url);
            returnJSON = responseEntity.getBody();
            parser = new JSONParser();
            array = (JSONArray) parser.parse(returnJSON);

            numNodes = array.size();

            // GET NUMBER OF BUSINESS TRANSACTIONS
            url = joinURL(instanceUrl, String.format(BUSINESS_TRANSACTION_LIST_PATH, application.getAppID()));
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

        // Node health percent is just 1 - (num node violations / num nodes)
        if (numNodes != 0)
            nodeHealthPercent = Math.floor(100.0 * (1.0 - ((double) (numNodeViolations) / (double) (numNodes)))) / 100.0;

        // Right now the timeframe is hard-coded to 15 min. Change this if that changes.
        healthMetrics.put(NODE_HEALTH_PECENT,nodeHealthPercent);

        // Business health percent is just 1 - (num business transaction violations / num business transactions)
        if (numBusinessTransactions != 0)
            businessHealthPercent = Math.floor(100.0 * (1.0 - ((double) (numBusinessViolations) / (double) (numBusinessTransactions)))) / 100.0;
        healthMetrics.put(BUSINESS_TRANSACTION_HEALTH,businessHealthPercent);

        return healthMetrics;
    }

    /**
     * Obtains a list of health violations for the current application from Appdynamics
     * e.g. /controller/#/location=APP_INCIDENT_LIST&application=<APPID>
     *
     * @param application the current application. Used to provide access to appID/name
     * @return Single element list, value is the raw JSON object of the health violations
     */
    private Map<String,Object> getViolations(AppdynamicsApplication application, String instanceUrl ) {
        Map<String,Object> violationObjects = new HashMap<>();

        try {
            String url = joinURL(instanceUrl, String.format(HEALTH_VIOLATIONS_PATH, application.getAppID()));
            ResponseEntity<String> responseEntity = makeRestCall(url);
            String returnJSON = responseEntity.getBody();
            JSONParser parser = new JSONParser();

            JSONArray array = (JSONArray) parser.parse(returnJSON);

            violationObjects.put(VIOLATION_OBJECT,array);
        } catch (MalformedURLException e) {
            LOG.error("client exception loading applications", e);
        } catch (ParseException e) {
            LOG.error("client exception loading applications", e);
        }

        return violationObjects;

    }

    /**
     * Calculates the response time and error rate severities.
     * 0: good, 1: warning, 2: critical
     * Iterates through list of violations. The final severity will be the highest of them all
     * (e.g. response time violations are Warning, Critical, Warning, Warning, Warning -> Critical)
     *
     * @param application the current application. Used to provide access to appID/name
     * @return List of two PerformanceMetrics that contain info about the severities
     */
    private Map<String,Object> getSeverityMetrics(AppdynamicsApplication application, String instanceUrl ) {

        long responseTimeSeverity = 0;
        long errorRateSeverity = 0;

        Map<String,Object> severityMetrics = new HashMap<>();

        try {
            // NUMBER OF VIOLATIONS
            String url = joinURL(instanceUrl, String.format(HEALTH_VIOLATIONS_PATH, application.getAppID()));
            ResponseEntity<String> responseEntity = makeRestCall(url);
            String returnJSON = responseEntity.getBody();
            JSONParser parser = new JSONParser();

            JSONArray array = (JSONArray) parser.parse(returnJSON);

            for (Object entry : array) {
                JSONObject jsonEntry = (JSONObject) entry;
                JSONObject affEntityObj = (JSONObject) jsonEntry.get("affectedEntityDefinition");

                String entityType = getString(affEntityObj, "entityType");


                if (entityType.equals(BUSINESS_TRANSACTION)) {

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
        severityMetrics.put(ERROR_RATE_SEVERITY,errorRateSeverity);
        severityMetrics.put(RESPONSE_TIME_SEVERITY,responseTimeSeverity);
        return severityMetrics;
    }

    private String parseMetricName(String metricPath) {
        String[] arr = metricPath.split(METRIC_PATH_DELIMITER);
        if (arr == null) return "";
        String metricName =   arr[arr.length - 1].replaceAll(" ","");
        metricName = metricName.replaceAll("\\(ms\\)","");
        metricName  = StringUtils.capitalize(metricName);
        metricName = Character.toLowerCase(metricName.charAt(0)) + metricName.substring(1);
        return  metricName;
    }

    private boolean in(String suspect,String ... obj){
        for (String string:obj) {
            if(string.equalsIgnoreCase(suspect)){
                return  true;
            }
        }
        return false;
    }

    protected ResponseEntity<String> makeRestCall(String sUrl) throws MalformedURLException {
        URI thisuri = URI.create(sUrl);
        String userInfo = thisuri.getUserInfo();
        ResponseEntity<String> response = null;
        //get userinfo from URI or settings (in spring properties)
        if (StringUtils.isEmpty(userInfo) && (this.settings.getUsername() != null) && (this.settings.getPassword() != null)) {
            userInfo = this.settings.getUsername() + ":" + this.settings.getPassword();
        }
        // Basic Auth only.
        if (StringUtils.isNotEmpty(userInfo)) {
            response =  rest.exchange(thisuri, HttpMethod.GET,
                    new HttpEntity<>(createHeaders(userInfo)),
                    String.class);
        } else {
            response =  rest.exchange(thisuri, HttpMethod.GET, null,
                    String.class);
        }
        return response;

    }

    protected HttpHeaders createHeaders(final String userInfo) {
        byte[] encodedAuth = Base64.encodeBase64(
                userInfo.getBytes(StandardCharsets.US_ASCII));
        String authHeader = "Basic " + new String(encodedAuth);

        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION, authHeader);
        return headers;
    }

    private String getString(JSONObject json, String key) {
        return (String) json.get(key);
    }

    private Long getLong(JSONObject json, String key) {
        return (Long) json.get(key);
    }

    private JSONArray getJsonArray(JSONObject json, String key) {
        Object array = json.get(key);
        return array == null ? new JSONArray() : (JSONArray) array;
    }
}
