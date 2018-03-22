package com.capitalone.dashboard.repository;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.Query;

import com.capitalone.dashboard.model.score.ScoreCollectorItem;

/**
 * Repository for {@link ScoreCollectorItem}s.
 */
public interface ScoreCollectorItemRepository extends BaseCollectorItemRepository<ScoreCollectorItem> {

  /**
   * Finds all enabled {@link ScoreCollectorItem}s
   *
   * @param collectorId ID
   * @return list of {@link ScoreCollectorItem}s
   */
  @Query(value = "{ 'collectorId' : ?0, enabled: true}")
  List<ScoreCollectorItem> findEnabledScores(ObjectId collectorId);

  @Query(value="{'collectorId': ?0, 'options.dashboardId': ?1}")
  ScoreCollectorItem findCollectorItemByCollectorIdAndDashboardId(ObjectId collectorId, ObjectId dashboardId);

}
