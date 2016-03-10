package com.capitalone.dashboard.service;

import com.capitalone.dashboard.misc.HygieiaException;
import com.capitalone.dashboard.model.Collector;
import com.capitalone.dashboard.model.CollectorItem;
import com.capitalone.dashboard.model.CollectorType;
import com.capitalone.dashboard.model.Dashboard;
import com.capitalone.dashboard.repository.CollectorItemRepository;
import com.capitalone.dashboard.repository.CollectorRepository;
import com.capitalone.dashboard.repository.DashboardRepository;
import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;

@Service
public class CollectorServiceImpl implements CollectorService {

    private final CollectorRepository collectorRepository;
    private final CollectorItemRepository collectorItemRepository;
    private final DashboardRepository dashboardRepository;

    @Autowired
    public CollectorServiceImpl(CollectorRepository collectorRepository,
                                CollectorItemRepository collectorItemRepository,
                                DashboardRepository dashboardRepository) {
        this.collectorRepository = collectorRepository;
        this.collectorItemRepository = collectorItemRepository;
        this.dashboardRepository = dashboardRepository;
    }

    @Override
    public List<Collector> collectorsByType(CollectorType collectorType) {
        return collectorRepository.findByCollectorType(collectorType);
    }

    @Override
    public List<CollectorItem> collectorItemsByType(CollectorType collectorType) {
        List<Collector> collectors = collectorRepository.findByCollectorType(collectorType);

        List<ObjectId> collectorIds = Lists.newArrayList(Iterables.transform(collectors, new ToCollectorId()));

        List<CollectorItem> collectorItems = collectorItemRepository.findByCollectorIdIn(collectorIds);

        for (CollectorItem options : collectorItems) {
            options.setCollector(collectorById(options.getCollectorId(), collectors));
        }

        return collectorItems;
    }

    /**
     * We want to initialize the Quasi-product collector when the API starts up
     * so that any existing Team dashboards will be added as CollectorItems.
     *
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
