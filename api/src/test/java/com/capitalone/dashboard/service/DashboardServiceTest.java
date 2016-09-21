package com.capitalone.dashboard.service;

import com.capitalone.dashboard.misc.HygieiaException;
import com.capitalone.dashboard.model.Application;
import com.capitalone.dashboard.model.Collector;
import com.capitalone.dashboard.model.CollectorItem;
import com.capitalone.dashboard.model.CollectorType;
import com.capitalone.dashboard.model.Component;
import com.capitalone.dashboard.model.Dashboard;
import com.capitalone.dashboard.model.DashboardType;
import com.capitalone.dashboard.model.Service;
import com.capitalone.dashboard.model.Widget;
import com.capitalone.dashboard.repository.CollectorItemRepository;
import com.capitalone.dashboard.repository.CollectorRepository;
import com.capitalone.dashboard.repository.ComponentRepository;
import com.capitalone.dashboard.repository.DashboardRepository;
import com.capitalone.dashboard.repository.ServiceRepository;
import com.google.common.collect.Lists;
import org.bson.types.ObjectId;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DashboardServiceTest {

    @Mock
    private DashboardRepository dashboardRepository;
    @Mock
    private ComponentRepository componentRepository;
    @Mock
    private CollectorRepository collectorRepository;
    @Mock
    private CollectorItemRepository collectorItemRepository;
    @Mock
    private ServiceRepository serviceRepository;
    @InjectMocks
    private DashboardServiceImpl dashboardService;

    @Test
    public void all() {
        Iterable<Dashboard> expected = Lists.newArrayList();
        when(dashboardRepository.findAll(Mockito.any(Sort.class))).thenReturn(expected);

        Iterable<Dashboard> actual = dashboardService.all();

        assertThat(actual, is(expected));
    }

    @Test
    public void get() {
        ObjectId id = ObjectId.get();
        Dashboard expected = makeTeamDashboard("template", "title", "AppName", "comp1");
        when(dashboardRepository.findOne(id)).thenReturn(expected);

        assertThat(dashboardService.get(id), is(expected));
    }

    @Test
    public void create() throws HygieiaException {
        Dashboard expected = makeTeamDashboard("template", "title", "appName", "comp1", "comp2");

        when(dashboardRepository.save(expected)).thenReturn(expected);

        assertThat(dashboardService.create(expected), is(expected));
        verify(componentRepository, times(1)).save(expected.getApplication().getComponents());
    }

    @Test
    public void create_dup_name_dash() throws HygieiaException {
        Dashboard firstDash = makeTeamDashboard("template", "title", "appName", "johns", "comp1", "comp2");
        when(dashboardRepository.save(firstDash)).thenReturn(firstDash);
        assertThat(dashboardService.create(firstDash), is(firstDash));
        verify(componentRepository, times(1)).save(firstDash.getApplication().getComponents());

        Dashboard secondDash = makeTeamDashboard("template", "title", "appName", "johns", "comp1", "comp2");

        Throwable t = new Throwable();
        RuntimeException excep = new RuntimeException("Failed creating dashboard.", t);
        when(dashboardRepository.save(secondDash)).thenThrow(excep);

        Iterable<Component> components = secondDash.getApplication().getComponents();
        when(componentRepository.save(secondDash.getApplication().getComponents())).thenReturn(components);

        try {
            dashboardService.create(secondDash);
            fail("Should throw RuntimeException");
        } catch(Exception e) {
            assertEquals(excep.getMessage(), e.getMessage());
        }

        verify(componentRepository).delete(components);
    }

    @Test
    public void update() throws HygieiaException {
        Dashboard expected = makeTeamDashboard("template", "title", "appName", "comp1", "comp2");

        when(dashboardRepository.save(expected)).thenReturn(expected);

        assertThat(dashboardService.update(expected), is(expected));
        verify(componentRepository, times(1)).save(expected.getApplication().getComponents());
    }

    @Test
    public void associateCollectorToComponent() {
        ObjectId compId = ObjectId.get();
        ObjectId collId = ObjectId.get();
        ObjectId collItemId = ObjectId.get();
        List<ObjectId> collItemIds = Arrays.asList(collItemId);

        CollectorItem item = new CollectorItem();
        item.setCollectorId(collId);
        item.setEnabled(true);
        Collector collector = new Collector();
        collector.setCollectorType(CollectorType.Build);
        Component component = new Component();

        when(collectorItemRepository.findOne(collItemId)).thenReturn(item);
        when(collectorRepository.findOne(collId)).thenReturn(collector);
        when(componentRepository.findOne(compId)).thenReturn(component);

        dashboardService.associateCollectorToComponent(compId, collItemIds);

        assertThat(component.getCollectorItems().get(CollectorType.Build), contains(item));

        verify(componentRepository).save(component);
        verify(collectorItemRepository, never()).save(item);
    }

    @Test
    public void associateCollectorToComponent_collectorItemDisabled_willBecomeEnabled() {
        ObjectId compId = ObjectId.get();
        ObjectId collId = ObjectId.get();
        ObjectId collItemId = ObjectId.get();
        List<ObjectId> collItemIds = Arrays.asList(collItemId);

        CollectorItem item = new CollectorItem();
        item.setCollectorId(collId);
        item.setEnabled(false);
        Collector collector = new Collector();
        collector.setCollectorType(CollectorType.Build);
        Component component = new Component();
        HashSet<CollectorItem> set = new HashSet<>();
        set.add(item);

        when(collectorItemRepository.findOne(collItemId)).thenReturn(item);
        when(collectorRepository.findOne(collId)).thenReturn(collector);
        when(componentRepository.findOne(compId)).thenReturn(component);

        dashboardService.associateCollectorToComponent(compId, collItemIds);

        assertThat(component.getCollectorItems().get(CollectorType.Build), contains(item));
        assertThat(item.isEnabled(), is(true));

        verify(componentRepository).save(component);
        verify(collectorItemRepository).save((set));
    }


    @Test
    public void associateCollectorToComponent_switch_Item1_with_Item2() {
        ObjectId compId = ObjectId.get();
        ObjectId collId = ObjectId.get();

        ObjectId collItemId1 = ObjectId.get();
        ObjectId collItemId2 = ObjectId.get();

        List<ObjectId> collItemIds = Arrays.asList(collItemId2);

        CollectorItem item1 = new CollectorItem();
        item1.setCollectorId(collId);
        item1.setId(collItemId1);
        item1.setEnabled(true);
        Collector collector = new Collector();
        collector.setCollectorType(CollectorType.Build);
        Component component = new Component();
        component.addCollectorItem(CollectorType.Build, item1);

        HashSet<CollectorItem> set = new HashSet<>();
        set.add(item1);

        CollectorItem item2 = new CollectorItem();
        item2.setCollectorId(collId);
        item2.setId(collItemId2);
        set.add(item2);


        when(collectorItemRepository.findOne(collItemId1)).thenReturn(item1);
        when(collectorItemRepository.findOne(collItemId2)).thenReturn(item2);
        when(collectorRepository.findOne(collId)).thenReturn(collector);
        when(componentRepository.findOne(compId)).thenReturn(component);

        dashboardService.associateCollectorToComponent(compId, collItemIds);

        assertThat(component.getCollectorItems().get(CollectorType.Build), contains(item2));
        assertThat(item1.isEnabled(), is(false));
        assertThat(item2.isEnabled(), is(true));

        verify(componentRepository).save(component);
        verify(collectorItemRepository).save((set));
    }


    @Test
    public void associateCollectorToComponent_replace_Item1_with_Item2_and_3() {
        ObjectId compId = ObjectId.get();
        ObjectId collId = ObjectId.get();

        ObjectId collItemId1 = ObjectId.get();
        ObjectId collItemId2 = ObjectId.get();
        ObjectId collItemId3 = ObjectId.get();

        List<ObjectId> collItemIds = Arrays.asList(collItemId2, collItemId3);

        HashSet<CollectorItem> set = new HashSet<>();

        CollectorItem item1 = new CollectorItem();
        item1.setCollectorId(collId);
        item1.setId(collItemId1);
        item1.setEnabled(true);
        set.add(item1);

        Collector collector = new Collector();
        collector.setCollectorType(CollectorType.Build);
        Component component = new Component();
        component.addCollectorItem(CollectorType.Build, item1);

        CollectorItem item2 = new CollectorItem();
        item2.setCollectorId(collId);
        item2.setId(collItemId2);
        set.add(item2);

        CollectorItem item3 = new CollectorItem();
        item3.setCollectorId(collId);
        item3.setId(collItemId3);
        set.add(item3);

        when(collectorItemRepository.findOne(collItemId1)).thenReturn(item1);
        when(collectorItemRepository.findOne(collItemId2)).thenReturn(item2);
        when(collectorItemRepository.findOne(collItemId3)).thenReturn(item3);
        when(collectorRepository.findOne(collId)).thenReturn(collector);
        when(componentRepository.findOne(compId)).thenReturn(component);

        dashboardService.associateCollectorToComponent(compId, collItemIds);

        assertThat(component.getCollectorItems().get(CollectorType.Build), contains(item2, item3));
        assertThat(item1.isEnabled(), is(false));
        assertThat(item2.isEnabled(), is(true));
        assertThat(item3.isEnabled(), is(true));

        verify(componentRepository).save(component);
        verify(collectorItemRepository).save((set));
    }

    @Test
    public void associateCollectorToComponent_replace_Item1_and_2_with_Item3() {
        ObjectId compId = ObjectId.get();
        ObjectId collId = ObjectId.get();

        ObjectId collItemId1 = ObjectId.get();
        ObjectId collItemId2 = ObjectId.get();
        ObjectId collItemId3 = ObjectId.get();

        List<ObjectId> collItemIds = Arrays.asList(collItemId3);

        HashSet<CollectorItem> set = new HashSet<>();

        CollectorItem item1 = new CollectorItem();
        item1.setCollectorId(collId);
        item1.setId(collItemId1);
        item1.setEnabled(true);
        set.add(item1);

        CollectorItem item2 = new CollectorItem();
        item2.setCollectorId(collId);
        item2.setId(collItemId2);
        set.add(item2);

        Collector collector = new Collector();
        collector.setCollectorType(CollectorType.Build);
        Component component = new Component();
        component.addCollectorItem(CollectorType.Build, item1);
        component.addCollectorItem(CollectorType.Build, item2);



        CollectorItem item3 = new CollectorItem();
        item3.setCollectorId(collId);
        item3.setId(collItemId3);
        set.add(item3);

        when(collectorItemRepository.findOne(collItemId1)).thenReturn(item1);
        when(collectorItemRepository.findOne(collItemId2)).thenReturn(item2);
        when(collectorItemRepository.findOne(collItemId3)).thenReturn(item3);
        when(collectorRepository.findOne(collId)).thenReturn(collector);
        when(componentRepository.findOne(compId)).thenReturn(component);

        dashboardService.associateCollectorToComponent(compId, collItemIds);

        assertThat(component.getCollectorItems().get(CollectorType.Build), contains(item3));
        assertThat(item1.isEnabled(), is(false));
        assertThat(item2.isEnabled(), is(false));
        assertThat(item3.isEnabled(), is(true));

        verify(componentRepository).save(component);
        verify(collectorItemRepository).save((set));
    }


    @Test
    public void delete() {
        ObjectId id = ObjectId.get();
        Dashboard expected = makeTeamDashboard("template", "title", "appName", "comp1", "comp2");
        when(dashboardRepository.findOne(id)).thenReturn(expected);

        List<Service> services = Arrays.asList(new Service());
        Service depService = new Service();
        depService.getDependedBy().add(id);
        when(serviceRepository.findByDashboardId(id)).thenReturn(services);
        when(serviceRepository.findByDependedBy(id)).thenReturn(Arrays.asList(depService));
        when(collectorRepository.findByCollectorType(CollectorType.Product)).thenReturn(new ArrayList<Collector>());

        dashboardService.delete(id);

        verify(componentRepository).delete(expected.getApplication().getComponents());
        verify(serviceRepository).delete(services);
        verify(serviceRepository).save(depService);
        verify(dashboardRepository).delete(expected);

        assertThat(depService.getDependedBy(), not(contains(id)));
    }

    @Test
    public void addWidget() {
        Dashboard d = makeTeamDashboard("template", "title", "appName", "amit");
        Widget expected = new Widget();

        Widget actual = dashboardService.addWidget(d, expected);

        assertThat(actual, is(expected));
        assertThat(actual.getId(), notNullValue());
        assertThat(d.getWidgets(), contains(actual));

        verify(dashboardRepository).save(d);
    }

    @Test
    public void getWidget() {
        ObjectId widgetId = ObjectId.get();
        Dashboard d = makeTeamDashboard("template", "title", "appName", "amit");
        Widget expected = new Widget();
        expected.setId(widgetId);
        d.getWidgets().add(expected);

        assertThat(dashboardService.getWidget(d, widgetId), is(expected));
    }

    @Test
    public void updateWidget() {
        ObjectId widgetId = ObjectId.get();
        Dashboard d = makeTeamDashboard("template", "title", "appName", "amit");
        d.getWidgets().add(makeWidget(widgetId, "existing"));
        Widget expected = makeWidget(widgetId, "updated");

        assertThat(dashboardService.updateWidget(d, expected), is(expected));
        assertThat(d.getWidgets(), contains(expected));
        assertThat(d.getWidgets().get(0).getName(), is(expected.getName()));

        verify(dashboardRepository).save(d);
    }

    private Dashboard makeTeamDashboard(String template, String title, String appName, String owner, String... compNames) {
        Application app = new Application(appName);
        for (String compName : compNames) {
            app.addComponent(new Component(compName));
        }
        return new Dashboard(template, title, app, owner, DashboardType.Team);
    }

    private Widget makeWidget(ObjectId id, String name) {
        Widget w = new Widget();
        w.setId(id);
        w.setName("updated");
        return w;
    }
}
