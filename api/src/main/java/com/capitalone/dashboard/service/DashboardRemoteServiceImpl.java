package com.capitalone.dashboard.service;

import com.capitalone.dashboard.misc.HygieiaException;
import com.capitalone.dashboard.model.Application;
import com.capitalone.dashboard.model.Cmdb;
import com.capitalone.dashboard.model.Collector;
import com.capitalone.dashboard.model.CollectorItem;
import com.capitalone.dashboard.model.Component;
import com.capitalone.dashboard.model.Dashboard;
import com.capitalone.dashboard.model.DashboardType;
import com.capitalone.dashboard.model.Widget;
import com.capitalone.dashboard.repository.CmdbRepository;
import com.capitalone.dashboard.repository.CollectorRepository;
import com.capitalone.dashboard.repository.CustomRepositoryQuery;
import com.capitalone.dashboard.repository.DashboardRepository;
import com.capitalone.dashboard.request.DashboardRemoteRequest;
import com.capitalone.dashboard.request.WidgetRequest;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DashboardRemoteServiceImpl implements DashboardRemoteService {
    private final CollectorRepository collectorRepository;
    private final CustomRepositoryQuery customRepositoryQuery;
    private final DashboardRepository dashboardRepository;
    private final DashboardService dashboardService;
    private final CollectorService collectorService;
    private final UserInfoService userInfoService;
    private final CmdbRepository cmdbRepository;


    @Autowired
    public DashboardRemoteServiceImpl(
            CollectorRepository collectorRepository,
            CustomRepositoryQuery customRepositoryQuery, DashboardRepository dashboardRepository, DashboardService dashboardService, CollectorService collectorService, UserInfoService userInfoService, CmdbRepository cmdbRepository) {
        this.collectorRepository = collectorRepository;
        this.customRepositoryQuery = customRepositoryQuery;
        this.dashboardRepository = dashboardRepository;
        this.dashboardService = dashboardService;
        this.collectorService = collectorService;
        this.userInfoService = userInfoService;
        this.cmdbRepository = cmdbRepository;
    }

    @Override
    public Dashboard remoteCreate(DashboardRemoteRequest request, boolean isUpdate) throws HygieiaException {
        Dashboard dashboard;
        Map<String, Widget> existingWidgets = new HashMap<>();

        if (!userInfoService.isUserValid(request.getMetaData().getOwner().getUsername(), request.getMetaData().getOwner().getAuthType())) {
            throw new HygieiaException("Invalid owner information or authentication type. Owner first needs to sign up in Hygieia", HygieiaException.BAD_DATA);
        }

        List<Dashboard> dashboards = dashboardRepository.findByTitle(request.getMetaData().getTitle());
        if (!CollectionUtils.isEmpty(dashboards)) {
            dashboard = dashboards.get(0);
            if (!isUpdate) {
                throw new HygieiaException("Dashboard " + dashboard.getTitle() + " (id =" + dashboard.getId() + ") already exists", HygieiaException.DUPLICATE_DATA);
            }
            //Save the widgets
            for (Widget w : dashboard.getWidgets()) {
                existingWidgets.put(w.getName(), w);
            }

        } else {
            if (isUpdate) {
                throw new HygieiaException("Dashboard " + request.getMetaData().getTitle() +  "does not exist.", HygieiaException.BAD_DATA);
            }
            dashboard = dashboardService.create(requestToDashboard(request));
        }

        List<DashboardRemoteRequest.Entry> entries = request.getAllEntries();
        Map<String, WidgetRequest> allWidgetRequests = new HashMap<>();

        for (DashboardRemoteRequest.Entry entry : entries) {
            List<Collector> collectors = collectorRepository.findByCollectorTypeAndName(entry.getType(), entry.getToolName());
            if (CollectionUtils.isEmpty(collectors)) {
                throw new HygieiaException(entry.getToolName() + " collector is not available.", HygieiaException.BAD_DATA);
            }
            Collector collector = collectors.get(0);
            WidgetRequest widgetRequest = allWidgetRequests.get(entry.getWidgetName());

            if (widgetRequest == null) {
                widgetRequest = entryToWidgetRequest(dashboard, entry, collector);
                allWidgetRequests.put(entry.getWidgetName(), widgetRequest);
            } else {
                CollectorItem item = entryToCollectorItem(entry, collector);
                if (item != null) {
                    widgetRequest.getCollectorItemIds().add(item.getId());
                }
            }
        }

        for (String key : allWidgetRequests.keySet()) {
            WidgetRequest widgetRequest = allWidgetRequests.get(key);
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
        ObjectId appId = null;
        ObjectId serviceId = null;
        if (!StringUtils.isEmpty(metaData.getBusinessApplication())) {
            Cmdb app = cmdbRepository.findByConfigurationItemAndItemType(metaData.getBusinessApplication(), "component");
            if (app == null) throw new HygieiaException("Invalid Business Application Name.", HygieiaException.BAD_DATA);
            appId = app.getId();
        }
        if (!StringUtils.isEmpty(metaData.getBusinessService())) {
            Cmdb service = cmdbRepository.findByConfigurationItemAndItemType(metaData.getBusinessService(), "app");
            if (service == null) throw new HygieiaException("Invalid Business Service Name.", HygieiaException.BAD_DATA);
            serviceId = service.getId();
        }
        List<String> activeWidgets = new ArrayList<>();
        return new Dashboard(true, metaData.getTemplate(), metaData.getTitle(), application, metaData.getOwner(), DashboardType.fromString(metaData.getType()), serviceId, appId,activeWidgets);
    }
}
