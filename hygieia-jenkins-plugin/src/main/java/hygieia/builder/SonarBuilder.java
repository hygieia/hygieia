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
    public static final String URL_METRIC_FRAGMENT = "/api/resources?format=json&resource=%s&metrics=%s&includealerts=true";
    public static final String METRICS = "security-violations,ncloc,violations,critical_violations,major_violations,blocker_violations,violations_density,tests,test_success_density,test_errors,test_failures,coverage,line_coverage,sqale_index";
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

    }

    public CodeQualityCreateRequest getSonarMetrics() throws ParseException {
        if (StringUtils.isEmpty(sonarServer) || StringUtils.isEmpty(sonarProjectID)) return null;
        String url = String.format(sonarServer + URL_METRIC_FRAGMENT, sonarProjectID, METRICS);
        RestCall restCall = new RestCall(publisher.getDescriptor().isUseProxy());
        RestCall.RestCallResponse callResponse = restCall.makeRestCallGet(url);
        int responseCode = callResponse.getResponseCode();
        if (responseCode == HttpStatus.SC_OK) {
            String resp = callResponse.getResponseString();
            return buildQualityRequest(resp);
        }
        listener.getLogger().println("Hygieia Publisher: Sonar Connection Failed. Response: " + responseCode);
        return null;
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
                metric.setValue(metricJson.get(VALUE));
                metric.setFormattedValue(str(metricJson, FORMATTED_VALUE));
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
