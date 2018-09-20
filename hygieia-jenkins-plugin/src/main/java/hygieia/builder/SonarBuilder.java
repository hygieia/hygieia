package hygieia.builder;

import com.capitalone.dashboard.model.CodeQualityMetric;
import com.capitalone.dashboard.model.CodeQualityMetricStatus;
import com.capitalone.dashboard.model.CodeQualityType;
import com.capitalone.dashboard.request.CodeQualityCreateRequest;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.util.IOUtils;
import jenkins.plugins.hygieia.RestCall;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.lang.InterruptedException;
import java.lang.String;
import java.lang.Thread;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static hygieia.utils.HygieiaUtils.getSafePositiveInteger;

public class SonarBuilder {
    private static final Logger logger = Logger.getLogger(SonarBuilder.class.getName());
    private static final String URL_VERSION = "/api/server/version";
    /**
     * Pattern for Sonar project URL in logs
     */
    public static final String URL_PATTERN_IN_LOGS = ".*" + Pattern.quote("ANALYSIS SUCCESSFUL, you can browse ") + "(.*)";
    public static final String URL_PROCESSING_STATUS_FRAGMENT = ".*" + Pattern.quote("More about the report processing at ") + "(.*)";

    public static final String URL_PROJECT_ID_FRAGMENT_PRE6_3 = "/api/projects/index?format=json&key=%s";
    public static final String URL_PROJECT_ID_FRAGMENT_POST6_3 = "/api/components/search?qualifiers=TRK&q=%s";


    public static final String URL_METRIC_FRAGMENT_PRE_6_3 = "/api/resources?format=json&resource=%s&metrics=%s&includealerts=true&includetrends=true";
    public static final String URL_METRICS_FRAGMENT_POST6_3 = "/api/measures/component?componentId=%s&metricKeys=%s";
    private static final String URL_PROJECT_ANALYSES = "/api/project_analyses/search?project=%s";

    public static final String METRICS_PRE6_3 = "quality_gate_details,ncloc,violations,critical_violations,major_violations,blocker_violations," +
            "violations_density,tests,test_success_density,test_errors,test_failures,coverage,line_coverage,sqale_index,new_violations," +
            "new_blocker_violations,new_critical_violations,new_major_violations,new_coverage,new_lines_to_cover,new_line_coverage";

    public static final String METRICS_POST6_3 = "alert_status,quality_gate_details,ncloc,new_vulnerabilities,violations,critical_violations,major_violations," +
            "blocker_violations,tests,test_success_density,test_errors,test_failures,coverage,line_coverage,sqale_index,new_violations,new_blocker_violations," +
            "new_critical_violations,new_major_violations,new_coverage,new_lines_to_cover,new_line_coverage";

    private static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ssZ";
    private static final String MINUTES_FORMAT = "%smin";
    private static final String HOURS_FORMAT = "%sh";
    private static final String DAYS_FORMAT = "%sd";

    private static final String ID = "id";
    private static final String NAME = "name";
    private static final String KEY = "key";
    private static final String VERSION = "version";
    private static final String MSR = "msr";
    private static final String MEASURES = "measures";
    private static final String ALERT = "alert";
    private static final String ALERT_TEXT = "alert_text";
    private static final String VAL = "val";
    private static final String VALUE = "value";
    private static final String FORMATTED_VALUE = "frmt_val";
    private static final String STATUS_WARN = "WARN";
    private static final String STATUS_ALERT = "ALERT";
    private static final String DATE = "date";
    private static final String EVENTS = "events";
    private static final String METRIC = "metric";
    private static final int HOURS_IN_DAY = 8;

    private String sonarServer;
    private String sonarProjectName;
    private String sonarProjectID;
    private String buildId;
    private TaskListener listener;

    private String sonarCEAPIUrl;
    private int ceQueryIntervalInSeconds;
    private int ceQueryMaxAttempts;
    private String jenkinsName;
    private boolean useProxy;

    private double sonarVersion;
    private String sonarBuildLink;

    private Run<?, ?> run;

    public static final int DEFAULT_QUERY_INTERVAL = 10;
    public static final int DEFAULT_QUERY_MAX_ATTEMPTS = 30;


