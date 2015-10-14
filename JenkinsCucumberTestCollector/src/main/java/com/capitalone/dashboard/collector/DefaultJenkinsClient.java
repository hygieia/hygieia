package com.capitalone.dashboard.collector;

import com.capitalone.dashboard.model.*;
import com.capitalone.dashboard.util.Supplier;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestOperations;

import java.net.URI;
import java.util.*;
import java.util.regex.Pattern;


@Component
public class DefaultJenkinsClient implements JenkinsClient {

    private static final Log LOG = LogFactory.getLog(DefaultJenkinsClient.class);

    private final RestOperations rest;
    private final Transformer<String, List<TestSuite>> cucumberTransformer;
    private final Pattern cucumberJsonFilePattern;

    private static final String JOBS_URL_SUFFIX = "/api/json?tree=jobs[name,url,builds[number,url]]";
    private static final String BUILD_URL_SUFFIX = "/api/json?tree=timestamp,duration,number,fullDisplayName,building,artifacts[fileName,relativePath]";

    @Autowired
    public DefaultJenkinsClient(Supplier<RestOperations> restOperationsSupplier,
                                Transformer<String, List<TestSuite>> cucumberTransformer,
                                JenkinsSettings settings) {
        this.rest = restOperationsSupplier.get();
        this.cucumberTransformer = cucumberTransformer;
        this.cucumberJsonFilePattern = Pattern.compile(settings.getCucumberJsonRegex());
    }

    @Override
    public Map<JenkinsJob, Set<Build>> getInstanceJobs(String instanceUrl) {
        Map<JenkinsJob, Set<Build>> result = new LinkedHashMap<>();
        try {
            JSONObject object = (JSONObject) new JSONParser().parse(getJson(instanceUrl, JOBS_URL_SUFFIX));

            for (Object job : getJsonArray(object, "jobs")) {
                JSONObject jsonJob = (JSONObject) job;

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
        } catch (ParseException e) {
            LOG.error("Parsing jobs on instance: " + instanceUrl, e);
        } catch (RestClientException rce) {
            LOG.error(rce);
        }

        return result;
    }

    @Override
    public boolean buildHasCucumberResults(String buildUrl) {

        JSONObject buildJson = null;
        try {
            // Get Build info
            buildJson = (JSONObject) new JSONParser().parse(getJson(buildUrl, BUILD_URL_SUFFIX));

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
        }

        return false;
    }

    @Override
    public TestResult getCucumberTestResult(String buildUrl) {
        try {
            JSONObject buildJson = (JSONObject) new JSONParser().parse(getJson(buildUrl, BUILD_URL_SUFFIX));

            List<TestSuite> suites = new ArrayList<>();
            Boolean building = (Boolean) buildJson.get("building");

            if (!building) {
                for (Object artifactObj : (JSONArray) buildJson.get("artifacts")) {
                    JSONObject artifact = (JSONObject) artifactObj;

                    if (cucumberJsonFilePattern.matcher(getString(artifact, "fileName")).matches()) {
                        String cucumberJson = getCucumberJson(buildUrl, getString(artifact, "relativePath"));
                        suites.addAll(cucumberTransformer.transformer(cucumberJson));
                    }
                }
            }

            if (!suites.isEmpty()) {
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
                testResult.getTestSuites().addAll(suites);

                // Calculate counts based on test suites
                for (TestSuite suite : suites) {
                    testResult.setFailureCount(testResult.getFailureCount() + suite.getFailureCount());
                    testResult.setErrorCount(testResult.getErrorCount() + suite.getErrorCount());
                    testResult.setSkippedCount(testResult.getSkippedCount() + suite.getSkippedCount());
                    testResult.setTotalCount(testResult.getTotalCount() + suite.getTotalCount());
                }

                return testResult;
            }
        } catch (ParseException e) {
            LOG.error("Parsing jobs on instance: " + buildUrl, e);
        } catch (RestClientException rce) {
            LOG.error(rce);
        }

        // An exception occurred or this build does not have cucumber test results
        return null;
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
        return rest.getForObject(URI.create(url), String.class);
    }

    private String getCucumberJson(String buildUrl, String artifactRelativePath) {
        return getJson(buildUrl, "/artifact/" + artifactRelativePath);
    }
}
