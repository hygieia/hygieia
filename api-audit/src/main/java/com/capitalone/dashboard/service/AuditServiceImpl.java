package com.capitalone.dashboard.service;

import com.capitalone.dashboard.model.Commit;
import com.capitalone.dashboard.model.GitRequest;
import com.capitalone.dashboard.repository.CommitRepository;
import com.capitalone.dashboard.repository.GitRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AuditServiceImpl implements AuditService {

    private GitRequestRepository gitRequestRepository;
    private final CommitRepository commitRepository;

    @Autowired
    public AuditServiceImpl(GitRequestRepository gitRequestRepository, CommitRepository commitRepository) {
        this.gitRequestRepository = gitRequestRepository;
        this.commitRepository = commitRepository;
    }

    public List<GitRequest> getPullRequests(String repo, String branch, long beginDt, long endDt) {
        List<GitRequest> pullRequests = gitRequestRepository.findByScmUrlAndScmBranchAndCreatedAtGreaterThanEqualAndMergedAtLessThanEqual(repo, branch, beginDt, endDt);
        return pullRequests;
    }

    public List<Commit> getCommitsBySha (String scmRevisionNumber) {
        return commitRepository.findByScmRevisionNumber(scmRevisionNumber);
    }
}
