package com.capitalone.dashboard.rest;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.bson.types.ObjectId;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.w3c.dom.Document;

import com.capitalone.dashboard.config.TestConfig;
import com.capitalone.dashboard.config.WebMVCConfig;
import com.capitalone.dashboard.model.DataResponse;
import com.capitalone.dashboard.model.EnvironmentComponent;
import com.capitalone.dashboard.model.deploy.DeployableUnit;
import com.capitalone.dashboard.model.deploy.Environment;
import com.capitalone.dashboard.model.deploy.Server;
import com.capitalone.dashboard.service.DeployService;

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
        DeployableUnit unit = new DeployableUnit(component, Collections.singletonList(server));

        Environment e = new Environment("QA", "http://www.google.com");
        e.getUnits().add(unit);
        DataResponse<List<Environment>> response = new DataResponse<>(Collections.singletonList(e), 1);

        when(deployService.getDeployStatus(componentId)).thenReturn(response);

        mockMvc.perform(get("/deploy/status/" + componentId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result", hasSize(1)))
                .andExpect(jsonPath("$.result[0].name", is(e.getName())))
                .andExpect(jsonPath("$.result[0].units", hasSize(1)))
                .andExpect(jsonPath("$.result[0].units[0].name", is(unit.getName())))
                .andExpect(jsonPath("$.result[0].units[0].version", is(unit.getVersion())))
                .andExpect(jsonPath("$.result[0].units[0].deployed", is(unit.isDeployed())))
                .andExpect(jsonPath("$.result[0].units[0].lastUpdated", is((int) unit.getLastUpdated())))
                .andExpect(jsonPath("$.result[0].units[0].servers", hasSize(1)))
                .andExpect(jsonPath("$.result[0].units[0].servers[0].name", is(server.getName())))
                .andExpect(jsonPath("$.result[0].units[0].servers[0].online", is(server.isOnline())));
    }
    
    @Test
    public void rundeckPostEndpointFailsToParseNonXMLDocument() throws Exception {
        mockMvc.perform(post("/deploy/rundeck")
                .content("failtoparse this because itsnot XML")
                .contentType(MediaType.TEXT_XML_VALUE)
                .header("X-Rundeck-Notification-Execution-ID", "test")
                .header("X-Rundeck-Notification-Trigger", "success"))
            .andExpect(status().isNotModified());           
    }
    
    @Test
    public void rundeckPostEndpointParsesXmlIntoDocument() throws Exception {
        when(deployService.createRundeckBuild(any(Document.class), any(), eq("test"), eq("success")))
            .thenReturn("8675309");
        mockMvc.perform(post("/deploy/rundeck")
                .content("<valid></valid>")
                .contentType(MediaType.TEXT_XML_VALUE)
                .header("X-Rundeck-Notification-Execution-ID", "test")
                .header("X-Rundeck-Notification-Trigger", "success"))
            .andExpect(status().isCreated())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON));        
    }

    @Test
    public void rundeckPostEndpointParsesXmlIntoDocumentV2() throws Exception {
        when(deployService.createRundeckBuildV2(any(Document.class), any(), eq("test"), eq("success")))
                .thenReturn("8675309");
        mockMvc.perform(post("/v2/deploy/rundeck")
                .content("<valid></valid>")
                .contentType(MediaType.TEXT_XML_VALUE)
                .header("X-Rundeck-Notification-Execution-ID", "test")
                .header("X-Rundeck-Notification-Trigger", "success"))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

}
