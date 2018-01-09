package com.capitalone.dashboard.rest;

import com.capitalone.dashboard.request.CodeReviewAuditRequest;
import com.capitalone.dashboard.response.CodeReviewAuditResponse;
import com.capitalone.dashboard.service.CodeReviewAuditService;
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
public class CodeReviewAuditController {
    private final CodeReviewAuditService codeReviewAuditService;

    @Autowired
    public CodeReviewAuditController(CodeReviewAuditService codeReviewAuditService) {

        this.codeReviewAuditService = codeReviewAuditService;
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
    public ResponseEntity<Iterable<CodeReviewAuditResponse>> peerReview(@Valid CodeReviewAuditRequest request) {
        GitHubParsedUrl gitHubParsed = new GitHubParsedUrl(request.getRepo());
        String repoUrl = gitHubParsed.getUrl();
        Collection<CodeReviewAuditResponse> allPeerReviews = codeReviewAuditService.getPeerReviewResponses(repoUrl, request.getBranch(), request.getScmName(), request.getBeginDate(), request.getEndDate());
        return ResponseEntity.ok().body(allPeerReviews);
    }

}

