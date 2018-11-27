package com.capitalone.dashboard.service;

import java.util.List;
import java.util.Set;

import com.capitalone.dashboard.model.CollectorItem;
import com.capitalone.dashboard.model.CollectorType;
import org.bson.types.ObjectId;

import com.capitalone.dashboard.misc.HygieiaException;
import com.capitalone.dashboard.model.Component;
import com.capitalone.dashboard.model.Dashboard;
import com.capitalone.dashboard.model.Owner;
import com.capitalone.dashboard.model.Widget;
import com.capitalone.dashboard.model.DataResponse;
import com.capitalone.dashboard.model.ScoreDisplayType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface DashboardService {

    /**
     * Fetches all registered dashboards, sorted by title.
     *
     * @return all dashboards
     */
    Iterable<Dashboard> all();


    /**
     * Fetches a Dashboard.
     *
     * @param id dashboard unique identifier
     * @return Dashboard instance
     */
    Dashboard get(ObjectId id);

    /**
     * Creates a new Dashbaord and saves it to the store.
     *
     * @param dashboard new Dashboard to createCollectorItem
     * @return newly created Dashboard
     */
    Dashboard create(Dashboard dashboard) throws HygieiaException;

    /**
     * Updates an existing dashboard instance.
     *
     * @param dashboard Dashboard to update
     * @return updated Dashboard instance
     */
    Dashboard update(Dashboard dashboard) throws HygieiaException;

    /**
     * Deletes an existing Dashboard instance.
     *
     * @param id unique identifier of Dashboard to delete
     */
    void delete(ObjectId id);

    /**
     * Retrieves a list of Dashboards for a set of CollectorItems and CollectorType.
     *
     * @param collectorItems - Set of CollectorItems
     * @param collectorType - Type of Collector i.e. build, SCM etc
     */
    List<Dashboard> getDashboardsByCollectorItems(Set<CollectorItem> collectorItems, CollectorType collectorType);

    /**
     * Associate a CollectorItem to a Component
     *
     * @param componentId unique identifier of the Component
     * @param collectorItemIds List of unique identifier of the CollectorItem
     * @return Component
     */
    Component associateCollectorToComponent(ObjectId componentId, List<ObjectId> collectorItemIds);

    /**
     * Creates a new Widget and adds it to the Dashboard indicated by the dashboardId parameter.
     *
     * @param dashboard add widget to this Dashboard
     * @param widget Widget to add
     * @return newly created Widget
     */
    Widget addWidget(Dashboard dashboard, Widget widget);

    /**
     * Find the Widget with the specified id in the Dashbaord provided.
     *
     * @param dashboard Dashboard
     * @param widgetId widget ID
     * @return Widget
     */
    Widget getWidget(Dashboard dashboard, ObjectId widgetId);

    /**
     * Updates an existing Widget.
     *
     * @param dashboard update widget on this Dashboard
     * @param widget Widget to update
     * @return updated widget
     */
    Widget updateWidget(Dashboard dashboard, Widget widget);

    /**
     * Deletes an existing Widget.
     *
     * @param dashboard delete widget on this Dashboard
     * @param widget Widget to delete
     *
     */
    void deleteWidget(Dashboard dashboard, Widget widget,ObjectId componentId);



    /**
     * Gets all dashboard belonging to the authenticated user
     * @return List of dashboards
     */
    
    List<Dashboard> getOwnedDashboards();

    /**
     * Gets all dashboard ObjectIds belonging to the authenticated user
     * @return List of dashboard ObjectIds
     */
    List<ObjectId> getOwnedDashboardsObjectIds();

    /**
     * Get the set of owners for a given dashboard
     * 
     * @param id get owners for this dashboard
     * @return the set of owners for provided dashboard
     */
    Iterable<Owner> getOwners(ObjectId id);

    /**
     * Updates the owners of the given dashboard with the set of given owners
     * 
     * @param dashboardId update owners on this dashboard
     * @param owners full collection of owners
     * @return the new set of owners for provided dashboard
     */
    Iterable<Owner> updateOwners(ObjectId dashboardId, Iterable<Owner> owners);
    
    /**
     * Get owner of dashboard on supplying dashboard Title
     * @Param dashboardTitle
     * @return String username
     * 
     */
    
    String getDashboardOwner(String dashboardTitle);

    /**
     * Get component
     * @Param component Id
     * @return Component
     *
     */

    Component getComponent(ObjectId componentId);
    /**
     * Fetches a Dashboards.
     *
     * @param configItem dashboard unique identifier
     * @return Dashboard instances
     */
    DataResponse<Iterable<Dashboard>> getByBusinessService(String configItem) throws HygieiaException;
    /**
     * Fetches a Dashboards.
     *
     * @param configItem dashboard unique identifier
     * @return Dashboard instances
     */
    DataResponse<Iterable<Dashboard>> getByBusinessApplication(String configItem) throws HygieiaException;
    /**
     * Fetches a Dashboards.
     *
     * @param configItemApplication dashboard unique identifier
     * @param configItemService dashboard unique identifier
     * @return Dashboard instances
     */
    DataResponse<Iterable<Dashboard>> getByServiceAndApplication(String configItemService, String configItemApplication) throws HygieiaException;

    /**
     *  Fetches a list of dashboards by title
     * @param title
     * @return
     */
    List<Dashboard> getByTitle(String title);

    /**
     *  Updates Dashboard Business Items
     * @param dashboardId
     * @param dashboard
     * @return dashboard instance
     */
    Dashboard updateDashboardBusinessItems(ObjectId dashboardId, Dashboard dashboard) throws HygieiaException;

    Dashboard updateDashboardWidgets(ObjectId dashboardId, Dashboard request) throws HygieiaException;

    Page<Dashboard> findDashboardsByPage(String type, Pageable page);

    Page<Dashboard> getDashboardByTitleWithFilter(String title, String type, Pageable pageable);

    long count(String type);

    Integer getAllDashboardsByTitleCount(String title, String type);

    int getPageSize();

    Page<Dashboard> findMyDashboardsByPage(String type, Pageable page);

    long myDashboardsCount(String type);

    int getMyDashboardsByTitleCount(String title, String type);

    Page<Dashboard> getMyDashboardByTitleWithFilter(String title, String type, Pageable pageable);

    Dashboard updateScoreSettings(ObjectId dashboardId, boolean scoreEnabled, ScoreDisplayType scoreDisplay);

}




