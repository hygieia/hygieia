package com.capitalone.dashboard.service;

import com.capitalone.dashboard.model.*;
import com.capitalone.dashboard.repository.*;
import com.google.common.collect.Lists;
import org.bson.types.ObjectId;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.data.domain.Sort;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class DashboardServiceTest {

    @Mock private DashboardRepository dashboardRepository;
    @Mock private ComponentRepository componentRepository;
    @Mock private CollectorRepository collectorRepository;
    @Mock private CollectorItemRepository collectorItemRepository;
    @Mock private ServiceRepository serviceRepository;
    @InjectMocks private DashboardServiceImpl dashboardService;

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
        Dashboard expected = makeDashboard("template", "title", "AppName", "comp1");
        when(dashboardRepository.findOne(id)).thenReturn(expected);

        assertThat(dashboardService.get(id), is(expected));
    }

    @Test
    public void create() {
        Dashboard expected = makeDashboard("template", "title", "appName", "comp1", "comp2");

        when(dashboardRepository.save(expected)).thenReturn(expected);

        assertThat(dashboardService.create(expected), is(expected));
        verify(componentRepository, times(1)).save(expected.getApplication().getComponents());
    }

    @Test
    public void update() {
        Dashboard expected = makeDashboard("template", "title", "appName", "comp1", "comp2");

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

        when(collectorItemRepository.findOne(collItemId)).thenReturn(item);
        when(collectorRepository.findOne(collId)).thenReturn(collector);
        when(componentRepository.findOne(compId)).thenReturn(component);

        dashboardService.associateCollectorToComponent(compId, collItemIds);

        assertThat(component.getCollectorItems().get(CollectorType.Build), contains(item));
        assertThat(item.isEnabled(), is(true));

        verify(componentRepository).save(component);
        verify(collectorItemRepository).save(item);
    }

    @Test
    public void delete() {
        ObjectId id = ObjectId.get();
        Dashboard expected = makeDashboard("template", "title", "appName", "comp1", "comp2");
        when(dashboardRepository.findOne(id)).thenReturn(expected);

        List<Service> services = Arrays.asList(new Service());
        Service depService = new Service();
        depService.getDependedBy().add(id);
        when(serviceRepository.findByDashboardId(id)).thenReturn(services);
        when(serviceRepository.findByDependedBy(id)).thenReturn(Arrays.asList(depService));

        dashboardService.delete(id);

        verify(componentRepository).delete(expected.getApplication().getComponents());
        verify(serviceRepository).delete(services);
        verify(serviceRepository).save(depService);
        verify(dashboardRepository).delete(expected);

        assertThat(depService.getDependedBy(), not(contains(id)));
    }

    @Test
    public void addWidget() {
        Dashboard d = makeDashboard("template", "title", "appName","amit");
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
        Dashboard d = makeDashboard("template", "title", "appName","amit");
        Widget expected = new Widget();
        expected.setId(widgetId);
        d.getWidgets().add(expected);

        assertThat(dashboardService.getWidget(d, widgetId), is(expected));
    }

    @Test
    public void updateWidget() {
        ObjectId widgetId = ObjectId.get();
        Dashboard d = makeDashboard("template", "title", "appName","amit");
        d.getWidgets().add(makeWidget(widgetId, "existing"));
        Widget expected = makeWidget(widgetId, "updated");

        assertThat(dashboardService.updateWidget(d, expected), is(expected));
        assertThat(d.getWidgets(), contains(expected));
        assertThat(d.getWidgets().get(0).getName(), is(expected.getName()));

        verify(dashboardRepository).save(d);
    }

    private Dashboard makeDashboard(String template, String title, String appName, String owner,String... compNames) {
        Application app = new Application(appName);
        for (String compName : compNames) {
            app.addComponent(new Component(compName));
        }
        return new Dashboard(template, title, app, owner);
    }

    private Widget makeWidget(ObjectId id, String name) {
        Widget w = new Widget();
        w.setId(id);
        w.setName("updated");
        return w;
    }
}
