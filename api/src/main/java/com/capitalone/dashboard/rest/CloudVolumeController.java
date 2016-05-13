package com.capitalone.dashboard.rest;

import com.capitalone.dashboard.model.CloudVolumeStorage;
import com.capitalone.dashboard.model.NameValue;
import com.capitalone.dashboard.request.CloudVolumeCreateRequest;
import com.capitalone.dashboard.request.CloudVolumeListRefreshRequest;
import com.capitalone.dashboard.response.CloudVolumeAggregatedResponse;
import com.capitalone.dashboard.service.CloudVolumeService;
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
public class CloudVolumeController {
    private final CloudVolumeService cloudVolumeService;


    @Autowired
    public CloudVolumeController(CloudVolumeService cloudVolumeService) {
        this.cloudVolumeService = cloudVolumeService;

    }

    //Cloud Volume Endpoints
    @RequestMapping(value = "/cloud/volume/refresh", method = POST, consumes = APPLICATION_JSON_VALUE,
            produces = APPLICATION_JSON_VALUE)

    public ResponseEntity<Collection<String>> refreshVolumes(
            @Valid @RequestBody CloudVolumeListRefreshRequest request) {
        return ResponseEntity.ok().body(cloudVolumeService.refreshVolumes(request));
    }

    @RequestMapping(value = "/cloud/volume/create", method = POST, consumes = APPLICATION_JSON_VALUE,
            produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<List<String>> upsertVolume(
            @Valid @RequestBody List<CloudVolumeCreateRequest> request) {
        return ResponseEntity.ok().body(cloudVolumeService.upsertVolume(request));
    }


    @RequestMapping(value = "/cloud/volume/details/component/{componentId}", method = GET, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Collection<CloudVolumeStorage>> getVolumeDetailsByComponentId(
            @PathVariable String componentId) {
        return ResponseEntity.ok().body(cloudVolumeService.getVolumeDetailsByComponentId(componentId));
    }

    @RequestMapping(value = "/cloud/volume/details/volumes/{volumeIds}", method = GET, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Collection<CloudVolumeStorage>> getVolumeDetailsByVolumeIds(
            @PathVariable List<String> volumeIds) {
        return ResponseEntity.ok().body(cloudVolumeService.getVolumeDetailsByVolumeIds(volumeIds));
    }

    @RequestMapping(value = "/cloud/volume/details/instanceIds", method = POST, consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Collection<CloudVolumeStorage>> getVolumeDetailsByInstanceIds(@Valid @RequestBody List<String> instanceIds) {
        return ResponseEntity.ok().body(cloudVolumeService.getVolumeDetailsByInstanceIds(instanceIds));
    }


    @RequestMapping(value = "/cloud/volume/details/account/{accountNumber}", method = GET, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Collection<CloudVolumeStorage>> getVolumeDetailsByAccount(
            @PathVariable String accountNumber) {
        return ResponseEntity.ok().body(cloudVolumeService.getVolumeDetailsByAccount(accountNumber));
    }

    @RequestMapping(value = "/cloud/volume/details/tags", method = POST, consumes = APPLICATION_JSON_VALUE,
            produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Collection<CloudVolumeStorage>> getVolumeDetailsByTags(
            @Valid @RequestBody List<NameValue> tags) {
        return ResponseEntity.ok().body(cloudVolumeService.getVolumeDetailsByTags(tags));
    }

    @RequestMapping(value = "/cloud/volume/aggregate/component/{componentId}", method = GET, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<CloudVolumeAggregatedResponse> getVolumeAggregatedData(
            @PathVariable String componentId) {
        return ResponseEntity.ok().body(cloudVolumeService.getVolumeAggregatedData(componentId));
    }
}
