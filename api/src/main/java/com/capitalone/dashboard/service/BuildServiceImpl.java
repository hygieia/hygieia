package com.capitalone.dashboard.service;

import com.capitalone.dashboard.model.Build;
import com.capitalone.dashboard.model.BuildStatus;
import com.capitalone.dashboard.model.Collector;
import com.capitalone.dashboard.model.CollectorItem;
import com.capitalone.dashboard.model.CollectorType;
import com.capitalone.dashboard.model.Component;
import com.capitalone.dashboard.model.DataResponse;
import com.capitalone.dashboard.model.QBuild;
import com.capitalone.dashboard.repository.BuildRepository;
import com.capitalone.dashboard.repository.CollectorRepository;
import com.capitalone.dashboard.repository.ComponentRepository;
import com.capitalone.dashboard.request.BuildDataCreateRequest;
import com.capitalone.dashboard.request.BuildSearchRequest;
import com.capitalone.dashboard.request.CollectorRequest;
import com.mysema.query.BooleanBuilder;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

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
        CollectorItem item = component.getCollectorItems().get(CollectorType.Build).get(0);

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
        return new DataResponse<>(buildRepository.findAll(builder.getValue()), collector.getLastExecuted());
    }

    @Override
    public String create(BuildDataCreateRequest request) {
        /**
         * Step 1: create Collector if not there
         * Step 2: create Collector item if not there
         * Step 3: Insert build data if new. If existing, delete old one and insert new one.
         */
        Collector collector = createCollector();

        if (collector == null) {
            return "";
        }

        CollectorItem collectorItem = createCollectorItem(collector, request);

        if (collectorItem == null) {
            return "";
        }

        Build build = createBuild(collectorItem, request);

        if (build == null) {
            return "";
        }

        return build.getId().toString();

    }

    private Collector createCollector() {
        CollectorRequest collectorReq = new CollectorRequest();
        collectorReq.setName("Hudson");
        collectorReq.setCollectorType(CollectorType.Build);
        Collector collector = collectorService.createCollector(collectorReq.toCollector());
        return collector;
    }

    private CollectorItem createCollectorItem(Collector collector, BuildDataCreateRequest request) {
        CollectorItem tempCi = new CollectorItem();
        tempCi.setCollectorId(collector.getId());
        tempCi.setDescription(request.getJobName());
        Map<String, Object> option = new HashMap<>();
        option.put("jobName", request.getJobName());
        option.put("jobUrl", request.getJobUrl());
        option.put("instanceUrl", request.getInstanceUrl());
        tempCi.getOptions().putAll(option);
        CollectorItem collectorItem = collectorService.createCollectorItem(tempCi);
        return collectorItem;
    }

    private Build createBuild(CollectorItem collectorItem, BuildDataCreateRequest request) {
        Build build = new Build();
        build.setNumber(request.getNumber());
        build.setBuildUrl(request.getBuildUrl());
        build.setStartTime(request.getStartTime());
        build.setEndTime(request.getEndTime());
        build.setDuration(request.getDuration());
        build.setStartedBy(request.getStartedBy());
        build.setBuildStatus(BuildStatus.fromString(request.getBuildStatus()));
        build.setCollectorItemId(collectorItem.getId());
        build.setSourceChangeSet(request.getSourceChangeSet());
        Build existingBuild = buildRepository.findByCollectorItemIdAndNumber(collectorItem.getId(),
                build.getNumber());

        if (existingBuild != null) {
            buildRepository.delete(existingBuild);
        }

        return buildRepository.save(build);
    }
}
