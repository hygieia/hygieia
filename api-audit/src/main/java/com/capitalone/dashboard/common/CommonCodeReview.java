package com.capitalone.dashboard.common;

import com.capitalone.dashboard.ApiSettings;
import com.capitalone.dashboard.model.CollectorItem;
import com.capitalone.dashboard.model.Comment;
import com.capitalone.dashboard.model.Commit;
import com.capitalone.dashboard.model.CommitStatus;
import com.capitalone.dashboard.model.GitRequest;
import com.capitalone.dashboard.model.Review;
import com.capitalone.dashboard.model.SCM;
import com.capitalone.dashboard.repository.CommitRepository;
import com.capitalone.dashboard.response.AuditReviewResponse;
import com.capitalone.dashboard.status.CodeReviewAuditStatus;
import com.capitalone.dashboard.util.GitHubParsedUrl;
import com.google.common.collect.Sets;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class CommonCodeReview {

    /**
     * Calculates the peer review status for a given pull request
     *
     * @param pr                      - pull request
     * @param auditReviewResponse - audit review response
     * @return boolean fail or pass
     */
    public static boolean computePeerReviewStatus(GitRequest pr, ApiSettings settings,  AuditReviewResponse<CodeReviewAuditStatus> auditReviewResponse) {
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
                            auditReviewResponse.addAuditStatus(CodeReviewAuditStatus.PEER_REVIEW_LGTM_PENDING);
                            break;

                        case "error":
                            auditReviewResponse.addAuditStatus(CodeReviewAuditStatus.PEER_REVIEW_LGTM_PENDING);
                            break;

                        case "success":
                            lgtmStateResult = true;
                            auditReviewResponse.addAuditStatus(CodeReviewAuditStatus.PEER_REVIEW_LGTM_SUCCESS);
                            break;

                        default:
                            auditReviewResponse.addAuditStatus(CodeReviewAuditStatus.PEER_REVIEW_LGTM_UNKNOWN);
                            break;
                    }
                }
            }

            if (lgtmAttempted) {
                //if lgtm self-review, then no peer-review was done unless someone else looked at it
                if (!CollectionUtils.isEmpty(auditReviewResponse.getAuditStatuses()) &&
                        !isPRLookedAtByPeer(pr)) {
                    auditReviewResponse.addAuditStatus(CodeReviewAuditStatus.PEER_REVIEW_LGTM_SELF_APPROVAL);
                    return false;
                }
                return lgtmStateResult;
            }
        }


        if (!CollectionUtils.isEmpty(reviews)) {
            for (Review review : reviews) {
                if ("approved".equalsIgnoreCase(review.getState())) {
                    //review done using GitHub Review workflow
                    auditReviewResponse.addAuditStatus(CodeReviewAuditStatus.PEER_REVIEW_GHR);
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Calculates if the PR was looked at by a peer
     *
     * @param pr
     * @return true if PR was looked at by at least one peer
     */
    private static boolean isPRLookedAtByPeer(GitRequest pr) {
        Set<String> commentUsers = pr.getComments() != null ? pr.getComments().stream().map(Comment::getUser).collect(Collectors.toCollection(HashSet::new)) : new HashSet<>();
        Set<String> reviewAuthors = pr.getReviews() != null ? pr.getReviews().stream().map(Review::getAuthor).collect(Collectors.toCollection(HashSet::new)) : new HashSet<>();

        Set<String> prCommitAuthors = pr.getCommits() != null ? pr.getCommits().stream().map(Commit::getScmAuthorLogin).collect(Collectors.toCollection(HashSet::new)) : new HashSet<>();
        prCommitAuthors.add(pr.getUserId());
        prCommitAuthors.remove("unknown");

        commentUsers.removeAll(prCommitAuthors);
        reviewAuthors.removeAll(prCommitAuthors);

        return (commentUsers.size() > 0) || (reviewAuthors.size() > 0);
    }

    public static Set<String> getCodeAuthors(List<CollectorItem> repoItems, long beginDate, long endDate, CommitRepository commitRepository) {
        Set<String> authors = new HashSet<>();
        //making sure we have a goot url?
        repoItems.forEach(repoItem -> {
            String scmUrl = (String) repoItem.getOptions().get("url");
            String scmBranch = (String) repoItem.getOptions().get("branch");
            GitHubParsedUrl gitHubParsed = new GitHubParsedUrl(scmUrl);
            String parsedUrl = gitHubParsed.getUrl(); //making sure we have a goot url?
            List<Commit> commits = commitRepository.findByCollectorItemIdAndScmCommitTimestampIsBetween(repoItem.getId(), beginDate-1, endDate+1);
            authors.addAll(commits.stream().map(SCM::getScmAuthor).collect(Collectors.toCollection(HashSet::new)));
        });
        return authors;
    }
}
