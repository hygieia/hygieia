package com.capitalone.dashboard.repository;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.Query;

import com.capitalone.dashboard.model.ScoreApplication;
import com.capitalone.dashboard.model.ScoreCollector;

/**
 * Repository for {@link ScoreApplication}s.
 */
public interface ScoreApplicationRepository extends BaseCollectorItemRepository<ScoreApplication> {

  /**
   * Find a {@link ScoreApplication} by dashboard id.
   *
   * @param collectorId ID of the {@link ScoreCollector}
   * @param dashboardId DashboardId
   * @return a {@link ScoreApplication} instance
   */
  @Query(value = "{ 'collectorId' : ?0, options.dashboardId : ?1}")
  ScoreApplication findScoreByDashboard(ObjectId collectorId, ObjectId dashboardId);

  /**
   * Finds all enabled {@link ScoreApplication}s
   *
   * @param collectorId ID of the {@link ScoreCollector}
   * @return list of {@link ScoreApplication}s
   */
  @Query(value = "{ 'collectorId' : ?0, enabled: true}")
  List<ScoreApplication> findEnabledScores(ObjectId collectorId);
}
