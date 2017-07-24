package com.capitalone.dashboard.service;

import com.capitalone.dashboard.misc.HygieiaException;
import com.capitalone.dashboard.model.Collector;
import com.capitalone.dashboard.model.CollectorItem;
import com.capitalone.dashboard.model.CollectorType;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface CollectorService {

    /**
     * Finds all Collectors of a given type.
     *
     * @param collectorType collector type
     * @return Collectors matching the specified type
     */
    List<Collector>  collectorsByType(CollectorType collectorType);
    
    /**
     * Finds paged results of CollectorItems of a given type.
     *
     * @param collectorType collector type
     * @param {@link org.springframework.data.domain.Pageable} object to determine which page to return
     * @return CollectorItems matching the specified type
     */
    Page<CollectorItem> collectorItemsByTypeWithFilter(CollectorType collectorType, String descriptionFilter, Pageable pageable);
    
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
     * Creates a new CollectorItem. If a CollectorItem already exists with the
     * same collector id and niceName, that CollectorItem will be returned instead
     * of creating a new CollectorItem.
     *
     * @param item CollectorItem to create
     * @return created CollectorItem
     */
    CollectorItem createCollectorItemByNiceNameAndJobName(CollectorItem item, String jobName) throws HygieiaException;


    // This is to handle scenarios where the option contains user credentials etc. We do not want to create a new collector item -
    // just update the new credentials.
    CollectorItem createCollectorItemSelectOptions(CollectorItem item, Map<String, Object> allOptions, Map<String, Object> selecOptions);


    /**
     * Creates a new CollectorItem. If a CollectorItem already exists with the
     * same collector id and niceName, that CollectorItem will be returned instead
     * of creating a new CollectorItem.
     *
     * @param item CollectorItem to create
     * @return created CollectorItem
     */
    CollectorItem createCollectorItemByNiceNameAndProjectId(CollectorItem item, String projectId) throws HygieiaException;

    /**
     * Creates a new Collector.
     * @param collector Collector to create
     * @return created Collector
     */
    Collector createCollector(Collector collector);


    /**
     * Gets a list of collectorItems for a given component id
     * @param id id
     * @return List of collectorItems
     */
    List<CollectorItem> getCollectorItemForComponent (String id, String type);
}
