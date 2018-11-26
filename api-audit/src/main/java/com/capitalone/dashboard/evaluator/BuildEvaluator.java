package com.capitalone.dashboard.evaluator;

import com.capitalone.dashboard.common.CommonCodeReview;
import com.capitalone.dashboard.misc.HygieiaException;
import com.capitalone.dashboard.model.AuditException;
import com.capitalone.dashboard.model.Build;
import com.capitalone.dashboard.model.CollectorItem;
import com.capitalone.dashboard.model.CollectorItemConfigHistory;
import com.capitalone.dashboard.model.CollectorType;
import com.capitalone.dashboard.model.Dashboard;
import com.capitalone.dashboard.model.RepoBranch;
import com.capitalone.dashboard.repository.BuildRepository;
import com.capitalone.dashboard.repository.CollItemConfigHistoryRepository;
import com.capitalone.dashboard.repository.CommitRepository;
import com.capitalone.dashboard.response.BuildAuditResponse;
import com.capitalone.dashboard.status.BuildAuditStatus;
import com.capitalone.dashboard.util.GitHubParsedUrl;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class BuildEvaluator extends Evaluator<BuildAuditResponse> {

    private final BuildRepository buildRepository;
    private final CollItemConfigHistoryRepository collItemConfigHistoryRepository;
    private final CommitRepository commitRepository;

    @Autowired
    public BuildEvaluator(BuildRepository buildRepository, CollItemConfigHistoryRepository collItemConfigHistoryRepository, CommitRepository commitRepository) {
        this.buildRepository = buildRepository;
        this.collItemConfigHistoryRepository = collItemConfigHistoryRepository;
        this.commitRepository = commitRepository;
    }


    @Override
    public Collection<BuildAuditResponse> evaluate(Dashboard dashboard, long beginDate, long endDate, Map<?, ?> data) throws AuditException {
        List<CollectorItem> buildItems = getCollectorItems(dashboard, "build", CollectorType.Build);
        List<CollectorItem> repoItems = getCollectorItems(dashboard, "repo", CollectorType.SCM);

        if (CollectionUtils.isEmpty(buildItems)) {
            throw new AuditException("No code repository configured", AuditException.NO_COLLECTOR_ITEM_CONFIGURED);
        }

        Map<String, List<CollectorItem>> repoData = new HashMap<>();
        repoData.put("repos", repoItems);
        return buildItems.stream().map(item -> evaluate(item, beginDate, endDate, repoData)).collect(Collectors.toList());
    }

    @Override
    public BuildAuditResponse evaluate(CollectorItem collectorItem, long beginDate, long endDate, Map<?, ?> data) {
        List<CollectorItem> repoItems = (List<CollectorItem>) data.get("repos");
        return getBuildJobAuditResponse(collectorItem, beginDate, endDate, repoItems);
    }

    private class ParsedRepo {
        String url;
        String branch;

        ParsedRepo(String url, String branch) {
            this.url = new GitHubParsedUrl(url).getUrl();
            this.branch = branch;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        String getBranch() {
            return branch;
        }

        public void setBranch(String branch) {
            this.branch = branch;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ParsedRepo that = (ParsedRepo) o;
            return Objects.equals(getUrl(), that.getUrl()) &&
                    Objects.equals(getBranch(), that.getBranch());
        }

        @Override
        public int hashCode() {

            return Objects.hash(getUrl(), getBranch());
        }
    }
    /**
     * Calculates Audit Response for a given dashboard
     *
     * @param beginDate
     * @param endDate
     * @return BuildAuditResponse for the build job for a given dashboard, begin and end date
     */
    private BuildAuditResponse getBuildJobAuditResponse(CollectorItem buildItem, long beginDate, long endDate, List<CollectorItem> repoItems) {


        BuildAuditResponse buildAuditResponse = new BuildAuditResponse();
        List<CollectorItemConfigHistory> jobConfigHists = collItemConfigHistoryRepository.findByCollectorItemIdAndTimestampIsBetweenOrderByTimestampDesc(buildItem.getId(), beginDate - 1, endDate + 1);

        //Check Jenkins Job config log to validate pr author is not modifying the Prod Job
        //since beginDate and endDate are the same column and between is excluding the edge values, we need to subtract/add a millisec
        buildAuditResponse.setConfigHistory(jobConfigHists);


        if (!CollectionUtils.isEmpty(repoItems)) {
            Build build = buildRepository.findTop1ByCollectorItemIdOrderByTimestampDesc(buildItem.getId());
            if (build != null) {
                List<RepoBranch> repoBranches = build.getCodeRepos();

                List<ParsedRepo> codeRepos = repoItems.stream().map(r -> new ParsedRepo((String)r.getOptions().get("url"), (String)r.getOptions().get("branch"))).collect(Collectors.toList());
                List<ParsedRepo> buildRepos = repoBranches.stream().map(b -> new ParsedRepo(b.getUrl(), b.getBranch())).collect(Collectors.toList());

                List<ParsedRepo> intersection =  codeRepos.stream().filter(buildRepos::contains).collect(Collectors.toList());

                buildAuditResponse.addAuditStatus(CollectionUtils.isEmpty(intersection) ? BuildAuditStatus.BUILD_REPO_MISMATCH : BuildAuditStatus.BUILD_MATCHES_REPO);
            } else {
                buildAuditResponse.addAuditStatus(BuildAuditStatus.NO_BUILD_FOUND);
            }
        }
        Set<String> codeAuthors = CommonCodeReview.getCodeAuthors(repoItems, beginDate, endDate, commitRepository);
        List<String> overlap = jobConfigHists.stream().map(CollectorItemConfigHistory::getUserID).filter(codeAuthors::contains).collect(Collectors.toList());
        buildAuditResponse.addAuditStatus(!CollectionUtils.isEmpty(overlap) ? BuildAuditStatus.BUILD_AUTHOR_EQ_REPO_AUTHOR : BuildAuditStatus.BUILD_AUTHOR_NE_REPO_AUTHOR);
        return buildAuditResponse;
    }

}
