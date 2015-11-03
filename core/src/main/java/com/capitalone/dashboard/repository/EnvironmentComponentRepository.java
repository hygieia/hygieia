package com.capitalone.dashboard.repository;

import com.capitalone.dashboard.model.EnvironmentComponent;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * {@link EnvironmentComponent} repository.
 */
public interface EnvironmentComponentRepository extends CrudRepository<EnvironmentComponent, ObjectId> {

    /**
     * Finds the {@link EnvironmentComponent} collector item id, environment name and component name.
     *
     * @param collectorItemId collector item id
     * @param environmentName environment name
     * @param componentName component name
     * @return a {@link EnvironmentComponent}
     */
    @Query(value="{ collectorItemId : ?0, environmentName : ?1, componentName : ?2}")
    EnvironmentComponent findComponent(ObjectId collectorItemId, String environmentName, String componentName);

    /**
     * Finds all {@link EnvironmentComponent}s for a given {@link com.capitalone.dashboard.model.CollectorItem}.
     *
     * @param collectorItemId collector item id
     * @return list of {@link EnvironmentComponent}
     */
    List<EnvironmentComponent> findByCollectorItemId(ObjectId collectorItemId);
}
