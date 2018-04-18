package com.capitalone.dashboard.evaluator;

import com.capitalone.dashboard.model.AuditException;
import com.capitalone.dashboard.model.CollectorItem;
import com.capitalone.dashboard.model.CollectorType;
import com.capitalone.dashboard.model.Dashboard;
import com.capitalone.dashboard.model.TestResult;
import com.capitalone.dashboard.model.TestSuiteType;
import com.capitalone.dashboard.model.TestCapability;
import com.capitalone.dashboard.model.TestSuite;
import com.capitalone.dashboard.model.StoryIndicators;
import com.capitalone.dashboard.model.Feature;
import com.capitalone.dashboard.repository.FeatureRepository;
import com.capitalone.dashboard.repository.TestResultRepository;
import com.capitalone.dashboard.response.TestResultsAuditResponse;
import com.capitalone.dashboard.status.TestResultAuditStatus;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import java.util.stream.Collectors;

@Component
public class RegressionTestResultEvaluator extends Evaluator<TestResultsAuditResponse> {

    private final TestResultRepository testResultRepository;
    private final FeatureRepository featureRepository;


    @Autowired
    public RegressionTestResultEvaluator(TestResultRepository testResultRepository, FeatureRepository featureRepository) {
        this.testResultRepository = testResultRepository;
        this.featureRepository = featureRepository;
    }

    @Override
    public Collection<TestResultsAuditResponse> evaluate(Dashboard dashboard, long beginDate, long endDate, Map<?, ?> dummy) throws AuditException {
        List<CollectorItem> testItems = getCollectorItems(dashboard, "codeanalysis", CollectorType.Test);
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
        List<TestResult> testResults = testResultRepository
                .findByCollectorItemIdAndTimestampIsBetweenOrderByTimestampDesc(testItem.getId(), beginDate-1, endDate+1);

        TestResultsAuditResponse testResultsAuditResponse = new TestResultsAuditResponse();

        if (CollectionUtils.isEmpty(testResults)) {
            testResultsAuditResponse.addAuditStatus(TestResultAuditStatus.TEST_RESULT_MISSING);
            return testResultsAuditResponse;
        }

        for (TestResult testResult : testResults) {
            if (TestSuiteType.Regression.toString().equalsIgnoreCase(testResult.getType().name()) ||
                    TestSuiteType.Functional.toString().equalsIgnoreCase(testResult.getType().name())) {
                testResultsAuditResponse.addAuditStatus((testResult.getFailureCount() == 0) ? TestResultAuditStatus.TEST_RESULT_AUDIT_OK : TestResultAuditStatus.TEST_RESULT_AUDIT_FAIL);
                testResultsAuditResponse.setTestCapabilities(testResult.getTestCapabilities());
                testResultsAuditResponse.setLastExecutionTime(testResult.getStartTime());

                List<StoryIndicators> storyIndicatorsList = new ArrayList<>();

                testResults.stream()
                        .map(TestResult::getTestCapabilities).flatMap(Collection::stream)
                        .map(TestCapability::getTestSuites).flatMap(Collection::stream)
                        .map(TestSuite::getTestCases).flatMap(Collection::stream)
                        .forEach(testCase -> {
                            Set<String> tags = testCase.getTags();
                            if (CollectionUtils.isEmpty(tags)) {
                                testResultsAuditResponse.addAuditStatus(TestResultAuditStatus.TEST_RESULTS_TRACEABILITY_NOT_FOUND);
                            } else {
                                Pattern p = Pattern.compile("((?<!([A-Za-z]{1,10})-?)[A-Z]+-\\d+)");
                                tags.forEach(tag -> {
                                    Matcher tagMatch = p.matcher(tag);
                                    if (tagMatch.find()){
                                        List<Feature> featureDetails = featureRepository.getStoryByNumber(tag.substring(1, tag.length()));

                                        featureDetails.stream()
                                                .forEach(feature -> {
                                                    System.out.print("\n ********** VALID TAG" + tag + "\n");
                                                    testResultsAuditResponse.addAuditStatus((tag.equals("@" + feature.getsNumber())) ? TestResultAuditStatus.TEST_RESULTS_TRACEABILITY_STORY_MATCH :
                                                            TestResultAuditStatus.TEST_RESULTS_TRACEABILITY_STORY_NOT_FOUND);

                                                    StoryIndicators storyIndicators = new StoryIndicators();

                                                    storyIndicators.setsId(feature.getsId());
                                                    storyIndicators.setsTypeName(feature.getsTypeName());
                                                    storyIndicators.setsNumber(feature.getsNumber());
                                                    storyIndicators.setsName(feature.getsName());
                                                    storyIndicators.setsEpicNumber(feature.getsEpicNumber());
                                                    storyIndicators.setsEpicName(feature.getsEpicName());
                                                    storyIndicators.setsProjectName(feature.getsProjectName());
                                                    storyIndicators.setsTeamName(feature.getsTeamName());
                                                    storyIndicators.setsSprintName(feature.getsSprintName());

                                                    if (feature.getsStatus().equalsIgnoreCase("ACCEPTED") ||
                                                            feature.getsStatus().equalsIgnoreCase("CLOSED") ||
                                                            feature.getsStatus().equalsIgnoreCase("RESOLVED") ||
                                                            feature.getsStatus().equalsIgnoreCase("DONE")) {
                                                        storyIndicators.setsStatus(feature.getsStatus());
                                                        storyIndicators.setsState(feature.getsState());
                                                    } else {
                                                        testResultsAuditResponse.addAuditStatus(TestResultAuditStatus.TEST_RESULTS_TRACEABILITY_STORY_STATUS_INVALID);
                                                    }
                                                    storyIndicatorsList.add(storyIndicators);
                                                    testCase.setStoryIndicators(storyIndicatorsList);
                                                });
                                    }

                                });
                            }
                        });
                break;
            }
        }

        return testResultsAuditResponse;
    }
}
