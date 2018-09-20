package com.capitalone.dashboard.collector;

import com.capitalone.dashboard.repository.AuditStatusCollectorRepository;
import com.capitalone.dashboard.repository.AuditStatusRepository;
import com.capitalone.dashboard.model.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

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

import java.util.Set;

@Component
public class AuditStatusCollectorTask extends CollectorTask<AuditStatusCollector>{
//public class AuditStatusCollectorTask {

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
    public AuditStatusCollectorTask(TaskScheduler taskScheduler, DashboardRepository dashboardRepository, DashboardAuditService
            dashboardAuditService, AuditStatusRepository auditStatusRepository, AuditStatusCollectorRepository auditStatusCollectorRepository){
    //    public AuditStatusCollectorTask(DashboardRepository dashboardRepository, DashboardAuditService dashboardAuditService, AuditStatusRepository auditStatusRepository, AuditStatusCollectorRepository auditStatusCollectorRepository){
       super(taskScheduler, "JenkinsAuditCollector");
        this.dashboardRepository = dashboardRepository;
        this.dashboardAuditService = dashboardAuditService;
        this.auditStatusRepository = auditStatusRepository;
        this.auditStatusCollectorRepository = auditStatusCollectorRepository;
        //collect(null);
    }

    @Override
    public void collect(AuditStatusCollector collector) {

        long timestamp = collector.getLastExecuted();
        Set<AuditType> auditTypes = new HashSet<>();
        auditTypes.add(AuditType.ALL);
        Iterable<Dashboard> newDashboards = dashboardRepository.findByTimestampAfter(timestamp);

        List<AuditResult> auditResults = new ArrayList<>();
        newDashboards.forEach(dashboard -> {
            try {
                DashboardReviewResponse dashboardReviewResponse = dashboardAuditService.getDashboardReviewResponse(
                        dashboard.getTitle(),
                        dashboard.getType(),
                        "",
                        "",
                        timestamp,
                        System.currentTimeMillis(),
                        auditTypes
                );
                AuditResult auditResult = new AuditResult(dashboard.getId(), dashboard.getTitle(),
                        dashboardReviewResponse.getAuditStatuses().iterator().next().toString());
               auditResults.add(auditResult);

            } catch (AuditException e) {
                LOGGER.error(e.getStackTrace().toString());
            }
            if(!auditResults.isEmpty()) { auditStatusRepository.save(auditResults); }
        });
    }

    @Override
    public AuditStatusCollector getCollector() {
        List<String> servers = new ArrayList<>();
        servers.add("http://localhost:8081/");
        return AuditStatusCollector.prototype(servers);
    }

    @Override
    public BaseCollectorRepository<AuditStatusCollector> getCollectorRepository() {
        return auditStatusCollectorRepository;
    }

    @Override
    public String getCron() {
        return "0 0/2 * * * *";
    }
}
