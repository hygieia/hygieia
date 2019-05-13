package hygieia.transformer;

import com.capitalone.dashboard.model.TestCapability;
import com.capitalone.dashboard.model.TestCase;
import com.capitalone.dashboard.model.TestCaseCondition;
import com.capitalone.dashboard.model.TestCaseStatus;
import com.capitalone.dashboard.model.TestCaseStep;
import com.capitalone.dashboard.model.TestSuite;
import com.capitalone.dashboard.model.TestSuiteType;
import com.capitalone.dashboard.model.quality.CucumberJsonReport;
import com.capitalone.dashboard.request.BuildDataCreateRequest;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Transforms a Cucumber result JSON string into a TestResult
 */

public class CucumberJsonToTestCapabilityTransformer {
    private static final Log logger = LogFactory.getLog(CucumberJsonToTestCapabilityTransformer.class);

    private BuildDataCreateRequest buildDataCreateRequest;
    private String capabilityDescription;

    public CucumberJsonToTestCapabilityTransformer(BuildDataCreateRequest buildDataCreateRequest, String capabilityDescription) {
        this.buildDataCreateRequest = buildDataCreateRequest;
        this.capabilityDescription = capabilityDescription;
    }

    private TestSuite parseFeatureAsTestSuite(CucumberJsonReport.Feature featureElement) {
        TestSuite suite = new TestSuite();
        suite.setId(featureElement.getId());
        suite.setType(TestSuiteType.Functional);
        suite.setDescription(featureElement.getKeyword() + ":" +featureElement.getName());

        long duration = 0;

        int testCaseTotalCount = featureElement.getElements().size();
        int testCaseSkippedCount = 0, testCaseSuccessCount = 0, testCaseFailCount = 0, testCaseUnknownCount = 0;

        for (CucumberJsonReport.Element scenarioElement : featureElement.getElements()) {
            TestCase testCase = parseScenarioAsTestCase(scenarioElement);
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

    private TestCase parseScenarioAsTestCase(CucumberJsonReport.Element scenarioElement) {
        TestCase testCase = new TestCase();
        testCase.setId(scenarioElement.getId());
        testCase.setDescription(scenarioElement.getKeyword() + ":" + scenarioElement.getName());
        // Parse each step as a TestCase
        int testStepSuccessCount = 0, testStepFailCount = 0, testStepSkippedCount = 0, testStepUnknownCount = 0;
        long testDuration=0;

        for (CucumberJsonReport.Step step :scenarioElement.getSteps()) {
            TestCaseStep testCaseStep = parseStepAsTestCaseStep( step);
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

        if (null!=scenarioElement.getTags()) {
            for (CucumberJsonReport.Tag tag : scenarioElement.getTags()) {
                testCase.getTags().add(tag.getName());
            }
        }

        if (null!= scenarioElement.getBefore()) {
            for (CucumberJsonReport.Condition before : scenarioElement.getBefore()) {
                TestCaseCondition condition = getTestCondition(before);
                if (condition != null) {
                    testCase.getBefore().add(condition);
                }
            }
        }

        if (null!=scenarioElement.getAfter()) {
            for (CucumberJsonReport.Condition after : scenarioElement.getAfter()) {
                TestCaseCondition condition = getTestCondition(after);
                if (condition != null) {
                    testCase.getAfter().add(condition);
                }
            }
        }
        return testCase;
    }

    private TestCaseCondition getTestCondition(CucumberJsonReport.Condition cond) {
        if (cond == null) return null;
        TestCaseCondition condition = new TestCaseCondition();
        CucumberJsonReport.Match match = cond.getMatch();
        if (match == null) return null;
        if (match.getLocation() instanceof ObjectNode) {
            ObjectNode location = (ObjectNode) match.getLocation();
            if (location == null) return null;
            JsonNode filepath = location.get("filepath");
            if (filepath == null) return null;
            condition.setCondition("Match: " + location.toString());
        } else {
            condition.setCondition("Match: " + match.toString());
        }
        CucumberJsonReport.Result result = cond.getResult();
        String stat = result.getStatus();
        long duration =result.getDuration();
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

    private TestCaseStep parseStepAsTestCaseStep(CucumberJsonReport.Step stepObject) {
        TestCaseStep step = new TestCaseStep();
        step.setDescription(stepObject.getKeyword() + ":" + stepObject.getName());
        step.setId(stepObject.getLine());
        TestCaseStatus stepStatus = TestCaseStatus.Unknown;

        CucumberJsonReport.Result resultObj = stepObject.getResult();
        if (resultObj != null) {
            step.setDuration(resultObj.getDuration() / 1000000l);
            stepStatus = getStatus(resultObj.getStatus());
        }
        step.setStatus(stepStatus);
        return step;
    }


    public TestCapability convert(CucumberJsonReport cucumberJsonReport) {

        List<TestSuite> testSuites = new ArrayList<>();
        List<CucumberJsonReport.Feature> features = cucumberJsonReport.getFeatures();
        for(CucumberJsonReport.Feature feature: features) {
            testSuites.add(this.parseFeatureAsTestSuite(feature));
        }

        return this.processTestSuites(testSuites);
    }

    private TestCapability processTestSuites(List<TestSuite> testSuites){
        TestCapability cap = new TestCapability();
        cap.setType(TestSuiteType.Functional);

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
        cap.setExecutionId(String.valueOf(buildDataCreateRequest.getNumber()));
        cap.setDescription(this.capabilityDescription);
        return cap;
    }
}
