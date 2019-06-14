package hygieia.builder;

import com.capitalone.dashboard.model.BuildStage;
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
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import static hygieia.utils.HygieiaUtils.getRepoBranch;

public class BuildBuilder {

    public BuildBuilder() {
    }

    public BuildDataCreateRequest createBuildRequestFromRun(Run<?, ?> run, String jenkinsName, TaskListener listener, BuildStatus result, boolean buildChangeSet, LinkedList<BuildStage> stages, String startedBy) {

        BuildDataCreateRequest request = new BuildDataCreateRequest();
        request.setNiceName(jenkinsName);
        request.setJobName(HygieiaUtils.getJobPath(run));
        request.setBuildUrl(HygieiaUtils.getBuildUrl(run));
        request.setJobUrl(HygieiaUtils.getJobUrl(run));
        request.setInstanceUrl(HygieiaUtils.getInstanceUrl(run, listener));
        request.setNumber(HygieiaUtils.getBuildNumber(run));
        request.setStartTime(run.getStartTimeInMillis());
        request.setBuildStatus(result.toString());
        request.setStages(stages);
        request.setStartedBy(startedBy);

        if (!result.equals(BuildStatus.InProgress)) {
            request.setDuration(System.currentTimeMillis() - run.getStartTimeInMillis());
            request.setEndTime(System.currentTimeMillis());
            if (buildChangeSet) {
                request.setCodeRepos(getRepoBranch(run));
                if( run instanceof WorkflowRun) {
                    WorkflowRun wr = (WorkflowRun) run;
                    request.setSourceChangeSet(getCommitList(wr.getChangeSets()));
                }
            }
        }
        return request;
    }

    public BuildDataCreateRequest createBuildRequest(AbstractBuild<?, ?> build, String jenkinsName, TaskListener listener, boolean isComplete, boolean buildChangeSet, LinkedList<BuildStage> stages, String startedBy) {
        BuildDataCreateRequest request = new BuildDataCreateRequest();
        BuildStatus result = null;
        if(!isComplete) {
            result =BuildStatus.InProgress;
        }
        boolean isBuildComplete = isComplete || !(Objects.equals(BuildStatus.InProgress, result));
        request.setNiceName(jenkinsName);
        request.setJobName(HygieiaUtils.getJobPath(build));
        request.setBuildUrl(HygieiaUtils.getBuildUrl(build));
        request.setJobUrl(HygieiaUtils.getJobUrl(build));
        request.setInstanceUrl(HygieiaUtils.getInstanceUrl(build, listener));
        request.setNumber(HygieiaUtils.getBuildNumber(build));
        request.setStartTime(build.getStartTimeInMillis());
        request.setStages(stages);
        request.setStartedBy(startedBy);
        if (isBuildComplete) {
            request.setBuildStatus(Objects.requireNonNull(HygieiaUtils.getBuildStatus(build.getResult())).toString());
            request.setDuration(build.getDuration());
            request.setEndTime(build.getStartTimeInMillis() + build.getDuration());
            if (buildChangeSet) {
                request.setCodeRepos(getRepoBranch(build));
                ChangeLogSet<? extends ChangeLogSet.Entry> sets = build.getChangeSet();
                List<ChangeLogSet<? extends ChangeLogSet.Entry>> changeLogSets = sets.isEmptySet() ? Collections.emptyList() : Collections.singletonList(sets);
                request.setSourceChangeSet(getCommitList(changeLogSets));
            }
        } else {
            request.setBuildStatus(BuildStatus.InProgress.toString());
        }
        return request;
    }
    
    private List<SCM> getCommitList(List<ChangeLogSet<? extends ChangeLogSet.Entry>> changeLogSets) {
        CommitBuilder commitBuilder = new CommitBuilder(changeLogSets);
        return commitBuilder.getCommits();
    }

}
