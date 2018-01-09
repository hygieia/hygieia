package com.capitalone.dashboard.service;

import com.capitalone.dashboard.response.PeerReviewResponse;

import java.util.Collection;

public interface PeerReviewAuditService {
    Collection<PeerReviewResponse> getPeerReviewResponses(String repo, String branch, String scmName, long beginDate, long endDate);

}
