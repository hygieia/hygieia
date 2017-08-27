package com.capitalone.dashboard.service;

import com.capitalone.dashboard.model.Comment;
import com.capitalone.dashboard.model.Commit;
import com.capitalone.dashboard.model.CommitType;
import com.capitalone.dashboard.model.GitRequest;
import com.capitalone.dashboard.repository.CommitRepository;
import com.capitalone.dashboard.repository.GitRequestRepository;
import com.capitalone.dashboard.request.PeerReviewRequest;
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
public class AuditServiceTest {

    @Mock
    private GitRequestRepository gitRequestRepository;
    @Mock
    private CommitRepository commitRepository;

    @InjectMocks
    private AuditServiceImpl auditService;

    @Test
    public void shouldGetPullRequestsForRepoAndBranch() {

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

        when(gitRequestRepository.findByScmUrlAndScmBranchAndCreatedAtGreaterThanEqualAndMergedAtLessThanEqual(
                request.getRepo(), request.getBranch(), request.getBeginDate(), request.getEndDate())).thenReturn(gitRequests);
        assertTrue(gitRequests.contains(gitRequest));

    }

    @Test
    public void shouldGetCommitsBySha() {
        List<Commit> baseCommits = new ArrayList<Commit>();
        Commit commit = new Commit();
        commit.setId(new ObjectId());
        commit.setType(CommitType.New);
        commit.setScmCommitLog("some commit log");
        commit.setScmRevisionNumber("acd323e123abc323a123a");
        baseCommits.add(commit);

        when(commitRepository.findByScmRevisionNumber("acd323e123abc323a123a")).thenReturn(baseCommits);

        assertTrue(baseCommits.contains(commit));
    }
}
