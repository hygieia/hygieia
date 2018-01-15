package com.capitalone.dashboard.rest;

import com.capitalone.dashboard.config.TestConfig;
import com.capitalone.dashboard.config.WebMVCConfig;
import com.capitalone.dashboard.model.Comment;
import com.capitalone.dashboard.model.Commit;
import com.capitalone.dashboard.model.CommitType;
import com.capitalone.dashboard.model.GitRequest;
import com.capitalone.dashboard.repository.CustomRepositoryQuery;
import com.capitalone.dashboard.request.CodeReviewAuditRequest;
import com.capitalone.dashboard.service.DashboardAuditService;
import com.google.gson.Gson;
import org.bson.types.ObjectId;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestConfig.class, WebMVCConfig.class})
@WebAppConfiguration
public class DashboardAuditControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext wac;
    @Autowired
    private DashboardAuditService dashboardAuditService;

    @Mock
    private CustomRepositoryQuery customRepositoryQuery;

    @Before
    public void before() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
    }

    @Test
    public void performPeerReview() throws Exception {
        CodeReviewAuditRequest request = new CodeReviewAuditRequest();
        request.setRepo("http://test.git.com/capone/better.git");
        request.setBranch("master");
        request.setBeginDate(1L);
        request.setEndDate(2L);

        List<GitRequest> gitRequests = new ArrayList<>();
        GitRequest gitRequest = new GitRequest();
        gitRequest.setScmUrl("scmUrl");
        gitRequest.setScmRevisionNumber("revNum");
        gitRequest.setScmAuthor("bob");
        gitRequest.setTimestamp(2);
        gitRequest.setUserId("bobsid");
        List<Comment> comments = new ArrayList<>();
        Comment comment = new Comment();
        comment.setBody("Some comment");
        comment.setUser("someuser");
        comments.add(comment);
        gitRequest.setComments(comments);

        gitRequest.setBaseSha("acd323e123abc323a123a");

        gitRequests.add(gitRequest);

        List<Commit> baseCommits = new ArrayList<>();
        Commit commit = new Commit();
        commit.setId(new ObjectId());
        commit.setType(CommitType.New);
        commit.setScmCommitLog("some commit log");
        baseCommits.add(commit);
        
        //when(dashboardAuditService.getCommitsBySha("acd323e123abc323a123a")).thenReturn(baseCommits);

        mockMvc.perform(get("/peerReview" + "?repo=" + request.getRepo()
                + "&branch=" + request.getBranch()
                + "&beginDate=" + request.getBeginDate()
                + "&endDate=" + request.getEndDate())
                .contentType(MediaType.APPLICATION_JSON)
                .content(new Gson().toJson(request))).andExpect(status().isOk());

    }
    
//    @Test
//    public void performStaticCodeAnalysisReviewWithArtifactMetadata() throws Exception {
//
//    	// Request
//    	String projectName = "com.capitalone.dashboard:Hygieia";
//    	String artVersion = "2.0.6-SNAPSHOT";
//
//
//
//    	List<CodeQualityAuditResponse> responses = new ArrayList<>();
//    	CodeQualityAuditResponse response =  new CodeQualityAuditResponse();
//    	response.addAuditStatus(AuditStatus.CODE_QUALITY_AUDIT_OK);
//    	responses.add(response);
//
//    	when(dashboardAuditService.getCodeQualityAudit(projectName,artVersion)).thenReturn(responses);
//
//    	mockMvc.perform(get("/staticCodeAnalysis" + "?projectName=" + projectName
//        + "&artifactVersion=" + artVersion).contentType(MediaType.APPLICATION_JSON))
//    	.andExpect(status().isOk());
//
//    }
//
//
//    @Test
//    public void performQualityProfileValidation() throws Exception {
//    	// Request
//    	String repo = "http://test.git.com/capone/better.git";
//    	String branch = "master";
//    	String projectName = "com.capitalone.Hygiea:hygieia-api";
//    	String artifactVersion = "2.0.5-SNAPSHOT";
//    	long beginDate = 1478136705000L;
//    	long endDate = 1497465958000L;
//
//
//
//    	QualityProfileAuditRequest request = new QualityProfileAuditRequest();
//    	request.setRepo(repo);
//        request.setBranch(branch);
//    	request.setArtifactVersion(artifactVersion);
//    	request.setProjectName(projectName);
//    	request.setBeginDate(beginDate);
//    	request.setEndDate(endDate);
//
//    	//Response contents
//
//    	List<QualityProfileAuditResponse> responses = new ArrayList<>();
//    	QualityProfileAuditResponse response =  new QualityProfileAuditResponse();
//    	response.addAuditStatus(AuditStatus.CODE_QUALITY_AUDIT_GATE_MISSING);
//    	responses.add(response);
//
//    	when(dashboardAuditService.getQualityGateValidationDetails(request.getRepo(),request.getBranch(),projectName,artifactVersion,beginDate,endDate)).thenReturn(response);
//
//    	String requestUrl= "/codeQualityProfileValidation" + "?repo=" +repo +"&branch=" + branch + "&projectName=" + projectName  +
//    			"&artifactVersion=" + artifactVersion + "&beginDate=" + beginDate + "&endDate=" + endDate;
//
//    	mockMvc.perform(get(requestUrl).contentType(MediaType.APPLICATION_JSON))
//    	.andExpect(status().isOk());
//
//    }
//
//    @Test
//    public void validateTestResultExecution() throws Exception {
//    	// Request
//    	String jobUrl = "https://jenkins.Hygieia.com";
//    	long beginDate = 1478136705000L;
//    	long endDate = 1497465958000l;
//
//    	TestResultAuditRequest request = new TestResultAuditRequest();
//    	request.setBeginDate(beginDate);
//    	request.setEndDate(endDate);
//    	request.setJobUrl(jobUrl);
//
//    	TestResultsAuditResponse response = new TestResultsAuditResponse();
//
//    	when(dashboardAuditService.getTestResultAudit(jobUrl,beginDate,endDate)).thenReturn(response);
//
//    	String requestUrl= "/validateTestResults" + "?jobUrl=" + jobUrl +"&beginDate=" + beginDate + "&endDate=" + endDate;
//
//    	mockMvc.perform(get(requestUrl).contentType(MediaType.APPLICATION_JSON))
//    	.andExpect(status().isOk());
//    }
    
}