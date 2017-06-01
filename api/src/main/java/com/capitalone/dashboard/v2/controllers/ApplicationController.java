package com.capitalone.dashboard.v2.controllers;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import org.springframework.hateoas.Resource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v2")
public class ApplicationController {

    @RequestMapping(method = RequestMethod.GET)
    public Resource<String> getApi() {
        Resource<String> resource = new Resource<>("Welcome to Hygieia");
        resource.add(linkTo(methodOn(ApplicationController.class).getApi()).withSelfRel(), 
                linkTo(methodOn(DashboardsController.class).getDashboards(false)).withRel("all-dashboards"),
                linkTo(methodOn(DashboardsController.class).getDashboards(true)).withRel("my-dashboards"));
        
        return resource;
    }
    
}
