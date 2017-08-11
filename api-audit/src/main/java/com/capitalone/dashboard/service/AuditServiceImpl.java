package com.capitalone.dashboard.service;

import com.capitalone.dashboard.misc.HygieiaException;
import com.capitalone.dashboard.model.AuditStatus;
import com.capitalone.dashboard.model.Build;
import com.capitalone.dashboard.model.Cmdb;
import com.capitalone.dashboard.model.CollItemCfgHist;
import com.capitalone.dashboard.model.Collector;
import com.capitalone.dashboard.model.CollectorItem;
import com.capitalone.dashboard.model.CollectorType;
import com.capitalone.dashboard.model.Comment;
import com.capitalone.dashboard.model.Commit;
import com.capitalone.dashboard.model.CommitType;
import com.capitalone.dashboard.model.Dashboard;
import com.capitalone.dashboard.model.GitRequest;
import com.capitalone.dashboard.model.JobCollectorItem;
import com.capitalone.dashboard.model.RepoBranch;
import com.capitalone.dashboard.model.Widget;
import com.capitalone.dashboard.repository.BuildRepository;
import com.capitalone.dashboard.repository.CmdbRepository;
import com.capitalone.dashboard.repository.CollItemCfgHistRepository;
import com.capitalone.dashboard.repository.CollectorRepository;
import com.capitalone.dashboard.repository.CommitRepository;
import com.capitalone.dashboard.repository.ComponentRepository;
import com.capitalone.dashboard.repository.DashboardRepository;
import com.capitalone.dashboard.repository.GitRequestRepository;
import com.capitalone.dashboard.repository.JobRepository;
import com.capitalone.dashboard.response.DashboardReviewResponse;
import com.capitalone.dashboard.response.JobReviewResponse;
import com.capitalone.dashboard.response.PeerReviewResponse;
import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

@Component
public class AuditServiceImpl implements AuditService {

    private GitRequestRepository gitRequestRepository;
    private final CommitRepository commitRepository;
    private JobRepository jobRepository;
    private CollectorRepository collectorRepository;
    private CollItemCfgHistRepository collItemCfgHistRepository;
    private DashboardRepository dashboardRepository;
    private CmdbRepository cmdbRepository;
    private ComponentRepository componentRepository;
    private BuildRepository buildRepository;

    @Autowired
    public AuditServiceImpl(GitRequestRepository gitRequestRepository, CommitRepository commitRepository,
                            JobRepository jobRepository, CollectorRepository collectorRepository,
                            CollItemCfgHistRepository collItemCfgHistRepository,
                            DashboardRepository dashboardRepository,
                            CmdbRepository cmdbRepository,
                            ComponentRepository componentRepository,
                            BuildRepository buildRepository) {
        this.gitRequestRepository = gitRequestRepository;
        this.commitRepository = commitRepository;
        this.jobRepository = jobRepository;
        this.collectorRepository = collectorRepository;
        this.collItemCfgHistRepository = collItemCfgHistRepository;
        this.dashboardRepository = dashboardRepository;
        this.cmdbRepository = cmdbRepository;
        this.componentRepository = componentRepository;
        this.buildRepository = buildRepository;
    }

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

        if (dashboard == null) {
            throw new HygieiaException("Unable to locate dashboard for title: " + title + " type: " + type +
                    " busServ: " + busServ + " busApp: " + busApp, HygieiaException.BAD_DATA);
        }

        DashboardReviewResponse dashboardReviewResponse = new DashboardReviewResponse();
        dashboardReviewResponse.setDashboardTitle(dashboard.getTitle());

        List<CollectorItem> repoItems = this.getCollectorItems(dashboard, "repo", CollectorType.SCM);

