package com.capitalone.dashboard.collector;


import com.capitalone.dashboard.model.Build;
import com.capitalone.dashboard.model.JenkinsJob;
import com.capitalone.dashboard.model.TestCapability;
import com.capitalone.dashboard.model.TestCaseStatus;
import com.capitalone.dashboard.model.TestResult;
import com.capitalone.dashboard.model.TestSuite;
import com.capitalone.dashboard.model.TestSuiteType;
import com.capitalone.dashboard.util.Supplier;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestOperations;

import java.net.MalformedURLException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

//@SuppressWarnings("PMD.ExcessiveMethodLength") // getCucumberTestResult needs refactor!
@Component
public class DefaultJenkinsClient implements JenkinsClient {

    private static final Log LOG = LogFactory.getLog(DefaultJenkinsClient.class);

    private final RestOperations rest;
    private final Transformer<String, List<TestSuite>> cucumberTransformer;
    private final Pattern cucumberJsonFilePattern;
    private final JenkinsSettings settings;

    private static final String JOBS_URL_SUFFIX = "/api/json?tree=jobs[name,url,builds[number,url],lastSuccessfulBuild[number]]";
    private static final String LAST_SUCCESSFUL_BUILD = "/lastSuccessfulBuild";
    private static final String LAST_SUCCESSFUL_BUILD_SUFFIX = "/lastSuccessfulBuild/api/json?tree=url,timestamp,number,fullDisplayName";
    private static final String LAST_SUCCESSFUL_BUILD_ARTIFACT_SUFFIX = "/lastSuccessfulBuild/api/json?tree=timestamp,duration,number,fullDisplayName,building,artifacts[fileName,relativePath]";

    @Autowired
    public DefaultJenkinsClient(Supplier<RestOperations> restOperationsSupplier,
                                Transformer<String, List<TestSuite>> cucumberTransformer,
                                JenkinsSettings settings) {
        this.rest = restOperationsSupplier.get();
        this.cucumberTransformer = cucumberTransformer;
        this.cucumberJsonFilePattern = Pattern.compile(settings.getCucumberJsonRegex());
        this.settings = settings;
    }

    @Override
    public Map<JenkinsJob, Set<Build>> getInstanceJobs(String instanceUrl) {
        Map<JenkinsJob, Set<Build>> result = new LinkedHashMap<>();
        try {
            JSONObject object = (JSONObject) new JSONParser().parse(getJson(instanceUrl, JOBS_URL_SUFFIX));

            for (Object job : getJsonArray(object, "jobs")) {
                JSONObject jsonJob = (JSONObject) job;
                JSONObject lastSuccessful = (JSONObject) jsonJob.get("lastSuccessfulBuild");
                if (lastSuccessful != null) {
                    JenkinsJob jenkinsJob = new JenkinsJob();
                    jenkinsJob.setInstanceUrl(instanceUrl);
                    jenkinsJob.setJobName(getString(jsonJob, "name"));
                    jenkinsJob.setJobUrl(getString(jsonJob, "url"));

                    Set<Build> builds = new LinkedHashSet<>();
                    result.put(jenkinsJob, builds);

                    for (Object build : getJsonArray(jsonJob, "builds")) {
                        JSONObject jsonBuild = (JSONObject) build;

                        // A basic Build object. This will be fleshed out later if this is a new Build.
                        Build hudsonBuild = new Build();
                        hudsonBuild.setNumber(jsonBuild.get("number").toString());
                        hudsonBuild.setBuildUrl(getString(jsonBuild, "url"));
                        builds.add(hudsonBuild);
                    }
                }
            }
        } catch (ParseException e) {
            LOG.error("Parsing jobs on instance: " + instanceUrl, e);
        } catch (RestClientException rce) {
            LOG.error(rce);
        }

        return result;
    }


    @Override
    public Build getLastSuccessfulBuild(String buildUrl) {
        Build build = new Build();
        try {
            // Get Build info
            JSONObject buildJson = (JSONObject) new JSONParser().parse(getJson(buildUrl, LAST_SUCCESSFUL_BUILD_SUFFIX));
            build.setBuildUrl(getString(buildJson, "url"));
            build.setNumber(buildJson.get("number").toString());
            build.setTimestamp(getLong(buildJson, "timestamp"));

        } catch (ParseException e) {
            LOG.error("Parsing jobs on instance: " + buildUrl, e);
        } catch (HttpClientErrorException hce) {
            if (hce.getStatusCode() != HttpStatus.NOT_FOUND) {
                LOG.error("HTTP Client Exception for: " + buildUrl, hce);
            }
        }
        return build;
    }

