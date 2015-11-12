package com.capitalone.dashboard.service;

import java.util.List;
import java.util.Map;

import org.bson.types.ObjectId;

import com.capitalone.dashboard.model.Collector;
import com.capitalone.dashboard.model.CollectorItem;
import com.capitalone.dashboard.model.CollectorType;

public interface CollectorService {

    /**
     * Finds all Collectors of a given type.
     *
     * @param collectorType collector type
     * @return Collectors matching the specified type
     */
    List<Collector>  collectorsByType(CollectorType collectorType);

    /**
     * Finds all CollectorItems of a given type.
     *
     * @param collectorType collector type
     * @return CollectorItems matching the specified type
     */
    List<CollectorItem> collectorItemsByType(CollectorType collectorType);

    /**
     * Find a CollectorItem by it's id.
     *
     * @param id id
     * @return CollectorItem
     */
    CollectorItem getCollectorItem(ObjectId id);

    /**
     * Creates a new CollectorItem. If a CollectorItem already exists with the
     * same collector id and options, that CollectorItem will be returned instead
     * of creating a new CollectorItem.
     *
     * @param item CollectorItem to create
     * @return created CollectorItem
     */
    CollectorItem createCollectorItem(CollectorItem item);

    /**
     * Gets a collectorItem with a given Collector Id and options
     *
     * @param id ObjectId
     * @return options Map<String, Object>
     */
    CollectorItem getCollectorItemByCollectorIDandOptions (ObjectId id, Map<String, Object> options);
}
