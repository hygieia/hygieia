package com.capitalone.dashboard.service;

import com.capitalone.dashboard.misc.HygieiaException;
import com.capitalone.dashboard.model.Collector;
import com.capitalone.dashboard.model.CollectorItem;
import com.capitalone.dashboard.model.CollectorType;
import com.capitalone.dashboard.model.Component;
import com.capitalone.dashboard.model.DataResponse;
import com.capitalone.dashboard.model.QTestResult;
import com.capitalone.dashboard.model.TestResult;
import com.capitalone.dashboard.repository.CollectorRepository;
import com.capitalone.dashboard.repository.ComponentRepository;
import com.capitalone.dashboard.repository.TestResultRepository;
import com.capitalone.dashboard.request.CollectorRequest;
import com.capitalone.dashboard.request.PerfTestDataCreateRequest;
import com.capitalone.dashboard.request.TestDataCreateRequest;
import com.capitalone.dashboard.request.TestResultRequest;
import com.google.common.collect.Lists;
import com.mysema.query.BooleanBuilder;
import org.apache.commons.lang.StringUtils;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TestResultServiceImpl implements TestResultService {

    private final TestResultRepository testResultRepository;
    private final ComponentRepository componentRepository;
    private final CollectorRepository collectorRepository;
    private final CollectorService collectorService;

    @Autowired
    public TestResultServiceImpl(TestResultRepository testResultRepository,
                                 ComponentRepository componentRepository,
                                 CollectorRepository collectorRepository,
                                 CollectorService collectorService) {
        this.testResultRepository = testResultRepository;
        this.componentRepository = componentRepository;
        this.collectorRepository = collectorRepository;
        this.collectorService = collectorService;
    }

    @Override
    public DataResponse<Iterable<TestResult>> search(TestResultRequest request) {
        Component component = componentRepository.findOne(request.getComponentId());

        if ((component == null) || !component.getCollectorItems().containsKey(CollectorType.Test)) {
            return new DataResponse<>(null, 0L);
        }
        List<TestResult> result = new ArrayList<>();
        validateAllCollectorItems(request, component, result);
        //One collector per Type. get(0) is hardcoded.
        if (!CollectionUtils.isEmpty(component.getCollectorItems().get(CollectorType.Test)) && (component.getCollectorItems().get(CollectorType.Test).get(0) != null)) {
            Collector collector = collectorRepository.findOne(component.getCollectorItems().get(CollectorType.Test).get(0).getCollectorId());
            if (collector != null) {
                return new DataResponse<>(pruneToDepth(result, request.getDepth()), collector.getLastExecuted());
            }
        }

        return new DataResponse<>(null, 0L);
    }



    private void validateAllCollectorItems(TestResultRequest request, Component component, List<TestResult> result) {
        // add all test result repos
        component.getCollectorItems().get(CollectorType.Test).forEach(item -> {
            QTestResult testResult = new QTestResult("testResult");
            BooleanBuilder builder = new BooleanBuilder();
            builder.and(testResult.collectorItemId.eq(item.getId()));
            validateStartDateRange(request, testResult, builder);
            validateEndDateRange(request, testResult, builder);
            validateDurationRange(request, testResult, builder);
            validateTestCapabilities(request, testResult, builder);
            addAllTestResultRepositories(request, result, testResult, builder);
        });
    }

    private void addAllTestResultRepositories(TestResultRequest request, List<TestResult> result, QTestResult testResult, BooleanBuilder builder) {
        if (request.getMax() == null) {
            result.addAll(Lists.newArrayList(testResultRepository.findAll(builder.getValue(), testResult.timestamp.desc())));
        } else {
            PageRequest pageRequest = new PageRequest(0, request.getMax(), Sort.Direction.DESC, "timestamp");
            result.addAll(Lists.newArrayList(testResultRepository.findAll(builder.getValue(), pageRequest).getContent()));
        }
    }

    private void validateTestCapabilities(TestResultRequest request, QTestResult testResult, BooleanBuilder builder) {
        if (!request.getTypes().isEmpty()) {
            builder.and(testResult.testCapabilities.any().type.in(request.getTypes()));
        }
    }

    private void validateDurationRange(TestResultRequest request, QTestResult testResult, BooleanBuilder builder) {
        if (request.validDurationRange()) {
            builder.and(testResult.duration.between(request.getDurationGreaterThan(), request.getDurationLessThan()));
        }
    }

    private void validateEndDateRange(TestResultRequest request, QTestResult testResult, BooleanBuilder builder) {
        if (request.validEndDateRange()) {
            builder.and(testResult.endTime.between(request.getEndDateBegins(), request.getEndDateEnds()));
        }
    }

    private void validateStartDateRange(TestResultRequest request, QTestResult testResult, BooleanBuilder builder) {
        if (request.validStartDateRange()) {
            builder.and(testResult.startTime.between(request.getStartDateBegins(), request.getStartDateEnds()));
        }
    }

    private Iterable<TestResult> pruneToDepth(List<TestResult> results, Integer depth) {
        // Prune the response to the requested depth
        // 0 - TestResult
        // 1 - TestCapability
        // 2 - TestSuite
        // 3 - TestCase
        // 4 - Entire response
        // null - Entire response
        if (depth == null || depth > 3) {
            return results;
        }
        results.forEach(result -> {
            if (depth == 0) {
                result.getTestCapabilities().clear();
            } else {
                result.getTestCapabilities().forEach(testCapability -> {
                    if (depth == 1) {
                        testCapability.getTestSuites().clear();
                    } else {
                        testCapability.getTestSuites().forEach(testSuite -> {
                            if (depth == 2) {
                                testSuite.getTestCases().clear();
                            } else { // depth == 3
                                testSuite.getTestCases().forEach(testCase -> {
                                    testCase.getTestSteps().clear();
                                });
                            }
                        });
                    }
                });
            }
        });

        return results;
    }


    protected TestResult createTest(TestDataCreateRequest request) throws HygieiaException {
        /*
          Step 1: create Collector if not there
          Step 2: create Collector item if not there
          Step 3: Insert test data if new. If existing, update it
         */
        Collector collector = createCollector();

        if (collector == null) {
            throw new HygieiaException("Failed creating Test collector.", HygieiaException.COLLECTOR_CREATE_ERROR);
        }

        CollectorItem collectorItem = createCollectorItem(collector, request);

        if (collectorItem == null) {
            throw new HygieiaException("Failed creating Test collector item.", HygieiaException.COLLECTOR_ITEM_CREATE_ERROR);
        }


        TestResult testResult = createTest(collectorItem, request);


        if (testResult == null) {
            throw new HygieiaException("Failed inserting/updating Test information.", HygieiaException.ERROR_INSERTING_DATA);
        }

        return testResult;

    }

    @Override
    public String create(TestDataCreateRequest request) throws HygieiaException {
        TestResult testResult = createTest(request);
        return testResult.getId().toString();
    }

    @Override
    public String createV2(TestDataCreateRequest request) throws HygieiaException {
        TestResult testResult = createTest(request);
        return testResult.getId().toString() + "," + testResult.getCollectorItemId().toString();
    }



    protected TestResult createPerfTest(PerfTestDataCreateRequest request) throws HygieiaException {
        /**
         * Step 1: create performance Collector if not there
         * Step 2: create Perfomance Collector item if not there
         * Step 3: Insert performance test data if new. If existing, update it
         */
        Collector collector = createGenericCollector(request.getPerfTool());
        if (collector == null) {
            throw new HygieiaException("Failed creating Test collector.", HygieiaException.COLLECTOR_CREATE_ERROR);
        }
        CollectorItem collectorItem = createGenericCollectorItem(collector, request);
        if (collectorItem == null) {
            throw new HygieiaException("Failed creating Test collector item.", HygieiaException.COLLECTOR_ITEM_CREATE_ERROR);
        }
        TestResult testResult = createPerfTest(collectorItem, request);
        if (testResult == null) {
            throw new HygieiaException("Failed inserting/updating Test information.", HygieiaException.ERROR_INSERTING_DATA);
        }
        return testResult;

    }

    @Override
    public String createPerf(PerfTestDataCreateRequest request) throws HygieiaException {
        TestResult testResult = createPerfTest(request);
        return testResult.getId().toString();
    }

    @Override
    public String createPerfV2(PerfTestDataCreateRequest request) throws HygieiaException {
        TestResult testResult = createPerfTest(request);
        return testResult.getId().toString() + "," + testResult.getCollectorItemId().toString();
    }

    private Collector createCollector() {
        CollectorRequest collectorReq = new CollectorRequest();
        collectorReq.setName("JenkinsCucumberTest");
        collectorReq.setCollectorType(CollectorType.Test);
        Collector col = collectorReq.toCollector();
        col.setEnabled(true);
        col.setOnline(true);
        col.setLastExecuted(System.currentTimeMillis());
        Map<String, Object> allOptions = new HashMap<>();
        allOptions.put("jobUrl", "");
        allOptions.put("instanceUrl", "");
        allOptions.put("jobName","");
        col.setAllFields(allOptions);
        //Combination of jobName and jobUrl should be unique always.
        Map<String, Object> uniqueOptions = new HashMap<>();
        uniqueOptions.put("jobUrl", "");
        uniqueOptions.put("jobName","");
        col.setUniqueFields(uniqueOptions);
        return collectorService.createCollector(col);
    }


    private Collector createGenericCollector(String performanceTool) {
        CollectorRequest collectorReq = new CollectorRequest();
        collectorReq.setName(performanceTool);
        collectorReq.setCollectorType(CollectorType.Test);
        Collector col = collectorReq.toCollector();
        col.setEnabled(true);
        col.setOnline(true);
        col.setLastExecuted(System.currentTimeMillis());
        Map<String, Object> allOptions = new HashMap<>();
        allOptions.put("jobName","");
        allOptions.put("instanceUrl", "");
        col.setAllFields(allOptions);
        col.setUniqueFields(allOptions);
        return collectorService.createCollector(col);
    }

    private CollectorItem createCollectorItem(Collector collector, TestDataCreateRequest request) throws HygieiaException {
        CollectorItem tempCi = new CollectorItem();
        tempCi.setCollectorId(collector.getId());
        tempCi.setDescription(request.getDescription());
        tempCi.setPushed(true);
        tempCi.setLastUpdated(System.currentTimeMillis());
        Map<String, Object> option = new HashMap<>();
        option.put("jobName", request.getTestJobName());
        option.put("jobUrl", request.getTestJobUrl());
        option.put("instanceUrl", request.getServerUrl());
        tempCi.getOptions().putAll(option);
        tempCi.setNiceName(request.getNiceName());
        if (StringUtils.isEmpty(tempCi.getNiceName())) {
            return collectorService.createCollectorItem(tempCi);
        }
        return collectorService.createCollectorItemByNiceNameAndJobName(tempCi, request.getTestJobName());
    }


    private CollectorItem createGenericCollectorItem(Collector collector, PerfTestDataCreateRequest request) {
        CollectorItem tempCi = new CollectorItem();
        tempCi.setCollectorId(collector.getId());
        tempCi.setDescription(request.getPerfTool()+" : "+request.getTestName());
        tempCi.setPushed(true);
        tempCi.setLastUpdated(System.currentTimeMillis());
        Map<String, Object> option = new HashMap<>();
        option.put("jobName", request.getTestName());
        option.put("instanceUrl", request.getInstanceUrl());
        tempCi.getOptions().putAll(option);
        tempCi.setNiceName(request.getPerfTool());
        return collectorService.createCollectorItem(tempCi);
    }


    private TestResult createTest(CollectorItem collectorItem, TestDataCreateRequest request) {
        TestResult testResult = testResultRepository.findByCollectorItemIdAndExecutionId(collectorItem.getId(),
                request.getExecutionId());
        if (testResult == null) {
            testResult = new TestResult();
        }

        testResult.setTargetAppName(request.getTargetAppName());
        testResult.setTargetEnvName(request.getTargetEnvName());
        testResult.setCollectorItemId(collectorItem.getId());
        testResult.setType(request.getType());
        testResult.setDescription(request.getDescription());
        testResult.setDuration(request.getDuration());
        testResult.setEndTime(request.getEndTime());
        testResult.setExecutionId(request.getExecutionId());
        testResult.setFailureCount(request.getFailureCount());
        testResult.setSkippedCount(request.getSkippedCount());
        testResult.setStartTime(request.getStartTime());
        testResult.setSuccessCount(request.getSuccessCount());
        if(request.getTimestamp() == 0) request.setTimestamp(System.currentTimeMillis());
        testResult.setTimestamp(request.getTimestamp());
        testResult.setTotalCount(request.getTotalCount());
        testResult.setUnknownStatusCount(request.getUnknownStatusCount());
        testResult.setUrl(request.getTestJobUrl());
        testResult.getTestCapabilities().addAll(request.getTestCapabilities());
        testResult.setBuildId(new ObjectId(request.getTestJobId()));

        return testResultRepository.save(testResult);
    }

    private TestResult createPerfTest(CollectorItem collectorItem, PerfTestDataCreateRequest request) {
        TestResult testResult = testResultRepository.findByCollectorItemIdAndExecutionId(collectorItem.getId(),
                request.getRunId());
        if (testResult == null) {
            testResult = new TestResult();
        }
        testResult.setTargetAppName(request.getTargetAppName());
        testResult.setTargetEnvName(request.getTargetEnvName());
        testResult.setCollectorItemId(collectorItem.getId());
        testResult.setType(request.getType());
        testResult.setDescription(request.getDescription());
        testResult.setDuration(request.getDuration());
        testResult.setEndTime(request.getEndTime());
        testResult.setExecutionId(request.getRunId());
        testResult.setFailureCount(request.getFailureCount());
        testResult.setSkippedCount(request.getSkippedCount());
        testResult.setStartTime(request.getStartTime());
        testResult.setSuccessCount(request.getSuccessCount());
        if(request.getTimestamp() == 0) request.setTimestamp(System.currentTimeMillis());
        testResult.setTimestamp(request.getTimestamp());
        testResult.setTotalCount(request.getTotalCount());
        testResult.setUnknownStatusCount(request.getUnknownStatusCount());
        testResult.setUrl(request.getReportUrl());
        testResult.getTestCapabilities().addAll(request.getTestCapabilities());
        testResult.setDescription(request.getDescription());
        testResult.setResultStatus(request.getResultStatus());
        return testResultRepository.save(testResult);
    }

}
