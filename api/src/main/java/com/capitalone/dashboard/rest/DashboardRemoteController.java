
package com.capitalone.dashboard.rest;

import com.capitalone.dashboard.misc.HygieiaException;
import com.capitalone.dashboard.model.Dashboard;
import com.capitalone.dashboard.request.DashboardRemoteRequest;
import com.capitalone.dashboard.service.DashboardRemoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
public class DashboardRemoteController {

//    private static final Logger LOGGER = LoggerFactory.getLogger(DashboardRemoteController.class);
    private final DashboardRemoteService dashboardRemoteService;


    @Autowired
    public DashboardRemoteController(DashboardRemoteService dashboardRemoteService) {
        this.dashboardRemoteService = dashboardRemoteService;
    }

    @RequestMapping(value = "/dashboard/remoteCreate", method = POST,
            consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<String> remoteCreateDashboard(@Valid @RequestBody DashboardRemoteRequest request) {
        try {
            Dashboard dashboard = dashboardRemoteService.remoteCreate(request, false);
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body("Successfully created dashboard: id =" + dashboard.getId());
        } catch (HygieiaException he) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Failed to create dashboard. Error: " + he.getMessage());
        }
    }

    @RequestMapping(value = "/dashboard/remoteUpdate", method = POST,
            consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<String> remoteUpdateDashboard(@Valid @RequestBody DashboardRemoteRequest request) {
        try {
            Dashboard dashboard = dashboardRemoteService.remoteCreate(request, true);
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body("Successfully updated dashboard: id =" + dashboard.getId());
        } catch (HygieiaException he) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Failed to update dashboard. Error: " + he.getMessage());
        }
    }
}
