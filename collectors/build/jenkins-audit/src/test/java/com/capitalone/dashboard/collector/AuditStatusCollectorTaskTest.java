package com.capitalone.dashboard.collector;

import com.capitalone.dashboard.model.CollectorType;
import com.capitalone.dashboard.repository.AuditStatusRepository;
import com.capitalone.dashboard.repository.DashboardRepository;
import com.capitalone.dashboard.service.DashboardAuditService;
import org.assertj.core.api.AssertionsForInterfaceTypes;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.springframework.scheduling.TaskScheduler;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.mock;

public class AuditStatusCollectorTaskTest {
    private AuditStatusCollectorTask testee;
    private TaskScheduler mockScheduler;
    private AuditStatusRepository statusRepository;
    private DashboardAuditService mockDataService;
    private DashboardRepository mockDashboardRepository;

    @Before
    public void setup(){
        mockScheduler = mock(TaskScheduler.class);
        statusRepository = mock(AuditStatusRepository.class);
        mockDataService = mock(DashboardAuditService.class);
        mockDashboardRepository = mock(DashboardRepository.class);
        this.testee = new AuditStatusCollectorTask(mockScheduler, mockDashboardRepository,mockDataService);
    }

    @Test
    public void getCollectorReturnsAuditStatusCollector(){
        final AuditStatusCollector collector = testee.getCollector();
        assertThat(collector).isNotNull().isInstanceOf(AuditStatusCollector.class);
        assertThat(collector.isEnabled()).isTrue();
        assertThat(collector.isOnline()).isTrue();
        AssertionsForInterfaceTypes.assertThat(collector.getBuildServers()).contains("server1", "server2");
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
        assertThat(testee.getCollectorRepository()).isNotNull().isSameAs(statusRepository);
    }




}
