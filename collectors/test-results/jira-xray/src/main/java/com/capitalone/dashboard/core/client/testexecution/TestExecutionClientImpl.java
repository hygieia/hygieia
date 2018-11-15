package com.capitalone.dashboard.core.client.testexecution;

import com.capitalone.dashboard.TestResultSettings;
import com.capitalone.dashboard.api.domain.TestExecution;
import com.capitalone.dashboard.api.domain.TestRun;
import com.capitalone.dashboard.api.domain.TestStep;
import com.capitalone.dashboard.core.client.JiraXRayRestClientImpl;
import com.capitalone.dashboard.core.client.JiraXRayRestClientSupplier;
import com.capitalone.dashboard.model.CollectorItem;
import com.capitalone.dashboard.model.Feature;
import com.capitalone.dashboard.model.TestResult;
import com.capitalone.dashboard.model.TestCase;
import com.capitalone.dashboard.model.TestCaseStatus;
import com.capitalone.dashboard.model.TestCaseStep;
import com.capitalone.dashboard.model.TestCapability;
import com.capitalone.dashboard.model.TestSuite;
import com.capitalone.dashboard.model.TestSuiteType;
import com.capitalone.dashboard.repository.CollectorItemRepository;
import com.capitalone.dashboard.repository.FeatureRepository;
import com.capitalone.dashboard.repository.TestResultCollectorRepository;
import com.capitalone.dashboard.repository.TestResultRepository;
import com.capitalone.dashboard.util.FeatureCollectorConstants;
import org.bson.types.ObjectId;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

public class TestExecutionClientImpl implements TestExecutionClient {
    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(TestExecutionClientImpl.class);
    private final TestResultSettings testResultSettings;
    private final TestResultRepository testResultRepository;
    private final TestResultCollectorRepository testResultCollectorRepository;
    private final FeatureRepository featureRepository;
    private final CollectorItemRepository collectorItemRepository;
    private JiraXRayRestClientImpl restClient;
    private final JiraXRayRestClientSupplier restClientSupplier;

    public TestExecutionClientImpl(TestResultRepository testResultRepository, TestResultCollectorRepository testResultCollectorRepository,
                                   FeatureRepository featureRepository, CollectorItemRepository collectorItemRepository,
                                   TestResultSettings testResultSettings, JiraXRayRestClientSupplier restClientSupplier) {
        this.testResultRepository = testResultRepository;
        this.testResultCollectorRepository = testResultCollectorRepository;
        this.featureRepository = featureRepository;
        this.testResultSettings = testResultSettings;
        this.restClientSupplier = restClientSupplier;
        this.collectorItemRepository = collectorItemRepository;
    }

    /**
     * Updates the test result information in MongoDB with Pagination. pageSize is defined in properties
     *
     * @return
     */
    public int updateTestResultInformation() {
        int count = 0;
        int pageSize = testResultSettings.getPageSize();

        boolean hasMore = true;
        List<Feature> testExecutions = featureRepository.getStoryByType("Test Execution");
        List<Feature> manualTestExecutions = this.getManualTestExecutions(testExecutions);

        for (int i = 0; hasMore; i += 1) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Obtaining story information starting at index " + i + "...");
            }
            long queryStart = System.currentTimeMillis();

