package hygieia.builder;

import com.capitalone.dashboard.model.SCM;
import hudson.model.AbstractBuild;
import hudson.scm.ChangeLogSet;

import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

public class CommitBuilder {
    private static final Logger logger = Logger.getLogger(CommitBuilder.class.getName());
    private List<SCM> commitList = new LinkedList<>();


    public CommitBuilder(ChangeLogSet changeLogSet) {
        buildCommits(changeLogSet);
    }

    public CommitBuilder(List<ChangeLogSet<? extends ChangeLogSet.Entry>> changeLogSets) {
        if (changeLogSets != null) {
            buildCommits(changeLogSets);
        }
    }


    private void buildCommits(ChangeLogSet changeLogSet) {
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
            if (isNewCommit(commit)) {
                logger.info("Adding commit:" + commit.getScmUrl()+":"+commit.getScmBranch()+":"+commit.getScmRevisionNumber()+":"+commit.getScmCommitLog());
                commitList.add(commit);
            }
            if ((entry.getParent() != null) && (!changeLogSet.equals(entry.getParent()))) {
                buildCommits(entry.getParent());
            }
        }
    }


    private void buildCommits(List<ChangeLogSet<? extends ChangeLogSet.Entry>> changeLogSets) {
        for (ChangeLogSet changeLogSet : changeLogSets) {
            buildCommits(changeLogSet);
        }
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
