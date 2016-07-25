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
import org.appdynamics.appdrestapi.data.Backends;
import org.appdynamics.appdrestapi.data.BusinessTransactions;
import org.appdynamics.appdrestapi.data.ConfigurationItems;
import org.appdynamics.appdrestapi.data.MetricData;
import org.appdynamics.appdrestapi.data.MetricItem;
import org.appdynamics.appdrestapi.data.MetricItems;
import org.appdynamics.appdrestapi.data.Nodes;
import org.appdynamics.appdrestapi.data.PolicyViolation;
import org.appdynamics.appdrestapi.exportdata.ExApplication;
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

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
public class DefaultAppdynamicsClient implements AppdynamicsClient {
    private static final Log LOG = LogFactory.getLog(DefaultAppdynamicsClient.class);
    //    private static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ssZ";
    private static final int NUM_MINUTES = 600; //14 days
    private static final double DEFAULT_VALUE = -1.0;
    private Map<String, Double> applicationDataMap = new HashMap<>();
    private final AppdynamicsSettings settings;
    private final RestOperations rest;

    private static final String APPLICATION_LIST_PATH = "/controller/rest/applications?output=json";
    private static final String OVERALL_METRIC_PATH = "controller/rest/applications/%s/metric-data?metric-path=Overall%20Application%20Performance%7C*&time-range-type=BEFORE_NOW&duration-in-mins=60&output=json";

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


    // TODO: Implement these using AppD rest api
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
                LOG.error("Parsing jobs on instance: " + settings.getInstanceUrl(), e);
            }
        } catch (RestClientException rce) {
            LOG.error("client exception loading jobs", rce);
            throw rce;
        } catch (MalformedURLException mfe) {
            LOG.error("malformed url for loading jobs", mfe);
        }
        return returnSet;
    }

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

    private String getRevision(JSONObject jsonItem) {
        // Use revision if provided, otherwise use id
        Long revision = (Long) jsonItem.get("revision");
        return revision == null ? getString(jsonItem, "id") : revision.toString();
    }

    private JSONArray getJsonArray(JSONObject json, String key) {
        Object array = json.get(key);
        return array == null ? new JSONArray() : (JSONArray) array;
    }


    @Override
    public Performance getPerformanceMetrics(AppdynamicsApplication application) {
        RESTAccess restClient = null;
        testingSDKCapabilities(restClient, application);
        Performance performance = new Performance();
        try {
            buildMetricDataMap(restClient, application);
        } catch (IOException e) {
            LOG.error("Oops", e);
        } catch (IllegalAccessException e) {
            LOG.error("Oops", e);
        }

        for (Map.Entry<String, Double> entry : applicationDataMap.entrySet()) {
            String metricName = entry.getKey();
            Double metricValue = entry.getValue();

            PerformanceMetric metric = new PerformanceMetric();
            metric.setName(metricName);
            metric.setValue(metricValue);

            performance.getMetrics().add(metric);

        }
        return performance;

    }


    public Map<String, Double> getApplicationDataMap() {
        return applicationDataMap;
    }


    private void buildMetricDataMap(RESTAccess access, AppdynamicsApplication application) throws IOException, IllegalAccessException {

        String[] metrics = new String[]{
                "Average Response Time (ms)",
                "Total Calls",
                "Calls per Minute",
                "Total Errors",
                "Errors per Minute",
                "Node Health Percent"
        };

        for (String metricName : metrics)
            applicationDataMap.put(metricName, DEFAULT_VALUE);

        //populate fields
        populateMetricFields(application.getAppName(), access);
    }

    private void buildViolationSeverityMap(String appName, RESTAccess access, long start, long end) throws IOException {

        applicationDataMap.put("Error Rate Severity", 0.0);
        applicationDataMap.put("Response Time Severity", 0.0);
        List<PolicyViolation> violations = access.getHealthRuleViolations(appName, start, end).getPolicyViolations();
        for (PolicyViolation violation : violations) {

            double currErrorRateSeverity = applicationDataMap.get("Error Rate Severity");
            double currResponseTimeSeverity = applicationDataMap.get("Response Time Severity");

            // If both are already critical, it's pointless to continue
            if (currErrorRateSeverity == 2.0 && currResponseTimeSeverity == 2.0)
                return;

            double severity = violation.getSeverity().equals("CRITICAL") ? 2.0 : 1.0;

            if (violation.getName().equals("Business Transaction error rate is much higher than normal")) {
                applicationDataMap.replace("Error Rate Severity", Math.max(currErrorRateSeverity, severity));
            } else if (violation.getName().equals("Business Transaction response time is much higher than normal")) {
                applicationDataMap.replace("Response Time Severity", Math.max(currResponseTimeSeverity, severity));
            }
        }

    }


    private void populateMetricFields(String appName, RESTAccess access) throws IllegalAccessException, IOException {

        //set boundaries. 2 weeks (20160 minutes), in this case.
        Calendar cal = Calendar.getInstance();
        long end = cal.getTimeInMillis();
        cal.add(Calendar.MINUTE, -NUM_MINUTES);
        long start = cal.getTimeInMillis();

        //metrics that need to be calculated (some "totals", "percents", etc. aren't provided by appdynamics)
        List<String> unknownMetrics = new ArrayList<>();

        //contains the names of the metrics requested by user
        for (Map.Entry<String, Double> entry : applicationDataMap.entrySet()) {
            String metricName = entry.getKey();

            double metricValue;
            // uses appdynamics api to obtain value. If it returns -1, it isn't a valid metric name--we have to calculate it.
            // "createPath" allows for generic code (e.g. "Total Calls" -> "Overall Application Performance|Total Calls"
            if ((metricValue = getMetricValue(appName, createPath(metricName), access, start, end)) == -1) {
                unknownMetrics.add(metricName);
                continue;
            }

            entry.setValue(metricValue);
        }

        //individually handle atypical possibilities (e.g. "Total Errors", "Node Health Percent", etc.)
        for (String metricName : unknownMetrics)
            applicationDataMap.replace(metricName, generateMetricValue(appName, metricName, access, start, end));


        buildViolationSeverityMap(appName, access, start, end);
        //testInit();
    }

    private double getMetricValue(String appName, String metricPath, RESTAccess access, long start, long end) throws IllegalAccessException {

        // generic call to appdynamics api to retrieve metric value
        List<MetricData> metricDataArr = access.getRESTGenericMetricQuery(appName, metricPath, start, end, true).getMetric_data();
        // if resulting array is empty, the metric doesn't exist--we have to calculate
        if (!metricDataArr.isEmpty())
            return metricDataArr.get(0).getSingleValue().getValue();
        return -1;
    }

    private double generateMetricValue(String appName, String metricName, RESTAccess access, long start, long end) throws IllegalAccessException {

        // we have "Errors per Minute", for example. Manipulating the names gives us a generic way to handle all totals
        if (metricName.contains("Total"))
            return NUM_MINUTES * applicationDataMap.get(totalToPerMinute(metricName));

        // must pull all of the nodes and all of the health violations.
        // 100 - (Num Violations / Num Nodes) = Node Health Percent
        if (metricName.equals("Node Health Percent"))
            return getNodeHealthPercent(appName, access, start, end);

        return -1;
    }


    private double getNodeHealthPercent(String appName, RESTAccess access, long start, long end) {

        //get # of violations, divide by # of nodes
        int numNodes = (access.getNodesForApplication(appName).getNodes()).size();
        int numViolations = (access.getHealthRuleViolations(appName, start, end)).getPolicyViolations().size();

        return 100.0 - (numViolations / numNodes);

    }

    private String totalToPerMinute(String currField) {

        return currField.replace("Total ", "") + " per Minute";
    }

    private String createPath(String currMember) {

        // "Open", "Close" part is deprecated. Needed when using reflection. Probably not anymore.
        return "Overall Application Performance|" + currMember.replace("OPEN", "(").replace("CLOSE", ")");
    }
