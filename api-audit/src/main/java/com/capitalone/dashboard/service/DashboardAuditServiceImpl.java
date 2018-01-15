package com.capitalone.dashboard.service;

import com.capitalone.dashboard.evaluator.BuildEvaluator;
import com.capitalone.dashboard.evaluator.CodeQualityEvaluator;
import com.capitalone.dashboard.evaluator.CodeReviewEvaluator;
import com.capitalone.dashboard.evaluator.PerformanceTestResultEvaluator;
import com.capitalone.dashboard.evaluator.RegressionTestResultEvaluator;
import com.capitalone.dashboard.misc.HygieiaException;
import com.capitalone.dashboard.model.AuditException;
import com.capitalone.dashboard.model.AuditType;
import com.capitalone.dashboard.model.Cmdb;
import com.capitalone.dashboard.model.Dashboard;
import com.capitalone.dashboard.repository.CmdbRepository;
import com.capitalone.dashboard.repository.DashboardRepository;
import com.capitalone.dashboard.response.BuildAuditResponse;
import com.capitalone.dashboard.response.CodeQualityAuditResponse;
import com.capitalone.dashboard.response.CodeReviewAuditResponseV2;
import com.capitalone.dashboard.response.DashboardReviewResponse;
import com.capitalone.dashboard.response.PerformanceTestAuditResponse;
import com.capitalone.dashboard.response.TestResultsAuditResponse;
import com.capitalone.dashboard.status.DashboardAuditStatus;
import com.google.common.collect.Sets;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Set;

@Component
public class DashboardAuditServiceImpl implements DashboardAuditService {


    private final DashboardRepository dashboardRepository;
    private final CmdbRepository cmdbRepository;
    private final CodeReviewEvaluator codeReviewEvaluator;
    private final BuildEvaluator buildEvaluator;
    private final CodeQualityEvaluator codeQualityEvaluator;
    private final RegressionTestResultEvaluator regressionTestResultEvaluator;
    private final PerformanceTestResultEvaluator performanceTestResultEvaluator;


//    private static final Log LOGGER = LogFactory.getLog(DashboardAuditServiceImpl.class);

    @Autowired
    public DashboardAuditServiceImpl(DashboardRepository dashboardRepository, CmdbRepository cmdbRepository, CodeReviewEvaluator codeReviewEvaluator, BuildEvaluator buildEvaluator, CodeQualityEvaluator codeQualityEvaluator, RegressionTestResultEvaluator regressionTestResultEvaluator, PerformanceTestResultEvaluator performanceTestResultEvaluator) {
        this.dashboardRepository = dashboardRepository;
        this.cmdbRepository = cmdbRepository;
        this.codeReviewEvaluator = codeReviewEvaluator;
        this.buildEvaluator = buildEvaluator;
        this.codeQualityEvaluator = codeQualityEvaluator;
        this.regressionTestResultEvaluator = regressionTestResultEvaluator;
        this.performanceTestResultEvaluator = performanceTestResultEvaluator;
    }

