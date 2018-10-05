package com.capitalone.dashboard.rest;

import com.capitalone.dashboard.config.TestConfig;
import com.capitalone.dashboard.config.WebMVCConfig;
import com.capitalone.dashboard.misc.HygieiaException;
import com.capitalone.dashboard.model.AuthType;
import com.capitalone.dashboard.model.Cmdb;
import com.capitalone.dashboard.model.Dashboard;
import com.capitalone.dashboard.model.DashboardType;
import com.capitalone.dashboard.request.CmdbRequest;
import com.capitalone.dashboard.request.DashboardRemoteRequest;
import com.capitalone.dashboard.service.CmdbRemoteService;
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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestConfig.class, WebMVCConfig.class})
@WebAppConfiguration
public class CmdbControllerTest {
    private MockMvc mockMvc;
    @Autowired private WebApplicationContext wac;
    @Autowired private CmdbRemoteService cmdbRemoteService;

    private String configItemBusServName = "ASVTEST";
    private String configItemBusAppName = "BAPTEST";

    @Before
    public void before() {
        SecurityContextHolder.clearContext();
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
    }

    @Test
    public void createCmdbRemote() throws Exception {
        Cmdb cmdb = makeCmdbItem("BAPTEST", "subtype",
                "type", "assignmentgroup","owner", "BAPTEST");
        CmdbRequest request = makeCmdbRequest("BAPTEST", "subtype",
                "type", "assignmentgroup","owner", "BAPTEST", "ASVTEST", "cmdbCollector");
        initiateSecurityContext("someuser", AuthType.STANDARD);
        when(cmdbRemoteService.remoteCreate(Matchers.any(CmdbRequest.class))).thenReturn(cmdb);

        mockMvc.perform(post("/cmdb/remoteCreate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new Gson().toJson(request)))
                .andExpect(status().isCreated());
    }
    private Cmdb makeCmdbItem(String configurationItem,
                              String configurationItemSubType,
                              String configurationItemType,
                              String assignmentGroup,
                              String ownerDept,
                              String commonName){
        Cmdb cmdb = new Cmdb();
        cmdb.setConfigurationItem(configurationItem);
        cmdb.setConfigurationItemSubType(configurationItemSubType);
        cmdb.setConfigurationItemType(configurationItemType);
        cmdb.setAssignmentGroup(assignmentGroup);
        cmdb.setOwnerDept(ownerDept);
        cmdb.setCommonName(commonName);

        return cmdb;
    }
    private CmdbRequest makeCmdbRequest(String configurationItem,
                                        String configurationItemSubType,
                                        String configurationItemType,
                                        String assignmentGroup,
                                        String ownerDept,
                                        String commonName,
                                        String configurationItemBusServName,
                                        String toolName){

        CmdbRequest request = new CmdbRequest();
        request.setConfigurationItem(configurationItem);
        request.setConfigurationItemSubType(configurationItemSubType);
        request.setConfigurationItemType(configurationItemType);
        request.setAssignmentGroup(assignmentGroup);
        request.setOwnerDept(ownerDept);
        request.setCommonName(commonName);
        request.setConfigurationItemBusServName(configurationItemBusServName);
        request.setToolName(toolName);
        return request;
    }

    private void initiateSecurityContext(String username, AuthType standard) {
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(username, "password");
        authentication.setDetails(AuthType.STANDARD.name());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

}
