package com.capitalone.dashboard.collector;
import com.capitalone.dashboard.model.Audit;
import com.capitalone.dashboard.model.AuditResult;
import com.capitalone.dashboard.model.AuditType;
import com.capitalone.dashboard.model.Dashboard;
import com.capitalone.dashboard.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;

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

    private static final long BEGIN_DATE = Instant.now().minus(Duration.ofDays(30)).toEpochMilli();
    private static final long END_DATE = Instant.now().toEpochMilli();

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
        long lastExecutedCollectorTimestamp = collector.getLastExecuted();

        Iterable<Dashboard> recentDashboards = dashboardRepository.findByTitle("Purple Rain Test");
        //Iterable<Dashboard> recentDashboards = dashboardRepository.findAll(new Sort(Sort.Direction.ASC, "title"));
        LOGGER.info("Get dashboards created after " + lastExecutedCollectorTimestamp);

        List<AuditResult> auditResults = getAuditResults(recentDashboards);
        if (!auditResults.isEmpty()) {
            try {
                auditResultRepository.save(auditResults);
            } catch (Exception e) {
                LOGGER.error("Error while saving audit status data to database", e.getMessage());
                throw new RuntimeException(e.getCause());
            }
        }
    }

    /**
     * Get audit statuses for the dashboards
     *
     * @param dashboards
     */
    private List<AuditResult> getAuditResults(Iterable<Dashboard> dashboards) throws HttpClientErrorException{
        dashboards.forEach((Dashboard dashboard) -> {
            try {
                Map<AuditType, Audit> auditMap = AuditCollectorUtil.getAudit(dashboard, settings, BEGIN_DATE, END_DATE);
                AuditCollectorUtil.addAuditResultByAuditType(dashboard, auditMap, cmdbRepository, END_DATE);
            }
            catch(HttpClientErrorException hce){
                LOGGER.error("Http Error while calling audit api service for the dashboard - " + dashboard.getTitle());
                throw hce;
            }
            catch (Exception e) {
                LOGGER.error("Error while calling audit api service for the dashboard - " + dashboard.getTitle());
                throw new RuntimeException(e.getCause());
            }
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
