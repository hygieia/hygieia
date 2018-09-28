package com.capitalone.dashboard.collector;

import com.capitalone.dashboard.model.CollectorType;
import com.capitalone.dashboard.repository.AuditCollectorRepository;
import com.capitalone.dashboard.repository.AuditResultRepository;
import com.capitalone.dashboard.repository.DashboardRepository;
import com.capitalone.dashboard.service.DashboardAuditService;
import org.assertj.core.api.AssertionsForInterfaceTypes;
import org.junit.Before;
import org.junit.Test;
import org.springframework.scheduling.TaskScheduler;

import java.util.Arrays;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.mock;

public class AuditCollectorTest {

    private AuditCollectorTask testee;
    private TaskScheduler taskScheduler;
    private DashboardRepository dashboardRepository;
    private DashboardAuditService dashboardAuditService;
    private AuditResultRepository auditResultRepository;
    private AuditCollectorRepository auditCollectorRepository;


    @Before
    public void setup(){
        taskScheduler = mock(TaskScheduler.class);
        auditResultRepository = mock(AuditResultRepository.class);
        auditCollectorRepository = mock(AuditCollectorRepository.class);
        dashboardRepository = mock(DashboardRepository.class);
        dashboardAuditService = mock(DashboardAuditService.class);
        AuditCollectorSettings auditCollectorSettings = new AuditCollectorSettings();
        auditCollectorSettings.setServers(Arrays.asList("http://localhost:8081/"));
        auditCollectorSettings.setCron("*/2 * * * *");


        this.testee = new AuditCollectorTask(taskScheduler,dashboardRepository, dashboardAuditService, auditResultRepository, auditCollectorRepository, auditCollectorSettings);
    }

    @Test
    public void getCollectorReturnsAuditStatusCollector(){
        final AuditCollector collector = testee.getCollector();
        assertThat(collector).isNotNull().isInstanceOf(AuditCollector.class);
        assertThat(collector.isEnabled()).isTrue();
        assertThat(collector.isOnline()).isTrue();
        AssertionsForInterfaceTypes.assertThat(collector.getBuildServers()).contains("http://localhost:8081/");
        AssertionsForInterfaceTypes.assertThat(collector.getCollectorType()).isEqualTo(CollectorType.Audit);
        assertThat(collector.getName()).isEqualTo("AuditCollector");
        assertThat(collector.getAllFields().get("instanceUrl")).isEqualTo("");
        assertThat(collector.getAllFields().get("jobName")).isEqualTo("");
        assertThat(collector.getAllFields().get("jobUrl")).isEqualTo("");
        assertThat(collector.getUniqueFields().get("instanceUrl")).isEqualTo("");
        assertThat(collector.getUniqueFields().get("jobName")).isEqualTo("");
        assertThat(collector.getUniqueFields().get("jobUrl")).isEqualTo("");
    }

    @Test
    public void getCollectorRepositoryReturnsTheRepository() {
        assertThat(testee.getCollectorRepository()).isNotNull().isInstanceOf(AuditCollectorRepository.class);
    }

    @Test
    public  void getCron(){
        assertThat(testee.getCron().equals("*/2 * * * *"));
    }
}
