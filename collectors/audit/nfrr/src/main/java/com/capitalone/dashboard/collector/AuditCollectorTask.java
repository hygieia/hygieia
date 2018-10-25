package com.capitalone.dashboard.collector;
import com.capitalone.dashboard.model.Dashboard;
import com.capitalone.dashboard.model.Audit;
import com.capitalone.dashboard.model.AuditResult;
import com.capitalone.dashboard.model.AuditType;
import com.capitalone.dashboard.model.DashboardType;

import com.capitalone.dashboard.repository.DashboardRepository;
import com.capitalone.dashboard.repository.AuditResultRepository;
import com.capitalone.dashboard.repository.AuditCollectorRepository;
import com.capitalone.dashboard.repository.CmdbRepository;
import com.capitalone.dashboard.repository.BaseCollectorRepository;
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

    @Autowired
    private DashboardRepository dashboardRepository;
    @Autowired
    private AuditResultRepository auditResultRepository;
    @Autowired
    private AuditCollectorRepository auditCollectorRepository;
    @Autowired
    private AuditSettings settings;
    @Autowired
    private CmdbRepository cmdbRepository;

    @Autowired
    public AuditCollectorTask(TaskScheduler taskScheduler, DashboardRepository dashboardRepository,
                              AuditResultRepository auditResultRepository, AuditCollectorRepository auditCollectorRepository,
                              AuditSettings settings) {
        super(taskScheduler, "AuditCollector");
        this.dashboardRepository = dashboardRepository;
        this.auditResultRepository = auditResultRepository;
        this.auditCollectorRepository = auditCollectorRepository;
        this.settings = settings;
    }

    public AuditCollectorTask (TaskScheduler taskScheduler) {
        super(taskScheduler, "AuditCollector");
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

        dashboards.forEach((Dashboard dashboard) -> {
                Map<AuditType, Audit> auditMap = AuditCollectorUtil.getAudit(dashboard, settings,
                        auditBeginDateTimeStamp, auditEndDateTimeStamp);

                LOGGER.info("NFRR Audit Collector adding audit results for the dashboard : " + dashboard.getTitle());
                AuditCollectorUtil.addAuditResultByAuditType(dashboard, auditMap, cmdbRepository, auditEndDateTimeStamp);
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
