package hygieia.transformer;

import com.capitalone.dashboard.model.TestCapability;
import com.capitalone.dashboard.model.TestResult;
import com.capitalone.dashboard.model.TestSuiteType;
import com.capitalone.dashboard.model.quality.CheckstyleReport;
import com.capitalone.dashboard.model.quality.CucumberJsonReport;
import com.capitalone.dashboard.model.quality.FindBugsXmlReport;
import com.capitalone.dashboard.model.quality.JacocoXmlReport;
import com.capitalone.dashboard.model.quality.JunitXmlReport;
import com.capitalone.dashboard.model.quality.MochaJsSpecReport;
import com.capitalone.dashboard.model.quality.PmdReport;
import com.capitalone.dashboard.model.quality.QualityVisitor;
import com.capitalone.dashboard.request.BuildDataCreateRequest;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by stevegal on 2019-03-25.
 */
public class TestResultVisitor implements QualityVisitor<TestResult> {

    private List<TestCapability> capabilities = new ArrayList<>();

    private String testType;
    private BuildDataCreateRequest buildDataCreateRequest;
    private String capabilityDescription;

    public TestResultVisitor(String testType, BuildDataCreateRequest buildDataCreateRequest) {
        this.testType = testType;
        this.buildDataCreateRequest = buildDataCreateRequest;
    }

    @Override
    public TestResult produceResult() {
        return this.buildTestResultObject(this.capabilities, this.buildDataCreateRequest, this.testType);
    }

    @Override
    public void visit(JunitXmlReport junitXmlReport) {
        // no impl... could expand
    }

    @Override
    public void visit(FindBugsXmlReport findBugsXmlReport) {
        // no impl... could expand
    }

    @Override
    public void visit(JacocoXmlReport jacocoXmlReport) {
        // no impl... could expand
    }

    @Override
    public void visit(PmdReport pmdReport) {
        // no impl... could expand
    }

    @Override
    public void visit(CheckstyleReport checkstyleReport) {
        // no impl... could expandÒ
    }

    @Override
    public void visit(MochaJsSpecReport mochaJsSpecReport) {
        MochaSpecToTestCapabilityTransformer transformer = new MochaSpecToTestCapabilityTransformer(this.buildDataCreateRequest, this.capabilityDescription);
        TestCapability capability = transformer.convert(mochaJsSpecReport);
        this.capabilities.add(capability);
    }

    @Override
    public void visit(CucumberJsonReport cucumberJsonReport) {
        CucumberJsonToTestCapabilityTransformer transformer = new CucumberJsonToTestCapabilityTransformer(this.buildDataCreateRequest, this.capabilityDescription);
        TestCapability capability = transformer.convert(cucumberJsonReport);
        this.capabilities.add(capability);
    }

    private TestResult buildTestResultObject(List<TestCapability> capabilities, BuildDataCreateRequest buildDataCreateRequest, String testType) {
        if (!capabilities.isEmpty()) {
            // There are test suites so let's construct a TestResult to encapsulate these results
            TestResult testResult = new TestResult();
            testResult.setType(TestSuiteType.fromString(testType));
            testResult.setDescription(buildDataCreateRequest.getJobName());
            testResult.setExecutionId(String.valueOf(buildDataCreateRequest.getNumber()));
            testResult.setUrl(buildDataCreateRequest.getBuildUrl() + String.valueOf(buildDataCreateRequest.getNumber()) + "/");
            testResult.setDuration(buildDataCreateRequest.getDuration());
            testResult.setEndTime(buildDataCreateRequest.getStartTime() + buildDataCreateRequest.getDuration());
            testResult.setStartTime(buildDataCreateRequest.getStartTime());
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

    public void setCurrentDescriprion(String capabilityDescription) {
        this.capabilityDescription = capabilityDescription;
    }
}
