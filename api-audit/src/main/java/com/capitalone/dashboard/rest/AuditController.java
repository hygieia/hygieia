package com.capitalone.dashboard.rest;

import com.capitalone.dashboard.misc.HygieiaException;
import com.capitalone.dashboard.model.AuditStatus;
import com.capitalone.dashboard.model.Commit;
import com.capitalone.dashboard.model.GitRequest;
import com.capitalone.dashboard.request.DashboardReviewRequest;
import com.capitalone.dashboard.request.JobReviewRequest;
import com.capitalone.dashboard.request.PeerReviewRequest;
import com.capitalone.dashboard.request.QualityProfileValidationRequest;
import com.capitalone.dashboard.request.StaticAnalysisRequest;
import com.capitalone.dashboard.request.TestExecutionValidationRequest;
import com.capitalone.dashboard.request.PerfReviewRequest;
import com.capitalone.dashboard.response.CodeQualityProfileValidationResponse;
import com.capitalone.dashboard.response.DashboardReviewResponse;
import com.capitalone.dashboard.response.JobReviewResponse;
import com.capitalone.dashboard.response.PeerReviewResponse;
import com.capitalone.dashboard.response.StaticAnalysisResponse;
import com.capitalone.dashboard.response.TestResultsResponse;
import com.capitalone.dashboard.response.PerfReviewResponse;
import com.capitalone.dashboard.service.AuditService;
import com.capitalone.dashboard.util.GitHubParsedUrl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.io.IOException;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RestController
public class AuditController {
    private final AuditService auditService;

	@Autowired
	public AuditController(AuditService auditService) {
		this.auditService = auditService;
	}


    private static final Logger LOGGER = LoggerFactory.getLogger(AuditController.class);



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
    public ResponseEntity<Iterable<PeerReviewResponse>> peerReview(@Valid PeerReviewRequest request)  {
        GitHubParsedUrl gitHubParsed = new GitHubParsedUrl(request.getRepo());
        String repoUrl = gitHubParsed.getUrl();

        LOGGER.warn("********************* repo " + repoUrl + " branch " + request.getBranch()
                + " " + request.getBeginDate() + " " + request.getEndDate());
        boolean isGitConfigured = auditService.isGitRepoConfigured(repoUrl,request.getBranch());
        if(!isGitConfigured){
            PeerReviewResponse peerReviewResponse = new PeerReviewResponse();
            peerReviewResponse.addAuditStatus(AuditStatus.REPO_NOT_CONFIGURED);
            return  ResponseEntity.ok().body(Stream.of(peerReviewResponse).collect(Collectors.toList()));
        }

        List<GitRequest> pullRequests = auditService.getPullRequests(repoUrl, request.getBranch(), request.getBeginDate(), request.getEndDate());
        List<Commit> commits = auditService.getCommits(repoUrl, request.getBranch(), request.getBeginDate(), request.getEndDate());
        List<PeerReviewResponse> allPeerReviews = auditService.getPeerReviewResponses(pullRequests, commits,request.getRepo(), request.getBranch(), request.getBeginDate(), request.getEndDate());
        return ResponseEntity.ok().body(allPeerReviews);
    }

//    @RequestMapping(value = "/allPeerReviews", method = GET, produces = APPLICATION_JSON_VALUE)
//    public ResponseEntity<Iterable<Iterable<PeerReviewResponse>>> allPeerReviews(@Valid PeerReviewRequest request)  {
//        List<CollectorItem> repos = auditService.getAllRepos();
//
//        List<Iterable<PeerReviewResponse>> allRepoPeerReviews = new ArrayList<>();
//        for(CollectorItem repo : repos) {
//            String repoUrl = (String)repo.getOptions().get("url");
//            String branch = (String)repo.getOptions().get("branch");
//
//            request.setRepo(repoUrl);
//            request.setBranch(branch);
//
//            ResponseEntity<Iterable<PeerReviewResponse>> allPeerReviews = this.peerReview(request);
//
//            allRepoPeerReviews.add(allPeerReviews.getBody());
//        }
//
//        return ResponseEntity.ok().body(allRepoPeerReviews);
//    }

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
    
	/**
	 * Code Quality Analysis - Has artifact met code quality gate threshold
	 * 
	 * @param request
	 * @return
	 * @throws IOException
	 */

	@RequestMapping(value = "/staticCodeAnalysis", method = GET, produces = APPLICATION_JSON_VALUE)
	public ResponseEntity<List<StaticAnalysisResponse>> staticCodeAnalysis(StaticAnalysisRequest request)
			throws HygieiaException, IOException {

		List<StaticAnalysisResponse> staticAnalysisResponse;
		staticAnalysisResponse = auditService.getCodeQualityAudit(request.getProjectName(), request.getArtifactVersion());
		return ResponseEntity.ok().body(staticAnalysisResponse);
	}

	
	/**
	 * Code Quality Profile Validation for a business application - Has the code
	 * quality profile been changed by a user other than the commit author
	 * 
	 * @param request
	 * @return
	 */

	@RequestMapping(value = "/codeQualityProfileValidation", method = GET, produces = APPLICATION_JSON_VALUE)
	public ResponseEntity<CodeQualityProfileValidationResponse> codeQualityGateValidation(QualityProfileValidationRequest request)
			throws HygieiaException {

		CodeQualityProfileValidationResponse codeQualityGateValidationResponse = null;

		codeQualityGateValidationResponse = auditService.getQualityGateValidationDetails(request.getRepo(),request.getBranch(),
				request.getProjectName(), request.getArtifactVersion(),
				request.getBeginDate(), request.getEndDate());
		return ResponseEntity.ok().body(codeQualityGateValidationResponse);
	}

	/**
	 * Test Result Validation for a business application - Has the code quality
	 * profile been changed by a user other than the commit author
	 * 
	 * @param request
	 * @return
	 */

	@RequestMapping(value = "/validateTestResults", method = GET, produces = APPLICATION_JSON_VALUE)
	public ResponseEntity<TestResultsResponse> validatetestResultExecution(TestExecutionValidationRequest request)
			throws HygieiaException {

		TestResultsResponse testResultsResponse;

		testResultsResponse = auditService.getTestResultExecutionDetails(request.getJobUrl(),request.getBeginDate(),request.getEndDate());
		return ResponseEntity.ok().body(testResultsResponse);
	}

	@RequestMapping(value = "/validatePerfResults", method = GET, produces = APPLICATION_JSON_VALUE)
	public ResponseEntity<PerfReviewResponse> validatePerfResultExecution(PerfReviewRequest request)
			throws HygieiaException {

		PerfReviewResponse perfReviewResponse;

		perfReviewResponse = auditService.getresultsBycomponetAndTime(request.getBusinessComponentName(),request.getRangeFrom(),request.getRangeTo());
		return ResponseEntity.ok().body(perfReviewResponse);
	}

}

