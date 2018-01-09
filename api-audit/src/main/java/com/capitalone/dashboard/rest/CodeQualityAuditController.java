package com.capitalone.dashboard.rest;

import com.capitalone.dashboard.misc.HygieiaException;
import com.capitalone.dashboard.request.CodeQualityAuditRequest;
import com.capitalone.dashboard.request.QualityProfileAuditRequest;
import com.capitalone.dashboard.response.QualityProfileAuditResponse;
import com.capitalone.dashboard.response.CodeQualityAuditResponse;
import com.capitalone.dashboard.service.CodeQualityAuditService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RestController
public class CodeQualityAuditController {
    private final CodeQualityAuditService codeQualityAuditService;

    @Autowired
    public CodeQualityAuditController(CodeQualityAuditService codeQualityAuditService) {

		this.codeQualityAuditService = codeQualityAuditService;
	}


	/**
	 * Code Quality Analysis - Has artifact met code quality gate threshold
	 *
	 * @param request
	 * @return
	 * @throws IOException
	 */

	@RequestMapping(value = "/staticCodeAnalysis", method = GET, produces = APPLICATION_JSON_VALUE)
	public ResponseEntity<List<CodeQualityAuditResponse>> staticCodeAnalysis(CodeQualityAuditRequest request)
			throws HygieiaException, IOException {

		List<CodeQualityAuditResponse> codeQualityAuditResponse;
		codeQualityAuditResponse = codeQualityAuditService.getCodeQualityAudit(request.getProjectName(), request.getArtifactVersion());
		return ResponseEntity.ok().body(codeQualityAuditResponse);
	}


	/**
	 * Code Quality Profile Validation for a business application - Has the code
	 * quality profile been changed by a user other than the commit author
	 *
	 * @param request
	 * @return
	 */

	@RequestMapping(value = "/codeQualityProfileValidation", method = GET, produces = APPLICATION_JSON_VALUE)
	public ResponseEntity<QualityProfileAuditResponse> codeQualityGateValidation(QualityProfileAuditRequest request)
			throws HygieiaException {

		QualityProfileAuditResponse codeQualityGateValidationResponse = codeQualityAuditService.getQualityGateValidationDetails(request.getRepo(),request.getBranch(),
				request.getProjectName(), request.getArtifactVersion(),
				request.getBeginDate(), request.getEndDate());

		return ResponseEntity.ok().body(codeQualityGateValidationResponse);
	}

}

