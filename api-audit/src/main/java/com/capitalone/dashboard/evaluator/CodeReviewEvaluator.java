package com.capitalone.dashboard.evaluator;

import com.capitalone.dashboard.model.AuditStatus;
import com.capitalone.dashboard.model.CollectorItem;
import com.capitalone.dashboard.model.CollectorType;
import com.capitalone.dashboard.model.Comment;
import com.capitalone.dashboard.model.Commit;
import com.capitalone.dashboard.model.CommitStatus;
import com.capitalone.dashboard.model.CommitType;
import com.capitalone.dashboard.model.Dashboard;
import com.capitalone.dashboard.model.GitRequest;
import com.capitalone.dashboard.model.Review;
import com.capitalone.dashboard.repository.CustomRepositoryQuery;
import com.capitalone.dashboard.response.CodeReviewAuditResponse;
import com.capitalone.dashboard.response.GenericAuditResponse;
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

import static com.capitalone.dashboard.response.GenericAuditResponse.CODE_REVIEW;

@Component
public class CodeReviewEvaluator extends Evaluator {

    private final CustomRepositoryQuery customRepositoryQuery;

    @Autowired
    public CodeReviewEvaluator(CustomRepositoryQuery customRepositoryQuery) {
        this.customRepositoryQuery = customRepositoryQuery;
    }

    @Override
    public GenericAuditResponse evaluate(Dashboard dashboard, long beginDate, long endDate, Collection<?> dummy) {
        return getCodeReviewAudit(dashboard, beginDate, endDate);

    }


    @Override
    public Collection<CodeReviewAuditResponse> evaluate(CollectorItem collectorItem, long beginDate, long endDate, Collection<?> dummy) {
        return  getPeerReviewResponses(collectorItem, beginDate, endDate);
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
        List<List<CodeReviewAuditResponse>> allReviews = new ArrayList<>();

        for (CollectorItem repoItem : repoItems) {
            String scmWidgetbranch = (String) repoItem.getOptions().get("branch");
            String scmWidgetrepoUrl = (String) repoItem.getOptions().get("url");
            GitHubParsedUrl gitHubParsed = new GitHubParsedUrl(scmWidgetrepoUrl);
            scmWidgetrepoUrl = gitHubParsed.getUrl();
            if (!StringUtils.isEmpty(scmWidgetbranch) && !StringUtils.isEmpty(scmWidgetrepoUrl)) {
                List<CodeReviewAuditResponse> reviewResponses = this.getPeerReviewResponses(repoItem, beginDate, endDate);
                allReviews.add(reviewResponses);
            }
        }
        genericAuditResponse.addResponse(CODE_REVIEW, allReviews);
        return genericAuditResponse;

    }

    public List<CodeReviewAuditResponse> getPeerReviewResponses(CollectorItem repoItem,
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

        if (!CollectionUtils.isEmpty(repoItem.getErrors())) {
            CodeReviewAuditResponse noPRsCodeReviewAuditResponse = new CodeReviewAuditResponse();
            noPRsCodeReviewAuditResponse.addAuditStatus(AuditStatus.COLLECTOR_ITEM_ERROR);

            noPRsCodeReviewAuditResponse.setLastUpdated(repoItem.getLastUpdated());
            noPRsCodeReviewAuditResponse.setScmBranch(scmBranch);
            noPRsCodeReviewAuditResponse.setScmUrl(scmUrl);
            noPRsCodeReviewAuditResponse.setErrorMessage(repoItem.getErrors().get(0).getErrorMessage());
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
            boolean peerReviewed = computePeerReviewStatus(pr, codeReviewAuditResponse);
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
            peerReviewResponseList.setScmUrl(scmUrl);
        });
        return allPeerReviews;
    }

    /**
     * Calculates the peer review status for a given pull request
     *
     * @param pr                 - pull request
     * @param codeReviewAuditResponse
     * @return
     */
    boolean computePeerReviewStatus(GitRequest pr, CodeReviewAuditResponse codeReviewAuditResponse) {
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
                            codeReviewAuditResponse.addAuditStatus(AuditStatus.PEER_REVIEW_LGTM_PENDING);
                            break;

                        case "error":
                            codeReviewAuditResponse.addAuditStatus(AuditStatus.PEER_REVIEW_LGTM_PENDING);
                            break;

                        case "success":
                            lgtmStateResult = true;
                            codeReviewAuditResponse.addAuditStatus(AuditStatus.PEER_REVIEW_LGTM_SUCCESS);
                            break;

                        default:
                            codeReviewAuditResponse.addAuditStatus(AuditStatus.PEER_REVIEW_LGTM_UNKNOWN);
                            break;
                    }
                }
            }

            if (lgtmAttempted) {
                //if lgtm self-review, then no peer-review was done unless someone else looked at it
                if (!CollectionUtils.isEmpty(codeReviewAuditResponse.getAuditStatuses()) &&
                        codeReviewAuditResponse.getAuditStatuses().contains(AuditStatus.COMMITAUTHOR_EQ_MERGECOMMITER) &&
                        !isPRLookedAtByPeer(pr)) {
                    codeReviewAuditResponse.addAuditStatus(AuditStatus.PEER_REVIEW_LGTM_SELF_APPROVAL);
                    return false;
                }
                return lgtmStateResult;
            }
        }


        if (!CollectionUtils.isEmpty(reviews)) {
            for (Review review : reviews) {
                if ("approved".equalsIgnoreCase(review.getState())) {
                    //review done using GitHub Review workflow
                    codeReviewAuditResponse.addAuditStatus(AuditStatus.PEER_REVIEW_GHR);
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
}
