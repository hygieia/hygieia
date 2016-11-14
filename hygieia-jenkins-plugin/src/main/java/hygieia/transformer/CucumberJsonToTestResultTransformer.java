package hygieia.transformer;

import com.capitalone.dashboard.model.TestCase;
import com.capitalone.dashboard.model.TestCaseCondition;
import com.capitalone.dashboard.model.TestCaseStatus;
import com.capitalone.dashboard.model.TestCaseStep;
import com.capitalone.dashboard.model.TestSuite;
import com.capitalone.dashboard.model.TestSuiteType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Transforms a Cucumber result JSON string into a TestResult
 */

public class CucumberJsonToTestResultTransformer implements Transformer<JSONArray, List<TestSuite>> {
    private static final Log logger = LogFactory.getLog(CucumberJsonToTestResultTransformer.class);


    public List<TestSuite> transformer(JSONArray json) {

        if (json == null) {
            throw new IllegalArgumentException("json must not be empty");
        }

        List<TestSuite> suites = new ArrayList<TestSuite>();

        for (Object featureObj : json) {
            JSONObject feature = (JSONObject) featureObj;
            suites.add(parseFeatureAsTestSuite(feature));
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
            switch (testCase.getStatus()) {
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
        if (testCaseFailCount > 0) {
            suite.setStatus(TestCaseStatus.Failure);
        } else if (testCaseSkippedCount > 0) {
            suite.setStatus(TestCaseStatus.Skipped);
        } else if (testCaseSuccessCount > 0) {
            suite.setStatus(TestCaseStatus.Success);
        } else {
            suite.setStatus(TestCaseStatus.Unknown);
        }
        return suite;
    }

    private TestCase parseScenarioAsTestCase(JSONObject scenarioElement) {
        TestCase testCase = new TestCase();
        testCase.setId(getString(scenarioElement, "id"));
        testCase.setDescription(getString(scenarioElement, "keyword") + ":" + getString(scenarioElement, "name"));
        // Parse each step as a TestCase
        int testStepSuccessCount = 0, testStepFailCount = 0, testStepSkippedCount = 0, testStepUnknownCount = 0;
        long testDuration = 0;

        for (Object step : getJsonArray(scenarioElement, "steps")) {
            TestCaseStep testCaseStep = parseStepAsTestCaseStep((JSONObject) step);
            testDuration += testCaseStep.getDuration();
            // Count Statuses
            switch (testCaseStep.getStatus()) {
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
        if (testStepFailCount > 0) {
            testCase.setStatus(TestCaseStatus.Failure);
        } else if (testStepSkippedCount > 0) {
            testCase.setStatus(TestCaseStatus.Skipped);
        } else if (testStepSuccessCount > 0) {
            testCase.setStatus(TestCaseStatus.Success);
        } else {
            testCase.setStatus(TestCaseStatus.Unknown);
        }

        for (Object tag : getJsonArray(scenarioElement, "tags")) {
            testCase.getTags().add(getString((JSONObject) tag, "name"));
        }

        for (Object before : getJsonArray(scenarioElement, "before")) {
            TestCaseCondition condition = getTestCondition((JSONObject) before);
            if (condition != null) {
                testCase.getBefore().add(condition);
            }
        }
        for (Object after : getJsonArray(scenarioElement, "after")) {
            TestCaseCondition condition = getTestCondition((JSONObject) after);
            if (condition != null) {
                testCase.getAfter().add(condition);
            }
        }
        return testCase;
    }

    private TestCaseCondition getTestCondition(JSONObject cond) {
        if (cond == null) return null;
        TestCaseCondition condition = new TestCaseCondition();
        JSONObject match = (JSONObject) cond.get("match");
        if (match == null) return null;
        if (match.get("location") instanceof JSONObject) {
            JSONObject location = (JSONObject) match.get("location");
            if (location == null) return null;
            JSONObject filepath = (JSONObject) location.get("filepath");
            if (filepath == null) return null;
            condition.setCondition("Match: " + getString(filepath, "filename"));
        } else {
            condition.setCondition("Match: " + getString(match, "location"));
        }
        JSONObject result = (JSONObject) cond.get("result");
        String stat = getString(result, "status");
        long duration = (long) result.get("duration");
        condition.setResult(getStatus(stat), duration);
        return condition;
    }

    private TestCaseStatus getStatus(String stat) {
        switch (stat) {
            case "passed":
                return TestCaseStatus.Success;
            case "skipped":
                return TestCaseStatus.Skipped;
            case "failed":
                return TestCaseStatus.Failure;
            default:
                return TestCaseStatus.Unknown;
        }
    }

    private TestCaseStep parseStepAsTestCaseStep(JSONObject stepObject) {
        TestCaseStep step = new TestCaseStep();
        step.setDescription(getString(stepObject, "keyword") + ":" + getString(stepObject, "name"));
        step.setId(stepObject.get("line").toString());
        TestCaseStatus stepStatus = TestCaseStatus.Unknown;

        Object resultObj = stepObject.get("result");
        if (resultObj != null) {
            JSONObject result = (JSONObject) resultObj;
            step.setDuration(getLong(result, "duration") / 1000000l);
            stepStatus = parseStatus(result);
        }
        step.setStatus(stepStatus);
        return step;
    }


    private TestCaseStatus parseStatus(JSONObject result) {
        String status = getString(result, "status");
        if (status.equalsIgnoreCase("passed")) return TestCaseStatus.Success;
        if (status.equalsIgnoreCase("failed")) return TestCaseStatus.Failure;
        if (status.equalsIgnoreCase("skipped")) return TestCaseStatus.Skipped;
        return TestCaseStatus.Unknown;
    }


    private JSONArray getJsonArray(JSONObject json, String key) {
        Object array = json.get(key);
        return array == null ? new JSONArray() : (JSONArray) array;
    }

    private String getString(JSONObject json, String key) {
        if (json == null) return "";
        Object obj = json.get(key);
        return (obj == null) ? "" : (String) obj;
    }

    private long getLong(JSONObject json, String key) {
        Object obj = json.get(key);
        return (obj == null) ? 0 : (Long) obj;
    }
}
