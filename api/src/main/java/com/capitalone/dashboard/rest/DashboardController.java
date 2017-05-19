package com.capitalone.dashboard.rest;

import com.capitalone.dashboard.auth.access.DashboardOwnerOrAdmin;
import com.capitalone.dashboard.misc.HygieiaException;
import com.capitalone.dashboard.model.*;
import com.capitalone.dashboard.request.DashboardRequest;
import com.capitalone.dashboard.request.DashboardRequestTitle;
import com.capitalone.dashboard.request.WidgetRequest;
import com.capitalone.dashboard.service.CmdbService;
import com.capitalone.dashboard.service.DashboardService;
import com.capitalone.dashboard.util.PaginationHeaderUtility;
import com.google.common.collect.Lists;
import org.bson.types.ObjectId;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.*;

@RestController
public class DashboardController {

    private static final Logger LOGGER = LoggerFactory.getLogger(DashboardController.class);
    private final DashboardService dashboardService;
    private final CmdbService cmdbService;
    private PaginationHeaderUtility paginationHeaderUtility;

    @Autowired
    public DashboardController(DashboardService dashboardService,CmdbService cmdbService, PaginationHeaderUtility paginationHeaderUtility) {
        this.dashboardService = dashboardService;
        this.cmdbService = cmdbService;
        this.paginationHeaderUtility = paginationHeaderUtility;
    }

    @RequestMapping(value = "/dashboard", method = GET, produces = APPLICATION_JSON_VALUE)
    public Iterable<Dashboard> dashboards() {
        return dashboardService.all();
    }

    @RequestMapping(value = "/dashboard", method = POST,
            consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Dashboard> createDashboard(@Valid @RequestBody DashboardRequest request) {
        try {
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(dashboardService.create(request.toDashboard()));
        } catch (HygieiaException he) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(null);
        }
    }


    @RequestMapping(value = "/dashboard/{id}", method = GET,
            produces = APPLICATION_JSON_VALUE)
    public Dashboard getDashboard(@PathVariable ObjectId id) {
        return dashboardService.get(id);
    }

    @DashboardOwnerOrAdmin
    @RequestMapping(value = "/dashboard/{id}", method = PUT, consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<String> updateDashboard(@PathVariable ObjectId id,
                                                  @RequestBody DashboardRequest request) {
        try {
            dashboardService.update(request.copyTo(dashboardService.get(id)));
            return ResponseEntity.ok("Updated");
        } catch (HygieiaException he) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(null);
        }
    }

