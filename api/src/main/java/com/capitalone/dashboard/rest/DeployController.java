package com.capitalone.dashboard.rest;

import com.capitalone.dashboard.model.DataResponse;
import com.capitalone.dashboard.model.deploy.Environment;
import com.capitalone.dashboard.service.DeployService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RestController
public class DeployController {

    private static final String JSON = MediaType.APPLICATION_JSON_VALUE;

    private final DeployService deployService;

    @Autowired
    public DeployController(DeployService deployService) {
        this.deployService = deployService;
    }

    @RequestMapping(value = "/deploy/status/{componentId}", method = GET, produces = JSON)
    public DataResponse<List<Environment>> deployStatus(@PathVariable ObjectId componentId) {
        return deployService.getDeployStatus(componentId);
    }
}