    /**
     * Calculates audit response for a given dashboard
     *
     * @param dashboardTitle
     * @param dashboardType
     * @param businessService
     * @param businessApp
     * @param beginDate
     * @param endDate
     * @param auditTypes
     * @return @DashboardReviewResponse for a given dashboard
     * @throws AuditException
     */
    @Override
    public DashboardReviewResponse getDashboardReviewResponse(String dashboardTitle, String dashboardType, String businessService, String businessApp, long beginDate, long endDate, Set<AuditType> auditTypes) throws AuditException {
        Dashboard dashboard = getDashboard(dashboardTitle, dashboardType, businessService, businessApp);

        DashboardReviewResponse dashboardReviewResponse = new DashboardReviewResponse();
        if (dashboard == null) {
            dashboardReviewResponse.addAuditStatus(DashboardAuditStatus.DASHBOARD_NOT_REGISTERED);
            return dashboardReviewResponse;
        }

        dashboardReviewResponse.setDashboardTitle(dashboard.getTitle());
        dashboardReviewResponse.setBusinessApplication(businessApp);
        dashboardReviewResponse.setBusinessService(businessService);


        if (auditTypes.contains(AuditType.ALL)) {
            auditTypes.addAll(Sets.newHashSet(AuditType.values()));
            auditTypes.remove(AuditType.ALL);
        }

        for (AuditType auditType : auditTypes) {
            switch (auditType) {
                case BUILD_REVIEW:
                    try {
                        Collection<BuildAuditResponse> buildAuditResponses = buildEvaluator.evaluate(dashboard, beginDate, endDate, null);
                        dashboardReviewResponse.addAuditStatus(DashboardAuditStatus.DASHBOARD_BUILD_CONFIGURED);
                        dashboardReviewResponse.setBuild(buildAuditResponses);
                    } catch (AuditException e) {
                        if (e.getErrorCode() == AuditException.NO_COLLECTOR_ITEM_CONFIGURED) {
                            dashboardReviewResponse.addAuditStatus(DashboardAuditStatus.DASHBOARD_BUILD_NOT_CONFIGURED);
                        }
                    }
                    break;

                case CODE_REVIEW:
                    try {
                        Collection<CodeReviewAuditResponseV2> codeReviewResponses = codeReviewEvaluator.evaluate(dashboard, beginDate, endDate, null);
                        dashboardReviewResponse.addAuditStatus(DashboardAuditStatus.DASHBOARD_REPO_CONFIGURED);
                        dashboardReviewResponse.setCodeReview(codeReviewResponses);
                    } catch (AuditException e) {
                        if (e.getErrorCode() == AuditException.NO_COLLECTOR_ITEM_CONFIGURED) {
                            dashboardReviewResponse.addAuditStatus(DashboardAuditStatus.DASHBOARD_REPO_NOT_CONFIGURED);
                        }
                    }
                    break;

                case CODE_QUALITY:
                    try {
                        Collection<CodeQualityAuditResponse> codeQualityAuditResponses = codeQualityEvaluator.evaluate(dashboard, beginDate, endDate, null);
                        dashboardReviewResponse.addAuditStatus(DashboardAuditStatus.DASHBOARD_CODEQUALITY_CONFIGURED);
                        dashboardReviewResponse.setCodeQuality(codeQualityAuditResponses);
                    } catch (AuditException e) {
                        if (e.getErrorCode() == AuditException.NO_COLLECTOR_ITEM_CONFIGURED) {
                            dashboardReviewResponse.addAuditStatus(DashboardAuditStatus.DASHBOARD_CODEQUALITY_NOT_CONFIGURED);
                        }
                    }
                    break;


                case TEST_RESULT:
                    try {
                        Collection<TestResultsAuditResponse> testResultsAuditResponses = regressionTestResultEvaluator.evaluate(dashboard, beginDate, endDate, null);
                        dashboardReviewResponse.addAuditStatus(DashboardAuditStatus.DASHBOARD_TEST_CONFIGURED);
                        dashboardReviewResponse.setRegresionTestResult(testResultsAuditResponses);
                    } catch (AuditException e) {
                        if (e.getErrorCode() == AuditException.NO_COLLECTOR_ITEM_CONFIGURED) {
                            dashboardReviewResponse.addAuditStatus(DashboardAuditStatus.DASHBOARD_TEST_NOT_CONFIGURED);
                        }
                    }
                    break;

                case PERF_TEST:
                    try {
                        Collection<PerformanceTestAuditResponse> performanceTestAuditResponses = performanceTestResultEvaluator.evaluate(dashboard, beginDate, endDate, null);
                        dashboardReviewResponse.addAuditStatus(DashboardAuditStatus.DASHBOARD_PERFORMANCE_TEST_CONFIGURED);
                        dashboardReviewResponse.setPerformanceTestResult(performanceTestAuditResponses);
                    } catch (AuditException e) {
                        if (e.getErrorCode() == AuditException.NO_COLLECTOR_ITEM_CONFIGURED) {
                            dashboardReviewResponse.addAuditStatus(DashboardAuditStatus.DASHBOARD_PERFORMANCE_TEST_NOT_CONFIGURED);
                        }
                    }
                    break;


                case ALL:
                    break;

                default:
                    break;
            }
        }
        return dashboardReviewResponse;
    }

    /**
     * Finds the dashboard
     *
     * @param title
     * @param type
     * @param busServ
     * @param busApp
     * @return the @Dashboard for a given title, type, business service and app
     * @throws HygieiaException
     */
    private Dashboard getDashboard(String title, String type, String busServ, String busApp) throws
            AuditException {
        if (!StringUtils.isEmpty(title) && !StringUtils.isEmpty(type)) {
            return dashboardRepository.findByTitleAndType(title, type);

        } else if (!StringUtils.isEmpty(busServ) && !StringUtils.isEmpty(busApp)) {
            Cmdb busServItem = cmdbRepository.findByConfigurationItemAndItemType(busServ, "app");
            if (busServItem == null)
                throw new AuditException("Invalid Business Service Name.", AuditException.BAD_INPUT_DATA);
            Cmdb busAppItem = cmdbRepository.findByConfigurationItemAndItemType(busApp, "component");
            if (busAppItem == null)
                throw new AuditException("Invalid Business Application Name.", AuditException.BAD_INPUT_DATA);

            return dashboardRepository.findByConfigurationItemBusServObjectIdAndConfigurationItemBusAppObjectId(busServItem.getId(), busAppItem.getId());
        }
        return null;
    }
}
