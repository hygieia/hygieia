package com.capitalone.dashboard.collector;

import com.capitalone.dashboard.collector.config.FongoConfig;
import com.capitalone.dashboard.collector.config.TestConfig;
import com.capitalone.dashboard.model.*;
import com.capitalone.dashboard.repository.*;
import com.capitalone.dashboard.response.DashboardReviewResponse;
import com.capitalone.dashboard.service.DashboardAuditService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


import java.io.IOException;
import java.util.*;

import static junit.framework.TestCase.assertNotNull;
import static org.mockito.Mockito.mock;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestConfig.class, FongoConfig.class})
public class AuditStatusCollectorTaskTest {
    private AuditStatusRepository mockstatusRepository;
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
        mockstatusRepository = mock(AuditStatusRepository.class);
        TestUtils.loadDashBoard(dashboardRepository);
        TestUtils.loadCollector(collectorRepository);
        TestUtils.loadComponent(componentRepository);
        TestUtils.loadCollectorItems(collectorItemRepository);
        AuditConfigSettings settings = new AuditConfigSettings();
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

                DashboardReviewResponse dashboardReviewResponse = dashboardAuditService.getDashboardReviewResponse(dashboard.getTitle(), dashboard.getType(), dashboard.getConfigurationItemBusServName(),dashboard.getConfigurationItemBusAppName(), timestamp, currentTimestamp, allAuditTypes);
                AuditResult auditResult = new AuditResult(dashboard.getId(), dashboardReviewResponse,timestamp);
                assert(auditResult.getDashboardTitle().equals("praveenDashboard"));
                assertNotNull(auditResult.getDashboardId());
                assertNotNull(auditResult.getDashboardReviewResponse());
                auditResults.add(auditResult);
                assertNotNull(auditResults);
            } catch (AuditException e) {
                e.getLocalizedMessage();
            }
            if(!auditResults.isEmpty())
             { mockstatusRepository.save(auditResults); }
        });
    }




}