    public SonarBuilder(Run<?, ?> run, TaskListener listener, String jenkinsName, String ceQueryIntervalInSeconds, String ceQueryMaxAttempts, String buildId, boolean useProxy) throws IOException, URISyntaxException, ParseException {
        this.listener = listener;
        this.jenkinsName = jenkinsName;
        this.ceQueryIntervalInSeconds = getSafePositiveInteger(ceQueryIntervalInSeconds, DEFAULT_QUERY_INTERVAL);
        this.ceQueryMaxAttempts = getSafePositiveInteger(ceQueryMaxAttempts, DEFAULT_QUERY_MAX_ATTEMPTS);
        this.buildId = buildId;
        this.jenkinsName = jenkinsName;
        this.useProxy = useProxy;
        this.run = run;
    }

    protected void setSonarDetails(Run run) throws IOException, URISyntaxException, ParseException {
        sonarBuildLink = extractSonarProjectURLFromLogs(run);
        if (!StringUtils.isEmpty(sonarBuildLink)) {
            this.sonarProjectName = getSonarProjectName(sonarBuildLink);
            this.sonarServer = sonarBuildLink.substring(0, sonarBuildLink.indexOf("/dashboard/index/" + this.sonarProjectName));
            this.sonarVersion = getSonarVersion();
            this.sonarProjectID = getSonarProjectID(this.sonarProjectName, sonarVersion);
        }
        String sonarCEAPILink = extractSonarProcessingStatusUrlFromLogs(run);
        if (!StringUtils.isEmpty(sonarCEAPILink)) {
            this.sonarCEAPIUrl = sonarCEAPILink;
        }
    }

    public double getSonarVersion() {
        RestCall restCall = new RestCall(useProxy);
        String url = sonarServer + URL_VERSION;
        RestCall.RestCallResponse callResponse = restCall.makeRestCallGet(url);
        int responseCode = callResponse.getResponseCode();
        if (responseCode == HttpStatus.SC_OK) {
            String[] parts = callResponse.getResponseString().split("\\.");
            return Double.parseDouble(parts[0] + "." + parts[1]);
        } else {
            listener.getLogger().println("Hygieia Publisher: Sonar Connection Failed: " + url + ". Response: " + responseCode);
            return 0;
        }
    }


    /**
     * Keeps polling Sonar's Compute Engine (CE) API to determine status of sonar analysis
     * From Sonar 5.2+, the final analysis is now an asynchronous and the status
     * of the sonar analysis needs to be determined from the Sonar CE API
     *
     * @param restCall
     * @return true after Compute Engine has completed processing or it is an old Sonar version.
     * Else returns false
     * @throws ParseException
     */
    private boolean sonarProcessingComplete(RestCall restCall) throws ParseException {
        // Sonar 5.2+ check if the sonar ce api url exists. If not,
        // then the project is using old sonar version and hence
        // request to Compute Engine api is not required.
        if (StringUtils.isEmpty(this.sonarCEAPIUrl)) {
            // request to CE API is not required as Sonar Version < 5.2
            return true;
        }

        // keep polling Sonar CE API for max configured attempts to fetch
        // status of sonar analysis. After every attempt if CE API is not yet
        // ready, sleep for configured interval period.
        // Return true as soon as the status changes to SUCCESS
        for (int i = 0; i < this.ceQueryMaxAttempts; i++) {
            // get the status of sonar analysis using Sonar CE API
            RestCall.RestCallResponse ceAPIResponse = restCall.makeRestCallGet(this.sonarCEAPIUrl);
            int responseCodeCEAPI = ceAPIResponse.getResponseCode();
            if (responseCodeCEAPI == HttpStatus.SC_OK) {
                String taskStatus = getSonarTaskStatus(ceAPIResponse.getResponseString());
                switch (taskStatus) {
                    case "IN_PROGRESS":
                    case "PENDING":
                        // Wait the configured interval then retry
                        listener.getLogger().println("Waiting for report processing to complete...");
                        try {
                            Thread.sleep(this.ceQueryIntervalInSeconds * 1000);
                        } catch (InterruptedException ie) {
                            listener.getLogger().println("Sonar report processing errored while getting the status...");
                            return false;
                        }
                        break;
                    case "SUCCESS":
                        // Exit
                        listener.getLogger().println("Sonar report processing completed...");
                        return true;
                    default:
                        listener.getLogger().println("Hygieia Publisher: Sonar CE API returned bad status: " + taskStatus);
                        return false;
                }

            } else {
                listener.getLogger().println("Hygieia Publisher: Sonar CE API Connection failed. Response: " + responseCodeCEAPI);
                return false;
            }
        }
        listener.getLogger().println("Hygieia Publisher: Sonar CE API could not return response on time.");
        return false;
    }


