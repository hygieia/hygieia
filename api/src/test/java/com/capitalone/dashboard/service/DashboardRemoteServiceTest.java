package com.capitalone.dashboard.service;

import com.capitalone.dashboard.misc.HygieiaException;
import com.capitalone.dashboard.model.Application;
import com.capitalone.dashboard.model.AuthType;
import com.capitalone.dashboard.model.Collector;
import com.capitalone.dashboard.model.CollectorItem;
import com.capitalone.dashboard.model.CollectorType;
import com.capitalone.dashboard.model.Component;
import com.capitalone.dashboard.model.Dashboard;
import com.capitalone.dashboard.model.DashboardType;
import com.capitalone.dashboard.model.Owner;
import com.capitalone.dashboard.repository.CollectorRepository;
import com.capitalone.dashboard.repository.DashboardRepository;
import com.capitalone.dashboard.request.DashboardRemoteRequest;
import org.bson.types.ObjectId;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.capitalone.dashboard.fixture.DashboardFixture.makeDashboardRemoteRequest;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DashboardRemoteServiceTest {

    @Mock
    private DashboardRepository dashboardRepository;
    @Mock
    private CollectorRepository collectorRepository;
    @Mock
    private UserInfoService userInfoService;
    @Mock
    private DashboardService dashboardService;
    @Mock
    private CollectorService collectorService;

    @InjectMocks
    private DashboardRemoteServiceImpl dashboardRemoteService;

    @Test
    public void remoteCreate() throws HygieiaException {
        ObjectId configItemBusServId = ObjectId.get();
        ObjectId configItemBusAppId = ObjectId.get();
        Dashboard expected = makeTeamDashboard("template", "dashboardtitle", "appName", "someuser",configItemBusServId,configItemBusAppId, "comp1","comp2");
        ObjectId objectId = ObjectId.get();
        expected.setId(objectId);
        DashboardRemoteRequest request = makeDashboardRemoteRequest("template", "dashboardtitle", "appName", "comp", "someuser", null, "team", configItemBusServId, configItemBusAppId);

        when(userInfoService.isUserValid(request.getMetaData().getOwner().getUsername(), request.getMetaData().getOwner().getAuthType())).thenReturn(true);
        when(dashboardRepository.findByTitle(request.getMetaData().getTitle())).thenReturn(new ArrayList<Dashboard>());
        when(dashboardService.create(Matchers.any(Dashboard.class))).thenReturn(expected);
        when(dashboardService.get(objectId)).thenReturn(expected);

        assertThat(dashboardRemoteService.remoteCreate(request, false), is(expected));
    }

    @Test
    public void remoteCreateInvalidUser() throws HygieiaException {
        ObjectId configItemBusServId = ObjectId.get();
        ObjectId configItemBusAppId = ObjectId.get();
        Dashboard expected = makeTeamDashboard("template", "dashboardtitle", "appName", "invaliduser",configItemBusServId,configItemBusAppId, "comp1","comp2");
        ObjectId objectId = ObjectId.get();
        expected.setId(objectId);
        DashboardRemoteRequest request = makeDashboardRemoteRequest("template", "dashboardtitle", "appName", "comp", "invaliduser", null, "team", configItemBusServId, configItemBusAppId);

        when(userInfoService.isUserValid(request.getMetaData().getOwner().getUsername(), request.getMetaData().getOwner().getAuthType())).thenReturn(false);
        when(dashboardRepository.findByTitle(request.getMetaData().getTitle())).thenReturn(new ArrayList<Dashboard>());
        when(dashboardService.create(Matchers.any(Dashboard.class))).thenReturn(expected);
        when(dashboardService.get(objectId)).thenReturn(expected);

        Throwable t = new Throwable();
        RuntimeException excep = new RuntimeException("Invalid owner information or authentication type. Owner first needs to sign up in Hygieia", t);

        try {
            dashboardRemoteService.remoteCreate(request, false);
            fail("Should throw RuntimeException");
        } catch(Exception e) {
            assertEquals(excep.getMessage(), e.getMessage());
        }
    }

    @Test
    public void remoteCreateDuplicateDashoard() throws HygieiaException {
        ObjectId configItemBusServId = ObjectId.get();
        ObjectId configItemBusAppId = ObjectId.get();
        Dashboard expected = makeTeamDashboard("template", "dashboardtitle", "appName", "validuser",configItemBusServId,configItemBusAppId, "comp1","comp2");
        ObjectId objectId = ObjectId.get();
        expected.setId(objectId);
        DashboardRemoteRequest request = makeDashboardRemoteRequest("template", "dashboardtitle", "appName", "comp", "validuser", null, "team", configItemBusServId, configItemBusAppId);

        when(userInfoService.isUserValid(request.getMetaData().getOwner().getUsername(), request.getMetaData().getOwner().getAuthType())).thenReturn(false);

        List<Dashboard> existingDashboards = new ArrayList<Dashboard>();
        existingDashboards.add(expected);

        when(userInfoService.isUserValid(request.getMetaData().getOwner().getUsername(), request.getMetaData().getOwner().getAuthType())).thenReturn(true);
        when(dashboardRepository.findByTitle(request.getMetaData().getTitle())).thenReturn(existingDashboards);
        when(dashboardService.create(Matchers.any(Dashboard.class))).thenReturn(expected);
        when(dashboardService.get(objectId)).thenReturn(expected);

        Throwable t = new Throwable();
        RuntimeException excep = new RuntimeException("Dashboard dashboardtitle (id =" + expected.getId() + ") already exists", t);

        try {
            dashboardRemoteService.remoteCreate(request, false);
            fail("Should throw RuntimeException");
        } catch(Exception e) {
            assertEquals(excep.getMessage(), e.getMessage());
        }
    }

    @Test
    public void remoteCreateWithInvalidCollector() throws HygieiaException {
        ObjectId configItemBusServId = ObjectId.get();
        ObjectId configItemBusAppId = ObjectId.get();
        Dashboard expected = makeTeamDashboard("template", "dashboardtitle", "appName", "someuser",configItemBusServId,configItemBusAppId, "comp1","comp2");
        ObjectId objectId = ObjectId.get();
        expected.setId(objectId);
        DashboardRemoteRequest request = makeDashboardRemoteRequest("template", "dashboardtitle", "appName", "comp", "someuser", null, "team", configItemBusServId, configItemBusAppId);
        List<DashboardRemoteRequest.CodeRepoEntry> entries = new ArrayList<DashboardRemoteRequest.CodeRepoEntry>();
        DashboardRemoteRequest.CodeRepoEntry invalidSCM = new DashboardRemoteRequest.CodeRepoEntry();
        invalidSCM.setToolName("Clearcase");
        entries.add(invalidSCM);
        request.setCodeRepoEntries(entries);

        when(userInfoService.isUserValid(request.getMetaData().getOwner().getUsername(), request.getMetaData().getOwner().getAuthType())).thenReturn(true);
        when(dashboardRepository.findByTitle(request.getMetaData().getTitle())).thenReturn(new ArrayList<Dashboard>());
        when(dashboardService.create(Matchers.any(Dashboard.class))).thenReturn(expected);
        when(dashboardService.get(objectId)).thenReturn(expected);

        Throwable t = new Throwable();
        RuntimeException excep = new RuntimeException(invalidSCM.getToolName() + " collector is not available.", t);

        try {
            dashboardRemoteService.remoteCreate(request, false);
            fail("Should throw RuntimeException");
        } catch(Exception e) {
            assertEquals(excep.getMessage(), e.getMessage());
        }
    }

    @Test
    public void remoteCreateSCM() throws HygieiaException {
        ObjectId configItemBusServId = ObjectId.get();
        ObjectId configItemBusAppId = ObjectId.get();
        Dashboard expected = makeTeamDashboard("template", "dashboardtitle", "appName", "someuser",configItemBusServId,configItemBusAppId, "comp1","comp2");
        ObjectId objectId = ObjectId.get();
        expected.setId(objectId);
        DashboardRemoteRequest request = makeDashboardRemoteRequest("template", "dashboardtitle", "appName", "comp", "someuser", null, "team", configItemBusServId, configItemBusAppId);
        List<DashboardRemoteRequest.CodeRepoEntry> entries = new ArrayList<DashboardRemoteRequest.CodeRepoEntry>();
        DashboardRemoteRequest.CodeRepoEntry validSCM = new DashboardRemoteRequest.CodeRepoEntry();
        validSCM.setToolName("GitHub");
        Map options = new HashMap();
        options.put("url", "http://git.test.com");
        options.put("branch", "master");
        validSCM.setOptions(options);
        entries.add(validSCM);
        request.setCodeRepoEntries(entries);

        when(userInfoService.isUserValid(request.getMetaData().getOwner().getUsername(), request.getMetaData().getOwner().getAuthType())).thenReturn(true);
        when(dashboardRepository.findByTitle(request.getMetaData().getTitle())).thenReturn(new ArrayList<Dashboard>());
        when(dashboardService.create(Matchers.any(Dashboard.class))).thenReturn(expected);
        when(dashboardService.get(objectId)).thenReturn(expected);

        List<Collector> collectors = new ArrayList<Collector>();
        Collector githubCollector = makeCollector("GitHub", CollectorType.SCM);
        Map uniqueFields = new HashMap();
        uniqueFields.put("branch", "");
        uniqueFields.put("url", "");
        githubCollector.setUniqueFields(uniqueFields);

        Map allFields = new HashMap();
        allFields.put("branch", "");
        allFields.put("url", "");
        allFields.put("userID", "");
        allFields.put("password", "");
        allFields.put("lastUpdate", "");
        githubCollector.setAllFields(allFields);

        collectors.add(githubCollector);

        when( collectorRepository.findByCollectorTypeAndName(validSCM.getType(), validSCM.getToolName()) ).thenReturn(collectors);

        CollectorItem item = makeCollectorItem();

        when(  collectorService.createCollectorItemSelectOptions(Matchers.any(CollectorItem.class), Matchers.any(Map.class), Matchers.any(Map.class) ) ).thenReturn(item);

        Component component = new Component();
        component.addCollectorItem(CollectorType.SCM, item);

        assertThat(dashboardRemoteService.remoteCreate(request, false), is(expected));
    }

    @Test
    public void remoteCreateBuild() throws HygieiaException {
        ObjectId configItemBusServId = ObjectId.get();
        ObjectId configItemBusAppId = ObjectId.get();
        Dashboard expected = makeTeamDashboard("template", "dashboardtitle", "appName", "someuser",configItemBusServId,configItemBusAppId, "comp1","comp2");
        ObjectId objectId = ObjectId.get();
        expected.setId(objectId);
        DashboardRemoteRequest request = makeDashboardRemoteRequest("template", "dashboardtitle", "appName", "comp", "someuser", null, "team", configItemBusServId, configItemBusAppId);
        List<DashboardRemoteRequest.BuildEntry> entries = new ArrayList<DashboardRemoteRequest.BuildEntry>();
        DashboardRemoteRequest.BuildEntry validBuild = new DashboardRemoteRequest.BuildEntry();
        validBuild.setToolName("Hudson");
        Map options = new HashMap();
        options.put("jobName", "MyBuildJob");
        options.put("jobUrl", "http://jenkins.com/MyBuildJob");
        options.put("instanceUrl", "http://jenkins.com");
        validBuild.setOptions(options);
        entries.add(validBuild);
        request.setBuildEntries(entries);

        when(userInfoService.isUserValid(request.getMetaData().getOwner().getUsername(), request.getMetaData().getOwner().getAuthType())).thenReturn(true);
        when(dashboardRepository.findByTitle(request.getMetaData().getTitle())).thenReturn(new ArrayList<Dashboard>());
        when(dashboardService.create(Matchers.any(Dashboard.class))).thenReturn(expected);
        when(dashboardService.get(objectId)).thenReturn(expected);

        List<Collector> collectors = new ArrayList<Collector>();
        Collector hudsonCollector = makeCollector("Hudson", CollectorType.Build);
        Map uniqueFields = new HashMap();
        uniqueFields.put("jobName", "");
        uniqueFields.put("jobUrl", "");
        uniqueFields.put("instanceUrl", "");
        hudsonCollector.setUniqueFields(uniqueFields);

        Map allFields = new HashMap();
        allFields.put("jobName", "");
        allFields.put("jobUrl", "");
        allFields.put("instanceUrl", "");
        hudsonCollector.setAllFields(allFields);

        collectors.add(hudsonCollector);

        when( collectorRepository.findByCollectorTypeAndName(validBuild.getType(), validBuild.getToolName()) ).thenReturn(collectors);

        CollectorItem item = makeCollectorItem();

        when(  collectorService.createCollectorItemSelectOptions(Matchers.any(CollectorItem.class), Matchers.any(Map.class), Matchers.any(Map.class) ) ).thenReturn(item);

        Component component = new Component();
        component.addCollectorItem(CollectorType.Build, item);

        assertThat(dashboardRemoteService.remoteCreate(request, false), is(expected));
    }

    private Dashboard makeTeamDashboard(String template, String title, String appName, String owner,ObjectId configItemBusServId,ObjectId configItemBusAppId, String... compNames) {
        Application app = new Application(appName);
        for (String compName : compNames) {
            app.addComponent(new Component(compName));
        }
        List<String> activeWidgets = new ArrayList<>();
        return new Dashboard(template, title, app, new Owner(owner, AuthType.STANDARD), DashboardType.Team, configItemBusServId, configItemBusAppId,activeWidgets);
    }

    private Collector makeCollector(String name, CollectorType type) {
        Collector collector = new Collector();
        collector.setId(ObjectId.get());
        collector.setName(name);
        collector.setCollectorType(type);
        collector.setEnabled(true);
        collector.setOnline(true);
        collector.setLastExecuted(System.currentTimeMillis());
        return collector;
    }

    private CollectorItem makeCollectorItem() {
        CollectorItem item = new CollectorItem();
        item.setCollectorId(new ObjectId());
        item.setId(new ObjectId());
        item.setEnabled(true);
        return item;
    }
}
