package com.capitalone.dashboard.request;


import java.util.List;
import java.util.Map;

import org.bson.types.ObjectId;

public class WeblogicRequest {

    private List<ObjectId> selectedCollectorItemIds;
    private Map<ObjectId,String> collectorItemIds;

    public List<ObjectId> getSelectedCollectorItemIds() {
        return selectedCollectorItemIds;
    }
    public Map<ObjectId, String> getCollectorItemIds() {
        return collectorItemIds;
    }
    public void setSelectedCollectorItemIds(List<ObjectId> selectedCollectorItemIds) {
        this.selectedCollectorItemIds = selectedCollectorItemIds;
    }
    public void setCollectorItemIds(Map<ObjectId, String> collectorItemIds) {
        this.collectorItemIds = collectorItemIds;
    }
}