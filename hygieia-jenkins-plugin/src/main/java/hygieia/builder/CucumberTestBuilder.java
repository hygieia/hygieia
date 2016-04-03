package hygieia.builder;

import com.capitalone.dashboard.model.TestCapability;
import com.capitalone.dashboard.model.TestCaseStatus;
import com.capitalone.dashboard.model.TestResult;
import com.capitalone.dashboard.model.TestSuite;
import com.capitalone.dashboard.model.TestSuiteType;
import com.capitalone.dashboard.request.TestDataCreateRequest;
import hudson.EnvVars;
import hudson.model.AbstractBuild;
import hudson.model.BuildListener;
import hygieia.transformer.CucumberJsonToTestResultTransformer;
import hygieia.utils.HygieiaUtils;
import jenkins.plugins.hygieia.HygieiaPublisher;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;


public class CucumberTestBuilder {
    private static final Logger logger = Logger.getLogger(CucumberTestBuilder.class.getName());
    AbstractBuild build;
    HygieiaPublisher publisher;
    BuildListener listener;
    String buildId;
    TestResult testResult;
    TestDataCreateRequest testDataCreateRequest;

    public CucumberTestBuilder(AbstractBuild build, HygieiaPublisher publisher, BuildListener listener, String buildId) {
        this.build = build;
        this.publisher = publisher;
        this.buildId = buildId;
        this.listener = listener;
        buildTestResults();
    }

    private void buildTestResults() {
        String directory = publisher.getHygieiaTest().getTestResultsDirectory().trim();
        String filePattern = publisher.getHygieiaTest().getTestFileNamePattern().trim();

        EnvVars env;
        try {
            env = build.getEnvironment(listener);
        } catch (Exception e) {
            listener.getLogger().println("Error retrieving environment vars: " + e.getMessage());
            env = new EnvVars();
        }

        String path = env.expand("$WORKSPACE");
        path = path + directory;
        listener.getLogger().println("Hygieia Test Result Publisher - Looking for file pattern '" + filePattern + "' in directory " + path);
        List<File> testFiles = HygieiaUtils.getArtifactFiles(new File(path), filePattern, new ArrayList<File>());
        testResult = buildTestResultObject(getCapabilities(testFiles));
    }

    private List<TestCapability> getCapabilities(List<File> testFiles) {
        List<TestCapability> capabilities = new ArrayList<TestCapability>();
        JSONParser parser = new JSONParser();
        CucumberJsonToTestResultTransformer cucumberTransformer = new CucumberJsonToTestResultTransformer();
        for (File file : testFiles) {
            try {
                listener.getLogger().println("Hygieia Publisher: Processing test file: " + file.getAbsolutePath());
                JSONArray cucumberJson = (JSONArray) parser.parse(new FileReader(file));
                TestCapability cap = new TestCapability();
                cap.setType(TestSuiteType.Functional);
                List<TestSuite> testSuites = cucumberTransformer.transformer(cucumberJson);
                cap.setDescription(getCapabilityDescription(file));

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
                cap.setExecutionId(String.valueOf(build.getNumber()));
                capabilities.add(cap);
            } catch (FileNotFoundException e) {
                listener.getLogger().println("Hygieia Publisher: Test File Not Found: " + file.getAbsolutePath());
            } catch (ParseException e) {
                listener.getLogger().println("Hygieia Publisher: Error Parsing File: " + file.getAbsolutePath());
            } catch (IOException e) {
                listener.getLogger().println("Hygieia Publisher: Error Reading File: " + file.getAbsolutePath());
            }
        }
        return capabilities;
    }

    private String getCapabilityDescription(File file) {
        String newFileName = file.getPath().replace("/" + file.getName(), "");
        int lastFolderIndex = newFileName.lastIndexOf("/");
        if (lastFolderIndex > 0) {
            return newFileName.substring(lastFolderIndex);
        }
        return newFileName;
    }

    private TestResult buildTestResultObject(List<TestCapability> capabilities) {
        if (!capabilities.isEmpty()) {
            // There are test suites so let's construct a TestResult to encapsulate these results
            TestResult testResult = new TestResult();
            testResult.setType(TestSuiteType.fromString(publisher.getHygieiaTest().getTestType()));
            testResult.setDescription(build.getProject().getName());
            testResult.setExecutionId(String.valueOf(build.getNumber()));
            testResult.setUrl(build.getProject().getAbsoluteUrl() + String.valueOf(build.getNumber()) + "/");
            testResult.setDuration(build.getDuration());
            testResult.setEndTime(build.getStartTimeInMillis() + build.getDuration());
            testResult.setStartTime(build.getStartTimeInMillis());
            testResult.getTestCapabilities().addAll(capabilities);  //add all capabilities
            testResult.setTotalCount(capabilities.size());
            testResult.setTimestamp(System.currentTimeMillis());
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

    // Helper Methods

    private String getString(JSONObject json, String key) {
        return (String) json.get(key);
    }

    private long getLong(JSONObject json, String key) {
        Object value = json.get(key);
        return value == null ? 0 : (Long) value;
    }

    private JSONArray getJsonArray(JSONObject json, String key) {
        Object array = json.get(key);
        return array == null ? new JSONArray() : (JSONArray) array;
    }

    public TestDataCreateRequest getTestDataCreateRequest() {

        if (testResult != null) {
            TestDataCreateRequest request = new TestDataCreateRequest();
            EnvVars env = null;
            try {
                env = build.getEnvironment(listener);
            } catch (IOException e) {
                logger.warning("Error getting environment variables");
            } catch (InterruptedException e) {
                logger.warning("Error getting environment variables");
            }
            if (env != null) {
                request.setServerUrl(env.get("JENKINS_URL"));
            } else {
                String jobPath = "/job" + "/" + build.getProject().getName() + "/";
                int ind = build.getProject().getAbsoluteUrl().indexOf(jobPath);
                request.setServerUrl(build.getProject().getAbsoluteUrl().substring(0, ind));
            }
            request.setTestJobId(buildId);
            request.setType(testResult.getType());
            request.setTestJobName(build.getProject().getName());
            request.setTestJobUrl(build.getProject().getAbsoluteUrl());
            request.setTimestamp(testResult.getTimestamp());
            request.setNiceName(publisher.getDescriptor().getHygieiaJenkinsName());

            request.setDescription(testResult.getDescription());
            request.setDuration(testResult.getDuration());
            request.setEndTime(testResult.getEndTime());
            request.setExecutionId(testResult.getExecutionId());
            request.setFailureCount(testResult.getFailureCount());
            request.setSkippedCount(testResult.getSkippedCount());
            request.setStartTime(testResult.getStartTime());
            request.setSuccessCount(testResult.getSuccessCount());

            request.setTotalCount(testResult.getTotalCount());
            request.setUnknownStatusCount(testResult.getUnknownStatusCount());
            request.getTestCapabilities().addAll(testResult.getTestCapabilities());

            request.setTargetAppName(publisher.getHygieiaTest().getTestApplicationName());
            request.setTargetEnvName(publisher.getHygieiaTest().getTestEnvironmentName());
            return request;
        }
        return null;
    }
}