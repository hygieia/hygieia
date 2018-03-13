package com.capitalone.dashboard.service;

import com.capitalone.dashboard.model.CollectorItem;
import com.capitalone.dashboard.model.Dashboard;

public interface ScoreDashboardService {

  CollectorItem enableScoreForNewDashboard(Dashboard dashboard);

  CollectorItem editScoreForDashboard(Dashboard prevSettings);

  CollectorItem addScoreForDashboard(Dashboard dashboard);

  CollectorItem disableScoreForDashboard(Dashboard dashboard);

}
