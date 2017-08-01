package com.capitalone.dashboard.service;


import java.util.List;
import java.util.Map;

import org.bson.types.ObjectId;

import com.capitalone.dashboard.model.Component;
import com.capitalone.dashboard.model.WebLogicMonitor;

public interface WLMonitorService {
    List<WebLogicMonitor> getAll();

    List<WebLogicMonitor> getAllServersByEnvName(String envName);
    int addEnvironments(Map<ObjectId, String> collectorItemIds);

    Component associateCollectorToComponent(ObjectId componentId, List<ObjectId> selectedCollectorItemIds);
}