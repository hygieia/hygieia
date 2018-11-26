package com.capitalone.dashboard.evaluator;

import com.capitalone.dashboard.ApiSettings;
import com.capitalone.dashboard.misc.HygieiaException;
import com.capitalone.dashboard.model.AuditException;
import com.capitalone.dashboard.model.CollectorItem;
import com.capitalone.dashboard.model.CollectorType;
import com.capitalone.dashboard.model.Dashboard;
import com.capitalone.dashboard.model.Widget;
import com.capitalone.dashboard.repository.CollectorItemRepository;
import com.capitalone.dashboard.repository.ComponentRepository;
import com.capitalone.dashboard.repository.DashboardRepository;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class Evaluator<T> {

    @Autowired
    protected ComponentRepository componentRepository;

    @Autowired
    protected DashboardRepository dashboardRepository;

    @Autowired
    protected CollectorItemRepository collectorItemRepository;

    @Autowired
    protected ApiSettings settings;

    public abstract Collection<T> evaluate(Dashboard dashboard, long beginDate, long endDate, Map<?, ?> data) throws AuditException;

    public abstract T evaluate(CollectorItem collectorItem, long beginDate, long endDate, Map<?, ?> data) throws AuditException, HygieiaException;

    /**
     * @param dashboard the dashboard
     * @param widgetName the widget name
     * @param collectorType the collector type
     * @return list of @CollectorItem for a given dashboard, widget name and collector type
     */
    List<CollectorItem> getCollectorItems(Dashboard dashboard, String widgetName, CollectorType collectorType) {
        List<Widget> widgets = dashboard.getWidgets();
        ObjectId componentId = widgets.stream().filter(widget -> widget.getName().equalsIgnoreCase(widgetName)).findFirst().map(Widget::getComponentId).orElse(null);

        if (null == componentId) return null;

        com.capitalone.dashboard.model.Component component = componentRepository.findOne(componentId);

        // This list from component is stale. So, need the id's to look up current state of collector items.
        List<CollectorItem> listFromComponent = component.getCollectorItems().get(collectorType);

        if (CollectionUtils.isEmpty(listFromComponent)) {
            return null;
        }

        List<ObjectId> ids = listFromComponent.stream().map(CollectorItem::getId).collect(Collectors.toList());
        return Lists.newArrayList(collectorItemRepository.findAll(ids));
    }


    public Dashboard getDashboard(String businessService, String businessComponent) {
        return dashboardRepository.findByConfigurationItemBusServNameIgnoreCaseAndConfigurationItemBusAppNameIgnoreCase(businessService, businessComponent);
    }
}
