package com.capitalone.dashboard.v2.application;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.capitalone.dashboard.config.TestConfig;
import com.capitalone.dashboard.config.WebMVCConfig;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestConfig.class, WebMVCConfig.class})
@WebAppConfiguration
@TestPropertySource(properties = {
        "version.number=testVersion",
    })
public class ApplicationControllerTest {

    private MockMvc mockMvc;
    
    @Autowired 
    private WebApplicationContext wac;
    
    @Before
    public void before() {
        SecurityContextHolder.clearContext();
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
    }
    
    @Test
    public void getApplication() throws Exception {
        mockMvc.perform(get("/v2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", is("testVersion")))
                .andExpect(jsonPath("$.links[0].rel", is("self")))
                .andExpect(jsonPath("$.links[0].href", is("http://localhost/v2")))
                .andExpect(jsonPath("$.links[1].rel", is("dashboards")))
                .andExpect(jsonPath("$.links[1].href", is("http://localhost/v2/dashboards")))
                .andExpect(jsonPath("$.links[2].rel", is("my-dashboards")))
                .andExpect(jsonPath("$.links[2].href", is("http://localhost/v2/dashboards?owned=true")));
    }

}
