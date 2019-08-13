package com.capitalone.dashboard.service;

import com.capitalone.dashboard.ApiSettings;
import com.capitalone.dashboard.model.FeatureMetrics;
import com.capitalone.dashboard.model.Feature;
import com.capitalone.dashboard.model.Dashboard;
import com.capitalone.dashboard.model.DashboardType;
import com.capitalone.dashboard.model.ProductFeatureMetrics;
import com.capitalone.dashboard.model.Cmdb;
import com.capitalone.dashboard.model.CodeQuality;
import com.capitalone.dashboard.model.CollectorItem;
import com.capitalone.dashboard.model.TestResult;
import com.capitalone.dashboard.model.TestSuiteType;
import com.capitalone.dashboard.model.TestCase;
import com.capitalone.dashboard.model.TestCapability;
import com.capitalone.dashboard.model.LobFeatureMetrics;
import com.capitalone.dashboard.model.ExecutiveFeatureMetrics;
import com.capitalone.dashboard.model.CollectorType;
import com.capitalone.dashboard.model.Build;
import com.capitalone.dashboard.model.BuildStage;
import com.capitalone.dashboard.model.Widget;
import com.capitalone.dashboard.model.TestSuite;
import com.capitalone.dashboard.model.CodeQualityMetric;

import com.capitalone.dashboard.repository.TestResultRepository;
import com.capitalone.dashboard.repository.CmdbRepository;
import com.capitalone.dashboard.repository.CodeQualityRepository;
import com.capitalone.dashboard.repository.DashboardRepository;
import com.capitalone.dashboard.repository.BuildRepository;
import com.capitalone.dashboard.repository.FeatureRepository;
import com.capitalone.dashboard.repository.ComponentRepository;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang3.StringUtils;
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
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Component
public class FeatureMetricServiceImpl implements FeatureMetricsService {

    private static final String STR_COVERAGE = "CODE_COVERAGE";

    private static final String STR_ERRORRATE = "PERF_ERROR_RATE";

    private static final String STR_FEATURE_TEST_PASS = "FEATURE_TEST_PASS";

    private static final String STR_TRACEABILITY = "TRACEABILITY";

    private static final String STR_DEPLOY_SCRIPTS = "DEPLOY_SCRIPTS";

    private static final String STR_PERCENTAGE = "percentage";

    private static final String STR_ACTUAL_ERRORRATE = "Actual Error Rate";

    private static final String STR_KPI_ERRORRATE = "KPI : Error Rate Threshold";

    private static final String STR_MTR_COVERAGE = "coverage";

    private static final String STR_WIDGETNAME = "codeanalysis";

    private static final String STR_WIDGET_BUILD = "build";

    private static final String WIDGET_FEATURE = "feature";

    private static final String STR_TEAM_ID = "teamId";

    private static final String SUCCESS = "success";

    private static final String FAILED = "failed";

    private static final String STR_UNDERSCORE = "_";

    private static final String STR_HYPHEN = "-";

    private static final String STR_AT = "@";

    private static final String STR_EMPTY = "";


    private final DashboardRepository dashboardRepository;

    private final ComponentRepository componentRepository;

    private final CodeQualityRepository codeQualityRepository;

    private final TestResultRepository testResultRepository;

    private final CmdbRepository cmdbRepository;

    private final FeatureRepository featureRepository;

    private final BuildRepository buildRepository;

    private final ApiSettings apiSettings;

    private static DecimalFormat df2 = new DecimalFormat("#.##");


