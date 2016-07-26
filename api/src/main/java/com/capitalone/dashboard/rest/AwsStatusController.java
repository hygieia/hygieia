package com.capitalone.dashboard.rest;

import com.capitalone.dashboard.model.DataResponse;
import com.capitalone.dashboard.model.AwsStatus;
import com.capitalone.dashboard.request.AwsStatusDataCreateRequest;
import com.capitalone.dashboard.request.AwsStatusRequest;
import com.capitalone.dashboard.service.AwsStatusService;
import com.capitalone.dashboard.service.AwsStatusServiceImpl;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.Console;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

// Handles the routing of requests for the Aws Status objects in the db.
@RestController
public class AwsStatusController {
    private final AwsStatusService awsStatusService;

    @Autowired
    public AwsStatusController(AwsStatusService awsStatusService) { this.awsStatusService = awsStatusService; }

    // Get all statuses, not sure if needed.
    @RequestMapping(value = "/awsStatus", method = GET, produces = APPLICATION_JSON_VALUE)
    public Iterable<AwsStatus> awsStatuses() { return awsStatusService.all(); }

    // Get all statuses for a dashboard
    @RequestMapping(value = "/dashboard/{id}/awsStatus", method = GET, produces = APPLICATION_JSON_VALUE)
    public DataResponse<Map<String, List<AwsStatus>>> dashboardAwsStatuses(@PathVariable ObjectId id) {
        Map<String, List<AwsStatus>> response = new HashMap<>();
        response.put("awsStatuses", awsStatusService.dashboardAwsStatuses(id));
        return new DataResponse<>(response, System.currentTimeMillis());
    }

    // Create a new status object.
    @RequestMapping(value = "/dashboard/{id}/awsStatus", method = POST, consumes = APPLICATION_JSON_VALUE,
            produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<AwsStatus> createAwsStatus(@PathVariable ObjectId id, @Valid @RequestBody
            AwsStatusDataCreateRequest awsStatusDataCreateRequest) {

        AwsStatus response = awsStatusService.create(id,awsStatusDataCreateRequest);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    // Update existing status object
    @RequestMapping(value = "/dashboard/{id}/awsStatus/{awsStatusId}", method = PUT, consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<AwsStatus> updateAwsStatus(@PathVariable ObjectId id, @PathVariable ObjectId awsStatusId,
            @RequestBody AwsStatusRequest request) {
        return ResponseEntity
                .ok()
                .body(awsStatusService.update(id,request.update(awsStatusService.get(awsStatusId))));
    }

    // Delete existing status object
    @RequestMapping(value = "/dashboard/{id}/awsStatus/{awsStatusId}", method = DELETE)
    public ResponseEntity<Void> deleteAwsStatus(@PathVariable ObjectId id, @PathVariable ObjectId awsStatusId) {
        awsStatusService.delete(id, awsStatusId);
        return ResponseEntity.noContent().build();
    }
}
