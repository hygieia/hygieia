package com.capitalone.dashboard.service;

import com.capitalone.dashboard.model.Collector;
import com.capitalone.dashboard.model.CollectorItem;
import com.capitalone.dashboard.model.CollectorType;
import com.capitalone.dashboard.model.Issue;
import com.capitalone.dashboard.model.Component;
import com.capitalone.dashboard.model.DataResponse;
import com.capitalone.dashboard.model.QIssue;
import com.capitalone.dashboard.repository.CollectorRepository;
import com.capitalone.dashboard.repository.IssueRepository;
import com.capitalone.dashboard.repository.ComponentRepository;
import com.capitalone.dashboard.request.IssueRequest;
import com.mysema.query.BooleanBuilder;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.ArrayList;
import java.util.StringTokenizer;

import static com.capitalone.dashboard.model.QPull.pull;


@Service
public class IssueServiceImpl implements IssueService {

    private final IssueRepository issueRepository;
    private final ComponentRepository componentRepository;
    private final CollectorRepository collectorRepository;
    //private final CollectorService collectorService;

    @Autowired
    public IssueServiceImpl(IssueRepository issueRepository,
                            ComponentRepository componentRepository,
                            CollectorRepository collectorRepository
    ) {
        this.issueRepository = issueRepository;
        this.componentRepository = componentRepository;
        this.collectorRepository = collectorRepository;
        //this.collectorService = colllectorService;
    }

    @Override
    public DataResponse<Iterable<Issue>> search(IssueRequest request) {
        QIssue issue = new QIssue("search");
        BooleanBuilder builder = new BooleanBuilder();

        Component component = componentRepository.findOne(request.getComponentId());
        CollectorItem item = component.getCollectorItems().get(CollectorType.SCM).get(0);
        builder.and(issue.collectorItemId.eq(item.getId()));

        if (request.getNumberOfDays() != null) {
            long endTimeTarget = new LocalDate().minusDays(request.getNumberOfDays()).toDate().getTime();
            builder.and(issue.timestamp.goe(endTimeTarget));
        }
        Collector collector = collectorRepository.findOne(item.getCollectorId());
        return new DataResponse<>(issueRepository.findAll(builder.getValue()), collector.getLastExecuted());
    }

    @Override
    public DataResponse<Iterable<Issue>> searchClosed(IssueRequest request) {
        QIssue issue = new QIssue("searchClosed");
        BooleanBuilder builder = new BooleanBuilder();

        Component component = componentRepository.findOne(request.getComponentId());
        CollectorItem item = component.getCollectorItems().get(CollectorType.SCM).get(0);
        builder.and(issue.collectorItemId.eq(item.getId()));

        if (request.getNumberOfDays() != null) {
            long endTimeTarget = new LocalDate().minusDays(request.getNumberOfDays()).toDate().getTime();
            builder.and(issue.scmCommitTimestamp.goe(endTimeTarget));
        }
        builder.and(issue.closedAt.isNotNull());

        Collector collector = collectorRepository.findOne(item.getCollectorId());
        return new DataResponse<>(issueRepository.findAll(builder.getValue()), collector.getLastExecuted());
    }

    @Override
    public DataResponse<Iterable<Issue>> searchOpen(IssueRequest request) {
        QIssue issue = new QIssue("searchOpen");
        BooleanBuilder builder = new BooleanBuilder();

        Component component = componentRepository.findOne(request.getComponentId());
        CollectorItem item = component.getCollectorItems().get(CollectorType.SCM).get(0);
        builder.and(issue.collectorItemId.eq(item.getId()));
        builder.and(issue.closedAt.isNull());

        if (request.getNumberOfDays() != null) {
            long endTimeTarget = new LocalDate().minusDays(request.getNumberOfDays()).toDate().getTime();
            builder.and(issue.timestamp.goe(endTimeTarget));
        }
        Collector collector = collectorRepository.findOne(item.getCollectorId());
        return new DataResponse<>(issueRepository.findAll(builder.getValue()), collector.getLastExecuted());
    }

/*
    @Override
    public DataResponse<Iterable<Issue>> searchByOrgs(IssueRequest request) {
        QIssue issue = new QIssue("searchByOrgs");
        BooleanBuilder builder = new BooleanBuilder();

        Component component = componentRepository.findOne(request.getComponentId());
        CollectorItem item = component.getCollectorItems().get(CollectorType.SCM).get(0);

        ArrayList orgs = new ArrayList();
        StringTokenizer tokens = new StringTokenizer((String )item.getOptions().get("url"),",");
        while (tokens.hasMoreTokens()) {
            String [] urls = tokens.nextToken().split("/");
            orgs.add(urls[3]);
        }
        builder.and(issue.orgName.in(orgs));

        if (request.getNumberOfDays() != null) {
            long endTimeTarget = new LocalDate().minusDays(request.getNumberOfDays()).toDate().getTime();
            builder.and(issue.timestamp.goe(endTimeTarget));
        }
        Collector collector = collectorRepository.findOne(item.getCollectorId());
        return new DataResponse<>(issueRepository.findAll(builder.getValue()), collector.getLastExecuted());
    }

    @Override
    public DataResponse<Iterable<Issue>> searchClosedByOrgs(IssueRequest request) {
        QIssue issue = new QIssue("searchClosedByOrgs");
        BooleanBuilder builder = new BooleanBuilder();

        Component component = componentRepository.findOne(request.getComponentId());
        CollectorItem item = component.getCollectorItems().get(CollectorType.SCM).get(0);
        ArrayList orgs = new ArrayList();
        StringTokenizer tokens = new StringTokenizer((String )item.getOptions().get("url"),",");
        while (tokens.hasMoreTokens()) {
            String [] urls = tokens.nextToken().split("/");
            orgs.add(urls[3]);
        }
        builder.and(issue.orgName.in(orgs));
        if (request.getNumberOfDays() != null) {
            long endTimeTarget = new LocalDate().minusDays(request.getNumberOfDays()).toDate().getTime();
            builder.and(issue.scmCommitTimestamp.goe(endTimeTarget));
        }
        builder.and(issue.closedAt.isNotNull());
        Collector collector = collectorRepository.findOne(item.getCollectorId());
        return new DataResponse<>(issueRepository.findAll(builder.getValue()), collector.getLastExecuted());
    }
    @Override
    public DataResponse<Iterable<Issue>> searchOpenByOrgs(IssueRequest request) {
        QIssue issue = new QIssue("searchOpenByOrgs");
        BooleanBuilder builder = new BooleanBuilder();

        Component component = componentRepository.findOne(request.getComponentId());
        CollectorItem item = component.getCollectorItems().get(CollectorType.SCM).get(0);
        ArrayList orgs = new ArrayList();
        StringTokenizer tokens = new StringTokenizer((String )item.getOptions().get("url"),",");
        while (tokens.hasMoreTokens()) {
            String [] urls = tokens.nextToken().split("/");
            orgs.add(urls[3]);
        }
        builder.and(issue.orgName.in(orgs));
        builder.and(issue.closedAt.isNull());

        if (request.getNumberOfDays() != null) {
            long endTimeTarget = new LocalDate().minusDays(request.getNumberOfDays()).toDate().getTime();
            builder.and(issue.timestamp.goe(endTimeTarget));
        }
        Collector collector = collectorRepository.findOne(item.getCollectorId());
        return new DataResponse<>(issueRepository.findAll(builder.getValue()), collector.getLastExecuted());
    }
    */
}
