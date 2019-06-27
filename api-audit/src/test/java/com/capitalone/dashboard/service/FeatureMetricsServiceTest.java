package com.capitalone.dashboard.service;

import com.capitalone.dashboard.common.TestUtils;
import com.capitalone.dashboard.config.FongoConfig;
import com.capitalone.dashboard.config.TestConfig;
import com.capitalone.dashboard.model.FeatureMetrics;
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



    @Before
    public void loadStuff() throws IOException {
        TestUtils.loadDashBoard(dashboardRepository);
        TestUtils.loadComponent(componentRepository);
        TestUtils.loadSSCRequests(codeQualityRepository);
        TestUtils.loadTestResults(testResultsRepository);
        TestUtils.loadCodeQuality(codeQualityRepository);
        TestUtils.loadCollectorItems(collectorItemRepository);
        TestUtils.loadCmdb(cmdbRepository);

    }

    @Test
    public void getFeatureMetrics(){
        FeatureMetrics featureMetrics = featureMetricsService.getFeatureMetrics("TestSSA");

        Assert.assertEquals("TestSSA", featureMetrics.getName());
        Assert.assertEquals("Component", featureMetrics.getType());
        Assert.assertEquals("TestAudit", featureMetrics.getApplication());
        Assert.assertEquals(getMetrics(), featureMetrics.getMetrics());

    }

    @Test
    public void getFetatureMetricsByType(){
        FeatureMetrics featureMetrics = featureMetricsService.getFeatureMetricsByType("TestSSA","FEATURE_TEST_PASS");
        Assert.assertEquals("TestSSA", featureMetrics.getName());
        Assert.assertEquals("Component", featureMetrics.getType());
        Assert.assertEquals("TestAudit", featureMetrics.getApplication());
        Assert.assertEquals(getMetricsByType(), featureMetrics.getMetrics());

    }

    private List<HashMap> getMetrics(){
        List<HashMap> metrics = new ArrayList<>();
        HashMap<String,HashMap<String,Double>> codeQuality = new HashMap<>();
        HashMap<String,Double> codeQualityPercent = new HashMap<>();
        codeQualityPercent.put("percentage", 93.5);
        codeQuality.put("CODE_COVERAGE", codeQualityPercent);
        HashMap<String,HashMap<String,Double>> errorRate = new HashMap<>();
        HashMap<String,Double> errorRatePercent = new HashMap<>();
        errorRatePercent.put("percentage", 0.0);
        errorRate.put("PERF_ERROR_RATE", errorRatePercent);
        HashMap<String,HashMap<String,Double>> featureTest = new HashMap<>();
        HashMap<String,Double> featureTestPercent = new HashMap<>();
        featureTestPercent.put("percentage", 100.0);
        featureTest.put("FEATURE_TEST_PASS", featureTestPercent);

        metrics.add(codeQuality);
        metrics.add(errorRate);
        metrics.add(featureTest);

        return metrics;

    }


    private List<HashMap> getMetricsByType(){
        List<HashMap> metrics = new ArrayList<>();
        HashMap<String,HashMap<String,Double>> featureTest = new HashMap<>();
        HashMap<String,Double> featureTestPercent = new HashMap<>();
        featureTestPercent.put("percentage", 100.0);
        featureTest.put("FEATURE_TEST_PASS", featureTestPercent);
        metrics.add(featureTest);
        return metrics;
    }



}
