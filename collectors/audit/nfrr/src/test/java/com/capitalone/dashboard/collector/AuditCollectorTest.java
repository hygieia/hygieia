package com.capitalone.dashboard.collector;

import com.capitalone.dashboard.collector.config.TestConfig;
import com.capitalone.dashboard.model.CollectorType;
import com.capitalone.dashboard.repository.AuditCollectorRepository;
import com.capitalone.dashboard.repository.AuditResultRepository;
import com.capitalone.dashboard.repository.DashboardRepository;
import org.assertj.core.api.AssertionsForInterfaceTypes;
import org.junit.Before;
import org.junit.Test;
import org.springframework.scheduling.TaskScheduler;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.mock;

public class AuditCollectorTest {

    private AuditCollectorTask taskToTest;
    private TaskScheduler taskScheduler;
    private DashboardRepository dashboardRepository;
    private AuditResultRepository auditResultRepository;
    private AuditCollectorRepository auditCollectorRepository;
    private AuditSettings auditSettings;

    @Before
    public void setup() {
        taskScheduler = mock(TaskScheduler.class);
        auditResultRepository = mock(AuditResultRepository.class);
        auditCollectorRepository = mock(AuditCollectorRepository.class);
        dashboardRepository = mock(DashboardRepository.class);
        auditSettings = new TestConfig().settings();
        this.taskToTest = new AuditCollectorTask(taskScheduler, dashboardRepository, auditResultRepository,
                auditCollectorRepository, auditSettings);
    }

    @Test
    public void getCollectorReturnsAuditStatusCollector() {
        final AuditCollector collector = taskToTest.getCollector();
        assertThat(collector).isNotNull().isInstanceOf(AuditCollector.class);
        assertThat(collector.isEnabled()).isTrue();
        assertThat(collector.isOnline()).isTrue();
        AssertionsForInterfaceTypes.assertThat(collector.getAuditServers()).contains(auditSettings.getServers().get(0));
        AssertionsForInterfaceTypes.assertThat(collector.getCollectorType()).isEqualTo(CollectorType.Audit);
        assertThat(collector.getName()).isEqualTo("AuditCollector");
        assertThat(collector.getCollectorType()).isEqualTo(CollectorType.Audit);
    }

    @Test
    public void getCollectorRepositoryReturnsTheRepository() {
        assertThat(taskToTest.getCollectorRepository()).isNotNull().isInstanceOf(AuditCollectorRepository.class);
    }

    @Test
    public void getCron() {
        assertThat(taskToTest.getCron().equals(auditSettings.getCron()));
    }
}
