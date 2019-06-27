package com.capitalone.dashboard.service;

import com.capitalone.dashboard.model.FeatureMetrics;
import com.capitalone.dashboard.model.Dashboard;
import com.capitalone.dashboard.model.CollectorItem;
import com.capitalone.dashboard.model.CodeQuality;
import com.capitalone.dashboard.model.TestResult;
import com.capitalone.dashboard.model.CodeQualityMetric;
import com.capitalone.dashboard.model.TestCapability;
import com.capitalone.dashboard.model.DashboardType;
import com.capitalone.dashboard.model.Cmdb;
import com.capitalone.dashboard.model.CollectorType;
import com.capitalone.dashboard.model.TestSuiteType;
import com.capitalone.dashboard.model.TestSuite;
import com.capitalone.dashboard.model.Widget;
import com.capitalone.dashboard.model.TestCase;

import com.capitalone.dashboard.repository.DashboardRepository;
import com.capitalone.dashboard.repository.ComponentRepository;
import com.capitalone.dashboard.repository.CollectorItemRepository;
import com.capitalone.dashboard.repository.CodeQualityRepository;
import com.capitalone.dashboard.repository.TestResultRepository;
import com.capitalone.dashboard.repository.CmdbRepository;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Collection;
import java.util.Set;
import java.util.Optional;
import java.util.DoubleSummaryStatistics;
import java.util.Comparator;
import java.util.HashMap;
import java.util.stream.Collectors;

@Component
public class FeatureMetricServiceImpl implements FeatureMetricsService {

    private static final String STR_COVERAGE = "CODE_COVERAGE";

    private static final String STR_ERRORRATE = "PERF_ERROR_RATE";

    private static final String STR_FEATURE_TEST_PASS = "FEATURE_TEST_PASS";

    private static final String STR_PERCENTAGE = "percentage";

    private static final String STR_ACTUAL_ERRORRATE = "Actual Error Rate";

    private static final String STR_KPI_ERRORRATE = "KPI : Error Rate Threshold";

    private static final String STR_MTR_COVERAGE = "coverage";

    private static final String STR_WIDGETNAME = "codeanalysis";

    private final DashboardRepository dashboardRepository;

    private final ComponentRepository componentRepository;

    private final CodeQualityRepository codeQualityRepository;

    private final CollectorItemRepository collectorItemRepository;

    private final TestResultRepository testResultRepository;

    private final CmdbRepository cmdbRepository;

    @Autowired
    public FeatureMetricServiceImpl(DashboardRepository dashboardRepository, ComponentRepository componentRepository, CodeQualityRepository codeQualityRepository, CollectorItemRepository collectorItemRepository, TestResultRepository testResultRepository, CmdbRepository cmdbRepository) {
        this.dashboardRepository = dashboardRepository;
        this.componentRepository = componentRepository;
        this.codeQualityRepository = codeQualityRepository;
        this.collectorItemRepository = collectorItemRepository;
        this.testResultRepository = testResultRepository;
        this.cmdbRepository = cmdbRepository;
    }


    @Override
    public FeatureMetrics getFeatureMetrics(String name) {
        FeatureMetrics featureMetrics = new FeatureMetrics();
        Dashboard dashboard = dashboardRepository.findByTitleAndType(name, DashboardType.Team);
        if(dashboard != null) {
            List<HashMap> metrics = getMetrics(dashboard, null);

            featureMetrics.setName(dashboard.getTitle());
            featureMetrics.setType("Component");
            featureMetrics.setApplication(dashboard.getApplication().getName());
            Cmdb cmdb = cmdbRepository.findByItemTypeAndCommonNameContainingIgnoreCase("component", dashboard.getApplication().getName());
            featureMetrics.setLob(cmdb.getOwnerDept());
            featureMetrics.setMetrics(metrics);
        }else{
            featureMetrics.setMessage("Component is not available");
        }
        return featureMetrics;
    }

    @Override
    public FeatureMetrics getFeatureMetricsByType(String name, String type) {

        FeatureMetrics featureMetrics = new FeatureMetrics();
        Dashboard dashboard = dashboardRepository.findByTitleAndType(name, DashboardType.Team);

        List<HashMap> metrics = getMetrics(dashboard, type);

        if(dashboard != null){
            featureMetrics.setName(dashboard.getTitle());
            featureMetrics.setType("Component");
            featureMetrics.setApplication(dashboard.getApplication().getName());
            Cmdb cmdb = cmdbRepository.findByItemTypeAndCommonNameContainingIgnoreCase("component",dashboard.getApplication().getName());
            featureMetrics.setLob(cmdb.getOwnerDept());
            featureMetrics.setMetrics(metrics);
        }else{
            featureMetrics.setMessage("Component is not available");
        }

        return featureMetrics;

    }


