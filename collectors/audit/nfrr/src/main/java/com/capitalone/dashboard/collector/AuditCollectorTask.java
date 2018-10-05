package com.capitalone.dashboard.collector;
import com.capitalone.dashboard.model.Audit;
import com.capitalone.dashboard.model.AuditResult;
import com.capitalone.dashboard.model.AuditType;
import com.capitalone.dashboard.model.Dashboard;
import com.capitalone.dashboard.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
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
        LOGGER.info("NFRR Audit Collector pulls all the dashboards");
        Iterable<Dashboard> dashboards = dashboardRepository.findAll(new Sort(Sort.Direction.ASC, "title"));

        List<AuditResult> auditResults = getAuditResults(dashboards);
        if (!auditResults.isEmpty()) {
            try {
                AuditCollectorUtil.clearAuditResultRepo(auditResultRepository);
                auditResultRepository.save(auditResults);
                AuditCollectorUtil.clearAuditResults();
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
    private List<AuditResult> getAuditResults(Iterable<Dashboard> dashboards) {

        long currentTimeStamp = Instant.now().toEpochMilli();
        LOGGER.info("NFRR Audit Collector audits the dashboards");
        dashboards.forEach((Dashboard dashboard) -> {
            try {
                Map<AuditType, Audit> auditMap = AuditCollectorUtil.getAudit(dashboard, settings,
                        Instant.now().minus(Duration.ofDays(30)).toEpochMilli(), currentTimeStamp);

                LOGGER.info("NFRR Audit Collector adding audit results by audit type ");
                AuditCollectorUtil.addAuditResultByAuditType(dashboard, auditMap, cmdbRepository, currentTimeStamp);
            }
            catch(HttpClientErrorException hce){
                LOGGER.error("Http Error while calling audit api service for the dashboard - " + dashboard.getTitle());
            }
            catch (Exception e) {
                LOGGER.error("Error while calling audit api service for the dashboard - " + dashboard.getTitle());
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
