package com.capitalone.dashboard.service;

import com.capitalone.dashboard.model.Collector;
import com.capitalone.dashboard.model.CollectorItem;
import com.capitalone.dashboard.model.CollectorType;
import com.capitalone.dashboard.model.Component;
import com.capitalone.dashboard.model.DataResponse;
import com.capitalone.dashboard.model.LibraryPolicyResult;
import com.capitalone.dashboard.model.QLibraryPolicyResult;
import com.capitalone.dashboard.repository.CollectorRepository;
import com.capitalone.dashboard.repository.ComponentRepository;
import com.capitalone.dashboard.repository.LibraryPolicyResultsRepository;
import com.capitalone.dashboard.request.LibraryPolicyRequest;
import com.mysema.query.BooleanBuilder;
import org.apache.commons.collections.CollectionUtils;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service
public class LibraryPolicyServiceImpl implements LibraryPolicyService {

    private final LibraryPolicyResultsRepository libraryPolicyResultsRepository;
    private final ComponentRepository componentRepository;
    private final CollectorRepository collectorRepository;

    @Autowired
    public LibraryPolicyServiceImpl(LibraryPolicyResultsRepository libraryPolicyResultsRepository,
                                    ComponentRepository componentRepository,
                                    CollectorRepository collectorRepository) {
        this.libraryPolicyResultsRepository = libraryPolicyResultsRepository;
        this.componentRepository = componentRepository;
        this.collectorRepository = collectorRepository;
    }

    @Override
    public DataResponse<List<LibraryPolicyResult>> search(LibraryPolicyRequest request) {
        if (request == null) {
            return new DataResponse<>(null, System.currentTimeMillis());
        }

        List<CollectorItem> items = getCollectorItems(request);
        if (CollectionUtils.isEmpty(items)) {
            return new DataResponse<>(null, System.currentTimeMillis());
        }

        QLibraryPolicyResult policyResult = new QLibraryPolicyResult("libraryPolicyResult");
        BooleanBuilder builder = new BooleanBuilder();

        List<LibraryPolicyResult> results = new ArrayList<>();

        long lastExecuted = -1; //uninitialized value

        for (CollectorItem item : items) {
            Iterable<LibraryPolicyResult> itemResult;
            builder.and(policyResult.collectorItemId.eq(item.getId()));

            if (request.getNumberOfDays() != null) {
                long endTimeTarget =
                        new LocalDate().minusDays(request.getNumberOfDays()).toDate().getTime();
                builder.and(policyResult.timestamp.goe(endTimeTarget));
            } else if (request.validDateRange()) {
                builder.and(policyResult.timestamp.between(request.getDateBegins(), request.getDateEnds()));
            }

            if (request.getMax() == null) {
                itemResult = libraryPolicyResultsRepository.findAll(builder.getValue(), policyResult.timestamp.desc());
            } else {
                PageRequest pageRequest =
                        new PageRequest(0, request.getMax(), Sort.Direction.DESC, "timestamp");
                itemResult = libraryPolicyResultsRepository.findAll(builder.getValue(), pageRequest).getContent();
            }
            if (itemResult != null) {
                for (Iterator<LibraryPolicyResult> it = itemResult.iterator(); it.hasNext(); ) {
                    LibraryPolicyResult lpr = it.next();
                    results.add(lpr);
                }
            }
            Collector collector = collectorRepository.findOne(item.getCollectorId());
            long runTime = (collector == null) ? 0 : collector.getLastExecuted();
            lastExecuted = (runTime < lastExecuted) ? runTime : lastExecuted;
        }
        return new DataResponse<>(results, lastExecuted);
    }


    protected List<CollectorItem> getCollectorItems(LibraryPolicyRequest request) {
        Component component = componentRepository.findOne(request.getComponentId());
        return (component != null) ? component.getCollectorItems(CollectorType.LibraryPolicy) : null;
    }
}
