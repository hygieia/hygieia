package com.capitalone.dashboard.util;

import com.capitalone.dashboard.model.Collector;
import com.capitalone.dashboard.model.CollectorItem;
import com.capitalone.dashboard.repository.ComponentRepository;
import org.bson.types.ObjectId;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class DashboardUtils {


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
}
