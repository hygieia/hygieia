package com.capitalone.dashboard.rest;

import com.capitalone.dashboard.misc.HygieiaException;
import com.capitalone.dashboard.model.CloudInstance;
import com.capitalone.dashboard.model.CloudInstanceHistory;
import com.capitalone.dashboard.model.NameValue;
import com.capitalone.dashboard.request.CloudInstanceCreateRequest;
import com.capitalone.dashboard.request.CloudInstanceListRefreshRequest;
import com.capitalone.dashboard.service.CloudInstanceService;
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
public class CloudInstanceController {
    private final CloudInstanceService cloudInstanceService;


    @Autowired
    public CloudInstanceController(CloudInstanceService cloudInstanceService) {
        this.cloudInstanceService = cloudInstanceService;

    }

    //Cloud Instance Endpoints
    @RequestMapping(value = "/cloud/instance/refresh", method = POST, consumes = APPLICATION_JSON_VALUE,
            produces = APPLICATION_JSON_VALUE)

    public ResponseEntity<Collection<String>> refreshInstances(
            @Valid @RequestBody CloudInstanceListRefreshRequest request) {
        return ResponseEntity.ok().body(cloudInstanceService.refreshInstances(request));
    }

    @RequestMapping(value = "/cloud/instance/create", method = POST, consumes = APPLICATION_JSON_VALUE,
            produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<List<String>> upsertInstance(
            @Valid @RequestBody List<CloudInstanceCreateRequest> request) throws HygieiaException {
        return ResponseEntity.ok().body(cloudInstanceService.upsertInstance(request));
    }


    @RequestMapping(value = "/cloud/instance/details/component/{componentId}", method = GET, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Collection<CloudInstance>> getInstanceDetailsByComponentId(
            @PathVariable String componentId) {
        return ResponseEntity.ok().body(cloudInstanceService.getInstanceDetailsByComponentId(componentId));
    }

    @RequestMapping(value = "/cloud/instance/details/account/{accountNumber}", method = GET, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Collection<CloudInstance>> getInstanceDetailsByAccount(
            @PathVariable String accountNumber) {
        return ResponseEntity.ok().body(cloudInstanceService.getInstanceDetailsByAccount(accountNumber));
    }


    @RequestMapping(value = "/cloud/instance/details/instances/{instanceIds}", method = GET,
            produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Collection<CloudInstance>> getInstanceDetailsByInstanceIds(
             @PathVariable List<String> instanceIds) {
        return ResponseEntity.ok().body(cloudInstanceService.getInstanceDetailsByInstanceIds(instanceIds));
    }


    @RequestMapping(value = "/cloud/instance/details/tags", method = POST, consumes = APPLICATION_JSON_VALUE,
            produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Collection<CloudInstance>> getInstanceDetailsByTags(
            @Valid @RequestBody List<NameValue> tags) {
        return ResponseEntity.ok().body(cloudInstanceService.getInstanceDetailsByTags(tags));
    }

    @RequestMapping(value = "/cloud/instance/history/account/{accountNumber}", method = GET, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Collection<CloudInstanceHistory>> getInstanceAggregatedDataByAccount(
            @PathVariable String accountNumber) {
        return ResponseEntity.ok().body(cloudInstanceService.getInstanceHistoryByAccount(accountNumber));
    }
}
