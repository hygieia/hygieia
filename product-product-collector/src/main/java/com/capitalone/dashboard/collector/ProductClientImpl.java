package com.capitalone.dashboard.collector;

import com.capitalone.dashboard.model.Dashboard;
import com.capitalone.dashboard.repository.DashboardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

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

    public List<Dashboard> getAllTeamDashboards(){
        return (List) dashboardRepository.findAll();
    }
}
