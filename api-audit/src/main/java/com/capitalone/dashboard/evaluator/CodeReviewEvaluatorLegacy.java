package com.capitalone.dashboard.evaluator;

import com.capitalone.dashboard.ApiSettings;
import com.capitalone.dashboard.common.CommonCodeReview;
import com.capitalone.dashboard.model.CollectorItem;
import com.capitalone.dashboard.model.Commit;
import com.capitalone.dashboard.model.CommitType;
import com.capitalone.dashboard.model.GitRequest;
import com.capitalone.dashboard.repository.CommitRepository;
import com.capitalone.dashboard.repository.GitRequestRepository;
import com.capitalone.dashboard.response.CodeReviewAuditResponse;
import com.capitalone.dashboard.status.CodeReviewAuditStatus;
import com.capitalone.dashboard.util.GitHubParsedUrl;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static com.capitalone.dashboard.status.CodeReviewAuditStatus.COLLECTOR_ITEM_ERROR;

@Component
public class CodeReviewEvaluatorLegacy extends LegacyEvaluator {

//    private final CustomRepositoryQuery customRepositoryQuery;
    private final CommitRepository commitRepository;
    private final GitRequestRepository gitRequestRepository;
    protected final ApiSettings settings;

    @Autowired
    public CodeReviewEvaluatorLegacy(CommitRepository commitRepository, GitRequestRepository gitRequestRepository, ApiSettings settings) {
        this.commitRepository = commitRepository;
        this.gitRequestRepository = gitRequestRepository;
        this.settings = settings;
    }


    @Override
    public List<CodeReviewAuditResponse> evaluate(CollectorItem collectorItem, long beginDate, long endDate, Collection data) {
        return getPeerReviewResponses(collectorItem, beginDate, endDate);
    }

    /**
     * Return an empty response in error situation
     *
     * @param repoItem  the repo item
     * @param scmBranch the scrm branch
     * @param scmUrl    the scm url
     * @return code review audit response
     */
    private CodeReviewAuditResponse getErrorResponse(CollectorItem repoItem, String scmBranch, String scmUrl) {
        CodeReviewAuditResponse noPRsCodeReviewAuditResponse = new CodeReviewAuditResponse();
        noPRsCodeReviewAuditResponse.addAuditStatus(COLLECTOR_ITEM_ERROR);

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

        List<GitRequest> pullRequests = gitRequestRepository.findByCollectorItemIdAndMergedAtIsBetween(repoItem.getId(), beginDt-1, endDt+1);
        List<Commit> commits = commitRepository.findByCollectorItemIdAndScmCommitTimestampIsBetween(repoItem.getId(), beginDt-1, endDt+1);
        if (CollectionUtils.isEmpty(pullRequests)) {
            CodeReviewAuditResponse noPRsCodeReviewAuditResponse = new CodeReviewAuditResponse();
            noPRsCodeReviewAuditResponse.addAuditStatus(CodeReviewAuditStatus.NO_PULL_REQ_FOR_DATE_RANGE);
            allPeerReviews.add(noPRsCodeReviewAuditResponse);
        }

        //            Commit mergeCommit = commitRepository.findByScmRevisionNumberAndScmUrlIgnoreCase(mergeSha, pr.getUrl());
//check for pr author <> pr merger
//check to see if pr was reviewed
//type of branching strategy
        pullRequests.stream().filter(pr -> "merged".equalsIgnoreCase(pr.getState())).forEach(pr -> {
            CodeReviewAuditResponse codeReviewAuditResponse = new CodeReviewAuditResponse();
            codeReviewAuditResponse.setPullRequest(pr);
            String mergeSha = pr.getScmRevisionNumber();
            Optional<Commit> mergeOptionalCommit = commits.stream().filter(c -> Objects.equals(c.getScmRevisionNumber(), mergeSha)).findFirst();
            Commit mergeCommit = mergeOptionalCommit.orElse(null);

            if (mergeCommit == null) {
                mergeOptionalCommit = commits.stream().filter(c -> Objects.equals(c.getScmRevisionNumber(), pr.getScmMergeEventRevisionNumber())).findFirst();
                mergeCommit = mergeOptionalCommit.orElse(null);
            }
            
            List<Commit> commitsRelatedToPr = pr.getCommits();
            commitsRelatedToPr.sort(Comparator.comparing(e -> (e.getScmCommitTimestamp())));
            if (mergeCommit == null) {
                codeReviewAuditResponse.addAuditStatus(CodeReviewAuditStatus.MERGECOMMITER_NOT_FOUND);
            } else {
                codeReviewAuditResponse.addAuditStatus(pr.getUserId().equalsIgnoreCase(mergeCommit.getScmAuthorLogin()) ? CodeReviewAuditStatus.COMMITAUTHOR_EQ_MERGECOMMITER : CodeReviewAuditStatus.COMMITAUTHOR_NE_MERGECOMMITER);
            }
            codeReviewAuditResponse.setCommits(commitsRelatedToPr);
            boolean peerReviewed = CommonCodeReview.computePeerReviewStatus(pr, settings, codeReviewAuditResponse, commits);
            codeReviewAuditResponse.addAuditStatus(peerReviewed ? CodeReviewAuditStatus.PULLREQ_REVIEWED_BY_PEER : CodeReviewAuditStatus.PULLREQ_NOT_PEER_REVIEWED);
            String sourceRepo = pr.getSourceRepo();
            String targetRepo = pr.getTargetRepo();
            codeReviewAuditResponse.addAuditStatus(sourceRepo == null ? CodeReviewAuditStatus.GIT_FORK_STRATEGY : sourceRepo.equalsIgnoreCase(targetRepo) ? CodeReviewAuditStatus.GIT_BRANCH_STRATEGY : CodeReviewAuditStatus.GIT_FORK_STRATEGY);
            allPeerReviews.add(codeReviewAuditResponse);
        });

        //check any commits not directly tied to pr
        CodeReviewAuditResponse codeReviewAuditResponse = new CodeReviewAuditResponse();
        List<Commit> commitsNotDirectlyTiedToPr = new ArrayList<>();
        commits.forEach(commit -> {
            if (StringUtils.isEmpty(commit.getPullNumber()) && commit.getType() == CommitType.New) {
                commitsNotDirectlyTiedToPr.add(commit);
                codeReviewAuditResponse.addAuditStatus(commit.isFirstEverCommit() ? CodeReviewAuditStatus.DIRECT_COMMITS_TO_BASE_FIRST_COMMIT : CodeReviewAuditStatus.DIRECT_COMMITS_TO_BASE);
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
                prsButNoCommitsInRangeCodeReviewAuditResponse.addAuditStatus(CodeReviewAuditStatus.NO_PULL_REQ_FOR_DATE_RANGE);
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