    /***
     * Parses the task status as returned from Sonar's CE API
     * @param ceTaskResponse
     * @return value of status element in the CE API Response
     * @throws org.json.simple.parser.ParseException
     */
    private String getSonarTaskStatus(String ceTaskResponse) throws org.json.simple.parser.ParseException {
        JSONObject ceTaskResponseObject = (JSONObject) new org.json.simple.parser.JSONParser().parse(ceTaskResponse);
        JSONObject task = (JSONObject) ceTaskResponseObject.get("task");
        return str(task, "status");
    }


    public CodeQualityCreateRequest getSonarMetrics() throws ParseException, IOException, URISyntaxException {
        setSonarDetails(this.run);
        if (StringUtils.isEmpty(sonarServer) || StringUtils.isEmpty(sonarProjectID)) return null;

        if (sonarVersion >= 6.3) {
            return getSonarMetricsPost6_3();
        } else {
            return getSonarMetricsPre6_3();
        }
    }


    public CodeQualityCreateRequest getSonarMetricsPre6_3() throws ParseException {
        String url = String.format(sonarServer + URL_METRIC_FRAGMENT_PRE_6_3, sonarProjectID, METRICS_PRE6_3);
        RestCall restCall = new RestCall(useProxy);
        //sonar 5.2+ changes - CE api
        if (sonarProcessingComplete(restCall)) {
            RestCall.RestCallResponse callResponse = restCall.makeRestCallGet(url);
            int responseCode = callResponse.getResponseCode();
            if (responseCode == HttpStatus.SC_OK) {
                String resp = callResponse.getResponseString();
                return buildQualityRequest_PRE6_3(resp);
            }
            listener.getLogger().println("Hygieia Publisher: Sonar Connection Failed: " + url + ". Response: " + responseCode);
            return null;
        } else {
            listener.getLogger().println("Hygieia Publisher: Sonar Compute Engine API Failed. ");
            return null;
        }
    }


    private CodeQualityCreateRequest buildQualityRequest_PRE6_3(String json) throws ParseException {
        JSONArray jsonArray = (JSONArray) new JSONParser().parse(json);
        if (!jsonArray.isEmpty()) {
            JSONObject prjData = (JSONObject) jsonArray.get(0);
            CodeQualityCreateRequest codeQuality = new CodeQualityCreateRequest();
            codeQuality.setProjectName(str(prjData, NAME));
            codeQuality.setProjectUrl(sonarServer + "/dashboard/index/" + sonarProjectID);
            codeQuality.setNiceName(jenkinsName);
            codeQuality.setType(CodeQualityType.StaticAnalysis);
            codeQuality.setTimestamp(timestamp(prjData,  DATE));
            codeQuality.setProjectVersion(str(prjData, VERSION));
            codeQuality.setHygieiaId(buildId);
            codeQuality.setProjectId(sonarProjectID);
            codeQuality.setServerUrl(sonarServer);
            codeQuality.setProjectVersion(str(prjData, "version"));
            for (Object metricObj : (JSONArray) prjData.get(MSR)) {
                JSONObject metricJson = (JSONObject) metricObj;
                CodeQualityMetric metric = new CodeQualityMetric(str(metricJson, KEY));


                // if data element is set, set data into value property
                // this usually happens for custom metrics
                if (metricJson.get("data") != null) {
                    metric.setFormattedValue(metricJson.get("data").toString());
                    metric.setValue(str(metricJson, "data"));
                } else if (metric.getName().startsWith("new_")) {
                    // for new  metrics- use var2 and fvar2
                    // this is because var2 and fvar2 represents values since
                    // last analysis
                    if (metricJson.get("var2") != null || metricJson.get("fvar2") != null) {
                        metric.setValue(str(metricJson, "var2"));
                        metric.setFormattedValue(str(metricJson, "fvar2"));
                    }
                } else {
                    // for other regular metrics - use default fields
                    metric.setValue(str(metricJson, VAL));
                    metric.setFormattedValue(str(metricJson, FORMATTED_VALUE));
                }
                metric.setStatus(metricStatus(str(metricJson, ALERT)));
                metric.setStatusMessage(str(metricJson, ALERT_TEXT));
                codeQuality.getMetrics().add(metric);
            }
            return codeQuality;
        }
        return null;
    }


