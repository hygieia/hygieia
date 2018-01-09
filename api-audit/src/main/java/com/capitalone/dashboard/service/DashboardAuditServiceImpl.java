package com.capitalone.dashboard.service;

import com.capitalone.dashboard.evaluator.BuildEvaluator;
import com.capitalone.dashboard.evaluator.CodeQualityEvaluator;
import com.capitalone.dashboard.evaluator.PeerReviewEvaluator;
import com.capitalone.dashboard.misc.HygieiaException;
import com.capitalone.dashboard.model.AuditStatus;
import com.capitalone.dashboard.model.Cmdb;
import com.capitalone.dashboard.model.CollectorItem;
import com.capitalone.dashboard.model.CollectorType;
import com.capitalone.dashboard.model.Dashboard;
import com.capitalone.dashboard.model.GitRequest;
import com.capitalone.dashboard.model.Widget;
import com.capitalone.dashboard.repository.CmdbRepository;
import com.capitalone.dashboard.repository.ComponentRepository;
import com.capitalone.dashboard.repository.DashboardRepository;
import com.capitalone.dashboard.response.DashboardReviewResponse;
import com.capitalone.dashboard.response.GenericAuditResponse;
import com.capitalone.dashboard.response.JobReviewResponse;
import com.capitalone.dashboard.response.PeerReviewResponse;
import com.capitalone.dashboard.response.StaticAnalysisResponse;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

import static com.capitalone.dashboard.response.GenericAuditResponse.CODE_REVIEW;
import static com.capitalone.dashboard.response.GenericAuditResponse.JOB_REVIEW;
import static com.capitalone.dashboard.response.GenericAuditResponse.STATIC_CODE_REVIEW;

@Component
public class DashboardAuditServiceImpl implements DashboardAuditService {


    private final DashboardRepository dashboardRepository;
    private final CmdbRepository cmdbRepository;
    private final ComponentRepository componentRepository;


    private final PeerReviewEvaluator peerReviewEvaluator;
    private final CodeQualityEvaluator codeQualityEvaluator;
    private final BuildEvaluator buildEvaluator;


    private static final Log LOGGER = LogFactory.getLog(DashboardAuditServiceImpl.class);

    @Autowired
    public DashboardAuditServiceImpl(DashboardRepository dashboardRepository, CmdbRepository cmdbRepository, ComponentRepository componentRepository, PeerReviewEvaluator peerReviewEvaluator, CodeQualityEvaluator codeQualityEvaluator, BuildEvaluator buildEvaluator) {
        this.dashboardRepository = dashboardRepository;
        this.cmdbRepository = cmdbRepository;
        this.componentRepository = componentRepository;
        this.peerReviewEvaluator = peerReviewEvaluator;

        this.codeQualityEvaluator = codeQualityEvaluator;
        this.buildEvaluator = buildEvaluator;
    }

    /**
     * Calculates audit response for a given dashboard
     *
     * @param title
     * @param type
     * @param busServ
     * @param busApp
     * @param beginDate
     * @param endDate
     * @return @DashboardReviewResponse for a given dashboard
     * @throws HygieiaException
     */
    public DashboardReviewResponse getDashboardReviewResponse(String title, String type, String busServ, String
            busApp, long beginDate, long endDate) throws HygieiaException {
        Dashboard dashboard = getDashboard(title, type, busServ, busApp);

        DashboardReviewResponse dashboardReviewResponse = new DashboardReviewResponse();
        if (dashboard == null) {
            dashboardReviewResponse.addAuditStatus(AuditStatus.DASHBOARD_NOT_REGISTERED);
            return dashboardReviewResponse;
        }

        dashboardReviewResponse.setDashboardTitle(dashboard.getTitle());

        //Code Review Audit

        GenericAuditResponse codeReviewResponse = peerReviewEvaluator.evaluate(dashboard, beginDate, endDate, null);
        dashboardReviewResponse.addAllAuditStatus(codeReviewResponse.getAuditStatuses());
        List<List<PeerReviewResponse>> peerReviewsAudit = (List<List<PeerReviewResponse>>) codeReviewResponse.getResponse(CODE_REVIEW);
        dashboardReviewResponse.setAllPeerReviewResponses(peerReviewsAudit);


        //Get the pull requests list back
        List<GitRequest> pullRequests = peerReviewsAudit.stream().flatMap(List::stream).map(PeerReviewResponse::getPullRequest).collect(Collectors.toList());

        //Build Audit
        GenericAuditResponse buildGenericAuditResponse = buildEvaluator.getBuildJobAuditResponse(dashboard, beginDate, endDate, pullRequests);
        dashboardReviewResponse.addAllAuditStatus(buildGenericAuditResponse.getAuditStatuses());
        dashboardReviewResponse.setJobReviewResponse((JobReviewResponse) buildGenericAuditResponse.getResponse(JOB_REVIEW));

        //Code Quality Audit
        GenericAuditResponse codeQualityGenericAuditResponse = codeQualityEvaluator.evaluate(dashboard, 0, 0, null);
        dashboardReviewResponse.addAllAuditStatus(codeQualityGenericAuditResponse.getAuditStatuses());
        dashboardReviewResponse.setStaticAnalysisResponse((StaticAnalysisResponse) codeQualityGenericAuditResponse.getResponse(STATIC_CODE_REVIEW));

//        List<CollectorItem> testItems = this.getCollectorItems(dashboard, "test", CollectorType.Test);
//
//        if (testItems != null && !testItems.isEmpty()) {
//            dashboardReviewResponse.addAuditStatus(AuditStatus.DASHBOARD_TEST_CONFIGURED);
//            testItems.stream().map(testItem -> getTestResults((String) testItem.getOptions().get("jobUrl"), beginDate, endDate)).map(this::regressionTestResultAudit).forEach(dashboardReviewResponse::setTestResultsResponse);
//
//        } else {
//            dashboardReviewResponse.addAuditStatus(AuditStatus.DASHBOARD_TEST_NOT_CONFIGURED);
//        }
        return dashboardReviewResponse;
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
    private Dashboard getDashboard(String title, String type, String busServ, String busApp) throws HygieiaException {
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
