package com.capitalone.dashboard.evaluator;

import com.capitalone.dashboard.model.AuditException;
import com.capitalone.dashboard.model.CollectorItem;
import com.capitalone.dashboard.model.CollectorType;
import com.capitalone.dashboard.model.Dashboard;
import com.capitalone.dashboard.model.PerfIndicators;
import com.capitalone.dashboard.model.PerfTest;
import com.capitalone.dashboard.model.TestCapability;
import com.capitalone.dashboard.model.TestCase;
import com.capitalone.dashboard.model.TestCaseStep;
import com.capitalone.dashboard.model.TestResult;
import com.capitalone.dashboard.model.TestSuite;
import com.capitalone.dashboard.model.TestSuiteType;
import com.capitalone.dashboard.repository.TestResultRepository;
import com.capitalone.dashboard.response.PerformanceTestAuditResponse;
import com.capitalone.dashboard.response.TestResultsAuditResponse;
import com.capitalone.dashboard.status.PerformanceTestAuditStatus;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class PerformanceTestResultEvaluator extends Evaluator<PerformanceTestAuditResponse> {

    private final TestResultRepository testResultRepository;


    @Autowired
    public PerformanceTestResultEvaluator(TestResultRepository testResultRepository) {

        this.testResultRepository = testResultRepository;
    }

    @Override
    public Collection<PerformanceTestAuditResponse> evaluate(Dashboard dashboard, long beginDate, long endDate, Map<?, ?> dummy) throws AuditException {
        List<CollectorItem> testItems = getCollectorItems(dashboard, "codeanalysis", CollectorType.Test);
        Collection<TestResultsAuditResponse> responses = new ArrayList<>();
        if (CollectionUtils.isEmpty(testItems)) {
            throw new AuditException("No tests configured", AuditException.NO_COLLECTOR_ITEM_CONFIGURED);
        }

        return testItems.stream().map(item -> evaluate(item, beginDate, endDate, null)).collect(Collectors.toList());
    }

    @Override
    public PerformanceTestAuditResponse evaluate(CollectorItem collectorItem, long beginDate, long endDate, Map<?, ?> dummy) {
        return getPerformanceTestAudit(collectorItem, beginDate, endDate);
    }


    private PerformanceTestAuditResponse getPerformanceTestAudit(CollectorItem perfItem, long beginDate, long endDate) {

        PerformanceTestAuditResponse perfReviewResponse = new PerformanceTestAuditResponse();
        List<TestResult> testResults = testResultRepository.findByCollectorItemIdAndTimestampIsBetweenOrderByTimestampDesc(perfItem.getId(), beginDate-1, endDate+1);
        List<PerfTest> testlist = new ArrayList<>();

        for (TestResult testResult : testResults) {
            if (TestSuiteType.Performance.toString().equalsIgnoreCase(testResult.getType().name())) {
                Collection<TestCapability> testCapabilities = testResult.getTestCapabilities();
                for (TestCapability testCapability : testCapabilities) {
                    PerfTest test = new PerfTest();
                    List<PerfIndicators> kpilist = new ArrayList<>();
                    Collection<TestSuite> testSuites = testCapability.getTestSuites();
                    for (TestSuite testSuite : testSuites) {
                        Collection<TestCase> testCases = testSuite.getTestCases();
                        for (TestCase testCase : testCases) {
                            PerfIndicators kpi = new PerfIndicators();
                            kpi.setStatus(testCase.getStatus().toString());
                            kpi.setType(testCase.getDescription().toString());
                            Collection<TestCaseStep> testSteps = testCase.getTestSteps();
                            int j = 0;
                            for (TestCaseStep testCaseStep : testSteps) {
                                String value = testCaseStep.getDescription();
                                if (j == 0) {
                                    kpi.setTarget(Double.parseDouble(value));
                                    if(testCase.getDescription().equalsIgnoreCase("KPI : Avg response times") && !value.isEmpty()){
                                        perfReviewResponse.addAuditStatus(PerformanceTestAuditStatus.PERFORMANCE_THRESHOLDS_RESPONSE_TIME_FOUND);
                                    }else if((testCase.getDescription().equalsIgnoreCase("KPI : Transaction Per Second") && !value.isEmpty())) {
                                        perfReviewResponse.addAuditStatus(PerformanceTestAuditStatus.PERFORMANCE_THRESHOLDS_TRANSACTIONS_PER_SECOND_FOUND);
                                    }else if(testCase.getDescription().equalsIgnoreCase("KPI : Error Rate Threshold") && !value.isEmpty() ){
                                        perfReviewResponse.addAuditStatus(PerformanceTestAuditStatus.PERFORMANCE_THRESHOLDS_ERROR_RATE_FOUND);
                                    }
                                }
                                if (j == 1) kpi.setAchieved(Double.parseDouble(value));
                                j++;
                            }
                            kpilist.add(kpi);
                            if(kpi.getType().equalsIgnoreCase("KPI : Avg response times")&& (kpi.getTarget() > kpi.getAchieved()))
                            {
                                perfReviewResponse.addAuditStatus(PerformanceTestAuditStatus.PERFORMANCE_THRESHOLD_RESPONSE_TIME_MET);
                            }else if(kpi.getType().equalsIgnoreCase("KPI : Transaction Per Second")&& (kpi.getTarget() <= kpi.getAchieved())){
                                perfReviewResponse.addAuditStatus(PerformanceTestAuditStatus.PERFORMANCE_THRESHOLD_TRANSACTIONS_PER_SECOND_MET);
                            }else if(kpi.getType().equalsIgnoreCase("KPI : Error Rate Threshold")&& (kpi.getTarget() >= kpi.getAchieved())){
                                perfReviewResponse.addAuditStatus(PerformanceTestAuditStatus.PERFORMANCE_THRESHOLD_ERROR_RATE_MET);
                            }

                        }
                        if(testResult.getDescription().equalsIgnoreCase("success")){
                            perfReviewResponse.addAuditStatus(PerformanceTestAuditStatus.PERFORMANCE_MET);
                        }
                        test.setRunId(testResult.getExecutionId());
                        test.setStartTime(testResult.getStartTime());
                        test.setEndTime(testResult.getEndTime());
                        test.setResultStatus(testResult.getDescription());
                        test.setPerfIndicators(kpilist);
                        test.setTestName(testSuite.getDescription());
                        test.setTimeStamp(testResult.getTimestamp());
                        testlist.add(test);
                    }
                }
            }
        }
        if (CollectionUtils.isEmpty(testlist)) {
            perfReviewResponse.addAuditStatus(PerformanceTestAuditStatus.PERF_RESULT_AUDIT_MISSING);
            return perfReviewResponse;
        }else {
            perfReviewResponse.addAuditStatus(PerformanceTestAuditStatus.PERFORMANCE_COMMIT_IS_CURRENT);
        }

        testlist.sort(Comparator.comparing(PerfTest::getStartTime).reversed());
        perfReviewResponse.setLastExecutionTime(testlist.get(0).getStartTime());
        perfReviewResponse.setResult(testlist);
        perfReviewResponse.addAuditStatus((int) testlist.stream().filter(list -> list.getResultStatus().matches("Success")).count() > 0 ?
                PerformanceTestAuditStatus.PERF_RESULT_AUDIT_OK : PerformanceTestAuditStatus.PERF_RESULT_AUDIT_FAIL);
        return perfReviewResponse;
    }
}
