package com.capitalone.dashboard.collector;

import com.capitalone.dashboard.model.CollectorType;
import com.capitalone.dashboard.repository.AuditStatusCollectorRepository;
import com.capitalone.dashboard.repository.AuditStatusRepository;
import com.capitalone.dashboard.repository.DashboardRepository;
import com.capitalone.dashboard.service.DashboardAuditService;
import org.assertj.core.api.AssertionsForInterfaceTypes;
import org.junit.Before;
import org.junit.Test;
import org.springframework.scheduling.TaskScheduler;

import java.util.Arrays;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.mock;

public class AuditStatusCollectorTest {

    private AuditStatusCollectorTask testee;
    private TaskScheduler taskScheduler;
    private DashboardRepository dashboardRepository;
    private DashboardAuditService dashboardAuditService;
    private  AuditStatusRepository auditStatusRepository;
    private AuditStatusCollectorRepository auditStatusCollectorRepository;


    @Before
    public void setup(){
        taskScheduler = mock(TaskScheduler.class);
        auditStatusRepository = mock(AuditStatusRepository.class);
        auditStatusCollectorRepository = mock(AuditStatusCollectorRepository.class);
        dashboardRepository = mock(DashboardRepository.class);
        dashboardAuditService = mock(DashboardAuditService.class);
        AuditConfigSettings auditConfigSettings = new AuditConfigSettings();
        auditConfigSettings.setServers(Arrays.asList("http://localhost:8081/"));
        auditConfigSettings.setCron("*/2 * * * *");


        this.testee = new AuditStatusCollectorTask(taskScheduler,dashboardRepository, dashboardAuditService, auditStatusRepository, auditStatusCollectorRepository,auditConfigSettings);
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
        assertThat(testee.getCollectorRepository()).isNotNull().isInstanceOf(AuditStatusCollectorRepository.class);
    }

    @Test
    public  void getCron(){
        assertThat(testee.getCron().equals("*/2 * * * *"));
    }
}
