package com.capitalone.dashboard.service;

import com.capitalone.dashboard.common.TestUtils;
import com.capitalone.dashboard.config.FongoConfig;
import com.capitalone.dashboard.config.TestConfig;
import com.capitalone.dashboard.model.ExecutiveFeatureMetrics;
import com.capitalone.dashboard.model.ComponentFeatureMetrics;
import com.capitalone.dashboard.model.LobFeatureMetrics;
import com.capitalone.dashboard.model.ProductFeatureMetrics;
import com.capitalone.dashboard.repository.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;



@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestConfig.class, FongoConfig.class})
@DirtiesContext
public class FeatureMetricsServiceTest {

    @Autowired
    private DashboardRepository dashboardRepository;

    @Autowired
    private ComponentRepository componentRepository;

    @Autowired
    private CodeQualityRepository codeQualityRepository;

    @Autowired
    private TestResultRepository testResultsRepository;

    @Autowired
    private CmdbRepository cmdbRepository;

    @Autowired
    private CollectorItemRepository collectorItemRepository;


    @Autowired
    private FeatureMetricsService featureMetricsService;

    @Autowired
    private FeatureRepository featureRepository;

    @Autowired
    private BuildRepository buildRepository;



    @Before
    public void loadStuff() throws IOException {
        TestUtils.loadDashBoard(dashboardRepository);
        TestUtils.loadComponent(componentRepository);
        TestUtils.loadSSCRequests(codeQualityRepository);
        TestUtils.loadTestResults(testResultsRepository);
        TestUtils.loadCodeQuality(codeQualityRepository);
        TestUtils.loadCollectorItems(collectorItemRepository);
        TestUtils.loadFeature(featureRepository);
        TestUtils.loadCmdb(cmdbRepository);
        TestUtils.loadBuilds(buildRepository);

    }

    @Test
    public void getFeatureMetrics(){
        ComponentFeatureMetrics featureMetrics = featureMetricsService.getComponentFeatureMetrics("TestSSA");

        Assert.assertEquals("TestSSA", featureMetrics.getId());
        Assert.assertEquals("Component", featureMetrics.getType());
        Assert.assertEquals("TestAudit", featureMetrics.getName());
        Assert.assertEquals(getMetrics(), featureMetrics.getMetrics());

    }

    @Test
    public void getFetatureMetricsByType(){
        ComponentFeatureMetrics featureMetrics = featureMetricsService.getComponentFeatureMetricByType("TestSSA","FEATURE_TEST_PASS");
        Assert.assertEquals("TestSSA", featureMetrics.getId());
        Assert.assertEquals("Component", featureMetrics.getType());
        Assert.assertEquals("TestAudit", featureMetrics.getName());
        Assert.assertEquals(getMetricsByType(), featureMetrics.getMetrics());
    }


    @Test
    public void getProductFeatureMetrics(){
        ProductFeatureMetrics productFeatureMetrics = featureMetricsService.getProductFeatureMetrics("product1");
        Assert.assertEquals("product1", productFeatureMetrics.getId());
        Assert.assertEquals("TestAudit", productFeatureMetrics.getName());
        Assert.assertEquals("application", productFeatureMetrics.getType());
        Assert.assertEquals("Tech", productFeatureMetrics.getLob());
        Assert.assertEquals("TestSSA", productFeatureMetrics.getComponents().get(0).getId());
        Assert.assertEquals("Component", productFeatureMetrics.getComponents().get(0).getType());
        Assert.assertEquals("TestAudit", productFeatureMetrics.getComponents().get(0).getName());
        Assert.assertEquals(getMetrics(), productFeatureMetrics.getComponents().get(0).getMetrics());
    }

    @Test
    public void getProductFeatureMetricsByType(){
        ProductFeatureMetrics productFeatureMetrics = featureMetricsService.getProductFeatureMetricsByType("product1","FEATURE_TEST_PASS");
        Assert.assertEquals("product1", productFeatureMetrics.getId());
        Assert.assertEquals("TestAudit", productFeatureMetrics.getName());
        Assert.assertEquals("application", productFeatureMetrics.getType());
        Assert.assertEquals("Tech", productFeatureMetrics.getLob());
        Assert.assertEquals("TestSSA", productFeatureMetrics.getComponents().get(0).getId());
        Assert.assertEquals("Component", productFeatureMetrics.getComponents().get(0).getType());
        Assert.assertEquals("TestAudit", productFeatureMetrics.getComponents().get(0).getName());
        Assert.assertEquals(getMetricsByType(), productFeatureMetrics.getComponents().get(0).getMetrics());
    }

