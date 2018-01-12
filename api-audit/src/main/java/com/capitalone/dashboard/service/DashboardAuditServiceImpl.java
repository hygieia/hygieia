package com.capitalone.dashboard.service;

import com.capitalone.dashboard.evaluator.CodeReviewEvaluator;
import com.capitalone.dashboard.misc.HygieiaException;
import com.capitalone.dashboard.model.AuditException;
import com.capitalone.dashboard.model.AuditStatus;
import com.capitalone.dashboard.model.AuditType;
import com.capitalone.dashboard.model.Cmdb;
import com.capitalone.dashboard.model.Dashboard;
import com.capitalone.dashboard.repository.CmdbRepository;
import com.capitalone.dashboard.repository.DashboardRepository;
import com.capitalone.dashboard.response.CodeReviewAuditResponseV2;
import com.capitalone.dashboard.response.DashboardReviewResponse;
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


//    private static final Log LOGGER = LogFactory.getLog(DashboardAuditServiceImpl.class);

    @Autowired
    public DashboardAuditServiceImpl(DashboardRepository dashboardRepository, CmdbRepository cmdbRepository, CodeReviewEvaluator codeReviewEvaluator) {
        this.dashboardRepository = dashboardRepository;
        this.cmdbRepository = cmdbRepository;
        this.codeReviewEvaluator = codeReviewEvaluator;
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
     * @throws HygieiaException
     */
    @Override
    public DashboardReviewResponse getDashboardReviewResponse(String dashboardTitle, String dashboardType, String businessService, String businessApp, long beginDate, long endDate, Set<AuditType> auditTypes) throws HygieiaException {
        Dashboard dashboard = getDashboard(dashboardTitle, dashboardType, businessService, businessApp);

        DashboardReviewResponse dashboardReviewResponse = new DashboardReviewResponse();
        if (dashboard == null) {
            dashboardReviewResponse.addAuditStatus(AuditStatus.DASHBOARD_NOT_REGISTERED);
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
                    break;

                case ALL:
                    break;

                case CODE_REVIEW:
                    try {
                        Collection<CodeReviewAuditResponseV2> codeReviewResponses = codeReviewEvaluator.evaluate(dashboard, beginDate, endDate, null);
                        dashboardReviewResponse.addAuditStatus(AuditStatus.DASHBOARD_REPO_CONFIGURED);
                        dashboardReviewResponse.setCodeReview(codeReviewResponses);
                    } catch (AuditException e) {
                        if (e.getErrorCode() == AuditException.NO_COLLECTOR_ITEM_CONFIGURED) {
                            dashboardReviewResponse.addAuditStatus(AuditStatus.DASHBOARD_REPO_NOT_CONFIGURED);
                        }
                    }
                    break;

                case CODE_QUALITY:
                    break;

                case TEST_RESULT:
                    break;

                case PERF_TEST:
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
        private Dashboard getDashboard (String title, String type, String busServ, String busApp) throws
        HygieiaException {
            if (!StringUtils.isEmpty(title) && !StringUtils.isEmpty(type)) {
                return dashboardRepository.findByTitleAndType(title, type);

            } else if (!StringUtils.isEmpty(busServ) && !StringUtils.isEmpty(busApp)) {
                Cmdb busServItem = cmdbRepository.findByConfigurationItemAndItemType(busServ, "app");
                if (busServItem == null)
                    throw new HygieiaException("Invalid Business Service Name.", HygieiaException.BAD_DATA);
                Cmdb busAppItem = cmdbRepository.findByConfigurationItemAndItemType(busApp, "component");
                if (busAppItem == null)
                    throw new HygieiaException("Invalid Business Application Name.", HygieiaException.BAD_DATA);

                return dashboardRepository.findByConfigurationItemBusServObjectIdAndConfigurationItemBusAppObjectId(busServItem.getId(), busAppItem.getId());
            }
            return null;
        }
    }
