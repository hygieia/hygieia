package com.capitalone.dashboard.rest;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

import com.capitalone.dashboard.config.TestConfig;
import com.capitalone.dashboard.config.WebMVCConfig;
import com.capitalone.dashboard.model.Configuration;
import com.capitalone.dashboard.request.ConfigurationCreateRequest;
import com.capitalone.dashboard.service.ConfigurationService;
import com.capitalone.dashboard.util.TestUtil;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestConfig.class, WebMVCConfig.class})
@WebAppConfiguration
public class ConfigurationControllerTest {
	private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext wac;
    @Autowired
    private ConfigurationService configurationService;
    
    @Before
    public void before() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
    }
    
    @Test
	public void insertConfigurationTest() throws Exception {
		List<Configuration> config = makeConfiguration();
		when(configurationService.insertConfigurationData((List<Configuration>)Matchers.any(ConfigurationCreateRequest.class))).thenReturn((config));
		mockMvc.perform(put("/dashboard/generalConfig")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(config)))
                .andExpect(status().isCreated());
	}
    
    @Test
    public void getConfigurationTest() throws Exception {
    	List<Configuration> config = makeConfiguration();
    	when(configurationService.getConfigurationData()).thenReturn(config);
    	mockMvc.perform(get("/dashboard/generalConfig/fetch"))
    			.andExpect(status().isOk())
    			.andExpect(jsonPath("$", hasSize(1)));
    	
    }
	private List<Configuration> makeConfiguration() {
		List<Configuration> configList = new ArrayList<>();
		Set<Map<String,String>> serverConfig = new HashSet<>();
		Map<String,String> options = new HashMap<>();
		Configuration config = new Configuration();
		config.setCollectorName("testCollector");
		options.put("url", "http://jenkinsserver.domain.com:9999/");
		options.put("password", "password");
		serverConfig.add(options);
		config.setInfo(serverConfig);
		configList.add(config);
		return configList;
	}
}
