package hygieia.builder;

import com.capitalone.dashboard.model.RepoBranch;
import com.capitalone.dashboard.model.SCM;
import hudson.model.AbstractBuild;
import hudson.scm.ChangeLogSet;
import hudson.scm.SubversionChangeLogSet;
import hudson.scm.SubversionSCM;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

public class CommitBuilder {
    private static final Logger logger = Logger.getLogger(CommitBuilder.class.getName());
    private List<SCM> commitList = new LinkedList<>();


    public CommitBuilder(AbstractBuild build) {
        buildCommits(build, build.getChangeSet());
    }


    private void buildCommits(AbstractBuild build, ChangeLogSet changeLogSet) {
        for (Object o : changeLogSet.getItems()) {
            ChangeLogSet.Entry entry = (ChangeLogSet.Entry) o;

            SCM commit = new SCM();
            if (entry.getAffectedFiles() != null) {
                commit.setNumberOfChanges(entry.getAffectedFiles().size());
            } else {
                commit.setNumberOfChanges(0);
            }
            if (!"".equals(entry.getAuthor().getFullName())) {
                commit.setScmAuthor(entry.getAuthor().getFullName());
            } else {
                commit.setScmAuthor(entry.getAuthor().getId());
            }
            commit.setScmCommitLog(entry.getMsg());
            commit.setScmCommitTimestamp(entry.getTimestamp()); //Timestamp will be -1 mostly per Jenkins documentation - as commits span over time.
            commit.setScmRevisionNumber(entry.getCommitId());
            RepoBranch repoBranch = getScmRepoBranch(build, entry.getCommitId());
            if (repoBranch != null) {
                commit.setScmUrl(repoBranch.getUrl());
                commit.setScmBranch(repoBranch.getBranch());
            }
            if (isNewCommit(commit)) {
                commitList.add(commit);
            }
            if ((entry.getParent() != null) && (!changeLogSet.equals(entry.getParent()))) {
                buildCommits(build, entry.getParent());
            }
        }
    }

    private RepoBranch getScmRepoBranch(AbstractBuild r, String commitId) {
        if (r.getProject().getScm() instanceof SubversionSCM) return getSVNRepoBranch(r, commitId);
        /**
         * At this point, its hard to associate a commit to a git branch and sometimes the repo (based on how
         * Git plugin is implemented. Will need more digging to find a good solution.
         * In the mean time, putting this commented code to at least show the intent!
         */
        //if (r.getProject().getScm() instanceof GitSCM) return getGitHubRepoBranch(r, commitId);
        return null;
    }

    private RepoBranch getSVNRepoBranch(AbstractBuild r, String commitId) {
        if (!(r.getChangeSet() instanceof SubversionChangeLogSet)) return null;
        SubversionChangeLogSet svnChanges = (SubversionChangeLogSet) r.getChangeSet();
        try {
            for (SubversionChangeLogSet.RevisionInfo rev : svnChanges.getRevisions()) {
                if (Long.parseLong(commitId) == rev.revision) {
                    RepoBranch repo = new RepoBranch();
                    repo.setUrl(rev.module);
                    return repo;
                }
            }
        } catch (IOException e) {
            logger.warning("Error getting SVN changes in Hygieia Plugin.");
        }
        return null;
    }



    private boolean isNewCommit(SCM commit) {
        for (SCM c : commitList) {
            if (c.getScmRevisionNumber().equals(commit.getScmRevisionNumber())) {
                return false;
            }
        }
        return true;
    }

    public List<SCM> getCommits() {
        return commitList;
    }
}
