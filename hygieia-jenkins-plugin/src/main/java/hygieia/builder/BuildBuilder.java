package hygieia.builder;

import com.capitalone.dashboard.model.BuildStatus;
import com.capitalone.dashboard.model.SCM;
import com.capitalone.dashboard.request.BuildDataCreateRequest;
import hudson.model.AbstractBuild;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.scm.ChangeLogSet;
import hygieia.utils.HygieiaUtils;
import org.jenkinsci.plugins.workflow.job.WorkflowRun;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static hygieia.utils.HygieiaUtils.getRepoBranch;

public class BuildBuilder {

    private AbstractBuild<?, ?> build;
    private Run<?, ?> run;
    private String jenkinsName;
    private TaskListener listener;
    private boolean isComplete;
    private BuildStatus result;
    private boolean buildChangeSet;

    public BuildBuilder(AbstractBuild<?, ?> build, String jenkinsName, TaskListener listener, boolean isComplete, boolean buildChangeSet) {
        this.build = build;
        this.jenkinsName = jenkinsName;
        this.listener = listener;
        this.buildChangeSet = buildChangeSet;
        this.isComplete = isComplete;
        if(!isComplete) {
            this.result =BuildStatus.InProgress;
        }
    }

    public BuildBuilder(Run<?, ?> run, String jenkinsName, TaskListener listener, BuildStatus result, boolean buildChangeSet) {
        this.run = run;
        this.jenkinsName = jenkinsName;
        this.listener = listener;
        this.result = result;
        this.buildChangeSet = buildChangeSet;
        if (run instanceof AbstractBuild) {
            this.build = (AbstractBuild<?, ?>) run;
        }
    }

    private BuildDataCreateRequest createBuildRequestFromRun() {
        BuildDataCreateRequest request = new BuildDataCreateRequest();
        request.setNiceName(this.jenkinsName);
        request.setJobName(HygieiaUtils.getJobPath(this.run));
        request.setBuildUrl(HygieiaUtils.getBuildUrl(this.run));
        request.setJobUrl(HygieiaUtils.getJobUrl(this.run));
        request.setInstanceUrl(HygieiaUtils.getInstanceUrl(this.run, this.listener));
        request.setNumber(HygieiaUtils.getBuildNumber(this.run));
        request.setStartTime(this.run.getStartTimeInMillis());
        request.setBuildStatus(this.result.toString());

        if (!this.result.equals(BuildStatus.InProgress)) {
            request.setDuration(System.currentTimeMillis() - this.run.getStartTimeInMillis());
            request.setEndTime(System.currentTimeMillis());
            if (this.buildChangeSet) {
                request.setCodeRepos(getRepoBranch(this.run));
                WorkflowRun wr = (WorkflowRun) this.run;
                request.setSourceChangeSet(getCommitList(wr.getChangeSets()));
            }
        }
        return request;
    }

    private BuildDataCreateRequest createBuildRequest() {
        BuildDataCreateRequest request = new BuildDataCreateRequest();
        boolean isBuildComplete = this.isComplete || !(Objects.equals(BuildStatus.InProgress, this.result));
        request.setNiceName(this.jenkinsName);
        request.setJobName(HygieiaUtils.getJobPath(this.build));
        request.setBuildUrl(HygieiaUtils.getBuildUrl(this.build));
        request.setJobUrl(HygieiaUtils.getJobUrl(this.build));
        request.setInstanceUrl(HygieiaUtils.getInstanceUrl(this.build, this.listener));
        request.setNumber(HygieiaUtils.getBuildNumber(this.build));
        request.setStartTime(this.build.getStartTimeInMillis());
        if (isBuildComplete) {
            request.setBuildStatus(Objects.requireNonNull(this.build.getResult()).toString());
            request.setDuration(this.build.getDuration());
            request.setEndTime(this.build.getStartTimeInMillis() + this.build.getDuration());
            if (this.buildChangeSet) {
                request.setCodeRepos(getRepoBranch(this.build));
                ChangeLogSet<? extends ChangeLogSet.Entry> sets = this.build.getChangeSet();
                List<ChangeLogSet<? extends ChangeLogSet.Entry>> changeLogSets = sets.isEmptySet() ? Collections.<ChangeLogSet<? extends ChangeLogSet.Entry>>emptyList() : Collections.<ChangeLogSet<? extends ChangeLogSet.Entry>>singletonList(sets);
                request.setSourceChangeSet(getCommitList(changeLogSets));
            }
        } else {
            request.setBuildStatus(BuildStatus.InProgress.toString());
        }
        return request;
    }

    public BuildDataCreateRequest getBuildData() {
        return (run == null || run instanceof AbstractBuild) ? createBuildRequest() : createBuildRequestFromRun();
    }

    private List<SCM> getCommitList(List<ChangeLogSet<? extends ChangeLogSet.Entry>> changeLogSets) {
        CommitBuilder commitBuilder = new CommitBuilder(changeLogSets);
        return commitBuilder.getCommits();
    }

}
