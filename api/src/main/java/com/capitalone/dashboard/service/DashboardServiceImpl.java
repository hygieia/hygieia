package com.capitalone.dashboard.service;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.ArrayList;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.capitalone.dashboard.auth.AuthenticationUtil;
import com.capitalone.dashboard.auth.exceptions.UserNotFoundException;
import com.capitalone.dashboard.misc.HygieiaException;
import com.capitalone.dashboard.model.AuthType;
import com.capitalone.dashboard.model.Collector;
import com.capitalone.dashboard.model.CollectorItem;
import com.capitalone.dashboard.model.CollectorType;
import com.capitalone.dashboard.model.Component;
import com.capitalone.dashboard.model.Dashboard;
import com.capitalone.dashboard.model.DashboardType;
import com.capitalone.dashboard.model.Owner;
import com.capitalone.dashboard.model.Widget;
import com.capitalone.dashboard.model.Cmdb;
import com.capitalone.dashboard.model.DataResponse;
import com.capitalone.dashboard.repository.CollectorItemRepository;
import com.capitalone.dashboard.repository.CollectorRepository;
import com.capitalone.dashboard.repository.ComponentRepository;
import com.capitalone.dashboard.repository.CustomRepositoryQuery;
import com.capitalone.dashboard.repository.DashboardRepository;
import com.capitalone.dashboard.repository.PipelineRepository;
import com.capitalone.dashboard.repository.ServiceRepository;
import com.capitalone.dashboard.repository.UserInfoRepository;
import com.capitalone.dashboard.util.UnsafeDeleteException;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

@Service
public class DashboardServiceImpl implements DashboardService {

    private final DashboardRepository dashboardRepository;
    private final ComponentRepository componentRepository;
    private final CollectorRepository collectorRepository;
    private final CollectorItemRepository collectorItemRepository;
    private final CustomRepositoryQuery customRepositoryQuery;
    @SuppressWarnings("unused")
    private final PipelineRepository pipelineRepository; //NOPMD
    private final ServiceRepository serviceRepository;
    private final UserInfoRepository userInfoRepository;
    private final CmdbService cmdbService;

    @Autowired
    public DashboardServiceImpl(DashboardRepository dashboardRepository,
                                ComponentRepository componentRepository,
                                CollectorRepository collectorRepository,
                                CollectorItemRepository collectorItemRepository,
                                CustomRepositoryQuery customRepositoryQuery,
                                ServiceRepository serviceRepository,
                                PipelineRepository pipelineRepository,
                                UserInfoRepository userInfoRepository,
                                CmdbService cmdbService) {
        this.dashboardRepository = dashboardRepository;
        this.componentRepository = componentRepository;
        this.collectorRepository = collectorRepository;
        this.collectorItemRepository = collectorItemRepository;
        this.customRepositoryQuery = customRepositoryQuery;
        this.serviceRepository = serviceRepository;
        this.pipelineRepository = pipelineRepository;   //TODO - Review if we need this param, seems it is never used according to PMD
        this.userInfoRepository = userInfoRepository;
        this.cmdbService = cmdbService;
    }

    @Override
    public Iterable<Dashboard> all() {
        Iterable<Dashboard> dashboards = dashboardRepository.findAll(new Sort(Sort.Direction.ASC, "title"));
        for(Dashboard dashboard: dashboards){
            ObjectId appObjectId = dashboard.getConfigurationItemBusServObjectId();
            ObjectId compObjectId = dashboard.getConfigurationItemBusAppObjectId();

            setAppAndComponentNameToDashboard(dashboard, appObjectId, compObjectId);
        }
        return dashboards;
    }

