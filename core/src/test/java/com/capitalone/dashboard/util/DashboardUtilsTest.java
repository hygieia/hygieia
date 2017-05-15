package com.capitalone.dashboard.util;

import com.capitalone.dashboard.model.Collector;
import com.capitalone.dashboard.model.CollectorItem;
import com.capitalone.dashboard.model.CollectorType;
import com.capitalone.dashboard.model.Component;
import com.capitalone.dashboard.repository.CollectorItemRepository;
import com.capitalone.dashboard.repository.CollectorRepository;
import com.capitalone.dashboard.repository.ComponentRepository;
import com.capitalone.dashboard.repository.FongoBaseRepositoryTest;
import org.bson.types.ObjectId;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.Set;

import static org.junit.Assert.*;

public class DashboardUtilsTest extends FongoBaseRepositoryTest{

    @Autowired
    private ComponentRepository componentRepository;

    @Autowired
    private CollectorRepository collectorRepository;

    @Autowired
    private CollectorItemRepository collectorItemRepository;


    @Test
    public void getUniqueCollectorItemIDsFromAllComponents() throws Exception {
        Component component1 = getComponent("TestComponent1");
        Collector collector1 = getCollector("TestCollector1",CollectorType.Build);
        CollectorItem collectorItem1 = getCollectorItem("TestCollectorItem1",collector1);
        component1.addCollectorItem(collector1.getCollectorType(), collectorItem1);
        Collector collector11 = getCollector("TestCollector1",CollectorType.SCM);
        CollectorItem collectorItem11 = getCollectorItem("TestCollectorItem11",collector11);
        component1.addCollectorItem(collector11.getCollectorType(), collectorItem11);

        Component component2 = getComponent("TestComponent2");
        CollectorItem collectorItem2 = getCollectorItem("TestCollectorItem2",collector11);
        component2.addCollectorItem(collector11.getCollectorType(), collectorItem2);


        Component component3 = getComponent("TestComponent2");
        component3.addCollectorItem(collector11.getCollectorType(), collectorItem2);

        componentRepository.save(Arrays.asList(component1,component2,component3));


        Set<ObjectId> uniqueIds = DashboardUtils.getUniqueCollectorItemIDsFromAllComponents(componentRepository,collector1);
        assertEquals(uniqueIds.size(),1);
        assertEquals(uniqueIds.contains(collectorItem1.getId()),true);

        uniqueIds = DashboardUtils.getUniqueCollectorItemIDsFromAllComponents(componentRepository,collector11);
        assertEquals(uniqueIds.size(),2);
        assertEquals(uniqueIds.contains(collectorItem2.getId()),true);
        assertEquals(uniqueIds.contains(collectorItem11.getId()),true);
    }

    private Component getComponent(String name) {
        Component component = new Component();
        component.setName(name);
        component.setOwner("Topo");
        return component;
    }

    private Collector getCollector (String name, CollectorType type) {
        Collector collector = new Collector();
        collector.setCollectorType(type);
        collector.setEnabled(true);
        collector.setName(name);
        return collectorRepository.save(collector);
    }

    private CollectorItem getCollectorItem (String description, Collector collector) {
        CollectorItem collectorItem = new CollectorItem();
        collectorItem.setEnabled(true);
        collectorItem.setCollectorId(collector.getId());
        collectorItem.setCollector(collector);
        collectorItem.setDescription(description);
        return collectorItemRepository.save(collectorItem);
    }
}