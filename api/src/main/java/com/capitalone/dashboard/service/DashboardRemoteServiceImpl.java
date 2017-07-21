package com.capitalone.dashboard.service;

import com.capitalone.dashboard.misc.HygieiaException;
import com.capitalone.dashboard.model.Application;
import com.capitalone.dashboard.model.AuthType;
import com.capitalone.dashboard.model.Cmdb;
import com.capitalone.dashboard.model.Collector;
import com.capitalone.dashboard.model.CollectorItem;
import com.capitalone.dashboard.model.Component;
import com.capitalone.dashboard.model.Dashboard;
import com.capitalone.dashboard.model.DashboardType;
import com.capitalone.dashboard.model.Owner;
import com.capitalone.dashboard.repository.CmdbRepository;
import com.capitalone.dashboard.repository.CollectorRepository;
import com.capitalone.dashboard.repository.DashboardRepository;
import com.capitalone.dashboard.request.DashboardRemoteRequest;
import com.capitalone.dashboard.request.WidgetRequest;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DashboardRemoteServiceImpl implements DashboardRemoteService {
    private final CollectorRepository collectorRepository;
    private final DashboardRepository dashboardRepository;
    private final DashboardService dashboardService;
    private final CollectorService collectorService;
    private final UserInfoService userInfoService;
    private final CmdbRepository cmdbRepository;


    @Autowired
    public DashboardRemoteServiceImpl(
            CollectorRepository collectorRepository,
            DashboardRepository dashboardRepository, DashboardService dashboardService, CollectorService collectorService, UserInfoService userInfoService, CmdbRepository cmdbRepository) {
        this.collectorRepository = collectorRepository;
        this.dashboardRepository = dashboardRepository;
        this.dashboardService = dashboardService;
        this.collectorService = collectorService;
        this.userInfoService = userInfoService;
        this.cmdbRepository = cmdbRepository;
    }

    @Override
    public Dashboard remoteCreate(DashboardRemoteRequest request) throws HygieiaException {
        Dashboard dashboard;
        List<Dashboard> dashboards = dashboardRepository.findByTitle(request.getMetaData().getTitle());
        if (!CollectionUtils.isEmpty(dashboards)) {
            dashboard = dashboards.get(0);
            dashboard.getWidgets().clear();
        } else {
            dashboard = dashboardService.create(requestToDashboard(request));
        }
        if (!isValidOwner(request.getMetaData().getOwner())) {
            throw new HygieiaException("Invalid owner information or authentication type. Owner first needs to sign up in Hygieia", HygieiaException.BAD_DATA);
        }

        List<DashboardRemoteRequest.Entry> entries = request.getAllEntries();
        Map<String, WidgetRequest> allWidgetRequests = new HashMap<>();

        for (DashboardRemoteRequest.Entry entry : entries) {
            WidgetRequest widgetRequest = allWidgetRequests.get(entry.getWidgetName());
            if (widgetRequest == null) {
                widgetRequest = entryToWidgetRequest(dashboard, entry);
                allWidgetRequests.put(entry.getWidgetName(), widgetRequest);
            } else {
                CollectorItem item = entryToCollectorItem(entry);
                if (item != null) {
                    widgetRequest.getCollectorItemIds().add(item.getId());
                }
            }
        }

        for (String key : allWidgetRequests.keySet()) {
            WidgetRequest widgetRequest = allWidgetRequests.get(key);
            Component component = dashboardService.associateCollectorToComponent(
                    dashboard.getApplication().getComponents().get(0).getId(), widgetRequest.getCollectorItemIds());
            dashboardService.addWidget(dashboard, widgetRequest.widget());
        }

        return (dashboard != null) ? dashboardService.get(dashboard.getId()) : null;
    }

    /**
     * Creates a collector item from an entry in the request.
     * @param entry
     * @return CollectorItem
     */

    private CollectorItem entryToCollectorItem(DashboardRemoteRequest.Entry entry) {
        CollectorItem item = entry.toCollectorItem();
        List<Collector> collectors = collectorRepository.findByCollectorTypeAndName(entry.getType(), entry.getToolName());
        if (CollectionUtils.isEmpty(collectors)) return null;
        item.setCollectorId(collectors.get(0).getId());
        return collectorService.createCollectorItem(item);
    }

    /**
     * Creates a widget from entry
     * @param dashboard
     * @param entry
     * @return WidgetRequest
     */
    private WidgetRequest entryToWidgetRequest(Dashboard dashboard, DashboardRemoteRequest.Entry entry) {
        WidgetRequest request = new WidgetRequest();
        CollectorItem item = entryToCollectorItem(entry);
        if (item != null) {
            request.setName(entry.getWidgetName());
            request.setComponentId(dashboard.getApplication().getComponents().get(0).getId());
            request.setOptions(entry.toWidgetOptions());
            request.setCollectorItemIds(Arrays.asList(item.getId()));
        }
        return request;
    }

    /**
     * Validates owner. If owner is STANDARD auth, we need the owner already signed up in Hygieia.
     * If owner is LDAP authed, we verify with LDAP that the user exists
     * @param owner
     * @return boolean
     * @throws HygieiaException
     */

    private boolean isValidOwner(Owner owner) throws HygieiaException {
        if (userInfoService.getUser(owner.getUsername(), owner.getAuthType()) != null) {
            return true;
        } else if (owner.getAuthType().equals(AuthType.LDAP)) {
            return true; //TODO: Need to implement LDAP user id validation.
        } else {
            return false;
        }
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
            Cmdb app = cmdbRepository.findByConfigurationItemAndItemType(metaData.getBusinessApplication(), "app");
            if (app == null) throw new HygieiaException("Invalid Business Application Name.", HygieiaException.BAD_DATA);
            appId = app.getId();
        }
        if (!StringUtils.isEmpty(metaData.getBusinessService())) {
            Cmdb service = cmdbRepository.findByConfigurationItemAndItemType(metaData.getBusinessService(), "service");
            if (service == null) throw new HygieiaException("Invalid Business Service Name.", HygieiaException.BAD_DATA);
            serviceId = service.getId();
        }
        return new Dashboard(metaData.getTemplate(), metaData.getTitle(), application, metaData.getOwner(), DashboardType.fromString(metaData.getType()), serviceId, appId);
    }
}
