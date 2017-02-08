package com.capitalone.dashboard.service;

import com.capitalone.dashboard.model.Collector;
import com.capitalone.dashboard.model.CollectorItem;
import com.capitalone.dashboard.model.CollectorType;
import com.capitalone.dashboard.model.Pull;
import com.capitalone.dashboard.model.Component;
import com.capitalone.dashboard.model.DataResponse;
import com.capitalone.dashboard.model.QPull;
import com.capitalone.dashboard.repository.CollectorRepository;
import com.capitalone.dashboard.repository.PullRepository;
import com.capitalone.dashboard.repository.ComponentRepository;
import com.capitalone.dashboard.request.PullRequest;
import com.mysema.query.BooleanBuilder;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.ArrayList;
import java.util.StringTokenizer;

import static com.capitalone.dashboard.model.QCollector.collector;


@Service
public class PullServiceImpl implements PullService {

    private final PullRepository pullRepository;
    private final ComponentRepository componentRepository;
    private final CollectorRepository collectorRepository;
    //private final CollectorService collectorService;

    @Autowired
    public PullServiceImpl(PullRepository pullRepository,
                           ComponentRepository componentRepository,
                           CollectorRepository collectorRepository) {
        this.pullRepository = pullRepository;
        this.componentRepository = componentRepository;
        this.collectorRepository = collectorRepository;
        //this.collectorService = colllectorService;
    }

    @Override
    public DataResponse<Iterable<Pull>> search(PullRequest request) {
        QPull pull = new QPull("search");
        BooleanBuilder builder = new BooleanBuilder();

        Component component = componentRepository.findOne(request.getComponentId());
        CollectorItem item = component.getCollectorItems().get(CollectorType.SCM).get(0);
        builder.and(pull.collectorItemId.eq(item.getId()));
        if (request.getNumberOfDays() != null) {
            long endTimeTarget = new LocalDate().minusDays(request.getNumberOfDays()).toDate().getTime();
            builder.and(pull.timestamp.goe(endTimeTarget));
        }
        Collector collector = collectorRepository.findOne(item.getCollectorId());
        return new DataResponse<>(pullRepository.findAll(builder.getValue()), collector.getLastExecuted());
    }

    @Override
    public DataResponse<Iterable<Pull>> searchMerged(PullRequest request) {
        QPull pull = new QPull("searchMerged");
        BooleanBuilder builder = new BooleanBuilder();

        Component component = componentRepository.findOne(request.getComponentId());
        CollectorItem item = component.getCollectorItems().get(CollectorType.SCM).get(0);
        builder.and(pull.collectorItemId.eq(item.getId()));

        if (request.getNumberOfDays() != null) {
            long endTimeTarget = new LocalDate().minusDays(request.getNumberOfDays()).toDate().getTime();
            builder.and(pull.scmCommitTimestamp.goe(endTimeTarget));
        }
        builder.and(pull.mergedAt.isNotNull());
        Collector collector = collectorRepository.findOne(item.getCollectorId());
        return new DataResponse<>(pullRepository.findAll(builder.getValue()), collector.getLastExecuted());
    }
    @Override
    public DataResponse<Iterable<Pull>> searchOpen(PullRequest request) {
        QPull pull = new QPull("searchOpen");
        BooleanBuilder builder = new BooleanBuilder();

        Component component = componentRepository.findOne(request.getComponentId());
        CollectorItem item = component.getCollectorItems().get(CollectorType.SCM).get(0);
        builder.and(pull.collectorItemId.eq(item.getId()));

        if (request.getNumberOfDays() != null) {
            long endTimeTarget = new LocalDate().minusDays(request.getNumberOfDays()).toDate().getTime();
            builder.and(pull.timestamp.goe(endTimeTarget));
        }
        builder.and(pull.closedAt.isNull());

        Collector collector = collectorRepository.findOne(item.getCollectorId());
        return new DataResponse<>(pullRepository.findAll(builder.getValue()), collector.getLastExecuted());
    }
/*
    @Override
    public DataResponse<Iterable<Pull>> searchByOrgs(PullRequest request) {
        QPull pull = new QPull("searchByOrgs");
        BooleanBuilder builder = new BooleanBuilder();

        Component component = componentRepository.findOne(request.getComponentId());
        CollectorItem item = component.getCollectorItems().get(CollectorType.SCM).get(0);
        ArrayList orgs = new ArrayList();
        StringTokenizer tokens = new StringTokenizer((String )item.getOptions().get("url"),",");
        while (tokens.hasMoreTokens()) {
            String [] urls = tokens.nextToken().split("/");
            orgs.add(urls[3]);
        }
        builder.and(pull.orgName.in(orgs));

        if (request.getNumberOfDays() != null) {
            long endTimeTarget = new LocalDate().minusDays(request.getNumberOfDays()).toDate().getTime();
            builder.and(pull.timestamp.goe(endTimeTarget));
        }
        Collector collector = collectorRepository.findOne(item.getCollectorId());
        return new DataResponse<>(pullRepository.findAll(builder.getValue()), collector.getLastExecuted());
    }

    @Override
    public DataResponse<Iterable<Pull>> searchMergedByOrgs(PullRequest request) {
        QPull pull = new QPull("searchMergedByOrgs");
        BooleanBuilder builder = new BooleanBuilder();

        Component component = componentRepository.findOne(request.getComponentId());
        CollectorItem item = component.getCollectorItems().get(CollectorType.SCM).get(0);
        ArrayList orgs = new ArrayList();
        StringTokenizer tokens = new StringTokenizer((String )item.getOptions().get("url"),",");
        while (tokens.hasMoreTokens()) {
            String [] urls = tokens.nextToken().split("/");
            orgs.add(urls[3]);
        }
        builder.and(pull.orgName.in(orgs));
        if (request.getNumberOfDays() != null) {
            long endTimeTarget = new LocalDate().minusDays(request.getNumberOfDays()).toDate().getTime();
            builder.and(pull.scmCommitTimestamp.goe(endTimeTarget));
        }
        builder.and(pull.mergedAt.isNotNull());
        Collector collector = collectorRepository.findOne(item.getCollectorId());
        return new DataResponse<>(pullRepository.findAll(builder.getValue()), collector.getLastExecuted());
    }

    @Override
    public DataResponse<Iterable<Pull>> searchOpenByOrgs(PullRequest request) {
        QPull pull = new QPull("searchOpenByOrgs");
        BooleanBuilder builder = new BooleanBuilder();

        Component component = componentRepository.findOne(request.getComponentId());
        CollectorItem item = component.getCollectorItems().get(CollectorType.SCM).get(0);
        ArrayList orgs = new ArrayList();
        StringTokenizer tokens = new StringTokenizer((String )item.getOptions().get("url"),",");
        while (tokens.hasMoreTokens()) {
            String [] urls = tokens.nextToken().split("/");
            orgs.add(urls[3]);
        }
        builder.and(pull.orgName.in(orgs));
        if (request.getNumberOfDays() != null) {
            long endTimeTarget = new LocalDate().minusDays(request.getNumberOfDays()).toDate().getTime();
            builder.and(pull.timestamp.goe(endTimeTarget));
        }
        builder.and(pull.closedAt.isNull());

        Collector collector = collectorRepository.findOne(item.getCollectorId());
        return new DataResponse<>(pullRepository.findAll(builder.getValue()), collector.getLastExecuted());
    }
    */
}
