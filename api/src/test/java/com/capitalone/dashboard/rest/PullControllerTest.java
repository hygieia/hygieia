package com.capitalone.dashboard.rest;

import com.capitalone.dashboard.config.TestConfig;
import com.capitalone.dashboard.config.WebMVCConfig;
import com.capitalone.dashboard.model.Pull;
import com.capitalone.dashboard.model.DataResponse;
import com.capitalone.dashboard.request.PullRequest;
import com.capitalone.dashboard.service.PullService;
import com.capitalone.dashboard.util.TestUtil;
import com.google.common.io.Resources;
import com.google.common.primitives.Ints;
import org.bson.types.ObjectId;
import org.json.simple.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestConfig.class, WebMVCConfig.class})
@WebAppConfiguration
public class PullControllerTest {

    private MockMvc mockMvc;

    @Autowired private WebApplicationContext wac;
    @Autowired private PullService pullService;

    @Before
    public void before() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
    }

    @Test
    public void pull_search() throws Exception {
        Pull pull = makePull();
        Iterable<Pull> pulls = Arrays.asList(pull);
        DataResponse<Iterable<Pull>> response = new DataResponse<>(pulls, 1);

        when(pullService.search(Mockito.any(PullRequest.class))).thenReturn(response);

        mockMvc.perform(get("/pulls?componentId=" + ObjectId.get()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$result", hasSize(1)))
                .andExpect(jsonPath("$result[0].scmUrl", is(pull.getScmUrl())))
                .andExpect(jsonPath("$result[0].scmRevisionNumber", is(pull.getScmRevisionNumber())))
                .andExpect(jsonPath("$result[0].numberOfChanges", is(Ints.saturatedCast(pull.getNumberOfChanges()))));

    }

    @Test
    public void  pulls_noComponentId_badRequest() throws Exception {
        mockMvc.perform(get("/pulls")).andExpect(status().isBadRequest());
    }

    @Test
    public void insertPullGoodRequest() throws Exception {
        byte[] content = Resources.asByteSource(Resources.getResource("github-push-v3.json")).read();
        when(pullService.createFromGitHubv3(Matchers.any(JSONObject.class))).thenReturn("123456");
        mockMvc.perform(post("/pulls/github/v3")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(content))
                .andExpect(status().isCreated());

    }

    @Test
    public void insertPullBadRequest1() throws Exception {

        byte[] content = "".getBytes();
        System.out.println(new String(content, StandardCharsets.UTF_8));
        when(pullService.createFromGitHubv3(Matchers.any(JSONObject.class))).thenReturn("");
        mockMvc.perform(post("/pulls/github/v3")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(content))
                .andExpect(status().isInternalServerError());

    }

    private Pull makePull() {
        Pull pull = new Pull();
        pull.setScmUrl("scmUrl");
        pull.setScmRevisionNumber("revNum");
        pull.setNumberOfChanges(20);
        pull.setScmAuthor("bob");
        pull.setTimestamp(2);
        return pull;
    }

}
