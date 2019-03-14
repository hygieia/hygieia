package com.capitalone.dashboard.evaluator;

import com.capitalone.dashboard.model.TestResult;
import com.capitalone.dashboard.model.TestSuiteType;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.HashMap;

@RunWith(MockitoJUnitRunner.class)
public class RegressionTestResultEvaluatorTest {

    @InjectMocks
    private RegressionTestResultEvaluator regressionTestResultEvaluator;

    @Test
    public void evaluate_featureTestResult(){
        TestResult testResult = getTestResult();
        HashMap featureTestMap = regressionTestResultEvaluator.getFeatureTestResult(testResult);
        Assert.assertEquals(testResult.getSuccessCount(), Integer.parseInt(featureTestMap.get("successCount").toString()));
        Assert.assertEquals(testResult.getFailureCount(), Integer.parseInt(featureTestMap.get("failureCount").toString()));
        Assert.assertEquals(testResult.getSkippedCount(), Integer.parseInt(featureTestMap.get("skipCount").toString()));
        Assert.assertEquals(testResult.getTotalCount(), Integer.parseInt(featureTestMap.get("totalCount").toString()));
    }

    private TestResult getTestResult(){
        TestResult testResult = new TestResult();
        testResult.setType(TestSuiteType.Functional);
        testResult.setSuccessCount(10);
        testResult.setFailureCount(5);
        testResult.setSkippedCount(1);
        testResult.setTotalCount(16);
        return testResult;
    }
}
