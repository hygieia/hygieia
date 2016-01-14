package com.capitalone.dashboard.rest;

import com.capitalone.dashboard.model.*;
import com.capitalone.dashboard.request.DashboardRequest;
import com.capitalone.dashboard.request.TeamDashboardRequest;
import com.capitalone.dashboard.request.WidgetRequest;
import com.capitalone.dashboard.service.DashboardService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

@RestController
public class DashboardController {

    private final DashboardService dashboardService;

    @Autowired
    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @RequestMapping(value = "/dashboard", method = GET, produces = APPLICATION_JSON_VALUE)
    public Iterable<Dashboard> dashboards() {
        return dashboardService.all();
    }

    @RequestMapping(value = "/dashboard", method = POST,
            consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Dashboard> createDashboard(@Valid @RequestBody DashboardRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(dashboardService.create(request.toDashboard()));
    }

    @RequestMapping(value = "/dashboard/{id}", method = GET,
            produces = APPLICATION_JSON_VALUE)
    public Dashboard getDashboard(@PathVariable ObjectId id) {
        return dashboardService.get(id);
    }

    @RequestMapping(value = "/dashboard/{id}", method = PUT, consumes = APPLICATION_JSON_VALUE)
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

    @RequestMapping(value = "/dashboard/{id}/widget", method = POST,
            consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<WidgetResponse> addWidget(@PathVariable ObjectId id, @RequestBody WidgetRequest request) {

        Dashboard dashboard = dashboardService.get(id);

        Component component = dashboardService.associateCollectorToComponent(
                request.getComponentId(), request.getCollectorItemIds());

        Widget widget = dashboardService.addWidget(dashboard, request.widget());

        return ResponseEntity.status(HttpStatus.CREATED).body(new WidgetResponse(component, widget));
    }

    @RequestMapping(value = "/dashboard/{id}/teamdashboard", method = POST,
            consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<TeamDashboard> addTeamDashboard(@PathVariable ObjectId id, @RequestBody TeamDashboardRequest request) {

        Dashboard programDashboard  = dashboardService.get(id);
        if(!programDashboard.getType().equals(DashboardType.Program)){
            throw new UnsupportedOperationException("Dashboard "+id+ "is not a Program Dashboard");
        }

        Dashboard dashboardToAdd = dashboardService.get(request.getDashboardId());
        TeamDashboard teamDashboard = new TeamDashboard(request.getName(), dashboardToAdd);
        TeamDashboard addedTeamDashboard = dashboardService.addTeamDashboard(programDashboard, teamDashboard);
        return ResponseEntity.status(HttpStatus.CREATED).body(addedTeamDashboard);
    }

    @RequestMapping(value = "/dashboard/{id}/widget/{widgetId}", method = PUT,
            consumes = APPLICATION_JSON_VALUE)
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

    @RequestMapping(value = "/dashboard/mydashboard/{username}", method = GET,
            produces = APPLICATION_JSON_VALUE)
    public List<Dashboard> getOwnedDashboards(@PathVariable String username) {
        List<Dashboard> myDashboard = dashboardService.getOwnedDashboards(username);
        return myDashboard;

    }

    @RequestMapping(value = "/dashboard/myowner/{dashboardtitle}", method = GET,
            produces = APPLICATION_JSON_VALUE)
    public String getDashboardOwner(@PathVariable String dashboardtitle) {
        String dashboardOwner = "No Owner defined";
        if (null != dashboardtitle) {
            dashboardOwner = dashboardService.getDashboardOwner(dashboardtitle);
        }
        return dashboardOwner;
    }
}
