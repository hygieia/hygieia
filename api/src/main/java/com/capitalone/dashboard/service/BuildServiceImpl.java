package com.capitalone.dashboard.service;

import com.capitalone.dashboard.misc.HygieiaException;
import com.capitalone.dashboard.model.*;
import com.capitalone.dashboard.repository.BuildRepository;
import com.capitalone.dashboard.repository.CollectorRepository;
import com.capitalone.dashboard.repository.ComponentRepository;
import com.capitalone.dashboard.request.BuildDataCreateRequest;
import com.capitalone.dashboard.request.BuildSearchRequest;
import com.capitalone.dashboard.request.CollectorRequest;
import com.mysema.query.BooleanBuilder;
import org.apache.commons.lang.StringUtils;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class BuildServiceImpl implements BuildService {

    private final BuildRepository buildRepository;
    private final ComponentRepository componentRepository;
    private final CollectorRepository collectorRepository;
    private final CollectorService collectorService;


    @Autowired
    public BuildServiceImpl(BuildRepository buildRepository,
                            ComponentRepository componentRepository,
                            CollectorRepository collectorRepository,
                            CollectorService collectorService) {
        this.buildRepository = buildRepository;
        this.componentRepository = componentRepository;
        this.collectorRepository = collectorRepository;
        this.collectorService = collectorService;
    }

    @Override
    public DataResponse<Iterable<Build>> search(BuildSearchRequest request) {
        Component component = componentRepository.findOne(request.getComponentId());
        CollectorItem item = component.getFirstCollectorItemForType(CollectorType.Build);
        if (item == null) {
            Iterable<Build> results = new ArrayList<>();
            return new DataResponse<>(results, new Date().getTime());
        }

        QBuild build = new QBuild("build");
        BooleanBuilder builder = new BooleanBuilder();

        builder.and(build.collectorItemId.eq(item.getId()));

        if (request.getNumberOfDays() != null) {
            long endTimeTarget = new LocalDate().minusDays(request.getNumberOfDays()).toDate().getTime();
            builder.and(build.endTime.goe(endTimeTarget));
        } else {
            if (request.validStartDateRange()) {
                builder.and(build.startTime.between(request.getStartDateBegins(), request.getStartDateEnds()));
            }
            if (request.validEndDateRange()) {
                builder.and(build.endTime.between(request.getEndDateBegins(), request.getEndDateEnds()));
            }
        }
        if (request.validDurationRange()) {
            builder.and(build.duration.between(request.getDurationGreaterThan(), request.getDurationLessThan()));
        }

        if (!request.getBuildStatuses().isEmpty()) {
            builder.and(build.buildStatus.in(request.getBuildStatuses()));
        }

        Collector collector = collectorRepository.findOne(item.getCollectorId());

        Iterable<Build> result;
        if (request.getMax() == null) {
            result = buildRepository.findAll(builder.getValue());
        } else {
            PageRequest pageRequest = new PageRequest(0, request.getMax(), Sort.Direction.DESC, "timestamp");
            result = buildRepository.findAll(builder.getValue(), pageRequest).getContent();
        }

        return new DataResponse<>(result, collector.getLastExecuted());
    }

    @Override
    public String create(BuildDataCreateRequest request) throws HygieiaException {
        /**
         * Step 1: create Collector if not there
         * Step 2: create Collector item if not there
         * Step 3: Insert build data if new. If existing, update it.
         */
        Collector collector = createCollector();

        if (collector == null) {
            throw new HygieiaException("Failed creating Build collector.", HygieiaException.COLLECTOR_CREATE_ERROR);
        }

        CollectorItem collectorItem = createCollectorItem(collector, request);

        if (collectorItem == null) {
            throw new HygieiaException("Failed creating Build collector item.", HygieiaException.COLLECTOR_ITEM_CREATE_ERROR);
        }

        Build build = createBuild(collectorItem, request);

        if (build == null) {
            throw new HygieiaException("Failed inserting/updating build information.", HygieiaException.ERROR_INSERTING_DATA);
        }

        return build.getId().toString();

    }

    private Collector createCollector() {
        CollectorRequest collectorReq = new CollectorRequest();
        collectorReq.setName("Hudson");  //for now hardcode it.
        collectorReq.setCollectorType(CollectorType.Build);
        Collector col = collectorReq.toCollector();
        col.setEnabled(true);
        col.setOnline(true);
        col.setLastExecuted(System.currentTimeMillis());
        Map<String, Object> allOptions = new HashMap<>();
        allOptions.put("jobUrl", "");
        allOptions.put("instanceUrl", "");
        allOptions.put("jobName","");
        col.setAllFields(allOptions);
        col.setUniqueFields(allOptions);
        return collectorService.createCollector(col);
    }

    private CollectorItem createCollectorItem(Collector collector, BuildDataCreateRequest request) throws HygieiaException {
        CollectorItem tempCi = new CollectorItem();
        tempCi.setCollectorId(collector.getId());
        tempCi.setDescription(request.getJobName());
        tempCi.setPushed(true);
        tempCi.setLastUpdated(System.currentTimeMillis());
        Map<String, Object> option = new HashMap<>();
        option.put("jobName", request.getJobName());
        option.put("jobUrl", request.getJobUrl());
        option.put("instanceUrl", request.getInstanceUrl());
        tempCi.setNiceName(request.getNiceName());
        tempCi.getOptions().putAll(option);
        if (StringUtils.isEmpty(tempCi.getNiceName())) {
            return collectorService.createCollectorItem(tempCi);
        }
        return collectorService.createCollectorItemByNiceNameAndJobName(tempCi, request.getJobName());
    }

    private Build createBuild(CollectorItem collectorItem, BuildDataCreateRequest request) {
        Build build = buildRepository.findByCollectorItemIdAndNumber(collectorItem.getId(),
                request.getNumber());
        if (build == null) {
            build = new Build();
        }
        build.setNumber(request.getNumber());
        build.setBuildUrl(request.getBuildUrl());
        build.setStartTime(request.getStartTime());
        build.setEndTime(request.getEndTime());
        build.setDuration(request.getDuration());
        build.setStartedBy(request.getStartedBy());
        build.setBuildStatus(BuildStatus.fromString(request.getBuildStatus()));
        build.setCollectorItemId(collectorItem.getId());
        build.setSourceChangeSet(request.getSourceChangeSet());
        build.setTimestamp(System.currentTimeMillis());
        Set<RepoBranch> rbs = new HashSet<>();
        rbs.addAll(build.getCodeRepos());
        rbs.addAll(request.getCodeRepos());
        build.getCodeRepos().clear();
        build.getCodeRepos().addAll(rbs);
        return buildRepository.save(build); // Save = Update (if ID present) or Insert (if ID not there)
    }
}