    @Override
    public Dashboard get(ObjectId id) {
        Dashboard dashboard = dashboardRepository.findOne(id);
        ObjectId appObjectId = dashboard.getConfigurationItemBusServObjectId();
        ObjectId compObjectId = dashboard.getConfigurationItemBusAppObjectId();

        setAppAndComponentNameToDashboard(dashboard, appObjectId, compObjectId);

        if (!dashboard.getApplication().getComponents().isEmpty()) {
            // Add transient Collector instance to each CollectorItem
            Map<CollectorType, List<CollectorItem>> itemMap = dashboard.getApplication().getComponents().get(0).getCollectorItems();

            Iterable<Collector> collectors = collectorsFromItems(itemMap);

            for (List<CollectorItem> collectorItems : itemMap.values()) {
                for (CollectorItem collectorItem : collectorItems) {
                    collectorItem.setCollector(getCollector(collectorItem.getCollectorId(), collectors));
                }
            }
        }

        return dashboard;
    }

    private Dashboard create(Dashboard dashboard, boolean isUpdate) throws HygieiaException {
        Iterable<Component> components = null;

        if(!isUpdate) {
            components = componentRepository.save(dashboard.getApplication().getComponents());
        }

        try {
            duplicateDashboardErrorCheck(dashboard);
            return dashboardRepository.save(dashboard);
        }  catch (Exception e) {
            //Exclude deleting of components if this is an update request
            if(!isUpdate) {
                componentRepository.delete(components);
            }

            if(e instanceof HygieiaException){
                throw e;
            }else{
                throw new HygieiaException("Failed creating dashboard.", HygieiaException.ERROR_INSERTING_DATA);
            }
        }
    }

    @Override
    public Dashboard create(Dashboard dashboard) throws HygieiaException {
        return create(dashboard, false);
    }
    @Override
    public Dashboard update(Dashboard dashboard) throws HygieiaException {
        return create(dashboard, true);
    }

    @Override
    public void delete(ObjectId id) {
        Dashboard dashboard = dashboardRepository.findOne(id);

        if (!isSafeDelete(dashboard)) {
            throw new UnsafeDeleteException("Cannot delete team dashboard " + dashboard.getTitle() + " as it is referenced by program dashboards.");
        }


        // Remove this Dashboard's services and service dependencies
        serviceRepository.delete(serviceRepository.findByDashboardId(id));
        for (com.capitalone.dashboard.model.Service service : serviceRepository.findByDependedBy(id)) { //NOPMD - using fully qualified or we pickup an incorrect spring class
            service.getDependedBy().remove(id);
            serviceRepository.save(service);
        }

        /**
         * Delete Dashboard. Then delete component. Then disable collector items if needed
         */
        dashboardRepository.delete(dashboard);
        componentRepository.delete(dashboard.getApplication().getComponents());
        handleCollectorItems(dashboard.getApplication().getComponents());
    }

    /**
     * For the dashboard, get all the components and get all the collector items for the components.
     * If a collector item is NOT associated with any Component, disable it.
     * @param components
     */
    private void handleCollectorItems(List<Component> components) {
        for (Component component : components) {
            Map<CollectorType, List<CollectorItem>> itemMap = component.getCollectorItems();
            for (CollectorType type : itemMap.keySet()) {
                List<CollectorItem> items = itemMap.get(type);
                for (CollectorItem i : items) {
                    if (CollectionUtils.isEmpty(customRepositoryQuery.findComponents(i.getCollectorId(),type,i))) {
                        i.setEnabled(false);
                        collectorItemRepository.save(i);
                    }
                }
            }
        }
    }

    private boolean isSafeDelete(Dashboard dashboard) {
        return !(dashboard.getType() == null || dashboard.getType().equals(DashboardType.Team)) || isSafeTeamDashboardDelete(dashboard);
    }

    private boolean isSafeTeamDashboardDelete(Dashboard dashboard) {
        boolean isSafe = false;
        List<Collector> productCollectors = collectorRepository.findByCollectorType(CollectorType.Product);
        if (productCollectors.isEmpty()) {
            return true;
        }

        Collector productCollector = productCollectors.get(0);

        CollectorItem teamDashboardCollectorItem = collectorItemRepository.findTeamDashboardCollectorItemsByCollectorIdAndDashboardId(productCollector.getId(), dashboard.getId().toString());

        //// TODO: 1/21/16 Is this safe? What if we add a new team dashbaord and quickly add it to a product and then delete it?
        if (teamDashboardCollectorItem == null) {
            return true;
        }

        if (dashboardRepository.findProductDashboardsByTeamDashboardCollectorItemId(teamDashboardCollectorItem.getId().toString()).isEmpty()) {
            isSafe = true;
        }
        return isSafe;
    }

