package com.capitalone.dashboard.repository;

import com.capitalone.dashboard.model.CollectorItem;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Base {@link CollectorItem} repository that provides methods useful for any {@link CollectorItem}
 * implementation.
 *
 * @param <T> Class that extends {@link CollectorItem}
 */
public interface BaseCollectorItemRepository<T extends CollectorItem> extends CrudRepository<T, ObjectId> {

    /**
     * Finds all {@link CollectorItem}s that are enabled.
     *
     * @return list of {@link CollectorItem}s
     */
    List<T> findByEnabledIsTrue();

    /**
     * Finds all {@link CollectorItem}s that match the provided id's.
     *
     * @param ids {@link Collection} of ids
     * @return list of {@link CollectorItem}s
     */
    List<T> findByCollectorIdIn(Collection<ObjectId> ids);

    /**
     * Finds the {@link CollectorItem} for a given collector and options. This should represent a unique
     * instance of a {@link CollectorItem} for a given {@link com.capitalone.dashboard.model.Collector}.
     *
     * @param collectorId {@link com.capitalone.dashboard.model.Collector} id
     * @param options options
     * @return a {@link CollectorItem}
     */
    @Query(value="{ 'collectorId' : ?0, options : ?1}")
    T findByCollectorAndOptions(ObjectId collectorId, Map<String, Object> options);

    List<T> findByCollectorIdAndNiceName (ObjectId collectorId, String niceName);
}
