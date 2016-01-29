package com.capitalone.dashboard.service;

import com.capitalone.dashboard.misc.HygieiaException;
import com.capitalone.dashboard.model.Collector;
import com.capitalone.dashboard.model.CollectorItem;
import com.capitalone.dashboard.model.CollectorType;
import com.capitalone.dashboard.repository.CollectorItemRepository;
import com.capitalone.dashboard.repository.CollectorRepository;
import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Service
public class CollectorServiceImpl implements CollectorService {

    private final CollectorRepository collectorRepository;
    private final CollectorItemRepository collectorItemRepository;

    @Autowired
    public CollectorServiceImpl(CollectorRepository collectorRepository,
                                CollectorItemRepository collectorItemRepository) {
        this.collectorRepository = collectorRepository;
        this.collectorItemRepository = collectorItemRepository;
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
    public CollectorItem createCollectorItemByNiceName(CollectorItem item) throws HygieiaException {
        //Try to find a matching by collector ID and niceName.
        List<CollectorItem> existing = collectorItemRepository.findByCollectorIdAndNiceName(item.getCollectorId(), item.getNiceName());

        //if not found, call the method to look up by collector ID and options. NiceName would be saved too
        if (CollectionUtils.isEmpty(existing)) return createCollectorItem(item);

        //if there are more than one found matching nice name, throw. This should be done by declaring niceName as unique, but
        // have to do this for backward compatibility.
        if (existing.size() > 1)
            throw new HygieiaException("Multiple collector items found with the same name: " + item.getNiceName(), HygieiaException.COLLECTOR_ITEM_CREATE_ERROR);

        //Flow is here because there is only one collector item with the same collector id and niceName. So, update with
        // the new info - keep the same collector item id. Save here = Update or Insert.
        item.setId(existing.get(0).getId());

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
