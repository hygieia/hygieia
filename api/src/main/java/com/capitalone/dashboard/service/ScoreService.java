package com.capitalone.dashboard.service;

import com.capitalone.dashboard.model.DataResponse;
import com.capitalone.dashboard.model.ScoreMetric;
import org.bson.types.ObjectId;

public interface ScoreService {

    /**
     * A snapshot of the deployment status of each DeployableUnit and Server
     * in all environments.
     *
     * @param componentId id of Component
     * @return list of Environments
     */
    DataResponse<ScoreMetric> getScoreMetric(ObjectId componentId);
}