    /**
     *
     * @param dashboard
     * @param type
     * @return
     */
    private List<HashMap> getMetrics(Dashboard dashboard, String type){

        List<HashMap> metrics = new ArrayList<>();
        List<CollectorItem> codeQualityItems = getCollectorItems(dashboard, STR_WIDGETNAME, CollectorType.CodeQuality);
        List<CollectorItem> perfItems = getCollectorItems(dashboard, STR_WIDGETNAME, CollectorType.Test);
        List<CollectorItem> testItems = getCollectorItems(dashboard, STR_WIDGETNAME, CollectorType.Test);
        if(type == null) {
            HashMap<String, HashMap<String, Double>> codeQuality = getAggregatedCodeQuality(codeQualityItems);
            HashMap<String, HashMap<String, Double>> errorRate = getAggregatedErrorRate(perfItems);
            HashMap<String, HashMap<String, Double>> featureTest = getAggregatedFeatureTestPass(testItems);
            metrics.add(codeQuality);
            metrics.add(errorRate);
            metrics.add(featureTest);
        }else if(STR_COVERAGE.equalsIgnoreCase(type)){
            HashMap<String, HashMap<String, Double>> codeQuality = getAggregatedCodeQuality(codeQualityItems);
            metrics.add(codeQuality);
        }else if(STR_ERRORRATE.equalsIgnoreCase(type)){
            HashMap<String, HashMap<String, Double>> errorRate = getAggregatedErrorRate(perfItems);
            metrics.add(errorRate);
        }else if(STR_FEATURE_TEST_PASS.equalsIgnoreCase(type)){
            HashMap<String, HashMap<String, Double>> testItems1 = getAggregatedFeatureTestPass(testItems);
            metrics.add(testItems1);
        }

        return metrics;
    }

    /**
     *
     * @param codeQualityItems
     * @return
     */
    private HashMap<String,HashMap<String,Double>> getAggregatedCodeQuality(List<CollectorItem> codeQualityItems){

        List<Double> values = new ArrayList();
        HashMap<String,HashMap<String,Double>> codeQuality = new HashMap<>();
        HashMap<String,Double> codeQualityPercent = new HashMap<>();


        if(CollectionUtils.isNotEmpty(codeQualityItems)){
            codeQualityItems.forEach(collectorItem -> {

                CodeQuality codeQualities = codeQualityRepository.findTop1ByCollectorItemIdOrderByTimestampDesc(collectorItem.getId());
                if(codeQualities != null){
                    Set<CodeQualityMetric> javaCollection =codeQualities.getMetrics();
                    Optional.ofNullable(javaCollection)
                            .orElseGet(Collections::emptySet)
                            .forEach(m -> {
                                if (STR_MTR_COVERAGE.equalsIgnoreCase(m.getName())) {
                                    String valueStr = m.getValue();
                                    double value = Double.parseDouble(valueStr);
                                    values.add(value);
                                }
                            });

                }
            });
        }
        DoubleSummaryStatistics stats = values.stream().mapToDouble((x) -> x).summaryStatistics();
        codeQualityPercent.put(STR_PERCENTAGE, stats.getAverage());
        codeQuality.put(STR_COVERAGE, codeQualityPercent);
        return codeQuality;

    }

