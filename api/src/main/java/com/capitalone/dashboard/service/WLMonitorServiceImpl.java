package com.capitalone.dashboard.service;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.capitalone.dashboard.model.CollectorItem;
import com.capitalone.dashboard.model.CollectorType;
import com.capitalone.dashboard.model.Component;
import com.capitalone.dashboard.model.WebLogicMonitor;
import com.capitalone.dashboard.repository.CollectorItemRepository;
import com.capitalone.dashboard.repository.ComponentRepository;
import com.capitalone.dashboard.repository.WLMonitorRepository;

@Service
public class WLMonitorServiceImpl implements WLMonitorService {

    private final WLMonitorRepository wLMonitorRepository;
    private final CollectorItemRepository collectorItemRepository;
    private final ComponentRepository componentRepository;
    @Autowired
    public WLMonitorServiceImpl(WLMonitorRepository wLMonitorRepository,CollectorItemRepository collectorItemRepository,ComponentRepository componentRepository) {
        this.wLMonitorRepository = wLMonitorRepository;
        this.collectorItemRepository = collectorItemRepository;
        this.componentRepository = componentRepository;
    }

    @Override
    public List<WebLogicMonitor> getAll() {
        return wLMonitorRepository.findAll();
    }

    @Override
    public List<WebLogicMonitor> getAllServersByEnvName(String envName){
        List<WebLogicMonitor> vmList = new ArrayList<WebLogicMonitor>();
        vmList = wLMonitorRepository.findAllByEnvironementName(envName);
        return vmList;
    }

    public int addEnvironments(Map<ObjectId,String> collectorItemIdMap)
    {
        int i=0;
        Iterator<Entry<ObjectId, String>> it = collectorItemIdMap.entrySet().iterator();
        while (it.hasNext()) {
            @SuppressWarnings("rawtypes")
            Map.Entry pair = (Map.Entry)it.next();
            CollectorItem collectorItem = collectorItemRepository
                    .findOne((ObjectId) pair.getKey());

            if(pair.getValue().toString().equalsIgnoreCase("true"))
            {
                collectorItem.setEnabled(true);

            }
            else
            {
                collectorItem.setEnabled(false);

            }
            collectorItemRepository.save(collectorItem);
            i++;
        }
        return i;
    }

    public Component associateCollectorToComponent(ObjectId componentId, List<ObjectId> collectorItemIds) {
        if (componentId == null || collectorItemIds == null) {
            return null;
        }
        Component component = componentRepository.findOne(componentId);

        component.getCollectorItems().remove(CollectorType.WLMonitor);

        for(ObjectId collectorItemId : collectorItemIds){
            CollectorItem collectorItem = collectorItemRepository.findOne(collectorItemId);
            component.addCollectorItem(CollectorType.WLMonitor, collectorItem);
        }
        componentRepository.save(component);
        return component;
    }
}


