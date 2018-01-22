package com.capitalone.dashboard.evaluator;

import com.capitalone.dashboard.common.CommonCodeReview;
import com.capitalone.dashboard.model.AuditException;
import com.capitalone.dashboard.model.CollectorItem;
import com.capitalone.dashboard.model.CollectorType;
import com.capitalone.dashboard.model.Commit;
import com.capitalone.dashboard.model.CommitType;
import com.capitalone.dashboard.model.Dashboard;
import com.capitalone.dashboard.model.GitRequest;
import com.capitalone.dashboard.repository.CommitRepository;
import com.capitalone.dashboard.repository.GitRequestRepository;
import com.capitalone.dashboard.response.CodeReviewAuditResponseV2;
import com.capitalone.dashboard.status.CodeReviewAuditStatus;
import com.capitalone.dashboard.util.GitHubParsedUrl;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Component
public class CodeReviewEvaluator extends Evaluator<CodeReviewAuditResponseV2> {

    private final CommitRepository commitRepository;
    private final GitRequestRepository gitRequestRepository;

    @Autowired
    public CodeReviewEvaluator(CommitRepository commitRepository, GitRequestRepository gitRequestRepository) {
        this.commitRepository = commitRepository;
        this.gitRequestRepository = gitRequestRepository;
    }


    @Override
    public Collection<CodeReviewAuditResponseV2> evaluate(Dashboard dashboard, long beginDate, long endDate, Map<?, ?> data) throws AuditException {
        List<CodeReviewAuditResponseV2> responseV2s = new ArrayList<>();
        List<CollectorItem> repoItems = getCollectorItems(dashboard, "repo", CollectorType.SCM);
        if (CollectionUtils.isEmpty(repoItems)) {
            throw new AuditException("No code repository configured", AuditException.NO_COLLECTOR_ITEM_CONFIGURED);
        }

        //making sure we have a goot url?
        repoItems.forEach(repoItem -> {
            String scmUrl = (String) repoItem.getOptions().get("url");
            String scmBranch = (String) repoItem.getOptions().get("branch");
            GitHubParsedUrl gitHubParsed = new GitHubParsedUrl(scmUrl);
            String parsedUrl = gitHubParsed.getUrl(); //making sure we have a goot url?
            CodeReviewAuditResponseV2 reviewResponse = evaluate(repoItem, beginDate, endDate, null);
            reviewResponse.setUrl(parsedUrl);
            reviewResponse.setBranch(scmBranch);
            reviewResponse.setLastUpdated(repoItem.getLastUpdated());
            responseV2s.add(reviewResponse);
        });
        return responseV2s;
    }

    @Override
    public CodeReviewAuditResponseV2 evaluate(CollectorItem collectorItem, long beginDate, long endDate, Map<?, ?> data) {
        return getPeerReviewResponses(collectorItem, beginDate, endDate);
    }


    /**
     * Return an empty response in error situation
     *
     * @param repoItem
     * @param scmBranch
     * @param scmUrl
     * @return
     */
    private CodeReviewAuditResponseV2 getErrorResponse(CollectorItem repoItem, String scmBranch, String scmUrl) {
        CodeReviewAuditResponseV2 noPRsCodeReviewAuditResponse = new CodeReviewAuditResponseV2();
        noPRsCodeReviewAuditResponse.addAuditStatus(CodeReviewAuditStatus.COLLECTOR_ITEM_ERROR);

        noPRsCodeReviewAuditResponse.setLastUpdated(repoItem.getLastUpdated());
        noPRsCodeReviewAuditResponse.setBranch(scmBranch);
        noPRsCodeReviewAuditResponse.setUrl(scmUrl);
        noPRsCodeReviewAuditResponse.setErrorMessage(repoItem.getErrors() == null ? null : repoItem.getErrors().get(0).getErrorMessage());
        return noPRsCodeReviewAuditResponse;
    }

