package hygieia.builder;

import com.capitalone.dashboard.model.CodeQualityMetric;
import com.capitalone.dashboard.model.CodeQualityMetricStatus;
import com.capitalone.dashboard.model.CodeQualityType;
import com.capitalone.dashboard.request.CodeQualityCreateRequest;
import hudson.model.AbstractBuild;
import hudson.model.BuildListener;
import hudson.util.IOUtils;
import jenkins.plugins.hygieia.HygieiaPublisher;
import jenkins.plugins.hygieia.RestCall;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import java.io.BufferedReader;
import java.io.IOException;
import java.lang.Integer;
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

public class SonarBuilder {
    private static final Logger logger = Logger.getLogger(SonarBuilder.class.getName());
    /**
     * Pattern for Sonar project URL in logs
     */
    public static final String URL_PATTERN_IN_LOGS = ".*" + Pattern.quote("ANALYSIS SUCCESSFUL, you can browse ") + "(.*)";
    public static final String URL_PROJECT_ID_FRAGMENT = "/api/projects?format=json&key=%s";

    public static final String CE_URL_PATTERN_IN_LOGS = ".*" + Pattern.quote("More about the report processing at ") + "(.*)";
    public static final String CE_URL_PROJECT_ID_FRAGMENT = "/ce/task?id=%s";

    public static final String URL_METRIC_FRAGMENT = "/api/resources?format=json&resource=%s&metrics=%s&includealerts=true&includetrends=true";
    public static final String METRICS = "security-violations,ncloc,violations,critical_violations,major_violations,blocker_violations,violations_density,tests,test_success_density,test_errors,test_failures,coverage,line_coverage,sqale_index,new_violations,new_blocker_violations,new_critical_violations,new_major_violations,new_coverage,new_lines_to_cover,new_line_coverage";
    private static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ssZ";
    private static final String ID = "id";
    private static final String NAME = "name";
    private static final String KEY = "key";
    private static final String VERSION = "version";
    private static final String MSR = "msr";
    private static final String ALERT = "alert";
    private static final String ALERT_TEXT = "alert_text";
    private static final String VALUE = "val";
    private static final String FORMATTED_VALUE = "frmt_val";
    private static final String STATUS_WARN = "WARN";
    private static final String STATUS_ALERT = "ALERT";
    private static final String DATE = "date";

    private String sonarServer;
    private String sonarProjectName;
    private String sonarProjectID;
    private String buildId;
    private BuildListener listener;
    private HygieiaPublisher publisher;
    private String sonarCEAPIUrl;
    private int ceQueryIntervalInSeconds;
    private int ceQueryMaxAttempts;

    /**
     * Hide utility-class constructor.
     */
    public SonarBuilder(AbstractBuild<?, ?> build, HygieiaPublisher publisher, BuildListener listener, String buildId) throws IOException, URISyntaxException, ParseException {
        this.listener = listener;
        this.publisher = publisher;
        setSonarDetails(build, buildId);
    }

    private void setSonarDetails(AbstractBuild build, String buildId) throws IOException, URISyntaxException, ParseException {
        String sonarBuildLink = extractSonarProjectURLFromLogs(build);
        this.buildId = buildId;
        if (!StringUtils.isEmpty(sonarBuildLink)) {
            this.sonarProjectName = getSonarProjectName(sonarBuildLink);
            this.sonarServer = sonarBuildLink.substring(0, sonarBuildLink.indexOf("/dashboard/index/" + this.sonarProjectName));
            this.sonarProjectID = getSonarProjectID(this.sonarProjectName);
        }
        //sonar 5.3 changes
        String sonarCEAPILink =  extractSonarProjectCEUrlFromLogs(build);
        if (!StringUtils.isEmpty(sonarCEAPILink)) {
            this.sonarCEAPIUrl = sonarCEAPILink;
            String queryIntervalFromConfig = StringUtils.defaultIfBlank(publisher.getHygieiaSonar().getCeQueryIntervalInSeconds(), "10");
            String queryMaxAttempts = StringUtils.defaultIfBlank(publisher.getHygieiaSonar().getCeQueryMaxAttempts(), "30");

            try {
                this.ceQueryIntervalInSeconds = Integer.parseInt(queryIntervalFromConfig);
            } catch (java.lang.NumberFormatException nfe) {
                // the value could not be fetched from config, use the
                // Sonar recommended value for query interval
                this.ceQueryIntervalInSeconds = publisher.getHygieiaSonar().DEFAULT_QUERY_INTERVAL;
            }
            try {
                this.ceQueryMaxAttempts = Integer.parseInt(queryMaxAttempts);
            } catch (java.lang.NumberFormatException nfe) {
                // the value could not be fetched from config, use the
                // Sonar recommended value for query max attempts
                this.ceQueryMaxAttempts = publisher.getHygieiaSonar().DEFAULT_QUERY_MAX_ATTEMPTS;
            }
        }

    }

    /** Keeps polling Sonar's Compute Engine (CE) API to determine status of sonar analysis
     * From Sonar 5.2+, the final analysis is now an asynchronous and the status
     * of the sonar analysis needs to be determined from the Sonar CE API
     * @param restCall
     * @return true after Compute Engine has completed processing or it is an old Sonar version.
     * Else returns false
     * @throws ParseException
     */
    private boolean ceProcessingComplete(RestCall restCall) throws ParseException {
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
        for(int i=0;i<this.ceQueryMaxAttempts;i++) {
            // get the status of sonar analysis using Sonar CE API
            RestCall.RestCallResponse ceAPIResponse = restCall.makeRestCallGet(this.sonarCEAPIUrl);
            int responseCodeCEAPI = ceAPIResponse.getResponseCode();
            if (responseCodeCEAPI == HttpStatus.SC_OK) {
                String taskStatus = getCETaskStatus(ceAPIResponse.getResponseString());
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

            }
            else {
                listener.getLogger().println("Hygieia Publisher: Sonar CE API Connection failed. Response: " + responseCodeCEAPI);
                return false;
            }
        }
        listener.getLogger().println("Hygieia Publisher: Sonar CE API could not return response on time.");
        return false;
    }


