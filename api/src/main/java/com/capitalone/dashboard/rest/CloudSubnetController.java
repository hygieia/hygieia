package com.capitalone.dashboard.rest;


import com.capitalone.dashboard.model.CloudSubNetwork;
import com.capitalone.dashboard.model.NameValue;
import com.capitalone.dashboard.request.CloudInstanceListRefreshRequest;
import com.capitalone.dashboard.request.CloudSubnetCreateRequest;
import com.capitalone.dashboard.response.CloudSubNetworkAggregatedResponse;
import com.capitalone.dashboard.service.CloudSubnetService;
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

    @RequestMapping(value = "/cloud/subnet/refresh", method = POST, consumes = APPLICATION_JSON_VALUE,
            produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Collection<String>> refreshInstances(
            @Valid @RequestBody CloudInstanceListRefreshRequest request) {
        return ResponseEntity.ok().body(cloudSubnetService.refreshSubnets(request));
    }

    @RequestMapping(value = "/cloud/subnet/create", method = POST, consumes = APPLICATION_JSON_VALUE,
            produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<List<String>> upsertSubNetwork(
            @Valid @RequestBody List<CloudSubnetCreateRequest> request) {
        return ResponseEntity.ok().body(cloudSubnetService.upsertSubNetwork(request));
    }

    @RequestMapping(value = "/cloud/subnet/details/component/{componentId}", method = GET, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Collection<CloudSubNetwork>> getSubNetworkDetailsByComponentId(
            @PathVariable String componentId) {
        return ResponseEntity.ok().body(cloudSubnetService.getSubNetworkDetailsByComponentId(componentId));
    }

    @RequestMapping(value = "/cloud/subnet/details/account/{accountNumber}", method = GET, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Collection<CloudSubNetwork>> getSubNetworkDetailsByAccount(
            @PathVariable String accountNumber) {
        return ResponseEntity.ok().body(cloudSubnetService.getSubNetworkDetailsByAccount(accountNumber));
    }

    @RequestMapping(value = "/cloud/subnet/details/subnets/{subnetIds}", method = GET, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Collection<CloudSubNetwork>> getSubNetworkDetailsBySubnetIds(
            @PathVariable List<String> subnetIds) {
        return ResponseEntity.ok().body(cloudSubnetService.getSubNetworkDetailsBySubnetIds(subnetIds));
    }

    @RequestMapping(value = "/cloud/subnet/details/tags", method = POST, consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Collection<CloudSubNetwork>> getSubNetworkDetailsByTag(
            @Valid @RequestBody List<NameValue> tags) {
        return ResponseEntity.ok().body(cloudSubnetService.getSubNetworkDetailsByTags(tags));
    }

    @RequestMapping(value = "/cloud/subnet/aggregate/{componentId}", method = GET, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<CloudSubNetworkAggregatedResponse> getSubNetworkAggregatedData(
            @PathVariable String componentId) {
        return ResponseEntity.ok().body(cloudSubnetService.getSubNetworkAggregatedData(componentId));
    }
}
