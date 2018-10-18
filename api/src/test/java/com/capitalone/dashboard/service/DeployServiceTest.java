package com.capitalone.dashboard.service;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.bson.types.ObjectId;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import com.capitalone.dashboard.misc.HygieiaException;
import com.capitalone.dashboard.model.Collector;
import com.capitalone.dashboard.model.CollectorItem;
import com.capitalone.dashboard.model.CollectorType;
import com.capitalone.dashboard.model.Component;
import com.capitalone.dashboard.model.DataResponse;
import com.capitalone.dashboard.model.EnvironmentComponent;
import com.capitalone.dashboard.model.EnvironmentStatus;
import com.capitalone.dashboard.model.deploy.DeployableUnit;
import com.capitalone.dashboard.model.deploy.Environment;
import com.capitalone.dashboard.model.deploy.Server;
import com.capitalone.dashboard.repository.CollectorItemRepository;
import com.capitalone.dashboard.repository.CollectorRepository;
import com.capitalone.dashboard.repository.ComponentRepository;
import com.capitalone.dashboard.repository.EnvironmentComponentRepository;
import com.capitalone.dashboard.repository.EnvironmentStatusRepository;
import com.capitalone.dashboard.request.DeployDataCreateRequest;

@RunWith(MockitoJUnitRunner.class)
public class DeployServiceTest {

    @Mock CollectorService collectorService;
    @Mock CollectorItemRepository collectorItemRepository;
    @Mock ComponentRepository componentRepository;
    @Mock EnvironmentComponentRepository environmentComponentRepository;
    @Mock EnvironmentStatusRepository environmentStatusRepository;
    @Mock private CollectorRepository collectorRepository;
    @InjectMocks DeployServiceImpl deployService;

    @Test
    public void getDeployStatus() {
        ObjectId compId = ObjectId.get();
        Component component = new Component();
        CollectorItem item = new CollectorItem();
        item.setId(ObjectId.get());
        item.setCollectorId(ObjectId.get());
        component.getCollectorItems().put(CollectorType.Deployment, Collections.singletonList(item));
        when(componentRepository.findOne(compId)).thenReturn(component);
        when(collectorRepository.findOne(item.getCollectorId())).thenReturn(new Collector());

        EnvironmentComponent c_qa_api = makeEnvComponent("QA", "API", "1.1", true);
        EnvironmentComponent c_qa_ui = makeEnvComponent("QA", "UI", "1.1", true);
        EnvironmentComponent c_prod_api = makeEnvComponent("PROD", "API", "1.0", true);
        EnvironmentComponent c_prod_ui = makeEnvComponent("PROD", "UI", "1.0", true);
        when(environmentComponentRepository.findByCollectorItemId(item.getId()))
                .thenReturn(Arrays.asList(c_qa_api, c_qa_ui, c_prod_api, c_prod_ui));

        EnvironmentStatus s_qa_api_s1 = makeEnvironmentStatus("QA", "API", "s1", true);
        EnvironmentStatus s_qa_api_s2 = makeEnvironmentStatus("QA", "API", "s2", true);
        EnvironmentStatus s_qa_ui_s3 = makeEnvironmentStatus("QA", "UI", "s3", true);
        EnvironmentStatus s_qa_ui_s4 = makeEnvironmentStatus("QA", "UI", "s4", true);
        EnvironmentStatus s_prod_api_s5 = makeEnvironmentStatus("PROD", "API", "s5", true);
        EnvironmentStatus s_prod_api_s6 = makeEnvironmentStatus("PROD", "API", "s6", true);
        EnvironmentStatus s_prod_ui_s7 = makeEnvironmentStatus("PROD", "UI", "s7", true);
        EnvironmentStatus s_prod_ui_s8 = makeEnvironmentStatus("PROD", "UI", "s8", true);
        when(environmentStatusRepository.findByCollectorItemId(item.getId()))
                .thenReturn(Arrays.asList(
                        s_qa_api_s1, s_qa_api_s2, s_qa_ui_s3, s_qa_ui_s4,
                        s_prod_api_s5, s_prod_api_s6, s_prod_ui_s7, s_prod_ui_s8
                ));

        DataResponse<List<Environment>> result = deployService.getDeployStatus(compId);

        Iterator<Environment> envIt;
        Iterator<DeployableUnit> unitIt;
        Iterator<Server> serverIt;
        Environment env;
        DeployableUnit unit;
        Server server;

        assertThat(result.getResult(), hasSize(2));
        envIt = result.getResult().iterator();

        env = envIt.next();
        assertThat(env.getName(), is("QA"));
        assertThat(env.getUnits(), hasSize(2));
        unitIt = env.getUnits().iterator();
        unit = unitIt.next();
        assertThat(unit.getName(), is("API"));
        assertThat(unit.getVersion(), is("1.1"));
        assertThat(unit.getServers(), hasSize(2));
        serverIt = unit.getServers().iterator();
        server = serverIt.next();
        assertThat(server.getName(), is("s1"));
        assertThat(server.isOnline(), is(true));
        server = serverIt.next();
        assertThat(server.getName(), is("s2"));
        assertThat(server.isOnline(), is(true));

        unit = unitIt.next();
        assertThat(unit.getName(), is("UI"));
        assertThat(unit.getVersion(), is("1.1"));
        assertThat(unit.getServers(), hasSize(2));
        serverIt = unit.getServers().iterator();
        server = serverIt.next();
        assertThat(server.getName(), is("s3"));
        assertThat(server.isOnline(), is(true));
        server = serverIt.next();
        assertThat(server.getName(), is("s4"));
        assertThat(server.isOnline(), is(true));



        env = envIt.next();
        assertThat(env.getName(), is("PROD"));
        assertThat(env.getUnits(), hasSize(2));
        unitIt = env.getUnits().iterator();
        unit = unitIt.next();
        assertThat(unit.getName(), is("API"));
        assertThat(unit.getVersion(), is("1.0"));
        assertThat(unit.getServers(), hasSize(2));
        serverIt = unit.getServers().iterator();
        server = serverIt.next();
        assertThat(server.getName(), is("s5"));
        assertThat(server.isOnline(), is(true));
        server = serverIt.next();
        assertThat(server.getName(), is("s6"));
        assertThat(server.isOnline(), is(true));

        unit = unitIt.next();
        assertThat(unit.getName(), is("UI"));
        assertThat(unit.getVersion(), is("1.0"));
        assertThat(unit.getServers(), hasSize(2));
        serverIt = unit.getServers().iterator();
        server = serverIt.next();
        assertThat(server.getName(), is("s7"));
        assertThat(server.isOnline(), is(true));
        server = serverIt.next();
        assertThat(server.getName(), is("s8"));
        assertThat(server.isOnline(), is(true));
    }
    
