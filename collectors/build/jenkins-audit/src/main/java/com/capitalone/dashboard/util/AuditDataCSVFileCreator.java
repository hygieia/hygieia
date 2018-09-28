package com.capitalone.dashboard.util;

import com.capitalone.dashboard.model.AuditResult;
import com.capitalone.dashboard.model.AuditType;
import com.capitalone.dashboard.model.Cmdb;
import com.capitalone.dashboard.model.Dashboard;
import com.capitalone.dashboard.repository.CmdbRepository;
import com.capitalone.dashboard.repository.DashboardRepository;
import com.capitalone.dashboard.response.AuditReviewResponse;
import com.capitalone.dashboard.response.DashboardReviewResponse;
import com.capitalone.dashboard.service.DashboardAuditService;
import com.capitalone.dashboard.status.DashboardAuditStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

/**
 * <h1>AuditDataCSVFileCreator</h1>
 * This class helps to create csv data from dashboard audit statuses
 * <p>
 * How to use?
 * 1. Add @Component tag
 * 2. Assign values - BEGIN_DATE, END_DATE, CSV_OUTPUT_FILE_PATH
 * 3. Execute Application
 *
 * @since 09/28/2018
 */
@SuppressWarnings("PMD")
public class AuditDataCSVFileCreator {

    // BEGIN_DATE, END_DATE,CSV_OUTPUT_FILE_PATH values finalized by CSV file creator
    private static final long BEGIN_DATE = 0;
    private static final long END_DATE = 0;
    private static final String CSV_OUTPUT_FILE_PATH = "";

    private static final Set<AuditType> AUDIT_TYPE = new HashSet<>();
    private static final String[] CSV_HEADER = {"LOB", "ASV", "BAP",
            "ASV_OWNER", "SERV_OWNER", "AUDIT_TYPE", "COLLECTION_STATUS", "AUDIT_STATUS", "URL", "ERROR"};
    private static final String[] AUDIT_TYPES = {"LIBRARY_POLICY", "CODE_REVIEW", "TEST_RESULT", "STATIC_SECURITY_ANALYSIS",
            "PERF_TEST", "CODE_QUALITY", "BUILD_REVIEW"};
    private static final String[] CODE_REVIEW_FAIL_STATUES = {"PEER_REVIEW_LGTM_ERROR", "PEER_REVIEW_LGTM_SELF_APPROVAL", "PEER_REVIEW_GHR_SELF_APPROVAL",
            "DIRECT_COMMITS_TO_BASE", "MERGECOMMITER_NOT_FOUND", "PULLREQ_NOT_PEER_REVIEWED", "COLLECTOR_ITEM_ERROR",
            "NO_COMMIT_FOR_DATE_RANGE", "COMMIT_AFTER_PR_MERGE", "SCM_AUTHOR_LOGIN_INVALID", "REPO_NOT_CONFIGURED", "PENDING_DATA_COLLECTION", "GIT_NO_WORKFLOW",
            "COMMITAUTHOR_EQ_MERGECOMMITER", "PEER_REVIEW_BY_SERVICEACCOUNT", "COMMITAUTHOR_EQ_SERVICEACCOUNT", "MERGECOMMITER_EQ_SERVICEACCOUNT"};

    private static String lob, asv, bap, asvOwner, serviceOwner, type, collectionStatus, auditStatus, error, url;
    private List<String> entireCSVData = new ArrayList<>();
    private final Logger LOGGER = LoggerFactory.getLogger(AuditDataCSVFileCreator.class);

    @Autowired
    private static DashboardRepository dashboardRepository;
    @Autowired
    private static DashboardAuditService dashboardAuditService;
    @Autowired
    private static CmdbRepository cmdbRepository;

    @Autowired
    public AuditDataCSVFileCreator(DashboardRepository dashboardRepository, DashboardAuditService dashboardAuditService, CmdbRepository cmdbRepository) {
        this.dashboardRepository = dashboardRepository;
        this.dashboardAuditService = dashboardAuditService;
        this.cmdbRepository = cmdbRepository;
        createCSVFile();
    }

    private void createCSVFile() {
        // This findByTimestampAfter can be changed based on the need.
        //Iterable<Dashboard> recentDashboards = dashboardRepository.findByTimestampAfter(BEGIN_DATE);
        Iterable<Dashboard> recentDashboards = dashboardRepository.findByTitle("CI304346");
        AUDIT_TYPE.add(AuditType.ALL);
        this.getAuditResults(recentDashboards, BEGIN_DATE);
        if(!(entireCSVData == null && entireCSVData.isEmpty())) {
            this.writeCSV();
        }
    }

