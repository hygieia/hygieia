package com.capitalone.dashboard.rest;


import com.capitalone.dashboard.model.CloudSubNetwork;
import com.capitalone.dashboard.response.CloudSubNetworkAggregatedResponse;
import com.capitalone.dashboard.service.CloudSubnetService;
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
public class CloudSubnetController {
    private final CloudSubnetService cloudSubnetService;

    @Autowired
    public CloudSubnetController(CloudSubnetService cloudSubnetService) {
        this.cloudSubnetService = cloudSubnetService;

    }

    // Cloud Subnet End Points

    @RequestMapping(value = "/cloud/subnet/create", method = POST, consumes = APPLICATION_JSON_VALUE,
            produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ObjectId>> upsertSubNetwork(
            @Valid @RequestBody List<CloudSubNetwork> request) {
        return ResponseEntity.ok().body(cloudSubnetService.upsertSubNetwork(request));
    }

    @RequestMapping(value = "/cloud/subnet/details/component/{componentId}", method = GET, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Collection<CloudSubNetwork>> getSubNetworkDetails(
            @PathVariable ObjectId componentId) {
        return ResponseEntity.ok().body(cloudSubnetService.getSubNetworkDetails(componentId));
    }

    @RequestMapping(value = "/cloud/subnet/details/subnet/{subnetId}", method = GET, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<CloudSubNetwork> getSubNetworkDetails(
            @PathVariable String subnetId) {
        return ResponseEntity.ok().body(cloudSubnetService.getSubNetworkDetails(subnetId));
    }

    @RequestMapping(value = "/cloud/subnet/aggregate/{componentId}", method = GET, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<CloudSubNetworkAggregatedResponse> getSubNetworkAggregatedData(
            @PathVariable ObjectId componentId) {
        return ResponseEntity.ok().body(cloudSubnetService.getSubNetworkAggregatedData(componentId));
    }
}
