package com.capitalone.dashboard.evaluator;

import com.capitalone.dashboard.ApiSettings;
import com.capitalone.dashboard.model.CollectorItem;
import com.capitalone.dashboard.model.Dashboard;
import com.capitalone.dashboard.model.DashboardType;
import com.capitalone.dashboard.model.TestResult;
import com.capitalone.dashboard.model.TestSuiteType;
import com.capitalone.dashboard.model.TestCapability;
import com.capitalone.dashboard.model.TestSuite;
import com.capitalone.dashboard.model.Widget;
import com.capitalone.dashboard.model.Feature;

import com.capitalone.dashboard.repository.FeatureRepository;
import com.capitalone.dashboard.repository.TestResultRepository;
import com.capitalone.dashboard.response.TestResultsAuditResponse;
import com.capitalone.dashboard.status.TestResultAuditStatus;
import org.bson.types.ObjectId;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class RegressionTestResultEvaluatorTest {

    @InjectMocks
    private RegressionTestResultEvaluator regressionTestResultEvaluator;

    @Mock
    private TestResultRepository testResultRepository;

    @Mock
    private FeatureRepository featureRepository;

    @Before
    public void setup(){
        regressionTestResultEvaluator.setSettings(getSettings());
    }

    @Test
    public void evaluate_testResultMissing(){
        CollectorItem collectorItem = new CollectorItem();
        collectorItem.setId(ObjectId.get());

        List<TestResult> emptyTestResults = new ArrayList<>();
        when(testResultRepository.findByCollectorItemIdAndTimestampIsBetweenOrderByTimestampDesc(collectorItem.getId(),
                123456789, 123456989)).thenReturn(emptyTestResults);
        TestResultsAuditResponse testResultsAuditResponse = regressionTestResultEvaluator.getRegressionTestResultAudit(getDashboard(), collectorItem);
        Assert.assertTrue(testResultsAuditResponse.getAuditStatuses().contains(TestResultAuditStatus.TEST_RESULT_MISSING));
        Assert.assertTrue(!testResultsAuditResponse.getAuditStatuses().contains(TestResultAuditStatus.TEST_RESULT_AUDIT_OK));
        Assert.assertTrue(!testResultsAuditResponse.getAuditStatuses().contains(TestResultAuditStatus.TEST_RESULT_AUDIT_FAIL));
        Assert.assertTrue(!testResultsAuditResponse.getAuditStatuses().contains(TestResultAuditStatus.TEST_RESULT_SKIPPED));
    }

    @Test
    public void evaluate_testResultAuditOK(){
        CollectorItem collectorItem = new CollectorItem();
        collectorItem.setId(ObjectId.get());
        List<TestResult> testResults = Arrays.asList(getAuditOKTestResult());
        when(testResultRepository.findByCollectorItemIdAndTimestampIsBetweenOrderByTimestampDesc(any(ObjectId.class),
                any(Long.class), any(Long.class))).thenReturn(testResults);
        when(featureRepository.getStoryByTeamID("TEST-1234")).thenReturn(Arrays.asList(new Feature()));
        TestResultsAuditResponse testResultsAuditResponse = regressionTestResultEvaluator.getRegressionTestResultAudit(getDashboard(), collectorItem);
        Assert.assertTrue(!testResultsAuditResponse.getAuditStatuses().contains(TestResultAuditStatus.TEST_RESULT_MISSING));
        Assert.assertTrue(testResultsAuditResponse.getAuditStatuses().contains(TestResultAuditStatus.TEST_RESULT_AUDIT_OK));
        Assert.assertTrue(!testResultsAuditResponse.getAuditStatuses().contains(TestResultAuditStatus.TEST_RESULT_AUDIT_FAIL));
        Assert.assertTrue(!testResultsAuditResponse.getAuditStatuses().contains(TestResultAuditStatus.TEST_RESULT_SKIPPED));
    }

    @Test
    public void evaluate_testResultAuditFAIL(){
        CollectorItem collectorItem = new CollectorItem();
        collectorItem.setId(ObjectId.get());
        List<TestResult> testResults = Arrays.asList(getAuditFAILTestResult());
        when(testResultRepository.findByCollectorItemIdAndTimestampIsBetweenOrderByTimestampDesc(any(ObjectId.class),
                any(Long.class), any(Long.class))).thenReturn(testResults);
        when(featureRepository.getStoryByTeamID("TEST-1234")).thenReturn(Arrays.asList(new Feature()));
        TestResultsAuditResponse testResultsAuditResponse = regressionTestResultEvaluator.getRegressionTestResultAudit(getDashboard(), collectorItem);
        Assert.assertTrue(!testResultsAuditResponse.getAuditStatuses().contains(TestResultAuditStatus.TEST_RESULT_MISSING));
        Assert.assertTrue(!testResultsAuditResponse.getAuditStatuses().contains(TestResultAuditStatus.TEST_RESULT_AUDIT_OK));
        Assert.assertTrue(testResultsAuditResponse.getAuditStatuses().contains(TestResultAuditStatus.TEST_RESULT_AUDIT_FAIL));
        Assert.assertTrue(!testResultsAuditResponse.getAuditStatuses().contains(TestResultAuditStatus.TEST_RESULT_SKIPPED));
    }

    @Test
    public void evaluate_testResultAuditSKIP(){
        CollectorItem collectorItem = new CollectorItem();
        collectorItem.setId(ObjectId.get());
        List<TestResult> testResults = Arrays.asList(getAuditSKIPTestResult());
        when(testResultRepository.findByCollectorItemIdAndTimestampIsBetweenOrderByTimestampDesc(any(ObjectId.class),
                any(Long.class), any(Long.class))).thenReturn(testResults);
        when(featureRepository.getStoryByTeamID("TEST-1234")).thenReturn(Arrays.asList(new Feature()));
        TestResultsAuditResponse testResultsAuditResponse = regressionTestResultEvaluator.getRegressionTestResultAudit(getDashboard(), collectorItem);
        Assert.assertTrue(!testResultsAuditResponse.getAuditStatuses().contains(TestResultAuditStatus.TEST_RESULT_MISSING));
        Assert.assertTrue(!testResultsAuditResponse.getAuditStatuses().contains(TestResultAuditStatus.TEST_RESULT_AUDIT_OK));
        Assert.assertTrue(!testResultsAuditResponse.getAuditStatuses().contains(TestResultAuditStatus.TEST_RESULT_AUDIT_FAIL));
        Assert.assertTrue(testResultsAuditResponse.getAuditStatuses().contains(TestResultAuditStatus.TEST_RESULT_SKIPPED));
    }

    @Test
    public void evaluate_featureTestResult() {
        TestResult testResult = getTestResult();
        HashMap featureTestMap = regressionTestResultEvaluator.getFeatureTestResult(testResult);
        Assert.assertEquals(testResult.getSuccessCount(), Integer.parseInt(featureTestMap.get("successCount").toString()));
        Assert.assertEquals(testResult.getFailureCount(), Integer.parseInt(featureTestMap.get("failureCount").toString()));
        Assert.assertEquals(testResult.getSkippedCount(), Integer.parseInt(featureTestMap.get("skippedCount").toString()));
        Assert.assertEquals(testResult.getTotalCount(), Integer.parseInt(featureTestMap.get("totalCount").toString()));
    }

    @Test
    public void evaluate_traceability_featureWidgetConfig() {
        Dashboard dashboard = getDashboard();
        Widget widget1 = new Widget();
        widget1.setName("TestWidget");
        dashboard.getWidgets().add(widget1);
        Widget emptyWidget = regressionTestResultEvaluator.getFeatureWidget(dashboard);
        Assert.assertNotEquals(emptyWidget.getName(), "feature");
        Widget widget2 = new Widget();
        widget2.setName("feature");
        dashboard.getWidgets().add(widget2);
        Widget featureWidget = regressionTestResultEvaluator.getFeatureWidget(dashboard);
        Assert.assertEquals(featureWidget.getName(), "feature");
    }

    private TestResult getTestResult() {
        TestResult testResult = new TestResult();
        testResult.setType(TestSuiteType.Functional);
        testResult.setSuccessCount(10);
        testResult.setFailureCount(5);
        testResult.setSkippedCount(1);
        testResult.setTotalCount(16);
        return testResult;
    }
    private TestResult getAuditOKTestResult() {
        TestResult testResult = new TestResult();
        testResult.setType(TestSuiteType.Regression);
        TestCapability testCapability = new TestCapability();

        TestSuite testSuite1 = new TestSuite();
        testSuite1.setSuccessTestCaseCount(18);
        testSuite1.setFailedTestCaseCount(1);
        testSuite1.setSkippedTestCaseCount(1);
        testSuite1.setTotalTestCaseCount(20);

        TestSuite testSuite2 = new TestSuite();
        testSuite2.setSuccessTestCaseCount(20);
        testSuite2.setFailedTestCaseCount(0);
        testSuite2.setSkippedTestCaseCount(0);
        testSuite2.setTotalTestCaseCount(20);

        testCapability.getTestSuites().add(testSuite1);
        testCapability.getTestSuites().add(testSuite2);
        testResult.getTestCapabilities().add(testCapability);
        return testResult;
    }

    private TestResult getAuditFAILTestResult() {
        TestResult testResult = new TestResult();
        testResult.setType(TestSuiteType.Regression);
        TestCapability testCapability = new TestCapability();


        TestSuite testSuite = new TestSuite();
        testSuite.setSuccessTestCaseCount(37);
        testSuite.setFailedTestCaseCount(2);
        testSuite.setSkippedTestCaseCount(1);
        testSuite.setTotalTestCaseCount(40);
        testCapability.getTestSuites().add(testSuite);
        testResult.getTestCapabilities().add(testCapability);
        return testResult;
    }

    public TestResult getAuditSKIPTestResult() {
        TestResult testResult = new TestResult();
        testResult.setType(TestSuiteType.Functional);
        TestCapability testCapability = new TestCapability();
        TestSuite testSuite = new TestSuite();
        testSuite.setSuccessTestCaseCount(0);
        testSuite.setFailedTestCaseCount(0);
        testSuite.setSkippedTestCaseCount(40);
        testSuite.setTotalTestCaseCount(40);
        testCapability.getTestSuites().add(testSuite);
        testResult.getTestCapabilities().add(testCapability);
        return testResult;
    }

    public Dashboard getDashboard() {
        Dashboard dashboard = new Dashboard("Template1", "Title1", null, null, DashboardType.Team,
                "ASV1", "BAP1", null, false, null);
        return dashboard;
    }

    private ApiSettings getSettings(){
        ApiSettings settings = new ApiSettings();
        settings.setTestResultSuccessPriority("Low");
        settings.setTestResultFailurePriority("High");
        settings.setTestResultSkippedPriority("High");
        settings.setTestResultThreshold(95.0);
        return settings;
    }
}