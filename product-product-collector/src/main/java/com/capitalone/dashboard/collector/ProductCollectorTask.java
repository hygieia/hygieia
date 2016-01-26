package com.capitalone.dashboard.collector;

import com.capitalone.dashboard.model.*;
import com.capitalone.dashboard.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class ProductCollectorTask extends CollectorTask<ProductDashboardCollector> {

    private final ProductDashboardRepository productDashboardRepository;
    private final TeamDashboardRepository teamDashboardRepository;
    private final ProductClient productClient;
    private final ProductSettings productSettings;

    @Autowired
    public ProductCollectorTask(TaskScheduler taskScheduler,
                                ProductDashboardRepository productDashboardRepository,
                                TeamDashboardRepository teamDashboardRepository,
                                ProductClient productClient,
                                ProductSettings productSettings) {
        super(taskScheduler, "Product");
        this.productDashboardRepository = productDashboardRepository;
        this.teamDashboardRepository = teamDashboardRepository;
        this.productClient = productClient;
        this.productSettings = productSettings;
    }

    @Override
    public String getCron() {
        return productSettings.getCron();
    }

    @Override
    public ProductDashboardCollector getCollector() {
        return ProductDashboardCollector.prototype();
    }

    @Override
    public BaseCollectorRepository<ProductDashboardCollector> getCollectorRepository() {
        return productDashboardRepository;
    }

    private void doCleanUp(List<TeamDashboardCollectorItem> collectorItems, List<Dashboard> dashboards){
        //get all collectorItems where there aren't dashboards'
        if(dashboards.isEmpty()){
            return;
        }

        List<String> dashboardIdsForCollectorItemsToRemove = new ArrayList<>();

        for(TeamDashboardCollectorItem collectorItem : collectorItems){
            dashboardIdsForCollectorItemsToRemove.add(collectorItem.getDashboardId());
        }
        for(Dashboard d :dashboards){
            dashboardIdsForCollectorItemsToRemove.remove(d.getId().toString());
        }

        List<TeamDashboardCollectorItem> collectorItemsToDelete = teamDashboardRepository.findByDashboardIdIn(dashboardIdsForCollectorItemsToRemove);
        teamDashboardRepository.delete(collectorItemsToDelete);
    }

    @Override
    public void collect(ProductDashboardCollector collector) {
        List<TeamDashboardCollectorItem> existingTeamDashboardCollectorItems = teamDashboardRepository.findTeamDashboards(collector.getId());
        List<Dashboard> allTeamDashboards = productClient.getAllTeamDashboards();

        doCleanUp(existingTeamDashboardCollectorItems, allTeamDashboards);
        addNewCollectorItemsForNewDashboards(existingTeamDashboardCollectorItems, allTeamDashboards, collector);
    }

    private void addNewCollectorItemsForNewDashboards(List<TeamDashboardCollectorItem> existingTeamDashboardCollectorItems,
                                                      List<Dashboard> allTeamDashboards, ProductDashboardCollector collector) {

        if(allTeamDashboards.isEmpty()){
            return;
        }

        Map<TeamDashboardCollectorItem, Dashboard> createdTeamDashboards = new HashMap<>();
        for(Dashboard dashboard : allTeamDashboards){
            TeamDashboardCollectorItem teamDashboardCollectorItem = new TeamDashboardCollectorItem();
            teamDashboardCollectorItem.setDashboardId(dashboard.getId().toString());
            createdTeamDashboards.put(teamDashboardCollectorItem, dashboard);
        }

        /**
         * find all collector items that are new
         */
        List<TeamDashboardCollectorItem> toUpdate = new ArrayList<>();
        for ( Map.Entry<TeamDashboardCollectorItem, Dashboard> entry : createdTeamDashboards.entrySet()) {
            /**
             * if collectoritem for the dashboard in the repository doesnt exist
             * as a collector item add it to the list of items to save
             */
            if(!existingTeamDashboardCollectorItems.contains(entry.getKey())) {
                TeamDashboardCollectorItem collectorItem = entry.getKey();
                collectorItem.setCollectorId(collector.getId());
                collectorItem.setDescription(entry.getValue().getTitle());
                toUpdate.add(collectorItem);
            }
        }
        teamDashboardRepository.save(toUpdate);
    }
}