        String scmWidgetbranch = null;
        String scmWidgetrepoUrl = null;
        List<GitRequest> pullRequests = null;
        if (repoItems != null && !repoItems.isEmpty()) {
            dashboardReviewResponse.addAuditStatus(AuditStatus.DASHBOARD_REPO_CONFIGURED);

            CollectorItem repoItem = repoItems.get(0);
            scmWidgetbranch = (String)repoItem.getOptions().get("branch");
            scmWidgetrepoUrl = (String)repoItem.getOptions().get("url");

            if (scmWidgetbranch != null && scmWidgetrepoUrl != null) {
                pullRequests = this.getPullRequests(scmWidgetrepoUrl, scmWidgetbranch, beginDate, endDate);
                List<PeerReviewResponse> allPeerReviews = this.getPeerReviewResponses(pullRequests);

                dashboardReviewResponse.setAllPeerReviewResponses(allPeerReviews);
            }
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

                    if (scmWidgetbranch != null && scmWidgetrepoUrl != null
                            && buildWidgetBranch != null && buildWidgetUrl != null) {
                        if (scmWidgetbranch.equalsIgnoreCase(buildWidgetBranch) && scmWidgetrepoUrl.equalsIgnoreCase(buildWidgetUrl)) {
                            dashboardReviewResponse.addAuditStatus(AuditStatus.DASHBOARD_REPO_BUILD_VALID);
                        } else {
                            dashboardReviewResponse.addAuditStatus(AuditStatus.DASHBOARD_REPO_BUILD_INVALID);
                        }
                    } else {
                        dashboardReviewResponse.addAuditStatus(AuditStatus.DASHBOARD_REPO_BUILD_INVALID);
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

        return dashboardReviewResponse;
    }

    public List<GitRequest> getPullRequests(String repo, String branch, long beginDt, long endDt) {
        List<GitRequest> pullRequests = gitRequestRepository.findByScmUrlAndScmBranchAndCreatedAtGreaterThanEqualAndMergedAtLessThanEqual(repo, branch, beginDt, endDt);
        return pullRequests;
    }

    public List<Commit> getCommitsBySha (String scmRevisionNumber) {
        return commitRepository.findByScmRevisionNumber(scmRevisionNumber);
    }

    @SuppressWarnings({"PMD.NPathComplexity","PMD.ExcessiveMethodLength","PMD.AvoidBranchingStatementAsLastInLoop","PMD.EmptyIfStmt"})
    public List<PeerReviewResponse> getPeerReviewResponses(List<GitRequest> pullRequests) {
        List<PeerReviewResponse> allPeerReviews = new ArrayList<PeerReviewResponse>();

        for(GitRequest pr : pullRequests) {
            List commitsRelatedToPr = new ArrayList();
            String mergeSha = pr.getScmRevisionNumber();
            List<Commit> mergeCommits = this.getCommitsBySha(mergeSha);
            String mergeAuthor = "";
            for(Commit mergeCommit: mergeCommits) {
                List<String> relatedCommitShas = mergeCommit.getScmParentRevisionNumbers();
                mergeAuthor = mergeCommit.getScmAuthorLogin();
                for(String relatedCommitSha: relatedCommitShas) {
                    List<Commit> relatedCommits = this.getCommitsBySha(relatedCommitSha);
                    commitsRelatedToPr.addAll(relatedCommits);
                }
                break;
            }
            PeerReviewResponse peerReviewResponse = new PeerReviewResponse();
            peerReviewResponse.setPullRequest(pr);
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
            List<Comment> comments = pr.getComments();
            boolean peerReviewed = false;
            for(Comment comment: comments) {
                if (!comment.getUser().equalsIgnoreCase(prAuthor)) {
                    peerReviewed = true;
                    break;
                }
            }
            List<Comment> reviewComments = pr.getReviewComments();
            for(Comment comment: reviewComments) {
                if (!comment.getUser().equalsIgnoreCase(prAuthor)) {
                    peerReviewed = true;
                    break;
                }
            }

            if (peerReviewed) {
                peerReviewResponse.addAuditStatus(AuditStatus.PULLREQ_REVIEWED_BY_PEER);
            } else {
                peerReviewResponse.addAuditStatus(AuditStatus.PULLREQ_NOT_PEER_REVIEWED);
            }

            //direct commit to master
            String baseSha = pr.getBaseSha();
            List<Commit> baseCommits = this.getCommitsBySha(baseSha);
            for(Commit baseCommit: baseCommits) {
                if (baseCommit.getType() == CommitType.New) {
                    peerReviewResponse.addAuditStatus(AuditStatus.DIRECT_COMMITS_TO_BASE);
                } else {
                    //merge commit
                }
            }

        }
        return allPeerReviews;
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
}
