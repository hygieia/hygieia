package com.capitalone.dashboard.service;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.capitalone.dashboard.model.CollectorItem;
import com.capitalone.dashboard.model.Component;
import com.capitalone.dashboard.model.Dashboard;
import com.capitalone.dashboard.model.Collector;
import com.capitalone.dashboard.model.Application;
import com.capitalone.dashboard.model.Widget;
import com.capitalone.dashboard.model.Cmdb;
import com.capitalone.dashboard.model.CollectorType;
import com.capitalone.dashboard.model.Service;
import com.capitalone.dashboard.model.Owner;
import com.capitalone.dashboard.model.UserInfo;
import com.capitalone.dashboard.model.AuthType;
import com.capitalone.dashboard.model.DashboardType;
import com.capitalone.dashboard.model.ScoreDisplayType;

import org.bson.types.ObjectId;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.capitalone.dashboard.auth.exceptions.UserNotFoundException;
import com.capitalone.dashboard.misc.HygieiaException;
import com.capitalone.dashboard.repository.CollectorItemRepository;
import com.capitalone.dashboard.repository.CollectorRepository;
import com.capitalone.dashboard.repository.ComponentRepository;
import com.capitalone.dashboard.repository.CustomRepositoryQuery;
import com.capitalone.dashboard.repository.DashboardRepository;
import com.capitalone.dashboard.repository.ServiceRepository;
import com.capitalone.dashboard.repository.UserInfoRepository;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

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
    @Mock
    private CustomRepositoryQuery customRepositoryQuery;
    @Mock
    private UserInfoRepository userInfoRepository;
    @Mock
    private UserInfoServiceImpl userInfoServiceImpl;
    @Mock
    private CmdbService cmdbService;
    @Mock
    private Dashboard myDashboard;
    @Mock
    private ScoreDashboardService scoreDashboardService;

    @InjectMocks
    private DashboardServiceImpl dashboardService;

    private static final String configItemBusServName = "ASVTEST";
    private static final String configItemBusAppName = "BAPTEST";

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
        Dashboard expected = makeTeamDashboard("template", "title", "AppName", "",configItemBusServName,configItemBusAppName,"comp1");
        when(cmdbService.configurationItemByConfigurationItem(configItemBusServName)).thenReturn(getConfigItem(configItemBusServName));
        when(cmdbService.configurationItemByConfigurationItem(configItemBusAppName)).thenReturn(getConfigItem(configItemBusAppName));
        when(dashboardRepository.findOne(id)).thenReturn(expected);

        assertThat(dashboardService.get(id), is(expected));
    }

    @Test
    public void create() throws HygieiaException {
        Dashboard expected = makeTeamDashboard("template", "title", "appName", "",configItemBusServName,configItemBusAppName, "comp1","comp2");

        when(dashboardRepository.save(expected)).thenReturn(expected);
        when(scoreDashboardService.addScoreForDashboard(any())).thenReturn(null);
        assertThat(dashboardService.create(expected), is(expected));
        verify(componentRepository, times(1)).save(expected.getApplication().getComponents());
    }

    @Test
    public void createWithScoreEnabled() throws HygieiaException {
        Dashboard expected = makeTeamDashboard("template", "title", "appName", "",configItemBusServName,configItemBusAppName, "comp1","comp2");
        expected.setScoreEnabled(true);
        when(dashboardRepository.save(expected)).thenReturn(expected);
        when(scoreDashboardService.addScoreForDashboard(expected)).thenReturn(new CollectorItem());
        assertThat(dashboardService.create(expected), is(expected));
        verify(componentRepository, times(1)).save(expected.getApplication().getComponents());
    }

    @Test
    public void create_dup_name_dash() throws HygieiaException {

        Dashboard firstDash = makeTeamDashboard("template", "title", "appName", "johns",configItemBusServName,configItemBusAppName, "comp1", "comp2");
        when(dashboardRepository.save(firstDash)).thenReturn(firstDash);
        assertThat(dashboardService.create(firstDash), is(firstDash));
        verify(componentRepository, times(1)).save(firstDash.getApplication().getComponents());

        Dashboard secondDash = makeTeamDashboard("template", "title", "appName", "johns",configItemBusServName,configItemBusAppName, "comp1", "comp2");

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
    public void updateWithScoreEnabled() throws HygieiaException {
        Dashboard expected = makeTeamDashboard("template", "title", "appName", "",configItemBusServName,configItemBusAppName,"comp1", "comp2");
        expected.setScoreEnabled(true);
        when(dashboardRepository.save(expected)).thenReturn(expected);
        when(scoreDashboardService.editScoreForDashboard(expected)).thenReturn(new CollectorItem());
        assertThat(dashboardService.update(expected), is(expected));
    }

    @Test
    public void updateWithScoreDisabled() throws HygieiaException {
        Dashboard expected = makeTeamDashboard("template", "title", "appName", "",configItemBusServName,configItemBusAppName,"comp1", "comp2");
        expected.setScoreEnabled(false);
        when(dashboardRepository.save(expected)).thenReturn(expected);
        when(scoreDashboardService.editScoreForDashboard(expected)).thenReturn(new CollectorItem());
        assertThat(dashboardService.update(expected), is(expected));
    }

    @Test
    public void update() throws HygieiaException {
        Dashboard expected = makeTeamDashboard("template", "title", "appName", "",configItemBusServName,configItemBusAppName,"comp1", "comp2");

        when(dashboardRepository.save(expected)).thenReturn(expected);
        when(scoreDashboardService.editScoreForDashboard(any())).thenReturn(null);
        assertThat(dashboardService.update(expected), is(expected));
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
    public void associateCollectorToComponentWithDisabledCollectorItem() {
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

        verify(componentRepository).save(component);
        verify(collectorItemRepository).save(set);
    }

    @Test
    public void associateCollectorToComponent_Item_with_two_components() {
        ObjectId compId = ObjectId.get();
        ObjectId collId = ObjectId.get();
        ObjectId collItemId = ObjectId.get();
        List<ObjectId> collItemIds = Arrays.asList(collItemId);

        CollectorItem item = new CollectorItem();
        item.setCollectorId(collId);
        item.setEnabled(false);
        Collector collector = new Collector();
        collector.setCollectorType(CollectorType.Build);
        Component component1 = new Component();
        HashSet<CollectorItem> set = new HashSet<>();
        set.add(item);


        Component component2 = new Component();

        when(collectorItemRepository.findOne(collItemId)).thenReturn(item);
        when(collectorRepository.findOne(collId)).thenReturn(collector);
        when(componentRepository.findOne(compId)).thenReturn(component1);
        when(customRepositoryQuery.findComponents(collector, item)).thenReturn(Arrays.asList(component1, component2));

        dashboardService.associateCollectorToComponent(compId, collItemIds);

        assertThat(component1.getCollectorItems().get(CollectorType.Build), contains(item));
        assertThat(item.isEnabled(), is(true));
        verify(componentRepository).save(component1);
        verify(collectorItemRepository).save(set);
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
        item2.setEnabled(true);
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
    public void associateCollectorToComponent_switch_Item1_with_Item2_Multiple_Components() {
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
        Component component1 = new Component();
        component1.addCollectorItem(CollectorType.Build, item1);

        Component component2 = new Component();
        component1.addCollectorItem(CollectorType.Build, item1);

        HashSet<CollectorItem> set = new HashSet<>();
        set.add(item1);

        CollectorItem item2 = new CollectorItem();
        item2.setCollectorId(collId);
        item2.setId(collItemId2);
        item2.setEnabled(true);
        set.add(item2);


        when(collectorItemRepository.findOne(collItemId1)).thenReturn(item1);
        when(collectorItemRepository.findOne(collItemId2)).thenReturn(item2);
        when(collectorRepository.findOne(collId)).thenReturn(collector);
        when(componentRepository.findOne(compId)).thenReturn(component1);
        when(customRepositoryQuery.findComponents(collector, item1)).thenReturn(Arrays.asList(component1, component2));

        dashboardService.associateCollectorToComponent(compId, collItemIds);

        assertThat(component1.getCollectorItems().get(CollectorType.Build), contains(item2));
        assertThat(item1.isEnabled(), is(true));
        assertThat(item2.isEnabled(), is(true));

        verify(componentRepository).save(component1);
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
        Dashboard expected = makeTeamDashboard("template", "title", "appName",  "",configItemBusServName, configItemBusAppName,"comp1", "comp2");
        when(dashboardRepository.findOne(id)).thenReturn(expected);

        List<Service> services = Arrays.asList(new Service());
        Service depService = new Service();
        depService.getDependedBy().add(id);
        when(serviceRepository.findByDashboardId(id)).thenReturn(services);
        when(serviceRepository.findByDependedBy(id)).thenReturn(Arrays.asList(depService));
        when(collectorRepository.findByCollectorType(CollectorType.Product)).thenReturn(new ArrayList<Collector>());
        when(scoreDashboardService.disableScoreForDashboard(any())).thenReturn(null);
        dashboardService.delete(id);

        verify(componentRepository).delete(expected.getApplication().getComponents());
        verify(serviceRepository).delete(services);
        verify(serviceRepository).save(depService);
        verify(dashboardRepository).delete(expected);

        assertThat(depService.getDependedBy(), not(contains(id)));
    }

    @Test
    public void deleteTestCollectorItemDisable() {
        ObjectId id = ObjectId.get();

        ObjectId collId = ObjectId.get();

        ObjectId collItemId1 = ObjectId.get();
        ObjectId collItemId2 = ObjectId.get();

        CollectorItem item1 = new CollectorItem();
        item1.setCollectorId(collId);
        item1.setId(collItemId1);
        item1.setEnabled(true);

        CollectorItem item2 = new CollectorItem();
        item2.setCollectorId(collId);
        item2.setId(collItemId2);
        item2.setEnabled(true);

        Component component = new Component();
        component.addCollectorItem(CollectorType.Build, item1);
        component.addCollectorItem(CollectorType.Build, item2);


        Dashboard expected = makeTeamDashboard("template", "title", "appName",  "",configItemBusServName, configItemBusAppName,"comp1");
        expected.getApplication().getComponents().get(0).addCollectorItem(CollectorType.Build, item1);
        expected.getApplication().getComponents().get(0).addCollectorItem(CollectorType.Build, item2);
        when(dashboardRepository.findOne(id)).thenReturn(expected);
        when(customRepositoryQuery.findComponents(collId, CollectorType.Build, item1)).thenReturn(Arrays.asList());
        when(customRepositoryQuery.findComponents(collId, CollectorType.Build, item2)).thenReturn(Arrays.asList());
        when(scoreDashboardService.disableScoreForDashboard(any())).thenReturn(null);
        dashboardService.delete(id);

        verify(componentRepository).delete(expected.getApplication().getComponents());
        verify(dashboardRepository).delete(expected);
        assertThat(item1.isEnabled(), is(false));
        assertThat(item2.isEnabled(),is(false));
        verify(collectorItemRepository).save(item1);
        verify(collectorItemRepository).save(item2);
    }


    @Test
    public void deleteTestCollectorOneItemDisable() {
        ObjectId id = ObjectId.get();

        ObjectId collId = ObjectId.get();

        ObjectId collItemId1 = ObjectId.get();
        ObjectId collItemId2 = ObjectId.get();

        CollectorItem item1 = new CollectorItem();
        item1.setCollectorId(collId);
        item1.setId(collItemId1);
        item1.setEnabled(true);

        CollectorItem item2 = new CollectorItem();
        item2.setCollectorId(collId);
        item2.setId(collItemId2);
        item2.setEnabled(true);

        Component component = new Component();
        component.addCollectorItem(CollectorType.Build, item1);

        Dashboard expected = makeTeamDashboard("template", "title", "appName",  "",configItemBusServName, configItemBusAppName,"comp1");
        expected.getApplication().getComponents().get(0).addCollectorItem(CollectorType.Build, item1);
        expected.getApplication().getComponents().get(0).addCollectorItem(CollectorType.Build, item2);
        when(dashboardRepository.findOne(id)).thenReturn(expected);
        when(customRepositoryQuery.findComponents(collId, CollectorType.Build, item1)).thenReturn(Arrays.asList(component));
        when(customRepositoryQuery.findComponents(collId, CollectorType.Build, item2)).thenReturn(Arrays.asList());
        when(scoreDashboardService.disableScoreForDashboard(any())).thenReturn(null);

        dashboardService.delete(id);

        verify(componentRepository).delete(expected.getApplication().getComponents());
        verify(dashboardRepository).delete(expected);
        assertThat(item1.isEnabled(), is(true));
        assertThat(item2.isEnabled(),is(false));
        verify(collectorItemRepository).save(item2);
    }

    @Test
    public void deleteTestCollectorNothingDisabled() {
        ObjectId id = ObjectId.get();

        ObjectId collId = ObjectId.get();

        ObjectId collItemId1 = ObjectId.get();
        ObjectId collItemId2 = ObjectId.get();

        CollectorItem item1 = new CollectorItem();
        item1.setCollectorId(collId);
        item1.setId(collItemId1);
        item1.setEnabled(true);

        CollectorItem item2 = new CollectorItem();
        item2.setCollectorId(collId);
        item2.setId(collItemId2);
        item2.setEnabled(true);

        Component component = new Component();
        component.addCollectorItem(CollectorType.Build, item1);
        component.addCollectorItem(CollectorType.Build, item2);

        Dashboard expected = makeTeamDashboard("template", "title", "appName",  "",configItemBusServName, configItemBusAppName,"comp1");
        expected.getApplication().getComponents().get(0).addCollectorItem(CollectorType.Build, item1);
        expected.getApplication().getComponents().get(0).addCollectorItem(CollectorType.Build, item2);
        when(dashboardRepository.findOne(id)).thenReturn(expected);
        when(customRepositoryQuery.findComponents(collId, CollectorType.Build, item1)).thenReturn(Arrays.asList(component));
        when(customRepositoryQuery.findComponents(collId, CollectorType.Build, item2)).thenReturn(Arrays.asList(component));
        when(scoreDashboardService.disableScoreForDashboard(any())).thenReturn(null);

        dashboardService.delete(id);

        verify(componentRepository).delete(expected.getApplication().getComponents());
        verify(dashboardRepository).delete(expected);
        assertThat(item1.isEnabled(), is(true));
        assertThat(item2.isEnabled(),is(true));
        verify(collectorItemRepository,never()).save(item1);
        verify(collectorItemRepository,never()).save(item2);
    }

    @Test
    public void addWidget() {
        Dashboard d = makeTeamDashboard("template", "title", "appName", "amit", configItemBusServName, configItemBusAppName);
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
        Dashboard d = makeTeamDashboard("template", "title", "appName", "amit", configItemBusServName, configItemBusAppName);
        Widget expected = new Widget();
        expected.setId(widgetId);
        d.getWidgets().add(expected);

        assertThat(dashboardService.getWidget(d, widgetId), is(expected));
    }

    @Test
    public void updateWidget() {
        ObjectId widgetId = ObjectId.get();
        Dashboard d = makeTeamDashboard("template", "title", "appName", "amit",configItemBusServName, configItemBusAppName);
        d.getWidgets().add(makeWidget(widgetId, "existing"));
        Widget expected = makeWidget(widgetId, "updated");

        assertThat(dashboardService.updateWidget(d, expected), is(expected));
        assertThat(d.getWidgets(), contains(expected));
        assertThat(d.getWidgets().get(0).getName(), is(expected.getName()));

        verify(dashboardRepository).save(d);
    }

    @Test
    public void deleteWidget() {
        ObjectId widgetId = ObjectId.get();
        ObjectId compId = ObjectId.get();
        Dashboard d = makeTeamDashboard("template", "title", "appName", "amit",configItemBusServName, configItemBusAppName);
        d.getWidgets().add(makeWidget(widgetId, "existing"));
        Widget expected = makeWidget(widgetId, "updated");

        Component c = new Component();
        c.setId(compId);

        when(componentRepository.findOne(compId)).thenReturn(c);
        dashboardService.deleteWidget(d, expected,compId);
        verify(componentRepository, times(1)).save(any(Component.class));
    }

    
    @Test
    public void updateOwners_empty_owner_set() {
    	Iterable<Owner> owners = Lists.newArrayList();
        List<String> activeWidgets = new ArrayList<>();
    	Dashboard dashboard = new Dashboard("template", "title", new Application("Application"), null, DashboardType.Team, configItemBusServName, configItemBusAppName, activeWidgets, false, ScoreDisplayType.HEADER);

    	when(dashboardRepository.findOne(dashboard.getId())).thenReturn(dashboard);
    	when(dashboardRepository.save(dashboard)).thenReturn(dashboard);
    	
    	Iterable<Owner> result = dashboardService.updateOwners(dashboard.getId(), owners);
    	assertTrue(Iterables.size(result) == 0);
    	
    	when(dashboardRepository.findOne(dashboard.getId())).thenReturn(dashboard);
    	when(dashboardRepository.save(dashboard)).thenReturn(dashboard);
    }
    
    @Test(expected = UserNotFoundException.class)
    public void updateOwners_user_not_found() {
    	Owner existingOwner = new Owner("existing", AuthType.LDAP);
    	Owner nonExistingOwner = new Owner("nonExisting", AuthType.STANDARD);
    	
    	when(userInfoRepository.findByUsernameAndAuthType("existing", AuthType.LDAP)).thenReturn(new UserInfo());
    	when(userInfoRepository.findByUsernameAndAuthType("nonExisting", AuthType.STANDARD)).thenReturn(null);
    	ObjectId dashboardId = ObjectId.get();
    	Iterable<Owner> owners = Lists.newArrayList(existingOwner, nonExistingOwner);
    	dashboardService.updateOwners(dashboardId, owners);
    	
    	verify(userInfoRepository, times(2)).findByUsernameAndAuthType(any(String.class), any(AuthType.class));
    }
    
    @Test
    public void updateOwners() {
    	Owner existingOwner = new Owner("existing", AuthType.LDAP);
    	UserInfo existingInfo = new UserInfo();
    	existingInfo.setUsername("existing");
    	existingInfo.setAuthType(AuthType.LDAP);
        List<String> activeWidgets = new ArrayList<>();
        List<Owner> existingOwners = new ArrayList<>();
        existingOwners.add(existingOwner);
    	Dashboard dashboard = new Dashboard("template", "title", new Application("Application"), existingOwners, DashboardType.Team,configItemBusServName,configItemBusAppName, activeWidgets, false, ScoreDisplayType.HEADER);
    	
        when(userInfoServiceImpl.isUserValid("existing", AuthType.LDAP)).thenReturn(true);
    	when(dashboardRepository.findOne(dashboard.getId())).thenReturn(dashboard);
    	when(dashboardRepository.save(dashboard)).thenReturn(dashboard);
    	
        Iterable<Owner> owners = Lists.newArrayList(existingOwner);
    	List<Owner> result = Lists.newArrayList(dashboardService.updateOwners(dashboard.getId(), owners));
    	
    	assertNotNull(result);
    	assertEquals(1, result.size());
    	assertEquals(existingOwner, result.get(0));
    	
    	verify(dashboardRepository).findOne(eq(dashboard.getId()));
    	verify(dashboardRepository).save(eq(dashboard));
    }
    @Test
    public void updateDashboardBusinessItems() throws HygieiaException{

        ObjectId id = ObjectId.get();

        String newServiceName = "ASVTEST123";
        String newAppName = "BAPTEST123";
        Cmdb serviceUpdated = getConfigItem(newServiceName);
        serviceUpdated.setConfigurationItem(newServiceName);
        Cmdb appUpdated = getConfigItem(newAppName);
        appUpdated.setConfigurationItem(newAppName);

        Dashboard myDashboard = makeTeamDashboard("template", "title", "appName", "amit",configItemBusServName, configItemBusAppName, "comp1", "comp2");
        Dashboard dashboardRequest = makeTeamDashboard("template", "title", "appName", "amit",newServiceName, newAppName, "comp1", "comp2");
        dashboardRequest.setConfigurationItemBusServName(newServiceName);
        dashboardRequest.setConfigurationItemBusAppName(newAppName);

        when(cmdbService.configurationItemByConfigurationItem(configItemBusServName)).thenReturn(getConfigItem(configItemBusServName));
        when(cmdbService.configurationItemByConfigurationItem(configItemBusAppName)).thenReturn(getConfigItem(configItemBusAppName));

        when(dashboardRepository.findOne(id)).thenReturn(myDashboard);

        when(cmdbService.configurationItemByConfigurationItem(newServiceName)).thenReturn(serviceUpdated);
        when(cmdbService.configurationItemByConfigurationItem(newAppName)).thenReturn(appUpdated);
        when(scoreDashboardService.addScoreForDashboard(any())).thenReturn(null);
        when(dashboardRepository.save(myDashboard)).thenReturn(myDashboard);
        assertNotNull(dashboardService.updateDashboardBusinessItems(id,dashboardRequest));
    }

    @Test
    public void updateDashboardWidgets() throws HygieiaException{
        ObjectId id = ObjectId.get();
        Dashboard myDashboard = makeTeamDashboard("template", "title", "appName", "amit",null, null, "comp1", "comp2");
        Dashboard dashboardRequest = makeTeamDashboard("template", "title", "appName", "amit",null, null, "comp1", "comp2");
        when(dashboardRepository.findOne(id)).thenReturn(myDashboard);
        when(scoreDashboardService.addScoreForDashboard(any())).thenReturn(null);
        when(dashboardRepository.save(myDashboard)).thenReturn(myDashboard);
        assertNotNull(dashboardService.updateDashboardWidgets(id,dashboardRequest));
    }


    @Test
    public void getDashboardByTitleWithFilter() throws HygieiaException{
        Dashboard myDashboard = makeTeamDashboard("template", "title", "appName", "amit",null, null, "comp1", "comp2");
        Page<Dashboard> pagedDashboards = new PageImpl<Dashboard>(Stream.of(myDashboard).collect(Collectors.toList()));
        when(dashboardRepository.findAllByTitleContainingIgnoreCase(any(String.class),any(Pageable.class) )).thenReturn(pagedDashboards);
        assertNotNull(dashboardService.getDashboardByTitleWithFilter("title", "", null));
    }

    @Test
    public void getAllDashboardsByTitleCount() throws HygieiaException{
        Dashboard myDashboard = makeTeamDashboard("template", "title", "appName", "amit",null, null, "comp1", "comp2");
        Page<Dashboard> pagedDashboards = new PageImpl<Dashboard>(Stream.of(myDashboard).collect(Collectors.toList()));
        when(dashboardRepository.findAllByTitleContainingIgnoreCase(any(String.class))).thenReturn(Stream.of(myDashboard).collect(Collectors.toList()));
        assertEquals(new Integer(dashboardService.getAllDashboardsByTitleCount("title","")),new Integer(1));
    }

    @Test
    public void updateScoreSettings() {
        Dashboard myDashboard = makeTeamDashboard("template", "title", "appName", "amit",null, null, "comp1", "comp2");
        myDashboard.setScoreEnabled(false);
        myDashboard.setId(ObjectId.get());
        when(dashboardRepository.findOne(myDashboard.getId())).thenReturn(myDashboard);

        Dashboard myDashboardResponse = makeTeamDashboard("template", "title", "appName", "amit",null, null, "comp1", "comp2");
        myDashboardResponse.setScoreEnabled(true);
        myDashboardResponse.setId(myDashboard.getId());
        myDashboardResponse.setScoreDisplay(ScoreDisplayType.HEADER);

        when(dashboardRepository.save(any(Dashboard.class))).thenReturn(myDashboardResponse);
        when(scoreDashboardService.editScoreForDashboard(myDashboardResponse)).thenReturn(new CollectorItem());
        assertNotNull(dashboardService.updateScoreSettings(myDashboard.getId(), true, ScoreDisplayType.HEADER));
    }

    @Test
    public void compareMapsTest() {
        Map<String, Object> map1 = new HashMap<>();
        map1.put("featureTool", "Jira");
        map1.put("teamId", "16909");

        JSONObject jsonObject1 = new JSONObject();
        jsonObject1.put("kanban", false);
        jsonObject1.put("scrum", true);
        map1.put("showStatus", jsonObject1);

        JSONArray jsonArray1 = new JSONArray();
        map1.put("valueArray", jsonArray1);
        jsonArray1.add("value1");
        jsonArray1.add("1");
        jsonArray1.add("value2");

        Map<String, Object> map2 = new HashMap<>();
        map2.put("featureTool", "Jira");
        map2.put("teamId", "16909");

        JSONObject jsonObject2 = new JSONObject();
        jsonObject2.put("kanban", false);
        jsonObject2.put("scrum", true);
        map2.put("showStatus", jsonObject2);

        JSONArray jsonArray2 = new JSONArray();
        map2.put("valueArray", jsonArray2);
        jsonArray2.add("value1");
        jsonArray2.add("1");
        jsonArray2.add("value2");

        boolean result = dashboardService.compareMaps(map1, map2);
        Assert.assertFalse(result);

        jsonArray1 = new JSONArray();
        map1.put("valueArray", jsonArray1);
        jsonArray1.add("value3");
        jsonArray1.add("1");
        jsonArray1.add("value2");

        result = dashboardService.compareMaps(map1, map2);
        Assert.assertTrue(result);
    }

    private Dashboard makeTeamDashboard(String template, String title, String appName, String owner,String configItemBusServName,String configItemBusAppName, String... compNames) {
        Application app = new Application(appName);
        for (String compName : compNames) {
            app.addComponent(new Component(compName));
        }
        List<String> activeWidgets = new ArrayList<>();
        List<Owner> owners = new ArrayList<>();
        owners.add(new Owner(owner, AuthType.STANDARD));
        return new Dashboard(template, title, app, owners, DashboardType.Team, configItemBusServName, configItemBusAppName, activeWidgets, false, ScoreDisplayType.HEADER);
    }

    private Widget makeWidget(ObjectId id, String name) {
        Widget w = new Widget();
        w.setId(id);
        w.setName("updated");
        return w;
    }
    private Cmdb getConfigItem(String name){
        Cmdb cmdb1 = new Cmdb();
        cmdb1.setId(new ObjectId());
        cmdb1.setConfigurationItem(name);
        cmdb1.setValidConfigItem(true);
        return cmdb1;
    }

}
