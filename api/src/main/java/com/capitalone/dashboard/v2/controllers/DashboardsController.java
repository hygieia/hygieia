package com.capitalone.dashboard.v2.controllers;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import java.util.ArrayList;
import java.util.Collection;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resources;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.capitalone.dashboard.misc.HygieiaException;
import com.capitalone.dashboard.model.Dashboard;
import com.capitalone.dashboard.service.DashboardService;
import com.capitalone.dashboard.v2.dtos.DashboardDTO;
import com.capitalone.dashboard.v2.utilities.ResourceConversionUtility;

import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/v2/dashboards")
public class DashboardsController {
    
    @Autowired
    private DashboardService dashboardService;
    
    @Autowired
    private ResourceConversionUtility converter;

    @ApiOperation(value = "Get all dashboards", notes = "This operation will return all the dashboards, there is an optional query parameter for just returning dashboards you own.")
    @RequestMapping(method = GET)
    public Resources<DashboardDTO> getDashboards(@RequestParam(defaultValue="false", required=false) Boolean owned) {
        Iterable<Dashboard> dashboards = owned ? dashboardService.getOwnedDashboards() : dashboardService.all();
        
        Collection<DashboardDTO> dashboardsWithLinks = new ArrayList<>();
        dashboards.forEach(dashboard -> {
            DashboardDTO resource = converter.toDashboardDTO(dashboard);
            Link link = linkTo(methodOn(DashboardsController.class).getDashboard(resource.getDashboardId())).withSelfRel();
            resource.add(link);
            dashboardsWithLinks.add(resource);
        });
        
        Resources<DashboardDTO> resources = new Resources<>(dashboardsWithLinks);
        resources.add(linkTo(methodOn(DashboardsController.class).getDashboards(owned)).withSelfRel());
        
        return resources;
    }
    
    @RequestMapping(value = "/{id}", method = GET)
    public DashboardDTO getDashboard(@PathVariable String id) {
        DashboardDTO dashboard = converter.toDashboardDTO(dashboardService.get(new ObjectId(id)));
        Link link = linkTo(methodOn(DashboardsController.class).getDashboard(dashboard.getDashboardId())).withSelfRel();
        dashboard.add(link);
        return dashboard;
    }
    
    @ApiOperation(value = "Create new Dashboard", code = 201)
    @RequestMapping(method = POST)
    public ResponseEntity<DashboardDTO> createDashboard(@RequestBody DashboardDTO dashboard) {
        try {
            Dashboard createdDashboard = dashboardService.create(converter.toDashboard(dashboard));
            DashboardDTO resource = converter.toDashboardDTO(createdDashboard);
            Link link = linkTo(methodOn(DashboardsController.class).getDashboard(resource.getDashboardId())).withSelfRel();
            resource.add(link);
            return ResponseEntity.status(HttpStatus.CREATED).body(resource);
        } catch (HygieiaException he) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }
    

}
