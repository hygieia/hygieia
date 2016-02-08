package com.capitalone.dashboard.rest;

import com.capitalone.dashboard.misc.HygieiaException;
import com.capitalone.dashboard.model.DataResponse;
import com.capitalone.dashboard.model.deploy.Environment;
import com.capitalone.dashboard.request.DeployDataCreateRequest;
import com.capitalone.dashboard.service.DeployService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
public class DeployController {


    private final DeployService deployService;

    @Autowired
    public DeployController(DeployService deployService) {
        this.deployService = deployService;
    }

    @RequestMapping(value = "/deploy/status/{componentId}", method = GET, produces = APPLICATION_JSON_VALUE)
    public DataResponse<List<Environment>> deployStatus(@PathVariable ObjectId componentId) {
        return deployService.getDeployStatus(componentId);
    }

    @RequestMapping(value = "/deploy/status/application/{applicationName}", method = GET, produces = APPLICATION_JSON_VALUE)
    public DataResponse<List<Environment>> deployStatus(@PathVariable String applicationName) {
        return deployService.getDeployStatus(applicationName);
    }

    @RequestMapping(value = "/deploy", method = POST,
            consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<String> createBuild(@Valid @RequestBody DeployDataCreateRequest request) throws HygieiaException {
        String response = deployService.create(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }
}
