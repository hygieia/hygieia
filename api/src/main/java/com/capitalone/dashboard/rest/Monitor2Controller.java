package com.capitalone.dashboard.rest;

import com.capitalone.dashboard.model.DataResponse;
import com.capitalone.dashboard.model.Monitor2;
import com.capitalone.dashboard.request.Monitor2DataCreateRequest;
import com.capitalone.dashboard.request.Monitor2Request;
import com.capitalone.dashboard.service.Monitor2Service;
import com.capitalone.dashboard.util.Supplier;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;

import javax.validation.Valid;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
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
public class Monitor2Controller {
    private final Monitor2Service monitor2Service;

    @Autowired
    public Monitor2Controller(Monitor2Service monitor2Service) { this.monitor2Service = monitor2Service; }

    // Get all statuses, not sure if needed.
    @RequestMapping(value = "/monitor2", method = GET, produces = APPLICATION_JSON_VALUE)
    public Iterable<Monitor2> monitor2es() { return monitor2Service.all(); }

    // Get all statuses for a dashboard
    @RequestMapping(value = "/dashboard/{id}/monitor2", method = GET, produces = APPLICATION_JSON_VALUE)
    public DataResponse<Map<String, List<Monitor2>>> dashboardMonitor2es(@PathVariable ObjectId id) {
        Map<String, List<Monitor2>> response = new HashMap<>();
        response.put("monitor2es", monitor2Service.dashboardMonitor2es(id));
        return new DataResponse<>(response, System.currentTimeMillis());
    }

    // Create a new status object.
    @RequestMapping(value = "/dashboard/{id}/monitor2", method = POST, consumes = APPLICATION_JSON_VALUE,
            produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Monitor2> createMonitor2(@PathVariable ObjectId id, @Valid @RequestBody
            Monitor2DataCreateRequest monitor2DataCreateRequest) {

        Monitor2 response = monitor2Service.create(id,monitor2DataCreateRequest);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    // Update existing status object
    @RequestMapping(value = "/dashboard/{id}/monitor2/{monitor2Id}", method = PUT, consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<Monitor2> updateMonitor2(@PathVariable ObjectId id, @PathVariable ObjectId monitor2Id,
            @RequestBody Monitor2Request request) {
        return ResponseEntity
                .ok()
                .body(monitor2Service.update(id,request.update(monitor2Service.get(monitor2Id))));
    }

    // Delete existing status object
    @RequestMapping(value = "/dashboard/{id}/monitor2/{monitor2Id}", method = DELETE)
    public ResponseEntity<Void> deleteMonitor2(@PathVariable ObjectId id, @PathVariable ObjectId monitor2Id) {
        monitor2Service.delete(id, monitor2Id);
        return ResponseEntity.noContent().build();
    }
}
