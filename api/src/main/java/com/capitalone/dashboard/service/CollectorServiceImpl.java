package com.capitalone.dashboard.service;

import com.capitalone.dashboard.misc.HygieiaException;
import com.capitalone.dashboard.model.Collector;
import com.capitalone.dashboard.model.CollectorItem;
import com.capitalone.dashboard.model.CollectorType;
import com.capitalone.dashboard.model.Component;
import com.capitalone.dashboard.model.Dashboard;
import com.capitalone.dashboard.repository.CollectorItemRepository;
import com.capitalone.dashboard.repository.CollectorRepository;
import com.capitalone.dashboard.repository.ComponentRepository;
import com.capitalone.dashboard.repository.CustomRepositoryQuery;
import com.capitalone.dashboard.repository.DashboardRepository;
import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class CollectorServiceImpl implements CollectorService {

    private final CollectorRepository collectorRepository;
    private final CollectorItemRepository collectorItemRepository;
    private final ComponentRepository componentRepository;
    private final DashboardRepository dashboardRepository;
    private final CustomRepositoryQuery customRepositoryQuery;

    @Autowired
    public CollectorServiceImpl(CollectorRepository collectorRepository,
                                CollectorItemRepository collectorItemRepository,
                                ComponentRepository componentRepository, DashboardRepository dashboardRepository, CustomRepositoryQuery customRepositoryQuery) {
        this.collectorRepository = collectorRepository;
        this.collectorItemRepository = collectorItemRepository;
        this.componentRepository = componentRepository;
        this.dashboardRepository = dashboardRepository;
        this.customRepositoryQuery = customRepositoryQuery;
    }

    @Override
    public List<Collector> collectorsByType(CollectorType collectorType) {
        return collectorRepository.findByCollectorType(collectorType);
    }

    @Override
    public Page<CollectorItem> collectorItemsByTypeWithFilter(CollectorType collectorType, String descriptionFilter, Pageable pageable) {
        List<Collector> collectors = collectorRepository.findByCollectorType(collectorType);

        List<ObjectId> collectorIds = Lists.newArrayList(Iterables.transform(collectors, new ToCollectorId()));
        Page<CollectorItem> collectorItems = null;
        String niceName = "";
        String jobName = "";
        List<String> l= findJobNameAndNiceName(descriptionFilter);
        if (!l.isEmpty()){
            niceName =  l.get(0).trim();
            if(l.size()>1)
            jobName = l.get(1).trim();
        }
        if(!niceName.isEmpty()){
           collectorItems = collectorItemRepository.findByCollectorIdInAndDescriptionContainingAndNiceNameContainingAllIgnoreCase(collectorIds, jobName,niceName, pageable);
        }else{
           collectorItems = collectorItemRepository.findByCollectorIdInAndDescriptionContainingIgnoreCase(collectorIds, descriptionFilter, pageable);
        }
        for (CollectorItem options : collectorItems) {
            options.setCollector(collectorById(options.getCollectorId(), collectors));
        }

        return collectorItems;
    }

    private List<String> findJobNameAndNiceName(String descriptionFilter){
        if(descriptionFilter.contains(":"))
          return  Stream.of(descriptionFilter.split(":"))
                            .collect(Collectors.toList());
        return new ArrayList<>();
    }

    /**
     * We want to initialize the Quasi-product collector when the API starts up
     * so that any existing Team dashboards will be added as CollectorItems.
     * <p>
     * TODO - Is this the best home for this method??
     */
    @PostConstruct
    public void initProductCollectorOnStartup() {
        Collector productCollector = collectorRepository.findByName("Product");
        if (productCollector == null) {
            productCollector = new Collector();
            productCollector.setName("Product");
            productCollector.setCollectorType(CollectorType.Product);
            productCollector.setEnabled(true);
            productCollector.setOnline(true);
            collectorRepository.save(productCollector);

            // Create collector items for existing team dashboards
            for (Dashboard dashboard : dashboardRepository.findTeamDashboards()) {
                CollectorItem item = new CollectorItem();
                item.setCollectorId(productCollector.getId());
                item.getOptions().put("dashboardId", dashboard.getId().toString());
                item.setDescription(dashboard.getTitle());
                collectorItemRepository.save(item);
            }
        }
    }

    @Override
    public CollectorItem getCollectorItem(ObjectId id) {
        CollectorItem item = collectorItemRepository.findOne(id);
        item.setCollector(collectorRepository.findOne(item.getCollectorId()));
        return item;
    }

    @Override
    public CollectorItem createCollectorItem(CollectorItem item) {
        CollectorItem existing = collectorItemRepository.findByCollectorAndOptions(
                item.getCollectorId(), item.getOptions());
        if (existing != null) {
            item.setId(existing.getId());
        }
        return collectorItemRepository.save(item);
    }

    // This is to handle scenarios where the option contains user credentials etc. We do not want to create a new collector item -
    // just update the new credentials.
    @Override
    public CollectorItem createCollectorItemSelectOptions(CollectorItem item, Map<String, Object> allOptions, Map<String, Object> selectOptions) {
        List<CollectorItem> existing = customRepositoryQuery.findCollectorItemsBySubsetOptions(
                item.getCollectorId(), allOptions, selectOptions);

        if (!CollectionUtils.isEmpty(existing)) {
            item.setId(existing.get(0).getId());   //
        }

        return collectorItemRepository.save(item);
    }


    @Override
    public CollectorItem createCollectorItemByNiceNameAndProjectId(CollectorItem item, String projectId) throws HygieiaException {
        //Try to find a matching by collector ID and niceName.
        CollectorItem existing = collectorItemRepository.findByCollectorIdNiceNameAndProjectId(item.getCollectorId(), item.getNiceName(), projectId);

        //if not found, call the method to look up by collector ID and options. NiceName would be saved too
        if (existing == null) return createCollectorItem(item);

        //Flow is here because there is only one collector item with the same collector id and niceName. So, update with
        // the new info - keep the same collector item id. Save = Update or Insert.
        item.setId(existing.getId());

        return collectorItemRepository.save(item);
    }

    @Override
    public CollectorItem createCollectorItemByNiceNameAndJobName(CollectorItem item, String jobName) throws HygieiaException {
        //Try to find a matching by collector ID and niceName.
        CollectorItem existing = collectorItemRepository.findByCollectorIdNiceNameAndJobName(item.getCollectorId(), item.getNiceName(), jobName);

        //if not found, call the method to look up by collector ID and options. NiceName would be saved too
        if (existing == null) return createCollectorItem(item);

        //Flow is here because there is only one collector item with the same collector id and niceName. So, update with
        // the new info - keep the same collector item id. Save = Update or Insert.
        item.setId(existing.getId());

        return collectorItemRepository.save(item);
    }

    @Override
    public Collector createCollector(Collector collector) {
        Collector existing = collectorRepository.findByName(collector.getName());
        if (existing != null) {
            collector.setId(existing.getId());
        }
        return collectorRepository.save(collector);
    }

    @Override
    public List<CollectorItem> getCollectorItemForComponent(String id, String type) {
        ObjectId oid = new ObjectId(id);
        CollectorType ctype = CollectorType.fromString(type);
        Component component = componentRepository.findOne(oid);

        List<CollectorItem> items = component.getCollectorItems(ctype);

        // the collector items from component are not updated for collector run. We need to
        // get the 'live' collector items from the collectorItemRepository
        List<ObjectId> ids = new ArrayList<>();
        for (CollectorItem item : items) {
            ids.add(item.getId());
        }
        return (List<CollectorItem>) collectorItemRepository.findAll(ids);
    }

    private Collector collectorById(ObjectId collectorId, List<Collector> collectors) {
        for (Collector collector : collectors) {
            if (collector.getId().equals(collectorId)) {
                return collector;
            }
        }
        return null;
    }

    private static class ToCollectorId implements Function<Collector, ObjectId> {
        @Override
        public ObjectId apply(Collector input) {
            return input.getId();
        }
    }
}
