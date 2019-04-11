package com.capitalone.dashboard.evaluator;

import com.capitalone.dashboard.ApiSettings;
import com.capitalone.dashboard.model.AuditException;
import com.capitalone.dashboard.model.CollectorItem;
import com.capitalone.dashboard.model.CollectorType;
import com.capitalone.dashboard.model.Dashboard;
import com.capitalone.dashboard.model.DashboardType;
import com.capitalone.dashboard.model.TestResult;
import com.capitalone.dashboard.model.TestSuiteType;
import com.capitalone.dashboard.model.TestCapability;
import com.capitalone.dashboard.model.TestSuite;
import com.capitalone.dashboard.model.Traceability;
import com.capitalone.dashboard.model.Widget;
import com.capitalone.dashboard.model.Feature;
import com.capitalone.dashboard.model.StoryIndicator;

import com.capitalone.dashboard.repository.FeatureRepository;
import com.capitalone.dashboard.repository.TestResultRepository;
import com.capitalone.dashboard.response.TestResultsAuditResponse;
import com.capitalone.dashboard.status.TestResultAuditStatus;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Date;
import java.util.Comparator;
import java.util.Optional;
import java.util.Arrays;
import java.util.Map;

import java.util.regex.Pattern;

import java.text.ParseException;
import java.text.SimpleDateFormat;

@Component
public class RegressionTestResultEvaluator extends Evaluator<TestResultsAuditResponse> {

    private final TestResultRepository testResultRepository;
    private final FeatureRepository featureRepository;
    private static final Logger LOGGER = LoggerFactory.getLogger(RegressionTestResultEvaluator.class);
    private long beginDate;
    private long endDate;
    private Dashboard dashboard;
    private static final String WIDGET_CODE_ANALYSIS = "codeanalysis";
    private static final String WIDGET_FEATURE = "feature";
    private static final String STR_TEAM_ID = "teamId";
    private static final String STR_UNDERSCORE = "_";
    private static final String STR_HYPHEN = "-";
    private static final String STR_AT = "@";
    private static final String STR_EMPTY = "";
    private static final String SUCCESS_COUNT = "successCount";
    private static final String FAILURE_COUNT = "failureCount";
    private static final String SKIP_COUNT = "skippedCount";
    private static final String TOTAL_COUNT = "totalCount";
    private static final String PRIORITY_HIGH = "High";

    @Autowired
    public RegressionTestResultEvaluator(TestResultRepository testResultRepository, FeatureRepository featureRepository) {
        this.testResultRepository = testResultRepository;
        this.featureRepository = featureRepository;
    }

    @Override
    public Collection<TestResultsAuditResponse> evaluate(Dashboard dashboard, long beginDate, long endDate, Map<?, ?> dummy) throws AuditException {
        this.beginDate = beginDate-1;
        this.endDate = endDate+1;
        this.dashboard = getDashboard(dashboard.getTitle(), DashboardType.Team);
        List<CollectorItem> testItems = getCollectorItems(this.dashboard, WIDGET_CODE_ANALYSIS, CollectorType.Test);
        Collection<TestResultsAuditResponse> testResultsAuditResponse = new ArrayList<>();
        if (CollectionUtils.isEmpty(testItems)) {
            throw new AuditException("No tests configured", AuditException.NO_COLLECTOR_ITEM_CONFIGURED);
        }
        testItems.forEach(testItem -> testResultsAuditResponse.add(getRegressionTestResultAudit(dashboard, testItem)));
        return testResultsAuditResponse;
    }

    @Override
    public TestResultsAuditResponse evaluate(CollectorItem collectorItem, long beginDate, long endDate, Map<?, ?> data) {
        return new TestResultsAuditResponse();
    }

    /**
     * Gets the json response from test_results collection with story information based on tags.
     * @param testItem
     * @return
     */
    protected TestResultsAuditResponse getRegressionTestResultAudit(Dashboard dashboard, CollectorItem testItem) {
        List<TestResult> testResults = testResultRepository.findByCollectorItemIdAndTimestampIsBetweenOrderByTimestampDesc(testItem.getId(), beginDate, endDate);
        return performTestResultAudit(dashboard, testItem, testResults);
    }

