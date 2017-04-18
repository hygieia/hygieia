package com.capitalone.dashboard.rest;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
import com.capitalone.dashboard.model.AuthType;
import com.capitalone.dashboard.model.UserInfo;
import com.capitalone.dashboard.service.UserInfoService;
import com.google.common.collect.Lists;
import com.google.gson.Gson;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestConfig.class, WebMVCConfig.class})
@WebAppConfiguration
public class AdminControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext wac;
    @Autowired
    private UserInfoService userInfoService;

    @Before
    public void before() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
    }
    
    @Test
    public void shouldGetAllUsers() throws Exception {
        Collection<UserInfo> users = Lists.newArrayList();
        when(userInfoService.getUsers()).thenReturn(users);
        mockMvc.perform(get("/admin/users")).andExpect(status().isOk());
    }
    
    @Test
    public void shouldAddAdmin() throws Exception{
        UserInfo user = new UserInfo();
        user.setAuthType(AuthType.STANDARD);
        user.setUsername("admin");
        when(userInfoService.promoteToAdmin("admin", AuthType.STANDARD)).thenReturn(user);
        mockMvc.perform(post("/admin/users/addAdmin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new Gson().toJson(user))).andExpect(status().isOk());
    }
  
    @Test
    public void shouldRemoveAdmin() throws Exception{
        UserInfo user = new UserInfo();
        user.setAuthType(AuthType.STANDARD);
        user.setUsername("admin");
        when(userInfoService.demoteFromAdmin("admin", AuthType.STANDARD)).thenReturn(user);
        mockMvc.perform(post("/admin/users/removeAdmin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new Gson().toJson(user))).andExpect(status().isOk());
    }

}
