package com.capitalone.dashboard.util;

import com.capitalone.dashboard.model.Collector;
import com.capitalone.dashboard.model.CollectorItem;
import com.capitalone.dashboard.model.CollectorType;
import com.capitalone.dashboard.repository.CollectorItemRepository;
import com.capitalone.dashboard.repository.CollectorRepository;
import com.capitalone.dashboard.repository.ComponentRepository;
import com.capitalone.dashboard.repository.CustomRepositoryQuery;
import org.apache.commons.collections.CollectionUtils;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class DashboardUtils {
    private final CollectorItemRepository collectorItemRepository;
    private final CollectorRepository collectorRepository;
    private final ComponentRepository componentRepository;
    private final CustomRepositoryQuery customRepositoryQuery;

    @Autowired
    public DashboardUtils(CollectorItemRepository collectorItemRepository,
                          CollectorRepository collectorRepository,
                          ComponentRepository componentRepository,
                          CustomRepositoryQuery customRepositoryQuery) {
        this.collectorItemRepository = collectorItemRepository;
        this.collectorRepository = collectorRepository;
        this.componentRepository = componentRepository;
        this.customRepositoryQuery = customRepositoryQuery;
    }

    /**
     * Gets a unique set of collector item ids for a given collector from all components in the Hygieia instance.
     * @param componentRepository component repo
     * @param collector collector
     * @return unique set of collector item ids
     */
    public static Set<ObjectId> getUniqueCollectorItemIDsFromAllComponents(ComponentRepository componentRepository, Collector collector) {
        Set<ObjectId> uniqueIDs = new HashSet<>();
        for (com.capitalone.dashboard.model.Component comp : componentRepository.findAll()) {
            if (comp.getCollectorItems() == null || comp.getCollectorItems().isEmpty()) continue;
            List<CollectorItem> itemList = comp.getCollectorItems().get(collector.getCollectorType());
            if (itemList == null) continue;
            for (CollectorItem ci : itemList) {
                if (ci != null && ci.getCollectorId().equals(collector.getId())) {
                    uniqueIDs.add(ci.getId());
                }
            }
        }
        return uniqueIDs;
    }

    public com.capitalone.dashboard.model.Component associateCollectorToComponent(ObjectId componentId, List<ObjectId> collectorItemIds) {
        if (componentId == null || collectorItemIds == null) {
            // Not all widgets gather data from collectors
            return null;
        }

        com.capitalone.dashboard.model.Component component = componentRepository.findOne(componentId); //NOPMD - using fully qualified name for clarity
        //we can not assume what collector item is added, what is removed etc so, we will
        //refresh the association. First disable all collector items, then remove all and re-add

        //First: disable all collectorItems of the Collector TYPEs that came in with the request.
        //Second: remove all the collectorItem association of the Collector Type  that came in
        HashSet<CollectorType> incomingTypes = new HashSet<>();
        HashMap<ObjectId, CollectorItem> toSaveCollectorItems = new HashMap<>();
        for (ObjectId collectorItemId : collectorItemIds) {
            CollectorItem collectorItem = collectorItemRepository.findOne(collectorItemId);
            Collector collector = collectorRepository.findOne(collectorItem.getCollectorId());
            if (!incomingTypes.contains(collector.getCollectorType())) {
                incomingTypes.add(collector.getCollectorType());
                List<CollectorItem> cItems = component.getCollectorItems(collector.getCollectorType());
                // Save all collector items as disabled for now
                if (!CollectionUtils.isEmpty(cItems)) {
                    for (CollectorItem ci : cItems) {
                        //if item is orphaned, disable it. Otherwise keep it enabled.
                        ci.setEnabled(!isLonely(ci, collector, component));
                        toSaveCollectorItems.put(ci.getId(), ci);
                    }
                }
                // remove all collector items of a type
                component.getCollectorItems().remove(collector.getCollectorType());
            }
        }

        //Last step: add collector items that came in
        for (ObjectId collectorItemId : collectorItemIds) {
            CollectorItem collectorItem = collectorItemRepository.findOne(collectorItemId);
            //the new collector items must be set to true
            collectorItem.setEnabled(true);
            Collector collector = collectorRepository.findOne(collectorItem.getCollectorId());
            component.addCollectorItem(collector.getCollectorType(), collectorItem);
            toSaveCollectorItems.put(collectorItemId, collectorItem);
            // set transient collector property
            collectorItem.setCollector(collector);
        }

        Set<CollectorItem> deleteSet = new HashSet<>();
        for (ObjectId id : toSaveCollectorItems.keySet()) {
            deleteSet.add(toSaveCollectorItems.get(id));
        }
        collectorItemRepository.save(deleteSet);
        componentRepository.save(component);
        return component;
    }

    private boolean isLonely(CollectorItem item, Collector collector, com.capitalone.dashboard.model.Component component) {
        List<com.capitalone.dashboard.model.Component> components = customRepositoryQuery.findComponents(collector, item);
        //if item is not attached to any component, it is orphaned.
        if (CollectionUtils.isEmpty(components)) return true;
        //if item is attached to more than 1 component, it is NOT orphaned
        if (components.size() > 1) return false;
        //if item is attached to ONLY 1 component it is the current one, it is going to be orphaned after this
        return (components.get(0).getId().equals(component.getId()));
    }
}