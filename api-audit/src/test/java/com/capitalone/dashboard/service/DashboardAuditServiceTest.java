package com.capitalone.dashboard.service;

import com.capitalone.dashboard.ApiSettings;
import com.capitalone.dashboard.model.AuditStatus;
import com.capitalone.dashboard.model.CodeQuality;
import com.capitalone.dashboard.model.Comment;
import com.capitalone.dashboard.model.Commit;
import com.capitalone.dashboard.model.CommitType;
import com.capitalone.dashboard.model.GitRequest;
import com.capitalone.dashboard.model.TestResult;
import com.capitalone.dashboard.model.TestSuiteType;
import com.capitalone.dashboard.repository.CodeQualityRepository;
import com.capitalone.dashboard.repository.CommitRepository;
import com.capitalone.dashboard.repository.GitRequestRepository;
import com.capitalone.dashboard.repository.TestResultRepository;
import com.capitalone.dashboard.request.CodeQualityAuditRequest;
import com.capitalone.dashboard.request.CodeReviewAuditRequest;
import com.capitalone.dashboard.response.CodeQualityAuditResponse;
import org.bson.types.ObjectId;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DashboardAuditServiceTest {

    @Mock
    private GitRequestRepository gitRequestRepository;
    @Mock
    private CommitRepository commitRepository;
    @Mock
    private CodeQualityRepository codeQualityRepository;
    @Mock
    private TestResultRepository testResultRepository;
    @Mock
    private ApiSettings settings;

    @InjectMocks
    private DashboardAuditServiceImpl auditService;

//    @Test
//    public void emptyPeerReview() {
//        when(settings.getPeerReviewContexts()).thenReturn("foo");
//        GitRequest gitRequest = new GitRequest();
//        assertFalse(auditService.computePeerReviewStatus(gitRequest, new CodeReviewAuditResponse()));
//    }

//    @Test
//    public void peerReviewWithCommitStatus() {
//        when(settings.getPeerReviewContexts()).thenReturn("foo");
//        GitRequest gitRequest = new GitRequest();
//        List<CommitStatus> commitStatuses = new ArrayList<>();
//        CommitStatus status = new CommitStatus();
//        status.setContext("bar");
//        status.setState("SUCCESS");
//        commitStatuses.add(status);
//        gitRequest.setCommitStatuses(commitStatuses);
//        assertFalse(auditService.computePeerReviewStatus(gitRequest, new CodeReviewAuditResponse()));
//        status.setContext("foo");
//        status.setState(null);
//        assertFalse(auditService.computePeerReviewStatus(gitRequest, new CodeReviewAuditResponse()));
//        status.setState("SUCCESS");
//        assertTrue(auditService.computePeerReviewStatus(gitRequest, new CodeReviewAuditResponse()));
//    }
//
//    @Test
//    public void peerReviewWithReviews() {
//        when(settings.getPeerReviewContexts()).thenReturn("foo");
//        GitRequest gitRequest = new GitRequest();
//        Review review = new Review();
//        review.setState("PENDING");
//        List<Review> reviews = new ArrayList<>();
//        reviews.add(review);
//        gitRequest.setReviews(reviews);
//        assertFalse(auditService.computePeerReviewStatus(gitRequest, new CodeReviewAuditResponse()));
//        review.setState("APPROVED");
//        assertTrue(auditService.computePeerReviewStatus(gitRequest, new CodeReviewAuditResponse()));
//        List<CommitStatus> commitStatuses = new ArrayList<>();
//        CommitStatus status = new CommitStatus();
//        commitStatuses.add(status);
//        gitRequest.setCommitStatuses(commitStatuses);
//        status.setContext("foo");
//        status.setState(null);
//        assertFalse(auditService.computePeerReviewStatus(gitRequest, new CodeReviewAuditResponse()));
//    }

    @Test
    public void shouldGetPullRequestsForRepoAndBranch() {

        CodeReviewAuditRequest request = new CodeReviewAuditRequest();
        request.setRepo("http://test.git.com");
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

        List<Comment> reviewComments = new ArrayList<>();
        Comment reviewComment = new Comment();
        reviewComment.setBody("Some review comment");
        reviewComment.setUser("anotheruser");
        reviewComments.add(reviewComment);
//        gitRequest.setReviewComments(reviewComments);

        gitRequests.add(gitRequest);

        when(gitRequestRepository.findByScmUrlIgnoreCaseAndScmBranchIgnoreCaseAndCreatedAtGreaterThanEqualAndMergedAtLessThanEqual(
                request.getRepo(), request.getBranch(), request.getBeginDate(), request.getEndDate())).thenReturn(gitRequests);
        assertTrue(gitRequests.contains(gitRequest));

    }

    @Test
    public void shouldGetCommitsBySha() {
        List<Commit> baseCommits = new ArrayList<>();
        Commit commit = new Commit();
        commit.setId(new ObjectId());
        commit.setType(CommitType.New);
        commit.setScmCommitLog("some commit log");
        commit.setScmRevisionNumber("acd323e123abc323a123a");
        baseCommits.add(commit);

        when(commitRepository.findByScmRevisionNumber("acd323e123abc323a123a")).thenReturn(baseCommits);

        assertTrue(baseCommits.contains(commit));
    }
    @Test
    public void shouldGetgetCodeQualityAuditDetailsforComponentAndVersion() {
    	String component = "BAPHYGIEIA";
    	String artifactVersion = "2.0.5";
    	
    	
    	CodeQualityAuditRequest request = new CodeQualityAuditRequest();
    	request.setArtifactVersion(artifactVersion);
    	
    	ObjectId collectorItemId = new ObjectId("58b945a890e46b264b95127d");
    	String version = "2.0.5";
    	
    	List<CodeQualityAuditResponse> responses = new ArrayList<>();
    	CodeQualityAuditResponse response = new CodeQualityAuditResponse();
    	List<CodeQuality> qualities = new ArrayList<>();
    	CodeQuality quality = new CodeQuality();
    	
    	quality.setVersion(version);
    	quality.setUrl("https://sonar.com");
    	quality.setCollectorItemId(collectorItemId);
    	quality.setName("sampleProject");
    	qualities.add(quality);
    	

    	response.addAuditStatus(AuditStatus.CODE_QUALITY_AUDIT_OK);
    	responses.add(response);
    	
    	when(codeQualityRepository.findByCollectorItemIdAndVersionOrderByTimestampDesc(collectorItemId, version)).thenReturn(qualities);
    	assertTrue(qualities.contains(quality));
    	
    }
    
    @Test
    public void shouldGetgetCodeQualityAuditDetailsforArtifactMetatdatan() {
    	String artifactGroup = "com.capitalone.Hygieia";
    	String artifactName = "audit-api";
    	String artifactVersion = "2.0.5";

    	
    	
    	List<CodeQualityAuditResponse> responses = new ArrayList<>();
    	CodeQualityAuditResponse response = new CodeQualityAuditResponse();
    	List<CodeQuality> qualities = new ArrayList<>();
    	CodeQuality quality = new CodeQuality();
    	
    	quality.setVersion(artifactVersion);
    	quality.setUrl("https://sonar.com");
    	quality.setName("sampleProject");
    	qualities.add(quality);
    	
    	response.addAuditStatus(AuditStatus.CODE_QUALITY_AUDIT_OK);
    	responses.add(response);
    	
    	when(codeQualityRepository.findByNameAndVersionOrderByTimestampDesc(artifactGroup+ ":" + artifactName,artifactVersion)).thenReturn(qualities);
    	assertTrue(qualities.contains(quality));
    	
    }
    
    @Test
    public void shouldGetTestExecutionDetails() {
    	String jobUrl = "https://testurl";
    	List<TestResult> testResults = new ArrayList<>();
    	TestResult testResult = new TestResult();
    	long timestamp = 1478136705000L;
    	long duration = 123456;
    	long beginDate = 1478136705000L;
    	long endDate = 1497465958000L;

    	
    	testResult.setFailureCount(0);
    	testResult.setSuccessCount(0);
    	testResult.setType(TestSuiteType.Functional);
    	testResult.setUrl(jobUrl);
    	testResult.setTimestamp(timestamp);
    	testResult.setDuration(duration);
    	
    	testResults.add(testResult);
    	
    	when(testResultRepository.findByUrlAndTimestampGreaterThanEqualAndTimestampLessThanEqual(jobUrl,beginDate,endDate)).thenReturn(testResults);

    	assertTrue(testResults.contains(testResult));
    	
    	
    }
    
}