    @Override
    public Component associateCollectorToComponent(ObjectId componentId, List<ObjectId> collectorItemIds) {
        if (componentId == null || collectorItemIds == null) {
            // Not all widgets gather data from collectors
            return null;
        }

        com.capitalone.dashboard.model.Component component = componentRepository.findOne(componentId); //NOPMD - using fully qualified name for clarity
        //we can not assume what collector item is added, what is removed etc so, we will
        //refresh the association. First disable all collector items, then remove all and re-add

        //First: disable all collectorItems of the Collector TYPEs that came in with the request.
        //Second: remove all the collectorItem association of the Collector Type  that came in
        HashSet<CollectorType> incomingTypes = new HashSet<>();
        HashMap<ObjectId, CollectorItem> toSaveCollectorItems = new HashMap<>();
        for (ObjectId collectorItemId : collectorItemIds) {
            CollectorItem collectorItem = collectorItemRepository.findOne(collectorItemId);
            Collector collector = collectorRepository.findOne(collectorItem.getCollectorId());
            if (!incomingTypes.contains(collector.getCollectorType())) {
                incomingTypes.add(collector.getCollectorType());
                List<CollectorItem> cItems = component.getCollectorItems(collector.getCollectorType());
                // Save all collector items as disabled for now
                if (!CollectionUtils.isEmpty(cItems)) {
                    for (CollectorItem ci : cItems) {
                        //if item is orphaned, disable it. Otherwise keep it enabled.
                        ci.setEnabled(!isLonely(ci, collector, component));
                        toSaveCollectorItems.put(ci.getId(), ci);
                    }
                }
                // remove all collector items of a type
                component.getCollectorItems().remove(collector.getCollectorType());
            }
        }

        //Last step: add collector items that came in
        for (ObjectId collectorItemId : collectorItemIds) {
            CollectorItem collectorItem = collectorItemRepository.findOne(collectorItemId);
            //the new collector items must be set to true
            collectorItem.setEnabled(true);
            Collector collector = collectorRepository.findOne(collectorItem.getCollectorId());
            component.addCollectorItem(collector.getCollectorType(), collectorItem);
            toSaveCollectorItems.put(collectorItemId, collectorItem);
            // set transient collector property
            collectorItem.setCollector(collector);
        }

        Set<CollectorItem> deleteSet = new HashSet<>();
        for (ObjectId id : toSaveCollectorItems.keySet()) {
            deleteSet.add(toSaveCollectorItems.get(id));
        }
        collectorItemRepository.save(deleteSet);
        componentRepository.save(component);
        return component;
    }


    private boolean isLonely(CollectorItem item, Collector collector, Component component) {
        List<Component> components = customRepositoryQuery.findComponents(collector, item);
        //if item is not attached to any component, it is orphaned.
        if (CollectionUtils.isEmpty(components)) return true;
        //if item is attached to more than 1 component, it is NOT orphaned
        if (components.size() > 1) return false;
        //if item is attached to ONLY 1 component it is the current one, it is going to be orphaned after this
        return (components.get(0).getId().equals(component.getId()));
    }

    @Override
    public Widget addWidget(Dashboard dashboard, Widget widget) {
        widget.setId(ObjectId.get());
        dashboard.getWidgets().add(widget);
        dashboardRepository.save(dashboard);
        return widget;
    }

    @Override
    public Widget getWidget(Dashboard dashboard, ObjectId widgetId) {
        return Iterables.find(dashboard.getWidgets(), new WidgetByIdPredicate(widgetId));
    }

    @Override
    public Widget updateWidget(Dashboard dashboard, Widget widget) {
        int index = dashboard.getWidgets().indexOf(widget);
        dashboard.getWidgets().set(index, widget);
        dashboardRepository.save(dashboard);
        return widget;
    }

    private static final class WidgetByIdPredicate implements Predicate<Widget> {
        private final ObjectId widgetId;

        public WidgetByIdPredicate(ObjectId widgetId) {
            this.widgetId = widgetId;
        }

