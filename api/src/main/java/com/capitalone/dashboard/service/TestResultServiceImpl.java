package com.capitalone.dashboard.service;

import com.capitalone.dashboard.model.*;
import com.capitalone.dashboard.repository.CollectorRepository;
import com.capitalone.dashboard.repository.ComponentRepository;
import com.capitalone.dashboard.repository.TestResultRepository;
import com.capitalone.dashboard.request.TestResultRequest;
import com.mysema.query.BooleanBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import com.google.common.collect.Lists;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;

@Service
public class TestResultServiceImpl implements TestResultService {

    private final TestResultRepository testResultRepository;
    private final ComponentRepository componentRepository;
    private final CollectorRepository collectorRepository;

    @Autowired
    public TestResultServiceImpl(TestResultRepository testResultRepository,
                                 ComponentRepository componentRepository,
                                 CollectorRepository collectorRepository) {
        this.testResultRepository = testResultRepository;
        this.componentRepository = componentRepository;
        this.collectorRepository = collectorRepository;
    }

    @Override
    public DataResponse<Iterable<TestResult>> search(TestResultRequest request) {
        Component component = componentRepository.findOne(request.getComponentId());
        if (!component.getCollectorItems().containsKey(CollectorType.Test)) {
            return new DataResponse<>(null, 0L);
        }
        ArrayList<TestResult> result = new ArrayList<>();


        for (CollectorItem item : component.getCollectorItems().get(CollectorType.Test)) {

            QTestResult testResult = new QTestResult("testResult");
            BooleanBuilder builder = new BooleanBuilder();

            builder.and(testResult.collectorItemId.eq(item.getId()));

            if (request.validStartDateRange()) {
                builder.and(testResult.startTime.between(request.getStartDateBegins(), request.getStartDateEnds()));
            }
            if (request.validEndDateRange()) {
                builder.and(testResult.endTime.between(request.getEndDateBegins(), request.getEndDateEnds()));
            }

            if (request.validDurationRange()) {
                builder.and(testResult.duration.between(request.getDurationGreaterThan(), request.getDurationLessThan()));
            }

            if (!request.getTypes().isEmpty()) {
                builder.and(testResult.testCapabilities.any().type.in(request.getTypes()));
            }


            if (request.getMax() == null) {
                result.addAll(Lists.newArrayList(testResultRepository.findAll(builder.getValue(), testResult.timestamp.desc())));
            } else {
                PageRequest pageRequest = new PageRequest(0, request.getMax(), Sort.Direction.DESC, "timestamp");
                result.addAll(Lists.newArrayList(testResultRepository.findAll(builder.getValue(), pageRequest).getContent()));
            }
        }
        //One collector per Type. get(0) is hardcoded.
        if (!CollectionUtils.isEmpty(component.getCollectorItems().get(CollectorType.Test)) && (component.getCollectorItems().get(CollectorType.Test).get(0) != null)) {
            Collector collector = collectorRepository.findOne(component.getCollectorItems().get(CollectorType.Test).get(0).getCollectorId());
            if (collector != null) {
                return new DataResponse<>((Iterable<TestResult>) result, collector.getLastExecuted());
            }
        }

        return new DataResponse<>(null, 0L);
    }
}