    // Functions split across multiple servers (prod server, non-prod server) but applications are the same
    @Test
    public void testGetDeployStatus_MultipleServers() {
        ObjectId compId = ObjectId.get();
        Component component = new Component();
        CollectorItem item = new CollectorItem();
        item.setId(ObjectId.get());
        item.setCollectorId(ObjectId.get());
        CollectorItem item2 = new CollectorItem();
        item2.setId(ObjectId.get());
        item2.setCollectorId(ObjectId.get());
        component.getCollectorItems().put(CollectorType.Deployment, Arrays.asList(item, item2));
        when(componentRepository.findOne(compId)).thenReturn(component);
        when(collectorRepository.findOne(item.getCollectorId())).thenReturn(new Collector());
        when(collectorRepository.findOne(item2.getCollectorId())).thenReturn(new Collector());

        EnvironmentComponent c_qa_api = makeEnvComponent("QA", "API", "1.1", true);
        EnvironmentComponent c_qa_ui = makeEnvComponent("QA", "UI", "1.1", true);
        EnvironmentComponent c_prod_api = makeEnvComponent("PROD", "API", "1.0", true);
        EnvironmentComponent c_prod_ui = makeEnvComponent("PROD", "UI", "1.0", true);
        when(environmentComponentRepository.findByCollectorItemId(item.getId()))
                .thenReturn(Arrays.asList(c_qa_api, c_qa_ui));
        when(environmentComponentRepository.findByCollectorItemId(item2.getId()))
        	.thenReturn(Arrays.asList(c_prod_api, c_prod_ui));

        EnvironmentStatus s_qa_api_s1 = makeEnvironmentStatus("QA", "API", "s1", true);
        EnvironmentStatus s_qa_api_s2 = makeEnvironmentStatus("QA", "API", "s2", true);
        EnvironmentStatus s_qa_ui_s3 = makeEnvironmentStatus("QA", "UI", "s3", true);
        EnvironmentStatus s_qa_ui_s4 = makeEnvironmentStatus("QA", "UI", "s4", true);
        EnvironmentStatus s_prod_api_s5 = makeEnvironmentStatus("PROD", "API", "s5", true);
        EnvironmentStatus s_prod_api_s6 = makeEnvironmentStatus("PROD", "API", "s6", true);
        EnvironmentStatus s_prod_ui_s7 = makeEnvironmentStatus("PROD", "UI", "s7", true);
        EnvironmentStatus s_prod_ui_s8 = makeEnvironmentStatus("PROD", "UI", "s8", true);
        when(environmentStatusRepository.findByCollectorItemId(item.getId()))
                .thenReturn(Arrays.asList(
                        s_qa_api_s1, s_qa_api_s2, s_qa_ui_s3, s_qa_ui_s4
                ));
        when(environmentStatusRepository.findByCollectorItemId(item2.getId()))
	        .thenReturn(Arrays.asList(
	                s_prod_api_s5, s_prod_api_s6, s_prod_ui_s7, s_prod_ui_s8
	        ));

        DataResponse<List<Environment>> result = deployService.getDeployStatus(compId);

        Iterator<Environment> envIt;
        Iterator<DeployableUnit> unitIt;
        Iterator<Server> serverIt;
        Environment env;
        DeployableUnit unit;
        Server server;

        assertThat(result.getResult(), hasSize(2));
        envIt = result.getResult().iterator();

        env = envIt.next();
        assertThat(env.getName(), is("QA"));
        assertThat(env.getUnits(), hasSize(2));
        unitIt = env.getUnits().iterator();
        unit = unitIt.next();
        assertThat(unit.getName(), is("API"));
        assertThat(unit.getVersion(), is("1.1"));
        assertThat(unit.getServers(), hasSize(2));
        serverIt = unit.getServers().iterator();
        server = serverIt.next();
        assertThat(server.getName(), is("s1"));
        assertThat(server.isOnline(), is(true));
        server = serverIt.next();
        assertThat(server.getName(), is("s2"));
        assertThat(server.isOnline(), is(true));

        unit = unitIt.next();
        assertThat(unit.getName(), is("UI"));
        assertThat(unit.getVersion(), is("1.1"));
        assertThat(unit.getServers(), hasSize(2));
        serverIt = unit.getServers().iterator();
        server = serverIt.next();
        assertThat(server.getName(), is("s3"));
        assertThat(server.isOnline(), is(true));
        server = serverIt.next();
        assertThat(server.getName(), is("s4"));
        assertThat(server.isOnline(), is(true));



        env = envIt.next();
        assertThat(env.getName(), is("PROD"));
        assertThat(env.getUnits(), hasSize(2));
        unitIt = env.getUnits().iterator();
        unit = unitIt.next();
        assertThat(unit.getName(), is("API"));
        assertThat(unit.getVersion(), is("1.0"));
        assertThat(unit.getServers(), hasSize(2));
        serverIt = unit.getServers().iterator();
        server = serverIt.next();
        assertThat(server.getName(), is("s5"));
        assertThat(server.isOnline(), is(true));
        server = serverIt.next();
        assertThat(server.getName(), is("s6"));
        assertThat(server.isOnline(), is(true));

        unit = unitIt.next();
        assertThat(unit.getName(), is("UI"));
        assertThat(unit.getVersion(), is("1.0"));
        assertThat(unit.getServers(), hasSize(2));
        serverIt = unit.getServers().iterator();
        server = serverIt.next();
        assertThat(server.getName(), is("s7"));
        assertThat(server.isOnline(), is(true));
        server = serverIt.next();
        assertThat(server.getName(), is("s8"));
        assertThat(server.isOnline(), is(true));
    }
    
