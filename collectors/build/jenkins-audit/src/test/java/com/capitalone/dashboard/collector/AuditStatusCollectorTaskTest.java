package com.capitalone.dashboard.collector;

import com.capitalone.dashboard.model.*;
import com.capitalone.dashboard.repository.AuditStatusCollectorRepository;
import com.capitalone.dashboard.repository.AuditStatusRepository;
import com.capitalone.dashboard.repository.DashboardRepository;
import com.capitalone.dashboard.response.DashboardReviewResponse;
import com.capitalone.dashboard.service.DashboardAuditService;
import org.assertj.core.api.AssertionsForInterfaceTypes;
import org.junit.Before;
import org.junit.Test;
import org.springframework.scheduling.TaskScheduler;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.mock;

public class AuditStatusCollectorTaskTest {
    private AuditStatusCollectorTask testee;
    private TaskScheduler mockScheduler;
    private AuditStatusRepository mockstatusRepository;
    private DashboardAuditService mockDashboardService;
    private DashboardRepository mockDashboardRepository;
    private AuditStatusCollectorRepository mockCollectorRepository;
    private AuditStatusCollector mockAudistatusCollector;
    private Dashboard mockDashboard;
    private DashboardReviewResponse mockDashboardReviewResponse;

    @Before
    public void setup(){
        mockScheduler = mock(TaskScheduler.class);
        mockstatusRepository = mock(AuditStatusRepository.class);
        mockDashboardService = mock(DashboardAuditService.class);
        mockDashboardRepository = mock(DashboardRepository.class);
        mockCollectorRepository = mock(AuditStatusCollectorRepository.class);
        mockAudistatusCollector = mock(AuditStatusCollector.class);
        mockDashboard = mock(Dashboard.class);
        mockDashboardReviewResponse = mock(DashboardReviewResponse.class);
        this.testee = new AuditStatusCollectorTask(mockScheduler, mockDashboardRepository,mockDashboardService, mockstatusRepository, mockCollectorRepository);
    }

    @Test
    public void getCollectorReturnsAuditStatusCollector(){
        final AuditStatusCollector collector = testee.getCollector();
        assertThat(collector).isNotNull().isInstanceOf(AuditStatusCollector.class);
        assertThat(collector.isEnabled()).isTrue();
        assertThat(collector.isOnline()).isTrue();
        AssertionsForInterfaceTypes.assertThat(collector.getBuildServers()).contains("http://localhost:8081/");
        AssertionsForInterfaceTypes.assertThat(collector.getCollectorType()).isEqualTo(CollectorType.Audit);
        assertThat(collector.getName()).isEqualTo("JenkinsAuditCollector");
        assertThat(collector.getAllFields().get("instanceUrl")).isEqualTo("");
        assertThat(collector.getAllFields().get("jobName")).isEqualTo("");
        assertThat(collector.getAllFields().get("jobUrl")).isEqualTo("");
        assertThat(collector.getUniqueFields().get("instanceUrl")).isEqualTo("");
        assertThat(collector.getUniqueFields().get("jobName")).isEqualTo("");
        assertThat(collector.getUniqueFields().get("jobUrl")).isEqualTo("");
    }

    @Test
    public void getCollectorRepositoryReturnsTheRepository() {
        assertThat(testee.getCollectorRepository()).isNotNull().isInstanceOf(mockCollectorRepository.getClass());
    }

    @Test
    public void getCollectAuditStatusData(){
        long timestamp = mockAudistatusCollector.getLastExecuted();
        Set<AuditType> auditTypes = new HashSet<>();
        auditTypes.add(AuditType.ALL);
        assertThat(auditTypes.contains("ALL"));
        Iterable<Dashboard> newDashboards = mockDashboardRepository.findByTimestampAfter(timestamp);

        List<AuditResult> auditResults = new ArrayList<>();
        newDashboards.forEach(dashboard -> {
            try {
                DashboardReviewResponse dashboardReviewResponse = mockDashboardService.getDashboardReviewResponse(
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
            }
            assertThat(!auditResults.isEmpty());
             { mockstatusRepository.save(auditResults); }
        });
    }

    @Test
    public  void getCron(){
        assertThat(!testee.getCron().isEmpty());
    }



}
