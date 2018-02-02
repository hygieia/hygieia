package com.capitalone.dashboard.service;

import com.capitalone.dashboard.model.*;
import com.capitalone.dashboard.repository.CollectorRepository;
import com.capitalone.dashboard.repository.ComponentRepository;
import com.capitalone.dashboard.repository.ScoreRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ScoreServiceImpl implements ScoreService {

    private final ComponentRepository componentRepository;
    private final ScoreRepository scoreRepository;
    private final CollectorRepository collectorRepository;

    @Autowired
    public ScoreServiceImpl(ComponentRepository componentRepository,
                             ScoreRepository scoreRepository,
                             CollectorRepository collectorRepository) {
        this.componentRepository = componentRepository;
        this.scoreRepository = scoreRepository;
        this.collectorRepository = collectorRepository;
    }

    @Override
    public DataResponse<ScoreMetric> getScoreMetric(ObjectId componentId) {
        Component component = componentRepository.findOne(componentId);
        CollectorItem item = component.getCollectorItems()
                .get(CollectorType.Score).get(0);
        ObjectId collectorItemId = item.getId();

        ScoreMetric scoreMetric = scoreRepository
                .findByCollectorItemId(collectorItemId);

        Collector collector = collectorRepository
                .findOne(item.getCollectorId());
        return new DataResponse<>(scoreMetric, collector.getLastExecuted());
    }
}