        @Override
        public boolean apply(Widget widget) {
            return widget.getId().equals(widgetId);
        }
    }

    private Iterable<Collector> collectorsFromItems(Map<CollectorType, List<CollectorItem>> itemMap) {
        Set<ObjectId> collectorIds = new HashSet<>();
        for (List<CollectorItem> collectorItems : itemMap.values()) {
            for (CollectorItem collectorItem : collectorItems) {
                collectorIds.add(collectorItem.getCollectorId());
            }
        }

        return collectorRepository.findAll(collectorIds);
    }

    private Collector getCollector(final ObjectId collectorId, Iterable<Collector> collectors) {
        return Iterables.tryFind(collectors, new Predicate<Collector>() {
            @Override
            public boolean apply(Collector collector) {
                return collector.getId().equals(collectorId);
            }
        }).orNull();
    }

	@Override
	public List<Dashboard> getOwnedDashboards() {
		Set<Dashboard> myDashboards = new HashSet<Dashboard>();
		
		Owner owner = new Owner(AuthenticationUtil.getUsernameFromContext(), AuthenticationUtil.getAuthTypeFromContext());
        List<Dashboard> findByOwnersList = dashboardRepository.findByOwners(owner);
        getAppAndComponentNames(findByOwnersList);
		myDashboards.addAll(findByOwnersList);
		
		// TODO: This if check is to ensure backwards compatibility for dashboards created before AuthenticationTypes were introduced.
		if (AuthenticationUtil.getAuthTypeFromContext() == AuthType.STANDARD) {
            List<Dashboard> findByOwnersListOld = dashboardRepository.findByOwner(AuthenticationUtil.getUsernameFromContext());
            getAppAndComponentNames(findByOwnersListOld);
			myDashboards.addAll(findByOwnersListOld);
		}
		
		return Lists.newArrayList(myDashboards);
	}

    @Override
    public List<ObjectId> getOwnedDashboardsObjectIds() {
        List<ObjectId> dashboardIdList = new ArrayList<>();
        List<Dashboard> ownedDashboards =  getOwnedDashboards();

        for(Dashboard dashboard: ownedDashboards){
            dashboardIdList.add(dashboard.getId());
        }

        return dashboardIdList;
    }
    @Override
    public Iterable<Owner> getOwners(ObjectId id) {
        Dashboard dashboard = get(id);
        return dashboard.getOwners();
    }

    @Override
    public Iterable<Owner> updateOwners(ObjectId dashboardId, Iterable<Owner> owners) {
        for(Owner owner : owners) {
        	String username = owner.getUsername();
        	AuthType authType = owner.getAuthType();
        	if(userInfoRepository.findByUsernameAndAuthType(username, authType) == null) {
        		throw new UserNotFoundException(username, authType);
        	}
        }
    	
    	Dashboard dashboard = dashboardRepository.findOne(dashboardId);
        dashboard.setOwners(Lists.newArrayList(owners));
        Dashboard result = dashboardRepository.save(dashboard);

        return result.getOwners();
    }
    
	@Override
	public String getDashboardOwner(String dashboardTitle) {
		String dashboardOwner=dashboardRepository.findByTitle(dashboardTitle).get(0).getOwner();
		
		return dashboardOwner;
	}

    @SuppressWarnings("unused")
    private DashboardType getDashboardType(Dashboard dashboard) {
        if (dashboard.getType() != null) {
            return dashboard.getType();
        }
        return DashboardType.Team;
    }