    public CodeQualityCreateRequest getSonarMetricsPost6_3() throws ParseException {
        String url = String.format(
                sonarServer + URL_METRICS_FRAGMENT_POST6_3, sonarProjectID, METRICS_POST6_3);
        RestCall restCall = new RestCall(useProxy);
        RestCall.RestCallResponse response = restCall.makeRestCallGet(url);

        JSONParser jsonParser = new JSONParser();
        JSONObject jsonObject = (JSONObject) jsonParser.parse(response.getResponseString());
        String key = "component";

        if (jsonObject != null) {
            JSONObject prjData = (JSONObject) jsonObject.get(key);

            CodeQualityCreateRequest codeQuality = new CodeQualityCreateRequest();
            codeQuality.setType(CodeQualityType.StaticAnalysis);
            codeQuality.setProjectName(str(prjData, NAME));
            codeQuality.setProjectUrl(sonarBuildLink);
            codeQuality.setServerUrl(sonarServer);
            codeQuality.setProjectId(str(prjData, ID));
            codeQuality.setNiceName(jenkinsName);
            codeQuality.setHygieiaId(buildId);
            url = String.format(
                    sonarServer + URL_PROJECT_ANALYSES, str(prjData, KEY));
            RestCall.RestCallResponse analysisResponse = restCall.makeRestCallGet(url);
            key = "analyses";
            JSONObject analysisJSONObject = (JSONObject) jsonParser.parse(analysisResponse.getResponseString());
            JSONArray jsonArray = (JSONArray) analysisJSONObject.get(key);
            JSONObject prjLatestData = (JSONObject) jsonArray.get(0);
            codeQuality.setTimestamp(timestamp(prjLatestData, DATE));
            for (Object eventObj : (JSONArray) prjLatestData.get(EVENTS)) {
                JSONObject eventJson = (JSONObject) eventObj;

                if (str(eventJson, "category").equals("VERSION")) {
                    codeQuality.setProjectVersion(str(eventJson, NAME));
                }
            }

            for (Object metricObj : (JSONArray) prjData.get(MEASURES)) {
                JSONObject metricJson = (JSONObject) metricObj;

                CodeQualityMetric metric = new CodeQualityMetric(str(metricJson, METRIC));
                metric.setValue(str(metricJson, VALUE));
                if (metric.getName().equals("sqale_index")) {
                    metric.setFormattedValue(format(str(metricJson, VALUE)));
                } else if (str(metricJson, VALUE).indexOf(".") > 0) {
                    metric.setFormattedValue(str(metricJson, VALUE) + "%");
                } else if (str(metricJson, VALUE).matches("\\d+")) {
                    metric.setFormattedValue(String.format("%,d", integer(metricJson, VALUE)));
                } else {
                    metric.setFormattedValue(str(metricJson, VALUE));
                }
                codeQuality.getMetrics().add(metric);
            }

            return codeQuality;
        }
        return null;
    }


    private CodeQualityMetricStatus metricStatus(String status) {
        if (StringUtils.isBlank(status)) {
            return CodeQualityMetricStatus.Ok;
        }
        if (status.equalsIgnoreCase(STATUS_WARN)) {
            return CodeQualityMetricStatus.Warning;
        }
        if (status.equalsIgnoreCase(STATUS_WARN)) {
            return CodeQualityMetricStatus.Alert;
        }
        return CodeQualityMetricStatus.Ok;

    }

    /**
     * Read logs of the build to find URL of the project dashboard in Sonar
     */
    private String extractSonarProjectURLFromLogs(Run run) throws IOException {
        return getSonarUrl(run.getLogReader(), URL_PATTERN_IN_LOGS);
    }


    /**
     * Sonar 5.3 Changes: As per changes in Sonar 5.3 onwards, the sonar analysis update on server
     * is now processed asynchronously on server. Sonar provides an API called Compute Engine (CE)
     * whihc needs to be polled regularly to determine status of the analysis. URL of CE API can be taken from logs
     */
    public String extractSonarProcessingStatusUrlFromLogs(Run run) throws IOException {
        return getSonarUrl(run.getLogReader(), URL_PROCESSING_STATUS_FRAGMENT);
    }

    private String getSonarUrl(Reader reader, String pattern) throws IOException {
        BufferedReader br = null;
        String url = null;
        try {
            br = new BufferedReader(reader);
            String strLine;
            Pattern p = Pattern.compile(pattern);
            while ((strLine = br.readLine()) != null) {
                Matcher match = p.matcher(strLine);
                if (match.matches()) {
                    url = match.group(1);
                }
            }
        } finally {
            IOUtils.closeQuietly(br);
        }
        return url;
    }

    private String getSonarProjectName(String url) throws URISyntaxException {
        URI uri = new URI(url);
        String[] segments = uri.getPath().split("/");
        if (segments.length > 1) {
            return segments[segments.length - 1];
        } else return "";
    }

