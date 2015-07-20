package com.capitalone.dashboard.rest;

import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

import java.util.List;

import javax.validation.Valid;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.capitalone.dashboard.model.Component;
import com.capitalone.dashboard.model.Dashboard;
import com.capitalone.dashboard.model.Widget;
import com.capitalone.dashboard.model.WidgetResponse;
import com.capitalone.dashboard.request.DashboardRequest;
import com.capitalone.dashboard.request.WidgetRequest;
import com.capitalone.dashboard.service.DashboardService;

@RestController
public class DashboardController {
    private static final String JSON = MediaType.APPLICATION_JSON_VALUE;

    private final DashboardService dashboardService;

    @Autowired
    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @RequestMapping(value = "/dashboard", method = GET, produces = JSON)
    public Iterable<Dashboard> dashboards() {
        return dashboardService.all();
    }

    @RequestMapping(value = "/dashboard", method = POST, consumes = JSON, produces = JSON)
    public ResponseEntity<Dashboard> createDashboard(@Valid @RequestBody DashboardRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(dashboardService.create(request.toDashboard()));
    }

    @RequestMapping(value = "/dashboard/{id}", method = GET, produces = JSON)
    public Dashboard getDashboard(@PathVariable ObjectId id) {
        return dashboardService.get(id);
    }

    @RequestMapping(value = "/dashboard/{id}", method = PUT, consumes = JSON)
    public ResponseEntity<String> updateDashboard(@PathVariable ObjectId id,
                                                  @RequestBody DashboardRequest request) {
        dashboardService.update(request.copyTo(dashboardService.get(id)));
        return ResponseEntity.ok("Updated");
    }

    @RequestMapping(value = "/dashboard/{id}", method = DELETE)
    public ResponseEntity deleteDashboard(@PathVariable ObjectId id) {
        dashboardService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @RequestMapping(value = "/dashboard/{id}/widget", method = POST, consumes = JSON)
    public ResponseEntity<WidgetResponse> addWidget(@PathVariable ObjectId id, @RequestBody WidgetRequest request) {
        Component component = dashboardService.associateCollectorToComponent(
                request.getComponentId(), request.getCollectorItemIds());

        Widget widget = dashboardService.addWidget(dashboardService.get(id), request.widget());

        return ResponseEntity.status(HttpStatus.CREATED).body(new WidgetResponse(component, widget));
    }

    @RequestMapping(value = "/dashboard/{id}/widget/{widgetId}", method = PUT, consumes = JSON)
    public ResponseEntity<WidgetResponse> updateWidget(@PathVariable ObjectId id,
                                                       @PathVariable ObjectId widgetId,
                                                       @RequestBody WidgetRequest request) {
        Component component = dashboardService.associateCollectorToComponent(
                request.getComponentId(), request.getCollectorItemIds());

        Dashboard dashboard = dashboardService.get(id);
        Widget widget = request.updateWidget(dashboardService.getWidget(dashboard, widgetId));
        widget = dashboardService.updateWidget(dashboard, widget);

        return ResponseEntity.ok().body(new WidgetResponse(component, widget));
    }

    @RequestMapping(value = "/dashboard/mydashboard/{username}", method = GET, produces = JSON)
    public List<Dashboard> getOwnedDashboards(@PathVariable String username) {
        List<Dashboard> myDashboard = dashboardService.getOwnedDashboards(username);
        return myDashboard;

    }

    @RequestMapping(value = "/dashboard/myowner/{dashboardtitle}", method = GET, produces = JSON)
    public String getDashboardOwner(@PathVariable String dashboardtitle) {
        String dashboardOwner = "No Owner defined";
        if (null != dashboardtitle) {
            System.out.println("Dashboard Title is:" + dashboardtitle);
            dashboardOwner = dashboardService.getDashboardOwner(dashboardtitle);
        }
        return dashboardOwner;
    }
}