    @Override
    public Component getComponent(ObjectId componentId){

        Component component = componentRepository.findOne(componentId);
        return component;
    }
    @Override
    public Dashboard updateDashboardBusinessItems(ObjectId dashboardId, Dashboard request) throws HygieiaException {
        Dashboard dashboard = get(dashboardId);
        String updatedBusServiceName = request.getConfigurationItemBusServName();
        String updatedBusApplicationName = request.getConfigurationItemBusAppName();
        String originalBusServiceName = dashboard.getConfigurationItemBusServName();
        String originalBusApplicationName = dashboard.getConfigurationItemBusAppName();
        boolean updateDashboard = false;

        if(updatedBusServiceName != null && !updatedBusServiceName.isEmpty()){
            Cmdb cmdb = cmdbService.configurationItemByConfigurationItem(updatedBusServiceName);
            if(cmdb != null){
                updateDashboard = true;
                dashboard.setConfigurationItemBusServObjectId(cmdb.getId());
            }
        } else if(originalBusServiceName != null && !originalBusServiceName.isEmpty()){

            updateDashboard = true;
            dashboard.setConfigurationItemBusServObjectId(null);
        }

        if(updatedBusApplicationName != null && !updatedBusApplicationName.isEmpty()){
            Cmdb cmdb = cmdbService.configurationItemByConfigurationItem(updatedBusApplicationName);
            if(cmdb != null){
                updateDashboard = true;
                dashboard.setConfigurationItemBusAppObjectId(cmdb.getId());
            }
        } else if(originalBusApplicationName != null && !originalBusApplicationName.isEmpty()){
                updateDashboard = true;
                dashboard.setConfigurationItemBusAppObjectId(null);
        }
        if(updateDashboard){
            dashboard = update(dashboard);
        }else{
            dashboard = null;
        }

        return dashboard;
    }
    @Override
    public DataResponse<Iterable<Dashboard>> getByBusinessService(String app) throws HygieiaException {
        Cmdb cmdb =  cmdbService.configurationItemByConfigurationItem(app);
        Iterable<Dashboard> rt = null;

        if(cmdb != null){
            rt = dashboardRepository.findAllByConfigurationItemBusServObjectId(cmdb.getId());
        }
        return new DataResponse<>(rt, System.currentTimeMillis());
    }
    @Override
    public DataResponse<Iterable<Dashboard>> getByBusinessApplication(String component) throws HygieiaException {
        Cmdb cmdb =  cmdbService.configurationItemByConfigurationItem(component);
        Iterable<Dashboard> rt = null;

        if(cmdb != null){
           rt = dashboardRepository.findAllByConfigurationItemBusAppObjectId(cmdb.getId());
        }
        return new DataResponse<>(rt, System.currentTimeMillis());
    }
    @Override
    public DataResponse<Iterable<Dashboard>> getByServiceAndApplication(String component, String app) throws HygieiaException {
        Cmdb cmdbCompItem =  cmdbService.configurationItemByConfigurationItem(component);
        Cmdb cmdbAppItem =  cmdbService.configurationItemByConfigurationItem(app);
        Iterable<Dashboard> rt = null;

        if(cmdbAppItem != null && cmdbCompItem != null){
            rt = dashboardRepository.findAllByConfigurationItemBusServObjectIdAndConfigurationItemBusAppObjectId(cmdbAppItem.getId(),cmdbCompItem.getId());
        }
        return new DataResponse<>(rt, System.currentTimeMillis());
    }


    @Override
    public Dashboard updateDashboardWidgets(ObjectId dashboardId, Dashboard request) throws HygieiaException {
        Dashboard dashboard = get(dashboardId);
        List<String> existingActiveWidgets = dashboard.getActiveWidgets();
        List<Component> components = dashboard.getApplication().getComponents();
        List<String> widgetToDelete =  findUpdateCollectorItems(existingActiveWidgets,request.getActiveWidgets());
        List<Widget> widgets = dashboard.getWidgets();
        ObjectId componentId = components.get(0)!=null?components.get(0).getId():null;
        List<Integer> indexList = new ArrayList<>();
        List<CollectorType> collectorTypesToDelete = new ArrayList<>();
        List<Widget> updatedWidgets = new ArrayList<>();

        for (String widgetName: widgetToDelete) {
            for (Widget widget:widgets) {
                if(widgetName.equalsIgnoreCase(widget.getName())){
                    int widgetIndex = widgets.indexOf(widget);
                    indexList.add(widgetIndex);
                    collectorTypesToDelete.add(findCollectorType(widgetName));
                    if(widgetName.equalsIgnoreCase("codeanalysis")){
                        collectorTypesToDelete.add(CollectorType.CodeQuality);
                        collectorTypesToDelete.add(CollectorType.StaticSecurityScan);
                        collectorTypesToDelete.add(CollectorType.LibraryPolicy);
                    }
                }
            }
        }
        //iterate through index and remove widgets
        for (Integer i:indexList) {
            widgets.set(i,null);
        }
        for (Widget w:widgets) {
            if(w!=null)
                updatedWidgets.add(w);
        }
        dashboard.setWidgets(updatedWidgets);
        dashboard.setActiveWidgets(request.getActiveWidgets());
        dashboard = update(dashboard);
        if(componentId!=null){
            com.capitalone.dashboard.model.Component component = componentRepository.findOne(componentId);
            for (CollectorType cType :collectorTypesToDelete) {
                component.getCollectorItems().remove(cType);
            }
            componentRepository.save(component);
        }
        return dashboard;
    }


