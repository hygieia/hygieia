package com.capitalone.dashboard.collector;

import com.capitalone.dashboard.collector.config.FongoConfig;
import com.capitalone.dashboard.collector.config.TestConfig;
import com.capitalone.dashboard.model.Dashboard;
import com.capitalone.dashboard.model.AuditResult;
import com.capitalone.dashboard.model.AuditType;
import com.capitalone.dashboard.model.Audit;
import com.capitalone.dashboard.model.AuditStatus;
import com.capitalone.dashboard.model.DataStatus;
import com.capitalone.dashboard.model.DashboardType;
import com.capitalone.dashboard.model.CollectorItem;
import com.capitalone.dashboard.repository.AuditResultRepository;
import com.capitalone.dashboard.repository.DashboardRepository;
import com.capitalone.dashboard.repository.ComponentRepository;
import com.capitalone.dashboard.repository.CollectorRepository;
import com.capitalone.dashboard.repository.CollectorItemRepository;
import com.google.common.io.Resources;
import org.apache.commons.io.IOUtils;
import org.bson.types.ObjectId;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
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

    public void setup() throws IOException {
        TestUtils.loadDashBoard(dashboardRepository);
        TestUtils.loadCollector(collectorRepository);
        TestUtils.loadComponent(componentRepository);
        loadCollectorItem();
        settings = new TestConfig().settings();
    }

    private void loadCollectorItem() throws IOException {
        TestUtils.loadCollectorItems(collectorItemRepository);
    }


    @Test
    public void test_testResultAuditCollect() throws IOException, ParseException {
        this.loadCollectorItem();
        ResponseEntity<String> response = new ResponseEntity<>(getJSONResponse("response/auditresponse.json"),
                HttpStatus.OK);
        JSONParser jsonParser = new JSONParser();
        JSONObject responseObj = (JSONObject) jsonParser.parse(response.getBody());
        JSONObject review = (JSONObject) responseObj.get("review");
        AuditCollectorUtil auditCollectorUtil = new AuditCollectorUtil(null, null, collectorItemRepository);
        CollectorItem collectorItem = collectorItemRepository.findByDescription("title test_result audit process").get(0);
        Audit testAudit = auditCollectorUtil.getTestAudit((JSONArray) review.get(AuditType.TEST_RESULT.name()),
                (JSONArray) responseObj.get("auditStatuses"));
        Assert.assertEquals(testAudit.getAuditStatus(), AuditStatus.OK);
        Assert.assertEquals(testAudit.getDataStatus(), DataStatus.OK);
        Assert.assertEquals(testAudit.getType(), AuditType.TEST_RESULT);
        Assert.assertEquals(testAudit.getCollectorItem().getId(), collectorItem.getId());
        Assert.assertTrue(Optional.ofNullable(testAudit.getOptions().get("traceability")).isPresent());
        Assert.assertTrue(Optional.ofNullable(testAudit.getOptions().get("featureTestResult")).isPresent());
    }

    @Test
    public void getCollectAuditStatusData() throws IOException {
        this.setup();
        Iterable<Dashboard> recentDashboards = dashboardRepository.findAll();
        List<AuditResult> auditResults = new ArrayList<>();
        AuditResult auditResult1 = new AuditResult(ObjectId.get(),"auditTestDashboard" , "CARD",
                "ASVC","BAP" ,"Owner" ,null ,
                AuditType.CODE_QUALITY ,"OK","OK" ,null,null, 7883L );
        CollectorItem collectorItem = collectorItemRepository.findByDescription("title test_result audit process").get(0);
        auditResult1.setCollectorItemId(collectorItem.getId());
        auditResults.add(auditResult1);

        Set<AuditType> allAuditTypes = new HashSet<>();
        allAuditTypes.add(AuditType.CODE_QUALITY);
        recentDashboards.forEach(dashboard -> {
            try {
                Map<AuditType,Audit> auditMap1 = new HashMap<>();
                Audit auditCQ = new Audit();
                auditCQ.setType(AuditType.CODE_QUALITY);
                auditCQ.setAuditStatus(AuditStatus.OK);
                auditCQ.setDataStatus(DataStatus.OK);
                auditMap1.put(AuditType.CODE_QUALITY, auditCQ);
                AuditResult auditResult = AuditCollectorUtil.getAuditResults().get(0);
                AuditCollectorUtil auditCollectorUtil = new AuditCollectorUtil(null, null, collectorItemRepository);
                Mockito.when(auditCollectorUtil.getAudit(dashboard,settings,BEGIN_DATE,END_DATE)).thenReturn(auditMap1);
                assertNotNull(auditResult.getDashboardId());
                assertNotNull(auditResult.getDashboardTitle());
                assertNotNull(auditResult.getTimestamp());
                assertNotNull(auditResult.getCollectorItemId());
                assertTrue(auditResult.getDashboardId().equals(dashboard.getId()));
                assertTrue(auditResult.getDashboardTitle().equalsIgnoreCase(dashboard.getTitle()));
                assertTrue(auditResult.getCollectorItemId().equals(collectorItem.getId()));
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
                    assertTrue(auditResult.getCollectorItemId().equals(collectorItem.getId()));
                });
            }
        });
    }

    private String getJSONResponse(String fileName) throws IOException {
        String path = "./" + fileName;
        URL fileUrl = Resources.getResource(path);
        return IOUtils.toString(fileUrl);
    }

    public Dashboard getDashboard() {
        Dashboard dashboard = new Dashboard("", "tychehygieiatitle", null, null, DashboardType.Team,
                "", "", null, false, null);
        return dashboard;
    }
}