    /**
     *
     * @param perfItems
     * @return
     */
    private HashMap<String,HashMap<String,Double>> getAggregatedErrorRate(List<CollectorItem> perfItems){

        List<Double> values = new ArrayList();
        HashMap<String,HashMap<String,Double>> perfErrorRate = new HashMap<>();
        HashMap<String,Double> errorRatePercent = new HashMap<>();
        if(CollectionUtils.isNotEmpty(perfItems)){
            perfItems.forEach(collectorItem -> {
                TestResult perfTestResult = testResultRepository.findTop1ByCollectorItemIdOrderByTimestampDesc(collectorItem.getId());
                if((perfTestResult != null) && (perfTestResult.getType().equals(TestSuiteType.Performance))){

                    Collection<TestCapability> testCapabilities = perfTestResult.getTestCapabilities();
                    if(!CollectionUtils.isEmpty(testCapabilities)){
                        Comparator<TestCapability> testCapabilityComparator = Comparator.comparing(TestCapability::getTimestamp);
                        List<TestCapability> tc = new ArrayList<>(testCapabilities);
                        Collections.sort(tc,testCapabilityComparator.reversed());
                        TestCapability testCapability =  tc.get(0);
                        Collection<TestSuite> testSuites = testCapability.getTestSuites();
                        for (TestSuite testSuite : testSuites) {
                            Collection<TestCase> testCases = testSuite.getTestCases();
                            for (TestCase testCase : testCases) {
                                if(STR_KPI_ERRORRATE.equalsIgnoreCase(testCase.getDescription())){
                                    testCase.getTestSteps().forEach(testCaseStep -> {
                                        if(STR_ACTUAL_ERRORRATE.equalsIgnoreCase(testCaseStep.getId())){
                                            values.add(Double.parseDouble(testCaseStep.getDescription()));
                                        }
                                    });
                                }
                            }
                        }
                    }
                }
            });
        }
        DoubleSummaryStatistics stats = values.stream().mapToDouble((x) -> x).summaryStatistics();
        errorRatePercent.put(STR_PERCENTAGE,stats.getAverage());
        perfErrorRate.put(STR_ERRORRATE ,errorRatePercent);
        return perfErrorRate;
    }

    /**
     *
     * @param testItems
     * @return
     */

    private HashMap<String,HashMap<String,Double>> getAggregatedFeatureTestPass(List<CollectorItem> testItems){
        List<Double> values = new ArrayList<>();
        HashMap<String,HashMap<String,Double>> featureTestPass = new HashMap<>();
        HashMap<String,Double> featureTestPercent = new HashMap<>();
        if(CollectionUtils.isNotEmpty(testItems)){
            testItems.forEach(collectorItem -> {
                TestResult testResults = testResultRepository.findTop1ByCollectorItemIdOrderByTimestampDesc(collectorItem.getId());
                if((testResults != null) && (testResults.getType().equals(TestSuiteType.Functional))){

                    List<TestCapability> testCapabilities = testResults.getTestCapabilities().stream().collect(Collectors.toList());
                    Double featureTestPassing = featureTestPassCount(testCapabilities);
                    values.add(featureTestPassing);

                }
            });
        }
        DoubleSummaryStatistics stats = values.stream().mapToDouble((x) -> x).summaryStatistics();
        featureTestPercent.put(STR_PERCENTAGE, stats.getAverage());
        featureTestPass.put(STR_FEATURE_TEST_PASS, featureTestPercent);
        return featureTestPass;
    }

    /**
     *
     * @param testCapabilities
     * @return
     */
    private Double featureTestPassCount(List<TestCapability> testCapabilities){


        double testCaseSuccessCount = testCapabilities.stream().mapToDouble(testCapability ->
                testCapability.getTestSuites().parallelStream().mapToDouble(TestSuite::getSuccessTestCaseCount).sum()
        ).sum();
        double totalTestCaseCount = testCapabilities.stream().mapToDouble(testCapability ->
                testCapability.getTestSuites().parallelStream().mapToDouble(TestSuite::getTotalTestCaseCount).sum()
        ).sum();

        return (testCaseSuccessCount/totalTestCaseCount) * 100;

    }

    /**
     *
     * @param dashboard
     * @param widgetName
     * @param collectorType
     * @return
     */
    private List<CollectorItem> getCollectorItems(Dashboard dashboard, String widgetName, CollectorType collectorType) {
        List<Widget> widgets = dashboard.getWidgets();
        ObjectId componentId = widgets.stream().filter(widget -> widget.getName().equalsIgnoreCase(widgetName)).findFirst().map(Widget::getComponentId).orElse(null);

        if (null == componentId) return null;

        com.capitalone.dashboard.model.Component component = componentRepository.findOne(componentId);

        List<CollectorItem> listFromComponent = component.getCollectorItems().get(collectorType);

        if (CollectionUtils.isEmpty(listFromComponent)) {
            return null;
        }

        List<ObjectId> ids = listFromComponent.stream().map(CollectorItem::getId).collect(Collectors.toList());
        return Lists.newArrayList(collectorItemRepository.findAll(ids));
    }
}
