package com.capitalone.dashboard.service;

import com.capitalone.dashboard.model.*;
import com.capitalone.dashboard.repository.CodeQualityRepository;
import com.capitalone.dashboard.repository.CollectorRepository;
import com.capitalone.dashboard.repository.ComponentRepository;
import com.capitalone.dashboard.request.CodeQualityRequest;
import com.mysema.query.BooleanBuilder;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class CodeQualityServiceImpl implements CodeQualityService {

    private final CodeQualityRepository codeQualityRepository;
    private final ComponentRepository componentRepository;
    private final CollectorRepository collectorRepository;

    @Autowired
    public CodeQualityServiceImpl(CodeQualityRepository codeQualityRepository,
                                  ComponentRepository componentRepository,
                                  CollectorRepository collectorRepository) {
        this.codeQualityRepository = codeQualityRepository;
        this.componentRepository = componentRepository;
        this.collectorRepository = collectorRepository;
    }

    @Override
    public DataResponse<Iterable<CodeQuality>> search(CodeQualityRequest request) {
        Component component = componentRepository.findOne(request.getComponentId());
        CollectorItem item = component.getCollectorItems().get(CollectorType.CodeQuality).get(0);

        QCodeQuality quality = new QCodeQuality("quality");
        BooleanBuilder builder = new BooleanBuilder();

        builder.and(quality.collectorItemId.eq(item.getId()));

        if (request.getNumberOfDays() != null) {
            long endTimeTarget = new LocalDate().minusDays(request.getNumberOfDays()).toDate().getTime();
            builder.and(quality.timestamp.goe(endTimeTarget));
        } else {
            if (request.validDateRange()) {
                builder.and(quality.timestamp.between(request.getDateBegins(), request.getDateEnds()));
            }
        }

        Iterable<CodeQuality> result;
        if (request.getMax() == null) {
            result = codeQualityRepository.findAll(builder.getValue(), quality.timestamp.desc());
        } else {
            PageRequest pageRequest = new PageRequest(0, request.getMax(), Sort.Direction.DESC, "timestamp");
            result = codeQualityRepository.findAll(builder.getValue(), pageRequest).getContent();
        }

        Collector collector = collectorRepository.findOne(item.getCollectorId());
        return new DataResponse<>(result, collector.getLastExecuted());
    }
}
