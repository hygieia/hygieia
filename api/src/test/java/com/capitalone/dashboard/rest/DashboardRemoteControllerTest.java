package com.capitalone.dashboard.rest;

import com.capitalone.dashboard.config.TestConfig;
import com.capitalone.dashboard.config.WebMVCConfig;
import com.capitalone.dashboard.model.AuthType;
import com.capitalone.dashboard.model.Dashboard;
import com.capitalone.dashboard.model.DashboardType;
import com.capitalone.dashboard.request.DashboardRemoteRequest;
import com.capitalone.dashboard.service.DashboardRemoteService;
import com.google.gson.Gson;
import org.bson.types.ObjectId;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static com.capitalone.dashboard.fixture.DashboardFixture.makeDashboard;
import static com.capitalone.dashboard.fixture.DashboardFixture.makeDashboardRemoteRequest;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestConfig.class, WebMVCConfig.class})
@WebAppConfiguration
public class DashboardRemoteControllerTest {
    private MockMvc mockMvc;
    @Autowired private WebApplicationContext wac;
    @Autowired private DashboardRemoteService dashboardRemoteService;

    @Before
    public void before() {
        SecurityContextHolder.clearContext();
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
    }

    @Test
    public void createTeamDashboardRemote() throws Exception {
        Dashboard dashboard = makeDashboard("t1", "title", "app", "comp", "someuser", DashboardType.Team, ObjectId.get(), ObjectId.get());
        dashboard.setId(ObjectId.get());

        DashboardRemoteRequest request = makeDashboardRemoteRequest("template", "dashboardtitle", "app", "comp", "someuser", null, "team", ObjectId.get(), ObjectId.get());
        initiateSecurityContext("someuser", AuthType.STANDARD);
        when(dashboardRemoteService.remoteCreate(Matchers.any(DashboardRemoteRequest.class), eq(false))).thenReturn(dashboard);

        mockMvc.perform(post("/dashboard/remoteCreate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new Gson().toJson(request)))
                .andExpect(status().isCreated());
    }

    @Test
    public void updateTeamDashboardRemote() throws Exception {
        Dashboard dashboard = makeDashboard("t1", "title", "app", "comp", "someuser", DashboardType.Team, ObjectId.get(), ObjectId.get());
        dashboard.setId(ObjectId.get());

        DashboardRemoteRequest request = makeDashboardRemoteRequest("template", "dashboardtitle", "app", "comp", "someuser", null, "team", ObjectId.get(), ObjectId.get());
        initiateSecurityContext("someuser", AuthType.STANDARD);
        when(dashboardRemoteService.remoteCreate(Matchers.any(DashboardRemoteRequest.class), eq(true))).thenReturn(dashboard);

        mockMvc.perform(post("/dashboard/remoteUpdate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new Gson().toJson(request)))
                .andExpect(status().isCreated());
    }

    private void initiateSecurityContext(String username, AuthType standard) {
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(username, "password");
        authentication.setDetails(AuthType.STANDARD.name());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
