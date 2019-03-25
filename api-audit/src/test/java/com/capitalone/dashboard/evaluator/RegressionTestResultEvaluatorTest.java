package com.capitalone.dashboard.evaluator;

import com.capitalone.dashboard.model.TestResult;
import com.capitalone.dashboard.model.CollectorItem;
import com.capitalone.dashboard.model.TestSuiteType;
import com.capitalone.dashboard.model.Dashboard;
import com.capitalone.dashboard.model.DashboardType;
import com.capitalone.dashboard.model.Feature;

import com.capitalone.dashboard.repository.FeatureRepository;
import com.capitalone.dashboard.repository.TestResultRepository;
import com.capitalone.dashboard.response.TestResultsAuditResponse;
import com.capitalone.dashboard.status.TestResultAuditStatus;
import org.bson.types.ObjectId;
import org.junit.Assert;
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


    @Test
    public void evaluate_testResultMissing(){
        CollectorItem collectorItem = new CollectorItem();
        collectorItem.setId(ObjectId.get());

        List<TestResult> emptyTestResults = new ArrayList<>();
        when(testResultRepository.findByCollectorItemIdAndTimestampIsBetweenOrderByTimestampDesc(collectorItem.getId(),
                123456789, 123456989)).thenReturn(emptyTestResults);
        TestResultsAuditResponse testResultsAuditResponse = regressionTestResultEvaluator.getRegressionTestResultAudit(collectorItem);
        Assert.assertTrue(testResultsAuditResponse.getAuditStatuses().contains(TestResultAuditStatus.TEST_RESULT_MISSING));
    }

    @Test
    public void evaluate_testResultAuditOK(){
        CollectorItem collectorItem = new CollectorItem();
        collectorItem.setId(ObjectId.get());
        List<TestResult> testResults = Arrays.asList(getAuditOKTestResult());
        when(testResultRepository.findByCollectorItemIdAndTimestampIsBetweenOrderByTimestampDesc(any(ObjectId.class),
                any(Long.class), any(Long.class))).thenReturn(testResults);
        when(featureRepository.getStoryByTeamID("TEST-1234")).thenReturn(Arrays.asList(new Feature()));
        TestResultsAuditResponse testResultsAuditResponse = regressionTestResultEvaluator.getRegressionTestResultAudit(collectorItem);
        Assert.assertTrue(testResultsAuditResponse.getAuditStatuses().contains(TestResultAuditStatus.TEST_RESULT_AUDIT_OK));
    }

    @Test
    public void evaluate_testResultAuditFAIL(){
        CollectorItem collectorItem = new CollectorItem();
        collectorItem.setId(ObjectId.get());
        List<TestResult> testResults = Arrays.asList(getAuditFAILTestResult());
        when(testResultRepository.findByCollectorItemIdAndTimestampIsBetweenOrderByTimestampDesc(any(ObjectId.class),
                any(Long.class), any(Long.class))).thenReturn(testResults);
        when(featureRepository.getStoryByTeamID("TEST-1234")).thenReturn(Arrays.asList(new Feature()));
        TestResultsAuditResponse testResultsAuditResponse = regressionTestResultEvaluator.getRegressionTestResultAudit(collectorItem);
        Assert.assertTrue(testResultsAuditResponse.getAuditStatuses().contains(TestResultAuditStatus.TEST_RESULT_AUDIT_FAIL));
    }

    @Test
    public void evaluate_testResultAuditSKIP(){
        CollectorItem collectorItem = new CollectorItem();
        collectorItem.setId(ObjectId.get());
        List<TestResult> testResults = Arrays.asList(getAuditSKIPTestResult());
        when(testResultRepository.findByCollectorItemIdAndTimestampIsBetweenOrderByTimestampDesc(any(ObjectId.class),
                any(Long.class), any(Long.class))).thenReturn(testResults);
        when(featureRepository.getStoryByTeamID("TEST-1234")).thenReturn(Arrays.asList(new Feature()));
        TestResultsAuditResponse testResultsAuditResponse = regressionTestResultEvaluator.getRegressionTestResultAudit(collectorItem);
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
        testResult.setSuccessCount(10);
        testResult.setFailureCount(0);
        testResult.setSkippedCount(0);
        testResult.setTotalCount(10);
        return testResult;
    }

    private TestResult getAuditFAILTestResult() {
        TestResult testResult = new TestResult();
        testResult.setType(TestSuiteType.Regression);
        testResult.setSuccessCount(5);
        testResult.setFailureCount(3);
        testResult.setSkippedCount(2);
        testResult.setTotalCount(10);
        return testResult;
    }

    public TestResult getAuditSKIPTestResult() {
        TestResult testResult = new TestResult();
        testResult.setType(TestSuiteType.Functional);
        testResult.setSuccessCount(0);
        testResult.setFailureCount(0);
        testResult.setSkippedCount(10);
        testResult.setTotalCount(10);
        return testResult;
    }

    public Dashboard getDashboard() {
        Dashboard dashboard = new Dashboard("Template1", "Title1", null, null, DashboardType.Team,
                "ASV1", "BAP1", null, false, null);
        return dashboard;
    }
}