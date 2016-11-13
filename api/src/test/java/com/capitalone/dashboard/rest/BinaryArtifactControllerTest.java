package com.capitalone.dashboard.rest;

import com.capitalone.dashboard.config.TestConfig;
import com.capitalone.dashboard.config.WebMVCConfig;
import com.capitalone.dashboard.request.BinaryArtifactCreateRequest;
import com.capitalone.dashboard.service.BinaryArtifactService;
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

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.HashMap;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestConfig.class, WebMVCConfig.class})
@WebAppConfiguration
public class BinaryArtifactControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext wac;
    @Autowired
    private BinaryArtifactService artifactService;

    @Before
    public void before() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
    }


    @Test
    public void insertArtifactGoodRequest() throws Exception {
        BinaryArtifactCreateRequest request = makeGoodArtifactRequest();
        when(artifactService.create((BinaryArtifactCreateRequest) Matchers.any(BinaryArtifactCreateRequest.class))).thenReturn("1234");
        mockMvc.perform(post("/artifact")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(request)))
                .andExpect(status().isCreated());

    }

    @Test
    public void insertBuildBadRequest1() throws Exception {
        BinaryArtifactCreateRequest request = makeMissingBuildIdArtifactRequest();
        when(artifactService.create((BinaryArtifactCreateRequest) Matchers.any(BinaryArtifactCreateRequest.class))).thenReturn("");
                mockMvc.perform(post("/artifact")
                        .contentType(TestUtil.APPLICATION_JSON_UTF8)
                        .content(TestUtil.convertObjectToJsonBytes(request)))
                        .andExpect(status().isBadRequest());

    }

    private BinaryArtifactCreateRequest makeGoodArtifactRequest() {
        BinaryArtifactCreateRequest artifact = new BinaryArtifactCreateRequest();
        artifact.setArtifactName("MyArtifact");
        artifact.setCanonicalName("MyArtifact-1.1.0.0");
        artifact.setBuildId("1234");
        artifact.setMetadata(new HashMap<>());
        artifact.setArtifactGroup("Mygroup.com");
        artifact.setArtifactVersion("1.1.0.0");
        return artifact;
    }

    private BinaryArtifactCreateRequest makeMissingBuildIdArtifactRequest() {
        BinaryArtifactCreateRequest artifact = new BinaryArtifactCreateRequest();
        artifact.setArtifactName("MyArtifact");
        artifact.setArtifactGroup("Mygroup.com");
        artifact.setArtifactVersion("1.1.0.0");
        return artifact;
    }
}
