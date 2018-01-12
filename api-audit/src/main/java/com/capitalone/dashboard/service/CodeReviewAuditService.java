package com.capitalone.dashboard.service;

import com.capitalone.dashboard.model.AuditException;
import com.capitalone.dashboard.response.CodeReviewAuditResponse;

import java.util.Collection;

public interface CodeReviewAuditService {
    Collection<CodeReviewAuditResponse> getPeerReviewResponses(String repo, String branch, String scmName, long beginDate, long endDate) throws AuditException;
}
