package com.capitalone.dashboard.v2.controllers;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

import java.util.ArrayList;
import java.util.Collection;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.capitalone.dashboard.model.Dashboard;
import com.capitalone.dashboard.service.DashboardService;

@RestController
@RequestMapping("/v2/dashboards")
public class DashboardsController {
    
    @Autowired
    private DashboardService dashboardService;

    @RequestMapping(method = GET)
    public Resources<Resource<Dashboard>> getDashboards() {
        Iterable<Dashboard> dashboards = dashboardService.all();
        
        Collection<Resource<Dashboard>> dashboardsWithLinks = new ArrayList<>();
        dashboards.forEach(dashboard -> {
            Link links = linkTo(methodOn(DashboardsController.class).getDashboard(dashboard.getId())).withSelfRel();
            Resource<Dashboard> dashboardWithLink = new Resource<>(dashboard, links);
            dashboardsWithLinks.add(dashboardWithLink);
        });
        
        Resources<Resource<Dashboard>> resources = new Resources<>(dashboardsWithLinks);
        resources.add(linkTo(methodOn(DashboardsController.class).getDashboards()).withSelfRel());
        
        return resources;
    }
    
    @RequestMapping(value = "/{id}", method = GET)
    public Resource<Dashboard> getDashboard(@PathVariable ObjectId id) {
        Dashboard dashboard = dashboardService.get(id);
        Link links = linkTo(methodOn(DashboardsController.class).getDashboard(dashboard.getId())).withSelfRel();
        
        return new Resource<Dashboard>(dashboard, links);
    }
    
    

}
