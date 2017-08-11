package com.capitalone.dashboard.rest;

import com.capitalone.dashboard.misc.HygieiaException;
import com.capitalone.dashboard.model.GitRequest;
import com.capitalone.dashboard.request.DashboardReviewRequest;
import com.capitalone.dashboard.request.JobReviewRequest;
import com.capitalone.dashboard.request.PeerReviewRequest;
import com.capitalone.dashboard.response.DashboardReviewResponse;
import com.capitalone.dashboard.response.JobReviewResponse;
import com.capitalone.dashboard.response.PeerReviewResponse;
import com.capitalone.dashboard.service.AuditService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RestController
public class AuditController {
    private final AuditService auditService;

    @Autowired
    public AuditController(AuditService auditService) {
        this.auditService = auditService;
    }

    /**
     * Dashboard review
     *     - Check which widgets are configured
     *     - Check whether repo and build point to same repository
     * @param request
     * @return
     * @throws HygieiaException
     */
    @RequestMapping(value = "/dashboardReview", method = GET, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<DashboardReviewResponse> dashboardReview(@Valid DashboardReviewRequest request) throws HygieiaException {
        DashboardReviewResponse dashboardReviewResponse = auditService.getDashboardReviewResponse(request.getTitle(), request.getType(),
                request.getBusServ(), request.getBusApp(),
                request.getBeginDate(), request.getEndDate());

        return ResponseEntity.ok().body(dashboardReviewResponse);
    }

    /**
     * Peer Review
     *     - Check commit author v/s who merged the pr
     *     - peer review of a pull request
     *     - check whether there are direct commits to base
     * @param request
     * @return
     */
    @RequestMapping(value = "/peerReview", method = GET, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Iterable<PeerReviewResponse>> peerReview(@Valid PeerReviewRequest request) {
        List<GitRequest> pullRequests = auditService.getPullRequests(request.getRepo(), request.getBranch(), request.getBeginDate(), request.getEndDate());
        List<PeerReviewResponse> allPeerReviews = auditService.getPeerReviewResponses(pullRequests);
        return ResponseEntity.ok().body(allPeerReviews);
    }

    /**
     * Build Job Review
     *     - Is job running on a Prod server
     *     - Is job inside a prod folder
     *     - Get config history
     * @param request
     * @return
     */
    @RequestMapping(value = "/buildJobReview", method = GET, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<JobReviewResponse> buildJobReview(@Valid JobReviewRequest request) {
        JobReviewResponse jobReviewResponse = auditService.getBuildJobReviewResponse(request.getJobUrl(), request.getJobName(), request.getBeginDate(), request.getEndDate());
        return ResponseEntity.ok().body(jobReviewResponse);
    }

}
