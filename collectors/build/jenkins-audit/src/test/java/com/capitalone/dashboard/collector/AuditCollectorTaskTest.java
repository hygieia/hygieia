package com.capitalone.dashboard.collector;

import com.capitalone.dashboard.collector.config.FongoConfig;
import com.capitalone.dashboard.collector.config.TestConfig;
import com.capitalone.dashboard.model.AuditException;
import com.capitalone.dashboard.model.AuditResult;
import com.capitalone.dashboard.model.AuditType;
import com.capitalone.dashboard.model.Dashboard;
import com.capitalone.dashboard.repository.AuditResultRepository;
import com.capitalone.dashboard.repository.DashboardRepository;
import com.capitalone.dashboard.repository.ComponentRepository;
import com.capitalone.dashboard.repository.CollectorRepository;
import com.capitalone.dashboard.repository.CollectorItemRepository;
import com.capitalone.dashboard.response.DashboardReviewResponse;
import com.capitalone.dashboard.service.DashboardAuditService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;

import static junit.framework.TestCase.assertNotNull;
import static org.mockito.Mockito.mock;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestConfig.class, FongoConfig.class})
public class AuditCollectorTaskTest {
    private AuditResultRepository mockstatusRepository;
    @Autowired
    private DashboardAuditService dashboardAuditService;
    @Autowired
    private DashboardRepository dashboardRepository;
    @Autowired
    private ComponentRepository componentRepository;
    @Autowired
    private CollectorRepository collectorRepository;
    @Autowired
    private CollectorItemRepository collectorItemRepository;

    @Before
    public void setup() throws IOException {
        mockstatusRepository = mock(AuditResultRepository.class);
        TestUtils.loadDashBoard(dashboardRepository);
        TestUtils.loadCollector(collectorRepository);
        TestUtils.loadComponent(componentRepository);
        TestUtils.loadCollectorItems(collectorItemRepository);
        AuditCollectorSettings settings = new AuditCollectorSettings();
        settings.setServers(Arrays.asList("http://localhost:8081/"));
        settings.setCron("*/2 * * * *");
    }
    @Test
    public void getCollectAuditStatusData() throws IOException {
        long timestamp = 1537824736000L;
        Iterable<Dashboard> recentDashboards = dashboardRepository.findAll();
        List<AuditResult> auditResults = new ArrayList<>();
        Set<AuditType> allAuditTypes = new HashSet<>();
        allAuditTypes.add(AuditType.CODE_QUALITY);
        recentDashboards.forEach(dashboard -> {
            try {
                long currentTimestamp = System.currentTimeMillis();
                DashboardReviewResponse dashboardReviewResponse = dashboardAuditService.getDashboardReviewResponse(dashboard.getTitle(), dashboard.getType(), dashboard.getConfigurationItemBusServName(), dashboard.getConfigurationItemBusAppName(), timestamp, currentTimestamp, allAuditTypes);
                AuditResult auditResult = new AuditResult(dashboard.getId(), dashboard.getTitle(), dashboardReviewResponse, timestamp);
                assert (auditResult.getDashboardTitle().equals("auditTestDashboard"));
                assertNotNull(auditResult.getDashboardId());
                assertNotNull(auditResult.getDashboardReview());
                auditResults.add(auditResult);
                assertNotNull(auditResults);
            } catch (AuditException e) {
                e.getLocalizedMessage();
            }
            if (!auditResults.isEmpty()) {
                mockstatusRepository.save(auditResults);
            }
        });
    }
}
