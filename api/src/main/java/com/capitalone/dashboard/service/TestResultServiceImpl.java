package com.capitalone.dashboard.service;

import com.capitalone.dashboard.misc.HygieiaException;
import com.capitalone.dashboard.model.*;
import com.capitalone.dashboard.repository.CollectorRepository;
import com.capitalone.dashboard.repository.ComponentRepository;
import com.capitalone.dashboard.repository.TestResultRepository;
import com.capitalone.dashboard.request.CollectorRequest;
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
        if (!component.getCollectorItems().containsKey(CollectorType.Test)) {
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
        for (CollectorItem item : component.getCollectorItems().get(CollectorType.Test)) {

            QTestResult testResult = new QTestResult("testResult");
            BooleanBuilder builder = new BooleanBuilder();

            builder.and(testResult.collectorItemId.eq(item.getId()));

            validateStartDateRange(request, testResult, builder);
            validateEndDateRange(request, testResult, builder);

            validateDurationRange(request, testResult, builder);

            validateTestCapabilities(request, testResult, builder);

            // add all test result repos
            addAllTestResultRepositories(request, result, testResult, builder);
        }
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
        for (TestResult result : results) {
            if (depth == 0) {
                result.getTestCapabilities().clear();
            } else {
                for (TestCapability testCapability : result.getTestCapabilities()) {
                    if (depth == 1) {
                        testCapability.getTestSuites().clear();
                    } else {
                        for (TestSuite testSuite : testCapability.getTestSuites()) {
                            if (depth == 2) {
                                testSuite.getTestCases().clear();
                            } else { // depth == 3
                                for (TestCase testCase : testSuite.getTestCases()) {
                                    testCase.getTestSteps().clear();
                                }
                            }
                        }
                    }
                }
            }
        }

        return results;
    }

    @Override
    public String create(TestDataCreateRequest request) throws HygieiaException {
        /**
         * Step 1: create Collector if not there
         * Step 2: create Collector item if not there
         * Step 3: Insert test data if new. If existing, update it
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

        return testResult.getId().toString();

    }

    private Collector createCollector() {
        CollectorRequest collectorReq = new CollectorRequest();
        collectorReq.setName("JenkinsCucumberTest");
        collectorReq.setCollectorType(CollectorType.Test);
        Collector col = collectorReq.toCollector();
        col.setEnabled(true);
        col.setOnline(true);
        col.setLastExecuted(System.currentTimeMillis());
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
        testResult.setTimestamp(request.getTimestamp());
        testResult.setTotalCount(request.getTotalCount());
        testResult.setUnknownStatusCount(request.getUnknownStatusCount());
        testResult.setUrl(request.getTestJobUrl());
        testResult.getTestCapabilities().addAll(request.getTestCapabilities());
        testResult.setBuildId(new ObjectId(request.getTestJobId()));

        return testResultRepository.save(testResult);
    }
}
