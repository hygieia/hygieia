package hygieia.transformer;

import com.capitalone.dashboard.model.TestCapability;
import com.capitalone.dashboard.model.TestCase;
import com.capitalone.dashboard.model.TestCaseStatus;
import com.capitalone.dashboard.model.TestSuite;
import com.capitalone.dashboard.model.TestSuiteType;
import com.capitalone.dashboard.model.quality.MochaJsSpecReport;
import com.capitalone.dashboard.request.BuildDataCreateRequest;

import java.util.ArrayList;
import java.util.List;

/**
 * Converts mocha report
 */
public class MochaSpecToTestCapabilityTransformer {

    private BuildDataCreateRequest buildDataCreateRequest;
    private String capabilityDescription;

    public MochaSpecToTestCapabilityTransformer(BuildDataCreateRequest buildDataCreateRequest, String capabilityDescription) {
        this.buildDataCreateRequest = buildDataCreateRequest;
        this.capabilityDescription = capabilityDescription;
    }

    public TestCapability convert(MochaJsSpecReport testReport) {
        List<TestSuite> testSuites = new ArrayList<>();

        for(MochaJsSpecReport.Suite suite: testReport.getSuites()) {
            TestSuite testSuite = buildTestSuite(suite);
            testSuites.add(testSuite);
        }

        return buildCapability(testReport, testSuites);
    }

    private TestSuite buildTestSuite(MochaJsSpecReport.Suite suite) {
        TestSuite testSuite = new TestSuite();
        testSuite.setDescription(suite.getTitle());
        long duration = 0;
        int testCaseSkippedCount = 0, testCaseSuccessCount = 0, testCaseFailCount = 0, testCaseUnknownCount = 0, testCaseTotalCount=0;
        for (MochaJsSpecReport.Test mochaTest: suite.getTests()) {
            TestCase testCase = buildTestCase(mochaTest);
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
            testCaseTotalCount++;
            testSuite.getTestCases().add(testCase);
        }
        testSuite.setSuccessTestCaseCount(testCaseSuccessCount);
        testSuite.setFailedTestCaseCount(testCaseFailCount);
        testSuite.setSkippedTestCaseCount(testCaseSkippedCount);
        testSuite.setTotalTestCaseCount(testCaseTotalCount);
        testSuite.setUnknownStatusCount(testCaseUnknownCount);
        testSuite.setDuration(duration);

        if (testCaseFailCount > 0) {
            testSuite.setStatus(TestCaseStatus.Failure);
        } else if (testCaseSkippedCount > 0) {
            testSuite.setStatus(TestCaseStatus.Skipped);
        } else if (testCaseSuccessCount > 0) {
            testSuite.setStatus(TestCaseStatus.Success);
        } else {
            testSuite.setStatus(TestCaseStatus.Unknown);
        }
        return testSuite;
    }

    private TestCase buildTestCase(MochaJsSpecReport.Test test) {
        TestCase testCase = new TestCase();

        testCase.setStatus(convertStatus(test.getResult()));
        testCase.setDuration(test.getDuration());
        testCase.setDescription(test.getTitle());
        return testCase;
    }

    private TestCaseStatus convertStatus(String result) {
        switch(result) {
            case "passed":
                return TestCaseStatus.Success;
            case "failed":
                return TestCaseStatus.Failure;
            case "pending":
                return TestCaseStatus.Skipped;
            default:
                return TestCaseStatus.Unknown;
        }
    }


    private TestCapability buildCapability(MochaJsSpecReport testReport,List<TestSuite> testSuites) {
        TestCapability cap = new TestCapability();
        cap.setType(TestSuiteType.Functional);

        cap.getTestSuites().addAll(testSuites);
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
