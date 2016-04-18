package com.capitalone.dashboard.rest;


import com.capitalone.dashboard.model.CloudVirtualNetwork;
import com.capitalone.dashboard.response.CloudVirtualNetworkAggregatedResponse;
import com.capitalone.dashboard.service.CloudVirtualNetworkService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Collection;
import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
public class CloudVirtualNetworkController {
    private final CloudVirtualNetworkService cloudVirtualNetworkService;

    @Autowired
    public CloudVirtualNetworkController(CloudVirtualNetworkService cloudVirtualNetworkService) {
        this.cloudVirtualNetworkService = cloudVirtualNetworkService;

    }
    //Cloud Virtual Network Endpoints

    @RequestMapping(value = "/cloud/virtualnetwork/create", method = POST, consumes = APPLICATION_JSON_VALUE,
            produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ObjectId>> upsertVirtualNetwork(
            @Valid @RequestBody List<CloudVirtualNetwork> request) {
        return ResponseEntity.ok().body(cloudVirtualNetworkService.upsertVirtualNetwork(request));
    }

    @RequestMapping(value = "/cloud/virtualnetwork/details/component/{componentId}", method = GET, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Collection<CloudVirtualNetwork>> getVirtualNetworkDetails(
            @PathVariable ObjectId componentId) {
        return ResponseEntity.ok().body(cloudVirtualNetworkService.getVirtualNetworkDetails(componentId));
    }

    @RequestMapping(value = "/cloud/virtualnetwork/details/netrwork/{virtualNetworkId}", method = GET, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<CloudVirtualNetwork> getVirtualNetworkDetails(
            @PathVariable String virtualNetworkId) {
        return ResponseEntity.ok().body(cloudVirtualNetworkService.getVirtualNetworkDetails(virtualNetworkId));
    }

    @RequestMapping(value = "/cloud/virtualnetwork/aggregate/component/{componentId}", method = GET, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<CloudVirtualNetworkAggregatedResponse> getVirtualNetworkAggregatedData(
            @PathVariable ObjectId componentId) {
        return ResponseEntity.ok().body(cloudVirtualNetworkService.getVirtualNetworkAggregated(componentId));
    }


}
