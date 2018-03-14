package com.capitalone.dashboard.service;

import com.capitalone.dashboard.model.CollectorItem;
import com.capitalone.dashboard.model.Dashboard;

public interface ScoreDashboardService {

  /**
   * Add Score Collector Item for dashboard if score is enabled
   *
   * @param dashboard Dashboard Model
   * @return CollectorItem for score
   */
  CollectorItem addScoreForDashboardIfScoreEnabled(Dashboard dashboard);

  /**
   * Edit Score for dashboard
   * Enable/Disable score for a existing dashboard
   *
   * @param prevSettings Dashboard Model
   * @return CollectorItem for score
   */
  CollectorItem editScoreForDashboard(Dashboard prevSettings);

  /**
   * Add Score Collector Item for dashboard
   *
   * @param dashboard Dashboard Model
   * @return CollectorItem for score
   */
  CollectorItem addScoreForDashboard(Dashboard dashboard);

  /**
   * Disable Score for dashboard
   *
   * @param dashboard Dashboard Model
   * @return CollectorItem for score
   */
  CollectorItem disableScoreForDashboard(Dashboard dashboard);

}
