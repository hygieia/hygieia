package com.capitalone.dashboard.repository;

import java.util.List;

import org.bson.types.ObjectId;

import com.capitalone.dashboard.model.EnvironmentComponent;

public interface XLDeployEnvironmentComponentRepository extends EnvironmentComponentRepository {
    /**
     * Finds all {@link EnvironmentComponent}s for a given {@link com.capitalone.dashboard.model.CollectorItem}.
     *
     * @param collectorItemId collector item id
     * @param environmentID the environment id
     * @return list of {@link EnvironmentComponent}
     */
    List<EnvironmentComponent> findByCollectorItemIdAndEnvironmentID(ObjectId collectorItemId, String environmentID);
}
