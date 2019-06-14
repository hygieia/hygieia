package com.capitalone.dashboard.request;

import com.capitalone.dashboard.model.Widget;
import org.bson.types.ObjectId;

import java.util.List;
import java.util.Map;

public class WidgetRequest {
    private String name;
    private ObjectId componentId;
    private List<ObjectId> collectorItemIds;
    private Map<String, Object> options;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ObjectId getComponentId() {
        return componentId;
    }

    public void setComponentId(ObjectId componentId) {
        this.componentId = componentId;
    }

    public List<ObjectId> getCollectorItemIds() {
        return collectorItemIds;
    }

    public void setCollectorItemIds(List<ObjectId> collectorItemIds) {
        this.collectorItemIds = collectorItemIds;
    }

    public Map<String, Object> getOptions() {
        return options;
    }

    public void setOptions(Map<String, Object> options) {
        this.options = options;
    }

    public Widget widget() {
        Widget widget = new Widget();
        widget.setName(name);
        widget.setComponentId(componentId);
        if ((options != null) && !options.isEmpty()) {
            widget.getOptions().put("id",options.get("id"));
            if("build".equalsIgnoreCase(name)){
                widget.getOptions().put("buildDurationThreshold",3);
                widget.getOptions().put("consecutiveFailureThreshold",5);
            } else if ("AgileTool".equalsIgnoreCase(name) || "feature".equalsIgnoreCase(name)) {
                widget.getOptions().putAll(options);
            }
        }
        return widget;
    }

    public Widget updateWidget(Widget widget) {
        widget.setComponentId(componentId);
        widget.setName(name);
        widget.getOptions().clear();
        if ((options != null) && !options.isEmpty()) {
            widget.getOptions().putAll(options);
        }
        return widget;
    }
}