/*
    private void testInit() throws IllegalAccessException {
        applicationDataMap.forEach((k, v) -> LOG.debug(k + ": " + v));
    }*/

//public List<String> getMetricsPathsAuto (RESTAccess access, AppdynamicsApplication app, String pathName) {
//
//    List<String> paths = new ArrayList<>();
//    MetricItems baseMetricList = access.getBaseMetricList(app.getAppName());
//    for (MetricItem mi: baseMetricList.getMetricItems()) {
//        if (mi.isFolder()) {
//            getMetricsPathsAuto(access, app, mi.getName());
//        } else {
//            paths.add()
//        }
//        MetricItems newItems = access.getBaseMetricListPath(app.getAppName(), mi.getName());
//        ArrayList<MetricItem> mis = newItems.getMetricItems();
//
//    }
//    return paths;
//}


    public void testingSDKCapabilities(RESTAccess access, AppdynamicsApplication app) {
        ExApplication exApplication = access.getApplicationExportObjById(Integer.parseInt(app.getAppID()));
        MetricItems baseMetricList = access.getBaseMetricList(app.getAppName());
        for (MetricItem mi : baseMetricList.getMetricItems()) {
            MetricItems newItems = access.getBaseMetricListPath(app.getAppName(), mi.getName());
            ArrayList<MetricItem> mis = newItems.getMetricItems();
        }
        Backends backends = access.getBackendsForApplication(app.getAppName());
        BusinessTransactions businessTransactions = access.getBTSForApplication(app.getAppName());
        Nodes nodes = access.getNodesForApplication(Integer.parseInt(app.getAppID()));
        ConfigurationItems configurationItems = access.getConfigurationItems(app.getAppName());
        String customePojoExportAll = access.getRESTCustomPojoExportAll(app.getAppName());
        String exportOfAuto = access.getRESTExportOfAuto(app.getAppName());
    }
}
