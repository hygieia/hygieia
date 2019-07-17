package com.capitalone.dashboard.service;

import com.capitalone.dashboard.model.*;

import com.capitalone.dashboard.repository.DashboardRepository;
import com.capitalone.dashboard.repository.ComponentRepository;
import com.capitalone.dashboard.repository.CodeQualityRepository;
import com.capitalone.dashboard.repository.TestResultRepository;
import com.capitalone.dashboard.repository.CmdbRepository;
import org.apache.commons.collections.CollectionUtils;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.DecimalFormat;
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

    private final TestResultRepository testResultRepository;

    private final CmdbRepository cmdbRepository;

    private static DecimalFormat df2 = new DecimalFormat("#.##");


    @Autowired
    public FeatureMetricServiceImpl(DashboardRepository dashboardRepository, ComponentRepository componentRepository, CodeQualityRepository codeQualityRepository, TestResultRepository testResultRepository, CmdbRepository cmdbRepository) {
        this.dashboardRepository = dashboardRepository;
        this.componentRepository = componentRepository;
        this.codeQualityRepository = codeQualityRepository;
        this.testResultRepository = testResultRepository;
        this.cmdbRepository = cmdbRepository;
    }


    @Override
    public FeatureMetrics getFeatureMetrics(String name) {
        FeatureMetrics featureMetrics = new FeatureMetrics();
        Dashboard dashboard = dashboardRepository.findByTitleAndType(name, DashboardType.Team);
        if(dashboard != null) {
            List<HashMap> metrics = getMetrics(dashboard, null);

            featureMetrics.setId(dashboard.getTitle());
            featureMetrics.setType("Component");
            featureMetrics.setName(dashboard.getApplication().getName());
           // Cmdb cmdb = cmdbRepository.findByItemTypeAndCommonNameContainingIgnoreCase("component", dashboard.getApplication().getName());
            //featureMetrics.setLob(cmdb.getOwnerDept());
            featureMetrics.setMetrics(metrics);
        }else{
            featureMetrics.setName(name);
            featureMetrics.setMessage("Component is not available");
        }
        return featureMetrics;
    }

    @Override
    public FeatureMetrics getFeatureMetricsByType(String name, String type) {

        FeatureMetrics featureMetrics = new FeatureMetrics();
        Dashboard dashboard = dashboardRepository.findByTitleAndType(name, DashboardType.Team);



        if(dashboard != null){
            List<HashMap> metrics = getMetrics(dashboard, type);
            featureMetrics.setId(dashboard.getTitle());
            featureMetrics.setType("Component");
            featureMetrics.setName(dashboard.getApplication().getName());
            //Cmdb cmdb = cmdbRepository.findByItemTypeAndCommonNameContainingIgnoreCase("component",dashboard.getApplication().getName());
            //featureMetrics.setLob(cmdb.getOwnerDept());
            featureMetrics.setMetrics(metrics);
        }else{
            featureMetrics.setName(name);
            featureMetrics.setMessage("Component is not configured");
        }

        return featureMetrics;

    }

    @Override
    public ProductFeatureMetrics getProductFeatureMetrics(String name) {
        ProductFeatureMetrics productFeatureMetrics = new ProductFeatureMetrics();
        Cmdb cmdb = cmdbRepository.findByConfigurationItemAndItemTypeAndValidConfigItem(name, "app", true);
        List<String> components = cmdb.getComponents();
        List<FeatureMetrics> productComponent = new ArrayList<>();
        Optional.ofNullable(components).orElseGet(Collections::emptyList)
                .stream().forEach(component -> {productComponent.add(getFeatureMetrics(component));});

        productFeatureMetrics.setId(name);
        productFeatureMetrics.setLob(cmdb.getOwnerDept());
        productFeatureMetrics.setName(cmdb.getCommonName());
        productFeatureMetrics.setType("application");
        productFeatureMetrics.setComponents(productComponent);


        return productFeatureMetrics;
    }

    @Override
    public ProductFeatureMetrics getProductFeatureMetricsByType(String name, String type) {
        ProductFeatureMetrics productFeatureMetrics = new ProductFeatureMetrics();


        Cmdb cmdb = cmdbRepository.findByConfigurationItemAndItemTypeAndValidConfigItem(name,"app" ,true );
        List<String> components = cmdb.getComponents();
        List<FeatureMetrics> productComponent = new ArrayList<>();
        Optional.ofNullable(components).orElseGet(Collections::emptyList)
                .stream().forEach(component -> {productComponent.add(getFeatureMetricsByType(component,type));});
        productFeatureMetrics.setId(name);
        productFeatureMetrics.setLob(cmdb.getOwnerDept());
        productFeatureMetrics.setName(cmdb.getCommonName());
        productFeatureMetrics.setType("application");
        productFeatureMetrics.setComponents(productComponent);


        return productFeatureMetrics;
    }

    @Override
    public LobFeatureMetrics getLobFeatureMetrics(String lob) {
        LobFeatureMetrics lobFeatureMetrics = new LobFeatureMetrics();
        List<ProductFeatureMetrics> productFeatureMetrics = new ArrayList<>();
        List<Cmdb> cmdb = cmdbRepository.findByItemTypeAndOwnerDeptAndValidConfigItem("app", lob,true);
        List<String> componentNames = new ArrayList<>();
        cmdb.forEach(cmdb1 -> componentNames.add(cmdb1.getConfigurationItem()));
        componentNames.forEach(componentName -> productFeatureMetrics.add(getProductFeatureMetrics(componentName)));
        lobFeatureMetrics.setName(lob);
        lobFeatureMetrics.setType("lob");
        lobFeatureMetrics.setApplications(productFeatureMetrics);


        return lobFeatureMetrics;
    }

    @Override
    public LobFeatureMetrics getLobFeatureMetricsByType(String lob, String type) {
        LobFeatureMetrics lobFeatureMetrics = new LobFeatureMetrics();
        List<ProductFeatureMetrics> productFeatureMetrics = new ArrayList<>();
        List<Cmdb> cmdb = cmdbRepository.findByItemTypeAndOwnerDeptAndValidConfigItem("app", lob,true);
        cmdb.stream().forEach(cmdb1 -> productFeatureMetrics.add(getProductMetrics(cmdb1,type)));
        List<Double> percentages = productFeatureMetrics.stream().map(ProductFeatureMetrics::getPercentage).collect(Collectors.toList());
        List<Double> actualPercentage = new ArrayList<>();
        percentages.forEach(percentage -> {
            if(!percentage.equals(Double.NaN)){
                actualPercentage.add(percentage);
            }
        });
        DoubleSummaryStatistics stats = actualPercentage.stream().mapToDouble((x) -> x).summaryStatistics();
        lobFeatureMetrics.setName(lob);
        lobFeatureMetrics.setType("lob");
        lobFeatureMetrics.setPercentage(String.valueOf(df2.format(stats.getAverage())));
        lobFeatureMetrics.setApplications(productFeatureMetrics);


        return lobFeatureMetrics;
    }

    @Override
    public ExecutiveFeatureMetrics getExecutiveFeatureMetrics(String name) {
        ExecutiveFeatureMetrics executiveFeatureMetrics = new ExecutiveFeatureMetrics();
        List<Cmdb> cmdb = cmdbRepository.findByBusinessOwnerAndItemTypeAndValidConfigItem(name, "app",true);
        List<ProductFeatureMetrics> productFeatureMetrics = new ArrayList<>();
        cmdb.stream().forEach(cmdb1 -> productFeatureMetrics.add(getProductMetrics(cmdb1,null)));
        executiveFeatureMetrics.setName(name);
        executiveFeatureMetrics.setType("executive");
        executiveFeatureMetrics.setApplications(productFeatureMetrics);
        return executiveFeatureMetrics;
    }

    @Override
    public ExecutiveFeatureMetrics getExecutiveFeatureMetricsByType(String name, String metricType) {
        ExecutiveFeatureMetrics executiveFeatureMetrics = new ExecutiveFeatureMetrics();
        List<Cmdb> cmdb = cmdbRepository.findByBusinessOwnerAndItemTypeAndValidConfigItem(name, "app",true);
        List<ProductFeatureMetrics> productFeatureMetrics = new ArrayList<>();
        cmdb.stream().forEach(cmdb1 -> productFeatureMetrics.add(getProductMetrics(cmdb1,metricType)));
        List<Double> percentages = productFeatureMetrics.stream().map(ProductFeatureMetrics::getPercentage).collect(Collectors.toList());
        List<Double> actualPercentage = new ArrayList<>();
        percentages.forEach(percentage -> {
            if(!percentage.equals(Double.NaN)){
                actualPercentage.add(percentage);
            }
        });
        DoubleSummaryStatistics stats = actualPercentage.stream().mapToDouble((x) -> x).summaryStatistics();
        executiveFeatureMetrics.setName(name);
        executiveFeatureMetrics.setType("executive");
        executiveFeatureMetrics.setPercentage(df2.format(stats.getAverage()));
        executiveFeatureMetrics.setApplications(productFeatureMetrics);
        return executiveFeatureMetrics;
    }


    private ProductFeatureMetrics getProductMetrics(Cmdb cmdb,String type){
        ProductFeatureMetrics productFeatureMetrics = new ProductFeatureMetrics();
        List<Double> percentages = new ArrayList<>();
        List<String> components = cmdb.getComponents();
        List<FeatureMetrics> productComponent = new ArrayList<>();
        Optional.ofNullable(components).orElseGet(Collections::emptyList)
                .stream().forEach(component -> {
                    FeatureMetrics featureMetrics = getFeatureMetricsByType(component,type);
                    productComponent.add(featureMetrics);
                    Optional.ofNullable(featureMetrics.getMetrics()).orElseGet(Collections ::emptyList).stream().forEach(metric-> {
                        if(metric.containsKey(type)){
                            Optional<HashMap<String,String>> percent = Optional.of((HashMap<String, String>) metric.get(type));
                            if(percent.get().containsKey(STR_PERCENTAGE)){
                                percentages.add(Double.parseDouble(percent.get().get(STR_PERCENTAGE)));
                            }
                        }
                    });
                });
        List<Double> actualPercentage = new ArrayList<>();
        percentages.forEach(percentage -> {
            if(!percentage.equals(Double.NaN)){
                actualPercentage.add(percentage);

            }
        });
        DoubleSummaryStatistics stats = actualPercentage.stream().mapToDouble((x) -> x).summaryStatistics();

        productFeatureMetrics.setId(cmdb.getConfigurationItem());
        productFeatureMetrics.setLob(cmdb.getOwnerDept());
        productFeatureMetrics.setName(cmdb.getCommonName());
        productFeatureMetrics.setType("application");
        if(actualPercentage.size() > 0){
        productFeatureMetrics.setPercentage(Double.valueOf(df2.format(stats.getAverage())));
        }else{
            productFeatureMetrics.setPercentage(Double.NaN);
        }
        productFeatureMetrics.setComponents(productComponent);


        return productFeatureMetrics;
    }


    /**
     *
     * @param dashboard
     * @param type
     * @return
     */
    private List<HashMap> getMetrics(Dashboard dashboard, String type){

        List<HashMap> metrics = new ArrayList<>();

        if(type == null) {
            List<ObjectId> codeQualityItems = getCollectorItems(dashboard, STR_WIDGETNAME, CollectorType.CodeQuality);
            List<ObjectId> perfItems = getCollectorItems(dashboard, STR_WIDGETNAME, CollectorType.Test);
            List<ObjectId> testItems = getCollectorItems(dashboard, STR_WIDGETNAME, CollectorType.Test);
            HashMap<String, HashMap<String, String>> codeQuality = getAggregatedCodeQuality(codeQualityItems);
            HashMap<String, HashMap<String, String>> errorRate = getAggregatedErrorRate(perfItems);
            HashMap<String, HashMap<String, String>> featureTest = getAggregatedFeatureTestPass(testItems);
            metrics.add(codeQuality);
            metrics.add(errorRate);
            metrics.add(featureTest);
        }else if(STR_COVERAGE.equalsIgnoreCase(type)){
            List<ObjectId> codeQualityItems = getCollectorItems(dashboard, STR_WIDGETNAME, CollectorType.CodeQuality);
            HashMap<String, HashMap<String, String>> codeQuality = getAggregatedCodeQuality(codeQualityItems);
            metrics.add(codeQuality);
        }else if(STR_ERRORRATE.equalsIgnoreCase(type)){
            List<ObjectId> perfItems = getCollectorItems(dashboard, STR_WIDGETNAME, CollectorType.Test);
            HashMap<String, HashMap<String, String>> errorRate = getAggregatedErrorRate(perfItems);
            metrics.add(errorRate);
        }else if(STR_FEATURE_TEST_PASS.equalsIgnoreCase(type)){
            List<ObjectId> testItems = getCollectorItems(dashboard, STR_WIDGETNAME, CollectorType.Test);
            HashMap<String, HashMap<String, String>> testItems1 = getAggregatedFeatureTestPass(testItems);
            metrics.add(testItems1);
        }

        return metrics;
    }

    /**
     *
     * @param codeQualityItemsIds
     * @return
     */
    private HashMap<String,HashMap<String,String>> getAggregatedCodeQuality(List<ObjectId> codeQualityItemsIds){

        List<Double> values = new ArrayList();
        HashMap<String,HashMap<String,String>> codeQuality = new HashMap<>();
        HashMap<String,String> codeQualityPercent = new HashMap<>();


        if(CollectionUtils.isNotEmpty(codeQualityItemsIds)){
            codeQualityItemsIds.forEach(collectorItemId -> {

                CodeQuality codeQualities = codeQualityRepository.findTop1ByCollectorItemIdOrderByTimestampDesc(collectorItemId);
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
            codeQuality.put(STR_COVERAGE, componentLevelAverage(values));

        }else {
            codeQualityPercent.put("message", "CodeQuality not configured");
            codeQuality.put(STR_COVERAGE, codeQualityPercent);
        }

        return codeQuality;

    }

    /**
     *
     * @param perfItems
     * @return
     */
    private HashMap<String,HashMap<String,String>> getAggregatedErrorRate(List<ObjectId> perfItems){

        List<Double> values = new ArrayList();
        HashMap<String,HashMap<String,String>> perfErrorRate = new HashMap<>();
        HashMap<String,String> errorRatePercent = new HashMap<>();
        if(CollectionUtils.isNotEmpty(perfItems)){
            perfItems.forEach(collectorItemId -> {
                TestResult perfTestResult = testResultRepository.findTop1ByCollectorItemIdOrderByTimestampDesc(collectorItemId);
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

            perfErrorRate.put(STR_ERRORRATE ,componentLevelAverage(values));
        }else {
            errorRatePercent.put("message", "PerfItems not configured");
            perfErrorRate.put(STR_ERRORRATE, errorRatePercent);
        }
        return perfErrorRate;
    }

    /**
     *
     * @param testItems
     * @return
     */

    private HashMap<String,HashMap<String,String>> getAggregatedFeatureTestPass(List<ObjectId> testItems){
        List<Double> values = new ArrayList<>();
        HashMap<String,HashMap<String,String>> featureTestPass = new HashMap<>();
        HashMap<String,String> featureTestPercent = new HashMap<>();
        if(CollectionUtils.isNotEmpty(testItems)){
            testItems.forEach(collectorItemId -> {
                TestResult testResults = testResultRepository.findTop1ByCollectorItemIdOrderByTimestampDesc(collectorItemId);
                if((testResults != null) && (testResults.getType().equals(TestSuiteType.Functional))){

                    List<TestCapability> testCapabilities = testResults.getTestCapabilities().stream().collect(Collectors.toList());
                    Double featureTestPassing = featureTestPassCount(testCapabilities);
                    values.add(featureTestPassing);
                }
            });
            featureTestPass.put(STR_FEATURE_TEST_PASS, componentLevelAverage(values));
        }else {
            featureTestPercent.put("message","TestItems not configured");
            featureTestPass.put(STR_FEATURE_TEST_PASS,featureTestPercent);
        }

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

    private HashMap<String,String> componentLevelAverage(List<Double> values){
        HashMap<String,String> featureTestPercent = new HashMap<>();
        if(values.size() > 0) {
            DoubleSummaryStatistics stats = values.stream().mapToDouble((x) -> x).summaryStatistics();
            featureTestPercent.put(STR_PERCENTAGE, String.valueOf(df2.format(stats.getAverage())));
        }else {
            featureTestPercent.put("message", "NO DATA");
        }
        return featureTestPercent;
    }

    /**
     *
     * @param dashboard
     * @param widgetName
     * @param collectorType
     * @return
     */
    private List<ObjectId> getCollectorItems(Dashboard dashboard, String widgetName, CollectorType collectorType) {
        List<Widget> widgets = dashboard.getWidgets();
        ObjectId componentId = widgets.stream().filter(widget -> widget.getName().equalsIgnoreCase(widgetName)).findFirst().map(Widget::getComponentId).orElse(null);

        if (null == componentId) return null;

        com.capitalone.dashboard.model.Component component = componentRepository.findOne(componentId);

        List<CollectorItem> listFromComponent = component.getCollectorItems().get(collectorType);

        if (CollectionUtils.isEmpty(listFromComponent)) {
            return null;
        }

        List<ObjectId> ids = listFromComponent.stream().map(CollectorItem::getId).collect(Collectors.toList());
        return ids;
    }
}
