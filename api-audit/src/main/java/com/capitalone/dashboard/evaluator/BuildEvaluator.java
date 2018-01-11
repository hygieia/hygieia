package com.capitalone.dashboard.evaluator;

import com.capitalone.dashboard.model.AuditException;
import com.capitalone.dashboard.model.AuditStatus;
import com.capitalone.dashboard.model.Build;
import com.capitalone.dashboard.model.CollItemCfgHist;
import com.capitalone.dashboard.model.Collector;
import com.capitalone.dashboard.model.CollectorItem;
import com.capitalone.dashboard.model.CollectorType;
import com.capitalone.dashboard.model.Dashboard;
import com.capitalone.dashboard.model.GitRequest;
import com.capitalone.dashboard.model.JobCollectorItem;
import com.capitalone.dashboard.model.RepoBranch;
import com.capitalone.dashboard.repository.BuildRepository;
import com.capitalone.dashboard.repository.CollItemCfgHistRepository;
import com.capitalone.dashboard.repository.CollectorRepository;
import com.capitalone.dashboard.repository.JobRepository;
import com.capitalone.dashboard.response.BuildAuditResponse;
import com.capitalone.dashboard.response.GenericAuditResponse;
import com.capitalone.dashboard.util.GitHubParsedUrl;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections4.SetUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

import static com.capitalone.dashboard.response.GenericAuditResponse.JOB_REVIEW;

@Component
public class BuildEvaluator extends Evaluator<BuildAuditResponse> {

    private final BuildRepository buildRepository;
    private final JobRepository jobRepository;
    private final CollectorRepository collectorRepository;
    private final CollItemCfgHistRepository collItemCfgHistRepository;

    @Autowired
    public BuildEvaluator(BuildRepository buildRepository, JobRepository jobRepository, CollectorRepository collectorRepository, CollItemCfgHistRepository collItemCfgHistRepository) {
        this.buildRepository = buildRepository;
        this.jobRepository = jobRepository;
        this.collectorRepository = collectorRepository;
        this.collItemCfgHistRepository = collItemCfgHistRepository;
    }


    @Override
    public Collection<BuildAuditResponse> evaluate(Dashboard dashboard, long beginDate, long endDate, Collection<?> data) throws AuditException {
        return null;
    }

    @Override
    public BuildAuditResponse evaluate(CollectorItem collectorItem, long beginDate, long endDate, Collection<?> data) throws AuditException {
        return null;
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
    public GenericAuditResponse getBuildJobAuditResponse(Dashboard dashboard, long beginDt, long endDt, List<GitRequest> pullRequests) {
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
            BuildAuditResponse buildAuditResponse = this.getBuildJobReviewResponse(jobUrl, jobName, beginDt, endDt);
            jobConfigHists = buildAuditResponse.getConfigHistory();
            genericAuditResponse.addResponse(JOB_REVIEW, buildAuditResponse);
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



    public BuildAuditResponse getBuildJobReviewResponse(String jobUrl, String jobName, long beginDt, long endDt) {

        BuildAuditResponse buildAuditResponse = new BuildAuditResponse();

        Collector hudsonCollector = collectorRepository.findByName("Hudson");
        JobCollectorItem collectorItem = jobRepository.findJobByJobUrl(hudsonCollector.getId(), jobUrl, jobName);

        if (collectorItem == null) {
            buildAuditResponse.addAuditStatus(AuditStatus.DASHBOARD_BUILD_NOT_CONFIGURED);
            return buildAuditResponse;
        }

        if (!CollectionUtils.isEmpty(collectorItem.getErrors())) {
            buildAuditResponse.addAuditStatus(AuditStatus.COLLECTOR_ITEM_ERROR);

            buildAuditResponse.setLastUpdated(collectorItem.getLastUpdated());
            buildAuditResponse.setErrorMessage(collectorItem.getErrors().get(0).getErrorMessage());
            return buildAuditResponse;
        }

        //Segregation of Pipeline Environments
        //Check Prod job URL to validate Prod deploy job in Enterprise Jenkins Prod folder
        buildAuditResponse.setEnvironment(collectorItem.getEnvironment());

        //Segregation of access to Pipeline Environments
        //Check Jenkins Job config log to validate pr author is not modifying the Prod Job
        //since beginDate and endDate are the same column and between is excluding the edge values, we need to subtract/add a millisec
        buildAuditResponse.setConfigHistory(collItemCfgHistRepository.findByCollectorItemIdAndJobAndJobUrlAndTimestampBetweenOrderByTimestampDesc(collectorItem.getId(), jobName, jobUrl, beginDt - 1, endDt + 1));

        if ("PROD".equalsIgnoreCase(buildAuditResponse.getEnvironment())) {
            if (jobUrl.toUpperCase(Locale.ENGLISH).contains("NON-PROD")) {
                buildAuditResponse.addAuditStatus(AuditStatus.BUILD_JOB_IS_NON_PROD);
            } else {
                buildAuditResponse.addAuditStatus(AuditStatus.BUILD_JOB_IS_PROD);
            }
        } else {
            buildAuditResponse.addAuditStatus(AuditStatus.BUILD_JOB_IS_NON_PROD);
        }

        return buildAuditResponse;
    }
}
