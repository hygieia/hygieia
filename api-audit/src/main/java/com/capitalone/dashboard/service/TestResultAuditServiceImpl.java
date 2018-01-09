package com.capitalone.dashboard.service;

import com.capitalone.dashboard.ApiSettings;
import com.capitalone.dashboard.evaluator.CodeQualityEvaluator;
import com.capitalone.dashboard.evaluator.PeerReviewEvaluator;
import com.capitalone.dashboard.misc.HygieiaException;
import com.capitalone.dashboard.model.AuditStatus;
import com.capitalone.dashboard.model.Cmdb;
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
import com.capitalone.dashboard.model.Widget;
import com.capitalone.dashboard.repository.BuildRepository;
import com.capitalone.dashboard.repository.CmdbRepository;
import com.capitalone.dashboard.repository.CollItemCfgHistRepository;
import com.capitalone.dashboard.repository.CollectorItemRepository;
import com.capitalone.dashboard.repository.CollectorRepository;
import com.capitalone.dashboard.repository.ComponentRepository;
import com.capitalone.dashboard.repository.CustomRepositoryQuery;
import com.capitalone.dashboard.repository.DashboardRepository;
import com.capitalone.dashboard.repository.JobRepository;
import com.capitalone.dashboard.repository.TestResultRepository;
import com.capitalone.dashboard.response.PerfReviewResponse;
import com.capitalone.dashboard.response.TestResultsResponse;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Component
public class TestResultAuditServiceImpl implements TestResultAuditService {

    private final CustomRepositoryQuery customRepositoryQuery;
    private final JobRepository jobRepository;
    private final CollectorRepository collectorRepository;
    private final CollItemCfgHistRepository collItemCfgHistRepository;
    private final DashboardRepository dashboardRepository;
    private final CmdbRepository cmdbRepository;
    private final ComponentRepository componentRepository;
    private final BuildRepository buildRepository;
    private final CollectorItemRepository collectorItemRepository;

    private final TestResultRepository testResultRepository;
    private final PeerReviewEvaluator peerReviewEvaluator;
    private final CodeQualityEvaluator codeQualityEvaluator;
    private final ApiSettings settings;

    private static final Log LOGGER = LogFactory.getLog(TestResultAuditServiceImpl.class);

    @Autowired
    public TestResultAuditServiceImpl(
            CustomRepositoryQuery customRepositoryQuery,
            JobRepository jobRepository, CollectorRepository collectorRepository,
            CollItemCfgHistRepository collItemCfgHistRepository,
            DashboardRepository dashboardRepository,
            CmdbRepository cmdbRepository,
            ComponentRepository componentRepository,
            BuildRepository buildRepository,
            CollectorItemRepository collectorItemRepository,
            TestResultRepository testResultRepository,
            PeerReviewEvaluator peerReviewEvaluator, CodeQualityEvaluator codeQualityEvaluator, ApiSettings settings) {
        this.customRepositoryQuery = customRepositoryQuery;
        this.jobRepository = jobRepository;
        this.collectorRepository = collectorRepository;
        this.collItemCfgHistRepository = collItemCfgHistRepository;
        this.dashboardRepository = dashboardRepository;
        this.cmdbRepository = cmdbRepository;
        this.componentRepository = componentRepository;
        this.buildRepository = buildRepository;
        this.collectorItemRepository = collectorItemRepository;
        this.codeQualityEvaluator = codeQualityEvaluator;
        this.testResultRepository = testResultRepository;
        this.peerReviewEvaluator = peerReviewEvaluator;
        this.settings = settings;
    }

       /**
     * Retrieves test result execution details for a business application and
     * artifact
     *
     * @param jobUrl  Job Url of test execution
     * @param beginDt Beginning timestamp boundary
     * @param endDt   End Timestamp boundry
     * @return TestResultsResponse
     * @throws HygieiaException
     */

    public TestResultsResponse getTestResultExecutionDetails(String jobUrl, long beginDt, long endDt) throws HygieiaException {

        List<TestResult> testResults = getTestResults(jobUrl, beginDt, endDt);

        if (CollectionUtils.isEmpty(testResults))
            throw new HygieiaException("Unable to retreive  test result details for : " + jobUrl,
                    HygieiaException.BAD_DATA);

        return regressionTestResultAudit(testResults);

    }

    /**
     * Reusable method for constructing the StaticAnalysisResponse object for a
     *
     * @param testResults Test Result List
     * @return TestResultsResponse
     * Thrown by Object mapper method
     */
    private TestResultsResponse regressionTestResultAudit(List<TestResult> testResults) {
        TestResultsResponse testResultsResponse = new TestResultsResponse();
        boolean regressionTestSuitePresent = false;

        for (TestResult testResult : testResults) {
            if ("Regression".equalsIgnoreCase(testResult.getType().name())) {

                regressionTestSuitePresent = true;

                if (testResult.getFailureCount() == 0) {
                    testResultsResponse.addAuditStatus(AuditStatus.TEST_RESULT_AUDIT_OK);
                } else
                    testResultsResponse.addAuditStatus(AuditStatus.TEST_RESULT_AUDIT_FAIL);

                testResultsResponse.setTestCapabilities(testResult.getTestCapabilities());
            }

        }

        if (!regressionTestSuitePresent) {
            testResultsResponse.addAuditStatus(AuditStatus.TEST_RESULT_AUDIT_MISSING);
        }

        return testResultsResponse;
    }


