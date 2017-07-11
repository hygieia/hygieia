package com.capitalone.dashboard.rest;

import com.capitalone.dashboard.config.TestConfig;
import com.capitalone.dashboard.config.WebMVCConfig;
import com.capitalone.dashboard.model.CodeQuality;
import com.capitalone.dashboard.model.CodeQualityMetric;
import com.capitalone.dashboard.model.CodeQualityType;
import com.capitalone.dashboard.model.DataResponse;
import com.capitalone.dashboard.model.LibraryPolicyResult;
import com.capitalone.dashboard.model.LibraryPolicyThreatLevel;
import com.capitalone.dashboard.model.LibraryPolicyType;
import com.capitalone.dashboard.request.CodeQualityRequest;
import com.capitalone.dashboard.request.LibraryPolicyRequest;
import com.capitalone.dashboard.service.BinaryArtifactService;
import com.capitalone.dashboard.service.LibraryPolicyService;
import org.bson.types.ObjectId;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;
import static org.mockito.Matchers.isNotNull;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestConfig.class, WebMVCConfig.class})
@WebAppConfiguration

public class LibraryPolicyControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext wac;
    @Autowired
    private LibraryPolicyService libraryPolicyService;


    @Before
    public void before() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
    }

    @Test
    public void libraryPolicySearch() throws Exception {
        LibraryPolicyResult result = makeLibraryPolicy();
        List<LibraryPolicyResult> results = Arrays.asList(result);
        DataResponse<List<LibraryPolicyResult>> response = new DataResponse<>(results, 1);


        when(libraryPolicyService.search(Mockito.any(LibraryPolicyRequest.class)))
                .thenReturn(response);
        mockMvc.perform(
                get("/libraryPolicy?componentId=" + ObjectId.get() + "&max=1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result", hasSize(1)))
                .andExpect(
                        jsonPath("$.result[0].id",
                                is(result.getId().toString())))
                .andExpect(
                        jsonPath("$.result[0].collectorItemId", is(result
                                .getCollectorItemId().toString())))
                .andExpect(
                        jsonPath("$.result[0].timestamp",
                                is(intVal(result.getTimestamp()))))
                .andExpect(
                        jsonPath("$.result[0].evaluationTimestamp",
                                is(intVal(result.getEvaluationTimestamp()))))
                .andExpect(jsonPath("$.result[0].reportUrl", is(result.getReportUrl())))
                .andExpect(
                        jsonPath("$.result[0].threats.License", hasSize(1)))
                .andExpect(
                        jsonPath("$.result[0].threats.License[0].level", is("High")))
                .andExpect(
                        jsonPath("$.result[0].threats.License[0].components", hasSize(1)))
                .andExpect(
                        jsonPath("$.result[0].threats.License[0].components[0]", is("component1")));
    }


    @Test
    public void builds_noComponentId_badRequest() throws Exception {
        mockMvc.perform(get("/libraryPolicy")).andExpect(status().isBadRequest());
    }

    private LibraryPolicyResult makeLibraryPolicy() {
        LibraryPolicyResult policy = new LibraryPolicyResult();
        policy.setId(ObjectId.get());
        policy.setCollectorItemId(ObjectId.get());
        policy.setTimestamp(1);
        policy.setEvaluationTimestamp(1);
        policy.setReportUrl("https://nexusiq.com/ui/MyApp/1234.html");

        LibraryPolicyResult.Threat threat = new LibraryPolicyResult.Threat(LibraryPolicyThreatLevel.High, 1);
        threat.setCount(1);
        threat.setComponents(Arrays.asList("component1"));
        Set<LibraryPolicyResult.Threat> threats = new HashSet<>();
        threats.add(threat);

        Map<LibraryPolicyType, Set<LibraryPolicyResult.Threat>> threatMap = new HashMap<>();
        threatMap.put(LibraryPolicyType.License, threats);
        policy.setThreats(threatMap);
        return policy;
    }

    private int intVal(long value) {
        return Long.valueOf(value).intValue();
    }
}