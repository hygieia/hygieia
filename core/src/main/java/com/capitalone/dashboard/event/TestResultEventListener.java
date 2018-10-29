package com.capitalone.dashboard.event;

import com.capitalone.dashboard.model.*;
import com.capitalone.dashboard.repository.PerformanceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.AfterSaveEvent;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class TestResultEventListener extends AbstractMongoEventListener<TestResult> {

    private static final Logger LOGGER = LoggerFactory.getLogger(TestResultEventListener.class);

    private static final String STR_RESP_TIME_THRESHOLD = "KPI : Avg response times";
    private static final String STR_ACTUAL_RESP_TIME = "Actual Response Time";
    private static final String STR_TARGET_RESP_TIME = "Target Response Time";
    private static final String STR_TXN_PER_SEC_THRESHOLD = "KPI : Transaction Per Second";
    private static final String STR_ACTUAL_TXN_PER_SEC = "Actual Transactions per sec";
    private static final String STR_TARGET_TXN_PER_SEC = "Target Transactions per sec";
    private static final String STR_ERROR_RATE_THRESHOLD = "KPI : Error Rate Threshold";
    private static final String STR_ACTUAL_ERROR_RATE = "Actual Error Rate";
    private static final String STR_TARGET_ERROR_RATE = "Target Error Rate Threshold";
    private static final String STR_ONE = "1.0";
    private static final Long LONG_ZERO = 0L;

    private static long targetRespTime;
    private static long actualRespTime;
    private static long targetTxnsPerSec;
    private static long actualTxnsPerSec;
    private static long targetErrorRate;
    private static long actualErrorRate;
    private static int avgRespTimeCount;
    private static int txnPerSecCount;
    private static int errorRateCntCount;

    private final PerformanceRepository performanceRepository;

    private enum performanceMetricKeys {
        averageResponseTime,totalCalls,errorsperMinute,businessTransactionHealthPercent,nodeHealthPercent,violationObject,
        totalErrors,errorRateSeverity,responseTimeSeverity,callsperMinute,targetResponseTime,targetTransactionPerSec,
        targetErrorRateThreshold
    };

    @Autowired
    public TestResultEventListener(PerformanceRepository performanceRepository) {
        this.performanceRepository = performanceRepository;
    }

    /**
     * Create and save performance object for every test_result type is performance
     * @param event
     */
    @Override
    public void onAfterSave(AfterSaveEvent<TestResult> event) {
        TestResult testResult = event.getSource();
        LOGGER.info("TestResult afterSave event is triggered");

        // Ignore anything other than performance tests
        if (!TestSuiteType.Performance.equals(testResult.getType())) {
            return;
        }
        for (Object testCapabilityObj : testResult.getTestCapabilities().toArray()){
            TestCapability testCapability = (TestCapability) testCapabilityObj;
            for(Object testSuiteObj : testCapability.getTestSuites().toArray()){
                TestSuite testSuite = (TestSuite) testSuiteObj;
                for(Object testCaseObj : testSuite.getTestCases().toArray()){
                    TestCase testCase = (TestCase) testCaseObj;
                    readPerformanceMetrics(testCase);
                }
            }
        }

        Performance performance = new Performance();
        performance.setCollectorItemId(testResult.getCollectorItemId());
        performance.setType(PerformanceType.ApplicationPerformance);
        performance.setUrl(testResult.getUrl());
        performance.setVersion(STR_ONE);
        performance.setTimestamp(System.currentTimeMillis());
        performance.setTargetAppName(testResult.getTargetAppName());
        performance.setTargetEnvName(testResult.getTargetEnvName());

        Map<String,Object> metrics = new HashMap<>();
        metrics.put(performanceMetricKeys.averageResponseTime.name(), actualRespTime/avgRespTimeCount);
        metrics.put(performanceMetricKeys.callsperMinute.name(), actualTxnsPerSec/txnPerSecCount);
        metrics.put(performanceMetricKeys.errorsperMinute.name(), actualErrorRate/errorRateCntCount);
        metrics.put(performanceMetricKeys.totalCalls.name(), Long.valueOf(testResult.getTotalCount()));
        metrics.put(performanceMetricKeys.totalErrors.name(), Long.valueOf(testResult.getFailureCount()));

        metrics.put(performanceMetricKeys.businessTransactionHealthPercent.name(), Double.valueOf(STR_ONE));
        metrics.put(performanceMetricKeys.nodeHealthPercent.name(), Double.valueOf(STR_ONE));
        metrics.put(performanceMetricKeys.violationObject.name(), new String[0]);
        metrics.put(performanceMetricKeys.errorRateSeverity.name(), LONG_ZERO);
        metrics.put(performanceMetricKeys.responseTimeSeverity.name(), LONG_ZERO);

        metrics.put(performanceMetricKeys.targetResponseTime.name(), targetRespTime);
        metrics.put(performanceMetricKeys.targetTransactionPerSec.name(), targetTxnsPerSec);
        metrics.put(performanceMetricKeys.targetErrorRateThreshold.name(), targetErrorRate);

        performance.setMetrics(metrics);
        performanceRepository.save(performance);
        LOGGER.info("New Performance created from TestResult saved successfully");
    }

    /**
     * Reads the test result threshold values to build performance metrics object
     * @param testCase
     */
    private void readPerformanceMetrics(TestCase testCase) {
        if(testCase.getDescription().equalsIgnoreCase(STR_RESP_TIME_THRESHOLD)){
            for(Object testCaseStepObj : testCase.getTestSteps().toArray()){
                TestCaseStep testCaseStep = (TestCaseStep) testCaseStepObj;
                if (testCaseStep.getId().equalsIgnoreCase(STR_TARGET_RESP_TIME)){
                    targetRespTime = new Double(Double.valueOf(testCaseStep.getDescription())).longValue();
                }
                if (testCaseStep.getId().equalsIgnoreCase(STR_ACTUAL_RESP_TIME)){
                    actualRespTime = actualRespTime + new Double(Double.valueOf(testCaseStep.getDescription())).longValue();
                    avgRespTimeCount++;
                }
            }
        }
        if(testCase.getDescription().equalsIgnoreCase(STR_TXN_PER_SEC_THRESHOLD)){
            for(Object testCaseStepObj : testCase.getTestSteps().toArray()){
                TestCaseStep testCaseStep = (TestCaseStep) testCaseStepObj;
                if (testCaseStep.getId().equalsIgnoreCase(STR_TARGET_TXN_PER_SEC)){
                    targetTxnsPerSec = new Double(Double.valueOf(testCaseStep.getDescription())).longValue();
                }
                if (testCaseStep.getId().equalsIgnoreCase(STR_ACTUAL_TXN_PER_SEC)){
                    actualTxnsPerSec = actualTxnsPerSec + new Double(Double.valueOf(testCaseStep.getDescription())).longValue();
                    txnPerSecCount++;
                }
            }
        }
        if(testCase.getDescription().equalsIgnoreCase(STR_ERROR_RATE_THRESHOLD)){
            for(Object testCaseStepObj : testCase.getTestSteps().toArray()){
                TestCaseStep testCaseStep = (TestCaseStep) testCaseStepObj;
                if (testCaseStep.getId().equalsIgnoreCase(STR_TARGET_ERROR_RATE)){
                    targetErrorRate = new Double(Double.valueOf(testCaseStep.getDescription())).longValue();
                }
                if (testCaseStep.getId().equalsIgnoreCase(STR_ACTUAL_ERROR_RATE)){
                    actualErrorRate = actualErrorRate + new Double(Double.valueOf(testCaseStep.getDescription())).longValue();
                    errorRateCntCount++;
                }
            }
        }
    }
}