    @RequestMapping(path = "/dashboard/addOwner/{id}", method = PUT, consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<UserInfo> addOwner(@PathVariable ObjectId id, @RequestBody UserInfo user) {
        UserInfo savedUser = dashboardService.promoteToOwner(id, user.getUsername(), user.getAuthType());

        return new ResponseEntity<UserInfo>(savedUser, HttpStatus.OK);
    }

    @RequestMapping(path = "/dashboard/removeOwner/{id}", method = PUT, consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<UserInfo> removeOwner(@PathVariable ObjectId id, @RequestBody UserInfo user) {
        UserInfo savedUser = dashboardService.demoteFromOwner(id, user.getUsername(), user.getAuthType());

        return new ResponseEntity<UserInfo>(savedUser, HttpStatus.OK);
    }

    @DashboardOwnerOrAdmin
    @RequestMapping(value = "/dashboard/rename/{id}", method = PUT, consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<String> renameDashboard(@PathVariable ObjectId id,
    		@Valid @RequestBody DashboardRequestTitle request) {


        Dashboard dashboard = getDashboard(id);
        String existingTitle = dashboard.getTitle();
        String newTitle = request.getTitle();

        //no change to title is ok
        if (existingTitle.equalsIgnoreCase(newTitle)) {
            return ResponseEntity.ok("Unchanged");
        }

        Iterable<Dashboard> allDashboard = dashboards();
        boolean titleExist = false;

        for(Dashboard l :allDashboard)
        {
            if (id.compareTo(l.getId()) == 0) {
                //skip the current dashboard
                continue;
            }
            if(l.getTitle().equals(request.getTitle()))
            {
                titleExist=true;
            }
        }

        LOGGER.info("Existing Title:" + titleExist);
        //check if any other dashboard has the same title

        if (!titleExist) {
            try {
                dashboard.setTitle(request.getTitle());
                dashboardService.update(dashboard);
                return ResponseEntity.ok("Renamed");
            } catch (HygieiaException he) {
                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body(null);
            }
        } else {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(null);
        }
    }

    @DashboardOwnerOrAdmin
    @RequestMapping(value = "/dashboard/{id}", method = DELETE)
    public ResponseEntity<Void> deleteDashboard(@PathVariable ObjectId id) {
        dashboardService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @DashboardOwnerOrAdmin
    @RequestMapping(value = "/dashboard/{id}/widget", method = POST,
            consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<WidgetResponse> addWidget(@PathVariable ObjectId id, @RequestBody WidgetRequest request) {

        Dashboard dashboard = dashboardService.get(id);

        Component component = dashboardService.associateCollectorToComponent(
                request.getComponentId(), request.getCollectorItemIds());

        Widget widget = dashboardService.addWidget(dashboard, request.widget());

        return ResponseEntity.status(HttpStatus.CREATED).body(new WidgetResponse(component, widget));
    }

    @DashboardOwnerOrAdmin
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

    @RequestMapping(value = "/dashboard/mydashboard", method = GET,
            produces = APPLICATION_JSON_VALUE)
    public List<Dashboard> getOwnedDashboards() {
    	List<Dashboard> myDashboard = dashboardService.getOwnedDashboards();
        return myDashboard;

    }

    @RequestMapping(value = "/dashboard/allUsers/{id}", method = GET,
            produces = APPLICATION_JSON_VALUE)
    public Iterable<UserInfo> getAllUsers(@PathVariable ObjectId id) {

        Iterable<UserInfo> allUsers = dashboardService.getAllUsers();

        ArrayList<Owner> owners = Lists.newArrayList(getOwners(id));
        for(UserInfo user: allUsers) {
            if (owners.contains(new Owner(user.getUsername(), user.getAuthType()))) {
                Collection<UserRole> roles = user.getAuthorities();
                //here ROLE_ADMIN = owner of dashboard
                //this is only to display on the UI and we don't update the user_info collection
                roles.add(UserRole.ROLE_ADMIN);
                user.setAuthorities(roles);
            }
        }

        return allUsers;
    }

    @RequestMapping(value = "/dashboard/owners/{id}", method = GET,
            produces = APPLICATION_JSON_VALUE)
    public Iterable<Owner> getOwners(@PathVariable ObjectId id) {
        return dashboardService.getOwners(id);
    }

    @Deprecated
    @RequestMapping(value = "/dashboard/myowner/{id}", method = GET,
            produces = APPLICATION_JSON_VALUE)
    public String getDashboardOwner(@PathVariable ObjectId id) {
    	return "Authorized";
    }

    @RequestMapping(value = "/dashboard/component/{componentId}", method = GET,
            produces = APPLICATION_JSON_VALUE)
    public Component getComponentForDashboard(@PathVariable ObjectId componentId) {
        Component component = new Component();
        if (null != componentId) {
            component = dashboardService.getComponent(componentId);
        }
        return component;
    }

    @RequestMapping(value = "/dashboard/configItem/{itemType}", method = GET,
            produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Cmdb>> getConfigItemByType(@PathVariable String itemType, @RequestParam(value = "search", required = false, defaultValue = "") String descriptionFilter, @PageableDefault(size = Integer.MAX_VALUE) Pageable pageable) {

        Page<Cmdb> pageOfConfigurationItems = cmdbService.configurationItemsByTypeWithFilter(itemType, descriptionFilter,pageable);

        return ResponseEntity
                .ok()
                .headers(paginationHeaderUtility.buildPaginationHeaders(pageOfConfigurationItems))
                .body(pageOfConfigurationItems.getContent());

    }

}
