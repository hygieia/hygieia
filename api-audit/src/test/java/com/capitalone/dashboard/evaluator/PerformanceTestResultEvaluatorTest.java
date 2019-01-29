package com.capitalone.dashboard.evaluator;

import com.capitalone.dashboard.model.CollectorItem;
import com.capitalone.dashboard.model.TestCapability;
import com.capitalone.dashboard.model.TestCase;
import com.capitalone.dashboard.model.TestCaseStatus;
import com.capitalone.dashboard.model.TestCaseStep;
import com.capitalone.dashboard.model.TestResult;
import com.capitalone.dashboard.model.TestSuite;
import com.capitalone.dashboard.model.TestSuiteType;
import com.capitalone.dashboard.repository.TestResultRepository;
import com.capitalone.dashboard.response.PerformanceTestAuditResponse;

import org.bson.types.ObjectId;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PerformanceTestResultEvaluatorTest {

    @InjectMocks
    private PerformanceTestResultEvaluator performanceTestResultEvaluator;
    @Mock
    private TestResultRepository testResultRepository;

    @Test
    public void evaluate_COLLECTOR_ITEM_ERROR() {
        CollectorItem c = null;
        PerformanceTestAuditResponse response = performanceTestResultEvaluator.evaluate(c,125634536, 6235263, null);
        Assert.assertEquals(true, response.getAuditStatuses().toString().contains("COLLECTOR_ITEM_ERROR"));
    }

    @Test
    public void evaluate_AuditStatus_Avg_response_times() {

        List<TestResult> tr = makeTestResult("KPI : Avg response times","Success");

        when(testResultRepository.findByCollectorItemIdAndTimestampIsBetweenOrderByTimestampDesc(any(ObjectId.class),any(Long.class),any(Long.class))).thenReturn(tr);
        PerformanceTestAuditResponse responseV2 = performanceTestResultEvaluator.evaluate(makeCollectorItem(0),125634536, 6235263, null);
        Assert.assertEquals(true, responseV2.getAuditStatuses().toString().contains("PERFORMANCE_COMMIT_IS_CURRENT"));
        Assert.assertEquals(true, responseV2.getAuditStatuses().toString().contains("PERFORMANCE_THRESHOLDS_RESPONSE_TIME_FOUND"));
        Assert.assertEquals(true, responseV2.getAuditStatuses().toString().contains("PERFORMANCE_MET"));
        Assert.assertEquals(true, responseV2.getAuditStatuses().toString().contains("PERFORMANCE_THRESHOLD_RESPONSE_TIME_MET"));
        Assert.assertEquals(true, responseV2.getAuditStatuses().toString().contains("PERF_RESULT_AUDIT_OK"));
    }

    @Test
    public void evaluate_AuditStatus_PERFORMANCE_THRESHOLDS_TRANSACTIONS_PER_SECOND_FOUND() {


        List<TestResult> tr = makeTestResult("KPI : Transaction Per Second","Success");

        when(testResultRepository.findByCollectorItemIdAndTimestampIsBetweenOrderByTimestampDesc(any(ObjectId.class),any(Long.class),any(Long.class))).thenReturn(tr);
        PerformanceTestAuditResponse responseV2 = performanceTestResultEvaluator.evaluate(makeCollectorItem(0),125634536, 6235263, null);
        Assert.assertEquals(true, responseV2.getAuditStatuses().toString().contains("PERFORMANCE_COMMIT_IS_CURRENT"));
        Assert.assertEquals(true, responseV2.getAuditStatuses().toString().contains("PERFORMANCE_THRESHOLDS_TRANSACTIONS_PER_SECOND_FOUND"));
        Assert.assertEquals(true, responseV2.getAuditStatuses().toString().contains("PERFORMANCE_MET"));
        Assert.assertEquals(true, responseV2.getAuditStatuses().toString().contains("PERF_RESULT_AUDIT_OK"));
    }

    @Test
    public void evaluate_AuditStatus_PERFORMANCE_THRESHOLDS_ERROR_RATE_FOUND() {

        List<TestResult> tr = makeTestResult("KPI : Error Rate Threshold","Success");

        when(testResultRepository.findByCollectorItemIdAndTimestampIsBetweenOrderByTimestampDesc(any(ObjectId.class),any(Long.class),any(Long.class))).thenReturn(tr);
        PerformanceTestAuditResponse responseV2 = performanceTestResultEvaluator.evaluate(makeCollectorItem(0),125634536, 6235263, null);
        Assert.assertEquals(true, responseV2.getAuditStatuses().toString().contains("PERFORMANCE_COMMIT_IS_CURRENT"));
        Assert.assertEquals(true, responseV2.getAuditStatuses().toString().contains("PERFORMANCE_THRESHOLDS_ERROR_RATE_FOUND"));
        Assert.assertEquals(true, responseV2.getAuditStatuses().toString().contains("PERFORMANCE_MET"));
        Assert.assertEquals(true, responseV2.getAuditStatuses().toString().contains("PERFORMANCE_THRESHOLD_ERROR_RATE_MET"));
        Assert.assertEquals(true, responseV2.getAuditStatuses().toString().contains("PERF_RESULT_AUDIT_OK"));
    }

    @Test
    public void evaluate_AuditStatus_PERFORMANCE_RESULT_STATUS_NULL() {
        List<TestResult> tr = makeTestResult("KPI : Error Rate Threshold",null);
        when(testResultRepository.findByCollectorItemIdAndTimestampIsBetweenOrderByTimestampDesc(any(ObjectId.class),any(Long.class),any(Long.class))).thenReturn(tr);
        PerformanceTestAuditResponse responseV2 = performanceTestResultEvaluator.evaluate(makeCollectorItem(0),125634536, 6235263, null);
        Assert.assertEquals(true, responseV2.getAuditStatuses().toString().contains("PERFORMANCE_COMMIT_IS_CURRENT"));
        Assert.assertEquals(true, responseV2.getAuditStatuses().toString().contains("PERFORMANCE_THRESHOLDS_ERROR_RATE_FOUND"));
        Assert.assertEquals(true, responseV2.getAuditStatuses().toString().contains("PERFORMANCE_MET"));
        Assert.assertEquals(true, responseV2.getAuditStatuses().toString().contains("PERFORMANCE_THRESHOLD_ERROR_RATE_MET"));
        Assert.assertEquals(true, responseV2.getAuditStatuses().toString().contains("PERF_RESULT_AUDIT_FAIL"));
    }




    private CollectorItem makeCollectorItem(int lastUpdated) {
        CollectorItem item = new CollectorItem();
        item.setCollectorId(ObjectId.get());
        item.setEnabled(true);
        item.getOptions().put("jobName", "testHygieiaPerf");
        item.getOptions().put("instanceUrl", "http://github.com/capone/hygieia");
        item.setLastUpdated(lastUpdated);
        return item;

    }


   private List<TestResult> makeTestResult(String testCaseDescription,String resultStatus) {
       TestResult tr = new TestResult();
       tr.setType(TestSuiteType.fromString("Performance"));
       tr.setDescription("Success");
       tr.setResultStatus(resultStatus);
       tr.getTestCapabilities().addAll(Stream.of(makeTestCapability(testCaseDescription)).collect(Collectors.toList()));
       List<TestResult> trs = new ArrayList<>();
       trs.add(tr);
       return trs;
    }

   private TestCapability makeTestCapability(String testCaseDescription){
        TestCapability tc = new TestCapability();
        tc.setTestSuites(Stream.of(makeTestSuite(testCaseDescription)).collect(Collectors.toList()));
        return tc;
   }

   private TestSuite makeTestSuite(String testCaseDescription){
        TestSuite ts = new TestSuite();
        ts.setTestCases(Stream.of(makeTestCase(testCaseDescription)).collect(Collectors.toList()));
        return ts;
   }

   private TestCase makeTestCase(String testCaseDescription){
        TestCase tc = new TestCase();
        tc.setStatus(TestCaseStatus.Failure);
        tc.setDescription(testCaseDescription);
        tc.setTestSteps(Stream.of(makeTestCaseStep("10")).collect(Collectors.toList()));
        return tc;

   }

   private TestCaseStep makeTestCaseStep(String description){
        TestCaseStep tcs = new TestCaseStep();
        tcs.setDescription(description);
        return tcs;
   }

}
