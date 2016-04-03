package com.capitalone.dashboard.rest;

import com.capitalone.dashboard.model.CloudComputeData;
import com.capitalone.dashboard.model.CloudComputeInstanceData;
import com.capitalone.dashboard.model.CollectorItem;
import com.capitalone.dashboard.model.CollectorType;
import com.capitalone.dashboard.model.DataResponse;
import com.capitalone.dashboard.request.CollectorItemRequest;
import com.capitalone.dashboard.service.CloudService;
import com.capitalone.dashboard.service.CollectorService;
import com.capitalone.dashboard.service.EncryptionService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

/**
 * REST service managing all requests to the feature repository.
 */
@RestController
public class CloudController {
    private final CloudService cloudService;
    private final EncryptionService encryptionService;
    private final CollectorService collectorService;

    @Autowired
    public CloudController(EncryptionService encryptionService,
                           CloudService cloudService, CollectorService collectorService) {
        this.cloudService = cloudService;
        this.encryptionService = encryptionService;
        this.collectorService = collectorService;
    }

    @RequestMapping(value = "/cloud/{componentId}/aggregate", method = GET, consumes =
            APPLICATION_JSON_VALUE,
            produces = APPLICATION_JSON_VALUE)
    public DataResponse<CloudComputeData> getAggregatedData(
            @Valid ObjectId componentId) {
        return cloudService.getAggregatedData(componentId);
    }

    @RequestMapping(value = "/cloud/{componentId}/details", method = GET, consumes =
            APPLICATION_JSON_VALUE,
            produces = APPLICATION_JSON_VALUE)
    public DataResponse<List<CloudComputeInstanceData>> getInstanceDetails(
            @Valid ObjectId componentId) {
        return cloudService.getInstanceDetails(componentId);
    }

    @RequestMapping(value = "/cloud/config", method = POST, consumes = APPLICATION_JSON_VALUE,
            produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<CollectorItem> createCloudConfigCollectorItem(
            @Valid @RequestBody CollectorItemRequest request) {

        final String ACCESS_KEY = "accessKey";
        final String SECRET_KEY = "secretKey";
        @SuppressWarnings("unused")
        final String PROVIDER = "cloudProvider";

        CollectorItem item = null;

        List<CollectorItem> items = collectorService.collectorItemsByType(CollectorType.Cloud);
        for (CollectorItem i : items) {
            if (i.getCollectorId().equals(request.getCollectorId()) &&
                    request.getOptions().equals(i.getOptions())) {
                item = i;
                break;
            }
        }

        if (item == null) {
            String encAccessKey = encryptionService.encrypt((String) request
                    .getOptions().get(ACCESS_KEY));
            String encSecretKey = encryptionService.encrypt((String) request
                    .getOptions().get(SECRET_KEY));
            if (!"ERROR".equalsIgnoreCase(encAccessKey)
                    && !"ERROR".equalsIgnoreCase(encSecretKey)) {
                request.getOptions().put(ACCESS_KEY, encAccessKey);
                request.getOptions().put(SECRET_KEY, encSecretKey);

                item = collectorService.createCollectorItem(request
                        .toCollectorItem());
                return ResponseEntity.status(HttpStatus.CREATED).body(item);
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(request.toCollectorItem());
            }
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(item);
    }
}
