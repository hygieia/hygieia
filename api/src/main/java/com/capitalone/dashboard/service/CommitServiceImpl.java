package com.capitalone.dashboard.service;

import com.capitalone.dashboard.model.*;
import com.capitalone.dashboard.repository.CollectorRepository;
import com.capitalone.dashboard.repository.CommitRepository;
import com.capitalone.dashboard.repository.ComponentRepository;
import com.capitalone.dashboard.request.CommitRequest;
import com.mysema.query.BooleanBuilder;
import org.apache.commons.lang.StringUtils;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CommitServiceImpl implements CommitService {

    private final CommitRepository commitRepository;
    private final ComponentRepository componentRepository;
    private final CollectorRepository collectorRepository;

    @Autowired
    public CommitServiceImpl(CommitRepository commitRepository,
                             ComponentRepository componentRepository,
                             CollectorRepository collectorRepository) {
        this.commitRepository = commitRepository;
        this.componentRepository = componentRepository;
        this.collectorRepository = collectorRepository;
    }

    @Override
    public DataResponse<Iterable<Commit>> search(CommitRequest request) {
        QCommit commit = new QCommit("commit");
        BooleanBuilder builder = new BooleanBuilder();

        Component component = componentRepository.findOne(request.getComponentId());
        CollectorItem item = component.getCollectorItems().get(CollectorType.SCM).get(0);
        builder.and(commit.collectorItemId.eq(item.getId()));

        if (request.getNumberOfDays() != null) {
            long endTimeTarget = new LocalDate().minusDays(request.getNumberOfDays()).toDate().getTime();
            builder.and(commit.scmCommitTimestamp.goe(endTimeTarget));
        } else if (request.validCommitDateRange()) {
            builder.and(commit.scmCommitTimestamp.between(request.getCommitDateBegins(), request.getCommitDateEnds()));
        }

        if (request.validChangesRange()) {
            builder.and(commit.numberOfChanges.between(request.getChangesGreaterThan(), request.getChangesLessThan()));
        }

        if (!request.getRevisionNumbers().isEmpty()) {
            builder.and(commit.scmRevisionNumber.in(request.getRevisionNumbers()));
        }

        if (!request.getAuthors().isEmpty()) {
            builder.and(commit.scmAuthor.in(request.getAuthors()));
        }

        if (StringUtils.isNotBlank(request.getMessageContains())) {
            builder.and(commit.scmCommitLog.contains(request.getMessageContains()));
        }

        Collector collector = collectorRepository.findOne(item.getCollectorId());
        return new DataResponse<>(commitRepository.findAll(builder.getValue()), collector.getLastExecuted());
    }
}
