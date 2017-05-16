package com.capitalone.dashboard.service;

import java.util.List;

import org.bson.types.ObjectId;

import com.capitalone.dashboard.misc.HygieiaException;
import com.capitalone.dashboard.model.AuthType;
import com.capitalone.dashboard.model.Component;
import com.capitalone.dashboard.model.Dashboard;
import com.capitalone.dashboard.model.Owner;
import com.capitalone.dashboard.model.UserInfo;
import com.capitalone.dashboard.model.Widget;


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
     * Gets all dashboard belonging to the authenticated user
     * @return List of dashboards
     */
    
    List<Dashboard> getOwnedDashboards();

    Iterable<UserInfo> getAllUsers();

    Iterable<Owner> getOwners(ObjectId id);

    UserInfo promoteToOwner(ObjectId dashboardId, String username, AuthType authType);

    UserInfo demoteFromOwner(ObjectId dashboardId, String username, AuthType authType);
    
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

}




