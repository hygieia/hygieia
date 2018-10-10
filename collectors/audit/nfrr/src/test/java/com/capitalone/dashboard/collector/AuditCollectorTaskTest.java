package com.capitalone.dashboard.collector;

import com.capitalone.dashboard.collector.config.FongoConfig;
import com.capitalone.dashboard.collector.config.TestConfig;
import com.capitalone.dashboard.model.Audit;
import com.capitalone.dashboard.model.AuditResult;
import com.capitalone.dashboard.model.AuditType;
import com.capitalone.dashboard.model.Dashboard;
import com.capitalone.dashboard.repository.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import java.util.Map;

import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertTrue;
import static org.mockito.Mockito.mock;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {FongoConfig.class})
public class AuditCollectorTaskTest {
    private AuditResultRepository mockstatusRepository;
    @Autowired
    private DashboardRepository dashboardRepository;
    @Autowired
    private ComponentRepository componentRepository;
    @Autowired
    private CollectorRepository collectorRepository;
    @Autowired
    private CollectorItemRepository collectorItemRepository;
    @Autowired
    private CmdbRepository cmdbRepository;

    AuditSettings settings = new AuditSettings();
    private static final long BEGIN_DATE = 1537824736000L;
    private static final long END_DATE = Instant.now().toEpochMilli();

    @Before
    public void setup() throws IOException {
        mockstatusRepository = mock(AuditResultRepository.class);
        TestUtils.loadDashBoard(dashboardRepository);
        TestUtils.loadCollector(collectorRepository);
        TestUtils.loadComponent(componentRepository);
        TestUtils.loadCollectorItems(collectorItemRepository);
        settings = new TestConfig().settings();
    }
    @Test
    public void getCollectAuditStatusData(){
        Iterable<Dashboard> recentDashboards = dashboardRepository.findAll();
        List<AuditResult> auditResults = new ArrayList<>();
        Set<AuditType> allAuditTypes = new HashSet<>();
        allAuditTypes.add(AuditType.CODE_QUALITY);
        recentDashboards.forEach(dashboard -> {
            try {
                Map<AuditType, Audit> auditMap = AuditCollectorUtil.getAudit(dashboard, settings, BEGIN_DATE, END_DATE);
                AuditCollectorUtil.addAuditResultByAuditType(dashboard, auditMap, cmdbRepository, END_DATE);
                AuditCollectorUtil.getAuditResults();
                AuditResult auditResult = AuditCollectorUtil.getAuditResults().get(0);
                assertNotNull(auditResult.getDashboardId());
                assertNotNull(auditResult.getDashboardTitle());
                assertNotNull(auditResult.getTimestamp());
                assertTrue(auditResult.getDashboardId().equals(dashboard.getId()));
                assertTrue(auditResult.getDashboardTitle().equalsIgnoreCase(dashboard.getTitle()));
                assertTrue(auditResult.getTimestamp() == END_DATE);
                auditResults.add(auditResult);
                assertNotNull(auditResults);
            } catch (Exception e) {
                e.getLocalizedMessage();
            }
            if (!auditResults.isEmpty()) {
                mockstatusRepository.save(auditResults);
            }
        });
    }
}
