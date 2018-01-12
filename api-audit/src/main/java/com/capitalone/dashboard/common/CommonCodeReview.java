package com.capitalone.dashboard.common;

import com.capitalone.dashboard.ApiSettings;
import com.capitalone.dashboard.model.AuditStatus;
import com.capitalone.dashboard.model.Comment;
import com.capitalone.dashboard.model.CommitStatus;
import com.capitalone.dashboard.model.GitRequest;
import com.capitalone.dashboard.model.Review;
import com.capitalone.dashboard.response.AuditReviewResponse;
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
    public static boolean computePeerReviewStatus(GitRequest pr, ApiSettings settings, AuditReviewResponse auditReviewResponse) {
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
                            auditReviewResponse.addAuditStatus(AuditStatus.PEER_REVIEW_LGTM_PENDING);
                            break;

                        case "error":
                            auditReviewResponse.addAuditStatus(AuditStatus.PEER_REVIEW_LGTM_PENDING);
                            break;

                        case "success":
                            lgtmStateResult = true;
                            auditReviewResponse.addAuditStatus(AuditStatus.PEER_REVIEW_LGTM_SUCCESS);
                            break;

                        default:
                            auditReviewResponse.addAuditStatus(AuditStatus.PEER_REVIEW_LGTM_UNKNOWN);
                            break;
                    }
                }
            }

            if (lgtmAttempted) {
                //if lgtm self-review, then no peer-review was done unless someone else looked at it
                if (!CollectionUtils.isEmpty(auditReviewResponse.getAuditStatuses()) &&
                        auditReviewResponse.getAuditStatuses().contains(AuditStatus.COMMITAUTHOR_EQ_MERGECOMMITER) &&
                        !isPRLookedAtByPeer(pr)) {
                    auditReviewResponse.addAuditStatus(AuditStatus.PEER_REVIEW_LGTM_SELF_APPROVAL);
                    return false;
                }
                return lgtmStateResult;
            }
        }


        if (!CollectionUtils.isEmpty(reviews)) {
            for (Review review : reviews) {
                if ("approved".equalsIgnoreCase(review.getState())) {
                    //review done using GitHub Review workflow
                    auditReviewResponse.addAuditStatus(AuditStatus.PEER_REVIEW_GHR);
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
        commentUsers.remove(pr.getUserId());
        reviewAuthors.remove(pr.getUserId());

        return !CollectionUtils.isEmpty(pr.getReviews()) || (commentUsers.size() > 0) || (reviewAuthors.size() > 0);
    }
}
