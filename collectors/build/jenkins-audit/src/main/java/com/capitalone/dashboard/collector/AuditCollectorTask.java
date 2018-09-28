package com.capitalone.dashboard.collector;

import com.capitalone.dashboard.repository.*;
import com.capitalone.dashboard.model.*;
import java.util.*;
import com.capitalone.dashboard.service.DashboardAuditService;

import com.capitalone.dashboard.model.AuditType;
import com.capitalone.dashboard.response.DashboardReviewResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;

/**
 * <h1>AuditCollectorTask</h1>
 * This task finds recent dashboards, collect and store audit statuses
 *
 * @since 09/28/2018
 */
@SuppressWarnings("PMD")
@Component
public class AuditCollectorTask extends CollectorTask<AuditCollector> {

    private final Logger LOGGER = LoggerFactory.getLogger(AuditCollectorTask.class);

    @Autowired
    private DashboardRepository dashboardRepository;
    @Autowired
    private DashboardAuditService dashboardAuditService;
    @Autowired
    private AuditResultRepository auditResultRepository;
    @Autowired
    private AuditCollectorRepository auditCollectorRepository;
    @Autowired
    private AuditCollectorSettings auditCollectorSettings;

    @Autowired
    public AuditCollectorTask(TaskScheduler taskScheduler, DashboardRepository dashboardRepository, DashboardAuditService
            dashboardAuditService, AuditResultRepository auditResultRepository, AuditCollectorRepository auditCollectorRepository,
                              AuditCollectorSettings auditCollectorSettings) {
        super(taskScheduler, "AuditCollector");
        this.dashboardRepository = dashboardRepository;
        this.dashboardAuditService = dashboardAuditService;
        this.auditResultRepository = auditResultRepository;
        this.auditCollectorRepository = auditCollectorRepository;
        this.auditCollectorSettings = auditCollectorSettings;
    }

    @Override
    public void collect(AuditCollector collector) {
        long lastExecutedCollectorTimestamp = collector.getLastExecuted();

        Iterable<Dashboard> recentDashboards = dashboardRepository.findByTimestampAfter(lastExecutedCollectorTimestamp);
        LOGGER.info("Get dashboards created after " + lastExecutedCollectorTimestamp);

        List<AuditResult> auditResults = getAuditResults(recentDashboards, lastExecutedCollectorTimestamp);
        if (!auditResults.isEmpty()) {
            try {
                auditResultRepository.save(auditResults);
            }catch(Exception e){
                LOGGER.error("Error while saving audit status data to database", e.getMessage());
            }
        }
    }

    /**
     * Get audit statuses for the dashboards
     * @param dashboards
     * @param lastExecutedCollectorTimestamp
     */
    private List<AuditResult> getAuditResults(Iterable<Dashboard> dashboards, long lastExecutedCollectorTimestamp) {
        List<AuditResult> auditResults = new ArrayList();
        Set<AuditType> allAuditTypes = new HashSet<>();
        allAuditTypes.add(AuditType.ALL);

        dashboards.forEach(dashboard -> {
            try {
                long currentTimestamp = System.currentTimeMillis();
                LOGGER.info("Get dashboard audit review response for the dashboard - " + dashboard.getTitle());

                DashboardReviewResponse dashboardReviewResponse = dashboardAuditService.getDashboardReviewResponse(
                        dashboard.getTitle(), dashboard.getType(), dashboard.getConfigurationItemBusServName(),
                        dashboard.getConfigurationItemBusAppName(), lastExecutedCollectorTimestamp, currentTimestamp, allAuditTypes
                );
                AuditResult auditResult = new AuditResult(dashboard.getId(), dashboardReviewResponse, lastExecutedCollectorTimestamp);
                auditResults.add(auditResult);

            } catch (Exception e) {
                LOGGER.error("Error while calling audit api service for the dashboard - " + dashboard.getTitle());
            }
        });
        return auditResults;
    }

    @Override
    public AuditCollector getCollector() {
        return AuditCollector.prototype(this.auditCollectorSettings.getServers());
    }

    @Override
    public BaseCollectorRepository<AuditCollector> getCollectorRepository() {
        return auditCollectorRepository;
    }

    /**
     * This property helps to determine AuditStatus Collector execution interval
     */
    @Override
    public String getCron() {
         return this.auditCollectorSettings.getCron();
    }
}
