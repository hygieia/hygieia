package com.capitalone.dashboard.service;

import com.capitalone.dashboard.misc.HygieiaException;
import com.capitalone.dashboard.model.Collector;
import com.capitalone.dashboard.model.CollectorItem;
import com.capitalone.dashboard.model.CollectorType;
import com.capitalone.dashboard.model.Component;
import com.capitalone.dashboard.model.DataResponse;
import com.capitalone.dashboard.model.Performance;
import com.capitalone.dashboard.model.PerformanceMetric;
import com.capitalone.dashboard.model.PerformanceType;
import com.capitalone.dashboard.model.QPerformance;
import com.capitalone.dashboard.repository.CollectorRepository;
import com.capitalone.dashboard.repository.ComponentRepository;
import com.capitalone.dashboard.repository.PerformanceRepository;
import com.capitalone.dashboard.request.CollectorRequest;
import com.capitalone.dashboard.request.PerformanceCreateRequest;
import com.capitalone.dashboard.request.PerformanceSearchRequest;
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
public class PerformanceServiceImpl implements PerformanceService {

    private final PerformanceRepository performanceRepository;
    private final ComponentRepository componentRepository;
    private final CollectorRepository collectorRepository;
    private final CollectorService collectorService;

    @Autowired
    public PerformanceServiceImpl(PerformanceRepository performanceRepository,
                                  ComponentRepository componentRepository,
                                  CollectorRepository collectorRepository,
                                  CollectorService collectorService) {
        this.performanceRepository = performanceRepository;
        this.componentRepository = componentRepository;
        this.collectorRepository = collectorRepository;
        this.collectorService = collectorService;
    }

    @Override
    public DataResponse<Iterable<Performance>> search(PerformanceSearchRequest request) {
        if (request == null) {
            return emptyResponse();
        }

        if (request.getType() == null) {
            return emptyResponse();
        }

        return searchType(request);
    }

    private DataResponse<Iterable<Performance>> emptyResponse() {
        return new DataResponse<>(null, System.currentTimeMillis());
    }

    private DataResponse<Iterable<Performance>> searchType(PerformanceSearchRequest request) {
        CollectorItem item = getCollectorItem(request);
        if (item == null) {
            return emptyResponse();
        }

        QPerformance performance = new QPerformance("performance");
        BooleanBuilder builder = new BooleanBuilder();

        builder.and(performance.collectorItemId.eq(item.getId()));

        if (request.getNumberOfDays() != null) {
            long endTimeTarget =
                    new LocalDate().minusDays(request.getNumberOfDays()).toDate().getTime();
            builder.and(performance.timestamp.goe(endTimeTarget));
        } else if (request.validDateRange()) {
            builder.and(performance.timestamp.between(request.getDateBegins(), request.getDateEnds()));
        }
        Iterable<Performance> result;
        if (request.getMax() == null) {
            result = performanceRepository.findAll(builder.getValue(), performance.timestamp.desc());
        } else {
            PageRequest pageRequest =
                    new PageRequest(0, request.getMax(), Sort.Direction.DESC, "timestamp");
            result = performanceRepository.findAll(builder.getValue(), pageRequest).getContent();
        }
        Collector collector = collectorRepository.findOne(item.getCollectorId());
        long lastExecuted = (collector == null) ? 0 : collector.getLastExecuted();
        return new DataResponse<>(result, lastExecuted);
    }

    protected CollectorItem getCollectorItem(PerformanceSearchRequest request) {
        CollectorItem item = null;
        Component component = componentRepository.findOne(request.getComponentId());

        PerformanceType qualityType = Objects.firstNonNull(request.getType(),
                PerformanceType.ApplicationPerformance);
        List<CollectorItem> items = component.getCollectorItems().get(qualityType.collectorType());
        if (items != null) {
            item = Iterables.getFirst(items, null);
        }
        return item;
    }


    @Override
    public String create(PerformanceCreateRequest request) throws HygieiaException {
        /**
         * Step 1: create Collector if not there
         * Step 2: create Collector item if not there
         * Step 3: Insert Quality data if new. If existing, update it.
         */
        Collector collector = createCollector(request.getCollectorName());

        if (collector == null) {
            throw new HygieiaException("Failed creating code quality collector.", HygieiaException.COLLECTOR_CREATE_ERROR);
        }

        CollectorItem collectorItem = createCollectorItem(collector, request);

        if (collectorItem == null) {
            throw new HygieiaException("Failed creating code quality collector item.", HygieiaException.COLLECTOR_ITEM_CREATE_ERROR);
        }

        Performance quality = createPerformance(collectorItem, request);

        if (quality == null) {
            throw new HygieiaException("Failed inserting/updating Quality information.", HygieiaException.ERROR_INSERTING_DATA);
        }

        return quality.getId().toString();

    }


    private Collector createCollector(String collectorName) {
        CollectorRequest collectorReq = new CollectorRequest();
        collectorReq.setName(collectorName);  //for now hardcode it.
        collectorReq.setCollectorType(CollectorType.CodeQuality);
        Collector col = collectorReq.toCollector();
        col.setEnabled(true);
        col.setOnline(true);
        col.setLastExecuted(System.currentTimeMillis());
        return collectorService.createCollector(col);
    }

    private CollectorItem createCollectorItem(Collector collector, PerformanceCreateRequest request) throws HygieiaException {
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

    private Performance createPerformance(CollectorItem collectorItem, PerformanceCreateRequest request) {
        Performance performance = performanceRepository.findByCollectorItemIdAndTimestamp(
                collectorItem.getId(), request.getTimestamp());
        if (performance == null) {
            performance = new Performance();
        }
        performance.setCollectorItemId(collectorItem.getId());
        performance.setExecutionId(new ObjectId(request.getHygieiaId()));
        performance.setType(PerformanceType.ApplicationPerformance);
        performance.setUrl(request.getProjectUrl());
        performance.setVersion(request.getProjectVersion());
        performance.setTimestamp(System.currentTimeMillis());
       // for (PerformanceMetric cm : request.getMetrics()) {
        //    performance.getMetrics().add(cm);
       // }

        return performanceRepository.save(performance); // Save = Update (if ID present) or Insert (if ID not there)
    }

}