    @Test
    public void getLobFeatureMetrics(){
        LobFeatureMetrics lobFeatureMetrics = featureMetricsService.getLobFeatureMetrics("Tech");
        Assert.assertEquals("Tech", lobFeatureMetrics.getName());
        Assert.assertEquals("lob", lobFeatureMetrics.getType());
        Assert.assertEquals("product1", lobFeatureMetrics.getApplications().get(0).getId());
        Assert.assertEquals("TestAudit", lobFeatureMetrics.getApplications().get(0).getName());
        Assert.assertEquals("application", lobFeatureMetrics.getApplications().get(0).getType());
        Assert.assertEquals("TestSSA", lobFeatureMetrics.getApplications().get(0).getComponents().get(0).getId());
        Assert.assertEquals("Component", lobFeatureMetrics.getApplications().get(0).getComponents().get(0).getType());
        Assert.assertEquals("TestAudit", lobFeatureMetrics.getApplications().get(0).getComponents().get(0).getName());
        Assert.assertEquals(getMetrics(), lobFeatureMetrics.getApplications().get(0).getComponents().get(0).getMetrics());
    }

    @Test
    public void getLobFeatureMetricsByType(){
        LobFeatureMetrics lobFeatureMetrics = featureMetricsService.getLobFeatureMetricsByType("Tech","FEATURE_TEST_PASS");
        Assert.assertEquals("Tech", lobFeatureMetrics.getName());
        Assert.assertEquals("lob", lobFeatureMetrics.getType());
        Assert.assertEquals("product1", lobFeatureMetrics.getApplications().get(0).getId());
        Assert.assertEquals("100", lobFeatureMetrics.getPercentage());
        Assert.assertEquals("TestAudit", lobFeatureMetrics.getApplications().get(0).getName());
        Assert.assertEquals("application", lobFeatureMetrics.getApplications().get(0).getType());
        Assert.assertEquals("TestSSA", lobFeatureMetrics.getApplications().get(0).getComponents().get(0).getId());
        Assert.assertEquals("Component", lobFeatureMetrics.getApplications().get(0).getComponents().get(0).getType());
        Assert.assertEquals("TestAudit", lobFeatureMetrics.getApplications().get(0).getComponents().get(0).getName());
        Assert.assertEquals(getMetricsByType(), lobFeatureMetrics.getApplications().get(0).getComponents().get(0).getMetrics());
    }

    @Test
    public void getExecutiveFeatureMetrics(){
        ExecutiveFeatureMetrics executiveFeatureMetrics = featureMetricsService.getExecutiveFeatureMetrics("chow");
        Assert.assertEquals("chow", executiveFeatureMetrics.getName());
        Assert.assertEquals("executive", executiveFeatureMetrics.getType());
        Assert.assertEquals("product1", executiveFeatureMetrics.getApplications().get(0).getId());
        Assert.assertEquals("TestAudit", executiveFeatureMetrics.getApplications().get(0).getName());
        Assert.assertEquals("application", executiveFeatureMetrics.getApplications().get(0).getType());
        Assert.assertEquals("TestSSA", executiveFeatureMetrics.getApplications().get(0).getComponents().get(0).getId());
        Assert.assertEquals("Component", executiveFeatureMetrics.getApplications().get(0).getComponents().get(0).getType());
        Assert.assertEquals("TestAudit", executiveFeatureMetrics.getApplications().get(0).getComponents().get(0).getName());
        Assert.assertEquals(getMetrics(), executiveFeatureMetrics.getApplications().get(0).getComponents().get(0).getMetrics());

    }

    @Test
    public void getExecutiveFeatureMetricsByType(){
        ExecutiveFeatureMetrics executiveFeatureMetrics = featureMetricsService.getExecutiveFeatureMetricsByType("chow","FEATURE_TEST_PASS");
        Assert.assertEquals("chow", executiveFeatureMetrics.getName());
        Assert.assertEquals("executive", executiveFeatureMetrics.getType());
        Assert.assertEquals("100", executiveFeatureMetrics.getPercentage());
        Assert.assertEquals("product1", executiveFeatureMetrics.getApplications().get(0).getId());
        Assert.assertEquals("TestAudit", executiveFeatureMetrics.getApplications().get(0).getName());
        Assert.assertEquals("application", executiveFeatureMetrics.getApplications().get(0).getType());
        Assert.assertEquals("TestSSA", executiveFeatureMetrics.getApplications().get(0).getComponents().get(0).getId());
        Assert.assertEquals("Component", executiveFeatureMetrics.getApplications().get(0).getComponents().get(0).getType());
        Assert.assertEquals("TestAudit", executiveFeatureMetrics.getApplications().get(0).getComponents().get(0).getName());
        Assert.assertEquals(getMetricsByType(), executiveFeatureMetrics.getApplications().get(0).getComponents().get(0).getMetrics());

    }