    private List<String> findUpdateCollectorItems(List<String> existingWidgets,List<String> currentWidgets){
        List<String> result = existingWidgets.stream().filter(elem -> !currentWidgets.contains(elem)).collect(Collectors.toList());
        return result;
    }

    private static CollectorType findCollectorType(String widgetName){
        if(widgetName.equalsIgnoreCase("build")) return CollectorType.Build;
        if(widgetName.equalsIgnoreCase("feature")) return CollectorType.AgileTool;
        if(widgetName.equalsIgnoreCase("deploy")) return CollectorType.Deployment;
        if(widgetName.equalsIgnoreCase("repo")) return CollectorType.SCM;
        if(widgetName.equalsIgnoreCase("performanceanalysis")) return CollectorType.AppPerformance;
        if(widgetName.equalsIgnoreCase("cloud")) return CollectorType.Cloud;
        if(widgetName.equalsIgnoreCase("chatops")) return CollectorType.ChatOps;
        return null;
    }

    private void getAppAndComponentNames(List<Dashboard> findByOwnersList) {
        for(Dashboard dashboard: findByOwnersList){


            ObjectId appObjectId = dashboard.getConfigurationItemBusServObjectId();
            ObjectId compObjectId = dashboard.getConfigurationItemBusAppObjectId();
            setAppAndComponentNameToDashboard(dashboard, appObjectId, compObjectId);
        }
    }

    /**
     *  Sets business service, business application and valid flag for each to the give Dashboard
     * @param dashboard
     * @param appObjectId
     * @param compObjectId
     */
    private void setAppAndComponentNameToDashboard(Dashboard dashboard, ObjectId appObjectId, ObjectId compObjectId) {
        if(appObjectId != null && !"".equals(appObjectId)){

            Cmdb cmdb =  cmdbService.configurationItemsByObjectId(appObjectId);
            dashboard.setConfigurationItemBusServName(cmdb.getConfigurationItem());
            dashboard.setValidServiceName(cmdb.isValidConfigItem());
        }
        if(compObjectId != null && !"".equals(compObjectId)){
            Cmdb cmdb = cmdbService.configurationItemsByObjectId(compObjectId);
            dashboard.setConfigurationItemBusAppName(cmdb.getConfigurationItem());
            dashboard.setValidAppName(cmdb.isValidConfigItem());
        }
    }

    /**
     *  Takes Dashboard and checks to see if there is an existing Dashboard with the same business service and business application
     *  Throws error if found
     * @param dashboard
     * @throws HygieiaException
     */
    private void duplicateDashboardErrorCheck(Dashboard dashboard) throws HygieiaException {
        ObjectId appObjectId = dashboard.getConfigurationItemBusServObjectId();
        ObjectId compObjectId = dashboard.getConfigurationItemBusAppObjectId();

        if(appObjectId != null && compObjectId != null){
            Dashboard existingDashboard = dashboardRepository.findByConfigurationItemBusServObjectIdAndConfigurationItemBusAppObjectId(appObjectId, compObjectId);
            if(existingDashboard != null && !existingDashboard.getId().equals(dashboard.getId())){
                throw new HygieiaException("Existing Dashboard: " + existingDashboard.getTitle(), HygieiaException.DUPLICATE_DATA);
            }
        }
    }
}
