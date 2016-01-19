package com.capitalone.dashboard.collector;

import com.capitalone.dashboard.model.Dashboard;
import com.capitalone.dashboard.model.ProductDashboardCollector;
import com.capitalone.dashboard.model.TeamDashboardCollectorItem;
import com.capitalone.dashboard.repository.ProductDashboardRepository;
import com.capitalone.dashboard.repository.TeamDashboardRepository;
import org.bson.types.ObjectId;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.scheduling.TaskScheduler;

import java.util.HashMap;
import java.util.Map;

@RunWith(MockitoJUnitRunner.class)
public class ProductCollectorTaskTests {

    @Mock
    private TaskScheduler taskScheduler;
    @Mock
    private ProductDashboardRepository productDashboardRepository;
    @Mock
    private TeamDashboardRepository teamDashboardRepository;
    @Mock
    private ProductClient productClient;
    @Mock
    private ProductSettings productSettings;

    @InjectMocks
    private ProductCollectorTask task;



    @Test
    @Ignore
    public void collect_brandNewTeamDashboardAdded() {

    }

    @Test
    @Ignore
    public void collect_noNewTeamDashboardAdded() {

    }

    private ProductDashboardCollector collector() {
        return ProductDashboardCollector.prototype();
    }

    private Map<TeamDashboardCollectorItem, Dashboard> oneTeamDashboardCollectorItemWithTeamDashboard(TeamDashboardCollectorItem teamDashboardCollectorItem, Dashboard dashboard) {
        Map<TeamDashboardCollectorItem, Dashboard> dashboards = new HashMap<>();
        dashboards.put(teamDashboardCollectorItem, dashboard);
        return dashboards;
    }

    private TeamDashboardCollectorItem createTeamDashboard(ObjectId id) {
        TeamDashboardCollectorItem teamDashboardCollectorItem = new TeamDashboardCollectorItem();
        teamDashboardCollectorItem.setDashboardId(id.toString());
        return teamDashboardCollectorItem;
    }
}