    /**
     * Perform test result audit
     *
     * @param testItem
     * @param testResults
     * @return testResultsAuditResponse
     */
    private TestResultsAuditResponse performTestResultAudit(Dashboard dashboard, CollectorItem testItem, List<TestResult> testResults) {

        TestResultsAuditResponse testResultsAuditResponse = new TestResultsAuditResponse();
        testResultsAuditResponse.setAuditEntity(testItem.getOptions());
        testResultsAuditResponse.setLastUpdated(testItem.getLastUpdated());
        if (CollectionUtils.isEmpty(testResults) || !isValidTestResultTestSuitType(testResults)){
            testResultsAuditResponse.addAuditStatus(TestResultAuditStatus.TEST_RESULT_MISSING);
            return testResultsAuditResponse;
        }
        TestResult testResult = testResults.stream().sorted(Comparator.comparing(TestResult::getTimestamp).reversed()).findFirst().get();
        Optional<TestCapability> testCapability = testResult.getTestCapabilities().stream().sorted(Comparator.comparing(TestCapability::getTimestamp).reversed()).findFirst();
        testResultsAuditResponse.setTestCapabilities(Arrays.asList(testCapability.orElse(new TestCapability())));
        testResultsAuditResponse.setLastExecutionTime(testResult.getStartTime());
        testResultsAuditResponse.setType(testResult.getType().toString());
        testResultsAuditResponse.setFeatureTestResult(getFeatureTestResult(testResult));
        testResultsAuditResponse = updateTestResultAuditStatuses(testCapability.get(), testResultsAuditResponse);
        testResultsAuditResponse = updateTraceabilityDetails(dashboard, testResult, testResultsAuditResponse);
        return testResultsAuditResponse;

    }

    /***
     * Update traceability details with calculated percent value
     * @param testResult,testResultsAuditResponse
     * @return testResultsAuditResponse
     */
    private TestResultsAuditResponse updateTraceabilityDetails(Dashboard dashboard, TestResult testResult, TestResultsAuditResponse testResultsAuditResponse) {

        Traceability traceability = new Traceability();
        List<String> totalStoriesList = new ArrayList<>();
        List<String> totalCompletedStories = new ArrayList<>();
        List<HashMap> totalStories = new ArrayList<>();
        double traceabilityThreshold = settings.getTraceabilityThreshold();

        Widget featureWidget = getFeatureWidget(dashboard);
        Optional<Object> teamIdOpt = Optional.ofNullable(featureWidget.getOptions().get(STR_TEAM_ID));
        String teamId = teamIdOpt.isPresent() ? teamIdOpt.get().toString() : "";
        List<Feature> featureList = featureRepository.getStoryByTeamID(teamId);

        featureList.stream().forEach(feature -> {
            HashMap<String, String> storyAuditStatusMap = new HashMap<>();
            totalStoriesList.add(feature.getsNumber());

            if(isValidChangeDate(feature)) {
                if(this.isValidStoryStatus(feature.getsStatus())){
                    totalCompletedStories.add(feature.getsNumber());
                    storyAuditStatusMap.put(feature.getsNumber(), TestResultAuditStatus.TEST_RESULTS_TRACEABILITY_STORY_MATCH.name());
                } else{
                    storyAuditStatusMap.put(feature.getsNumber(), TestResultAuditStatus.TEST_RESULTS_TRACEABILITY_STORY_STATUS_INVALID.name());
                }
            } else {
                storyAuditStatusMap.put(feature.getsNumber(), TestResultAuditStatus.TEST_RESULTS_TRACEABILITY_STORY_NOT_FOUND.name());
            }
            totalStories.add(storyAuditStatusMap);
        });
        if (totalCompletedStories.size() > NumberUtils.INTEGER_ZERO) {
            int totalStoryIndicatorCount = getTotalStoryIndicators(testResult).size();
            double percentage = (totalStoryIndicatorCount * 100) / totalCompletedStories.size();
            traceability.setPercentage(percentage);

            if (traceabilityThreshold == NumberUtils.DOUBLE_ZERO) {
                testResultsAuditResponse.addAuditStatus(TestResultAuditStatus.TEST_RESULTS_TRACEABILITY_THRESHOLD_DEFAULT);
            }
            if(percentage == NumberUtils.DOUBLE_ZERO){
                testResultsAuditResponse.addAuditStatus(TestResultAuditStatus.TEST_RESULTS_TRACEABILITY_NOT_FOUND);
            }
        } else {
            testResultsAuditResponse.addAuditStatus(TestResultAuditStatus.TEST_RESULTS_TRACEABILITY_NOT_FOUND_IN_GIVEN_DATE_RANGE);
        }
        traceability.setTotalCompletedStories(totalCompletedStories);
        traceability.setTotalStories(totalStories);
        traceability.setTotalStoryCount(totalStories.size());
        traceability.setThreshold(traceabilityThreshold);
        testResultsAuditResponse.setTraceability(traceability);
        return testResultsAuditResponse;
    }

