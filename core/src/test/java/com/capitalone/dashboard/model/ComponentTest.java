package com.capitalone.dashboard.model;

import org.bson.types.ObjectId;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;


public class ComponentTest {

    @Test
    public void testGetLastUpdatedCollectorItemForType() {
        Component component = new Component();
        List<CollectorItem> cItems = new ArrayList<>();
        cItems.add(makeCollectorItem(1537476665987L));
        cItems.add(makeCollectorItem(0));
        cItems.add(makeCollectorItem(1537471111111L));
        cItems.add(makeCollectorItem(1537472222222L));
        cItems.add(makeCollectorItem(1537473333333L));
        component.getCollectorItems().put(CollectorType.SCM, cItems);
        CollectorItem c = component.getLastUpdatedCollectorItemForType(CollectorType.SCM);
        Assert.assertArrayEquals(new long[]{1537476665987L}, new long[]{c.getLastUpdated()});

    }

    @Test
    public void testGetLastUpdatedCollectorItemForTypeForOneItem() {
        Component component = new Component();
        List<CollectorItem> cItems = new ArrayList<>();
        cItems.add(makeCollectorItem(1537473333333L));
        component.getCollectorItems().put(CollectorType.SCM, cItems);
        CollectorItem c = component.getLastUpdatedCollectorItemForType(CollectorType.SCM);
        Assert.assertArrayEquals(new long[]{1537473333333L}, new long[]{c.getLastUpdated()});

    }

    @Test
    public void testGetLastUpdatedCollectorItemForTypeForZero() {
        Component component = new Component();
        List<CollectorItem> cItems = new ArrayList<>();
        cItems.add(makeCollectorItem(1537473333333L));
        cItems.add(makeCollectorItem(0));
        component.getCollectorItems().put(CollectorType.SCM, cItems);
        CollectorItem c = component.getLastUpdatedCollectorItemForType(CollectorType.SCM);
        Assert.assertArrayEquals(new long[]{1537473333333L}, new long[]{c.getLastUpdated()});

    }

    @Test
    public void testGetLastUpdatedCollectorItemForTypeForZeros() {
        Component component = new Component();
        List<CollectorItem> cItems = new ArrayList<>();
        cItems.add(makeCollectorItem(0));
        cItems.add(makeCollectorItem(0));
        component.getCollectorItems().put(CollectorType.SCM, cItems);
        CollectorItem c = component.getLastUpdatedCollectorItemForType(CollectorType.SCM);
        Assert.assertArrayEquals(new long[]{0}, new long[]{c.getLastUpdated()});

    }

    @Test
    public void testGetLastUpdatedCollectorItemForTypeForOneItemZero() {
        Component component = new Component();
        List<CollectorItem> cItems = new ArrayList<>();
        cItems.add(makeCollectorItem(0));
        component.getCollectorItems().put(CollectorType.SCM, cItems);
        CollectorItem c = component.getLastUpdatedCollectorItemForType(CollectorType.SCM);
        Assert.assertArrayEquals(new long[]{0}, new long[]{c.getLastUpdated()});

    }

    private CollectorItem makeCollectorItem(long lastUpdated) {
        CollectorItem c = new CollectorItem();
        c.setId(ObjectId.get());
        c.setLastUpdated(lastUpdated);
        return c;
    }
}
