package com.capitalone.dashboard.rest;

import com.capitalone.dashboard.editors.CaseInsensitiveServiceStatusEditor;
import com.capitalone.dashboard.model.DataResponse;
import com.capitalone.dashboard.model.Service;
import com.capitalone.dashboard.model.ServiceStatus;
import com.capitalone.dashboard.request.ServiceRequest;
import com.capitalone.dashboard.service.ServiceService;
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

import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

@RestController
public class ServiceController {
    private final ServiceService serviceService;

    @Autowired
    public ServiceController(ServiceService serviceService) {
        this.serviceService = serviceService;
    }

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(ServiceStatus.class, new CaseInsensitiveServiceStatusEditor());
    }

    @RequestMapping(value = "/service", method = GET, produces = APPLICATION_JSON_VALUE)
    public Iterable<Service> services() {
        return serviceService.all();
    }

    @RequestMapping(value = "/dashboard/{id}/service", method = GET, produces = APPLICATION_JSON_VALUE)
    public DataResponse<Map<String, List<Service>>> dashboardServices(@PathVariable ObjectId id) {
        Map<String, List<Service>> response = new HashMap<>();
        response.put("services", serviceService.dashboardServices(id));
        response.put("dependencies", serviceService.dashboardDependentServices(id));
        return new DataResponse<>(response, System.currentTimeMillis());
    }

    @RequestMapping(value = "/dashboard/{id}/service", method = POST,
            consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Service> createService(@PathVariable ObjectId id, @NotNull @RequestBody String name) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(serviceService.create(id, name));
    }

    @RequestMapping(value = "/dashboard/{id}/service/{serviceId}", method = PUT,
            consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<Service> updateService(@PathVariable ObjectId id,
                                                @PathVariable ObjectId serviceId,
                                                @RequestBody ServiceRequest request) {
        return ResponseEntity
                .ok()
                .body(serviceService.update(id, request.update(serviceService.get(serviceId))));
    }

    @RequestMapping(value = "/dashboard/{id}/service/{serviceId}", method = DELETE)
    public ResponseEntity<Void> deleteService(@PathVariable ObjectId id, @PathVariable ObjectId serviceId) {
        serviceService.delete(id, serviceId);
        return ResponseEntity.noContent().build();
    }

    @RequestMapping(value = "/dashboard/{id}/dependent-service/{serviceId}", method = POST,
            produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Service> addDependentService(@PathVariable ObjectId id, @PathVariable ObjectId serviceId) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(serviceService.addDependentService(id, serviceId));
    }

    @RequestMapping(value = "/dashboard/{id}/dependent-service/{serviceId}", method = DELETE)
    public ResponseEntity<Void> deleteDependentService(@PathVariable ObjectId id, @PathVariable ObjectId serviceId) {
        serviceService.deleteDependentService(id, serviceId);
        return ResponseEntity.noContent().build();
    }
}
