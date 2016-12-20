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
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import static hygieia.utils.HygieiaUtils.getEnvironmentVariable;

public class BuildBuilder {

    private static final Logger logger = Logger.getLogger(ArtifactBuilder.class.getName());
    private AbstractBuild<?, ?> build;
    private Run<?, ?> run;
    private String jenkinsName;
    private TaskListener listener;
    private boolean isComplete;
    private BuildDataCreateRequest request;
    private BuildStatus result;

    public BuildBuilder(AbstractBuild<?, ?> build, String jenkinsName, TaskListener listener, boolean isComplete) {
        this.build = build;
        this.jenkinsName = jenkinsName;
        this.listener = listener;
        this.isComplete = isComplete;
        createBuildRequest();
    }

    public BuildBuilder(Run<?, ?> run, String jenkinsName, TaskListener listener, BuildStatus result) {
        this.run = run;
        this.jenkinsName = jenkinsName;
        this.listener = listener;
        this.result = result;
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
            request.setCodeRepos(getRepoBranch(run));
            WorkflowRun wr = (WorkflowRun) run;
            request.setSourceChangeSet(getCommitList(wr.getChangeSets()));
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
            request.setCodeRepos(getRepoBranch(build));
            request.setSourceChangeSet(getCommitList(build.getChangeSets()));
            request.setBuildStatus(build.getResult().toString());
            request.setDuration(build.getDuration());
            request.setEndTime(build.getStartTimeInMillis() + build.getDuration());
        } else {
            request.setBuildStatus("InProgress");
        }
    }

    public BuildDataCreateRequest getBuildData() {
        return request;
    }

    private List<SCM> getCommitList(List<ChangeLogSet<? extends ChangeLogSet.Entry>> changeLogSets) {
        CommitBuilder commitBuilder = new CommitBuilder(changeLogSets);
        return commitBuilder.getCommits();
    }

    private List<RepoBranch> getRepoBranchFromScmObject (Object scm, Run r) {
        List<RepoBranch> list = new ArrayList<>();
        if (scm instanceof SubversionSCM) {
            list = getSVNRepoBranch((SubversionSCM) scm);
        } else if (scm instanceof GitSCM) {
            list = getGitHubRepoBranch((GitSCM) scm, r);
        } else if (scm instanceof MultiSCM) {
            List<hudson.scm.SCM> multiScms = ((MultiSCM) scm).getConfiguredSCMs();
            listener.getLogger().print("ITS MULTI");
            for (hudson.scm.SCM hscm : multiScms) {
                if (hscm instanceof SubversionSCM) {
                    list.addAll(getSVNRepoBranch((SubversionSCM) hscm));
                } else if (hscm instanceof GitSCM) {
                    list.addAll(getGitHubRepoBranch((GitSCM) hscm, r));
                }
            }
        }
        return list;
    }

    private List<RepoBranch> getRepoBranch(AbstractBuild r) {
        List<RepoBranch> list = new ArrayList<>();
        return getRepoBranchFromScmObject(r.getProject().getScm(), r);
    }

    private List<RepoBranch> getRepoBranch(Run run) {
        List<RepoBranch> list = new ArrayList<>();
        if (run instanceof WorkflowRun) {
            WorkflowRun r = (WorkflowRun) run;
            for (Object o : r.getParent().getSCMs()) {
                list.addAll(getRepoBranchFromScmObject(o, run));
            }
        }
        return list;
    }

    private List<RepoBranch> getGitHubRepoBranch(GitSCM scm, Run r) {
        List<RepoBranch> list = new ArrayList<>();
        if (!CollectionUtils.isEmpty(scm.getBuildData(r).remoteUrls)) {
            for (String url : scm.getBuildData(r).remoteUrls) {
                if (url.endsWith(".git")) {
                    url = url.substring(0, url.lastIndexOf(".git"));
                }
                Map<String, hudson.plugins.git.util.Build> branches = scm.getBuildData(r).getBuildsByBranchName();
                String branch = "";
                for (String key : branches.keySet()) {
                    hudson.plugins.git.util.Build b = branches.get(key);
                    if (b.hudsonBuildNumber == r.getNumber()) {
                        branch = key;
                    }
                }
                list.add(new RepoBranch(url, branch, RepoBranch.RepoType.GIT));
            }
        }
        return list;
    }

    private List<RepoBranch> getSVNRepoBranch(SubversionSCM scm) {
        List<RepoBranch> list = new ArrayList<>();
        SubversionSCM.ModuleLocation[] mLocations = scm.getLocations();
        if (mLocations != null) {
            for (int i = 0; i < mLocations.length; i++) {
                list.add(new RepoBranch(mLocations[i].getURL(), "", RepoBranch.RepoType.SVN));
            }
        }
        return list;
    }
}
