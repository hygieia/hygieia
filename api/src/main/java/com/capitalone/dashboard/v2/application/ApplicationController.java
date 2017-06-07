package com.capitalone.dashboard.v2.application;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.hateoas.Resource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.capitalone.dashboard.v2.dashboard.DashboardsController;

@RestController
@RequestMapping("/v2")
public class ApplicationController {
    
    @Value("${version.number}")
    private String versionNumber;

    @RequestMapping(method = RequestMethod.GET)
    public Resource<String> getApi() {
        
        Resource<String> resource = new Resource<>(versionNumber);
        resource.add(linkTo(methodOn(ApplicationController.class).getApi()).withSelfRel(), 
                linkTo(methodOn(DashboardsController.class).getDashboards(null)).withRel("dashboards"),
                linkTo(methodOn(DashboardsController.class).getDashboards(true)).withRel("my-dashboards"));
        
        return resource;
    }
    
}
