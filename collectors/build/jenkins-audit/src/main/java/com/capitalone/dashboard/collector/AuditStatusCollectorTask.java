package com.capitalone.dashboard.collector;

import com.capitalone.dashboard.model.AuditStatus;
import com.capitalone.dashboard.repository.AuditStatusRepository;
import com.capitalone.dashboard.model.*;
import java.time.Instant;
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

    Logger LOGGER = LoggerFactory.getLogger(AuditStatusCollectorTask.class);

    @Autowired
    private DashboardRepository dashboardRepository;

    @Autowired
    private DashboardAuditService dashboardAuditService;

    private AuditStatusRepository auditStatusRepository;

    @Autowired
    public AuditStatusCollectorTask(TaskScheduler taskScheduler, DashboardRepository dashboardRepository, DashboardAuditService dashboardAuditService){
        super(taskScheduler, "JenkinsAuditCollector");
        this.dashboardRepository = dashboardRepository;
        this.dashboardAuditService = dashboardAuditService;
    }

    @Override
    public void collect(AuditStatusCollector collector) {

        // timestamp and collector job execution interval yet to be finalized
        long timestamp = Instant.now().toEpochMilli();

        Iterable<Dashboard> dashboardList = dashboardRepository.findByTimestampAfter(timestamp);

        //List<Dashboard> dashboardList = dashboardRepository.findByOwners(owner);
        // LocalTime time = LocalTime.now();
        // LocalTime twoDaysBeforeTime = time.minusHours(48);

        Set<AuditType> auditTypes = new HashSet<>();
        auditTypes.add(AuditType.fromString("ALL"));

        List<AuditStatus> auditStatusList = new ArrayList<>();
        dashboardList.forEach(dashboard -> {
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
                AuditStatus auditStatus = new AuditStatus(dashboard.getId(), dashboard.getTitle(),
                        dashboardReviewResponse.getAuditStatuses().iterator().next().toString());
                auditStatusList.add(auditStatus);

            } catch (AuditException e) {
                LOGGER.error(e.getMessage());
                e.printStackTrace();
            }
            auditStatusRepository.save(auditStatusList);
        });
    }

    @Override
    public AuditStatusCollector getCollector() {
        return this.getCollector();
    }

    @Override
    public BaseCollectorRepository<AuditStatusCollector> getCollectorRepository() {
        return this.getCollectorRepository();
    }

    @Override
    public String getCron() {
        return this.getCron();
    }
}
