package com.capitalone.dashboard.service;

import com.capitalone.dashboard.model.*;
import com.capitalone.dashboard.repository.BuildRepository;
import com.capitalone.dashboard.repository.CollectorRepository;
import com.capitalone.dashboard.repository.ComponentRepository;
import com.capitalone.dashboard.request.BuildRequest;
import com.mysema.query.BooleanBuilder;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BuildServiceImpl implements BuildService {

    private final BuildRepository buildRepository;
    private final ComponentRepository componentRepository;
    private final CollectorRepository collectorRepository;

    @Autowired
    public BuildServiceImpl(BuildRepository buildRepository,
                            ComponentRepository componentRepository,
                            CollectorRepository collectorRepository) {
        this.buildRepository = buildRepository;
        this.componentRepository = componentRepository;
        this.collectorRepository = collectorRepository;
    }

    @Override
    public DataResponse<Iterable<Build>> search(BuildRequest request) {
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
}