    @Autowired
    public FeatureMetricServiceImpl(DashboardRepository dashboardRepository, ComponentRepository componentRepository, CodeQualityRepository codeQualityRepository, TestResultRepository testResultRepository, CmdbRepository cmdbRepository, FeatureRepository featureRepository, BuildRepository buildRepository, ApiSettings apiSettings) {
        this.dashboardRepository = dashboardRepository;
        this.componentRepository = componentRepository;
        this.codeQualityRepository = codeQualityRepository;
        this.testResultRepository = testResultRepository;
        this.cmdbRepository = cmdbRepository;
        this.featureRepository = featureRepository;
        this.buildRepository = buildRepository;
        this.apiSettings = apiSettings;
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
            featureMetrics.setMetrics(metrics);
        }else{
            featureMetrics.setName(name);
            featureMetrics.setMessage("Component is not configured");
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

    /**
     *
     * @param cmdb
     * @param type
     * @return
     */
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
            List<ObjectId> buildItems = getCollectorItems(dashboard, STR_WIDGET_BUILD, CollectorType.Build);
            HashMap<String, HashMap<String, String>> codeQuality = getAggregatedCodeQuality(codeQualityItems);
            HashMap<String, HashMap<String, String>> errorRate = getAggregatedErrorRate(perfItems);
            HashMap<String, HashMap<String, String>> featureTest = getAggregatedFeatureTestPass(testItems);
            HashMap<String, HashMap<String, String>> traceability = getAggregatedTraceability(testItems,dashboard);
            HashMap<String, HashMap<String, String>> deployScripts = getAggregatedDeployScripts(buildItems,dashboard);

            metrics.add(codeQuality);
            metrics.add(errorRate);
            metrics.add(featureTest);
            metrics.add(traceability);
            metrics.add(deployScripts);
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
        } else if (STR_TRACEABILITY.equalsIgnoreCase(type)) {
            List<ObjectId> testItems = getCollectorItems(dashboard, STR_WIDGETNAME, CollectorType.Test);
            HashMap<String, HashMap<String, String>> traceability = getAggregatedTraceability(testItems,dashboard);
            metrics.add(traceability);
        }else if(STR_DEPLOY_SCRIPTS.equalsIgnoreCase(type)){
            List<ObjectId> buildItems = getCollectorItems(dashboard, STR_WIDGET_BUILD, CollectorType.Build);
            HashMap<String, HashMap<String, String>> deployScripts = getAggregatedDeployScripts(buildItems,dashboard);
            metrics.add(deployScripts);
        }

