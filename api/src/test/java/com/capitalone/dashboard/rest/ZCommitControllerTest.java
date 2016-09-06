package com.capitalone.dashboard.rest;

import com.capitalone.dashboard.config.TestConfig;
import com.capitalone.dashboard.config.WebMVCConfig;
import com.capitalone.dashboard.model.Commit;
import com.capitalone.dashboard.model.DataResponse;
import com.capitalone.dashboard.request.CommitRequest;
import com.capitalone.dashboard.service.CommitService;
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
public class ZCommitControllerTest {

    private MockMvc mockMvc;

    @Autowired private WebApplicationContext wac;
    @Autowired private CommitService commitService;

    @Before
    public void before() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
    }

    @Test
    public void commit_search() throws Exception {
        Commit commit = makeCommit();
        Iterable<Commit> commits = Arrays.asList(commit);
        DataResponse<Iterable<Commit>> response = new DataResponse<>(commits, 1);

        when(commitService.search(Mockito.any(CommitRequest.class))).thenReturn(response);

        mockMvc.perform(get("/commit?componentId=" + ObjectId.get()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$result", hasSize(1)))
                .andExpect(jsonPath("$result[0].scmUrl", is(commit.getScmUrl())))
                .andExpect(jsonPath("$result[0].scmRevisionNumber", is(commit.getScmRevisionNumber())))
                .andExpect(jsonPath("$result[0].numberOfChanges", is(Ints.saturatedCast(commit.getNumberOfChanges()))))
                .andExpect(jsonPath("$result[0].scmCommitTimestamp", is(Ints.saturatedCast(commit.getScmCommitTimestamp()))))
                .andExpect(jsonPath("$result[0].scmCommitLog", is(commit.getScmCommitLog())))
                .andExpect(jsonPath("$result[0].scmAuthor", is(commit.getScmAuthor())));
    }

    @Test
    public void  commits_noComponentId_badRequest() throws Exception {
        mockMvc.perform(get("/commit")).andExpect(status().isBadRequest());
    }

    @Test
    public void insertCommitGoodRequest() throws Exception {
        byte[] content = Resources.asByteSource(Resources.getResource("github-push-v3.json")).read();
        when(commitService.createFromGitHubv3(Matchers.any(JSONObject.class))).thenReturn("123456");
        mockMvc.perform(post("/commit/github/v3")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(content))
                .andExpect(status().isCreated());

    }

    @Test
    public void insertCommitBadRequest1() throws Exception {

        byte[] content = "".getBytes();
        System.out.println(new String(content, StandardCharsets.UTF_8));
        when(commitService.createFromGitHubv3(Matchers.any(JSONObject.class))).thenReturn("");
        mockMvc.perform(post("/commit/github/v3")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(content))
                .andExpect(status().isInternalServerError());

    }

    private Commit makeCommit() {
        Commit commit = new Commit();
        commit.setScmUrl("scmUrl");
        commit.setScmRevisionNumber("revNum");
        commit.setNumberOfChanges(20);
        commit.setScmCommitTimestamp(200);
        commit.setScmCommitLog("Log message");
        commit.setScmAuthor("bob");
        commit.setTimestamp(2);
        return commit;
    }

}
