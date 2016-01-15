package com.capitalone.dashboard.collector;

import com.capitalone.dashboard.model.Dashboard;
import com.capitalone.dashboard.model.TeamDashboardCollectorItem;
import com.capitalone.dashboard.repository.DashboardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Would be used to get team dashboard information if we need to for of the model objects
 */
@Component
public class ProductClientImpl implements ProductClient {

    private DashboardRepository dashboardRepository;

    @Autowired
    public ProductClientImpl(DashboardRepository dashboardRepository){
        this.dashboardRepository = dashboardRepository;
    }

    public Map<TeamDashboardCollectorItem, Dashboard> getTeamDashboards(){
        Map<TeamDashboardCollectorItem, Dashboard> teamDashboards = new HashMap<>();
        Iterable<Dashboard> dashboardList = dashboardRepository.findAll();
        for (Dashboard dashboard: dashboardList) {
            TeamDashboardCollectorItem teamDashboardCollectorItem = new TeamDashboardCollectorItem();
            teamDashboardCollectorItem.setDashboardId(dashboard.getId());
            teamDashboards.put(teamDashboardCollectorItem, dashboard);
        }
        return teamDashboards;


    }

}
