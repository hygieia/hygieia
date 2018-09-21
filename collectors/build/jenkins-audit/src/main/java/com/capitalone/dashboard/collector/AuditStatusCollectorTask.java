package com.capitalone.dashboard.collector;

import com.capitalone.dashboard.repository.AuditStatusCollectorRepository;
import com.capitalone.dashboard.repository.AuditStatusRepository;
import com.capitalone.dashboard.model.*;

import java.util.*;

import com.capitalone.dashboard.repository.BaseCollectorRepository;
import com.capitalone.dashboard.repository.DashboardRepository;
import com.capitalone.dashboard.service.DashboardAuditService;

import com.capitalone.dashboard.model.AuditType;
import com.capitalone.dashboard.response.DashboardReviewResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;

@Component
public class AuditStatusCollectorTask extends CollectorTask<AuditStatusCollector> {

    private final Logger LOGGER = LoggerFactory.getLogger(AuditStatusCollectorTask.class);

    @Autowired
    private DashboardRepository dashboardRepository;
    @Autowired
    private DashboardAuditService dashboardAuditService;
    @Autowired
    private AuditStatusRepository auditStatusRepository;
    @Autowired
    private AuditStatusCollectorRepository auditStatusCollectorRepository;
    @Autowired
    private AuditConfigSettings auditConfigSettings;

    @Autowired
    public AuditStatusCollectorTask(TaskScheduler taskScheduler, DashboardRepository dashboardRepository, DashboardAuditService
            dashboardAuditService, AuditStatusRepository auditStatusRepository, AuditStatusCollectorRepository auditStatusCollectorRepository, AuditConfigSettings auditConfigSettings) {
        super(taskScheduler, "JenkinsAuditCollector");
        this.dashboardRepository = dashboardRepository;
        this.dashboardAuditService = dashboardAuditService;
        this.auditStatusRepository = auditStatusRepository;
        this.auditStatusCollectorRepository = auditStatusCollectorRepository;
        this.auditConfigSettings = auditConfigSettings;
    }

    @Override
    public void collect(AuditStatusCollector collector) {
        long lastExecutedTimestamp = collector.getLastExecuted();
        Iterable<Dashboard> recentDashboards = dashboardRepository.findByTimestampAfter(lastExecutedTimestamp);
        List<AuditResult> auditResults = getAuditResults(recentDashboards, lastExecutedTimestamp);
        if (!auditResults.isEmpty()) {
            auditStatusRepository.save(auditResults);
        }
    }

    private List<AuditResult> getAuditResults(Iterable<Dashboard> dashboards, long timestamp) {
        List<AuditResult> auditResults = new ArrayList();
        Set<AuditType> allAuditTypes = new HashSet<>();
        allAuditTypes.add(AuditType.ALL);
        dashboards.forEach(dashboard -> {
            try {
                long currentTimestamp = System.currentTimeMillis();
                DashboardReviewResponse dashboardReviewResponse = dashboardAuditService.getDashboardReviewResponse(
                        dashboard.getTitle(), dashboard.getType(), dashboard.getConfigurationItemBusServName(),
                        dashboard.getConfigurationItemBusAppName(), timestamp, currentTimestamp, allAuditTypes
                );
                AuditResult auditResult = new AuditResult(dashboard.getId(), dashboardReviewResponse, timestamp);
                auditResults.add(auditResult);

            } catch (AuditException e) {
                LOGGER.error(e.getLocalizedMessage());
            }
        });
        return auditResults;
    }

    @Override
    public AuditStatusCollector getCollector() {
        return AuditStatusCollector.prototype(this.auditConfigSettings.getServers());
    }

    @Override
    public BaseCollectorRepository<AuditStatusCollector> getCollectorRepository() {
        return auditStatusCollectorRepository;
    }

    @Override
    public String getCron() {
        return this.auditConfigSettings.getCron();
    }
}
