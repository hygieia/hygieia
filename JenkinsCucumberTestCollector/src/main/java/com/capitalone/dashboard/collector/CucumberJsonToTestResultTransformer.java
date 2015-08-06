package com.capitalone.dashboard.collector;

import com.capitalone.dashboard.model.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Transforms a Cucumber result JSON string into a TestResult
 */
@Component
public class CucumberJsonToTestResultTransformer implements Transformer<String, List<TestSuite>> {
    private static final Log LOG = LogFactory.getLog(CucumberJsonToTestResultTransformer.class);

    @Override
    public List<TestSuite> transformer(String json) {
        if (StringUtils.isEmpty(json)) {
            throw new IllegalArgumentException("json must not be empty");
        }

        JSONParser parser = new JSONParser();

        List<TestSuite> suites = new ArrayList<>();
        try {
            // Parse features
            for (Object featureObj : (JSONArray) parser.parse(json)) {
                JSONObject feature = (JSONObject) featureObj;

                suites.add(parseFeatureAsTestSuite(feature));
            }
        } catch (ParseException e) {
            LOG.error(e);
        }

        return suites;
    }

    private TestSuite parseFeatureAsTestSuite(JSONObject featureElement) {
        TestSuite suite = new TestSuite();
        suite.setType(TestSuiteType.Functional);
        suite.setDescription(getString(featureElement, "name"));

        long duration = 0;

        int testCaseTotalCount = getJsonArray(featureElement, "elements").size();
        int testCaseSkippedCount = 0, testCaseErrorCount = 0, testCaseFailCount = 0;

        for (Object scenarioElement : getJsonArray(featureElement, "elements")) {
            TestCase testCase = parseScenarioAsTestCase((JSONObject) scenarioElement);

            duration += testCase.getDuration();

            suite.getTestCases().add(testCase);
            switch(testCase.getStatus()) {
                case Error:
                    testCaseErrorCount++;
                    break;
                case Failure:
                    testCaseFailCount++;
                    break;
                case Skipped:
                    testCaseSkippedCount++;
                    break;
            }
        }

        suite.setErrorCount(testCaseErrorCount);
        suite.setFailureCount(testCaseFailCount);
        suite.setSkippedCount(testCaseSkippedCount);
        suite.setTotalCount(testCaseTotalCount);
        suite.setDuration(duration);

        return suite;
    }

    private TestCase parseScenarioAsTestCase(JSONObject scenarioElement) {
        TestCase testCase  = new TestCase();
        testCase.setId(getString(scenarioElement, "name"));
        testCase.setDescription(getString(scenarioElement, "name")); //TODO determine if change is necessary

        // Parse each step as a TestCase
        int testStepErrorCount = 0, testStepFailCount = 0, testStepSkippedCount = 0;
        long testDuration = 0;

        for (Object step : getJsonArray(scenarioElement, "steps")) {
            TestCaseStatus stepStatus;

            Object resultObj = ((JSONObject) step).get("result");
            if (resultObj == null) {
                stepStatus = TestCaseStatus.Unknown;
            } else {
                JSONObject result = (JSONObject) resultObj;
                // Add the duration of this step to the overall duration of the test case
                testDuration += getLong(result, "duration") / 1000l;
                stepStatus = parseStatus(result);
            }

            // Count Statuses
            switch(stepStatus) {
                case Error:
                    testStepErrorCount++;
                    break;
                case Failure:
                    testStepFailCount++;
                    break;
                case Skipped:
                    testStepSkippedCount++;
                    break;
            }
        }

        // Set Duration
        testCase.setDuration(testDuration);

        // Set Status
        if(testStepErrorCount > 0) {
            testCase.setStatus(TestCaseStatus.Error);
        } else if(testStepFailCount > 0) {
            testCase.setStatus(TestCaseStatus.Failure);
        } else if(testStepSkippedCount > 0) {
            testCase.setStatus(TestCaseStatus.Skipped);
        } else {
            testCase.setStatus(TestCaseStatus.Success);
        }

        return testCase;
    }


    private TestCaseStatus parseStatus(JSONObject result) {
        String status = getString(result, "status");
        return "passed".equalsIgnoreCase(status) ? TestCaseStatus.Success
                : "failed".equalsIgnoreCase(status) ? TestCaseStatus.Failure
                : TestCaseStatus.Unknown;
    }

    private JSONArray getJsonArray(JSONObject json, String key) {
        Object array = json.get(key);
        return array == null ? new JSONArray() : (JSONArray) array;
    }

    private String getString(JSONObject json, String key) {
        return (String) json.get(key);
    }

    private long getLong(JSONObject json, String key) {
        Object obj = json.get(key);
        return obj == null ? 0 : (long) obj;
    }
}
