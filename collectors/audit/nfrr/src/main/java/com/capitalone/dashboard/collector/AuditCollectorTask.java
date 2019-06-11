package com.capitalone.dashboard.collector;
import com.capitalone.dashboard.model.Dashboard;
import com.capitalone.dashboard.model.Audit;
import com.capitalone.dashboard.model.AuditResult;
import com.capitalone.dashboard.model.AuditType;
import com.capitalone.dashboard.model.DashboardType;
import com.capitalone.dashboard.model.Cmdb;

import com.capitalone.dashboard.repository.DashboardRepository;
import com.capitalone.dashboard.repository.AuditResultRepository;
import com.capitalone.dashboard.repository.AuditCollectorRepository;
import com.capitalone.dashboard.repository.CmdbRepository;
import com.capitalone.dashboard.repository.BaseCollectorRepository;
import com.capitalone.dashboard.repository.ComponentRepository;
import com.capitalone.dashboard.repository.CollectorItemRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;


import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;

/**
 * <h1>AuditCollectorTask</h1>
 * This task finds recent dashboards, collect and store audit statuses
 *
 * @since 09/28/2018
 */
@Component
public class AuditCollectorTask extends CollectorTask<AuditCollector> {

    private final Logger LOGGER = LoggerFactory.getLogger(AuditCollectorTask.class);
    private DashboardRepository dashboardRepository;
    private AuditResultRepository auditResultRepository;
    private AuditCollectorRepository auditCollectorRepository;
    private AuditSettings settings;
    private CmdbRepository cmdbRepository;
    private ComponentRepository componentRepository;
    private CollectorItemRepository collectorItemRepository;
    private static final String COLLECTOR_NAME = "AuditCollector";

    @Autowired
    public AuditCollectorTask(TaskScheduler taskScheduler, DashboardRepository dashboardRepository,
                              AuditResultRepository auditResultRepository, AuditCollectorRepository auditCollectorRepository,
                              CmdbRepository cmdbRepository, ComponentRepository componentRepository,
                              CollectorItemRepository collectorItemRepository, AuditSettings settings) {
        super(taskScheduler, COLLECTOR_NAME);
        this.dashboardRepository = dashboardRepository;
        this.auditResultRepository = auditResultRepository;
        this.auditCollectorRepository = auditCollectorRepository;
        this.cmdbRepository = cmdbRepository;
        this.componentRepository = componentRepository;
        this.collectorItemRepository = collectorItemRepository;
        this.settings = settings;
    }

    @Override
    public void collect(AuditCollector collector) {
        LOGGER.info("NFRR Audit Collector pulls all the team dashboards");
        Iterable<Dashboard> dashboards = dashboardRepository.findAllByType(DashboardType.Team);

        List<AuditResult> auditResults = getAuditResults(dashboards);
        if (auditResults.isEmpty()){
            return;
        }
        AuditCollectorUtil.clearAuditResultRepo(auditResultRepository);
        auditResultRepository.save(auditResults);
        AuditCollectorUtil.clearAuditResults();
        LOGGER.info("NFRR Audit Collector executed successfully");
        }

    /**
     * Get audit statuses for the dashboards
     *
     * @param dashboards
     */
    protected List<AuditResult> getAuditResults(Iterable<Dashboard> dashboards) {
        int numberOfAuditDays = settings.getDays();
        long auditBeginDateTimeStamp = Instant.now().minus(Duration.ofDays(numberOfAuditDays)).toEpochMilli();
        long auditEndDateTimeStamp = Instant.now().toEpochMilli();
        LOGGER.info("NFRR Audit Collector audits with begin,end timestamps as " + auditBeginDateTimeStamp + "," + auditEndDateTimeStamp);

        AuditCollector collector = getCollectorRepository().findByName(COLLECTOR_NAME);
        AuditCollectorUtil auditCollectorUtil = new AuditCollectorUtil(collector, componentRepository, collectorItemRepository);
        dashboards.forEach((Dashboard dashboard) -> {
                Map<AuditType, Audit> auditMap = auditCollectorUtil.getAudit(dashboard, settings,
                        auditBeginDateTimeStamp, auditEndDateTimeStamp);

                LOGGER.info("NFRR Audit Collector adding audit results for the dashboard : " + dashboard.getTitle());
                Cmdb cmdb = cmdbRepository.findByConfigurationItem(dashboard.getConfigurationItemBusServName());
                AuditCollectorUtil.addAuditResultByAuditType(dashboard, auditMap, cmdb, auditEndDateTimeStamp);
        });
        return AuditCollectorUtil.getAuditResults();
    }

    @Override
    public AuditCollector getCollector() {
        return AuditCollector.prototype(this.settings.getServers());
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
        return this.settings.getCron();
    }

}
