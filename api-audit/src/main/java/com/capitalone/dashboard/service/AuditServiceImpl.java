package com.capitalone.dashboard.service;

import com.capitalone.dashboard.ApiSettings;
import com.capitalone.dashboard.misc.HygieiaException;
import com.capitalone.dashboard.model.AuditStatus;
import com.capitalone.dashboard.model.Build;
import com.capitalone.dashboard.model.Cmdb;
import com.capitalone.dashboard.model.CodeQuality;
import com.capitalone.dashboard.model.CodeQualityMetric;
import com.capitalone.dashboard.model.CollItemCfgHist;
import com.capitalone.dashboard.model.Collector;
import com.capitalone.dashboard.model.CollectorItem;
import com.capitalone.dashboard.model.CollectorType;
import com.capitalone.dashboard.model.Comment;
import com.capitalone.dashboard.model.Commit;
import com.capitalone.dashboard.model.CommitStatus;
import com.capitalone.dashboard.model.CommitType;
import com.capitalone.dashboard.model.Dashboard;
import com.capitalone.dashboard.model.GitRequest;
import com.capitalone.dashboard.model.JobCollectorItem;
import com.capitalone.dashboard.model.PerfIndicators;
import com.capitalone.dashboard.model.PerfTest;
import com.capitalone.dashboard.model.RepoBranch;
import com.capitalone.dashboard.model.Review;
import com.capitalone.dashboard.model.SCM;
import com.capitalone.dashboard.model.TestCapability;
import com.capitalone.dashboard.model.TestCase;
import com.capitalone.dashboard.model.TestCaseStep;
import com.capitalone.dashboard.model.TestResult;
import com.capitalone.dashboard.model.TestSuite;
import com.capitalone.dashboard.model.Widget;
import com.capitalone.dashboard.repository.BuildRepository;
import com.capitalone.dashboard.repository.CmdbRepository;
import com.capitalone.dashboard.repository.CodeQualityRepository;
import com.capitalone.dashboard.repository.CollItemCfgHistRepository;
import com.capitalone.dashboard.repository.CollectorItemRepository;
import com.capitalone.dashboard.repository.CollectorRepository;
import com.capitalone.dashboard.repository.CommitRepository;
import com.capitalone.dashboard.repository.ComponentRepository;
import com.capitalone.dashboard.repository.CustomRepositoryQuery;
import com.capitalone.dashboard.repository.DashboardRepository;
import com.capitalone.dashboard.repository.GitRequestRepository;
import com.capitalone.dashboard.repository.JobRepository;
import com.capitalone.dashboard.repository.TestResultRepository;
import com.capitalone.dashboard.response.CodeQualityProfileValidationResponse;
import com.capitalone.dashboard.response.DashboardReviewResponse;
import com.capitalone.dashboard.response.JobReviewResponse;
import com.capitalone.dashboard.response.PeerReviewResponse;
import com.capitalone.dashboard.response.PerfReviewResponse;
import com.capitalone.dashboard.response.StaticAnalysisResponse;
import com.capitalone.dashboard.response.TestResultsResponse;
import com.capitalone.dashboard.util.GitHubParsedUrl;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Sets;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.collections4.SetUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class AuditServiceImpl implements AuditService {

    private GitRequestRepository gitRequestRepository;
    private final CommitRepository commitRepository;
    private final CustomRepositoryQuery customRepositoryQuery;
    private JobRepository jobRepository;
    private CollectorRepository collectorRepository;
    private CollItemCfgHistRepository collItemCfgHistRepository;
    private DashboardRepository dashboardRepository;
    private CmdbRepository cmdbRepository;
    private ComponentRepository componentRepository;
    private BuildRepository buildRepository;
    private CollectorItemRepository collectorItemRepository;
    private CodeQualityRepository codeQualityRepository;
    private TestResultRepository testResultRepository;
    private ApiSettings settings;

    private static final Log LOGGER = LogFactory.getLog(AuditServiceImpl.class);

    @Autowired
    public AuditServiceImpl(GitRequestRepository gitRequestRepository, CommitRepository commitRepository,
                            CustomRepositoryQuery customRepositoryQuery,
                            JobRepository jobRepository, CollectorRepository collectorRepository,
                            CollItemCfgHistRepository collItemCfgHistRepository,
                            DashboardRepository dashboardRepository,
                            CmdbRepository cmdbRepository,
                            ComponentRepository componentRepository,
                            BuildRepository buildRepository,
                            CollectorItemRepository collectorItemRepository,
                            CodeQualityRepository codeQualityRepository,
                            TestResultRepository testResultRepository,
                            ApiSettings settings) {
        this.gitRequestRepository = gitRequestRepository;
        this.commitRepository = commitRepository;
        this.customRepositoryQuery = customRepositoryQuery;
        this.jobRepository = jobRepository;
        this.collectorRepository = collectorRepository;
        this.collItemCfgHistRepository = collItemCfgHistRepository;
        this.dashboardRepository = dashboardRepository;
        this.cmdbRepository = cmdbRepository;
        this.componentRepository = componentRepository;
        this.buildRepository = buildRepository;
        this.collectorItemRepository = collectorItemRepository;
        this.codeQualityRepository = codeQualityRepository;
        this.testResultRepository = testResultRepository;
        this.settings = settings;
    }

//    public List<CollectorItem> getAllRepos() {
//        Collector githubCollector = collectorRepository.findByName("GitHub");
//        List<CollectorItem> collectorItems = collectorItemRepository.findAllRepos(githubCollector.getId(), true);
//
//        return collectorItems;
//    }


    private class GenericAuditResponse {

        private Set<AuditStatus> auditStatuses = EnumSet.noneOf(AuditStatus.class);

        private Map<String, Object> response = new HashMap<>();

        protected static final String PULL_REQUESTS = "pull_request";

        protected static final String CODE_REVIEW = "code_review";
        protected static final String JOB_REVIEW = "job_review";
        protected static final String JOB_CONFIG_REVIEW = "job_config_review";
        protected static final String STATIC_CODE_REVIEW = "static_code_review";
        protected static final String STATIC_CODE_CONFIG_REVIEW = "static_code_config_review";

        public Set<AuditStatus> getAuditStatuses() {
            return auditStatuses;
        }

        public void addAuditStatus(AuditStatus status) {
            auditStatuses.add(status);
        }

        public void setAuditStatuses(Set<AuditStatus> auditStatuses) {
            this.auditStatuses = auditStatuses;
        }

        public Object getResponse(String name) {
            return response.get(name);
        }

        public void addResponse(String name, Object object) {
            response.put(name, object);
        }
    }


    /**
     * Calculates audit response for a given dashboard
     *
     * @param title
     * @param type
     * @param busServ
     * @param busApp
     * @param beginDate
     * @param endDate
     * @return @DashboardReviewResponse for a given dashboard
     * @throws HygieiaException
     */
    public DashboardReviewResponse getDashboardReviewResponse(String title, String type, String busServ, String
            busApp, long beginDate, long endDate) throws HygieiaException {
        Dashboard dashboard = getDashboard(title, type, busServ, busApp);

        DashboardReviewResponse dashboardReviewResponse = new DashboardReviewResponse();
        if (dashboard == null) {
            dashboardReviewResponse.addAuditStatus(AuditStatus.DASHBOARD_NOT_REGISTERED);
            return dashboardReviewResponse;
        }

        dashboardReviewResponse.setDashboardTitle(dashboard.getTitle());

        //Code Review Audit
        GenericAuditResponse codeReviewResponse = getCodeReviewAudit(dashboard, beginDate, endDate);
        dashboardReviewResponse.addAllAuditStatus(codeReviewResponse.getAuditStatuses());
        List<List<PeerReviewResponse>> peerReviewsAudit = (List<List<PeerReviewResponse>>) codeReviewResponse.getResponse(GenericAuditResponse.CODE_REVIEW);
        dashboardReviewResponse.setAllPeerReviewResponses(peerReviewsAudit);


        //Get the pull requests list back
        List<GitRequest> pullRequests = peerReviewsAudit.stream().flatMap(List::stream).map(PeerReviewResponse::getPullRequest).collect(Collectors.toList());

        //Build Audit
        GenericAuditResponse buildGenericAuditResponse = getBuildJobAuditResponse(dashboard, beginDate, endDate, pullRequests);
        dashboardReviewResponse.addAllAuditStatus(buildGenericAuditResponse.getAuditStatuses());
        dashboardReviewResponse.setJobReviewResponse((JobReviewResponse) buildGenericAuditResponse.getResponse(GenericAuditResponse.JOB_REVIEW));

        //Code Quality Audit
        GenericAuditResponse codeQualityGenericAuditResponse = getCodeQualityAuditResponse(dashboard);
        dashboardReviewResponse.addAllAuditStatus(codeQualityGenericAuditResponse.getAuditStatuses());
        dashboardReviewResponse.setStaticAnalysisResponse((StaticAnalysisResponse) codeQualityGenericAuditResponse.getResponse(GenericAuditResponse.STATIC_CODE_REVIEW));

        List<CollectorItem> testItems = this.getCollectorItems(dashboard, "test", CollectorType.Test);

        if (testItems != null && !testItems.isEmpty()) {
            dashboardReviewResponse.addAuditStatus(AuditStatus.DASHBOARD_TEST_CONFIGURED);
            testItems.stream().map(testItem -> getTestResults((String) testItem.getOptions().get("jobUrl"), beginDate, endDate)).map(this::regressionTestResultAudit).forEach(dashboardReviewResponse::setTestResultsResponse);

        } else {
            dashboardReviewResponse.addAuditStatus(AuditStatus.DASHBOARD_TEST_NOT_CONFIGURED);
        }
        return dashboardReviewResponse;
    }



    /**
     * Calculates code review audit status for a given @Dashboard
     *
     * @param dashboard
     * @param beginDate
     * @param endDate
     * @return @GenericAuditResponse for a given @Dashboard
     */
    private GenericAuditResponse getCodeReviewAudit(Dashboard dashboard, long beginDate, long endDate) {
        GenericAuditResponse genericAuditResponse = new GenericAuditResponse();
        List<CollectorItem> repoItems = this.getCollectorItems(dashboard, "repo", CollectorType.SCM);
        if (CollectionUtils.isEmpty(repoItems)) {
            genericAuditResponse.addAuditStatus(AuditStatus.DASHBOARD_REPO_NOT_CONFIGURED);
            return genericAuditResponse;
        }
        genericAuditResponse.addAuditStatus(AuditStatus.DASHBOARD_REPO_CONFIGURED);
        List<List<PeerReviewResponse>> allReviews = new ArrayList<>();

        for (CollectorItem repoItem : repoItems) {
            String scmWidgetbranch = (String) repoItem.getOptions().get("branch");
            String scmWidgetrepoUrl = (String) repoItem.getOptions().get("url");
            GitHubParsedUrl gitHubParsed = new GitHubParsedUrl(scmWidgetrepoUrl);
            scmWidgetrepoUrl = gitHubParsed.getUrl();
            if (!StringUtils.isEmpty(scmWidgetbranch) && !StringUtils.isEmpty(scmWidgetrepoUrl)) {
                List<PeerReviewResponse> reviewResponses = this.getPeerReviewResponses(repoItem, beginDate, endDate);
                allReviews.add(reviewResponses);
            }
        }
        genericAuditResponse.addResponse(GenericAuditResponse.CODE_REVIEW, allReviews);
        return genericAuditResponse;

    }


    /**
     * Calculates peer review response
     *
     * @param repoUrl
     * @param repoBranch
     * @param scmName
     * @param beginDt
     * @param endDt
     * @return
     */
    @Override
    public List<PeerReviewResponse> getPeerReviewResponses(String repoUrl, String repoBranch, String scmName,
                                                           long beginDt, long endDt) {
        LOGGER.info("********************* repo " + repoUrl + " branch " + repoBranch
                + " " + beginDt + " " + endDt);
        Collector githubCollector = collectorRepository.findByName(!StringUtils.isEmpty(scmName) ? scmName : "GitHub");
        CollectorItem collectorItem = collectorItemRepository.findRepoByUrlAndBranch(githubCollector.getId(),
                repoUrl, repoBranch, true);

        return getPeerReviewResponses(collectorItem, beginDt, endDt);
    }


    /**
     * Calculates peer review response
     *
     * @param repoItem
     * @param beginDt
     * @param endDt
     * @return
     */

    @Override
    public List<PeerReviewResponse> getPeerReviewResponses(CollectorItem repoItem,
                                                           long beginDt, long endDt) {

        List<PeerReviewResponse> allPeerReviews = new ArrayList<>();

        if (repoItem == null) {
            PeerReviewResponse peerReviewResponse = new PeerReviewResponse();
            peerReviewResponse.addAuditStatus(AuditStatus.REPO_NOT_CONFIGURED);
            allPeerReviews.add(peerReviewResponse);
            return allPeerReviews;
        }

        String scmUrl = (String) repoItem.getOptions().get("url");
        String scmBranch = (String) repoItem.getOptions().get("branch");

        if (!CollectionUtils.isEmpty(repoItem.getErrors())) {
            PeerReviewResponse noPRsPeerReviewResponse = new PeerReviewResponse();
            noPRsPeerReviewResponse.addAuditStatus(AuditStatus.COLLECTOR_ITEM_ERROR);

            noPRsPeerReviewResponse.setLastUpdated(repoItem.getLastUpdated());
            noPRsPeerReviewResponse.setScmBranch(scmBranch);
            noPRsPeerReviewResponse.setScmUrl(scmUrl);
            noPRsPeerReviewResponse.setErrorMessage(repoItem.getErrors().get(0).getErrorMessage());
            allPeerReviews.add(noPRsPeerReviewResponse);
            return allPeerReviews;
        }

        List<GitRequest> pullRequests = customRepositoryQuery.findByScmUrlIgnoreCaseAndScmBranchIgnoreCaseAndMergedAtGreaterThanEqualAndMergedAtLessThanEqual(scmUrl, scmBranch, beginDt, endDt);
        List<Commit> commits = customRepositoryQuery.findByScmUrlAndScmBranchAndScmCommitTimestampGreaterThanEqualAndScmCommitTimestampLessThanEqual(scmUrl, scmBranch, beginDt, endDt);

        if (CollectionUtils.isEmpty(pullRequests)) {
            PeerReviewResponse noPRsPeerReviewResponse = new PeerReviewResponse();
            noPRsPeerReviewResponse.addAuditStatus(AuditStatus.NO_PULL_REQ_FOR_DATE_RANGE);
            allPeerReviews.add(noPRsPeerReviewResponse);
        }

        for (GitRequest pr : pullRequests) {
            PeerReviewResponse peerReviewResponse = new PeerReviewResponse();
            peerReviewResponse.setPullRequest(pr);
            String mergeSha = pr.getScmRevisionNumber();
            Optional<Commit> mergeOptionalCommit = commits.stream().filter(c -> Objects.equals(c.getScmRevisionNumber(), mergeSha)).findFirst();
            Commit mergeCommit = mergeOptionalCommit.orElse(null);
//            Commit mergeCommit = commitRepository.findByScmRevisionNumberAndScmUrlIgnoreCase(mergeSha, pr.getScmUrl());
            if (mergeCommit == null) {
                continue;
            }

            List<Commit> commitsRelatedToPr = pr.getCommits();
            commitsRelatedToPr.sort(Comparator.comparing(e -> (e.getScmCommitTimestamp())));

            //check for pr author <> pr merger
            peerReviewResponse.addAuditStatus(pr.getUserId().equalsIgnoreCase(mergeCommit.getScmAuthorLogin()) ? AuditStatus.COMMITAUTHOR_EQ_MERGECOMMITER : AuditStatus.COMMITAUTHOR_NE_MERGECOMMITER);
            peerReviewResponse.setCommits(commitsRelatedToPr);
            //check to see if pr was reviewed
            boolean peerReviewed = computePeerReviewStatus(pr, peerReviewResponse);

            peerReviewResponse.addAuditStatus(peerReviewed ? AuditStatus.PULLREQ_REVIEWED_BY_PEER : AuditStatus.PULLREQ_NOT_PEER_REVIEWED);

            //type of branching strategy
            String sourceRepo = pr.getSourceRepo();
            String targetRepo = pr.getTargetRepo();
            peerReviewResponse.addAuditStatus(sourceRepo == null ? AuditStatus.GIT_FORK_STRATEGY : sourceRepo.equalsIgnoreCase(targetRepo) ? AuditStatus.GIT_BRANCH_STRATEGY : AuditStatus.GIT_FORK_STRATEGY);
            allPeerReviews.add(peerReviewResponse);
        }

        //check any commits not directly tied to pr
        PeerReviewResponse peerReviewResponse = new PeerReviewResponse();
        List<Commit> commitsNotDirectlyTiedToPr = new ArrayList<>();
        commits.forEach(commit -> {
            if (StringUtils.isEmpty(commit.getPullNumber()) && commit.getType() == CommitType.New) {
                commitsNotDirectlyTiedToPr.add(commit);
                peerReviewResponse.addAuditStatus(commit.isFirstEverCommit() ? AuditStatus.DIRECT_COMMITS_TO_BASE_FIRST_COMMIT : AuditStatus.DIRECT_COMMITS_TO_BASE);
            }
        });
        if (!commitsNotDirectlyTiedToPr.isEmpty()) {
            peerReviewResponse.setCommits(commitsNotDirectlyTiedToPr);
            allPeerReviews.add(peerReviewResponse);
        }

        //pull requests in date range, but merged prior to 14 days so no commits available in hygieia
        if (!CollectionUtils.isEmpty(pullRequests)) {
            if (allPeerReviews.isEmpty()) {
                PeerReviewResponse prsButNoCommitsInRangePeerReviewResponse = new PeerReviewResponse();
                prsButNoCommitsInRangePeerReviewResponse.addAuditStatus(AuditStatus.NO_PULL_REQ_FOR_DATE_RANGE);
                allPeerReviews.add(prsButNoCommitsInRangePeerReviewResponse);
            }
        }

        allPeerReviews.forEach(peerReviewResponseList -> {
            peerReviewResponseList.setLastUpdated(repoItem.getLastUpdated());
            peerReviewResponseList.setScmBranch(scmBranch);
            peerReviewResponseList.setScmUrl(scmUrl);
        });
        return allPeerReviews;
    }

    /**
     * Calculates the peer review status for a given pull request
     *
     * @param pr                 - pull request
     * @param peerReviewResponse
     * @return
     */
    boolean computePeerReviewStatus(GitRequest pr, PeerReviewResponse peerReviewResponse) {
        List<Review> reviews = pr.getReviews();

        List<CommitStatus> statuses = pr.getCommitStatuses();

        if (!CollectionUtils.isEmpty(statuses)) {
            String contextString = settings.getPeerReviewContexts();
            Set<String> prContexts = StringUtils.isEmpty(contextString) ? new HashSet<>() : Sets.newHashSet(contextString.trim().split(","));

            boolean lgtmAttempted = false;
            boolean lgtmStateResult = false;
            for (CommitStatus status : statuses) {
                if (status.getContext() != null && prContexts.contains(status.getContext())) {
                    //review done using LGTM workflow assuming its in the settings peerReviewContexts
                    lgtmAttempted = true;
                    String stateString = (status.getState() != null) ? status.getState().toLowerCase() : "unknown";
                    switch (stateString) {
                        case "pending":
                            peerReviewResponse.addAuditStatus(AuditStatus.PEER_REVIEW_LGTM_PENDING);
                            break;

                        case "error":
                            peerReviewResponse.addAuditStatus(AuditStatus.PEER_REVIEW_LGTM_PENDING);
                            break;

                        case "success":
                            lgtmStateResult = true;
                            peerReviewResponse.addAuditStatus(AuditStatus.PEER_REVIEW_LGTM_SUCCESS);
                            break;

                        default:
                            peerReviewResponse.addAuditStatus(AuditStatus.PEER_REVIEW_LGTM_UNKNOWN);
                            break;
                    }
                }
            }

            if (lgtmAttempted) {
                //if lgtm self-review, then no peer-review was done unless someone else looked at it
                if (!CollectionUtils.isEmpty(peerReviewResponse.getAuditStatuses()) &&
                        peerReviewResponse.getAuditStatuses().contains(AuditStatus.COMMITAUTHOR_EQ_MERGECOMMITER) &&
                        !isPRLookedAtByPeer(pr)) {
                    peerReviewResponse.addAuditStatus(AuditStatus.PEER_REVIEW_LGTM_SELF_APPROVAL);
                    return false;
                }
                return lgtmStateResult;
            }
        }


        if (!CollectionUtils.isEmpty(reviews)) {
            for (Review review : reviews) {
                if ("approved".equalsIgnoreCase(review.getState())) {
                    //review done using GitHub Review workflow
                    peerReviewResponse.addAuditStatus(AuditStatus.PEER_REVIEW_GHR);
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Calculates if the PR was looked at by a peer
     * @param pr
     * @return true if PR was looked at by at least one peer
     */
    private boolean isPRLookedAtByPeer(GitRequest pr) {
        Set<String> commentUsers = pr.getComments() != null ? pr.getComments().stream().map(Comment::getUser).collect(Collectors.toCollection(HashSet::new)) : new HashSet<>();
        Set<String> reviewAuthors = pr.getReviews() != null ? pr.getReviews().stream().map(Review::getAuthor).collect(Collectors.toCollection(HashSet::new)) : new HashSet<>();
        commentUsers.remove(pr.getUserId());
        reviewAuthors.remove(pr.getUserId());

        return !CollectionUtils.isEmpty(pr.getReviews()) || (commentUsers.size() > 0) || (reviewAuthors.size() > 0);
    }



    /**
     * Calculates Audit Response for a given dashboard
     *
     * @param dashboard
     * @param beginDt
     * @param endDt
     * @param pullRequests
     * @return @GenericAuditResponse for the build job for a given dashboard, begin and end date
     */
    private GenericAuditResponse getBuildJobAuditResponse(Dashboard dashboard, long beginDt, long endDt, List<GitRequest> pullRequests) {
        GenericAuditResponse genericAuditResponse = new GenericAuditResponse();
        List<CollectorItem> buildItems = this.getCollectorItems(dashboard, "build", CollectorType.Build);
        List<CollectorItem> repoItems = this.getCollectorItems(dashboard, "repo", CollectorType.SCM);

        List<CollItemCfgHist> jobConfigHists = null;

        if (CollectionUtils.isEmpty(buildItems)) {
            genericAuditResponse.addAuditStatus(AuditStatus.DASHBOARD_BUILD_NOT_CONFIGURED);
            return genericAuditResponse;
        }

        genericAuditResponse.addAuditStatus(AuditStatus.DASHBOARD_BUILD_CONFIGURED);

        CollectorItem buildItem = buildItems.get(0);

        String jobUrl = (String) buildItem.getOptions().get("jobUrl");
        String jobName = (String) buildItem.getOptions().get("jobName");

        if (jobUrl != null && jobName != null) {
            JobReviewResponse jobReviewResponse = this.getBuildJobReviewResponse(jobUrl, jobName, beginDt, endDt);
            jobConfigHists = jobReviewResponse.getConfigHistory();
            genericAuditResponse.addResponse(GenericAuditResponse.JOB_REVIEW, jobReviewResponse);
        }

        if (CollectionUtils.isEmpty(repoItems)) {
            genericAuditResponse.addAuditStatus(AuditStatus.DASHBOARD_REPO_NOT_CONFIGURED);
        } else {
            Build build = buildRepository.findTop1ByCollectorItemIdOrderByTimestampDesc(buildItem.getId());
            if (build != null) {
                List<RepoBranch> repoBranches = build.getCodeRepos();
                if (!CollectionUtils.isEmpty(repoBranches)) {
                    RepoBranch repoBranch = repoBranches.get(0);
                    String buildWidgetBranch = repoBranch.getBranch();
                    String buildWidgetUrl = repoBranch.getUrl();

                    boolean matchFound = false;
                    for (CollectorItem repoItem : repoItems) {
                        String aRepoItembranch = (String) repoItem.getOptions().get("branch");
                        String aRepoItemUrl = (String) repoItem.getOptions().get("url");
                        GitHubParsedUrl gitHubParsed = new GitHubParsedUrl(aRepoItemUrl);
                        aRepoItemUrl = gitHubParsed.getUrl();

                        if (aRepoItembranch != null && aRepoItemUrl != null
                                && buildWidgetBranch != null && buildWidgetUrl != null && aRepoItembranch.equalsIgnoreCase(buildWidgetBranch) && aRepoItemUrl.equalsIgnoreCase(buildWidgetUrl)) {
                            genericAuditResponse.addAuditStatus(AuditStatus.DASHBOARD_REPO_BUILD_VALID);
                            matchFound = true;
                            break;
                        }

                    }
                    if (!matchFound) {
                        genericAuditResponse.addAuditStatus(AuditStatus.DASHBOARD_REPO_BUILD_INVALID);
                    }
                }
            }

        }
        if (!CollectionUtils.isEmpty(pullRequests) && !CollectionUtils.isEmpty(jobConfigHists)) {
            Set<String> prAuthorsSet = pullRequests.stream().map(GitRequest::getUserId).collect(Collectors.toSet());
            Set<String> configAuthorsSet = jobConfigHists.stream().map(CollItemCfgHist::getUserID).collect(Collectors.toSet());
            genericAuditResponse.addAuditStatus(!SetUtils.intersection(prAuthorsSet, configAuthorsSet).isEmpty() ? AuditStatus.DASHBOARD_REPO_PR_AUTHOR_EQ_BUILD_AUTHOR : AuditStatus.DASHBOARD_REPO_PR_AUTHOR_NE_BUILD_AUTHOR);
        }

        return genericAuditResponse;
    }


    /**
     * Calculates code quality audit response
     *
     * @param dashboard
     * @return @GenericAuditResponse for the code quality of a given @Dashboard
     * @throws HygieiaException
     */
    private GenericAuditResponse getCodeQualityAuditResponse(Dashboard dashboard) throws HygieiaException {
        List<CollectorItem> codeQualityItems = this.getCollectorItems(dashboard, "codeanalysis", CollectorType.CodeQuality);
        GenericAuditResponse genericAuditResponse = new GenericAuditResponse();
        if (CollectionUtils.isEmpty(codeQualityItems)) {
            genericAuditResponse.addAuditStatus(AuditStatus.DASHBOARD_CODEQUALITY_NOT_CONFIGURED);
            return genericAuditResponse;
        }
        genericAuditResponse.addAuditStatus(AuditStatus.DASHBOARD_CODEQUALITY_CONFIGURED);
        CollectorItem codeQualityItem = codeQualityItems.get(0);
        List<CodeQuality> codeQualityDetails = codeQualityRepository.findByCollectorItemIdOrderByTimestampDesc(codeQualityItem.getCollectorId());
        StaticAnalysisResponse staticAnalysisResponse = this.getStaticAnalysisResponse(codeQualityDetails);
        genericAuditResponse.addResponse(GenericAuditResponse.STATIC_CODE_REVIEW, staticAnalysisResponse);
        //Commenting this out until Sonar Collector is updated to pull config changes
//			if(repoItems != null && !repoItems.isEmpty()){
//				for (CollectorItem repoItem : repoItems) {
//					String aRepoItembranch = (String) repoItem.getOptions().get("branch");
//					String aRepoItemUrl = (String) repoItem.getOptions().get("url");
//					List<Commit> repoCommits = getCommits(aRepoItemUrl, aRepoItembranch, beginDate, endDate);
//
//					CodeQualityProfileValidationResponse codeQualityProfileValidationResponse = this.qualityProfileAudit(repoCommits,codeQualityDetails,beginDate, endDate);
//					genericAuditResponse.setCodeQualityProfileValidationResponse(codeQualityProfileValidationResponse);
//				}
//			}

        return genericAuditResponse;
    }

    public JobReviewResponse getBuildJobReviewResponse(String jobUrl, String jobName, long beginDt, long endDt) {

        JobReviewResponse jobReviewResponse = new JobReviewResponse();

        Collector hudsonCollector = collectorRepository.findByName("Hudson");
        JobCollectorItem collectorItem = jobRepository.findJobByJobUrl(hudsonCollector.getId(), jobUrl, jobName);

        if (collectorItem == null) {
            jobReviewResponse.addAuditStatus(AuditStatus.DASHBOARD_BUILD_NOT_CONFIGURED);
            return jobReviewResponse;
        }

        if (!CollectionUtils.isEmpty(collectorItem.getErrors())) {
            jobReviewResponse.addAuditStatus(AuditStatus.COLLECTOR_ITEM_ERROR);

            jobReviewResponse.setLastUpdated(collectorItem.getLastUpdated());
            jobReviewResponse.setErrorMessage(collectorItem.getErrors().get(0).getErrorMessage());
            return jobReviewResponse;
        }

        //Segregation of Pipeline Environments
        //Check Prod job URL to validate Prod deploy job in Enterprise Jenkins Prod folder
        jobReviewResponse.setEnvironment(collectorItem.getEnvironment());

        //Segregation of access to Pipeline Environments
        //Check Jenkins Job config log to validate pr author is not modifying the Prod Job
        //since beginDate and endDate are the same column and between is excluding the edge values, we need to subtract/add a millisec
        jobReviewResponse.setConfigHistory(collItemCfgHistRepository.findByCollectorItemIdAndJobAndJobUrlAndTimestampBetweenOrderByTimestampDesc(collectorItem.getId(), jobName, jobUrl, beginDt - 1, endDt + 1));

        if ("PROD".equalsIgnoreCase(jobReviewResponse.getEnvironment())) {
            if (jobUrl.toUpperCase(Locale.ENGLISH).contains("NON-PROD")) {
                jobReviewResponse.addAuditStatus(AuditStatus.BUILD_JOB_IS_NON_PROD);
            } else {
                jobReviewResponse.addAuditStatus(AuditStatus.BUILD_JOB_IS_PROD);
            }
        } else {
            jobReviewResponse.addAuditStatus(AuditStatus.BUILD_JOB_IS_NON_PROD);
        }

        return jobReviewResponse;
    }

    /**
     * Gets StaticAnalysisResponses for artifact
     *
     * @param projectName     Sonar Project Name
     * @param artifactVersion Artifact Version
     * @return List of StaticAnalysisResponse
     * @throws HygieiaException
     */
    public List<StaticAnalysisResponse> getCodeQualityAudit(String projectName,
                                                            String artifactVersion) throws HygieiaException {
        List<CodeQuality> qualities = codeQualityRepository
                .findByNameAndVersionOrderByTimestampDesc(projectName, artifactVersion);
        if (CollectionUtils.isEmpty(qualities))
            throw new HygieiaException("Empty CodeQuality collection", HygieiaException.BAD_DATA);
        StaticAnalysisResponse response = getStaticAnalysisResponse(qualities);
        return Arrays.asList(response);
    }

    /**
     * Reusable method for constructing the StaticAnalysisResponse object for a
     *
     * @param codeQualities Code Quality List
     * @return StaticAnalysisResponse
     * @throws HygieiaException
     */
    private StaticAnalysisResponse getStaticAnalysisResponse(List<CodeQuality> codeQualities) throws HygieiaException {
        if (codeQualities == null)
            return new StaticAnalysisResponse();

        ObjectMapper mapper = new ObjectMapper();

        if (CollectionUtils.isEmpty(codeQualities))
            throw new HygieiaException("Empty CodeQuality collection", HygieiaException.BAD_DATA);
        CodeQuality returnQuality = codeQualities.get(0);

        StaticAnalysisResponse staticAnalysisResponse = new StaticAnalysisResponse();
        staticAnalysisResponse.setCodeQualityDetails(returnQuality);
        for (CodeQualityMetric metric : returnQuality.getMetrics()) {
            if (metric.getName().equalsIgnoreCase("quality_gate_details")) {
                TypeReference<HashMap<String, Object>> typeRef = new TypeReference<HashMap<String, Object>>() {
                };
                Map<String, String> values;
                try {
                    values = mapper.readValue((String) metric.getValue(), typeRef);
                    if (MapUtils.isNotEmpty(values) && values.containsKey("level")) {
                        String level = values.get("level");
                        if (level.equalsIgnoreCase("ok")) {
                            staticAnalysisResponse.addAuditStatus(AuditStatus.CODE_QUALITY_AUDIT_OK);
                        } else {
                            staticAnalysisResponse.addAuditStatus(AuditStatus.CODE_QUALITY_AUDIT_FAIL);
                        }
                    }
                    break;
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }
        }
        Set<AuditStatus> auditStatuses = staticAnalysisResponse.getAuditStatuses();
        if (!(auditStatuses.contains(AuditStatus.CODE_QUALITY_AUDIT_OK)
                || auditStatuses.contains(AuditStatus.CODE_QUALITY_AUDIT_FAIL))) {
            staticAnalysisResponse.addAuditStatus(AuditStatus.CODE_QUALITY_AUDIT_GATE_MISSING);
        }

        return staticAnalysisResponse;
    }

    /**
     * Retrieves test result execution details for a business application and
     * artifact
     *
     * @param jobUrl  Job Url of test execution
     * @param beginDt Beginning timestamp boundary
     * @param endDt   End Timestamp boundry
     * @return TestResultsResponse
     * @throws HygieiaException
     */

    public TestResultsResponse getTestResultExecutionDetails(String jobUrl, long beginDt, long endDt) throws HygieiaException {

        List<TestResult> testResults = getTestResults(jobUrl, beginDt, endDt);

        if (CollectionUtils.isEmpty(testResults))
            throw new HygieiaException("Unable to retreive  test result details for : " + jobUrl,
                    HygieiaException.BAD_DATA);

        return regressionTestResultAudit(testResults);

    }

    /**
     * Reusable method for constructing the StaticAnalysisResponse object for a
     *
     * @param testResults Test Result List
     * @return TestResultsResponse
     * Thrown by Object mapper method
     */
    private TestResultsResponse regressionTestResultAudit(List<TestResult> testResults) {
        TestResultsResponse testResultsResponse = new TestResultsResponse();
        boolean regressionTestSuitePresent = false;

        for (TestResult testResult : testResults) {
            if ("Regression".equalsIgnoreCase(testResult.getType().name())) {

                regressionTestSuitePresent = true;

                if (testResult.getFailureCount() == 0) {
                    testResultsResponse.addAuditStatus(AuditStatus.TEST_RESULT_AUDIT_OK);
                } else
                    testResultsResponse.addAuditStatus(AuditStatus.TEST_RESULT_AUDIT_FAIL);

                testResultsResponse.setTestCapabilities(testResult.getTestCapabilities());
            }

        }

        if (!regressionTestSuitePresent) {
            testResultsResponse.addAuditStatus(AuditStatus.TEST_RESULT_AUDIT_MISSING);
        }

        return testResultsResponse;
    }

    /**
     * Retrieves code quality profile changeset for a given time period and
     * determines if change author matches commit author within time period
     *
     * @param repoUrl         SCM repo url
     * @param repoBranch      SCM repo branch
     * @param projectName     Sonar Project name
     * @param artifactVersion Artifact Version
     * @return CodeQualityProfileValidationResponse
     * @throws HygieiaException
     */

    public CodeQualityProfileValidationResponse getQualityGateValidationDetails(String repoUrl, String repoBranch,
                                                                                String projectName, String artifactVersion, long beginDate, long endDate)
            throws HygieiaException {

        List<Commit> commits = customRepositoryQuery.findByScmUrlAndScmBranchAndScmCommitTimestampGreaterThanEqualAndScmCommitTimestampLessThanEqual(repoUrl, repoBranch, beginDate, endDate);

        List<CodeQuality> codeQualities = codeQualityRepository
                .findByNameAndVersionOrderByTimestampDesc(projectName, artifactVersion);

        return this.qualityProfileAudit(commits, codeQualities, beginDate, endDate);

    }

    private CodeQualityProfileValidationResponse qualityProfileAudit(List<Commit> commits, List<CodeQuality> codeQualities, long beginDate, long endDate) {

        Set<String> authors = commits.stream().map(SCM::getScmAuthor).collect(Collectors.toSet());

        CodeQualityProfileValidationResponse codeQualityGateValidationResponse = new CodeQualityProfileValidationResponse();
        CodeQuality codeQuality = codeQualities.get(0);
        String url = codeQuality.getUrl();
        List<CollItemCfgHist> qualityProfileChanges = collItemCfgHistRepository
                .findByJobUrlAndTimestampBetweenOrderByTimestampDesc(url, beginDate - 1, endDate + 1);

        // If no change has been made to quality profile between the time range,
        // then return an audit status of no change
        // Need to differentiate between document not being found and whether
        // there was no change for the quality profile
        if (CollectionUtils.isEmpty(qualityProfileChanges)) {
            codeQualityGateValidationResponse.addAuditStatus(AuditStatus.QUALITY_PROFILE_VALIDATION_AUDIT_NO_CHANGE);
        } else {

            // Iterate over all the change performers and check if they exist
            // within the authors set
            Set<String> qualityProfileChangePerformers = new HashSet<>();
            // TODO Improve this check as it is inefficient
// If the change performer matches a commit author, then fail
// the audit
            qualityProfileChanges.stream().map(CollItemCfgHist::getUserID).forEach(qualityProfileChangePerformer -> {
                qualityProfileChangePerformers.add(qualityProfileChangePerformer);
                if (authors.contains(qualityProfileChangePerformer)) {
                    codeQualityGateValidationResponse.addAuditStatus(AuditStatus.QUALITY_PROFILE_VALIDATION_AUDIT_FAIL);
                }
            });
            // If there is no match between change performers and commit
            // authors, then pass the audit
            Set<AuditStatus> auditStatuses = codeQualityGateValidationResponse.getAuditStatuses();
            if (!(auditStatuses.contains(AuditStatus.QUALITY_PROFILE_VALIDATION_AUDIT_FAIL))) {
                codeQualityGateValidationResponse.addAuditStatus(AuditStatus.QUALITY_PROFILE_VALIDATION_AUDIT_OK);
            }
            codeQualityGateValidationResponse.setQualityGateChangePerformers(qualityProfileChangePerformers);
        }
        codeQualityGateValidationResponse.setCommitAuthors(authors);
        return codeQualityGateValidationResponse;
    }

    private List<TestResult> getTestResults(String jobUrl, long beginDt, long endDt) {
        return customRepositoryQuery
                .findByUrlAndTimestampGreaterThanEqualAndTimestampLessThanEqual(jobUrl, beginDt, endDt);
    }

    public PerfReviewResponse getresultsBycomponetAndTime(String businessComp, long from, long to) {
        Cmdb cmdb = cmdbRepository.findByConfigurationItemIgnoreCase(businessComp); // get CMDB iD
        Iterable<Dashboard> dashboard = dashboardRepository.findAllByConfigurationItemBusAppObjectId(cmdb.getId()); //get dashboard based on CMDB ID
        Iterator<Dashboard> dashboardIT = dashboard.iterator();  //Iterate through the dashboards to obtain the collectorIteamID
        PerfReviewResponse perfReviewResponse = new PerfReviewResponse();
        while (dashboardIT.hasNext()) {
            dashboardIT.next();
            Set<CollectorType> ci = dashboard.iterator().next().getApplication().getComponents().iterator().next().getCollectorItems().keySet();
            boolean Isperf = dashboard.iterator().next().getApplication().getComponents().iterator().next().getCollectorItems().values().iterator().next().iterator().next().getOptions().containsValue("jmeter");
            boolean Istest = Objects.equals(ci.iterator().next().name(), CollectorType.Test.name());
            if (Istest && Isperf)  //validate if the Test collector exists with jmeter collector Item
            {
                ObjectId collectorItemID = dashboard.iterator().next().getApplication().getComponents().iterator().next().getCollectorItems().values().iterator().next().iterator().next().getId();
                List<TestResult> result = customRepositoryQuery.findByCollectorItemIdAndTimestampGreaterThanEqualAndTimestampLessThanEqual(collectorItemID, from, to);
                List<PerfTest> testlist = new ArrayList<>();
                //loop through test result object to obtain performance artifacts.
                for (TestResult testResult : result) { //parse though the results to obtain performance KPI's
                    Collection<TestCapability> testCapabilityCollection = testResult.getTestCapabilities();
                    List<TestCapability> testCapabilityList = new ArrayList<>(testCapabilityCollection);

                    for (TestCapability testCapability : testCapabilityList) {
                        PerfTest test = new PerfTest();
                        List<PerfIndicators> kpilist = new ArrayList<>();
                        Collection<TestSuite> testSuitesCollection = testCapability.getTestSuites();
                        List<TestSuite> testSuiteList = new ArrayList<>(testSuitesCollection);

                        for (TestSuite testSuite : testSuiteList) {
                            Collection<TestCase> testCaseCollection = testSuite.getTestCases();
                            List<TestCase> testCaseList = new ArrayList<>(testCaseCollection);

                            for (TestCase testCase : testCaseList) {
                                PerfIndicators kpi = new PerfIndicators();
                                kpi.setStatus(testCase.getStatus().toString());
                                kpi.setType(testCase.getDescription());
                                Collection<TestCaseStep> testCaseStepCollection = testCase.getTestSteps();
                                List<TestCaseStep> testCaseStepList = new ArrayList<>(testCaseStepCollection);
                                int j = 0;
                                for (TestCaseStep testCaseStep : testCaseStepList) {
                                    String value = testCaseStep.getDescription();
                                    if (j == 0) {
                                        double targetdouble = Double.parseDouble(value);
                                        kpi.setTarget(targetdouble);
                                    } else {
                                        double achievedouble = Double.parseDouble(value);
                                        kpi.setAchieved(achievedouble);
                                    }
                                    j++;
                                }
                                kpilist.add(kpi);
                            }
                            //create performance test review object
                            test.setRunId(testResult.getExecutionId());
                            test.setStartTime(testResult.getStartTime());
                            test.setEndTime(testResult.getEndTime());
                            test.setResultStatus(testResult.getDescription());
                            test.setPerfIndicators(kpilist);
                            CollectorItem collectoritem = collectorItemRepository.findOne(collectorItemID);
                            test.setTestName((String) collectoritem.getOptions().get("jobName"));
                            test.setTimeStamp(testResult.getTimestamp());
                            testlist.add(test);
                        }
                    }
                }
                perfReviewResponse.setResult(testlist);
                int counter = (int) testlist.stream().filter(list -> list.getResultStatus().matches("Success")).count();
                if (testlist.size() == 0) {
                    perfReviewResponse.setAuditStatuses(AuditStatus.PERF_RESULT_AUDIT_MISSING);
                } else if (counter >= 1) {
                    perfReviewResponse.setAuditStatuses(AuditStatus.PERF_RESULT_AUDIT_OK);
                } else {
                    perfReviewResponse.setAuditStatuses(AuditStatus.PERF_RESULT_AUDIT_FAIL);
                }
            }
        }
        return perfReviewResponse;
    }

    /**
     * @param dashboard
     * @param widgetName
     * @param collectorType
     * @return list of @CollectorItem for a given dashboard, widget name and collector type
     */
    public List<CollectorItem> getCollectorItems(Dashboard dashboard, String widgetName, CollectorType collectorType) {
        List<Widget> widgets = dashboard.getWidgets();
        ObjectId componentId = widgets.stream().filter(widget -> widget.getName().equalsIgnoreCase(widgetName)).findFirst().map(Widget::getComponentId).orElse(null);

        if (componentId == null) return null;

        com.capitalone.dashboard.model.Component component = componentRepository.findOne(componentId);

        return component.getCollectorItems().get(collectorType);
    }



    /**
     * Finds the dashboard
     *
     * @param title
     * @param type
     * @param busServ
     * @param busApp
     * @return the @Dashboard for a given title, type, business service and app
     * @throws HygieiaException
     */
    private Dashboard getDashboard(String title, String type, String busServ, String busApp) throws HygieiaException {
        if (!StringUtils.isEmpty(title) && !StringUtils.isEmpty(type)) {
            return dashboardRepository.findByTitleAndType(title, type);

        } else if (!StringUtils.isEmpty(busServ) && !StringUtils.isEmpty(busApp)) {
            Cmdb busServItem = cmdbRepository.findByConfigurationItemAndItemType(busServ, "app");
            if (busServItem == null)
                throw new HygieiaException("Invalid Business Service Name.", HygieiaException.BAD_DATA);
            Cmdb busAppItem = cmdbRepository.findByConfigurationItemAndItemType(busApp, "component");
            if (busAppItem == null)
                throw new HygieiaException("Invalid Business Application Name.", HygieiaException.BAD_DATA);

            return dashboardRepository.findByConfigurationItemBusServObjectIdAndConfigurationItemBusAppObjectId(busServItem.getId(), busAppItem.getId());
        }
        return null;
    }


}
