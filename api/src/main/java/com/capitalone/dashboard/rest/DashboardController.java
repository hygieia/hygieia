
package com.capitalone.dashboard.rest;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

import java.util.List;

import javax.validation.Valid;

import com.capitalone.dashboard.util.PaginationHeaderUtility;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.capitalone.dashboard.auth.access.DashboardOwnerOrAdmin;
import com.capitalone.dashboard.misc.HygieiaException;
import com.capitalone.dashboard.model.Component;
import com.capitalone.dashboard.model.Dashboard;
import com.capitalone.dashboard.model.Owner;
import com.capitalone.dashboard.model.Widget;
import com.capitalone.dashboard.model.WidgetResponse;
import com.capitalone.dashboard.model.ScoreDisplayType;
import com.capitalone.dashboard.request.DashboardRequest;
import com.capitalone.dashboard.request.DashboardRequestTitle;
import com.capitalone.dashboard.request.WidgetRequest;
import com.capitalone.dashboard.service.DashboardService;

@RestController
public class DashboardController {

    private static final Logger LOGGER = LoggerFactory.getLogger(DashboardController.class);
    private final DashboardService dashboardService;
    private PaginationHeaderUtility paginationHeaderUtility;


    @Autowired
    public DashboardController(DashboardService dashboardService, PaginationHeaderUtility paginationHeaderUtility) {
        this.dashboardService = dashboardService;
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
            Dashboard dashboard = request.toDashboard();
            dashboard.setErrorMessage(he.getMessage());
            dashboard.setErrorCode(he.getErrorCode());
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(dashboard);
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

    @DashboardOwnerOrAdmin
    @RequestMapping(path = "/dashboard/{id}/owners", method = PUT, consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<Iterable<Owner>> updateOwners(@PathVariable ObjectId id, @RequestBody Iterable<Owner> owners) {
    	return new ResponseEntity<Iterable<Owner>>(dashboardService.updateOwners(id, owners), HttpStatus.ACCEPTED);
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


        boolean titleExist = false;
        List<Dashboard> existingDashboardList = dashboardService.getByTitle(newTitle);
        if( existingDashboardList != null && !existingDashboardList.isEmpty()){
            titleExist=true;
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

    @RequestMapping(value = "/dashboard/{id}/owners", method = GET,
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
    @RequestMapping(value = "/dashboard/configItemApp/{configItem}", method = GET,
            produces = APPLICATION_JSON_VALUE)
    public ResponseEntity getDashboardByApp(@PathVariable String configItem) {
        try{
            return ResponseEntity.status(HttpStatus.OK).body(dashboardService.getByBusinessService(configItem));
        } catch (HygieiaException he) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(he.getMessage());
        }
    }
    @RequestMapping(value = "/dashboard/configItemComponent/{configItem}", method = GET,
            produces = APPLICATION_JSON_VALUE)
    public ResponseEntity getDashboardByComp(@PathVariable String configItem) {
        try{
            return ResponseEntity.status(HttpStatus.OK).body(dashboardService.getByBusinessApplication(configItem));
        } catch (HygieiaException he) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(he.getMessage());
        }
    }
    @RequestMapping(value = "/dashboard/configItemComponentAndApp/{configItemComp}/{configItemApp}", method = GET,
            produces = APPLICATION_JSON_VALUE)
    public ResponseEntity getDashboardByCompAndApp(@PathVariable String configItemComp,@PathVariable String configItemApp) {
        try{
            return ResponseEntity.status(HttpStatus.OK).body(dashboardService.getByServiceAndApplication(configItemComp,configItemApp));
        } catch (HygieiaException he) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(he.getMessage());
        }
    }

    @DashboardOwnerOrAdmin
    @RequestMapping(value = "/dashboard/updateBusItems/{id}", method = PUT, consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<String> updateDashboardBusinessItems(@PathVariable ObjectId id, @RequestBody Dashboard request) {
        try {
            Dashboard dashboard = dashboardService.updateDashboardBusinessItems(id, request);
            if(dashboard != null){
                return ResponseEntity.ok("Updated");
            }else{
                return ResponseEntity.ok("Unchanged");
            }

        } catch (HygieiaException he) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(he.getMessage());
        }

    }

    @DashboardOwnerOrAdmin
    @RequestMapping(value = "/dashboard/updateDashboardWidgets/{id}", method = PUT, consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<String> updateDashboardWidgets(@PathVariable ObjectId id, @RequestBody Dashboard request) {
        try {
            Dashboard dashboard = dashboardService.updateDashboardWidgets(id, request);
            if(dashboard != null){
                return ResponseEntity.ok("Updated");
            }else{
                return ResponseEntity.ok("Unchanged");
            }

        } catch (HygieiaException he) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(he.getMessage());
        }

    }

    @DashboardOwnerOrAdmin
    @RequestMapping(value = "/dashboard/updateScoreSettings/{id}", method = PUT)
    public ResponseEntity<String> updateScoreSettings(
      @PathVariable ObjectId id,
      @RequestParam(value = "scoreEnabled", required = true, defaultValue = "false") boolean scoreEnabled,
      @RequestParam(value = "scoreDisplay", required = false, defaultValue = "HEADER") String scoreDisplay) {
        Dashboard dashboard = dashboardService.updateScoreSettings(
          id, scoreEnabled, ScoreDisplayType.fromString(scoreDisplay)
        );
        if(dashboard != null){
            return ResponseEntity.ok("Updated");
        }else{
            return ResponseEntity.ok("Unchanged");
        }
    }


    @DashboardOwnerOrAdmin
    @RequestMapping(value = "/dashboard/{id}/deleteWidget/{widgetId}", method = PUT,
            consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<WidgetResponse> deleteWidget(@PathVariable ObjectId id,
                                                       @PathVariable ObjectId widgetId,
                                                       @RequestBody WidgetRequest request) {
        Component component = dashboardService.associateCollectorToComponent(
                request.getComponentId(), request.getCollectorItemIds());

        Dashboard dashboard = dashboardService.get(id);
        Widget widget =dashboardService.getWidget(dashboard, widgetId);
        dashboardService.deleteWidget(dashboard, widget,request.getComponentId());

        return ResponseEntity.ok().body(new WidgetResponse(component, null));
    }

    /**
     * Get count of all dashboards
     *
     * @return Integer
     */
    @RequestMapping(value = "/dashboard/count/{type}", method = GET, produces = APPLICATION_JSON_VALUE)
    public long dashboardsCount(@PathVariable String type) {
        return dashboardService.count(type);
    }

    /**
     * Get count of all filtered dashboards
     *
     * @return Integer
     */
        @RequestMapping(value = "/dashboard/filter/count/{title}/{type}", method = GET, produces = APPLICATION_JSON_VALUE)
    public long dashboardsFilterCount(@PathVariable String title, @PathVariable String type) {
        return dashboardService.getAllDashboardsByTitleCount(title, type);
    }

    /**
     * Get dashboards filtered by title (pageable)
     *
     * @return List of Dashboards
     */
    @RequestMapping(value = "/dashboard/page/filter", method = GET, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Dashboard>> dashboardByTitlePage(@RequestParam(value = "search", required = false, defaultValue = "") String search,
                                                                @RequestParam(value = "type", required = false, defaultValue = "") String type,
                                                                Pageable pageable) throws HygieiaException {
        Page<Dashboard> pageDashboardItems = dashboardService.getDashboardByTitleWithFilter(search, type, pageable);
        return ResponseEntity
                .ok()
                .headers(paginationHeaderUtility.buildPaginationHeaders(pageDashboardItems))
                .body(pageDashboardItems.getContent());
    }

    /**
     * Get page size
     *
     * @return int
     */
    @RequestMapping(value = "/dashboard/pagesize", method = GET, produces = APPLICATION_JSON_VALUE)
    public int getPageSize() {
        return dashboardService.getPageSize();
    }


    /**
     * Get list of dashboards by page (default = 10)
     *
     * @return List of dashboards
     */
    @RequestMapping(value = "/dashboard/page", method = GET, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Dashboard>> dashboardByPage(@RequestParam(value = "search", required = false, defaultValue = "") String search,
                                                           @RequestParam(value = "type", required = false, defaultValue = "") String type,
                                                           Pageable pageable) {
        Page<Dashboard> pageDashboardItems = dashboardService.findDashboardsByPage(type, pageable);
        return ResponseEntity
                .ok()
                .headers(paginationHeaderUtility.buildPaginationHeaders(pageDashboardItems))
                .body(pageDashboardItems.getContent());
    }

    // MyDashboard pagination

    /**
     * Get list of my dashboards by page (default = 10)
     *
     * @return List of dashboards
     */
    @RequestMapping(value = "/dashboard/mydashboard/page", method = GET, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Dashboard>> myDashboardByPage(@RequestParam(value = "username", required = false, defaultValue = "") String username,
                                                             @RequestParam(value = "type", required = false, defaultValue = "") String type,
                                                             Pageable pageable) {
        Page<Dashboard> pageDashboardItems = dashboardService.findMyDashboardsByPage(type, pageable);
        return ResponseEntity
                .ok()
                .headers(paginationHeaderUtility.buildPaginationHeaders(pageDashboardItems))
                .body(pageDashboardItems.getContent());
    }

    /**
     * Get count of my dashboards
     *
     * @return Integer
     */
    @RequestMapping(value = "/dashboard/mydashboard/count/{type}", method = GET, produces = APPLICATION_JSON_VALUE)
    public long myDashboardCount(@PathVariable String type) {
        return dashboardService.myDashboardsCount(type);
    }

    /**
     * Get count of all filtered dashboards
     *
     * @return Integer
     */
    @RequestMapping(value = "/dashboard/mydashboard/filter/count/{title}/{type}", method = GET, produces = APPLICATION_JSON_VALUE)
    public long myDashboardsFilterCount(@PathVariable String title, @PathVariable String type) {
        return dashboardService.getMyDashboardsByTitleCount(title, type);
    }

    /**
     * Get my dashboards filtered by title (pageable)
     *
     * @return List of Dashboards
     */
    @RequestMapping(value = "/dashboard/mydashboard/page/filter", method = GET, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Dashboard>> myDashboardByTitlePage(@RequestParam(value = "search", required = false, defaultValue = "") String search,
                                                                  @RequestParam(value = "type", required = false, defaultValue = "") String type,
                                                                  Pageable pageable) throws HygieiaException {
        Page<Dashboard> pageDashboardItems = dashboardService.getMyDashboardByTitleWithFilter(search, type, pageable);
        return ResponseEntity
                .ok()
                .headers(paginationHeaderUtility.buildPaginationHeaders(pageDashboardItems))
                .body(pageDashboardItems.getContent());
    }
}
