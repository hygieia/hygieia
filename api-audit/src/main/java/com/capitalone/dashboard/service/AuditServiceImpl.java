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
import com.capitalone.dashboard.model.RepoBranch;
import com.capitalone.dashboard.model.TestResult;
import com.capitalone.dashboard.model.Review;
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
import com.capitalone.dashboard.response.StaticAnalysisResponse;
import com.capitalone.dashboard.response.TestResultsResponse;
import com.capitalone.dashboard.util.GitHubParsedUrl;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

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

    public List<CollectorItem> getCollectorItems(Dashboard dashboard, String widgetName, CollectorType collectorType) {
        List<Widget> widgets = dashboard.getWidgets();
        ObjectId componentId = null;

        for (Widget widget : widgets) {
            if (widget.getName().equalsIgnoreCase(widgetName)) {
                componentId = widget.getComponentId();
                break;
            }
        }

        if (componentId == null) return null;

        com.capitalone.dashboard.model.Component component = componentRepository.findOne(componentId);

        return component.getCollectorItems().get(collectorType);
    }

    @SuppressWarnings({"PMD.NPathComplexity","PMD.ExcessiveMethodLength","PMD.AvoidBranchingStatementAsLastInLoop","PMD.EmptyIfStmt"})
    public DashboardReviewResponse getDashboardReviewResponse(String title, String type, String busServ, String busApp,
                                                              long beginDate, long endDate) throws HygieiaException {
        Dashboard dashboard = null;
        if (!StringUtils.isEmpty(title) && !StringUtils.isEmpty(type)) {
            dashboard = dashboardRepository.findByTitleAndType(title, type);

        } else if (!StringUtils.isEmpty(busServ) && !StringUtils.isEmpty(busApp)) {
            Cmdb busServItem = cmdbRepository.findByConfigurationItemAndItemType(busServ, "app"); //asv
            if (busServItem == null) throw new HygieiaException("Invalid Business Service Name.", HygieiaException.BAD_DATA);
            Cmdb busAppItem = cmdbRepository.findByConfigurationItemAndItemType(busApp, "component"); //bap
            if (busAppItem == null) throw new HygieiaException("Invalid Business Application Name.", HygieiaException.BAD_DATA);

            dashboard = dashboardRepository.findByConfigurationItemBusServObjectIdAndConfigurationItemBusAppObjectId(busServItem.getId(), busAppItem.getId());
        }

        DashboardReviewResponse dashboardReviewResponse = new DashboardReviewResponse();
        if (dashboard == null) {
            //if looking up by valid ASV and BAP and its not tied to a dashboard
            //or if looking up by title and its not found
            dashboardReviewResponse.addAuditStatus(AuditStatus.DASHBOARD_NOT_REGISTERED);
            return dashboardReviewResponse;
        }

        dashboardReviewResponse.setDashboardTitle(dashboard.getTitle());

        List<CollectorItem> repoItems = this.getCollectorItems(dashboard, "repo", CollectorType.SCM);


        List<GitRequest> pullRequests = null;
        List<Commit> commits = null;
        if (repoItems != null && !repoItems.isEmpty()) {
            String scmWidgetbranch;
            String scmWidgetrepoUrl;
            dashboardReviewResponse.addAuditStatus(AuditStatus.DASHBOARD_REPO_CONFIGURED);
            List<List<PeerReviewResponse>> allRepos = new ArrayList<>();

            for(CollectorItem repoItem: repoItems) {
                scmWidgetbranch = (String)repoItem.getOptions().get("branch");
                scmWidgetrepoUrl = (String)repoItem.getOptions().get("url");
				GitHubParsedUrl gitHubParsed = new GitHubParsedUrl(scmWidgetrepoUrl);
            	scmWidgetrepoUrl = gitHubParsed.getUrl();
                if (scmWidgetbranch != null && scmWidgetrepoUrl != null) {
                    pullRequests = this.getPullRequests(scmWidgetrepoUrl, scmWidgetbranch, beginDate, endDate);
                    commits = this.getCommits(scmWidgetrepoUrl, scmWidgetbranch, beginDate, endDate);
                    List<PeerReviewResponse> inside = this.getPeerReviewResponses(pullRequests, commits, scmWidgetrepoUrl, scmWidgetbranch);
                    allRepos.add(inside);
                }

            }
            dashboardReviewResponse.setAllPeerReviewResponses(allRepos);
        } else {
            dashboardReviewResponse.addAuditStatus(AuditStatus.DASHBOARD_REPO_NOT_CONFIGURED);
        }

        List<CollectorItem> buildItems = this.getCollectorItems(dashboard, "build", CollectorType.Build);

        List<CollItemCfgHist> jobConfigHists = null;
        if (buildItems != null && !buildItems.isEmpty()) {
            dashboardReviewResponse.addAuditStatus(AuditStatus.DASHBOARD_BUILD_CONFIGURED);

            CollectorItem buildItem = buildItems.get(0);

            String jobUrl = (String)buildItem.getOptions().get("jobUrl");
            String jobName = (String)buildItem.getOptions().get("jobName");

            if (jobUrl != null && jobName != null) {
                JobReviewResponse jobReviewResponse = this.getBuildJobReviewResponse(jobUrl, jobName, beginDate, endDate);
                jobConfigHists = jobReviewResponse.getConfigHistory();
                dashboardReviewResponse.setJobReviewResponse(jobReviewResponse);
            }

        } else {
            dashboardReviewResponse.addAuditStatus(AuditStatus.DASHBOARD_BUILD_NOT_CONFIGURED);
        }

        List<CollectorItem> codeQualityItems = this.getCollectorItems(dashboard, "codeanalysis", CollectorType.CodeQuality);

        if (codeQualityItems != null && !codeQualityItems.isEmpty()) {
        	dashboardReviewResponse.addAuditStatus(AuditStatus.DASHBOARD_CODEQUALITY_CONFIGURED);
			CollectorItem codeQualityItem = codeQualityItems.get(0);
			List<CodeQuality> codeQualityDetails = codeQualityRepository.findByCollectorItemIdOrderByTimestampDesc(codeQualityItem.getCollectorId());
			StaticAnalysisResponse staticAnalysisResponse = this.getStaticAnalysisResponse(codeQualityDetails);
			dashboardReviewResponse.setStaticAnalysisResponse(staticAnalysisResponse);
			
			if(repoItems != null && !repoItems.isEmpty()){
				for (CollectorItem repoItem : repoItems) {
					String aRepoItembranch = (String) repoItem.getOptions().get("branch");
					String aRepoItemUrl = (String) repoItem.getOptions().get("url");
					List<Commit> repoCommits = getCommits(aRepoItemUrl, aRepoItembranch, beginDate, endDate);
					
					CodeQualityProfileValidationResponse codeQualityProfileValidationResponse = this.qualityProfileAudit(repoCommits,codeQualityDetails,beginDate, endDate);
					dashboardReviewResponse.setCodeQualityProfileValidationResponse(codeQualityProfileValidationResponse);
				}
			}

        } else {
            dashboardReviewResponse.addAuditStatus(AuditStatus.DASHBOARD_CODEQUALITY_NOT_CONFIGURED);
        }

        if (buildItems != null && !buildItems.isEmpty()) {

            CollectorItem buildItem = buildItems.get(0);
            Build build = buildRepository.findTop1ByCollectorItemIdOrderByTimestampDesc(buildItem.getId());

            if (build != null) {
                List<RepoBranch> repoBranches = build.getCodeRepos();
                if (repoBranches != null && !repoBranches.isEmpty()) {
                    RepoBranch repoBranch = repoBranches.get(0);
                    String buildWidgetBranch = repoBranch.getBranch();
                    String buildWidgetUrl = repoBranch.getUrl();

                    if (repoItems != null && !repoItems.isEmpty()) {

                        boolean matchFound = false;
                        for(CollectorItem repoItem: repoItems) {

                            String aRepoItembranch = (String)repoItem.getOptions().get("branch");
                            String aRepoItemUrl = (String)repoItem.getOptions().get("url");
                            GitHubParsedUrl gitHubParsed = new GitHubParsedUrl(aRepoItemUrl);
                            aRepoItemUrl = gitHubParsed.getUrl();

                            if (aRepoItembranch != null &&  aRepoItemUrl != null
                                    && buildWidgetBranch != null && buildWidgetUrl != null) {
                                if (aRepoItembranch.equalsIgnoreCase(buildWidgetBranch) && aRepoItemUrl.equalsIgnoreCase(buildWidgetUrl)) {
                                    dashboardReviewResponse.addAuditStatus(AuditStatus.DASHBOARD_REPO_BUILD_VALID);
                                    matchFound = true;
                                    break;
                                }
                            }

                        }

                        if (!matchFound) {
                            dashboardReviewResponse.addAuditStatus(AuditStatus.DASHBOARD_REPO_BUILD_INVALID);
                        }

                    }

                }
            }
        } else {
            dashboardReviewResponse.addAuditStatus(AuditStatus.DASHBOARD_REPO_BUILD_INVALID);
        }

        if (pullRequests != null && jobConfigHists != null) {
            HashSet<String> prAuthorsSet = new HashSet();
            for(GitRequest pr : pullRequests) {
                prAuthorsSet.add(pr.getUserId());
            }

            HashSet<String> configAuthorsSet = new HashSet();
            for(CollItemCfgHist cfgHist : jobConfigHists) {
                configAuthorsSet.add(cfgHist.getUserID());
            }

            Iterator<String> prAuthorsSetIter = prAuthorsSet.iterator();
            while(prAuthorsSetIter.hasNext()) {
                String prAuthor = prAuthorsSetIter.next();
                if (configAuthorsSet.contains(prAuthor)) {
                    dashboardReviewResponse.addAuditStatus(AuditStatus.DASHBOARD_REPO_PR_AUTHOR_EQ_BUILD_AUTHOR);
                    break;
                }
            }

            dashboardReviewResponse.addAuditStatus(AuditStatus.DASHBOARD_REPO_PR_AUTHOR_NE_BUILD_AUTHOR);
        }
		
		List<CollectorItem> testItems = this.getCollectorItems(dashboard, "test", CollectorType.Test);

		if (testItems != null && !testItems.isEmpty()) {
			dashboardReviewResponse.addAuditStatus(AuditStatus.DASHBOARD_TEST_CONFIGURED);
			for (CollectorItem testItem : testItems){
				List<TestResult> testResults = getTestResults((String)testItem.getOptions().get("jobUrl"),beginDate,endDate);
				TestResultsResponse testResultsResponse = this.regressionTestResultAudit(testResults);
				dashboardReviewResponse.setTestResultsResponse(testResultsResponse);
			}

		} else {
			dashboardReviewResponse.addAuditStatus(AuditStatus.DASHBOARD_TEST_NOT_CONFIGURED);
		}
        return dashboardReviewResponse;
    }

    public List<GitRequest> getPullRequests(String repo, String branch, long beginDt, long endDt) {
        List<GitRequest> pullRequests = gitRequestRepository.findByScmUrlIgnoreCaseAndScmBranchIgnoreCaseAndCreatedAtGreaterThanEqualAndMergedAtLessThanEqual(repo, branch, beginDt, endDt);
        return pullRequests;
    }

    public List<Commit> getCommits(String repo, String branch, long beginDt, long endDt) {
        List<Commit> commits = customRepositoryQuery.findByScmUrlAndScmBranchAndScmCommitTimestampGreaterThanEqualAndScmCommitTimestampLessThanEqual(repo, branch, beginDt, endDt);
        return commits;
    }

    private void getRelatedCommits(String aCommit, HashMap<String, Commit> mapCommitsRelatedToPr, GitRequest pr) {
        Commit relatedCommit = commitRepository.findByScmRevisionNumberAndScmUrlIgnoreCase(aCommit, pr.getScmUrl());
        if (relatedCommit != null) {

            if (!mapCommitsRelatedToPr.containsKey(relatedCommit.getScmRevisionNumber())) {
                mapCommitsRelatedToPr.put(relatedCommit.getScmRevisionNumber(), relatedCommit);
            }

            //uncomment if tracing back all the previous parent commits
//            List<String> relatedCommitShas = relatedCommit.getScmParentRevisionNumbers();
//            for(String relatedCommitSha: relatedCommitShas) {
//                getRelatedCommits(relatedCommitSha, mapCommitsRelatedToPr, pr);
//            }
        }
    }

    boolean computePeerReviewStatus(GitRequest pr, PeerReviewResponse peerReviewResponse) {
        List<CommitStatus> statuses = pr.getCommitStatuses();
        List<Review> reviews = pr.getReviews();
        if (statuses != null) {
            Set<String> prContexts = new HashSet<>();
            String contextString = settings.getPeerReviewContexts();
            if (contextString != null) {
                prContexts.addAll(Arrays.asList(contextString.trim().split(",")));
            }
            for (CommitStatus status : statuses) {
                if (prContexts.contains(status.getContext())) {
                    //review done using LGTM workflow
                    peerReviewResponse.addAuditStatus(AuditStatus.PEER_REVIEW_LGTM);
                    return "success".equalsIgnoreCase(status.getState());
                }
            }
        }
        if (reviews != null) {
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

    @SuppressWarnings({"PMD.NPathComplexity","PMD.ExcessiveMethodLength","PMD.AvoidBranchingStatementAsLastInLoop","PMD.EmptyIfStmt"})
    public List<PeerReviewResponse> getPeerReviewResponses(List<GitRequest> pullRequests, List<Commit> commits, String scmUrl, String scmBranch) {
        List<PeerReviewResponse> allPeerReviews = new ArrayList<PeerReviewResponse>();

        HashMap<String, Commit> mapCommitsRelatedToAllPrs = new HashMap();

        if (pullRequests == null || pullRequests.isEmpty()) {
            PeerReviewResponse noPRsPeerReviewResponse = new PeerReviewResponse();
            noPRsPeerReviewResponse.addAuditStatus(AuditStatus.NO_PULL_REQ_FOR_DATE_RANGE);
            allPeerReviews.add(noPRsPeerReviewResponse);
        }

        for(GitRequest pr : pullRequests) {
            HashMap<String, Commit> mapCommitsRelatedToPr = new HashMap();
            String mergeSha = pr.getScmRevisionNumber();
            Commit mergeCommit = commitRepository.findByScmRevisionNumberAndScmUrlIgnoreCase(mergeSha, pr.getScmUrl());
            if (mergeCommit == null) {
                continue;
            }
            String mergeAuthor = mergeCommit.getScmAuthorLogin();

            List<String> relatedCommitShas = mergeCommit.getScmParentRevisionNumbers();
            if (!CollectionUtils.isEmpty(relatedCommitShas)) {
                for (String relatedCommitSha : relatedCommitShas) {
                    getRelatedCommits(relatedCommitSha, mapCommitsRelatedToPr, pr);
                }
            }

            PeerReviewResponse peerReviewResponse = new PeerReviewResponse();
            peerReviewResponse.setPullRequest(pr);

            List<Commit> commitsRelatedToPr = new ArrayList(mapCommitsRelatedToPr.values());
            commitsRelatedToPr.sort((e1, e2) -> new Long(e1.getScmCommitTimestamp()).compareTo(new Long(e2.getScmCommitTimestamp())));

            peerReviewResponse.setCommits(commitsRelatedToPr);

            //check for pr author <> pr merger
            String prAuthor = pr.getUserId();
            if (prAuthor.equalsIgnoreCase(mergeAuthor)) {
                peerReviewResponse.addAuditStatus(AuditStatus.COMMITAUTHOR_EQ_MERGECOMMITER);
            } else {
                peerReviewResponse.addAuditStatus(AuditStatus.COMMITAUTHOR_NE_MERGECOMMITER);
            }

            allPeerReviews.add(peerReviewResponse);

            //check to see if pr was reviewed
            boolean peerReviewed = computePeerReviewStatus(pr, peerReviewResponse);

            if (!peerReviewed) {
                //fallback check to see if pr has comments or reviewComments
                List<Comment> comments = pr.getComments();
                for(Comment comment: comments) {
                    if (!comment.getUser().equalsIgnoreCase(prAuthor)) {
                        peerReviewed = true;
                        peerReviewResponse.addAuditStatus(AuditStatus.PEER_REVIEW_REG_COMMENTS);
                        break;
                    }
                }
                List<Comment> reviewComments = pr.getReviewComments();
                for(Comment comment: reviewComments) {
                    if (!comment.getUser().equalsIgnoreCase(prAuthor)) {
                        peerReviewed = true;
                        peerReviewResponse.addAuditStatus(AuditStatus.PEER_REVIEW_REV_COMMENTS);
                        break;
                    }
                }
            }

            if (peerReviewed) {
                peerReviewResponse.addAuditStatus(AuditStatus.PULLREQ_REVIEWED_BY_PEER);
            } else {
                peerReviewResponse.addAuditStatus(AuditStatus.PULLREQ_NOT_PEER_REVIEWED);
            }

            //type of branching strategy
            String sourceRepo = pr.getSourceRepo();
            String sourceBranch = pr.getSourceBranch();
            String targetRepo = pr.getTargetRepo();
            String targetBranch = pr.getTargetBranch();

            if (sourceRepo == null) {
                //fork could be deleted
                peerReviewResponse.addAuditStatus(AuditStatus.GIT_FORK_STRATEGY);
            } else {
                if (sourceRepo.equalsIgnoreCase(targetRepo)) {
                    peerReviewResponse.addAuditStatus(AuditStatus.GIT_BRANCH_STRATEGY);
                } else {
                    peerReviewResponse.addAuditStatus(AuditStatus.GIT_FORK_STRATEGY);
                }
            }

            //direct commit to master
            String baseSha = pr.getBaseSha();
            String headSha = pr.getHeadSha();
            for(Commit commit: commitsRelatedToPr) {
                if (commit.getType() == CommitType.New) {
                    if (commit.getScmParentRevisionNumbers() != null) {
                        if (commit.getScmParentRevisionNumbers().isEmpty()) {
                            peerReviewResponse.addAuditStatus(AuditStatus.DIRECT_COMMITS_TO_BASE_FIRST_COMMIT);
                        } else {

                            //New commit has ONLY one parent
                            List<String> parentCommitShas = commit.getScmParentRevisionNumbers();
                            if (!CollectionUtils.isEmpty(parentCommitShas)) {
                                computeParentCommit(parentCommitShas.get(0), peerReviewResponse, baseSha, headSha, pr, mapCommitsRelatedToPr);
                            }

                        }
                    } else {
                        peerReviewResponse.addAuditStatus(AuditStatus.DIRECT_COMMITS_TO_BASE_FIRST_COMMIT);
                    }
                } else {
                    //merge commit
                }
            }

            mapCommitsRelatedToAllPrs.putAll(mapCommitsRelatedToPr);
        }

        PeerReviewResponse peerReviewResponse = new PeerReviewResponse();
        List<Commit> commitsNotDirectlyTiedToPr = new ArrayList<>();
        //check any commits not directly tied to pr
        for(Commit commit: commits) {
            String commitSha = commit.getScmRevisionNumber();
            if (!mapCommitsRelatedToAllPrs.containsKey(commitSha)) {
                if (commit.getType() == CommitType.New) {
                    commitsNotDirectlyTiedToPr.add(commit);
                    if (commit.getScmParentRevisionNumbers() != null) {
                        if (commit.getScmParentRevisionNumbers().isEmpty()) {
                            peerReviewResponse.addAuditStatus(AuditStatus.DIRECT_COMMITS_TO_BASE_FIRST_COMMIT);
                        } else {
                            peerReviewResponse.addAuditStatus(AuditStatus.GIT_NO_WORKFLOW);
                            peerReviewResponse.addAuditStatus(AuditStatus.DIRECT_COMMITS_TO_BASE);
                        }
                    } else {
                        peerReviewResponse.addAuditStatus(AuditStatus.DIRECT_COMMITS_TO_BASE_FIRST_COMMIT);
                    }
                } else {
                    //merge commit
                }
            }
        }
        if (!commitsNotDirectlyTiedToPr.isEmpty()) {
            peerReviewResponse.setCommits(commitsNotDirectlyTiedToPr);
            allPeerReviews.add(peerReviewResponse);
        }

        //pull requests in date range, but merged prior to 14 days so no commits available in hygieia
        if (pullRequests != null && !pullRequests.isEmpty()) {
            if (allPeerReviews.isEmpty()) {
                PeerReviewResponse prsButNoCommitsInRangePeerReviewResponse = new PeerReviewResponse();
                prsButNoCommitsInRangePeerReviewResponse.addAuditStatus(AuditStatus.NO_PULL_REQ_FOR_DATE_RANGE);
                allPeerReviews.add(prsButNoCommitsInRangePeerReviewResponse);
            }
        }

        Collector githubCollector = collectorRepository.findByName("GitHub");
        CollectorItem collectorItem = collectorItemRepository.findRepoByUrlAndBranch(githubCollector.getId(),
                scmUrl, scmBranch, true);
        for(PeerReviewResponse peerReviewResponseList: allPeerReviews){
            String collectorItemScmUrl = (String) collectorItem.getOptions().get("url");
            String collectorItemScmBranch = (String) collectorItem.getOptions().get("branch");
            if(collectorItemScmUrl != null && collectorItemScmUrl.equals(scmUrl)
                    && collectorItemScmBranch != null && collectorItemScmBranch.equals(scmBranch)){
                peerReviewResponseList.setLastUpdated(collectorItem.getLastUpdated());
            }
            peerReviewResponseList.setScmBranch(scmBranch);
            peerReviewResponseList.setScmUrl(scmUrl);
        }
        return allPeerReviews;
    }

    private boolean computeParentCommit(String commitSha, PeerReviewResponse peerReviewResponse, String baseSha, String headSha, GitRequest pr, HashMap<String, Commit> mapCommitsRelatedToPr) {
        boolean traceBack = true;
        Commit commit = commitRepository.findByScmRevisionNumberAndScmUrlIgnoreCase(commitSha, pr.getScmUrl());
        if (commit == null || commit.getType() == null || commit.getType() == CommitType.Merge || commit.getType() == CommitType.NotBuilt) {
            return false;
        }
        LOGGER.warn("Enter computeParentCommit " + commitSha + " " + commit.getType());
        while (traceBack) {
            if (commit.getScmRevisionNumber().equalsIgnoreCase(baseSha)) {
                if (commit.getScmParentRevisionNumbers() != null) {
                    if (commit.getScmParentRevisionNumbers().isEmpty()) {
                        peerReviewResponse.addAuditStatus(AuditStatus.DIRECT_COMMITS_TO_BASE_FIRST_COMMIT);
                    } else {
                        peerReviewResponse.addAuditStatus(AuditStatus.DIRECT_COMMITS_TO_BASE);
                    }
                } else {
                    peerReviewResponse.addAuditStatus(AuditStatus.DIRECT_COMMITS_TO_BASE_FIRST_COMMIT);
                }
                traceBack = false;
            } else if (commit.getScmRevisionNumber().equalsIgnoreCase(headSha)) {
                traceBack = false;
            } else {
                mapCommitsRelatedToPr.put(commitSha, commit);
                //New commit has ONLY one parent
                List<String> parentCommitShas = commit.getScmParentRevisionNumbers();
                if (!CollectionUtils.isEmpty(parentCommitShas)) {
                    traceBack = computeParentCommit(parentCommitShas.get(0), peerReviewResponse, baseSha, headSha, pr, mapCommitsRelatedToPr);
                } else {
                    //reached first commit
                    traceBack = false;
                }
            }
        }
        return traceBack;
    }

    public String getJobEnvironment(String jobUrl, String jobName) {
        Collector hudsonCollector = collectorRepository.findByName("Hudson");
        JobCollectorItem collectorItem = jobRepository.findJobByJobUrl(hudsonCollector.getId(), jobUrl, jobName);
        return collectorItem.getEnvironment();
    }

    public List<CollItemCfgHist> getCollItemCfgHist(String jobUrl, String jobName, long beginDt, long endDt) {
        Collector hudsonCollector = collectorRepository.findByName("Hudson");
        JobCollectorItem collectorItem = jobRepository.findJobByJobUrl(hudsonCollector.getId(), jobUrl, jobName);
        //since beginDate and endDate are the same column and between is excluding the edge values, we need to subtract/add a millisec
        return collItemCfgHistRepository.findByCollectorItemIdAndJobAndJobUrlAndTimestampBetweenOrderByTimestampDesc(collectorItem.getId(), jobName, jobUrl, beginDt-1, endDt+1);
    }

    public JobReviewResponse getBuildJobReviewResponse(String jobUrl, String jobName, long beginDt, long endDt) {

        //Segregation of Pipeline Environments
        //Check Prod job URL to validate Prod deploy job in Enterprise Jenkins Prod folder
        JobReviewResponse jobReviewResponse = new JobReviewResponse();
        String environment = this.getJobEnvironment(jobUrl, jobName);
        jobReviewResponse.setEnvironment(environment);

        //Segregation of access to Pipeline Environments
        //Check Jenkins Job config log to validate pr author is not modifying the Prod Job
        jobReviewResponse.setConfigHistory(this.getCollItemCfgHist(jobUrl, jobName, beginDt, endDt));

        if ("PROD".equalsIgnoreCase(environment)) {
            if (jobUrl.toUpperCase(Locale.ENGLISH).indexOf("NON-PROD") >= 0) {
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
	 * @param artifactGroup
	 *            Artifact Group
	 * @param artifactName
	 *            Artifact Name
	 * @param artifactVersion
	 *            Artifact Version
	 * @return List of StaticAnalysisResponse
	 * @throws IOException
	 *             thrown by called method
	 * @throws HygieiaException
	 */
	public List<StaticAnalysisResponse> getCodeQualityAudit(String artifactGroup, String artifactName,
			String artifactVersion) throws HygieiaException {
		List<CodeQuality> qualities = codeQualityRepository
				.findByNameAndVersionOrderByTimestampDesc(artifactGroup + ":" + artifactName, artifactVersion);
		if (CollectionUtils.isEmpty(qualities))
			throw new HygieiaException("Empty CodeQuality collection", HygieiaException.BAD_DATA);
		StaticAnalysisResponse response = getStaticAnalysisResponse(qualities);
		return Arrays.asList(response);
	}

	/**
	 * Reusable method for constructing the StaticAnalysisResponse object for a
	 * 
	 * @param codeQualities
	 *            Code Quality List
	 * @return StaticAnalysisResponse
	 * @throws JsonMappingException
	 * @throws JsonParseException
	 * @throws IOException
	 *             Thrown by Object mapper method
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
						String level = (String) values.get("level");
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
	 * @param jobUrl
	 *            Job Url of test execution
	 * @param beginDt
	 *            Beginning timestamp boundary
	 * @param endDt
	 *            End Timestamp boundry
	 * @return TestResultsResponse 
	 * @throws HygieiaException
	 */

	public TestResultsResponse getTestResultExecutionDetails(String jobUrl,long beginDt, long endDt) throws HygieiaException {
				
		List<TestResult> testResults = getTestResults(jobUrl,beginDt,endDt);
		
		if (CollectionUtils.isEmpty(testResults))
			throw new HygieiaException("Unable to retreive  test result details for : " + jobUrl,
					HygieiaException.BAD_DATA);

		TestResultsResponse testResultsResponse = regressionTestResultAudit(testResults);

		return testResultsResponse;

	}
	
	/**
	 * Reusable method for constructing the StaticAnalysisResponse object for a
	 * 
	 * @param testResults
	 *            Test Result List
	 * @return TestResultsResponse
	 *             Thrown by Object mapper method
	 */
	private TestResultsResponse regressionTestResultAudit(List<TestResult> testResults) {
		TestResultsResponse testResultsResponse = new TestResultsResponse();
		boolean regressionTestSuitePresent = false;
		
		for(TestResult testResult : testResults){
			if ("Regression".equalsIgnoreCase(testResult.getType().name())) {
				
				regressionTestSuitePresent = true;
				
				if (testResult.getFailureCount() == 0) {
					testResultsResponse.addAuditStatus(AuditStatus.TEST_RESULT_AUDIT_OK);
				} else
					testResultsResponse.addAuditStatus(AuditStatus.TEST_RESULT_AUDIT_FAIL);

				testResultsResponse.setTestCapabilities(testResult.getTestCapabilities());
			}
			
		}
		
		if (!regressionTestSuitePresent){
			testResultsResponse.addAuditStatus(AuditStatus.TEST_RESULT_AUDIT_MISSING);
		}	

		return testResultsResponse;
	}

	/**
	 * Retrieves code quality profile changeset for a given time period and
	 * determines if change author matches commit author within time period
	 * 
	 * @param repoUrl
	 *            SCM repo url
	 * @param repoBranch
	 *  		  SCM repo branch
	 * @param artifactGroup
	 *            Artifact Group
	 * @param artifactName
	 *            Artifact Name
	 * @param artifactVersion
	 *            Artifact Version
	 *            
	 * @return CodeQualityProfileValidationResponse
	 * @throws HygieiaException
	 */

	public CodeQualityProfileValidationResponse getQualityGateValidationDetails(String repoUrl,String repoBranch,
			String artifactGroup, String artifactName, String artifactVersion, long beginDate, long endDate)
			throws HygieiaException {
		
		List<Commit> commits = getCommits(repoUrl, repoBranch, beginDate, endDate);

		List<CodeQuality> codeQualities = codeQualityRepository
				.findByNameAndVersionOrderByTimestampDesc(artifactGroup + ":" + artifactName, artifactVersion);
		
		CodeQualityProfileValidationResponse codeQualityGateValidationResponse = this.qualityProfileAudit(commits,codeQualities,beginDate,endDate);

		
		return codeQualityGateValidationResponse; 
		
	}
	
	private CodeQualityProfileValidationResponse qualityProfileAudit(List<Commit> commits,List<CodeQuality> codeQualities,long beginDate, long endDate){
		
		Set<String> authors = new HashSet<String>();
		for (Commit commit : commits) {
			authors.add(commit.getScmAuthor());
		}
		
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
			Set<String> qualityProfileChangePerformers = new HashSet<String>();
			for (CollItemCfgHist qualityProfileChange : qualityProfileChanges) {
				String qualityProfileChangePerformer = qualityProfileChange.getUserID();
				qualityProfileChangePerformers.add(qualityProfileChangePerformer);

				// TODO Improve this check as it is inefficient
				// If the change performer matches a commit author, then fail
				// the audit
				if (authors.contains(qualityProfileChangePerformer)) {
					codeQualityGateValidationResponse.addAuditStatus(AuditStatus.QUALITY_PROFILE_VALIDATION_AUDIT_FAIL);
				}
			}
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

	@Override
    public boolean isGitRepoConfigured(String url, String branch) {
        Collector githubCollector = collectorRepository.findByName("GitHub");
        CollectorItem collectorItem = collectorItemRepository.findRepoByUrlAndBranch(githubCollector.getId(),
                url, branch, true);
        if (collectorItem != null) {
            return true;
        }
        return false;
    }
	
	private List<TestResult> getTestResults(String jobUrl,long beginDt, long endDt){
		List<TestResult> testResults = customRepositoryQuery
				.findByUrlAndTimestampGreaterThanEqualAndTimestampLessThanEqual(jobUrl,beginDt,endDt);
		
		return testResults;
	}
}
