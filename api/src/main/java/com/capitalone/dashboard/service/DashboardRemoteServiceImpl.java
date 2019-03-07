package com.capitalone.dashboard.service;

import com.capitalone.dashboard.misc.HygieiaException;
import com.capitalone.dashboard.model.Application;
import com.capitalone.dashboard.model.Cmdb;
import com.capitalone.dashboard.model.Collector;
import com.capitalone.dashboard.model.CollectorItem;
import com.capitalone.dashboard.model.CollectorType;
import com.capitalone.dashboard.model.Component;
import com.capitalone.dashboard.model.Dashboard;
import com.capitalone.dashboard.model.DashboardType;
import com.capitalone.dashboard.model.Owner;
import com.capitalone.dashboard.model.Widget;
import com.capitalone.dashboard.model.ScoreDisplayType;
import com.capitalone.dashboard.repository.CmdbRepository;
import com.capitalone.dashboard.repository.CollectorRepository;
import com.capitalone.dashboard.repository.ComponentRepository;
import com.capitalone.dashboard.repository.CustomRepositoryQuery;
import com.capitalone.dashboard.repository.DashboardRepository;
import com.capitalone.dashboard.request.DashboardRemoteRequest;
import com.capitalone.dashboard.request.WidgetRequest;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class DashboardRemoteServiceImpl implements DashboardRemoteService {
    private static final Log LOG = LogFactory.getLog(DashboardRemoteServiceImpl.class);
    private final CollectorRepository collectorRepository;
    private final CustomRepositoryQuery customRepositoryQuery;
    private final DashboardRepository dashboardRepository;
    private final DashboardService dashboardService;
    private final CollectorService collectorService;
    private final UserInfoService userInfoService;
    private final CmdbRepository cmdbRepository;
    private final ComponentRepository componentRepository;

    @Autowired
    public DashboardRemoteServiceImpl(
            CollectorRepository collectorRepository,
            CustomRepositoryQuery customRepositoryQuery,
            DashboardRepository dashboardRepository, DashboardService dashboardService, CollectorService collectorService, UserInfoService userInfoService, CmdbRepository cmdbRepository, ComponentRepository componentRepository) {
        this.collectorRepository = collectorRepository;
        this.customRepositoryQuery = customRepositoryQuery;
        this.dashboardRepository = dashboardRepository;
        this.dashboardService = dashboardService;
        this.collectorService = collectorService;
        this.userInfoService = userInfoService;
        this.cmdbRepository = cmdbRepository;
        this.componentRepository = componentRepository;
    }

    /**
     * Creates a list of unique owners from the owner and owners requests
     * @param request
     * @return List<Owner> list of owners to be added to the dashboard
     * @throws HygieiaException
     */
    private List<Owner> getOwners(DashboardRemoteRequest request) throws HygieiaException {
        DashboardRemoteRequest.DashboardMetaData metaData = request.getMetaData();
        Owner owner = metaData.getOwner();
        List<Owner> owners = metaData.getOwners();

        if (owner == null && CollectionUtils.isEmpty(owners)) {
            throw new HygieiaException("There are no owner/owners field in the request", HygieiaException.INVALID_CONFIGURATION);
        }

        if (owners == null) {
            owners = new ArrayList<Owner>();
            owners.add(owner);
        } else if (owner != null) {
            owners.add(owner);
        }

        Set<Owner> uniqueOwners = new HashSet<Owner>(owners);
        return new ArrayList<Owner>(uniqueOwners);
    }

    @Override
    public Dashboard remoteCreate(DashboardRemoteRequest request, boolean isUpdate) throws HygieiaException {
        final String METHOD_NAME = "DashboardRemoteServiceImpl.remoteCreate";
        Dashboard dashboard;
        Map<String, Widget> existingWidgets = new HashMap<>();

        List<Owner> owners = getOwners(request);
        List<Owner> validOwners = Lists.newArrayList();
        for (Owner owner : owners) {
            if (userInfoService.isUserValid(owner.getUsername(), owner.getAuthType())) {
                validOwners.add(owner);
            } else {
                LOG.warn(METHOD_NAME + " Invalid owner passed in the request : " + owner.getUsername());
            }
        }

        if (validOwners.isEmpty()) {
            throw new HygieiaException("There are no valid owner/owners in the request", HygieiaException.INVALID_CONFIGURATION);
        }

        List<Dashboard> dashboards = findExistingDashboardsFromRequest( request );
        if (!CollectionUtils.isEmpty(dashboards)) {
            dashboard = dashboards.get(0);
            Set<Owner> uniqueOwners = new HashSet<Owner>(validOwners);
            uniqueOwners.addAll(dashboard.getOwners());
            dashboard.setOwners(new ArrayList<Owner>(uniqueOwners));
            if (!isUpdate) {
                throw new HygieiaException("Dashboard " + dashboard.getTitle() + " (id =" + dashboard.getId() + ") already exists", HygieiaException.DUPLICATE_DATA);
            }
            dashboardService.update(dashboard);
            //Save the widgets
            for (Widget w : dashboard.getWidgets()) {
                existingWidgets.put(w.getName(), w);
            }

        } else {
            if (isUpdate) {
                throw new HygieiaException("Dashboard " + request.getMetaData().getTitle() +  " does not exist.", HygieiaException.BAD_DATA);
            }
            request.getMetaData().setOwners(validOwners);
            dashboard = dashboardService.create(requestToDashboard(request));
        }

        List<DashboardRemoteRequest.Entry> entries = request.getAllEntries();
        Map<String, WidgetRequest> allWidgetRequests = generateRequestWidgetList( entries, dashboard);
        //adds widgets
        for (String key : allWidgetRequests.keySet()) {
            WidgetRequest widgetRequest = allWidgetRequests.get(key);

            if( key.equals( "codeanalysis" ) ){

                List< CollectorItem > list = new ArrayList<>();
                Component component = componentRepository.findOne( dashboard.getApplication().getComponents().get(0).getId() );

                list.addAll( component.getCollectorItems(CollectorType.Test ));
                list.addAll( component.getCollectorItems(CollectorType.StaticSecurityScan ) );
                list.addAll( component.getCollectorItems(CollectorType.CodeQuality ) );
                list.addAll( component.getCollectorItems(CollectorType.LibraryPolicy ) );

                List< ObjectId > collectorItemIdList = list.stream().map( CollectorItem::getId).collect(Collectors.toList() );
                widgetRequest.getCollectorItemIds().addAll( collectorItemIdList );
            }

            Component component = dashboardService.associateCollectorToComponent(
                    dashboard.getApplication().getComponents().get(0).getId(), widgetRequest.getCollectorItemIds());
            Widget newWidget = widgetRequest.widget();
            if (isUpdate) {
                Widget oldWidget = existingWidgets.get(newWidget.getName());
                if (oldWidget == null) {
                    dashboardService.addWidget(dashboard, newWidget);
                } else {
                    Widget widget = widgetRequest.updateWidget(dashboardService.getWidget(dashboard, oldWidget.getId()));
                    dashboardService.updateWidget(dashboard, widget);
                }
            } else {
                dashboardService.addWidget(dashboard, newWidget);
            }
        }
        return (dashboard != null) ? dashboardService.get(dashboard.getId()) : null;
    }

    /**
     * Generates a Widget Request list of Widgets to be created from the request
     * @param entries
     * @param dashboard
     * @return Map< String, WidgetRequest > list of Widgets to be created
     * @throws HygieiaException
     */
    private  Map < String, WidgetRequest > generateRequestWidgetList( List < DashboardRemoteRequest.Entry > entries, Dashboard dashboard ) throws HygieiaException {
        Map< String, WidgetRequest > allWidgetRequests = new HashMap<>();
        //builds widgets
        for ( DashboardRemoteRequest.Entry entry : entries ) {

            List < Collector > collectors = collectorRepository.findByCollectorTypeAndName( entry.getType(), entry.getToolName() );
            if ( CollectionUtils.isEmpty( collectors ) ) {
                throw new HygieiaException( entry.getToolName() + " collector is not available.", HygieiaException.BAD_DATA );
            }
            Collector collector = collectors.get( 0 );
            WidgetRequest widgetRequest = allWidgetRequests.get( entry.getWidgetName() );

            if ( widgetRequest == null ) {
                widgetRequest = entryToWidgetRequest( dashboard, entry, collector) ;
                allWidgetRequests.put( entry.getWidgetName(), widgetRequest );
            } else {
                CollectorItem item = entryToCollectorItem( entry, collector );
                if ( item != null ) {
                    widgetRequest.getCollectorItemIds().add( item.getId() );
                }
            }
        }
        return allWidgetRequests;
    }
    /**
     * Takes a DashboardRemoteRequest. If the request contains a Business Service and Business Application then returns dashboard. Otherwise,
     * Checks dashboards for existing Title and returns dashboards.
     * @param request
     * @return  List< Dashboard >
     */
    private List< Dashboard > findExistingDashboardsFromRequest( DashboardRemoteRequest request ) {
        String businessService = request.getMetaData().getBusinessService();
        String businessApplication = request.getMetaData().getBusinessApplication();

        if( !StringUtils.isEmpty( businessService ) && !StringUtils.isEmpty( businessApplication ) ){
           return dashboardRepository.findAllByConfigurationItemBusServNameContainingIgnoreCaseAndConfigurationItemBusAppNameContainingIgnoreCase( businessService, businessApplication );
        }else {
           return dashboardRepository.findByTitle( request.getMetaData().getTitle() );
        }
    }


    private CollectorItem entryToCollectorItem(DashboardRemoteRequest.Entry entry, Collector collector) throws HygieiaException {
        CollectorItem item = entry.toCollectorItem(collector);
        item.setCollectorId(collector.getId());

        return collectorService.createCollectorItemSelectOptions(item,collector.getAllFields(), item.getOptions());
    }

    /**
     * Creates a widget from entry
     * @param dashboard
     * @param entry
     * @return WidgetRequest
     */
    private WidgetRequest entryToWidgetRequest(Dashboard dashboard, DashboardRemoteRequest.Entry entry, Collector collector) throws HygieiaException {
        WidgetRequest request = new WidgetRequest();
        CollectorItem item = entryToCollectorItem(entry, collector);
        if (item != null) {
            request.setName(entry.getWidgetName());
            request.setComponentId(dashboard.getApplication().getComponents().get(0).getId());
            request.setOptions(entry.toWidgetOptions());
            List<ObjectId> ids = new ArrayList<>();
            ids.add(item.getId());
            request.setCollectorItemIds(ids);
        }
        return request;
    }



    /**
     * Creates a Dashboard object from the request.
     * @param request
     * @return Dashboard
     * @throws HygieiaException
     */
    private Dashboard requestToDashboard(DashboardRemoteRequest request) throws HygieiaException {
        DashboardRemoteRequest.DashboardMetaData metaData = request.getMetaData();
        Application application = new Application(metaData.getApplicationName(), new Component(metaData.getComponentName()));
        String appName = null;
        String serviceName = null;
        if (!StringUtils.isEmpty(metaData.getBusinessApplication())) {
            Cmdb app = cmdbRepository.findByConfigurationItemAndItemType(metaData.getBusinessApplication(), "component");
            if (app == null) throw new HygieiaException("Invalid Business Application Name.", HygieiaException.BAD_DATA);
            appName = app.getConfigurationItem();
        }
        if (!StringUtils.isEmpty(metaData.getBusinessService())) {
            Cmdb service = cmdbRepository.findByConfigurationItemAndItemType(metaData.getBusinessService(), "app");
            if (service == null) throw new HygieiaException("Invalid Business Service Name.", HygieiaException.BAD_DATA);
            serviceName = service.getConfigurationItem();
        }
        List<String> activeWidgets = new ArrayList<>();
        return new Dashboard(true, metaData.getTemplate(), metaData.getTitle(), application, metaData.getOwners(), DashboardType.fromString(metaData.getType()), serviceName, appName,activeWidgets, false, ScoreDisplayType.HEADER);
    }
}