    @Test
    public void collectorIsCreatedFromCollectorNamePropertyIfPresent_v2() throws HygieiaException {
        DeployDataCreateRequest request = makeDataCreateRequest();
        Collector expectedCollector = makeCollector();
        when(collectorService.createCollector(any())).thenReturn(expectedCollector);
        CollectorItem expectedItem = makeCollectorItem();
        when(collectorService.createCollectorItem(any())).thenReturn(expectedItem);
        when(environmentComponentRepository.findByUniqueKey(any(), any(), any(), anyLong()))
            .thenReturn(null);
        EnvironmentComponent co = new EnvironmentComponent();
        ObjectId id = new ObjectId();
        co.setId(id);
        ObjectId id2 = new ObjectId();
        setUpCollector(id, id2);
        co.setCollectorItemId(id2);
        when(environmentComponentRepository.save((EnvironmentComponent)any()))
            .thenReturn(co);
        String output = deployService.createV2(request);
        ArgumentCaptor<Collector> collectorCaptor = ArgumentCaptor.forClass(Collector.class);
        verify(collectorService, times(1)).createCollector(collectorCaptor.capture());
        assertEquals("customCollector", collectorCaptor.getValue().getName());
        assertEquals(id.toString()+","+ id2.toString(), output);
    }

    @Test
    public void collectorIsCreatedFromCollectorNamePropertyIfPresent() throws HygieiaException {
        DeployDataCreateRequest request = makeDataCreateRequest();
        Collector expectedCollector = makeCollector();
        when(collectorService.createCollector(any())).thenReturn(expectedCollector);
        CollectorItem expectedItem = makeCollectorItem();
        when(collectorService.createCollectorItem(any())).thenReturn(expectedItem);
        when(environmentComponentRepository.findByUniqueKey(any(), any(), any(), anyLong()))
                .thenReturn(null);
        EnvironmentComponent co = new EnvironmentComponent();
        ObjectId id = new ObjectId();
        co.setId(id);
        ObjectId id2 = new ObjectId();
        setUpCollector(id, id2);
        co.setCollectorItemId(id2);
        when(environmentComponentRepository.save((EnvironmentComponent)any()))
                .thenReturn(co);
        String output = deployService.create(request);
        ArgumentCaptor<Collector> collectorCaptor = ArgumentCaptor.forClass(Collector.class);
        verify(collectorService, times(1)).createCollector(collectorCaptor.capture());
        assertEquals("customCollector", collectorCaptor.getValue().getName());
        assertEquals(id.toString(), output);
    }