    @Test
    public void getExecutiveTraceabilityByType(){
        ExecutiveFeatureMetrics executiveFeatureMetrics = featureMetricsService.getExecutiveFeatureMetricsByType("chow","TRACEABILITY");
        Assert.assertEquals("chow", executiveFeatureMetrics.getName());
        Assert.assertEquals("executive", executiveFeatureMetrics.getType());
        Assert.assertEquals("50", executiveFeatureMetrics.getPercentage());
        Assert.assertEquals("product1", executiveFeatureMetrics.getApplications().get(0).getId());
        Assert.assertEquals("TestAudit", executiveFeatureMetrics.getApplications().get(0).getName());
        Assert.assertEquals("application", executiveFeatureMetrics.getApplications().get(0).getType());
        Assert.assertEquals("TestSSA", executiveFeatureMetrics.getApplications().get(0).getComponents().get(0).getId());
        Assert.assertEquals("Component", executiveFeatureMetrics.getApplications().get(0).getComponents().get(0).getType());
        Assert.assertEquals("TestAudit", executiveFeatureMetrics.getApplications().get(0).getComponents().get(0).getName());
        Assert.assertEquals(getTraceabilityMetricByType(), executiveFeatureMetrics.getApplications().get(0).getComponents().get(0).getMetrics());

    }

    private List<HashMap> getMetrics(){
        List<HashMap> metrics = new ArrayList<>();
        HashMap<String,HashMap<String,String>> codeQuality = new HashMap<>();
        HashMap<String,String> codeQualityPercent = new HashMap<>();
        codeQualityPercent.put("percentage", "93.5");
        codeQuality.put("CODE_COVERAGE", codeQualityPercent);
        HashMap<String,HashMap<String,String>> errorRate = new HashMap<>();
        HashMap<String,String> errorRatePercent = new HashMap<>();
        errorRatePercent.put("percentage", "0");
        errorRate.put("PERF_ERROR_RATE", errorRatePercent);
        HashMap<String,HashMap<String,String>> featureTest = new HashMap<>();
        HashMap<String,String> featureTestPercent = new HashMap<>();
        featureTestPercent.put("percentage", "100");
        featureTest.put("FEATURE_TEST_PASS", featureTestPercent);
        HashMap<String,HashMap<String,String>> traceability = new HashMap<>();
        HashMap<String,String> traceabilityPercent = new HashMap<>();
        traceabilityPercent.put("percentage", "50");
        traceability.put("TRACEABILITY", traceabilityPercent);
        HashMap<String,HashMap<String,String>> deployScripts = new HashMap<>();
        HashMap<String,String> deployScriptPercent = new HashMap<>();
        deployScriptPercent.put("percentage", "0");
        deployScripts.put("DEPLOY_SCRIPTS", deployScriptPercent);
        metrics.add(codeQuality);
        metrics.add(errorRate);
        metrics.add(featureTest);
        metrics.add(traceability);
        metrics.add(deployScripts);

        return metrics;

    }

    private List<HashMap> getMetricsByType(){
        List<HashMap> metrics = new ArrayList<>();
        HashMap<String,HashMap<String,String>> featureTest = new HashMap<>();
        HashMap<String,String> featureTestPercent = new HashMap<>();
        featureTestPercent.put("percentage", "100");
        featureTest.put("FEATURE_TEST_PASS", featureTestPercent);
        metrics.add(featureTest);
        return metrics;
    }

    private List<HashMap> getTraceabilityMetricByType(){
        List<HashMap> metrics = new ArrayList<>();
        HashMap<String,HashMap<String,String>> featureTest = new HashMap<>();
        HashMap<String,String> featureTestPercent = new HashMap<>();
        featureTestPercent.put("percentage", "50");
        featureTest.put("TRACEABILITY", featureTestPercent);
        metrics.add(featureTest);
        return metrics;
    }
}
