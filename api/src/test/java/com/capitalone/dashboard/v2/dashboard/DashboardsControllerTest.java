package com.capitalone.dashboard.v2.dashboard;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;

import org.bson.types.ObjectId;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.capitalone.dashboard.config.TestConfig;
import com.capitalone.dashboard.config.WebMVCConfig;
import com.capitalone.dashboard.model.Application;
import com.capitalone.dashboard.model.Dashboard;
import com.capitalone.dashboard.model.DashboardType;
import com.capitalone.dashboard.service.DashboardService;
import com.capitalone.dashboard.util.TestUtil;
import com.google.common.collect.Sets;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestConfig.class, WebMVCConfig.class})
@WebAppConfiguration
public class DashboardsControllerTest {

    private MockMvc mockMvc;
    
    @Autowired 
    private WebApplicationContext wac;
    
    @Autowired 
    private DashboardService dashboardService;
    
    @Before
    public void before() {
        SecurityContextHolder.clearContext();
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
    }
    
    @Test
    public void getAllDashboards() throws Exception {
        Dashboard dashboard = new Dashboard("Template", "Title", new Application("App Name"), Sets.newHashSet(), DashboardType.Team);
        ObjectId dashboardId = new ObjectId();
        dashboard.setId(dashboardId);
        when(dashboardService.all()).thenReturn(Arrays.asList(dashboard));
        mockMvc.perform(get("/v2/dashboards"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].template", is("Template")))
                .andExpect(jsonPath("$.content[0].title", is("Title")))
                .andExpect(jsonPath("$.content[0].links[0].rel", is("self")))
                .andExpect(jsonPath("$.content[0].links[0].href", is("http://localhost/v2/dashboards/" + dashboardId)))
                .andExpect(jsonPath("$.links[0].rel", is("self")))
                .andExpect(jsonPath("$.links[0].href", is("http://localhost/v2/dashboards?owned=false")));
    }
    
    @Test
    public void getOwnedDashboards() throws Exception {
        Dashboard dashboard = new Dashboard("Template", "Title", new Application("App Name"), Sets.newHashSet(), DashboardType.Team);
        ObjectId dashboardId = new ObjectId();
        dashboard.setId(dashboardId);
        when(dashboardService.getOwnedDashboards()).thenReturn(Arrays.asList(dashboard));
        mockMvc.perform(get("/v2/dashboards?owned=true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].template", is("Template")))
                .andExpect(jsonPath("$.content[0].title", is("Title")))
                .andExpect(jsonPath("$.content[0].links[0].rel", is("self")))
                .andExpect(jsonPath("$.content[0].links[0].href", is("http://localhost/v2/dashboards/" + dashboardId)))
                .andExpect(jsonPath("$.links[0].rel", is("self")))
                .andExpect(jsonPath("$.links[0].href", is("http://localhost/v2/dashboards?owned=true")));
    }
    
    @Test
    public void getDashboardById() throws Exception {
        Dashboard dashboard = new Dashboard("Template", "Title", new Application("App Name"), Sets.newHashSet(), DashboardType.Team);
        ObjectId dashboardId = new ObjectId();
        dashboard.setId(dashboardId);
        when(dashboardService.get(dashboardId)).thenReturn(dashboard);
        mockMvc.perform(get("/v2/dashboards/" + dashboardId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.template", is("Template")))
                .andExpect(jsonPath("$.title", is("Title")))
                .andExpect(jsonPath("$.links[0].rel", is("self")))
                .andExpect(jsonPath("$.links[0].href", is("http://localhost/v2/dashboards/" + dashboardId)));
    }
    
    @Test
    public void createDashboard() throws Exception {
        Dashboard dashboard = new Dashboard("Template", "Title", new Application("App Name"), Sets.newHashSet(), DashboardType.Team);
        ObjectId dashboardId = new ObjectId();
        dashboard.setId(dashboardId);
        com.capitalone.dashboard.v2.dashboard.Dashboard dashboardResource = new com.capitalone.dashboard.v2.dashboard.Dashboard(dashboard);
        
        when(dashboardService.create(isA(Dashboard.class))).thenReturn(dashboard);
        mockMvc.perform(post("/v2/dashboards/")
                .content(TestUtil.convertObjectToJsonBytes(dashboardResource))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.template", is("Template")))
                .andExpect(jsonPath("$.title", is("Title")))
                .andExpect(jsonPath("$.links[0].rel", is("self")))
                .andExpect(jsonPath("$.links[0].href", is("http://localhost/v2/dashboards/" + dashboardId)));
    }
    
    @Test
    public void updateDashbaord() throws Exception {
        Dashboard dashboard = new Dashboard("Template", "Title", new Application("App Name"), Sets.newHashSet(), DashboardType.Team);
        ObjectId dashboardId = new ObjectId();
        dashboard.setId(dashboardId);
        com.capitalone.dashboard.v2.dashboard.Dashboard dashboardResource = new com.capitalone.dashboard.v2.dashboard.Dashboard(dashboard);
        
        when(dashboardService.update(isA(Dashboard.class))).thenReturn(dashboard);
        mockMvc.perform(put("/v2/dashboards/" + dashboardId)
                .content(TestUtil.convertObjectToJsonBytes(dashboardResource))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.template", is("Template")))
                .andExpect(jsonPath("$.title", is("Title")))
                .andExpect(jsonPath("$.links[0].rel", is("self")))
                .andExpect(jsonPath("$.links[0].href", is("http://localhost/v2/dashboards/" + dashboardId)));
    }
    
    @Test
    public void deleteDashboard() throws Exception {
        ObjectId dashboardId = new ObjectId();
        mockMvc.perform(delete("/v2/dashboards/" + dashboardId))
                .andExpect(status().isNoContent());
        verify(dashboardService).delete(dashboardId);
    }

}
