package com.capitalone.dashboard.service;

import com.capitalone.dashboard.model.*;
import com.capitalone.dashboard.model.deploy.DeployableUnit;
import com.capitalone.dashboard.model.deploy.Environment;
import com.capitalone.dashboard.model.deploy.Server;
import com.capitalone.dashboard.repository.CollectorRepository;
import com.capitalone.dashboard.repository.ComponentRepository;
import com.capitalone.dashboard.repository.EnvironmentComponentRepository;
import com.capitalone.dashboard.repository.EnvironmentStatusRepository;
import org.bson.types.ObjectId;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class DeployServiceTest {

    @Mock ComponentRepository componentRepository;
    @Mock EnvironmentComponentRepository environmentComponentRepository;
    @Mock EnvironmentStatusRepository environmentStatusRepository;
    @Mock private CollectorRepository collectorRepository;
    @InjectMocks DeployServiceImpl deployService;

    @org.junit.Test
    public void getDeployStatus() {
        ObjectId compId = ObjectId.get();
        Component component = new Component();
        CollectorItem item = new CollectorItem();
        item.setId(ObjectId.get());
        item.setCollectorId(ObjectId.get());
        component.getCollectorItems().put(CollectorType.Deployment, Arrays.asList(item));
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
    @org.junit.Test
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

    private EnvironmentComponent makeEnvComponent(String envName, String name, String version, boolean deployed) {
        EnvironmentComponent comp = new EnvironmentComponent();
        comp.setEnvironmentName(envName);
        comp.setComponentName(name);
        comp.setComponentVersion(version);
        comp.setDeployed(deployed);
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
}