    @Override
    public boolean buildHasCucumberResults(String buildUrl) {

        JSONObject buildJson;
        try {
            // Get Build info
            buildJson = (JSONObject) new JSONParser().parse(getJson(buildUrl, LAST_SUCCESSFUL_BUILD_ARTIFACT_SUFFIX));

            Boolean building = (Boolean) buildJson.get("building");

            if (building != null && !building) {
                for (Object artifactObj : (JSONArray) buildJson.get("artifacts")) {
                    JSONObject artifact = (JSONObject) artifactObj;

                    // return true if we find an archived file that matches the naming of the regex config
                    if (cucumberJsonFilePattern.matcher(getString(artifact, "fileName")).matches()) {
                        return true;
	                        // TODO: maybe we want to validate that we can parse the json
	                        //String cucumberJson = getCucumberJson(buildUrl, getString(artifact, "relativePath"));
	                        //suites.addAll(cucumberTransformer.transformer(cucumberJson));
                    }
                }
            }
            return false;

        } catch (ParseException e) {
            LOG.error("Parsing jobs on instance: " + buildUrl, e);
        } catch (HttpClientErrorException hce) {
            if (hce.getStatusCode() != HttpStatus.NOT_FOUND) {
                LOG.error("HTTP Client Exception for: " + buildUrl, hce);
            }
        }

        return false;
    }


    protected List<TestCapability> getCapabilities(JSONObject buildJson, String buildUrl) {
        List<TestCapability> capabilities = new ArrayList<>();

        for (Object artifactObj : (JSONArray) buildJson.get("artifacts")) {
            JSONObject artifact = (JSONObject) artifactObj;
            if (cucumberJsonFilePattern.matcher(getString(artifact, "fileName")).matches()) {
                String cucumberJson = getCucumberJson(buildUrl, getString(artifact, "relativePath"));
                if (!StringUtils.isEmpty(cucumberJson)) {
                    TestCapability cap = new TestCapability();
                    cap.setType(TestSuiteType.Functional);
                    List<TestSuite> testSuites = cucumberTransformer.transformer(cucumberJson);
                    cap.setDescription(getCapabilityDescription(cucumberJsonFilePattern.pattern(), getString(artifact, "relativePath")));
                    cap.getTestSuites().addAll(testSuites); //add test suites
                    long duration = 0;
                    int testSuiteSkippedCount = 0, testSuiteSuccessCount = 0, testSuiteFailCount = 0, testSuiteUnknownCount = 0;
                    for (TestSuite t : testSuites) {
                        duration += t.getDuration();
                        switch (t.getStatus()) {
                            case Success:
                                testSuiteSuccessCount++;
                                break;
                            case Failure:
                                testSuiteFailCount++;
                                break;
                            case Skipped:
                                testSuiteSkippedCount++;
                                break;
                            default:
                                testSuiteUnknownCount++;
                                break;
                        }
                    }
                    if (testSuiteFailCount > 0) {
                        cap.setStatus(TestCaseStatus.Failure);
                    } else if (testSuiteSkippedCount > 0) {
                        cap.setStatus(TestCaseStatus.Skipped);
                    } else if (testSuiteSuccessCount > 0) {
                        cap.setStatus(TestCaseStatus.Success);
                    } else {
                        cap.setStatus(TestCaseStatus.Unknown);
                    }
                    cap.setFailedTestSuiteCount(testSuiteFailCount);
                    cap.setSkippedTestSuiteCount(testSuiteSkippedCount);
                    cap.setSuccessTestSuiteCount(testSuiteSuccessCount);
                    cap.setUnknownStatusTestSuiteCount(testSuiteUnknownCount);
                    cap.setTotalTestSuiteCount(testSuites.size());
                    cap.setDuration(duration);
                    cap.setExecutionId(buildJson.get("number").toString());
                    capabilities.add(cap);
                }
            }
        }
        return capabilities;
    }