            List<Feature> pagedTestExecutions = this.getTestExecutions(manualTestExecutions, i, pageSize);

            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Story information query took " + (System.currentTimeMillis() - queryStart) + " ms");
            }

            if (manualTestExecutions != null && !manualTestExecutions.isEmpty()) {
                updateMongoInfo(pagedTestExecutions);
                count += pagedTestExecutions.size();
            }

            LOGGER.info("Loop i " + i + " pageSize " + pagedTestExecutions.size());

            // will result in an extra call if number of results == pageSize
            // but I would rather do that then complicate the jira client implementation
            if (pagedTestExecutions == null || pagedTestExecutions.size() < pageSize) {
                hasMore = false;
                break;
            }
        }

        return count;
    }

    /**
     * Updates the MongoDB with a JSONArray received from the source system
     * back-end with story-based data.
     *
     * @param currentPagedTestExecutions
     *            A list response of Jira issues from the source system
     */
    private void updateMongoInfo(List<Feature> currentPagedTestExecutions) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Size of paged Jira response: " + (currentPagedTestExecutions == null? 0 : currentPagedTestExecutions.size()));
        }

        if (currentPagedTestExecutions != null) {
            List<TestResult> testResultsToSave = new ArrayList<>();
            ObjectId collectorItemId;

            for (Feature testExec : currentPagedTestExecutions) {

                // Set collectoritemid for manual test results
                if (testExec.getsTeamID() != null) {
                    collectorItemId = this.collectorItemRepository.findByJiraTeamId(testExec.getsTeamID()).getId();
                } else if (testExec.getsProjectID() != null) {
                    collectorItemId = this.collectorItemRepository.findByJiraProjectId(testExec.getsProjectID()).getId();
                } else {
                    CollectorItem collectorItem = new CollectorItem();
                    collectorItemId = collectorItem.getId();
                }

                TestResult testResult = new TestResult();

                testResult.setCollectorItemId(collectorItemId);
                testResult.setDescription(testExec.getsName());

                testResult.setTargetAppName(testExec.getsProjectName());
                testResult.setType(TestSuiteType.Manual);
                try {
                    TestExecution testExecution = new TestExecution(new URI(testExec.getsUrl()), testExec.getsNumber(), Long.parseLong(testExec.getsId()));
                    testResult.setUrl(testExecution.getSelf().toString());

                    restClient = (JiraXRayRestClientImpl) restClientSupplier.get();
                    Iterable<TestExecution.Test> tests = restClient.getTestExecutionClient().getTests(testExecution).claim();

                    if (tests != null) {
                        int totalCount = (int) tests.spliterator().getExactSizeIfKnown();

                        Map<String,Integer> testCountByStatus = this.getTestCountStatusMap(testExec, tests);
                        int failCount = testCountByStatus.get("FAIL_COUNT");
                        int passCount = testCountByStatus.get("PASS_COUNT");

                        List<TestCapability> capabilities = new ArrayList<>();
                        TestCapability capability = new TestCapability();
                        capability.setDescription(testExec.getsName());
                        capability.setTotalTestSuiteCount(1);
                        capability.setType(TestSuiteType.Manual);
                        List<TestSuite> testSuites = new ArrayList<>();
                        TestSuite testSuite = new TestSuite();

                        testSuite.setDescription(testExec.getsName());
                        testSuite.setType(TestSuiteType.Manual);

                        testSuite.setTotalTestCaseCount(totalCount);
                        testSuite.setFailedTestCaseCount(failCount);
                        testSuite.setSuccessTestCaseCount(passCount);

                        int skipCount = totalCount - (failCount + passCount);
                        testSuite.setSkippedTestCaseCount(skipCount);

                        if(failCount > 0) {
                            capability.setStatus(TestCaseStatus.Failure);
                            testResult.setResultStatus("Failure");
                            testSuite.setStatus(TestCaseStatus.Failure);
                            testResult.setFailureCount(1);
                            capability.setFailedTestSuiteCount(1);
                        } else if (totalCount == passCount){
                            capability.setStatus(TestCaseStatus.Success);
                            testResult.setResultStatus("Success");
                            testSuite.setStatus(TestCaseStatus.Success);
                            testResult.setSuccessCount(1);
                            capability.setSuccessTestSuiteCount(1);
                        } else {
                            capability.setStatus(TestCaseStatus.Skipped);
                            testResult.setResultStatus("Skipped");
                            testSuite.setStatus(TestCaseStatus.Skipped);
                            testResult.setSkippedCount(1);
                            capability.setSkippedTestSuiteCount(1);
                        }

                        testSuite.setTestCases(this.getTestCases(tests, testExec));
                        testSuites.add(testSuite);
                        capability.setTestSuites(testSuites);
                        capabilities.add(capability);
                        testResult.setTestCapabilities(capabilities);
                    }

                } catch (URISyntaxException u) {
                    LOGGER.error("URI Syntax Invalid");
                }
                testResultsToSave.add(testResult);
            }

            // Saving back to MongoDB
            testResultRepository.save(testResultsToSave);
        }
    }

    /**
     * Get the test cases for a test suite
     *
     * @param tests
     * @param testExec
     * @return
     */
    private List<TestCase> getTestCases(Iterable<TestExecution.Test> tests, Feature testExec) {
        List<TestCase> testCases = new ArrayList<>();

        for (TestExecution.Test test : tests) {
            TestCase testCase = new TestCase();

            try {
                TestRun testRun = restClient.getTestRunClient().getTestRun(testExec.getsNumber(), test.getKey()).claim();

                testCase.setId(testRun.getId().toString());
                testCase.setDescription(test.toString());
                if (testRun.getSteps() != null) {
                    int totalSteps = (int) testRun.getSteps().spliterator().getExactSizeIfKnown();
                    Map<String,Integer> stepCountByStatus = this.getStepCountStatusMap(testRun);

                    int failSteps = stepCountByStatus.get("FAILSTEP_COUNT");
                    int passSteps = stepCountByStatus.get("PASSSTEP_COUNT");
                    int skipSteps = stepCountByStatus.get("SKIPSTEP_COUNT");
                    int unknownSteps = stepCountByStatus.get("UNKNOWNSTEP_COUNT");
                    testCase.setTotalTestStepCount(totalSteps);
                    testCase.setFailedTestStepCount(failSteps);
                    testCase.setSuccessTestStepCount(passSteps);
                    testCase.setSkippedTestStepCount(skipSteps);
                    testCase.setUnknownStatusCount(unknownSteps);
                    if(failSteps > 0) {
                        testCase.setStatus(TestCaseStatus.Failure);
                    } else if (skipSteps > 0){
                        testCase.setStatus(TestCaseStatus.Skipped);
                    } else if(passSteps > 0){
                        testCase.setStatus(TestCaseStatus.Success);
                    } else {
                        testCase.setStatus(TestCaseStatus.Unknown);
                    }

                    testCase.setTestSteps(this.getTestSteps(testRun));
                }

            } catch (Exception e) {
                LOGGER.error("Unable to get the Test Step: " + e);
            }
            testCases.add(testCase);
        }

        return testCases;
    }

    /**
     * Gets the test steps for a test case
     *
     * @param testRun
     * @return
     */
    private List<TestCaseStep> getTestSteps(TestRun testRun) {

        List<TestCaseStep> testSteps = new ArrayList<>();

        for (TestStep testStep : testRun.getSteps()) {
            TestCaseStep testCaseStep = new TestCaseStep();

            testCaseStep.setId(testStep.getId().toString());
            testCaseStep.setDescription(testStep.getStep().getRaw());
            if (testStep.getStatus().toString().equals("PASS")) {
                testCaseStep.setStatus(TestCaseStatus.Success);
            } else if (testStep.getStatus().toString().equals("FAIL")) {
                testCaseStep.setStatus(TestCaseStatus.Failure);
            } else {
                testCaseStep.setStatus(TestCaseStatus.Skipped);
            }
            testSteps.add(testCaseStep);
        }

        return testSteps;
    }


    /**
     * Gets the test cases count map based on the status {pass, fail, skip & unknown}
     *
     * @param testExec
     * @param tests
     * @return
     */
    private Map<String,Integer> getTestCountStatusMap(Feature testExec, Iterable<TestExecution.Test> tests) {

        Map<String,Integer> map = new HashMap<String,Integer>(4);
        int failCount = 0;
        int passCount = 0;
        int skipCount = 0;
        int unknownCount = 0;

        for (TestExecution.Test test : tests) {
            try {
                TestRun testRun = restClient.getTestRunClient().getTestRun(testExec.getsNumber(), test.getKey()).claim();
                if (testRun != null) {
                    if (testRun.getStatus().toString().equals("FAIL")) {
                        failCount++;
                    }else if (testRun.getStatus().toString().equals("PASS")) {
                        passCount++;
                    }else if (testRun.getStatus().toString().equals("SKIP")){
                        skipCount++;
                    }else{
                        unknownCount++;
                    }
                }
            } catch (Exception e) {
                LOGGER.error("Unable to get the Test Run: " + e);
            }
        }
        map.put("FAIL_COUNT", failCount);
        map.put("PASS_COUNT", passCount);
        map.put("SKIP_COUNT", skipCount);
        map.put("UNKNOWN_COUNT", unknownCount);
        return map;
    }

    /**
     * Gets the test step count map based on the status
     *
     * @param testRun
     * @return
     */
    private Map<String,Integer> getStepCountStatusMap(TestRun testRun) {
        Map<String,Integer> map = new HashMap<>(4);
        int failStepCount = 0, passStepCount = 0, skipStepCount = 0, unknownStepCount = 0;
        for (TestStep testStep : testRun.getSteps()) {
            if (testRun != null) {
                if (testStep.getStatus().toString().equals("PASS")) {
                    passStepCount++;
                } else if (testStep.getStatus().toString().equals("FAIL")) {
                    failStepCount++;
                } else if (testStep.getStatus().equals("SKIP")){
                    skipStepCount++;
                } else{
                    unknownStepCount++;
                }
            }
        }
        map.put("FAILSTEP_COUNT", failStepCount);
        map.put("PASSSTEP_COUNT", passStepCount);
        map.put("SKIPSTEP_COUNT", skipStepCount);
        map.put("UNKNOWNSTEP_COUNT", unknownStepCount);

        return map;
    }

    /**
     * Gets test executions with pagination
     *
     * @param sourceList
     * @param page
     * @param pageSize
     * @return
     */
    public List<Feature> getTestExecutions(List<Feature> sourceList, int page, int pageSize) {
        if(pageSize <= 0 || page < 0) {
            throw new IllegalArgumentException("invalid page size: " + pageSize);
        }

        int fromIndex = page * pageSize;
        if(sourceList == null || sourceList.size() < fromIndex){
            return Collections.emptyList();
        }

        return sourceList.subList(fromIndex, Math.min(fromIndex + pageSize, sourceList.size()));
    }

    /**
     * Filters all the manual test executions
     *
     * @param testExecutions
     * @return
     */
    public List<Feature> getManualTestExecutions(List<Feature> testExecutions) {
        List<Feature> manualTestExecutions = new ArrayList<>();
        String[] automationKeywords = {"automated", "automation"};

        for (Feature testExecution : testExecutions) {
            if (!Arrays.stream(automationKeywords).parallel().anyMatch(testExecution.getsName().toLowerCase()::contains)) {
                manualTestExecutions.add(testExecution);
            }
        }
        return manualTestExecutions;
    }

    /**
     * Retrieves the maximum change date for a given query.
     *
     * @return A list object of the maximum change date
     */
    public String getMaxChangeDate() {
        String data = null;

        try {
            List<Feature> response = featureRepository
                    .findTopByCollectorIdAndChangeDateGreaterThanOrderByChangeDateDesc(
                            testResultCollectorRepository.findByName(FeatureCollectorConstants.JIRA_XRAY).getId(),
                            testResultSettings.getDeltaStartDate());
            if ((response != null) && !response.isEmpty()) {
                data = response.get(0).getChangeDate();
            }
        } catch (Exception e) {
            LOGGER.error("There was a problem retrieving or parsing data from the local "
                    + "repository while retrieving a max change date\nReturning null", e);
        }

        return data;
    }
}