        return metrics;
    }

    private HashMap<String,HashMap<String,String>> getAggregatedDeployScripts(List<ObjectId> buildItems, Dashboard dashboard) {
        List<Double> values = new ArrayList();
        HashMap<String,HashMap<String,String>> deployScripts = new HashMap<>();
        HashMap<String,String> deployScriptsPercentage = new HashMap<>();

        if(CollectionUtils.isNotEmpty(buildItems)){
            buildItems.forEach(collectorItemId -> {

                Build build = buildRepository.findTop1ByCollectorItemIdOrderByTimestampDesc(collectorItemId);
                if(build != null){
                    List<BuildStage> stages = build.getStages();
                    if (matchStage(stages, SUCCESS, apiSettings)) {
                        values.add(100.0);
                    } else if (matchStage(stages, FAILED, apiSettings)) {
                        values.add(0.0);
                    }else{
                        values.add(0.0);
                    }

                }
            });
            deployScripts.put(STR_DEPLOY_SCRIPTS, componentLevelAverage(values));

        }else {
            deployScriptsPercentage.put("message", "BuildItems not configured");
            deployScripts.put(STR_DEPLOY_SCRIPTS, deployScriptsPercentage);
        }
        return deployScripts;
    }

    public boolean matchStage(List<BuildStage> stages, String status, ApiSettings settings) {
        if (StringUtils.isEmpty(settings.getBuildStageRegEx())) return false;
        return stages.stream().filter(s -> Pattern.compile(settings.getBuildStageRegEx()).matcher(s.getName()).find() && s.getStatus().equalsIgnoreCase(status)).findAny().isPresent();
    }

    private HashMap<String,HashMap<String,String>> getAggregatedTraceability(List<ObjectId> testItems,Dashboard dashboard) {
        List<Double> values = new ArrayList<>();
        HashMap<String,HashMap<String,String>> traceability = new HashMap<>();
        HashMap<String,String> featureTestPercent = new HashMap<>();
        Widget featureWidget = getFeatureWidget(dashboard);
        Optional<Object> teamIdOpt = Optional.ofNullable(featureWidget.getOptions().get(STR_TEAM_ID));

        List<String> totalCompletedList = new ArrayList<>();

            if(CollectionUtils.isNotEmpty(testItems)){
                if(teamIdOpt.isPresent()){
                List<Feature> featureList = featureRepository.getStoryByTeamID(teamIdOpt.get().toString());
                if(CollectionUtils.isNotEmpty(featureList))
                featureList.stream().forEach(feature -> {
                    if(this.isValidStoryStatus(feature.getsStatus())){
                        totalCompletedList.add(feature.getsNumber());}});}
                testItems.forEach(collectorItemId -> {
                    TestResult testResults = testResultRepository.findTop1ByCollectorItemIdOrderByTimestampDesc(collectorItemId);
                    if((testResults != null) && (testResults.getType().equals(TestSuiteType.Functional))){

                         values.add((double) getTotalStoryIndicators(testResults).size());
                    }
                });
                if(totalCompletedList.size()> NumberUtils.INTEGER_ZERO) {
                    double traceabilityPercentage = (values.size() * 100) / totalCompletedList.size();
                    featureTestPercent.put(STR_PERCENTAGE, String.valueOf(df2.format(traceabilityPercentage)));
                    traceability.put(STR_TRACEABILITY, featureTestPercent);
                }else {
                    featureTestPercent.put("message", "Traceability Not Found");
                    traceability.put(STR_TRACEABILITY, featureTestPercent);
                }
            }else {
                featureTestPercent.put("message","TestItems not configured");
                traceability.put(STR_TRACEABILITY,featureTestPercent);
            }
        return traceability;
    }

    private  List<String> getTotalStoryIndicators(TestResult testResult) {

        Pattern featureIdPattern = Pattern.compile(apiSettings.getFeatureIDPattern());
        List<String> totalStoryIndicatorList = new ArrayList<>();
        testResult.getTestCapabilities().stream()
                .map(TestCapability::getTestSuites).flatMap(Collection::stream)
                .map(TestSuite::getTestCases).flatMap(Collection::stream)
                .forEach(testCase -> {
                    List<String> storyList = new ArrayList<>();
                    testCase.getTags().forEach(tag -> {
                        if (featureIdPattern.matcher(getValidFeatureId(tag)).find()) {
                            List<Feature> features = featureRepository.getStoryByNumber(tag);
                            features.forEach(feature -> {
                                if (isValidStoryStatus(feature.getsStatus())) {
                                    storyList.add(feature.getsNumber());
                                }
                            });
                        }
                    });
                    storyList.forEach(storyIndicator -> {
                        if (!totalStoryIndicatorList.contains(storyIndicator)) {
                            totalStoryIndicatorList.add(storyIndicator);
                        }
                    });
                });
        return totalStoryIndicatorList;
    }

    private CharSequence getValidFeatureId(String tag) {
        tag = tag.replaceAll(STR_UNDERSCORE, STR_HYPHEN).replaceAll(STR_AT, STR_EMPTY);
        return tag;
    }

    public Widget getFeatureWidget(Dashboard dashboard) {
        return dashboard.getWidgets()
                .stream()
                .filter(widget -> widget.getName().equalsIgnoreCase(WIDGET_FEATURE))
                .findFirst().orElse(new Widget());
    }

    private boolean isValidStoryStatus(String storyStatus) {
        final List<String> validStatus = apiSettings.getValidStoryStatus();
        return validStatus.contains(storyStatus.toUpperCase());
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
                    Set<CodeQualityMetric> javaCollection = codeQualities.getMetrics();
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

    /**
     *
     * @param values
     * @return
     */
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
