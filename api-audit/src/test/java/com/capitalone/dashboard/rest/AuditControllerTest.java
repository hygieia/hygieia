package com.capitalone.dashboard.rest;

import com.capitalone.dashboard.config.TestConfig;
import com.capitalone.dashboard.config.WebMVCConfig;
import com.capitalone.dashboard.model.Comment;
import com.capitalone.dashboard.model.Commit;
import com.capitalone.dashboard.model.CommitType;
import com.capitalone.dashboard.model.GitRequest;
import com.capitalone.dashboard.request.PeerReviewRequest;
import com.capitalone.dashboard.service.AuditService;
import com.google.gson.Gson;
import org.bson.types.ObjectId;
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

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestConfig.class, WebMVCConfig.class})
@WebAppConfiguration
public class AuditControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext wac;
    @Autowired
    private AuditService auditService;

    @Before
    public void before() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
    }

    @Test
    public void performPeerReview() throws Exception {
        PeerReviewRequest request = new PeerReviewRequest();
        request.setRepo("http://test.git.com");
        request.setBranch("master");
        request.setBeginDate(1l);
        request.setEndDate(2l);

        List<GitRequest> gitRequests = new ArrayList<GitRequest>();
        GitRequest gitRequest = new GitRequest();
        gitRequest.setScmUrl("scmUrl");
        gitRequest.setScmRevisionNumber("revNum");
        gitRequest.setNumberOfChanges(20);
        gitRequest.setScmAuthor("bob");
        gitRequest.setTimestamp(2);
        gitRequest.setUserId("bobsid");
        List<Comment> comments = new ArrayList<Comment>();
        Comment comment = new Comment();
        comment.setBody("Some comment");
        comment.setUser("someuser");
        comments.add(comment);
        gitRequest.setComments(comments);

        gitRequest.setBaseSha("acd323e123abc323a123a");

        List<Comment> reviewComments = new ArrayList<Comment>();
        Comment reviewComment = new Comment();
        reviewComment.setBody("Some review comment");
        reviewComment.setUser("anotheruser");
        reviewComments.add(reviewComment);
        gitRequest.setReviewComments(reviewComments);

        gitRequests.add(gitRequest);

        List<Commit> baseCommits = new ArrayList<Commit>();
        Commit commit = new Commit();
        commit.setId(new ObjectId());
        commit.setType(CommitType.New);
        commit.setScmCommitLog("some commit log");
        baseCommits.add(commit);
        
        when(auditService.getCommitsBySha("acd323e123abc323a123a")).thenReturn(baseCommits);

        when(auditService.getPullRequests("http://test.git.com", "master", 1l, 2l)).thenReturn(gitRequests);
        mockMvc.perform(get("/peerReview" + "?repo=" + request.getRepo()
                + "&branch=" + request.getBranch()
                + "&beginDate=" + request.getBeginDate()
                + "&endDate=" + request.getEndDate())
                .contentType(MediaType.APPLICATION_JSON)
                .content(new Gson().toJson(request))).andExpect(status().isOk());

//        mockMvc.perform(get("/team/" + testTeamId + "?component=" + mockComponentId.toString() ))
//                .andExpect(status().isOk());

        /*
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
         */
    }
}