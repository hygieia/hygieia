package com.capitalone.dashboard.rest;

import com.capitalone.dashboard.config.TestConfig;
import com.capitalone.dashboard.config.WebMVCConfig;
import com.capitalone.dashboard.model.CloudInstance;
import com.capitalone.dashboard.request.CloudInstanceCreateRequest;
import com.capitalone.dashboard.request.CloudInstanceListRefreshRequest;
import com.capitalone.dashboard.service.CloudInstanceService;
import com.capitalone.dashboard.util.TestUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestConfig.class, WebMVCConfig.class})
@WebAppConfiguration
public class CloudInstanceControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext wac;

    @Autowired
    private CloudInstanceService cloudInstanceService;

    @Before
    public void before() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
    }

    @Test
    public void refreshInstances() throws Exception {
        CloudInstanceListRefreshRequest req = new CloudInstanceListRefreshRequest();
        req.setAccountNumber("1234");
        long now = System.currentTimeMillis();
        req.setRefreshDate(new Date(now));
        req.setInstanceIds(Arrays.asList("i-1234", "i-2345"));
        List<String> curList = Arrays.asList("i-1234", "i-2345", "i-3456");

        when(cloudInstanceService.refreshInstances(Matchers.any(CloudInstanceListRefreshRequest.class))).thenReturn(curList);
        mockMvc.perform(post("/cloud/instance/refresh")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(req)))
                .andExpect(status().isOk());
    }

    @Test
    public void refreshInstancesBadRequest() throws Exception {
        CloudInstanceListRefreshRequest req = new CloudInstanceListRefreshRequest();
        req.setAccountNumber("1234");
        List<String> curList = Arrays.asList("i-1234", "i-2345", "i-3456");

        when(cloudInstanceService.refreshInstances(Matchers.any(CloudInstanceListRefreshRequest.class))).thenReturn(curList);
        mockMvc.perform(post("/cloud/instance/refresh")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(new String("stuff"))))
                .andExpect(status().is5xxServerError());
    }

    @Test
    public void refreshInstancesEmptyRequest() throws Exception {
        CloudInstanceListRefreshRequest req = new CloudInstanceListRefreshRequest();
        req.setAccountNumber("1234");

        mockMvc.perform(post("/cloud/instance/refresh")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(null)))
                .andExpect(status().isOk());
    }


    @Test
    public void upsertInstanceEmptyRequest() throws Exception {
        List<CloudInstance> req = new ArrayList<>();
        mockMvc.perform(post("/cloud/instance/create")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(req)))
                .andExpect(status().isOk());
    }

    @Test
    public void upsertInstanceNullRequest() throws Exception {
        mockMvc.perform(post("/cloud/instance/create")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(null)))
                .andExpect(status().isOk());
    }

    @Test
    public void upsertInstanceBadRequest() throws Exception {
        mockMvc.perform(post("/cloud/instance/create")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes("")))
                .andExpect(status().is5xxServerError());
    }


    @Test
    public void upsertInstanceOneItemRequest() throws Exception {
        List<CloudInstanceCreateRequest> req = new ArrayList<>();
        req.add(new CloudInstanceCreateRequest());
        mockMvc.perform(post("/cloud/instance/create")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(req)))
                .andExpect(status().isOk());
    }


    @Test
    public void getInstanceDetails() throws Exception {
        mockMvc.perform(get("/cloud/instance/details/instances/i-123456")
                .contentType(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());
    }

    @Test
    public void getInstanceDetailsNoInput() throws Exception {
        mockMvc.perform(get("/cloud/instance/details/instances")
                .contentType(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isNotFound());
    }

    @Test
    public void getInstanceDetailsMultipleInput() throws Exception {
        mockMvc.perform(get("/cloud/instance/details/instances/i-123456,i234567")
                .contentType(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());
    }


    @Test
    public void getInstanceDetailsAccount() throws Exception {
        mockMvc.perform(get("/cloud/instance/details/account/12345678")
                .contentType(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());
    }

    @Test
    public void getInstanceDetailsAccountNoInput() throws Exception {
        mockMvc.perform(get("/cloud/instance/details/account")
                .contentType(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isNotFound());
    }

    @Test
    public void getInstanceDetailsAccountHistory() throws Exception {
        mockMvc.perform(get("/cloud/instance/history/account/1234567")
                .contentType(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());
    }

    @Test
    public void getInstanceDetailsAccountHistoryNoInput() throws Exception {
        mockMvc.perform(get("/cloud/instance/history/account")
                .contentType(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isNotFound());
    }

    @Test
    public void getInstanceByTags() throws Exception {
        CloudInstanceListRefreshRequest req = new CloudInstanceListRefreshRequest();
        req.setAccountNumber("1234");
        long now = System.currentTimeMillis();
        req.setRefreshDate(new Date(now));
        req.setInstanceIds(Arrays.asList("i-1234", "i-2345"));
        List<String> curList = Arrays.asList("i-1234", "i-2345", "i-3456");

        when(cloudInstanceService.refreshInstances(Matchers.any(CloudInstanceListRefreshRequest.class))).thenReturn(curList);
        mockMvc.perform(post("/cloud/instance/refresh")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(req)))
                .andExpect(status().isOk());
    }

}