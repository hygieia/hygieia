package com.capitalone.dashboard.service;

import com.capitalone.dashboard.misc.HygieiaException;
import com.capitalone.dashboard.model.CodeQuality;
import com.capitalone.dashboard.model.CodeQualityMetric;
import com.capitalone.dashboard.model.CodeQualityType;
import com.capitalone.dashboard.model.Collector;
import com.capitalone.dashboard.model.CollectorItem;
import com.capitalone.dashboard.model.CollectorType;
import com.capitalone.dashboard.model.Component;
import com.capitalone.dashboard.model.DataResponse;
import com.capitalone.dashboard.model.QCodeQuality;
import com.capitalone.dashboard.repository.CodeQualityRepository;
import com.capitalone.dashboard.repository.CollectorRepository;
import com.capitalone.dashboard.repository.ComponentRepository;
import com.capitalone.dashboard.request.CodeQualityCreateRequest;
import com.capitalone.dashboard.request.CodeQualityRequest;
import com.capitalone.dashboard.request.CollectorRequest;
import com.google.common.base.Objects;
import com.google.common.collect.Iterables;
import com.mysema.query.BooleanBuilder;
import org.apache.commons.lang.StringUtils;
import org.bson.types.ObjectId;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CodeQualityServiceImpl implements CodeQualityService {

    private final CodeQualityRepository codeQualityRepository;
    private final ComponentRepository componentRepository;
    private final CollectorRepository collectorRepository;
    private final CollectorService collectorService;

    @Autowired
    public CodeQualityServiceImpl(CodeQualityRepository codeQualityRepository,
                                  ComponentRepository componentRepository,
                                  CollectorRepository collectorRepository,
                                  CollectorService collectorService) {
        this.codeQualityRepository = codeQualityRepository;
        this.componentRepository = componentRepository;
        this.collectorRepository = collectorRepository;
        this.collectorService = collectorService;
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
        long lastExecuted = (collector == null) ? 0 : collector.getLastExecuted();
        return new DataResponse<>(result, lastExecuted);
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


    @Override
    public String create(CodeQualityCreateRequest request) throws HygieiaException {
        /**
         * Step 1: create Collector if not there
         * Step 2: create Collector item if not there
         * Step 3: Insert Quality data if new. If existing, update it.
         */
        Collector collector = createCollector();

        if (collector == null) {
            throw new HygieiaException("Failed creating code quality collector.", HygieiaException.COLLECTOR_CREATE_ERROR);
        }

        CollectorItem collectorItem = createCollectorItem(collector, request);

        if (collectorItem == null) {
            throw new HygieiaException("Failed creating code quality collector item.", HygieiaException.COLLECTOR_ITEM_CREATE_ERROR);
        }

        CodeQuality quality = createCodeQuality(collectorItem, request);

        if (quality == null) {
            throw new HygieiaException("Failed inserting/updating Quality information.", HygieiaException.ERROR_INSERTING_DATA);
        }

        return quality.getId().toString();

    }


    private Collector createCollector() {
        CollectorRequest collectorReq = new CollectorRequest();
        collectorReq.setName("Sonar");  //for now hardcode it.
        collectorReq.setCollectorType(CollectorType.CodeQuality);
        Collector col = collectorReq.toCollector();
        col.setEnabled(true);
        col.setOnline(true);
        col.setLastExecuted(System.currentTimeMillis());
        return collectorService.createCollector(col);
    }

    private CollectorItem createCollectorItem(Collector collector, CodeQualityCreateRequest request) throws HygieiaException {
        CollectorItem tempCi = new CollectorItem();
        tempCi.setCollectorId(collector.getId());
        tempCi.setDescription(request.getProjectName());
        tempCi.setPushed(true);
        tempCi.setLastUpdated(System.currentTimeMillis());
        Map<String, Object> option = new HashMap<>();
        option.put("projectName", request.getProjectName());
        option.put("projectId", request.getProjectId());
        option.put("instanceUrl", request.getServerUrl());
        tempCi.getOptions().putAll(option);
        tempCi.setNiceName(request.getNiceName());

        if (StringUtils.isEmpty(tempCi.getNiceName())) {
            return collectorService.createCollectorItem(tempCi);
        }
        return collectorService.createCollectorItemByNiceNameAndProjectId(tempCi, request.getProjectId());
    }

    private CodeQuality createCodeQuality(CollectorItem collectorItem, CodeQualityCreateRequest request) {
        CodeQuality quality = codeQualityRepository.findByCollectorItemIdAndTimestamp(
                collectorItem.getId(), request.getTimestamp());
        if (quality == null) {
            quality = new CodeQuality();
        }
        quality.setCollectorItemId(collectorItem.getId());
        quality.setBuildId(new ObjectId(request.getHygieiaId()));
        quality.setName(request.getProjectName());
        quality.setType(CodeQualityType.StaticAnalysis);
        quality.setUrl(request.getProjectUrl());
        quality.setVersion(request.getProjectVersion());
        quality.setTimestamp(System.currentTimeMillis());
        for (CodeQualityMetric cm : request.getMetrics()) {
            quality.getMetrics().add(cm);
        }
        return codeQualityRepository.save(quality); // Save = Update (if ID present) or Insert (if ID not there)
    }

}
