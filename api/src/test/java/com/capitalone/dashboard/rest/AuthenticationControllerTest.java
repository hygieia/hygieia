package com.capitalone.dashboard.rest;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
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
import com.capitalone.dashboard.model.UserRole;
import com.capitalone.dashboard.request.RoleRequest;
import com.capitalone.dashboard.service.AuthenticationService;
import com.google.common.collect.Lists;
import com.google.gson.Gson;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestConfig.class, WebMVCConfig.class})
@WebAppConfiguration
public class AuthenticationControllerTest {

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
        mockMvc.perform(get("/users")).andExpect(status().isOk());
    }
    
    @Test
    public void shouldAddAdmin() throws Exception{
        RoleRequest request = new RoleRequest();
        request.setUserRole(UserRole.ROLE_ADMIN);
        Authentication user = new Authentication("admin", "password");
        when(authService.addRole("admin", UserRole.ROLE_ADMIN)).thenReturn(user);
        mockMvc.perform(post("/users/admin/roles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new Gson().toJson(request))).andExpect(status().isCreated());
    }
  
    @Test
    public void shouldRemoveAdmin() throws Exception{
        String username = "admin";
        Authentication user = new Authentication(username, "password");
        when(authService.removeRole(username, UserRole.ROLE_ADMIN)).thenReturn(user);
        mockMvc.perform(delete("/users/admin/roles/ROLE_ADMIN")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

}