    public CodeQualityCreateRequest getSonarMetrics() throws ParseException {
        if (StringUtils.isEmpty(sonarServer) || StringUtils.isEmpty(sonarProjectID)) return null;
        String url = String.format(sonarServer + URL_METRIC_FRAGMENT, sonarProjectID, METRICS);
        RestCall restCall = new RestCall(publisher.getDescriptor().isUseProxy());

        //sonar 5.2+ changes - CE api
        if(ceProcessingComplete(restCall)) {
            RestCall.RestCallResponse callResponse = restCall.makeRestCallGet(url);
            int responseCode = callResponse.getResponseCode();
            if (responseCode == HttpStatus.SC_OK) {
                String resp = callResponse.getResponseString();
                return buildQualityRequest(resp);
            }
            listener.getLogger().println("Hygieia Publisher: Sonar Connection Failed. Response: " + responseCode);
            return null;
        }
        else {
            listener.getLogger().println("Hygieia Publisher: Sonar Compute Engine API Failed. ");
            return null;
        }

    }

    /***
     * Parses the task status as returned from Sonar's CE API
     * @param ceTaskResponse
     * @return value of status element in the CE API Response
     * @throws org.json.simple.parser.ParseException
     */
    private String getCETaskStatus(String ceTaskResponse) throws org.json.simple.parser.ParseException {
        JSONObject ceTaskResponseObject = (JSONObject) new org.json.simple.parser.JSONParser().parse(ceTaskResponse);
        JSONObject task = (JSONObject) ceTaskResponseObject.get("task");
        return str(task, "status");
    }

    private CodeQualityCreateRequest buildQualityRequest(String json) throws ParseException {
        JSONArray jsonArray = (JSONArray) new JSONParser().parse(json);
        if (!jsonArray.isEmpty()) {
            JSONObject prjData = (JSONObject) jsonArray.get(0);
            CodeQualityCreateRequest codeQuality = new CodeQualityCreateRequest();
            codeQuality.setProjectName(str(prjData, NAME));
            codeQuality.setProjectUrl(sonarServer + "/dashboard/index/" + sonarProjectID);
            codeQuality.setNiceName(publisher.getDescriptor().getHygieiaJenkinsName());
            codeQuality.setType(CodeQualityType.StaticAnalysis);
            codeQuality.setTimestamp(timestamp(prjData, DATE));
            codeQuality.setProjectVersion(str(prjData, VERSION));
            codeQuality.setHygieiaId(buildId);
            codeQuality.setProjectId(sonarProjectID);
            codeQuality.setServerUrl(sonarServer);
            for (Object metricObj : (JSONArray) prjData.get(MSR)) {
                JSONObject metricJson = (JSONObject) metricObj;
                CodeQualityMetric metric = new CodeQualityMetric(str(metricJson, KEY));


                // if data element is set, set data into value property
                // this usually happens for custom metrics
                if(metricJson.get("data") != null) {
                    metric.setFormattedValue(metricJson.get("data").toString());
                    metric.setValue(metricJson.get("data"));
                }
                else if (metric.getName().startsWith("new_")) {
                    // for new  metrics- use var2 and fvar2
                    // this is because var2 and fvar2 represents values since
                    // last analysis
                    if (metricJson.get("var2") != null || metricJson.get("fvar2") != null) {
                        metric.setValue(metricJson.get("var2"));
                        metric.setFormattedValue(str(metricJson, "fvar2"));
                    }
                }
                else {
                    // for other regular metrics - use default fields
                    metric.setValue(metricJson.get(VALUE));
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
    private String extractSonarProjectURLFromLogs(AbstractBuild build) throws IOException {
        BufferedReader br = null;
        String url = null;
        try {
            br = new BufferedReader(build.getLogReader());
            String strLine;
            Pattern p = Pattern.compile(URL_PATTERN_IN_LOGS);
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

    /**
     * Sonar 5.3 Changes: As per changes in Sonar 5.3 onwards, the sonar analysis update on server
     * is now processed asynchronously on server. Sonar provides an API called Compute Engine (CE)
     * whihc needs to be polled regularly to determine status of the analysis. URL of CE API can be taken from logs
     */
    public String extractSonarProjectCEUrlFromLogs(AbstractBuild build) throws IOException {
        BufferedReader br = null;
        String url = null;
        try {
            br = new BufferedReader(build.getLogReader());
            String strLine;
            Pattern p = Pattern.compile(CE_URL_PATTERN_IN_LOGS);
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

    private String getSonarProjectID(String project) throws IOException, URISyntaxException, ParseException {
        String url = String.format(sonarServer + URL_PROJECT_ID_FRAGMENT, project);
        RestCall restCall = new RestCall(publisher.getDescriptor().isUseProxy());
        RestCall.RestCallResponse callResponse = restCall.makeRestCallGet(url);
        int responseCode = callResponse.getResponseCode();
        if (responseCode == HttpStatus.SC_OK) {
            String resp = callResponse.getResponseString();
            JSONArray arr = (JSONArray) new JSONParser().parse(resp);
            JSONObject obj = (JSONObject) arr.get(0);
            return str(obj, "id");
        }
        logger.log(Level.WARNING, "Hygieia Sonar Connection Failed. Response: " + responseCode);
        return "";
    }

    private String str(JSONObject json, String key) {
        Object obj = json.get(key);
        return obj == null ? null : obj.toString();
    }
}
