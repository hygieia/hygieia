package com.capitalone.dashboard.rest;

import com.capitalone.dashboard.config.TestConfig;
import com.capitalone.dashboard.config.WebMVCConfig;
import com.capitalone.dashboard.model.DataResponse;
import com.capitalone.dashboard.model.EnvironmentComponent;
import com.capitalone.dashboard.model.deploy.DeployableUnit;
import com.capitalone.dashboard.model.deploy.Environment;
import com.capitalone.dashboard.model.deploy.Server;
import com.capitalone.dashboard.service.DeployService;
import org.bson.types.ObjectId;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestConfig.class, WebMVCConfig.class})
@WebAppConfiguration
public class DeployControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext wac;
    @Autowired private DeployService deployService;

    @Before
    public void before() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
    }

    @Test
    public void services() throws Exception {
        ObjectId componentId = ObjectId.get();

        EnvironmentComponent component = new EnvironmentComponent();
        component.setComponentName("component name");
        component.setComponentVersion("component version");
        component.setDeployed(true);
        component.setAsOfDate(100L);

        Server server = new Server("server name", false);
        DeployableUnit unit = new DeployableUnit(component, Arrays.asList(server));

        Environment e = new Environment("QA", "http://www.google.com");
        e.getUnits().add(unit);
        DataResponse<List<Environment>> response = new DataResponse<>(Arrays.asList(e), 1);

        when(deployService.getDeployStatus(componentId)).thenReturn(response);

        mockMvc.perform(get("/deploy/status/" + componentId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$result", hasSize(1)))
                .andExpect(jsonPath("$result[0].name", is(e.getName())))
                .andExpect(jsonPath("$result[0].units", hasSize(1)))
                .andExpect(jsonPath("$result[0].units[0].name", is(unit.getName())))
                .andExpect(jsonPath("$result[0].units[0].version", is(unit.getVersion())))
                .andExpect(jsonPath("$result[0].units[0].deployed", is(unit.isDeployed())))
                .andExpect(jsonPath("$result[0].units[0].lastUpdated", is((int) unit.getLastUpdated())))
                .andExpect(jsonPath("$result[0].units[0].servers", hasSize(1)))
                .andExpect(jsonPath("$result[0].units[0].servers[0].name", is(server.getName())))
                .andExpect(jsonPath("$result[0].units[0].servers[0].online", is(server.isOnline())));
    }
}