    protected TestResult buildTestResultObject(JSONObject buildJson, String buildUrl, List<TestCapability> capabilities) {
        if (!capabilities.isEmpty()) {
            // There are test suites so let's construct a TestResult to encapsulate these results
            TestResult testResult = new TestResult();
            testResult.setDescription(getString(buildJson, "fullDisplayName"));
            testResult.setExecutionId(buildJson.get("number").toString());
            testResult.setUrl(buildUrl);
            // Using the build times for start, end and duration is not ideal but the Cucumber JSON does not capture
            // start or end times
            testResult.setDuration(getLong(buildJson, "duration"));
            testResult.setEndTime(getLong(buildJson, "timestamp"));
            testResult.setStartTime(testResult.getEndTime() - testResult.getDuration());
            testResult.getTestCapabilities().addAll(capabilities);  //add all capabilities
            testResult.setTotalCount(capabilities.size());
            int testCapabilitySkippedCount = 0, testCapabilitySuccessCount = 0, testCapabilityFailCount = 0;
            int testCapabilityUnknownCount = 0;
            // Calculate counts based on test suites
            for (TestCapability cap : capabilities) {
                switch (cap.getStatus()) {
                    case Success:
                        testCapabilitySuccessCount++;
                        break;
                    case Failure:
                        testCapabilityFailCount++;
                        break;
                    case Skipped:
                        testCapabilitySkippedCount++;
                        break;
                    default:
                        testCapabilityUnknownCount++;
                        break;
                }
            }
            testResult.setSuccessCount(testCapabilitySuccessCount);
            testResult.setFailureCount(testCapabilityFailCount);
            testResult.setSkippedCount(testCapabilitySkippedCount);
            testResult.setUnknownStatusCount(testCapabilityUnknownCount);
            return testResult;
        }
        return null;
    }


    @Override
    public TestResult getCucumberTestResult(String buildUrl) {
        try {
            JSONObject buildJson = (JSONObject) new JSONParser().parse(getJson(buildUrl, LAST_SUCCESSFUL_BUILD_ARTIFACT_SUFFIX));
            List<TestCapability> capabilities = getCapabilities(buildJson, buildUrl);
            return buildTestResultObject(buildJson, buildUrl, capabilities);
        } catch (ParseException e) {
            LOG.error("Parsing jobs on instance: " + buildUrl, e);
        } catch (RestClientException rce) {
            LOG.error(rce);
        }
        // An exception occurred or this build does not have cucumber test results
        return null;
    }


    protected String getCucumberJson(String buildUrl, String artifactRelativePath) {
        return getJson(StringUtils.removeEnd(buildUrl, "/") + LAST_SUCCESSFUL_BUILD, "/artifact/" + artifactRelativePath);
    }


    /**
     * @param cucumberJsonPattern
     * @param fileName
     * @return String
     */
    private String getCapabilityDescription(String cucumberJsonPattern, String fileName) {
        return StringUtils.removeEnd(fileName, cucumberJsonPattern);
    }

    // Helper Methods

    private String getString(JSONObject json, String key) {
        return (String) json.get(key);
    }

    private long getLong(JSONObject json, String key) {
        Object value = json.get(key);
        return value == null ? 0 : (long) value;
    }

    private JSONArray getJsonArray(JSONObject json, String key) {
        Object array = json.get(key);
        return array == null ? new JSONArray() : (JSONArray) array;
    }

    private String getJson(String baseUrl, String endpoint) {
        String url = StringUtils.removeEnd(baseUrl, "/") + endpoint;
        ResponseEntity<String> result = null;
        try {
            result = makeRestCall(url);
            return result.getBody();
        } catch (MalformedURLException mfe) {
            LOG.error("malformed url" + mfe.getStackTrace());
        }
        return "";
    }

    protected ResponseEntity<String> makeRestCall(String sUrl) throws MalformedURLException {
    	String url = sUrl;
    	String dockerLocalHostOverride = settings.getDockerLocalHostIP();
		LOG.debug("dockerLocalHostOverride =  " + dockerLocalHostOverride);

    	if( !dockerLocalHostOverride.isEmpty() ){
    		url = sUrl.replace("localhost", dockerLocalHostOverride);
    		LOG.debug("WARNING: mapping localhost to: " + dockerLocalHostOverride);
    		LOG.debug("URL: " + url );
        } else {
    		LOG.debug("URL: " + url );
        }

    	URI thisuri = URI.create(url);
        String userInfo = thisuri.getUserInfo();

        //get userinfo from URI or settings (in spring properties)
        if (StringUtils.isEmpty(userInfo) && (this.settings.getUsername() != null) && (this.settings.getApiKey() != null)) {
            userInfo = this.settings.getUsername() + ":" + this.settings.getApiKey();
        }
        // Basic Auth only.
        if (StringUtils.isNotEmpty(userInfo)) {
            return rest.exchange(thisuri, HttpMethod.GET,
                    new HttpEntity<>(createHeaders(userInfo)),
                    String.class);
        }
        return rest.exchange(thisuri, HttpMethod.GET, null,
                String.class);
    }

    protected HttpHeaders createHeaders(final String userInfo) {
        byte[] encodedAuth = org.apache.commons.codec.binary.Base64.encodeBase64(
                userInfo.getBytes(StandardCharsets.US_ASCII));
        String authHeader = "Basic " + new String(encodedAuth);

        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION, authHeader);
        return headers;
    }
}
