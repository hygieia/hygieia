package com.capitalone.dashboard.rest;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Collection;

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

import com.capitalone.dashboard.config.TestConfig;
import com.capitalone.dashboard.config.WebMVCConfig;
import com.capitalone.dashboard.model.UserInfo;
import com.capitalone.dashboard.service.UserInfoService;
import com.google.common.collect.Lists;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestConfig.class, WebMVCConfig.class})
@WebAppConfiguration
public class UserInfoControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext wac;
    
    @Autowired
    private UserInfoService userInfoService;

    private Collection<UserInfo> response;
    
    @Before
    public void before() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
        
        UserInfo info1 = new UserInfo();
        info1.setUsername("one");
        UserInfo info2 = new UserInfo();
        info2.setUsername("two");
        UserInfo info3 = new UserInfo();
        info3.setUsername("three");
        response = Lists.newArrayList(info1, info2, info3);
        
        when(userInfoService.getUsers()).thenReturn(response);
    }
    
    @Test
    public void getAllUsers() throws Exception {
    	mockMvc.perform(get("/users").contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(3)))
        .andExpect(jsonPath("$[0].username", is("one")))
        .andExpect(jsonPath("$[1].username", is("two")))
        .andExpect(jsonPath("$[2].username", is("three")));
    	
    	verify(userInfoService).getUsers();
    }
}
