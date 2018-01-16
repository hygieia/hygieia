package com.capitalone.dashboard.service;

import com.capitalone.dashboard.evaluator.Evaluator;
import com.capitalone.dashboard.model.AuditException;
import com.capitalone.dashboard.model.AuditType;
import com.capitalone.dashboard.model.Cmdb;
import com.capitalone.dashboard.model.Dashboard;
import com.capitalone.dashboard.model.DashboardAuditModel;
import com.capitalone.dashboard.repository.CmdbRepository;
import com.capitalone.dashboard.repository.DashboardRepository;
import com.capitalone.dashboard.response.AuditReviewResponse;
import com.capitalone.dashboard.response.DashboardReviewResponse;
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
    private final DashboardAuditModel auditModel;


//    private static final Log LOGGER = LogFactory.getLog(DashboardAuditServiceImpl.class);

    @Autowired
    public DashboardAuditServiceImpl(DashboardRepository dashboardRepository, CmdbRepository cmdbRepository, DashboardAuditModel auditModel) {

        this.dashboardRepository = dashboardRepository;
        this.cmdbRepository = cmdbRepository;
        this.auditModel = auditModel;
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
    @SuppressWarnings("PMD.NPathComplexity")
    @Override
    public DashboardReviewResponse getDashboardReviewResponse(String dashboardTitle, String dashboardType, String businessService, String businessApp, long beginDate, long endDate, Set<AuditType> auditTypes) throws AuditException {

        DashboardReviewResponse dashboardReviewResponse = new DashboardReviewResponse();

        Dashboard dashboard = getDashboard(dashboardTitle, dashboardType, businessService, businessApp);
        if (dashboard == null) {
            dashboardReviewResponse.addAuditStatus(DashboardAuditStatus.DASHBOARD_NOT_REGISTERED);
            return dashboardReviewResponse;
        }

        dashboardReviewResponse.setDashboardTitle(dashboard.getTitle());
        dashboardReviewResponse.setBusinessApplication(StringUtils.isEmpty(businessApp) ? "unknown" : businessApp);
        dashboardReviewResponse.setBusinessService(StringUtils.isEmpty(businessService) ? "unknown" : businessService);


        if (auditTypes.contains(AuditType.ALL)) {
            auditTypes.addAll(Sets.newHashSet(AuditType.values()));
            auditTypes.remove(AuditType.ALL);
        }

        auditTypes.forEach(auditType -> {
            Evaluator evaluator = auditModel.evaluatorMap().get(auditType);
            try {
                Collection<AuditReviewResponse> auditResponse = evaluator.evaluate(dashboard, beginDate, endDate, null);
                dashboardReviewResponse.addReview(auditType, auditResponse);
                dashboardReviewResponse.addAuditStatus(auditModel.successStatusMap().get(auditType));
            } catch (AuditException e) {
                if (e.getErrorCode() == AuditException.NO_COLLECTOR_ITEM_CONFIGURED) {
                    dashboardReviewResponse.addAuditStatus(auditModel.errorStatusMap().get(auditType));
                }
            }
        });
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
     * @throws AuditException
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
