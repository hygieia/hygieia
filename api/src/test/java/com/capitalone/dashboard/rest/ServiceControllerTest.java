package com.capitalone.dashboard.rest;

import com.capitalone.dashboard.config.TestConfig;
import com.capitalone.dashboard.config.WebMVCConfig;
import com.capitalone.dashboard.model.Service;
import com.capitalone.dashboard.model.ServiceStatus;
import com.capitalone.dashboard.request.ServiceRequest;
import com.capitalone.dashboard.service.ServiceService;
import com.capitalone.dashboard.util.TestUtil;
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

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.junit.Assert.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestConfig.class, WebMVCConfig.class})
@WebAppConfiguration
public class ServiceControllerTest {

    private MockMvc mockMvc;

    @Autowired private WebApplicationContext wac;
    @Autowired private ServiceService serviceService;

    @Before
    public void before() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
    }

    @Test
    public void services() throws Exception {
        Service s = makeService("serviceName", "message", ServiceStatus.Ok);
        when(serviceService.all()).thenReturn(Arrays.asList(s));

        mockMvc.perform(get("/service"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(s.getId().toString())))
                .andExpect(jsonPath("$[0].dashboardId", is(s.getDashboardId().toString())))
                .andExpect(jsonPath("$[0].lastUpdated", is(s.getLastUpdated())))
                .andExpect(jsonPath("$[0].applicationName", is(s.getApplicationName())))
                .andExpect(jsonPath("$[0].name", is(s.getName())))
                .andExpect(jsonPath("$[0].message", is(s.getMessage())))
                .andExpect(jsonPath("$[0].status", is(s.getStatus().toString())));
    }

    @Test
    public void dashboardServices() throws Exception {
        ObjectId dashboardId = ObjectId.get();
        Service s = makeService("serviceName", "message", ServiceStatus.Warning);
        Service dep = makeService("depServiceName", "depMessage", ServiceStatus.Alert);

        when(serviceService.dashboardServices(dashboardId)).thenReturn(Arrays.asList(s));
        when(serviceService.dashboardDependentServices(dashboardId)).thenReturn(Arrays.asList(dep));

        mockMvc.perform(get("/dashboard/" + dashboardId.toString() + "/service"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.services", hasSize(1)))
                .andExpect(jsonPath("$.result.services[0].id", is(s.getId().toString())))
                .andExpect(jsonPath("$.result.services[0].dashboardId", is(s.getDashboardId().toString())))
                .andExpect(jsonPath("$.result.services[0].lastUpdated", is(s.getLastUpdated())))
                .andExpect(jsonPath("$.result.services[0].applicationName", is(s.getApplicationName())))
                .andExpect(jsonPath("$.result.services[0].name", is(s.getName())))
                .andExpect(jsonPath("$.result.services[0].message", is(s.getMessage())))
                .andExpect(jsonPath("$.result.services[0].status", is(s.getStatus().toString())))
                .andExpect(jsonPath("$.result.dependencies", hasSize(1)))
                .andExpect(jsonPath("$.result.dependencies[0].id", is(dep.getId().toString())))
                .andExpect(jsonPath("$.result.dependencies[0].dashboardId", is(dep.getDashboardId().toString())))
                .andExpect(jsonPath("$.result.dependencies[0].lastUpdated", is(dep.getLastUpdated())))
                .andExpect(jsonPath("$.result.dependencies[0].applicationName", is(dep.getApplicationName())))
                .andExpect(jsonPath("$.result.dependencies[0].name", is(dep.getName())))
                .andExpect(jsonPath("$.result.dependencies[0].message", is(dep.getMessage())))
                .andExpect(jsonPath("$.result.dependencies[0].status", is(dep.getStatus().toString())));
    }

    @Test
    public void createService() throws Exception {
        ObjectId dashboardId = ObjectId.get();
        mockMvc.perform(post("/dashboard/" + dashboardId.toString() + "/service")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content("\"Service Name\""))
                .andExpect(status().isCreated());

        verify(serviceService).create(dashboardId, "Service Name");
    }

    @Test
    public void updateService() throws Exception {
        ObjectId dashboardId = ObjectId.get();
        ObjectId serviceId = ObjectId.get();
        Service service = new Service();
        ServiceRequest request = makeServiceRequest(ServiceStatus.Ok, "Ok now");

        when(serviceService.get(serviceId)).thenReturn(service);

        mockMvc.perform(put("/dashboard/" + dashboardId.toString() + "/service/" + serviceId.toString())
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(request)))
                .andExpect(status().isOk());

        verify(serviceService).update(dashboardId, service);
        assertThat(service.getStatus(), is(ServiceStatus.Ok));
        assertThat(service.getMessage(), is("Ok now"));
    }

    @Test
    public void deleteService() throws Exception {
        ObjectId dashboardId = ObjectId.get();
        ObjectId serviceId = ObjectId.get();

        mockMvc.perform(delete("/dashboard/" + dashboardId.toString() + "/service/" + serviceId.toString()))
                .andExpect(status().isNoContent());

        verify(serviceService).delete(dashboardId, serviceId);
    }

    @Test
    public void addDependentService() throws Exception {
        ObjectId dashboardId = ObjectId.get();
        ObjectId serviceId = ObjectId.get();

        mockMvc.perform(post("/dashboard/" + dashboardId.toString() + "/dependent-service/" + serviceId.toString()))
                .andExpect(status().isCreated());

        verify(serviceService).addDependentService(dashboardId, serviceId);
    }

    @Test
    public void deleteDependentService() throws Exception {
        ObjectId dashboardId = ObjectId.get();
        ObjectId serviceId = ObjectId.get();

        mockMvc.perform(delete("/dashboard/" + dashboardId.toString() + "/dependent-service/" + serviceId.toString()))
                .andExpect(status().isNoContent());

        verify(serviceService).deleteDependentService(dashboardId, serviceId);
    }

    private Service makeService(String name, String message, ServiceStatus status) {
        Service s = new Service();
        s.setId(ObjectId.get());
        s.setDashboardId(ObjectId.get());
        s.setApplicationName("appName");
        s.setLastUpdated(System.currentTimeMillis());
        s.setName(name);
        s.setMessage(message);
        s.setStatus(status);
        return s;
    }

    private ServiceRequest makeServiceRequest(ServiceStatus status, String message) {
        ServiceRequest req = new ServiceRequest();
        req.setStatus(status);
        req.setMessage(message);
        return req;
    }

}