    /**
     * Get story indicators by matching test case tags with feature stories
     * @param testResult
     * @return
     */
    private  List<StoryIndicator> getTotalStoryIndicators(TestResult testResult) {

        Pattern featureIdPattern = Pattern.compile(settings.getFeatureIDPattern());
        List<StoryIndicator> totalStoryIndicatorList = new ArrayList<>();
        testResult.getTestCapabilities().stream()
                .map(TestCapability::getTestSuites).flatMap(Collection::stream)
                .map(TestSuite::getTestCases).flatMap(Collection::stream)
                .forEach(testCase -> {
                    List<StoryIndicator> storyIndicatorList = new ArrayList<>();
                    testCase.getTags().forEach(tag -> {
                        if (featureIdPattern.matcher(getValidFeatureId(tag)).find()) {
                            List<Feature> features = featureRepository.getStoryByNumber(tag);
                            features.forEach(feature -> {
                                if (isValidChangeDate(feature) && isValidStoryStatus(feature.getsStatus())) {
                                    StoryIndicator storyIndicator = new StoryIndicator();
                                    storyIndicator.setStoryId(feature.getsId());
                                    storyIndicator.setStoryType(feature.getsTypeName());
                                    storyIndicator.setStoryNumber(feature.getsNumber());
                                    storyIndicator.setStoryName(feature.getsName());
                                    storyIndicator.setEpicNumber(feature.getsEpicNumber());
                                    storyIndicator.setEpicName(feature.getsEpicName());
                                    storyIndicator.setProjectName(feature.getsProjectName());
                                    storyIndicator.setTeamName(feature.getsTeamName());
                                    storyIndicator.setSprintName(feature.getsSprintName());
                                    storyIndicator.setStoryStatus(feature.getsStatus());
                                    storyIndicator.setStoryState(feature.getsState());
                                    storyIndicatorList.add(storyIndicator);
                                }
                            });
                    }
                    });
                    storyIndicatorList.forEach(storyIndicator -> {
                        if (!totalStoryIndicatorList.contains(storyIndicator)) {
                            totalStoryIndicatorList.add(storyIndicator);
                        }
                    });
                    testCase.setStoryIndicators(storyIndicatorList);
                });
        return totalStoryIndicatorList;
    }

    private CharSequence getValidFeatureId(String tag) {
        tag = tag.replaceAll(STR_UNDERSCORE, STR_HYPHEN).replaceAll(STR_AT, STR_EMPTY);
        return tag;
    }

    /**
     * Coverts the Human readable time date to Epoch Time Stamp in Milliseconds
     * @param feature
     * @return
     */
    private long getEpochChangeDate(Feature feature) {
        String datePattern = "yyyy-MM-dd'T'HH:mm:ss.SSS";
        long changeDate = 0;

        try {
            SimpleDateFormat sdf = new SimpleDateFormat(datePattern);
            Date dt = sdf.parse(feature.getChangeDate());
            changeDate = dt.getTime();
        } catch(ParseException e) {
            e.printStackTrace();
            LOGGER.error("Error in RegressionTestResultEvaluator.getEpochChangeDate() - Unable to match date pattern - " + e.getMessage());
        }

        return changeDate;
    }

    /**
     * Check whether the story status is valid
     * @param storyStatus
     * @return
     */
    private boolean isValidStoryStatus(String storyStatus) {
        final List<String> validStatus = settings.getValidStoryStatus();
        return validStatus.contains(storyStatus.toUpperCase());
    }

    /**
     * Check whether the feature date is valid
     * @param feature
     * @return
     */
    private boolean isValidChangeDate(Feature feature){
        return (this.getEpochChangeDate(feature) >= beginDate && this.getEpochChangeDate(feature) <= endDate);
    }

    /**
     * Get dashboard by title and type
     * @param title
     * @param dashboardType
     * @return
     */
    private Dashboard getDashboard(String title, DashboardType dashboardType) {
        return dashboardRepository.findByTitleAndType(title, dashboardType);
    }

