package com.capitalone.dashboard.evaluator;

import com.capitalone.dashboard.ApiSettings;
import com.capitalone.dashboard.common.CommonCodeReview;
import com.capitalone.dashboard.misc.HygieiaException;
import com.capitalone.dashboard.model.AuditException;
import com.capitalone.dashboard.model.AuditStatus;
import com.capitalone.dashboard.model.CollectorItem;
import com.capitalone.dashboard.model.Comment;
import com.capitalone.dashboard.model.Commit;
import com.capitalone.dashboard.model.CommitStatus;
import com.capitalone.dashboard.model.CommitType;
import com.capitalone.dashboard.model.GitRequest;
import com.capitalone.dashboard.model.Review;
import com.capitalone.dashboard.repository.CustomRepositoryQuery;
import com.capitalone.dashboard.response.CodeReviewAuditResponse;
import com.capitalone.dashboard.util.GitHubParsedUrl;
import com.google.common.collect.Sets;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class CodeReviewEvaluatorLegacy extends LegacyEvaluator{

    private final CustomRepositoryQuery customRepositoryQuery;
    protected final ApiSettings settings;

    @Autowired
    public CodeReviewEvaluatorLegacy(CustomRepositoryQuery customRepositoryQuery, ApiSettings settings) {
        this.customRepositoryQuery = customRepositoryQuery;
        this.settings = settings;
    }



    @Override
    public List<CodeReviewAuditResponse> evaluate(CollectorItem collectorItem, long beginDate, long endDate, Collection data) throws AuditException {
        return getPeerReviewResponses(collectorItem,beginDate,endDate);
    }

    /**
     * Return an empty response in error situation
     * @param repoItem
     * @param scmBranch
     * @param scmUrl
     * @return
     */
    private CodeReviewAuditResponse getErrorResponse(CollectorItem repoItem, String scmBranch, String scmUrl) {
        CodeReviewAuditResponse noPRsCodeReviewAuditResponse = new CodeReviewAuditResponse();
        noPRsCodeReviewAuditResponse.addAuditStatus(AuditStatus.COLLECTOR_ITEM_ERROR);

        noPRsCodeReviewAuditResponse.setLastUpdated(repoItem.getLastUpdated());
        noPRsCodeReviewAuditResponse.setScmBranch(scmBranch);
        noPRsCodeReviewAuditResponse.setScmUrl(scmUrl);
        noPRsCodeReviewAuditResponse.setErrorMessage(repoItem.getErrors().get(0).getErrorMessage());
        return noPRsCodeReviewAuditResponse;
    }

    private List<CodeReviewAuditResponse> getPeerReviewResponses(CollectorItem repoItem,
                                                                long beginDt, long endDt) {

        List<CodeReviewAuditResponse> allPeerReviews = new ArrayList<>();

        if (repoItem == null) {
            CodeReviewAuditResponse codeReviewAuditResponse = new CodeReviewAuditResponse();
            codeReviewAuditResponse.addAuditStatus(AuditStatus.REPO_NOT_CONFIGURED);
            allPeerReviews.add(codeReviewAuditResponse);
            return allPeerReviews;
        }

        String scmUrl = (String) repoItem.getOptions().get("url");
        String scmBranch = (String) repoItem.getOptions().get("branch");

        GitHubParsedUrl gitHubParsed = new GitHubParsedUrl(scmUrl);

        String parsedUrl = gitHubParsed.getUrl(); //making sure we have a goot url

        if (StringUtils.isEmpty(scmBranch) || StringUtils.isEmpty(scmUrl)) {
            CodeReviewAuditResponse noPRsCodeReviewAuditResponse = getErrorResponse(repoItem, scmBranch, parsedUrl);
            allPeerReviews.add(noPRsCodeReviewAuditResponse);
            return allPeerReviews;
        }

        if (!CollectionUtils.isEmpty(repoItem.getErrors())) {
            CodeReviewAuditResponse noPRsCodeReviewAuditResponse = getErrorResponse(repoItem, scmBranch, parsedUrl);
            allPeerReviews.add(noPRsCodeReviewAuditResponse);
            return allPeerReviews;
        }

        List<GitRequest> pullRequests = customRepositoryQuery.findByScmUrlIgnoreCaseAndScmBranchIgnoreCaseAndMergedAtGreaterThanEqualAndMergedAtLessThanEqual(scmUrl, scmBranch, beginDt, endDt);
        List<Commit> commits = customRepositoryQuery.findByScmUrlAndScmBranchAndScmCommitTimestampGreaterThanEqualAndScmCommitTimestampLessThanEqual(scmUrl, scmBranch, beginDt, endDt);

        if (CollectionUtils.isEmpty(pullRequests)) {
            CodeReviewAuditResponse noPRsCodeReviewAuditResponse = new CodeReviewAuditResponse();
            noPRsCodeReviewAuditResponse.addAuditStatus(AuditStatus.NO_PULL_REQ_FOR_DATE_RANGE);
            allPeerReviews.add(noPRsCodeReviewAuditResponse);
        }

        //            Commit mergeCommit = commitRepository.findByScmRevisionNumberAndScmUrlIgnoreCase(mergeSha, pr.getScmUrl());
//check for pr author <> pr merger
//check to see if pr was reviewed
//type of branching strategy
        pullRequests.forEach(pr -> {
            CodeReviewAuditResponse codeReviewAuditResponse = new CodeReviewAuditResponse();
            codeReviewAuditResponse.setPullRequest(pr);
            String mergeSha = pr.getScmRevisionNumber();
            Optional<Commit> mergeOptionalCommit = commits.stream().filter(c -> Objects.equals(c.getScmRevisionNumber(), mergeSha)).findFirst();
            Commit mergeCommit = mergeOptionalCommit.orElse(null);
            if (mergeCommit == null) {
                return;
            }
            List<Commit> commitsRelatedToPr = pr.getCommits();
            commitsRelatedToPr.sort(Comparator.comparing(e -> (e.getScmCommitTimestamp())));
            codeReviewAuditResponse.addAuditStatus(pr.getUserId().equalsIgnoreCase(mergeCommit.getScmAuthorLogin()) ? AuditStatus.COMMITAUTHOR_EQ_MERGECOMMITER : AuditStatus.COMMITAUTHOR_NE_MERGECOMMITER);
            codeReviewAuditResponse.setCommits(commitsRelatedToPr);
            boolean peerReviewed = CommonCodeReview.computePeerReviewStatus(pr, settings, codeReviewAuditResponse);
            codeReviewAuditResponse.addAuditStatus(peerReviewed ? AuditStatus.PULLREQ_REVIEWED_BY_PEER : AuditStatus.PULLREQ_NOT_PEER_REVIEWED);
            String sourceRepo = pr.getSourceRepo();
            String targetRepo = pr.getTargetRepo();
            codeReviewAuditResponse.addAuditStatus(sourceRepo == null ? AuditStatus.GIT_FORK_STRATEGY : sourceRepo.equalsIgnoreCase(targetRepo) ? AuditStatus.GIT_BRANCH_STRATEGY : AuditStatus.GIT_FORK_STRATEGY);
            allPeerReviews.add(codeReviewAuditResponse);
        });

        //check any commits not directly tied to pr
        CodeReviewAuditResponse codeReviewAuditResponse = new CodeReviewAuditResponse();
        List<Commit> commitsNotDirectlyTiedToPr = new ArrayList<>();
        commits.forEach(commit -> {
            if (StringUtils.isEmpty(commit.getPullNumber()) && commit.getType() == CommitType.New) {
                commitsNotDirectlyTiedToPr.add(commit);
                codeReviewAuditResponse.addAuditStatus(commit.isFirstEverCommit() ? AuditStatus.DIRECT_COMMITS_TO_BASE_FIRST_COMMIT : AuditStatus.DIRECT_COMMITS_TO_BASE);
            }
        });
        if (!commitsNotDirectlyTiedToPr.isEmpty()) {
            codeReviewAuditResponse.setCommits(commitsNotDirectlyTiedToPr);
            allPeerReviews.add(codeReviewAuditResponse);
        }

        //pull requests in date range, but merged prior to 14 days so no commits available in hygieia
        if (!CollectionUtils.isEmpty(pullRequests)) {
            if (allPeerReviews.isEmpty()) {
                CodeReviewAuditResponse prsButNoCommitsInRangeCodeReviewAuditResponse = new CodeReviewAuditResponse();
                prsButNoCommitsInRangeCodeReviewAuditResponse.addAuditStatus(AuditStatus.NO_PULL_REQ_FOR_DATE_RANGE);
                allPeerReviews.add(prsButNoCommitsInRangeCodeReviewAuditResponse);
            }
        }

        allPeerReviews.forEach(peerReviewResponseList -> {
            peerReviewResponseList.setLastUpdated(repoItem.getLastUpdated());
            peerReviewResponseList.setScmBranch(scmBranch);
            peerReviewResponseList.setScmUrl(parsedUrl);
        });
        return allPeerReviews;
    }



}