    private CodeReviewAuditResponseV2 getPeerReviewResponses(CollectorItem repoItem,
                                                             long beginDt, long endDt) {


        String scmUrl = (String) repoItem.getOptions().get("url");
        String scmBranch = (String) repoItem.getOptions().get("branch");

        GitHubParsedUrl gitHubParsed = new GitHubParsedUrl(scmUrl);

        String parsedUrl = gitHubParsed.getUrl(); //making sure we have a goot url?

        if (StringUtils.isEmpty(scmBranch) || StringUtils.isEmpty(scmUrl)) {
            return getErrorResponse(repoItem, scmBranch, parsedUrl);
        }

        if (!CollectionUtils.isEmpty(repoItem.getErrors())) {
            return getErrorResponse(repoItem, scmBranch, parsedUrl);

        }

        List<GitRequest> pullRequests = gitRequestRepository.findByCollectorItemIdAndMergedAtIsBetween(repoItem.getId(), beginDt-1, endDt+1);
        List<Commit> commits = commitRepository.findByCollectorItemIdAndScmCommitTimestampIsBetween(repoItem.getId(), beginDt-1, endDt+1);
        commits.sort(Comparator.comparing(Commit::getScmCommitTimestamp).reversed());
        pullRequests.sort(Comparator.comparing(GitRequest::getMergedAt).reversed());

        CodeReviewAuditResponseV2 reviewAuditResponseV2 = new CodeReviewAuditResponseV2();

        if (CollectionUtils.isEmpty(pullRequests)) {
            reviewAuditResponseV2.addAuditStatus(CodeReviewAuditStatus.NO_PULL_REQ_FOR_DATE_RANGE);
        }
        if (CollectionUtils.isEmpty(commits)) {
            reviewAuditResponseV2.addAuditStatus(CodeReviewAuditStatus.NO_COMMIT_FOR_DATE_RANGE);
        }
        reviewAuditResponseV2.setUrl(parsedUrl);
        reviewAuditResponseV2.setBranch(scmBranch);
        reviewAuditResponseV2.setLastCommitTime(CollectionUtils.isEmpty(commits)? 0 : commits.get(0).getScmCommitTimestamp());
        reviewAuditResponseV2.setLastPRMergeTime(CollectionUtils.isEmpty(pullRequests)? 0 : pullRequests.get(0).getMergedAt());
        if (reviewAuditResponseV2.getLastCommitTime() > reviewAuditResponseV2.getLastPRMergeTime()) {
            reviewAuditResponseV2.addAuditStatus(CodeReviewAuditStatus.COMMIT_AFTER_PR_MERGE);
        }
        reviewAuditResponseV2.setLastUpdated(repoItem.getLastUpdated());

        //                reviewAuditResponseV2.addPullRequest(pullRequestAudit);
        pullRequests.forEach(pr -> {
            String mergeSha = pr.getScmRevisionNumber();
            Optional<Commit> mergeOptionalCommit = commits.stream().filter(c -> Objects.equals(c.getScmRevisionNumber(), mergeSha)).findFirst();
            Commit mergeCommit = mergeOptionalCommit.orElse(null);
            if (mergeCommit == null) {
//                reviewAuditResponseV2.addPullRequest(pullRequestAudit);
                return;
            }
            CodeReviewAuditResponseV2.PullRequestAudit pullRequestAudit = new CodeReviewAuditResponseV2.PullRequestAudit();
            pullRequestAudit.setPullRequest(pr);
            List<Commit> commitsRelatedToPr = pr.getCommits();
            commitsRelatedToPr.sort(Comparator.comparing(e -> (e.getScmCommitTimestamp())));
            pullRequestAudit.addAuditStatus(pr.getUserId().equalsIgnoreCase(mergeCommit.getScmAuthorLogin()) ? CodeReviewAuditStatus.COMMITAUTHOR_EQ_MERGECOMMITER : CodeReviewAuditStatus.COMMITAUTHOR_NE_MERGECOMMITER);
            boolean peerReviewed = CommonCodeReview.computePeerReviewStatus(pr, settings, pullRequestAudit);
            pullRequestAudit.addAuditStatus(peerReviewed ? CodeReviewAuditStatus.PULLREQ_REVIEWED_BY_PEER : CodeReviewAuditStatus.PULLREQ_NOT_PEER_REVIEWED);
            String sourceRepo = pr.getSourceRepo();
            String targetRepo = pr.getTargetRepo();
            pullRequestAudit.addAuditStatus(sourceRepo == null ? CodeReviewAuditStatus.GIT_FORK_STRATEGY : sourceRepo.equalsIgnoreCase(targetRepo) ? CodeReviewAuditStatus.GIT_BRANCH_STRATEGY : CodeReviewAuditStatus.GIT_FORK_STRATEGY);
            reviewAuditResponseV2.addPullRequest(pullRequestAudit);
        });

        //check any commits not directly tied to pr
        commits.stream().filter(commit -> StringUtils.isEmpty(commit.getPullNumber()) && commit.getType() == CommitType.New).forEach(reviewAuditResponseV2::addDirectCommit);
        return reviewAuditResponseV2;
    }
}