    /**
     * Check whether the test result test suit type is valid
     * @param testResults
     * @return
     */
    public boolean isValidTestResultTestSuitType(List<TestResult> testResults) {
        return testResults.stream()
                .anyMatch(testResult -> testResult.getType().equals(TestSuiteType.Functional)
                        || testResult.getType().equals(TestSuiteType.Manual)
                        || testResult.getType().equals(TestSuiteType.Regression));
    }

    /**
     * Get feature widget
     * @return
     */
    public Widget getFeatureWidget(Dashboard dashboard) {
        return dashboard.getWidgets()
                .stream()
                .filter(widget -> widget.getName().equalsIgnoreCase(WIDGET_FEATURE))
                .findFirst().orElse(new Widget());
    }
    /**
     * Builds feature test result data map
     * @param testResult
     * @return featureTestResultMap
     */
    protected HashMap getFeatureTestResult(TestResult testResult) {
        HashMap<String,Integer> featureTestResultMap = new HashMap<>();
        featureTestResultMap.put(SUCCESS_COUNT, testResult.getSuccessCount());
        featureTestResultMap.put(FAILURE_COUNT, testResult.getFailureCount());
        featureTestResultMap.put(SKIP_COUNT, testResult.getSkippedCount());
        featureTestResultMap.put(TOTAL_COUNT,testResult.getTotalCount());
        return featureTestResultMap;
    }

    /**
     * update test result audit statuses
     * @param testCapability
     * @param testResultsAuditResponse
     * @return
     */
    private TestResultsAuditResponse updateTestResultAuditStatuses(TestCapability testCapability, TestResultsAuditResponse testResultsAuditResponse) {

        boolean isSuccessHighPriority = settings.getTestResultSuccessPriority().equalsIgnoreCase(PRIORITY_HIGH);
        boolean isFailureHighPriority = settings.getTestResultFailurePriority().equalsIgnoreCase(PRIORITY_HIGH);

        if(isAllTestCasesSkipped(testCapability)){
            testResultsAuditResponse.addAuditStatus(TestResultAuditStatus.TEST_RESULT_SKIPPED);
            return testResultsAuditResponse;
        }
        double testCasePassPercent = this.getTestCasePassPercent(testCapability);
        if (isFailureHighPriority){
            if (testCasePassPercent < settings.getTestResultThreshold()) {
                testResultsAuditResponse.addAuditStatus(TestResultAuditStatus.TEST_RESULT_AUDIT_FAIL);
            } else {
                testResultsAuditResponse.addAuditStatus(TestResultAuditStatus.TEST_RESULT_AUDIT_OK);
            }
        }else if (isSuccessHighPriority){
            if (testCasePassPercent > NumberUtils.INTEGER_ZERO) {
                testResultsAuditResponse.addAuditStatus(TestResultAuditStatus.TEST_RESULT_AUDIT_OK);
            } else {
                testResultsAuditResponse.addAuditStatus(TestResultAuditStatus.TEST_RESULT_AUDIT_FAIL);
            }
        }else {
            testResultsAuditResponse.addAuditStatus(TestResultAuditStatus.TEST_RESULT_MISSING);
        }
        return testResultsAuditResponse;
    }

    /**
     * Get test result pass percent
     * @param testCapability
     * @return
     */
    private double getTestCasePassPercent(TestCapability testCapability) {
        double testCaseSuccessCount = testCapability.getTestSuites().stream().mapToDouble(TestSuite::getSuccessTestCaseCount).sum();
        double totalTestCaseCount = testCapability.getTestSuites().stream().mapToDouble(TestSuite::getTotalTestCaseCount).sum();
        return (testCaseSuccessCount/totalTestCaseCount) * 100;
    }

    public void setSettings(ApiSettings settings) {
        this.settings = settings;
    }

    /**
     * Check if all the test cases are skipped
     * @param testCapability
     * @return
     */
    public boolean isAllTestCasesSkipped(TestCapability testCapability) {
        int totalTestCaseCount = testCapability.getTestSuites().stream().mapToInt(TestSuite::getTotalTestCaseCount).sum();
        int testCaseSkippedCount = testCapability.getTestSuites().stream().mapToInt(TestSuite::getSkippedTestCaseCount).sum();
        boolean isSkippedHighPriority = settings.getTestResultSkippedPriority().equalsIgnoreCase(PRIORITY_HIGH);

        if ((testCaseSkippedCount >= totalTestCaseCount) && isSkippedHighPriority){
            return true;
        }
        return false;
    }
}