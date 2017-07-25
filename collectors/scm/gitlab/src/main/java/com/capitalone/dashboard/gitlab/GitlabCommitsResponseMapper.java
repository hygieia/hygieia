package com.capitalone.dashboard.gitlab;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.joda.time.DateTime;
import org.springframework.stereotype.Component;

import com.capitalone.dashboard.gitlab.model.GitlabCommit;
import com.capitalone.dashboard.model.Commit;
import com.capitalone.dashboard.model.CommitType;

@Component
public class GitlabCommitsResponseMapper {
    
    public List<Commit> map(GitlabCommit[] gitlabCommits, String repoUrl, String branch) {
        List<Commit> commits = new ArrayList<>();
        for (GitlabCommit gitlabCommit : gitlabCommits) {
            commits.add(map(repoUrl, branch, gitlabCommit));     
        }
        
        return commits;
    }

    private Commit map(String repoUrl, String branch, GitlabCommit gitlabCommit) {
        long timestamp = new DateTime(gitlabCommit.getCreatedAt()).getMillis();
        int parentSize = CollectionUtils.isNotEmpty(gitlabCommit.getParentIds()) ? gitlabCommit.getParentIds().size() : 0;
        CommitType commitType = parentSize > 1 ? CommitType.Merge : CommitType.New;
        
        Commit commit = new Commit();
        commit.setTimestamp(System.currentTimeMillis());
        commit.setScmUrl(repoUrl);
        commit.setScmBranch(branch);
        commit.setScmRevisionNumber(gitlabCommit.getId());
        commit.setScmAuthor(gitlabCommit.getAuthorName());
        commit.setScmCommitLog(gitlabCommit.getMessage());
        commit.setScmCommitTimestamp(timestamp);
        commit.setNumberOfChanges(1);
        commit.setScmParentRevisionNumbers(gitlabCommit.getParentIds());
        commit.setType(commitType);
        return commit;
    }

    
}
