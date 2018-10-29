package com.capitalone.dashboard.event;

import com.capitalone.dashboard.model.Collector;
import com.capitalone.dashboard.model.CollectorItem;
import com.capitalone.dashboard.model.CollectorType;
import com.capitalone.dashboard.model.Component;
import com.capitalone.dashboard.repository.CollectorRepository;
import com.capitalone.dashboard.repository.ComponentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.AfterSaveEvent;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@org.springframework.stereotype.Component
public class CollectorItemEventListener extends AbstractMongoEventListener<CollectorItem> {

    private final ComponentRepository componentRepository;
    private final CollectorRepository collectorRepository;

    @Autowired
    public CollectorItemEventListener(ComponentRepository componentRepository,CollectorRepository collectorRepository) {
        this.componentRepository = componentRepository;
        this.collectorRepository = collectorRepository;
     }

    @Override
    public void onAfterSave(AfterSaveEvent<CollectorItem> event) {
        CollectorItem collectorItem = event.getSource();
        if(collectorItem.isEnabled()) {
            Collector collector = collectorRepository.findOne(collectorItem.getCollectorId());
            CollectorType collectorType = collector.getCollectorType();
            List<Component> components = componentRepository.findByCollectorTypeAndItemIdIn(collectorType,Stream.of(collectorItem.getId()).collect(Collectors.toList()));
            components.forEach(component -> { updateCollectorItemsForComponent(component, collectorItem); });
        }
    }

    private void updateCollectorItemsForComponent(Component component,CollectorItem collectorItem){
        Map<CollectorType, List<CollectorItem>> collectorItemsByType = component.getCollectorItems();
        collectorItemsByType.forEach((collectorType,collectorItems)-> {
           saveCollectorItemsByType(collectorType,collectorItems,collectorItem,component);
        });
        componentRepository.save(component);
    }

    private void saveCollectorItemsByType(CollectorType collectorType,List<CollectorItem> collectorItems,CollectorItem newCollectorItem,Component component){
        collectorItems.forEach(collectorItem -> {
            CollectorItem c = setLastUpdated(collectorItem,newCollectorItem);
            component.updateCollectorItem(collectorType,c);
        });
    }

    private CollectorItem setLastUpdated(CollectorItem existingCollectorItem, CollectorItem newCollectorItem) {
        if(newCollectorItem.getLastUpdated() > existingCollectorItem.getLastUpdated()){
            existingCollectorItem.setLastUpdated(newCollectorItem.getLastUpdated());
        }
        return existingCollectorItem;
    }

}
