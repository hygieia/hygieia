package com.capitalone.dashboard.service;

import com.capitalone.dashboard.model.*;
import com.capitalone.dashboard.model.score.ScoreCollectorItem;
import com.capitalone.dashboard.model.score.ScoreMetric;
import com.capitalone.dashboard.repository.*;
import org.apache.commons.collections.CollectionUtils;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ScoreServiceImpl implements ScoreService {

    private final CollectorService collectorService;
    private final ScoreCollectorItemRepository scoreCollectorItemRepository;
    private final ScoreRepository scoreRepository;

    @Autowired
    public ScoreServiceImpl(CollectorService collectorService,
                             ScoreRepository scoreRepository,
                             ScoreCollectorItemRepository scoreCollectorItemRepository) {
        this.collectorService = collectorService;
        this.scoreRepository = scoreRepository;
        this.scoreCollectorItemRepository = scoreCollectorItemRepository;
    }

    @Override
    public DataResponse<ScoreMetric> getScoreMetric(ObjectId dashboardId) {
        List<Collector> collectors = collectorService.collectorsByType(CollectorType.Score);
        if (CollectionUtils.isEmpty(collectors)) {
            return new DataResponse<>(null, 0);
        }

        Collector scoreCollector = collectors.get(0);

        ScoreCollectorItem scoreCollectorItem = this.scoreCollectorItemRepository.findCollectorItemByCollectorIdAndDashboardId(
          scoreCollector.getId(),
          dashboardId
        );

        if (null == scoreCollectorItem) {
            return new DataResponse<>(null, 0);
        }

        ObjectId collectorItemId = scoreCollectorItem.getId();

        ScoreMetric scoreMetric = scoreRepository
                .findByCollectorItemId(collectorItemId);

        return new DataResponse<>(scoreMetric, scoreCollector.getLastExecuted());
    }
}
