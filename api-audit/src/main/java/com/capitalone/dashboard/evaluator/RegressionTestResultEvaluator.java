package com.capitalone.dashboard.evaluator;

import com.capitalone.dashboard.model.AuditException;
import com.capitalone.dashboard.model.CollectorItem;
import com.capitalone.dashboard.model.CollectorType;
import com.capitalone.dashboard.model.Dashboard;
import com.capitalone.dashboard.model.TestResult;
import com.capitalone.dashboard.model.TestSuiteType;
import com.capitalone.dashboard.repository.CustomRepositoryQuery;
import com.capitalone.dashboard.response.TestResultsAuditResponse;
import com.capitalone.dashboard.status.TestResultAuditStatus;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class RegressionTestResultEvaluator extends Evaluator<TestResultsAuditResponse> {

    private final CustomRepositoryQuery customRepositoryQuery;


    @Autowired
    public RegressionTestResultEvaluator(CustomRepositoryQuery customRepositoryQuery) {
        this.customRepositoryQuery = customRepositoryQuery;
    }

    @Override
    public Collection<TestResultsAuditResponse> evaluate(Dashboard dashboard, long beginDate, long endDate, Map<?, ?> dummy) throws AuditException {
        List<CollectorItem> testItems = getCollectorItems(dashboard, "test", CollectorType.Test);
        Collection<TestResultsAuditResponse> responses = new ArrayList<>();
        if (CollectionUtils.isEmpty(testItems)) {
            throw new AuditException("No tests configured", AuditException.NO_COLLECTOR_ITEM_CONFIGURED);
        }

        return testItems.stream().map(item -> evaluate(item, beginDate, endDate, null)).collect(Collectors.toList());
    }

    @Override
    public TestResultsAuditResponse evaluate(CollectorItem collectorItem, long beginDate, long endDate, Map<?, ?> dummy) {
        return getRegressionTestResultAudit(collectorItem, beginDate, endDate);
    }

    /**
     * @param testItem Test Collector Item
     * @return TestResultsAuditResponse
     * Thrown by Object mapper method
     */
    private TestResultsAuditResponse getRegressionTestResultAudit(CollectorItem testItem, long beginDate, long endDate) {
        List<TestResult> testResults = customRepositoryQuery
                .findByCollectorItemIdAndTimestampGreaterThanEqualAndTimestampLessThanEqual(testItem.getId(), beginDate, endDate);

        TestResultsAuditResponse testResultsAuditResponse = new TestResultsAuditResponse();

        if (CollectionUtils.isEmpty(testResults)) {
            testResultsAuditResponse.addAuditStatus(TestResultAuditStatus.TEST_RESULT_MISSING);
            return testResultsAuditResponse;
        }

        testResults.stream().filter(testResult -> TestSuiteType.Regression.toString().equalsIgnoreCase(testResult.getType().name())).forEach(testResult -> {
            testResultsAuditResponse.addAuditStatus((testResult.getFailureCount() == 0) ? TestResultAuditStatus.TEST_RESULT_AUDIT_OK : TestResultAuditStatus.TEST_RESULT_AUDIT_FAIL);
            testResultsAuditResponse.setTestCapabilities(testResult.getTestCapabilities());
        });

        if (CollectionUtils.isEmpty(testResultsAuditResponse.getTestCapabilities())) {
            testResultsAuditResponse.addAuditStatus(TestResultAuditStatus.TEST_RESULT_MISSING);
        }
        return testResultsAuditResponse;
    }
}
