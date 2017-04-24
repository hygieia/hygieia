package com.capitalone.dashboard.rest;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
import com.capitalone.dashboard.model.Authentication;
import com.capitalone.dashboard.request.AuthenticationRequest;
import com.capitalone.dashboard.service.AuthenticationService;
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
    private AuthenticationService authService;

    @Before
    public void before() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
    }
    
    @Test
    public void shouldGetAllUsers() throws Exception {
        Iterable<Authentication> users = Lists.newArrayList();
        when(authService.all()).thenReturn(users);
        mockMvc.perform(get("/admin/users")).andExpect(status().isOk());
    }
    
    @Test
    public void shouldAddAdmin() throws Exception{
        AuthenticationRequest request = new AuthenticationRequest();
        request.setUsername("admin");
        Authentication user = new Authentication("admin", "password");
        when(authService.promoteToAdmin("admin")).thenReturn(user);
        mockMvc.perform(post("/admin/users/addAdmin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new Gson().toJson(request))).andExpect(status().isOk());
    }
  
    @Test
    public void shouldRemoveAdmin() throws Exception{
        AuthenticationRequest request = new AuthenticationRequest();
        request.setUsername("admin");
        Authentication user = new Authentication("admin", "password");
        when(authService.demoteFromAdmin("admin")).thenReturn(user);
        mockMvc.perform(post("/admin/users/removeAdmin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new Gson().toJson(request))).andExpect(status().isOk());
    }

}