    private String getSonarProjectID(String project, double sonarVersion) throws IOException, URISyntaxException, ParseException {
        if (sonarVersion < 6.3) {
            return getSonarProjectID_PRE6_3(project);
        } else {
            return getSonarProjectID_POST6_3(project);
        }
    }

    private String getSonarProjectID_PRE6_3(String project) throws ParseException {
        String url = String.format(sonarServer + URL_PROJECT_ID_FRAGMENT_PRE6_3, project);
        RestCall restCall = new RestCall(useProxy);
        RestCall.RestCallResponse callResponse = restCall.makeRestCallGet(url);
        int responseCode = callResponse.getResponseCode();
        if (responseCode == HttpStatus.SC_OK) {
            String resp = callResponse.getResponseString();
            JSONArray arr = (JSONArray) new JSONParser().parse(resp);
            JSONObject obj = (JSONObject) arr.get(0);
            return str(obj, "id");
        }
        logger.log(Level.WARNING, "Hygieia getSonarProjectID Failed. Response: " + responseCode);
        return "";
    }

    private String getSonarProjectID_POST6_3(String project) throws ParseException {
        String url = String.format(sonarServer + URL_PROJECT_ID_FRAGMENT_POST6_3, project);
        RestCall restCall = new RestCall(useProxy);
        RestCall.RestCallResponse callResponse = restCall.makeRestCallGet(url);
        int responseCode = callResponse.getResponseCode();
        if (responseCode == HttpStatus.SC_OK) {
            String resp = callResponse.getResponseString();
            JSONObject body = (JSONObject) new JSONParser().parse(resp);
            JSONArray arr = (JSONArray) body.get("components");
            if (!CollectionUtils.isEmpty(arr)) {
                JSONObject obj = (JSONObject) arr.get(0);
                return str(obj, "id");
            }
            logger.log(Level.WARNING, "Hygieia getSonarProjectID Failed. Response: " + callResponse.getResponseString());
        }
        logger.log(Level.WARNING, "Hygieia getSonarProjectID Failed. Response: " + responseCode);
        return "";
    }


    private String str(JSONObject json, String key) {
        Object obj = json.get(key);
        return obj == null ? "" : obj.toString();
    }

    private Integer integer(JSONObject json, String key) {
        Object obj = json.get(key);
        return obj == null ? 0 : Integer.valueOf(obj.toString());
    }


    private String format(String duration) {
        Long durationInMinutes = Long.valueOf(duration);
        if (durationInMinutes == 0) {
            return "0";
        }
        boolean isNegative = durationInMinutes < 0;
        Long absDuration = Math.abs(durationInMinutes);

        int days = ((Double) ((double) absDuration / HOURS_IN_DAY / 60)).intValue();
        Long remainingDuration = absDuration - (days * HOURS_IN_DAY * 60);
        int hours = ((Double) (remainingDuration.doubleValue() / 60)).intValue();
        remainingDuration = remainingDuration - (hours * 60);
        int minutes = remainingDuration.intValue();

        return format(days, hours, minutes, isNegative);
    }

    private static String format(int days, int hours, int minutes, boolean isNegative) {
        StringBuilder message = new StringBuilder();
        if (days > 0) {
            message.append(String.format(DAYS_FORMAT, isNegative ? (-1 * days) : days));
        }
        if (displayHours(days, hours)) {
            addSpaceIfNeeded(message);
            message.append(String.format(HOURS_FORMAT, isNegative && message.length() == 0 ? (-1 * hours) : hours));
        }
        if (displayMinutes(days, hours, minutes)) {
            addSpaceIfNeeded(message);
            message.append(String.format(MINUTES_FORMAT, isNegative && message.length() == 0 ? (-1 * minutes) : minutes));
        }
        return message.toString();
    }

    private static boolean displayHours(int days, int hours) {
        return hours > 0 && days < 10;
    }

    private static boolean displayMinutes(int days, int hours, int minutes) {
        return minutes > 0 && hours < 10 && days == 0;
    }

    private static void addSpaceIfNeeded(StringBuilder message) {
        if (message.length() > 0) {
            message.append(" ");
        }
    }



    private long timestamp(JSONObject json, String key) {
        Object obj = json.get(key);
        if (obj != null) {
            try {
                return new SimpleDateFormat(DATE_FORMAT).parse(obj.toString()).getTime();
            } catch (java.text.ParseException e) {
                logger.warning(obj + " is not in expected format " + DATE_FORMAT + e);
            }
        }
        return 0;
    }
}
