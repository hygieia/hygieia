package com.capitalone.dashboard.audit.service;

import com.capitalone.dashboard.model.Commit;
import com.capitalone.dashboard.model.GitRequest;

import java.util.List;

public interface AuditService {
    List<GitRequest> getPullRequests(String repo, String branch, long beginDt, long endDt);

    List<Commit> getCommitsBySha (String scmRevisionNumber);
}
