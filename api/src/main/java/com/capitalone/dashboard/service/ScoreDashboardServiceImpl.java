package com.capitalone.dashboard.service;

import com.capitalone.dashboard.model.Collector;
import com.capitalone.dashboard.model.CollectorItem;
import com.capitalone.dashboard.model.CollectorType;
import com.capitalone.dashboard.model.Dashboard;
import com.capitalone.dashboard.repository.CollectorItemRepository;
import com.capitalone.dashboard.repository.CustomRepositoryQuery;
import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ScoreDashboardServiceImpl implements ScoreDashboardService {

  private static final Logger LOGGER = Logger.getLogger(ScoreDashboardServiceImpl.class);

  private final CollectorService collectorService;
  private final CustomRepositoryQuery customRepositoryQuery;
  private final CollectorItemRepository collectorItemRepository;

  @Autowired
  public ScoreDashboardServiceImpl(
    CollectorService collectorService,
    CustomRepositoryQuery customRepositoryQuery,
    CollectorItemRepository collectorItemRepository) {
    this.collectorService = collectorService;
    this.customRepositoryQuery = customRepositoryQuery;
    this.collectorItemRepository = collectorItemRepository;
  }

  @Override
  public CollectorItem enableScoreForNewDashboard(Dashboard dashboard) {
    if (dashboard.isScoreEnabled()) {
      return addScoreForDashboard(dashboard);
    }
    return null;
  }

  @Override
  public CollectorItem editScoreForDashboard(Dashboard dashboard) {
    if (dashboard.isScoreEnabled()) {
      return addScoreForDashboard(dashboard);
    } else {
      return disableScoreForDashboard(dashboard);
    }

  }

  @Override
  public CollectorItem addScoreForDashboard(Dashboard dashboard) {
    return createScoreCollectorItem(dashboard.getId());
  }

  @Override
  public CollectorItem disableScoreForDashboard(Dashboard dashboard) {
    return disableScoreCollectorItem(dashboard.getId());
  }

  private CollectorItem disableScoreCollectorItem(ObjectId dashboardId) {
    Collector scoreCollector;
    List<Collector> collectors = collectorService.collectorsByType(CollectorType.Score);
    if (CollectionUtils.isEmpty(collectors)) {
      LOGGER.warn("No Score Collector Type found");
      return null;
    } else {
      scoreCollector = collectors.get(0);
    }

    CollectorItem scoreCollectorItem = generateScoreCollectorItem(
      dashboardId.toString(),
      scoreCollector.getId()
    );

    List<CollectorItem> items = customRepositoryQuery.findCollectorItemsBySubsetOptions(
      scoreCollector.getId(),
      scoreCollectorItem.getOptions(),
      scoreCollectorItem.getOptions()
    );

    if (CollectionUtils.isEmpty(items)) {
      LOGGER.warn("No Score Collector item found");
      return null;
    }

    scoreCollectorItem = items.get(0);
    scoreCollectorItem.setEnabled(false);

    return this.collectorItemRepository.save(scoreCollectorItem);
  }


  private CollectorItem createScoreCollectorItem(ObjectId dashboardId) {
    Collector scoreCollector;
    List<Collector> collectors = collectorService.collectorsByType(CollectorType.Score);
    if (CollectionUtils.isNotEmpty(collectors)) {
      scoreCollector = collectors.get(0);
    } else {
      scoreCollector = collectorService.createCollector(generateScoreCollector());
    }

    CollectorItem scoreCollectorItem = generateScoreCollectorItem(
      dashboardId.toString(),
      scoreCollector.getId()
    );

    return collectorService.createCollectorItem(scoreCollectorItem);
  }

  private Collector generateScoreCollector() {
    Collector scoreCollector = new Collector();
    scoreCollector.setName("Score");
    scoreCollector.setCollectorType(CollectorType.Score);
    scoreCollector.setOnline(true);
    scoreCollector.setEnabled(true);
    return scoreCollector;
  }


  private CollectorItem generateScoreCollectorItem(String dashboardId, ObjectId collectorId) {
    CollectorItem item = new CollectorItem();
    item.setCollectorId(collectorId);
    item.getOptions().put("dashboardId", dashboardId);
    item.setDescription(dashboardId);
    item.setEnabled(true);
    return item;
  }


}