    private void writeCSV() {
        try {
            LOGGER.info("Writing CSV Data...");
            Files.write(Paths.get(CSV_OUTPUT_FILE_PATH), String.join("\n", entireCSVData).getBytes());
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            e.printStackTrace();
        }
    }

    private void getAuditResults(Iterable<Dashboard> dashboards, long timestamp) {
        List<AuditResult> auditResults = new ArrayList();
        // CSV Header
        entireCSVData.add(String.join(",", Arrays.asList(CSV_HEADER)));

        dashboards.forEach(dashboard -> {
            try {
                DashboardReviewResponse dashboardReviewResponse = dashboardAuditService.getDashboardReviewResponse(
                        dashboard.getTitle(), dashboard.getType(), dashboard.getConfigurationItemBusServName(),
                        dashboard.getConfigurationItemBusAppName(), BEGIN_DATE, END_DATE, AUDIT_TYPE
                );
                AuditResult auditResult = new AuditResult(dashboard.getId(), dashboardReviewResponse, timestamp);

                auditResults.add(auditResult);
                this.assignAuditDataValues(auditResult);
                this.addCSVRowData(dashboard);

            } catch (Exception e) {
                e.printStackTrace();
                LOGGER.error("AUDIT API ERROR - " + e.getMessage());
                Cmdb cmdb = cmdbRepository.findByConfigurationItem(dashboard.getConfigurationItemBusServName());
                List<String> csvAuditAPIFailedRow = Arrays.asList(cmdb.getOwnerDept(), dashboard.getConfigurationItemBusServName(),
                        dashboard.getConfigurationItemBusServName(), cmdb.getBusinessOwner(), cmdb.getAppServiceOwner(),
                        "", "NO_DATA", "NA", "", "AUDIT API ERROR - " + e.getMessage());
                entireCSVData.add(String.join(",", csvAuditAPIFailedRow));
            }
        });
    }

    private void addCSVRowData(Dashboard dashboard) {
        Cmdb cmdb = cmdbRepository.findByConfigurationItem(dashboard.getConfigurationItemBusServName());
        lob = cmdb.getOwnerDept();
        asv = dashboard.getConfigurationItemBusServName();
        bap = dashboard.getConfigurationItemBusServName();
        asvOwner = cmdb.getBusinessOwner();
        serviceOwner = cmdb.getAppServiceOwner();
        url = "";
        List<String> csvRowData = Arrays.asList(lob, asv, bap, asvOwner, serviceOwner, type, collectionStatus, auditStatus, error, url);
        entireCSVData.add(String.join(",", csvRowData));
    }

    private void assignAuditDataValues(AuditResult auditResult) {

        List<String> auditTypes = Arrays.asList(AUDIT_TYPES);
        Set<DashboardAuditStatus> dashboardAuditStatuses = auditResult.getDashboardReviewResponse().getAuditStatuses();
        Map<AuditType, Collection<AuditReviewResponse>> auditDetailReview = auditResult.getDashboardReviewResponse().getReview();

        for (String auditType : auditTypes) {
            type = auditType;

            switch (type) {
                case "LIBRARY_POLICY": {
                    if (dashboardAuditStatuses.contains(DashboardAuditStatus.DASHBOARD_LIBRARY_POLICY_ANALYSIS_CONFIGURED)) {
                        assignAuditDetailStatusValues(auditDetailReview, AuditType.LIBRARY_POLICY);
                    } else {
                        assignAuditDetailDefaults();
                    }
                    break;
                }
                case "CODE_REVIEW": {
                    if (dashboardAuditStatuses.contains(DashboardAuditStatus.DASHBOARD_REPO_CONFIGURED)) {
                        assignAuditDetailStatusValues(auditDetailReview, AuditType.CODE_REVIEW);
                    } else {
                        assignAuditDetailDefaults();
                    }
                    break;
                }
                case "TEST_RESULT": {
                    if (dashboardAuditStatuses.contains(DashboardAuditStatus.DASHBOARD_TEST_CONFIGURED)) {
                        assignAuditDetailStatusValues(auditDetailReview, AuditType.TEST_RESULT);
                    } else {
                        assignAuditDetailDefaults();
                    }
                    break;
                }
                case "STATIC_SECURITY_ANALYSIS": {
                    if (dashboardAuditStatuses.contains(DashboardAuditStatus.DASHBOARD_STATIC_SECURITY_ANALYSIS_CONFIGURED)) {
                        assignAuditDetailStatusValues(auditDetailReview, AuditType.STATIC_SECURITY_ANALYSIS);
                    } else {
                        assignAuditDetailDefaults();
                    }
                    break;
                }
                case "PERF_TEST": {
                    if (dashboardAuditStatuses.contains(DashboardAuditStatus.DASHBOARD_PERFORMANCE_TEST_CONFIGURED)) {
                        assignAuditDetailStatusValues(auditDetailReview, AuditType.PERF_TEST);
                    } else {
                        assignAuditDetailDefaults();
                    }
                    break;
                }
                case "CODE_QUALITY": {
                    if (dashboardAuditStatuses.contains(DashboardAuditStatus.DASHBOARD_CODEQUALITY_CONFIGURED)) {
                        assignAuditDetailStatusValues(auditDetailReview, AuditType.CODE_QUALITY);
                    } else {
                        assignAuditDetailDefaults();
                    }
                    break;
                }
                case "BUILD_REVIEW": {
                    if (dashboardAuditStatuses.contains(DashboardAuditStatus.DASHBOARD_BUILD_CONFIGURED)) {
                        assignAuditDetailStatusValues(auditDetailReview, AuditType.BUILD_REVIEW);
                    } else {
                        assignAuditDetailDefaults();
                    }
                    break;
                }
                default: {
                    assignAuditDetailDefaults();
                    break;
                }
            }
        }
    }