    private List<TestResult> getTestResults(String jobUrl, long beginDt, long endDt) {
        return customRepositoryQuery
                .findByUrlAndTimestampGreaterThanEqualAndTimestampLessThanEqual(jobUrl, beginDt, endDt);
    }

    public PerfReviewResponse getresultsBycomponetAndTime(String businessComp, long from, long to) {
        Cmdb cmdb = cmdbRepository.findByConfigurationItemIgnoreCase(businessComp); // get CMDB iD
        Iterable<Dashboard> dashboard = dashboardRepository.findAllByConfigurationItemBusAppObjectId(cmdb.getId()); //get dashboard based on CMDB ID
        Iterator<Dashboard> dashboardIT = dashboard.iterator();  //Iterate through the dashboards to obtain the collectorIteamID
        PerfReviewResponse perfReviewResponse = new PerfReviewResponse();
        while (dashboardIT.hasNext()) {
            dashboardIT.next();
            Set<CollectorType> ci = dashboard.iterator().next().getApplication().getComponents().iterator().next().getCollectorItems().keySet();
            boolean Isperf = dashboard.iterator().next().getApplication().getComponents().iterator().next().getCollectorItems().values().iterator().next().iterator().next().getOptions().containsValue("jmeter");
            boolean Istest = Objects.equals(ci.iterator().next().name(), CollectorType.Test.name());
            if (Istest && Isperf)  //validate if the Test collector exists with jmeter collector Item
            {
                ObjectId collectorItemID = dashboard.iterator().next().getApplication().getComponents().iterator().next().getCollectorItems().values().iterator().next().iterator().next().getId();
                List<TestResult> result = customRepositoryQuery.findByCollectorItemIdAndTimestampGreaterThanEqualAndTimestampLessThanEqual(collectorItemID, from, to);
                List<PerfTest> testlist = new ArrayList<>();
                //loop through test result object to obtain performance artifacts.
                for (TestResult testResult : result) { //parse though the results to obtain performance KPI's
                    Collection<TestCapability> testCapabilityCollection = testResult.getTestCapabilities();
                    List<TestCapability> testCapabilityList = new ArrayList<>(testCapabilityCollection);

                    for (TestCapability testCapability : testCapabilityList) {
                        PerfTest test = new PerfTest();
                        List<PerfIndicators> kpilist = new ArrayList<>();
                        Collection<TestSuite> testSuitesCollection = testCapability.getTestSuites();
                        List<TestSuite> testSuiteList = new ArrayList<>(testSuitesCollection);

                        for (TestSuite testSuite : testSuiteList) {
                            Collection<TestCase> testCaseCollection = testSuite.getTestCases();
                            List<TestCase> testCaseList = new ArrayList<>(testCaseCollection);

                            for (TestCase testCase : testCaseList) {
                                PerfIndicators kpi = new PerfIndicators();
                                kpi.setStatus(testCase.getStatus().toString());
                                kpi.setType(testCase.getDescription());
                                Collection<TestCaseStep> testCaseStepCollection = testCase.getTestSteps();
                                List<TestCaseStep> testCaseStepList = new ArrayList<>(testCaseStepCollection);
                                int j = 0;
                                for (TestCaseStep testCaseStep : testCaseStepList) {
                                    String value = testCaseStep.getDescription();
                                    if (j == 0) {
                                        double targetdouble = Double.parseDouble(value);
                                        kpi.setTarget(targetdouble);
                                    } else {
                                        double achievedouble = Double.parseDouble(value);
                                        kpi.setAchieved(achievedouble);
                                    }
                                    j++;
                                }
                                kpilist.add(kpi);
                            }
                            //create performance test review object
                            test.setRunId(testResult.getExecutionId());
                            test.setStartTime(testResult.getStartTime());
                            test.setEndTime(testResult.getEndTime());
                            test.setResultStatus(testResult.getDescription());
                            test.setPerfIndicators(kpilist);
                            CollectorItem collectoritem = collectorItemRepository.findOne(collectorItemID);
                            test.setTestName((String) collectoritem.getOptions().get("jobName"));
                            test.setTimeStamp(testResult.getTimestamp());
                            testlist.add(test);
                        }
                    }
                }
                perfReviewResponse.setResult(testlist);
                int counter = (int) testlist.stream().filter(list -> list.getResultStatus().matches("Success")).count();
                if (testlist.size() == 0) {
                    perfReviewResponse.setAuditStatuses(AuditStatus.PERF_RESULT_AUDIT_MISSING);
                } else if (counter >= 1) {
                    perfReviewResponse.setAuditStatuses(AuditStatus.PERF_RESULT_AUDIT_OK);
                } else {
                    perfReviewResponse.setAuditStatuses(AuditStatus.PERF_RESULT_AUDIT_FAIL);
                }
            }
        }
        return perfReviewResponse;
    }

    /**
     * @param dashboard
     * @param widgetName
     * @param collectorType
     * @return list of @CollectorItem for a given dashboard, widget name and collector type
     */
    public List<CollectorItem> getCollectorItems(Dashboard dashboard, String widgetName, CollectorType collectorType) {
        List<Widget> widgets = dashboard.getWidgets();
        ObjectId componentId = widgets.stream().filter(widget -> widget.getName().equalsIgnoreCase(widgetName)).findFirst().map(Widget::getComponentId).orElse(null);

        if (componentId == null) return null;

        com.capitalone.dashboard.model.Component component = componentRepository.findOne(componentId);

        return component.getCollectorItems().get(collectorType);
    }

}
