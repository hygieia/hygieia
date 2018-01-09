package com.capitalone.dashboard.rest;

import com.capitalone.dashboard.misc.HygieiaException;
import com.capitalone.dashboard.request.PerformanceTestAuditRequest;
import com.capitalone.dashboard.request.TestResultAuditRequest;
import com.capitalone.dashboard.response.PerformaceTestAuditResponse;
import com.capitalone.dashboard.response.TestResultsResponse;
import com.capitalone.dashboard.service.TestResultAuditService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RestController
public class TestAuditController {
    private final TestResultAuditService testResultAuditService;

    @Autowired
    public TestAuditController(TestResultAuditService testResultAuditService) {

		this.testResultAuditService = testResultAuditService;
	}

	/**
	 * Test Result Validation for a business application - Has the code quality
	 * profile been changed by a user other than the commit author
	 *
	 * @param request
	 * @return
	 */

	@RequestMapping(value = "/validateTestResults", method = GET, produces = APPLICATION_JSON_VALUE)
	public ResponseEntity<TestResultsResponse> validatetestResultExecution(TestResultAuditRequest request)
			throws HygieiaException {

		TestResultsResponse testResultsResponse;

		testResultsResponse = testResultAuditService.getTestResultExecutionDetails(request.getJobUrl(),request.getBeginDate(),request.getEndDate());
		return ResponseEntity.ok().body(testResultsResponse);
	}

	@RequestMapping(value = "/validatePerfResults", method = GET, produces = APPLICATION_JSON_VALUE)
	public ResponseEntity validatePerfResultExecution(PerformanceTestAuditRequest request) {
		try {
				PerformaceTestAuditResponse performaceTestAuditResponse;
				performaceTestAuditResponse = testResultAuditService.getresultsBycomponetAndTime(request.getBusinessComponentName(), request.getRangeFrom(), request.getRangeTo());
				return ResponseEntity.ok().body(performaceTestAuditResponse);
		}catch (Exception e){
			return ResponseEntity.ok().body(request.getBusinessComponentName() + " is not a valid businessComp name or does not exists");
		}
	}
}

