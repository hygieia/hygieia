package com.capitalone.dashboard.audit.rest;

import com.capitalone.dashboard.audit.model.AuditStatus;
import com.capitalone.dashboard.audit.model.PeerReviewResponse;
import com.capitalone.dashboard.audit.request.PeerReviewRequest;

import com.capitalone.dashboard.audit.service.AuditService;
import com.capitalone.dashboard.model.Comment;
import com.capitalone.dashboard.model.Commit;
import com.capitalone.dashboard.model.CommitType;

import com.capitalone.dashboard.model.GitRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RestController
@RequestMapping("/audit")
public class AuditController {
    private final AuditService auditService;

    @Autowired
    public AuditController(AuditService auditService) {
        this.auditService = auditService;
    }

    @SuppressWarnings({"PMD.NPathComplexity","PMD.ExcessiveMethodLength","PMD.AvoidBranchingStatementAsLastInLoop","PMD.EmptyIfStmt"})
    @RequestMapping(value = "/peerReview", method = GET, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Iterable<PeerReviewResponse>> peerReview(@Valid PeerReviewRequest request) {

        List<PeerReviewResponse> allPeerReviews = new ArrayList<PeerReviewResponse>();

        List<GitRequest> pullRequests = auditService.getPullRequests(request.getRepo(), request.getBranch(), request.getBeginDate(), request.getEndDate());

        for(GitRequest pr : pullRequests) {
            List commitsRelatedToPr = new ArrayList();
            String mergeSha = pr.getScmRevisionNumber();
            List<Commit> mergeCommits = auditService.getCommitsBySha(mergeSha);
            String mergeAuthor = "";
            for(Commit mergeCommit: mergeCommits) {
                List<String> relatedCommitShas = mergeCommit.getScmParentRevisionNumbers();
                mergeAuthor = mergeCommit.getScmAuthorLogin();
                for(String relatedCommitSha: relatedCommitShas) {
                    List<Commit> relatedCommits = auditService.getCommitsBySha(relatedCommitSha);
                    commitsRelatedToPr.addAll(relatedCommits);
                }
                break;
            }
            PeerReviewResponse peerReviewResponse = new PeerReviewResponse();
            peerReviewResponse.setPullRequest(pr);
            peerReviewResponse.setCommits(commitsRelatedToPr);

            //check for pr author <> pr merger
            String prAuthor = pr.getUserId();
            if (prAuthor.equalsIgnoreCase(mergeAuthor)) {
                peerReviewResponse.addAuditStatus(AuditStatus.COMMITAUTHOR_EQ_MERGECOMMITER);
            } else {
                peerReviewResponse.addAuditStatus(AuditStatus.COMMITAUTHOR_NE_MERGECOMMITER);
            }

            allPeerReviews.add(peerReviewResponse);

            //check to see if pr was reviewed
            List<Comment> comments = pr.getComments();
            boolean peerReviewed = false;
            for(Comment comment: comments) {
                if (!comment.getUser().equalsIgnoreCase(prAuthor)) {
                    peerReviewed = true;
                    break;
                }
            }
            List<Comment> reviewComments = pr.getReviewComments();
            for(Comment comment: reviewComments) {
                if (!comment.getUser().equalsIgnoreCase(prAuthor)) {
                    peerReviewed = true;
                    break;
                }
            }

            if (peerReviewed) {
                peerReviewResponse.addAuditStatus(AuditStatus.PULLREQ_REVIEWED_BY_PEER);
            } else {
                peerReviewResponse.addAuditStatus(AuditStatus.PULLREQ_NOT_PEER_REVIEWED);
            }

            //direct commit to master
            String baseSha = pr.getBaseSha();
            List<Commit> baseCommits = auditService.getCommitsBySha(baseSha);
            for(Commit baseCommit: baseCommits) {
                if (baseCommit.getType() == CommitType.New) {
                    peerReviewResponse.addAuditStatus(AuditStatus.DIRECT_COMMITS_TO_BASE);
                } else {
                    //merge commit
                }
            }

        }
        return ResponseEntity.ok().body(allPeerReviews);
    }

}
