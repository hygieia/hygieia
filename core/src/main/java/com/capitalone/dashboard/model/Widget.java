package com.capitalone.dashboard.model;

import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents a widget on the dashboard. Each widget is associated with a specific component. The id, name and options
 * should be provided by the UI.
 */
public class Widget {
    private ObjectId id;
    private String name;
    private ObjectId componentId;
    private Map<String,Object> options = new HashMap<>();
    private List<ObjectId> collectorItemIds = new ArrayList<>();

    public List<ObjectId> getCollectorItemIds() {
        return collectorItemIds;
    }

    public void setCollectorItemIds(List<ObjectId> collectorItemIds) {
        this.collectorItemIds.clear();
        this.collectorItemIds.addAll(collectorItemIds);
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

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

    public Map<String, Object> getOptions() {
        return options;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Widget widget = (Widget) o;

        return id.equals(widget.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
