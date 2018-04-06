package com.capitalone.dashboard.rest;

import com.capitalone.dashboard.model.AuditException;
import com.capitalone.dashboard.model.DashboardType;
import com.capitalone.dashboard.request.DashboardAuditRequest;
import com.capitalone.dashboard.response.DashboardReviewResponse;
import com.capitalone.dashboard.service.DashboardAuditService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import io.swagger.annotations.ApiOperation;

@RestController
public class DashboardAuditController {
    private final DashboardAuditService dashboardAuditService;

    @Autowired
    public DashboardAuditController(DashboardAuditService dashboardAuditService) {

		this.dashboardAuditService = dashboardAuditService;
	}

    /**
     * Dashboard review
     *     - Check which widgets are configured
     *     - Check whether repo and build point to same repository
     * @param request incoming request
     * @return response entity
     * @throws AuditException audit exception
     */
    @RequestMapping(value = "/dashboardReview", method = GET, produces = APPLICATION_JSON_VALUE)
	@ApiOperation(value = "Dashboard Review", notes = "This endpoint validates that your artifact is meeting the quality gate threshold established in Sonar and returns an appropriate audit status based on whether the threshold has been met or not.", response = DashboardReviewResponse.class, responseContainer = "List")
    public ResponseEntity<DashboardReviewResponse> dashboardReview(@Valid @RequestBody DashboardAuditRequest request) throws AuditException {
        DashboardReviewResponse dashboardReviewResponse = dashboardAuditService.getDashboardReviewResponse(request.getTitle(), DashboardType.Team,
                request.getBusinessService(), request.getBusinessApplication(),
                request.getBeginDate(), request.getEndDate(), request.getAuditType());

        return ResponseEntity.ok().body(dashboardReviewResponse);
    }
}

