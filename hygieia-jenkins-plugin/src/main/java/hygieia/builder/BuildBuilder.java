package hygieia.builder;

import com.capitalone.dashboard.model.BuildStatus;
import com.capitalone.dashboard.model.RepoBranch;
import com.capitalone.dashboard.model.SCM;
import com.capitalone.dashboard.request.BuildDataCreateRequest;
import hudson.model.*;
import hudson.plugins.git.GitSCM;
import hudson.plugins.git.util.*;
import hudson.scm.ChangeLogSet;
import hudson.scm.SubversionSCM;
import hygieia.utils.HygieiaUtils;
import org.apache.commons.collections.CollectionUtils;
import org.jenkinsci.plugins.multiplescms.MultiSCM;
import org.jenkinsci.plugins.workflow.job.WorkflowRun;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import static hygieia.utils.HygieiaUtils.getEnvironmentVariable;
import static hygieia.utils.HygieiaUtils.getRepoBranch;

public class BuildBuilder {

    private static final Logger logger = Logger.getLogger(ArtifactBuilder.class.getName());
    private AbstractBuild<?, ?> build;
    private Run<?, ?> run;
    private String jenkinsName;
    private TaskListener listener;
    private boolean isComplete;
    private BuildDataCreateRequest request;
    private BuildStatus result;
    boolean buildChangeSet;

    public BuildBuilder(AbstractBuild<?, ?> build, String jenkinsName, TaskListener listener, boolean isComplete, boolean buildChangeSet) {
        this.build = build;
        this.jenkinsName = jenkinsName;
        this.listener = listener;
        this.isComplete = isComplete;
        this.buildChangeSet = buildChangeSet;
        createBuildRequest();
    }

    public BuildBuilder(Run<?, ?> run, String jenkinsName, TaskListener listener, BuildStatus result, boolean buildChangeSet) {
        this.run = run;
        this.jenkinsName = jenkinsName;
        this.listener = listener;
        this.result = result;
        this.buildChangeSet = buildChangeSet;
        if (run instanceof AbstractBuild) {
            this.build = (AbstractBuild<?, ?>) run;
            createBuildRequest();
        } else {
            createBuildRequestFromRun();
        }
    }

    private void createBuildRequestFromRun() {
        request = new BuildDataCreateRequest();
        request.setNiceName(jenkinsName);
        request.setJobName(HygieiaUtils.getJobName(run));
        request.setBuildUrl(HygieiaUtils.getBuildUrl(run));
        request.setJobUrl(HygieiaUtils.getJobUrl(run));
        request.setInstanceUrl(HygieiaUtils.getInstanceUrl(run, listener));
        request.setNumber(HygieiaUtils.getBuildNumber(run));
        request.setStartTime(run.getStartTimeInMillis());
        request.setBuildStatus(result.toString());

        if (!result.equals(BuildStatus.InProgress)) {
            request.setDuration(System.currentTimeMillis() - run.getStartTimeInMillis());
            request.setEndTime(System.currentTimeMillis());
            if (buildChangeSet) {
                request.setCodeRepos(getRepoBranch(run));
                WorkflowRun wr = (WorkflowRun) run;
                request.setSourceChangeSet(getCommitList(wr.getChangeSets()));
            }
        }
    }

    private void createBuildRequest() {
        request = new BuildDataCreateRequest();
        request.setNiceName(jenkinsName);
        request.setJobName(HygieiaUtils.getJobName(build));
        request.setBuildUrl(HygieiaUtils.getBuildUrl(build));
        request.setJobUrl(HygieiaUtils.getJobUrl(build));
        request.setInstanceUrl(HygieiaUtils.getInstanceUrl(build, listener));
        request.setNumber(HygieiaUtils.getBuildNumber(build));
        request.setStartTime(build.getStartTimeInMillis());
        if (isComplete) {
            request.setBuildStatus(build.getResult().toString());
            request.setDuration(build.getDuration());
            request.setEndTime(build.getStartTimeInMillis() + build.getDuration());
            if (buildChangeSet) {
                request.setCodeRepos(getRepoBranch(build));
                ChangeLogSet<? extends ChangeLogSet.Entry> sets = ((AbstractBuild) run).getChangeSet();
                List<ChangeLogSet<? extends ChangeLogSet.Entry>> changeLogSets = sets.isEmptySet() ? Collections.<ChangeLogSet<? extends ChangeLogSet.Entry>>emptyList() : Collections.<ChangeLogSet<? extends ChangeLogSet.Entry>>singletonList(sets);
                request.setSourceChangeSet(getCommitList(changeLogSets));
            }
        } else {
            request.setBuildStatus(BuildStatus.InProgress.toString());
        }
    }

    public BuildDataCreateRequest getBuildData() {
        return request;
    }

    private List<SCM> getCommitList(List<ChangeLogSet<? extends ChangeLogSet.Entry>> changeLogSets) {
        CommitBuilder commitBuilder = new CommitBuilder(changeLogSets);
        return commitBuilder.getCommits();
    }


}
