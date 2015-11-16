package com.capitalone.dashboard.service;

import com.capitalone.dashboard.model.CodeQuality;
import com.capitalone.dashboard.model.CodeQualityType;
import com.capitalone.dashboard.model.Collector;
import com.capitalone.dashboard.model.CollectorItem;
import com.capitalone.dashboard.model.Component;
import com.capitalone.dashboard.model.DataResponse;
import com.capitalone.dashboard.model.QCodeQuality;
import com.capitalone.dashboard.repository.CodeQualityRepository;
import com.capitalone.dashboard.repository.CollectorRepository;
import com.capitalone.dashboard.repository.ComponentRepository;
import com.capitalone.dashboard.request.CodeQualityRequest;
import com.google.common.base.Objects;
import com.google.common.collect.Iterables;
import com.mysema.query.BooleanBuilder;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

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
        if (request == null) {
            return emptyResponse();
        }

        if (request.getType() == null) { // return whole model
            // TODO: but the dataresponse needs changing.. the timestamp breaks this ability.
//            Iterable<CodeQuality> concatinatedResult = ImmutableList.of();
//            for (CodeQualityType type : CodeQualityType.values()) {
//                request.setType(type);
//                DataResponse<Iterable<CodeQuality>> result = searchType(request);
//                Iterables.concat(concatinatedResult, result.getResult());
//            }
            return emptyResponse();
        }

        return searchType(request);
    }

    protected DataResponse<Iterable<CodeQuality>> emptyResponse() {
        return new DataResponse<>(null, System.currentTimeMillis());
    }

    public DataResponse<Iterable<CodeQuality>> searchType(CodeQualityRequest request) {
        CollectorItem item = getCollectorItem(request);
        if (item == null) {
            return emptyResponse();
        }

        QCodeQuality quality = new QCodeQuality("quality");
        BooleanBuilder builder = new BooleanBuilder();

        builder.and(quality.collectorItemId.eq(item.getId()));

        if (request.getNumberOfDays() != null) {
            long endTimeTarget =
                    new LocalDate().minusDays(request.getNumberOfDays()).toDate().getTime();
            builder.and(quality.timestamp.goe(endTimeTarget));
        } else if (request.validDateRange()) {
            builder.and(quality.timestamp.between(request.getDateBegins(), request.getDateEnds()));
        }

        Iterable<CodeQuality> result;
        if (request.getMax() == null) {
            result = codeQualityRepository.findAll(builder.getValue(), quality.timestamp.desc());
        } else {
            PageRequest pageRequest =
                    new PageRequest(0, request.getMax(), Sort.Direction.DESC, "timestamp");
            result = codeQualityRepository.findAll(builder.getValue(), pageRequest).getContent();
        }

        Collector collector = collectorRepository.findOne(item.getCollectorId());
        return new DataResponse<>(result, collector.getLastExecuted());
    }

    protected CollectorItem getCollectorItem(CodeQualityRequest request) {
        CollectorItem item = null;
        Component component = componentRepository.findOne(request.getComponentId());

        CodeQualityType qualityType = Objects.firstNonNull(request.getType(),
                CodeQualityType.StaticAnalysis);
        List<CollectorItem> items = component.getCollectorItems().get(qualityType.collectorType());
        if (items != null) {
            item = Iterables.getFirst(items, null);
        }

        return item;
    }
}
