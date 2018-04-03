package com.capitalone.dashboard.service;

import com.capitalone.dashboard.model.DataResponse;
import com.capitalone.dashboard.model.score.ScoreMetric;
import org.bson.types.ObjectId;

public interface ScoreService {

    /**
     * Score Metrics by dashboard id
     *
     * @param dashboardId id of dashboard
     * @return DataResponse of type Scores
     */
    DataResponse<ScoreMetric> getScoreMetric(ObjectId dashboardId);
}
