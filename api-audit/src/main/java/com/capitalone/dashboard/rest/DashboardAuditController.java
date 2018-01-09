package com.capitalone.dashboard.rest;

import com.capitalone.dashboard.misc.HygieiaException;
import com.capitalone.dashboard.request.DashboardReviewRequest;
import com.capitalone.dashboard.response.DashboardReviewResponse;
import com.capitalone.dashboard.service.DashboardAuditService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

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
     * @throws HygieiaException
     */
    @RequestMapping(value = "/dashboardReview", method = GET, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<DashboardReviewResponse> dashboardReview(@Valid DashboardReviewRequest request) throws HygieiaException {
        DashboardReviewResponse dashboardReviewResponse = dashboardAuditService.getDashboardReviewResponse(request.getTitle(), request.getType(),
                request.getBusServ(), request.getBusApp(),
                request.getBeginDate(), request.getEndDate());

        return ResponseEntity.ok().body(dashboardReviewResponse);
    }
}

