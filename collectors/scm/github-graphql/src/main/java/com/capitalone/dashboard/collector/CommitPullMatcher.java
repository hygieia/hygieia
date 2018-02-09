package com.capitalone.dashboard.collector;

import com.capitalone.dashboard.model.Commit;
import com.capitalone.dashboard.model.GitRequest;
import org.apache.commons.collections.CollectionUtils;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class CommitPullMatcher {

    /**
     * Normal merge: Match PR's commit sha's with commit list
     * Squash merge: Match PR's merge sha's with commit list
     * Rebase merge: Match PR's commit's "message"+"author name"+"date" with commit list
     * <p>
     * If match found, set the commit's PR number and possibly set the PR merge type
     * <p>
     * For setting type:
     * If PR commit's SHAs are all found in commit stream, then the commit for the merge sha is a merge commit.
     * In all other cases it is a new commit
     */

    public static List<Commit> matchCommitToPulls(List<Commit> commits, List<GitRequest> pullRequests) {
        List<Commit> newCommitList = new LinkedList<>();
        if (CollectionUtils.isEmpty(commits) || CollectionUtils.isEmpty(pullRequests)) {
            return commits;
        }
        //TODO: Need to optimize this method
        for (Commit commit : commits) {
            Iterator<GitRequest> pIter = pullRequests.iterator();
            boolean foundPull = false;
            while (!foundPull && pIter.hasNext()) {
                GitRequest pull = pIter.next();
                if (Objects.equals(pull.getScmRevisionNumber(), commit.getScmRevisionNumber()) ||
                        Objects.equals(pull.getScmMergeEventRevisionNumber(), commit.getScmRevisionNumber())) {
                    foundPull = true;
                    commit.setPullNumber(pull.getNumber());
                } else {
                    List<Commit> prCommits = pull.getCommits();
                    boolean foundCommit = false;
                    if (!CollectionUtils.isEmpty(prCommits)) {
                        Iterator<Commit> cIter = prCommits.iterator();
                        while (!foundCommit && cIter.hasNext()) {
                            Commit loopCommit = cIter.next();
                            if (Objects.equals(commit.getScmAuthor(), loopCommit.getScmAuthor()) &&
                                    (commit.getScmCommitTimestamp() == loopCommit.getScmCommitTimestamp()) &&
                                    Objects.equals(commit.getScmCommitLog(), loopCommit.getScmCommitLog())) {
                                foundCommit = true;
                                foundPull = true;
                                commit.setPullNumber(pull.getNumber());
                            }
                        }
                    }
                }
            }
            newCommitList.add(commit);
        }
        return newCommitList;
    }
}
