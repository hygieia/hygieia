package com.capitalone.dashboard.rest;

import com.capitalone.dashboard.request.PeerReviewRequest;
import com.capitalone.dashboard.response.PeerReviewResponse;
import com.capitalone.dashboard.service.PeerReviewAuditService;
import com.capitalone.dashboard.util.GitHubParsedUrl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Collection;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RestController
public class PeerReviewController {
    private final PeerReviewAuditService peerReviewAuditService;

    @Autowired
    public PeerReviewController(PeerReviewAuditService peerReviewAuditService) {

        this.peerReviewAuditService = peerReviewAuditService;
    }


    /**
     * Peer Review
     * - Check commit author v/s who merged the pr
     * - peer review of a pull request
     * - check whether there are direct commits to base
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/peerReview", method = GET, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Iterable<PeerReviewResponse>> peerReview(@Valid PeerReviewRequest request) {
        GitHubParsedUrl gitHubParsed = new GitHubParsedUrl(request.getRepo());
        String repoUrl = gitHubParsed.getUrl();
        Collection<PeerReviewResponse> allPeerReviews = peerReviewAuditService.getPeerReviewResponses(repoUrl, request.getBranch(), request.getScmName(), request.getBeginDate(), request.getEndDate());
        return ResponseEntity.ok().body(allPeerReviews);
    }

}

