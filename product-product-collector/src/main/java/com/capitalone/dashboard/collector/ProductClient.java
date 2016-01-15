package com.capitalone.dashboard.collector;

import com.capitalone.dashboard.model.Dashboard;
import com.capitalone.dashboard.model.TeamDashboardCollectorItem;

import java.util.Map;

/**
 * Team
 */
public interface ProductClient {
    Map<TeamDashboardCollectorItem, Dashboard> getTeamDashboards();

}
