package com.capitalone.dashboard.repository;

import com.capitalone.dashboard.model.Collector;
import com.capitalone.dashboard.model.CollectorType;
import org.bson.types.ObjectId;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Generic Collector repository that contains methods common to any model that extends from
 * Collector.
 *
 * @param <T> Class that extends {@link Collector}
 */
public interface BaseCollectorRepository<T extends Collector> extends CrudRepository<T, ObjectId> {

    /**
     * Finds a {@link Collector} by its name.
     *
     * @param name name
     * @return a {@link Collector}
     */
    T findByName(String name);

    /**
     * Finds all {@link Collector}s of a given {@link CollectorType}.
     *
     * @param collectorType a {@link CollectorType}
     * @return list of {@link Collector}s of a given {@link CollectorType}
     */
    List<T> findByCollectorType(CollectorType collectorType);

    /**
     * Finds all {@link Collector}s of a given {@link CollectorType}.
     *
     * @param collectorType a {@link CollectorType}
     * @param  name
     * @return list of {@link Collector}s of a given {@link CollectorType}
     */

    List<T> findByCollectorTypeAndName(CollectorType collectorType, String name);
}