    private void assignAuditDetailDefaults() {
        collectionStatus = "NOT_CONFIGURED";
        auditStatus = "NA";
        error = "";
    }

    private String[] assignAuditDetailStatusValues(Map<AuditType, Collection<AuditReviewResponse>> statusReviewes, AuditType auditType) {

        // collection statuses expected are NO_DATA, NOT_CONFIGURED, OK 
        // Audit statuses expected are OK, FAIL, NA 

        String[] statuses = new String[3];
        Collection<AuditReviewResponse> auditReviewResponses = statusReviewes.get(auditType);
        boolean isAssigned = false;

        List<String> codeReviewFailStatuses = Arrays.asList(CODE_REVIEW_FAIL_STATUES);
        if (auditReviewResponses == null || auditReviewResponses.isEmpty()) {
            collectionStatus = "NO_DATA";
            auditStatus = "NA";
            error = "NO AUDIT STATUS RECEIVED - NULL";
            isAssigned = true;
        } else {
            for (AuditReviewResponse auditReviewResponse : auditReviewResponses) {
                for (Object dashboardAuditStatus : auditReviewResponse.getAuditStatuses()) {

                    if (dashboardAuditStatus.toString().equalsIgnoreCase("COLLECTOR_ITEM_ERROR")) {
                        collectionStatus = "NO_DATA";
                        auditStatus = "NA";
                        error = dashboardAuditStatus.toString();
                        isAssigned = true;
                        break;
                    }

                    if (auditType.equals(AuditType.CODE_REVIEW) && codeReviewFailStatuses.contains(dashboardAuditStatus.toString())) {
                        collectionStatus = "OK";
                        auditStatus = "FAIL";
                        error = dashboardAuditStatus.toString();
                        isAssigned = true;
                        break;
                    }
                    if (dashboardAuditStatus.toString().contains("MISSING")) {
                        collectionStatus = "NO_DATA";
                        auditStatus = "NA";
                        error = dashboardAuditStatus.toString();
                        isAssigned = true;
                        break;
                    } else if (dashboardAuditStatus.toString().contains("FAIL") || dashboardAuditStatus.toString().contains("ERROR")
                            || dashboardAuditStatus.toString().contains("NOT_FOUND") || dashboardAuditStatus.toString().contains("INVALID") ||
                            dashboardAuditStatus.toString().contains("UNAUTHORIZED")) {
                        if (!(dashboardAuditStatus.toString().contains("ERROR_RATE_MET") || dashboardAuditStatus.toString().contains("ERROR_RATE_FOUND"))) {
                            collectionStatus = "OK";
                            auditStatus = "FAIL";
                            error = dashboardAuditStatus.toString();
                            isAssigned = true;
                        }

                    }
                }
                if (isAssigned) {
                    break;
                }
            }
        }

        if (!isAssigned) {
            collectionStatus = "OK";
            auditStatus = "OK";
            error = "";
        }
        return statuses;
    }
}