    @Test
    public void getDeployStatusSearchesAllPossibleCollectorNamesForMatches() {
        List<Collector> colls = Arrays.asList(makeCollector(), makeCollector());
        ObjectId id1 = new ObjectId();
        colls.get(0).setId(id1);
        ObjectId id2 = new ObjectId();
        colls.get(1).setId(id2);
        when(collectorRepository.findByCollectorType(CollectorType.Deployment))
            .thenReturn(colls);
        when(collectorItemRepository.findByOptionsAndDeployedApplicationName(id1, "appName"))
            .thenReturn(Collections.emptyList());
        when(collectorItemRepository.findByOptionsAndDeployedApplicationName(id2, "appName"))
            .thenReturn(Collections.singletonList(makeCollectorItem()));
        when(environmentComponentRepository.findByCollectorItemId(any()))
            .thenReturn(Collections.emptyList());
        Collector collector = makeCollector();
        collector.setLastExecuted(234234L);
        when(collectorRepository.findOne(any()))
            .thenReturn(collector);
        DataResponse<List<Environment>> envs = deployService.getDeployStatus("appName");
        assertEquals(234234L,envs.getLastUpdated());
        assertThat(envs.getResult().isEmpty(), is(true));
    }

    @Test
    public void rundeckDocumentCreatesValidDeployRequest() throws Exception {
        InputStream body = DeployServiceTest.class.getResourceAsStream("rundeck_request.xml");
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new InputSource(body));
        String executionId = "22";
        String status = "success";
        ObjectId id = new ObjectId();
        ObjectId id2 = new ObjectId();
        setUpCollector(id, id2);
        ArgumentCaptor<EnvironmentComponent> captor = ArgumentCaptor.forClass(EnvironmentComponent.class);
        String output = deployService.createRundeckBuild(doc, new HashMap<>(), executionId, status);
        assertEquals(id.toString(), output);
        verify(environmentComponentRepository, times(1)).save(captor.capture());
        EnvironmentComponent value = captor.getValue();
        assertTrue(value.isDeployed());
        assertEquals("http://localhost:4440/project/Test/execution/follow/22", value.getJobUrl());
        assertEquals(1481001727759L, value.getDeployTime());
    }

    @Test
    public void rundeckDocumentCreatesValidDeployRequest_v2() throws Exception {
        InputStream body = DeployServiceTest.class.getResourceAsStream("rundeck_request.xml");
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new InputSource(body));
        String executionId = "22";
        String status = "success";
        ObjectId id = new ObjectId();
        ObjectId id2 = new ObjectId();
        setUpCollector(id, id2);
        ArgumentCaptor<EnvironmentComponent> captor = ArgumentCaptor.forClass(EnvironmentComponent.class);
        String output = deployService.createRundeckBuildV2(doc, new HashMap<>(), executionId, status);
        assertEquals(id.toString() + "," + id2.toString(), output);
        verify(environmentComponentRepository, times(1)).save(captor.capture());
        EnvironmentComponent value = captor.getValue();
        assertTrue(value.isDeployed());
        assertEquals("http://localhost:4440/project/Test/execution/follow/22", value.getJobUrl());
        assertEquals(1481001727759L, value.getDeployTime());
    }


    @Test
    public void createDeployRequest_v2() throws Exception {
        ObjectId collectorId = ObjectId.get();

        DeployDataCreateRequest request = makeDataCreateRequest();

        when(collectorRepository.findOne(collectorId)).thenReturn(new Collector());
        when(collectorService.createCollector(any(Collector.class))).thenReturn(new Collector());
        when(collectorService.createCollectorItem(any(CollectorItem.class))).thenReturn(new CollectorItem());

        EnvironmentComponent environmentComponent = makeEnvComponent("QA", "API", "1.1", true);

        when(environmentComponentRepository.save(any(EnvironmentComponent.class))).thenReturn(environmentComponent);
        String response = deployService.createV2(request);
        String expected = environmentComponent.getId().toString() + "," + environmentComponent.getCollectorItemId();
        assertEquals(response, expected);
    }



    @Test
    public void createDeployRequest() throws Exception {
        ObjectId collectorId = ObjectId.get();

        DeployDataCreateRequest request = makeDataCreateRequest();

        when(collectorRepository.findOne(collectorId)).thenReturn(new Collector());
        when(collectorService.createCollector(any(Collector.class))).thenReturn(new Collector());
        when(collectorService.createCollectorItem(any(CollectorItem.class))).thenReturn(new CollectorItem());

        EnvironmentComponent environmentComponent = makeEnvComponent("QA", "API", "1.1", true);

        when(environmentComponentRepository.save(any(EnvironmentComponent.class))).thenReturn(environmentComponent);
        String response = deployService.create(request);
        String expected = environmentComponent.getId().toString();
        assertEquals(response, expected);
    }

    private void setUpCollector(ObjectId id1, ObjectId id2) {
        Collector expectedCollector = makeCollector();
        when(collectorService.createCollector(any())).thenReturn(expectedCollector);
        CollectorItem expectedItem = makeCollectorItem();
        when(collectorService.createCollectorItem(any())).thenReturn(expectedItem);
        EnvironmentComponent co = new EnvironmentComponent();
        co.setCollectorItemId(id2);
        co.setId(id1);
        when(environmentComponentRepository.save((EnvironmentComponent)any()))
            .thenReturn(co);        
    }
    
    private EnvironmentComponent makeEnvComponent(String envName, String name, String version, boolean deployed) {
        EnvironmentComponent comp = new EnvironmentComponent();
        comp.setEnvironmentName(envName);
        comp.setComponentName(name);
        comp.setComponentVersion(version);
        comp.setDeployed(deployed);
        comp.setCollectorItemId(ObjectId.get());
        comp.setId(ObjectId.get());
        return comp;
    }

    private EnvironmentStatus makeEnvironmentStatus(String envName, String name, String server, boolean online) {
        EnvironmentStatus status = new EnvironmentStatus();
        status.setEnvironmentName(envName);
        status.setComponentName(name);
        status.setResourceName(server);
        status.setOnline(online);
        return status;
    }
    
    private DeployDataCreateRequest makeDataCreateRequest() {
        DeployDataCreateRequest deployDataCreateRequest = new DeployDataCreateRequest();
        deployDataCreateRequest.setCollectorName("customCollector");
        return deployDataCreateRequest;
    }
    
    private Collector makeCollector() {
        Collector coll = new Collector();
        coll.setId(new ObjectId());
        return coll;
    }
  
    private CollectorItem makeCollectorItem() {
        CollectorItem item = new CollectorItem();
        item.setId(new ObjectId());
        return item;
    }
}
