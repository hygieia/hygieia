package com.capitalone.dashboard.evaluator;

import com.capitalone.dashboard.ApiSettings;
import com.capitalone.dashboard.misc.HygieiaException;
import com.capitalone.dashboard.model.Collector;
import com.capitalone.dashboard.model.CollectorItem;
import com.capitalone.dashboard.model.CollectorType;
import com.capitalone.dashboard.model.Dashboard;
import com.capitalone.dashboard.model.Widget;
import com.capitalone.dashboard.repository.CmdbRepository;
import com.capitalone.dashboard.repository.CollectorItemRepository;
import com.capitalone.dashboard.repository.CollectorRepository;
import com.capitalone.dashboard.repository.ComponentRepository;
import com.capitalone.dashboard.repository.CustomRepositoryQuery;
import com.capitalone.dashboard.repository.DashboardRepository;
import com.capitalone.dashboard.response.GenericAuditResponse;
import org.apache.commons.collections.CollectionUtils;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public abstract class Evaluator {

    @Autowired
    protected ComponentRepository componentRepository;

    @Autowired
    protected CmdbRepository cmdbRepository;

    @Autowired
    protected DashboardRepository dashboardRepository;

    @Autowired
    protected CollectorItemRepository collectorItemRepository;

    @Autowired
    protected CustomRepositoryQuery customRepositoryQuery;

    @Autowired
    protected CollectorRepository collectorRepository;

    @Autowired
    protected ApiSettings settings;


    public abstract GenericAuditResponse evaluate(Dashboard dashboard, long beginDate, long endDate, Collection<?> data) throws HygieiaException;

    public abstract Collection<?> evaluate(CollectorItem collectorItem, long beginDate, long endDate, Collection<?> data) throws HygieiaException;

    public GenericAuditResponse evaluate(String businessService, String businessComponent, long beginDate, long endDate, Collection<?> data) throws HygieiaException {
        return evaluate(getDashboard(businessService, businessComponent), beginDate, endDate, data);
    }

    public Collection<?> evaluate(CollectorType collctorType, String collectorName, Map<String, Object> option, long beginDate, long endDate, Collection<?> data) throws HygieiaException {
        Collection<GenericAuditResponse> responses = new ArrayList<>();
        List<Collector> collectors = collectorRepository.findByCollectorTypeAndName(collctorType, collectorName);
        if (CollectionUtils.isEmpty(collectors)) {
            throw new HygieiaException("Invalid collector", HygieiaException.BAD_DATA);
        }
        Collector collector = collectors.get(0);

        List<CollectorItem> collectorItems = customRepositoryQuery.findCollectorItemsBySubsetOptions(collector.getId(), collector.getAllFields(), option);

        if (CollectionUtils.isEmpty(collectorItems)) {
            throw new HygieiaException("Invalid options", HygieiaException.BAD_DATA);
        }

        if (collectorItems.size() > 1) {
            throw new HygieiaException("Multiple collector items found", HygieiaException.BAD_DATA);
        }
        CollectorItem collectorItem = collectorItems.get(0);


        return  evaluate(collectorItem, beginDate, endDate, data);

    }


    /**
     * @param dashboard
     * @param widgetName
     * @param collectorType
     * @return list of @CollectorItem for a given dashboard, widget name and collector type
     */
    public List<CollectorItem> getCollectorItems(Dashboard dashboard, String widgetName, CollectorType collectorType) {
        List<Widget> widgets = dashboard.getWidgets();
        ObjectId componentId = widgets.stream().filter(widget -> widget.getName().equalsIgnoreCase(widgetName)).findFirst().map(Widget::getComponentId).orElse(null);

        if (componentId == null) return null;

        com.capitalone.dashboard.model.Component component = componentRepository.findOne(componentId);

        return component.getCollectorItems().get(collectorType);
    }


    public Dashboard getDashboard(String businessService, String businessComponent) {
        return dashboardRepository.findDashboardByConfigurationItemBusServNameAndConfigurationItemBusAppName(businessService, businessComponent);
    }
}
