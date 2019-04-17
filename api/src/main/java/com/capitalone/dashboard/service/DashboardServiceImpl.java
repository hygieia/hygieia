package com.capitalone.dashboard.service;

import com.capitalone.dashboard.auth.AuthenticationUtil;
import com.capitalone.dashboard.auth.exceptions.UserNotFoundException;
import com.capitalone.dashboard.misc.HygieiaException;
import com.capitalone.dashboard.model.AuthType;
import com.capitalone.dashboard.model.BaseModel;
import com.capitalone.dashboard.model.Cmdb;
import com.capitalone.dashboard.model.Collector;
import com.capitalone.dashboard.model.CollectorItem;
import com.capitalone.dashboard.model.CollectorType;
import com.capitalone.dashboard.model.Component;
import com.capitalone.dashboard.model.Dashboard;
import com.capitalone.dashboard.model.DashboardType;
import com.capitalone.dashboard.model.DataResponse;
import com.capitalone.dashboard.model.Owner;
import com.capitalone.dashboard.model.ScoreDisplayType;
import com.capitalone.dashboard.model.Widget;
import com.capitalone.dashboard.repository.CollectorItemRepository;
import com.capitalone.dashboard.repository.CollectorRepository;
import com.capitalone.dashboard.repository.ComponentRepository;
import com.capitalone.dashboard.repository.CustomRepositoryQuery;
import com.capitalone.dashboard.repository.DashboardRepository;
import com.capitalone.dashboard.repository.PipelineRepository;
import com.capitalone.dashboard.repository.ServiceRepository;
import com.capitalone.dashboard.repository.UserInfoRepository;
import com.capitalone.dashboard.settings.ApiSettings;
import com.capitalone.dashboard.util.UnsafeDeleteException;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class DashboardServiceImpl implements DashboardService {
    private static final Log LOG = LogFactory.getLog(DashboardServiceImpl.class);
    private final DashboardRepository dashboardRepository;
    private final ComponentRepository componentRepository;
    private final CollectorRepository collectorRepository;
    private final CollectorItemRepository collectorItemRepository;
    private final CustomRepositoryQuery customRepositoryQuery;
    @SuppressWarnings("unused")
    private final PipelineRepository pipelineRepository; //NOPMD
    private final ServiceRepository serviceRepository;
    private final UserInfoRepository userInfoRepository;
    private final UserInfoService userInfoService;
    private final ScoreDashboardService scoreDashboardService;
    private final CmdbService cmdbService;
    private final String UNDEFINED = "undefined";
    private final static EnumSet<CollectorType> QualityWidget = EnumSet.of(CollectorType.Test , CollectorType.StaticSecurityScan, CollectorType.CodeQuality, CollectorType.LibraryPolicy);

    @Autowired
    private ApiSettings settings;

    @Autowired
    public DashboardServiceImpl(DashboardRepository dashboardRepository,
                                ComponentRepository componentRepository,
                                CollectorRepository collectorRepository,
                                CollectorItemRepository collectorItemRepository,
                                CustomRepositoryQuery customRepositoryQuery,
                                ServiceRepository serviceRepository,
                                PipelineRepository pipelineRepository,
                                UserInfoRepository userInfoRepository,
                                UserInfoService userInfoService,
                                CmdbService cmdbService,
                                ScoreDashboardService scoreDashboardService,
                                ApiSettings settings) {
        this.dashboardRepository = dashboardRepository;
        this.componentRepository = componentRepository;
        this.collectorRepository = collectorRepository;
        this.collectorItemRepository = collectorItemRepository;
        this.customRepositoryQuery = customRepositoryQuery;
        this.serviceRepository = serviceRepository;
        this.pipelineRepository = pipelineRepository;   //TODO - Review if we need this param, seems it is never used according to PMD
        this.userInfoRepository = userInfoRepository;
        this.userInfoService = userInfoService;
        this.cmdbService = cmdbService;
        this.scoreDashboardService = scoreDashboardService;
        this.settings = settings;
    }

    @Override
    public Iterable<Dashboard> all() {
        Iterable<Dashboard> dashboards = dashboardRepository.findAll(new Sort(Sort.Direction.ASC, "title"));
        for(Dashboard dashboard: dashboards){
            String appName = dashboard.getConfigurationItemBusServName();
            String compName = dashboard.getConfigurationItemBusAppName();

            setAppAndComponentNameToDashboard(dashboard, appName, compName);
        }
        return dashboards;
    }

    @Override
    public Dashboard get(ObjectId id) {
        Dashboard dashboard = dashboardRepository.findOne(id);
        String appName = dashboard.getConfigurationItemBusServName();
        String compName = dashboard.getConfigurationItemBusAppName();

        setAppAndComponentNameToDashboard(dashboard, appName, compName);

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

    /**
     * Get all the dashboards that have the collector items
     *
     * @param collectorItems collector items
     * @param collectorType  type of the collector
     * @return a list of dashboards
     */
    @Override
    public List<Dashboard> getDashboardsByCollectorItems(Set<CollectorItem> collectorItems, CollectorType collectorType) {
        if (org.apache.commons.collections4.CollectionUtils.isEmpty(collectorItems)) {
            return new ArrayList<>();
        }
        List<ObjectId> collectorItemIds = collectorItems.stream().map(BaseModel::getId).collect(Collectors.toList());
        // Find the components that have these collector items
        List<com.capitalone.dashboard.model.Component> components = componentRepository.findByCollectorTypeAndItemIdIn(collectorType, collectorItemIds);
        List<ObjectId> componentIds = components.stream().map(BaseModel::getId).collect(Collectors.toList());
        return dashboardRepository.findByApplicationComponentIdsIn(componentIds);
    }

    private Dashboard create(Dashboard dashboard, boolean isUpdate) throws HygieiaException {
        Iterable<Component> components = null;

        if(!isUpdate) {
            components = componentRepository.save(dashboard.getApplication().getComponents());
        } else {
           dashboard.setUpdatedAt(System.currentTimeMillis());
        }

        try {
            duplicateDashboardErrorCheck(dashboard);
            Dashboard savedDashboard = dashboardRepository.save(dashboard);
            CollectorItem scoreCollectorItem;
            if (isUpdate) {
                scoreCollectorItem = this.scoreDashboardService.editScoreForDashboard(savedDashboard);
            } else {
                scoreCollectorItem = this.scoreDashboardService.addScoreForDashboardIfScoreEnabled(savedDashboard);
            }
            return savedDashboard;
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
        if (dashboard.isScoreEnabled()) {
            this.scoreDashboardService.disableScoreForDashboard(dashboard);
        }

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
        final String METHOD_NAME = "DashboardServiceImpl.associateCollectorToComponent :";
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
            if(collectorItem == null) {
                LOG.warn(METHOD_NAME + " Bad CollectorItemId passed in the request : " + collectorItemId);
                continue;
            }
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

        // If a collector type is within the code analysis widget, check to see if any of the remaining fields were passed values
        if(incomingTypes.stream().anyMatch(QualityWidget::contains)){
            if(!incomingTypes.contains(CollectorType.Test)){
                component.getCollectorItems().remove(CollectorType.Test);
            }
            if(!incomingTypes.contains(CollectorType.StaticSecurityScan)){
                component.getCollectorItems().remove(CollectorType.StaticSecurityScan);
            }
            if(!incomingTypes.contains(CollectorType.CodeQuality)){
                component.getCollectorItems().remove(CollectorType.CodeQuality);
            }
            if(!incomingTypes.contains(CollectorType.LibraryPolicy)){
                component.getCollectorItems().remove(CollectorType.LibraryPolicy);
            }
        }

        //Last step: add collector items that came in
        for (ObjectId collectorItemId : collectorItemIds) {
            CollectorItem collectorItem = collectorItemRepository.findOne(collectorItemId);
            if(collectorItem == null) {
                LOG.warn(METHOD_NAME + " Bad CollectorItemId passed in the incoming request : " + collectorItemId);
                continue;
            }
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
        Owner owner = new Owner(AuthenticationUtil.getUsernameFromContext(), AuthenticationUtil.getAuthTypeFromContext());
        List<Dashboard> findByOwnersList = dashboardRepository.findByOwners(owner);
        getAppAndComponentNames(findByOwnersList);
        return findByOwnersList.stream().distinct().collect(Collectors.toList());
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
            if (!userInfoService.isUserValid(username, authType)) {
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

        if(StringUtils.isEmpty(updatedBusServiceName)){

            dashboard.setConfigurationItemBusServName(null);
        }else{
            Cmdb cmdb = cmdbService.configurationItemByConfigurationItem(updatedBusServiceName);
            if(cmdb != null){

                dashboard.setConfigurationItemBusServName(cmdb.getConfigurationItem());
            }
        }
        if(StringUtils.isEmpty(updatedBusApplicationName)){

            dashboard.setConfigurationItemBusAppName(null);
        }else{
            Cmdb cmdb = cmdbService.configurationItemByConfigurationItem(updatedBusApplicationName);
            if(cmdb != null){

                dashboard.setConfigurationItemBusAppName(cmdb.getConfigurationItem());
            }
        }

        return update(dashboard);
    }
    @Override
    public DataResponse<Iterable<Dashboard>> getByBusinessService(String app) {
        Cmdb cmdb =  cmdbService.configurationItemByConfigurationItem(app);
        Iterable<Dashboard> rt = null;

        if(cmdb != null){
            rt = dashboardRepository.findAllByConfigurationItemBusServName(cmdb.getConfigurationItem());
        }
        return new DataResponse<>(rt, System.currentTimeMillis());
    }
    @Override
    public DataResponse<Iterable<Dashboard>> getByBusinessApplication(String component) {
        Cmdb cmdb =  cmdbService.configurationItemByConfigurationItem(component);
        Iterable<Dashboard> rt = null;

        if(cmdb != null){
           rt = dashboardRepository.findAllByConfigurationItemBusAppName(cmdb.getConfigurationItem());
        }
        return new DataResponse<>(rt, System.currentTimeMillis());
    }
    @Override
    public DataResponse<Iterable<Dashboard>> getByServiceAndApplication(String component, String app) {
        Cmdb cmdbCompItem =  cmdbService.configurationItemByConfigurationItem(component);
        Cmdb cmdbAppItem =  cmdbService.configurationItemByConfigurationItem(app);
        Iterable<Dashboard> rt = null;

        if(cmdbAppItem != null && cmdbCompItem != null){
            rt = dashboardRepository.findAllByConfigurationItemBusServNameAndConfigurationItemBusAppName(cmdbAppItem.getConfigurationItem(),cmdbCompItem.getConfigurationItem());
        }
        return new DataResponse<>(rt, System.currentTimeMillis());
    }
    @Override
    public  List<Dashboard> getByTitle(String title) {
        List<Dashboard> dashboard = dashboardRepository.findByTitle(title);

        return dashboard;
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
                        collectorTypesToDelete.add(CollectorType.Test);
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


    @Override
    public void deleteWidget(Dashboard dashboard, Widget widget,ObjectId componentId) {
        int index = dashboard.getWidgets().indexOf(widget);
        dashboard.getWidgets().set(index, null);
        List<Widget> widgets = dashboard.getWidgets();
        List<Widget> updatedWidgets = new ArrayList<>();
        for (Widget w: widgets) {
            if(w!=null)
                updatedWidgets.add(w);
        }
        dashboard.setWidgets(updatedWidgets);
        dashboardRepository.save(dashboard);

        String widgetName = widget.getName();

        List<CollectorType> collectorTypesToDelete = new ArrayList<>();
        CollectorType cType = findCollectorType(widgetName);
        collectorTypesToDelete.add(cType);
        if(widgetName.equalsIgnoreCase("codeanalysis")){
            collectorTypesToDelete.add(CollectorType.CodeQuality);
            collectorTypesToDelete.add(CollectorType.StaticSecurityScan);
            collectorTypesToDelete.add(CollectorType.LibraryPolicy);
            collectorTypesToDelete.add(CollectorType.Test);
        }
        if(componentId!=null){
            Component component = componentRepository.findOne(componentId);
            for (CollectorType c:collectorTypesToDelete) {
                component.getCollectorItems().remove(c);
            }

            componentRepository.save(component);
        }

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
        if(widgetName.equalsIgnoreCase("test")) return CollectorType.Test;
        return null;
    }

    private void getAppAndComponentNames(List<Dashboard> findByOwnersList) {
        for(Dashboard dashboard: findByOwnersList){


            String appName = dashboard.getConfigurationItemBusServName();
            String compName = dashboard.getConfigurationItemBusAppName();
            setAppAndComponentNameToDashboard(dashboard, appName, compName);
        }
    }

    /**
     *  Sets business service, business application and valid flag for each to the give Dashboard
     * @param dashboard
     * @param appName
     * @param compName
     */
    private void setAppAndComponentNameToDashboard(Dashboard dashboard, String appName, String compName) {
        if(appName != null && !"".equals(appName)){

            Cmdb cmdb =  cmdbService.configurationItemByConfigurationItem(appName);
            if(cmdb !=null) {
                dashboard.setConfigurationItemBusServName(cmdb.getConfigurationItem());
                dashboard.setValidServiceName(cmdb.isValidConfigItem());
            }
        }
        if(compName != null && !"".equals(compName)){
            Cmdb cmdb = cmdbService.configurationItemByConfigurationItem(compName);
            if(cmdb !=null) {
                dashboard.setConfigurationItemBusAppName(cmdb.getConfigurationItem());
                dashboard.setValidAppName(cmdb.isValidConfigItem());
            }
        }
    }

    /**
     *  Takes Dashboard and checks to see if there is an existing Dashboard with the same business service and business application
     *  Throws error if found
     * @param dashboard
     * @throws HygieiaException
     */
    private void duplicateDashboardErrorCheck(Dashboard dashboard) throws HygieiaException {
        String appName = dashboard.getConfigurationItemBusServName();
        String compName = dashboard.getConfigurationItemBusAppName();

        if(appName != null && !appName.isEmpty() && compName != null && !compName.isEmpty()){
            Dashboard existingDashboard = dashboardRepository.findByConfigurationItemBusServNameIgnoreCaseAndConfigurationItemBusAppNameIgnoreCase(appName, compName);
            if(existingDashboard != null && !existingDashboard.getId().equals(dashboard.getId())){
                throw new HygieiaException("Existing Dashboard: " + existingDashboard.getTitle(), HygieiaException.DUPLICATE_DATA);
            }
        }
    }

    /**
     * Get all dashboards filtered by title and Pageable ( default page size = 10)
     *
     * @param title, pageable
     * @return Page<Dashboard>
     */
    @Override
    public Page<Dashboard> getDashboardByTitleWithFilter(String title, String type, Pageable pageable) {
        Page<Dashboard> dashboardItems = null;
        if ((type != null) && (!type.isEmpty()) && (!UNDEFINED.equalsIgnoreCase(type))) {
            dashboardItems = dashboardRepository.findAllByTypeContainingIgnoreCaseAndTitleContainingIgnoreCase(type, title, pageable);
        } else {
            dashboardItems = dashboardRepository.findAllByTitleContainingIgnoreCase(title, pageable);
        }

        return dashboardItems;
    }

    /**
     * Get count of all dashboards filtered by title
     *
     * @param title
     * @return Integer
     */
    @Override
    public Integer getAllDashboardsByTitleCount(String title, String type) {
        List<Dashboard> dashboards = null;
        if ((type != null) && (!type.isEmpty()) && (!UNDEFINED.equalsIgnoreCase(type))) {
            dashboards = dashboardRepository.findAllByTypeContainingIgnoreCaseAndTitleContainingIgnoreCase(type, title);
        } else {
            dashboards = dashboardRepository.findAllByTitleContainingIgnoreCase(title);
        }
        return dashboards != null ? dashboards.size() : 0;
    }

    /**
     * Get count of all dashboards, use dashboard type if supplied
     *
     * @param
     * @return long
     */
    @Override
    public long count(String type) {
        if ((type != null) && (!type.isEmpty()) && (!UNDEFINED.equalsIgnoreCase(type))) {
            return dashboardRepository.countByTypeContainingIgnoreCase(type);
        } else {
            return dashboardRepository.count();
        }
    }

    /**
     * Get all dashboards with page size (default = 10)
     *
     * @param page size
     * @return List of dashboards
     */
    @Override
    public Page<Dashboard> findDashboardsByPage(String type, Pageable page) {
        if ((type != null) && (!type.isEmpty()) && (!UNDEFINED.equalsIgnoreCase(type))) {
            return dashboardRepository.findAllByTypeContainingIgnoreCase(type, page);
        }
        return dashboardRepository.findAll(page);
    }

    /**
     * Get page size
     *
     * @param
     * @return Integer
     */
    @Override
    public int getPageSize() {
        return settings.getPageSize();
    }

    @Override
    public Page<Dashboard> findMyDashboardsByPage(String type, Pageable page){
        Owner owner = new Owner(AuthenticationUtil.getUsernameFromContext(), AuthenticationUtil.getAuthTypeFromContext());
        Page<Dashboard> ownersList = null;

        if ((type != null) && (!type.isEmpty()) && (!UNDEFINED.equalsIgnoreCase(type))) {
            ownersList = dashboardRepository.findByOwnersAndTypeContainingIgnoreCase(owner, type, page);
        } else {
            ownersList = dashboardRepository.findByOwners(owner, page);
        }
        for (Dashboard dashboard: ownersList) {
            String appName = dashboard.getConfigurationItemBusServName();
            String compName = dashboard.getConfigurationItemBusAppName();
            setAppAndComponentNameToDashboard(dashboard, appName, compName);
        }
        return ownersList;
    }

    @Override
    public long myDashboardsCount(String type){
        Owner owner = new Owner(AuthenticationUtil.getUsernameFromContext(), AuthenticationUtil.getAuthTypeFromContext());
        List<Dashboard> ownersList = null;
        if ((type != null) && (!type.isEmpty()) && (!UNDEFINED.equalsIgnoreCase(type))) {
            ownersList = dashboardRepository.findByOwnersAndTypeContainingIgnoreCase(owner, type);
        } else {
            ownersList = dashboardRepository.findByOwners(owner);
        }
        return ownersList!=null?ownersList.size():0;
    }

    @Override
    public int getMyDashboardsByTitleCount(String title, String type){
        Owner owner = new Owner(AuthenticationUtil.getUsernameFromContext(), AuthenticationUtil.getAuthTypeFromContext());
        List<Dashboard> dashboards = null;
        if ((type != null) && (!type.isEmpty()) && (!UNDEFINED.equalsIgnoreCase(type))) {
            dashboards = dashboardRepository.findByOwnersAndTypeContainingIgnoreCaseAndTitleContainingIgnoreCase(owner,type,title);
        } else {
            dashboards = dashboardRepository.findByOwnersAndTitleContainingIgnoreCase(owner,title);
        }
        return dashboards!=null?dashboards.size():0;
    }

    @Override
    public Page<Dashboard> getMyDashboardByTitleWithFilter(String title, String type, Pageable pageable) {
        Owner owner = new Owner(AuthenticationUtil.getUsernameFromContext(), AuthenticationUtil.getAuthTypeFromContext());
        Page<Dashboard> ownersList = null;
        if ((type != null) && (!type.isEmpty()) && (!UNDEFINED.equalsIgnoreCase(type))) {
            ownersList = dashboardRepository.findByOwnersAndTypeContainingIgnoreCaseAndTitleContainingIgnoreCase(owner,type,title,pageable);
        } else {
            ownersList = dashboardRepository.findByOwnersAndTitleContainingIgnoreCase(owner,title,pageable);
        }

        for (Dashboard dashboard: ownersList) {
            String appName = dashboard.getConfigurationItemBusServName();
            String compName = dashboard.getConfigurationItemBusAppName();
            setAppAndComponentNameToDashboard(dashboard, appName, compName);
        }
        return ownersList;
    }


    @Override
    public Dashboard updateScoreSettings(ObjectId dashboardId, boolean scoreEnabled, ScoreDisplayType scoreDisplay) {
        Dashboard dashboard = get(dashboardId);
        if ((scoreEnabled == dashboard.isScoreEnabled()) &&
            (scoreDisplay == dashboard.getScoreDisplay())) {
            return null;
        }

        dashboard.setScoreEnabled(scoreEnabled);
        dashboard.setScoreDisplay(scoreDisplay);
        Dashboard savedDashboard = dashboardRepository.save(dashboard);
        this.scoreDashboardService.editScoreForDashboard(savedDashboard);
        return savedDashboard;
    }

}
