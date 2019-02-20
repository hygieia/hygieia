package com.capitalone.dashboard.collector;

import com.capitalone.dashboard.collector.config.FongoConfig;
import com.capitalone.dashboard.collector.config.TestConfig;
import com.capitalone.dashboard.model.Dashboard;
import com.capitalone.dashboard.model.AuditResult;
import com.capitalone.dashboard.model.AuditType;
import com.capitalone.dashboard.model.Audit;
import com.capitalone.dashboard.model.AuditStatus;
import com.capitalone.dashboard.model.DataStatus;
import com.capitalone.dashboard.repository.AuditResultRepository;
import com.capitalone.dashboard.repository.DashboardRepository;
import com.capitalone.dashboard.repository.ComponentRepository;
import com.capitalone.dashboard.repository.CollectorRepository;
import com.capitalone.dashboard.repository.CollectorItemRepository;
import org.bson.types.ObjectId;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashMap;
import java.util.Map;
import java.util.HashSet;

import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertTrue;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {FongoConfig.class})
public class AuditCollectorTaskTest {
    @Autowired
    private AuditResultRepository mockstatusRepository;
    @Autowired
    private DashboardRepository dashboardRepository;
    @Autowired
    private ComponentRepository componentRepository;
    @Autowired
    private CollectorRepository collectorRepository;
    @Autowired
    private CollectorItemRepository collectorItemRepository;

    AuditSettings settings = new AuditSettings();
    private static final long BEGIN_DATE = 1537824736000L;
    private static final long END_DATE = 1540503701000L;

    @Before
    public void setup() throws IOException {
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
        Map traceability = new HashMap();
        AuditResult auditResult1 = new AuditResult(ObjectId.get(),"auditTestDashboard" , "CARD",
                "ASVC","BAP" ,"Owner" ,null ,
                AuditType.CODE_QUALITY ,"OK","OK" ,null,null , traceability, 7883L );
        auditResults.add(auditResult1);

        Set<AuditType> allAuditTypes = new HashSet<>();
        allAuditTypes.add(AuditType.CODE_QUALITY);
        recentDashboards.forEach(dashboard -> {
            try {
                Map<AuditType, Audit> auditMap = AuditCollectorUtil.getAudit(dashboard, settings, BEGIN_DATE, END_DATE);
                Map<AuditType,Audit> auditMap1 = new HashMap<>();
                Audit auditCQ = new Audit();
                auditCQ.setType(AuditType.CODE_QUALITY);
                auditCQ.setAuditStatus(AuditStatus.OK);
                auditCQ.setDataStatus(DataStatus.OK);
                auditMap1.put(AuditType.CODE_QUALITY, auditCQ);
                AuditResult auditResult = AuditCollectorUtil.getAuditResults().get(0);
                Mockito.when(AuditCollectorUtil.getAudit(dashboard,settings,BEGIN_DATE,END_DATE)).thenReturn(auditMap1);
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
                assertNotNull(mockstatusRepository);
                Iterable<AuditResult> auditresult2 = mockstatusRepository.findByDashboardTitle("auditTestDashboard");
                auditresult2.forEach(auditResult -> {
                    assertTrue(auditResult.getDashboardTitle().equals("auditTestDashboard"));
                    assertTrue(auditResult.getLineOfBusiness().equals("CARD"));;
                    assertTrue(auditResult.getAuditType().equals(AuditType.CODE_QUALITY));
                    assertTrue(auditResult.getAuditTypeStatus().equals(DataStatus.OK.name()));
                    assertTrue(auditResult.getAuditStatus().equals(AuditStatus.OK.name()));
                });
            }
        });
    }
}