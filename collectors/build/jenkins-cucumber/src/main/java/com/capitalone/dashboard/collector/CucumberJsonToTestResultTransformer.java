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
        suite.setId(getString(featureElement, "id"));
        suite.setType(TestSuiteType.Functional);
        suite.setDescription(getString(featureElement, "keyword") + ":" + getString(featureElement, "name"));

        long duration = 0;

        int testCaseTotalCount = getJsonArray(featureElement, "elements").size();
        int testCaseSkippedCount = 0, testCaseSuccessCount = 0, testCaseFailCount = 0, testCaseUnknownCount = 0;

        for (Object scenarioElement : getJsonArray(featureElement, "elements")) {
            TestCase testCase = parseScenarioAsTestCase((JSONObject) scenarioElement);
            duration += testCase.getDuration();
            switch(testCase.getStatus()) {
                case Success:
                    testCaseSuccessCount++;
                    break;
                case Failure:
                    testCaseFailCount++;
                    break;
                case Skipped:
                    testCaseSkippedCount++;
                    break;
                default:
                    testCaseUnknownCount++;
                    break;
            }
            suite.getTestCases().add(testCase);
        }
        suite.setSuccessTestCaseCount(testCaseSuccessCount);
        suite.setFailedTestCaseCount(testCaseFailCount);
        suite.setSkippedTestCaseCount(testCaseSkippedCount);
        suite.setTotalTestCaseCount(testCaseTotalCount);
        suite.setUnknownStatusCount(testCaseUnknownCount);
        suite.setDuration(duration);
        if(testCaseFailCount > 0) {
            suite.setStatus(TestCaseStatus.Failure);
        } else if(testCaseSkippedCount > 0) {
            suite.setStatus(TestCaseStatus.Skipped);
        } else if (testCaseSuccessCount > 0){
            suite.setStatus(TestCaseStatus.Success);
        } else {
            suite.setStatus(TestCaseStatus.Unknown);
        }
        return suite;
    }

    private TestCase parseScenarioAsTestCase(JSONObject scenarioElement) {
        TestCase testCase  = new TestCase();
        testCase.setId(getString(scenarioElement, "id"));
        testCase.setDescription(getString(scenarioElement, "keyword") + ":" + getString(scenarioElement, "name"));
        // Parse each step as a TestCase
        int testStepSuccessCount = 0, testStepFailCount = 0, testStepSkippedCount = 0, testStepUnknownCount = 0;
        long testDuration = 0;

        for (Object step : getJsonArray(scenarioElement, "steps")) {
            TestCaseStep testCaseStep = parseStepAsTestCaseStep((JSONObject) step);
            testDuration += testCaseStep.getDuration();
            // Count Statuses
            switch(testCaseStep.getStatus()) {
                case Success:
                    testStepSuccessCount++;
                    break;
                case Failure:
                    testStepFailCount++;
                    break;
                case Skipped:
                    testStepSkippedCount++;
                    break;
                default:
                    testStepUnknownCount++;
                    break;

            }
            testCase.getTestSteps().add(testCaseStep);
        }
        // Set Duration
        testCase.setDuration(testDuration);
        testCase.setSuccessTestStepCount(testStepSuccessCount);
        testCase.setSkippedTestStepCount(testStepSkippedCount);
        testCase.setFailedTestStepCount(testStepFailCount);
        testCase.setUnknownStatusCount(testStepUnknownCount);
        testCase.setTotalTestStepCount(testCase.getTestSteps().size());
        // Set Status
        if(testStepFailCount > 0) {
            testCase.setStatus(TestCaseStatus.Failure);
        } else if(testStepSkippedCount > 0) {
            testCase.setStatus(TestCaseStatus.Skipped);
        } else if (testStepSuccessCount > 0){
            testCase.setStatus(TestCaseStatus.Success);
        } else {
            testCase.setStatus(TestCaseStatus.Unknown);
        }
        return testCase;
    }

    private TestCaseStep parseStepAsTestCaseStep(JSONObject stepObject) {
        TestCaseStep step  = new TestCaseStep();
        step.setDescription(getString(stepObject, "keyword") + ":" + getString(stepObject, "name"));
        step.setId(stepObject.get("line").toString());
        TestCaseStatus stepStatus = TestCaseStatus.Unknown;

        Object resultObj = stepObject.get("result");
        if (resultObj != null) {
            JSONObject result = (JSONObject) resultObj;
            step.setDuration(getLong(result, "duration") / 1000l);
            stepStatus = parseStatus(result);
        }
        step.setStatus(stepStatus);
        return step;
    }


    private TestCaseStatus parseStatus(JSONObject result) {
        String status = getString(result, "status");
        switch (status) {
            case "passed" :
                return TestCaseStatus.Success;
            case "failed" :
                return TestCaseStatus.Failure;
            case "skipped" :
                return TestCaseStatus.Skipped;
            default:
                return TestCaseStatus.Unknown;
        }
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
