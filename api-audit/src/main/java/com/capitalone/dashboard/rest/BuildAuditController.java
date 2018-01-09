package com.capitalone.dashboard.rest;

import com.capitalone.dashboard.request.JobReviewRequest;
import com.capitalone.dashboard.response.JobReviewResponse;
import com.capitalone.dashboard.service.BuildAuditService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RestController
public class BuildAuditController {
    private final BuildAuditService buildAuditService;

    @Autowired
    public BuildAuditController(BuildAuditService buildAuditService) {

        this.buildAuditService = buildAuditService;
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
        JobReviewResponse jobReviewResponse = buildAuditService.getBuildJobReviewResponse(request.getJobUrl(), request.getJobName(), request.getBeginDate(), request.getEndDate());
        return ResponseEntity.ok().body(jobReviewResponse);
    }
    

}

