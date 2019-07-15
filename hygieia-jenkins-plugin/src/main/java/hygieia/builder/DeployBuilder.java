package hygieia.builder;

import com.capitalone.dashboard.model.BuildStage;
import com.capitalone.dashboard.model.BuildStatus;
import com.capitalone.dashboard.request.BuildDataCreateRequest;
import com.capitalone.dashboard.request.DeployDataCreateRequest;
import hudson.EnvVars;
import hudson.FilePath;
import hudson.model.AbstractBuild;
import hudson.model.Run;
import hudson.model.TaskListener;
import hygieia.utils.HygieiaUtils;
import jenkins.plugins.hygieia.HygieiaPublisher;
import jenkins.plugins.hygieia.workflow.HygieiaDeployPublishStep;
import org.apache.commons.lang3.StringUtils;
import org.jenkinsci.plugins.workflow.job.WorkflowRun;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class DeployBuilder {

    private Run run;
    private TaskListener listener;
    private String buildId;
    private String jenkinsName;
    private BuildStatus result;
    private HygieiaPublisher.HygieiaDeploy hygieiaDeploy;
    private HygieiaPublisher hygieiaPublisher;
    private HygieiaDeployPublishStep hygieiaDeployPublishStep;
    private FilePath filePath;

    public DeployBuilder(AbstractBuild build, HygieiaPublisher hygieiaPublisher, TaskListener listener, String buildId) {
        this.run = build;
        this.hygieiaPublisher = hygieiaPublisher;
        this.hygieiaDeploy = hygieiaPublisher.getHygieiaDeploy();
        this.buildId = HygieiaUtils.getBuildCollectionId(buildId);
        this.listener = listener;
        this.jenkinsName = hygieiaPublisher.getDescriptor().getHygieiaJenkinsName();
    }

    public DeployBuilder(Run run, String jenkinsName, HygieiaDeployPublishStep publisher, FilePath filePath, TaskListener listener, String buildId, BuildStatus result) {
        this.run = run;
        this.buildId = HygieiaUtils.getBuildCollectionId(buildId);
        this.listener = listener;
        this.filePath = filePath;
        this.hygieiaDeployPublishStep = publisher;
        this.jenkinsName = jenkinsName;
        this.result = result;
    }


    @SuppressWarnings("Duplicates")
    private Set<DeployDataCreateRequest> buildDeployRequests() {
        EnvVars envVars;
        String artifactVersion = "";
        String artifactName = "";
        String applicationName = "";
        String environmentName = "";
        FilePath rootDirectory = null;
        Set<DeployDataCreateRequest> deploys = new HashSet<>();
        //Quick fix have seperate implementations for AbstractBuild and DeployStep
        boolean retrieveFromAbstractBuild = this.run instanceof AbstractBuild && hygieiaPublisher != null && hygieiaDeploy != null;

        // The artifact information is now local and moved out of the costructor.
        artifactVersion = StringUtils.trim(retrieveFromAbstractBuild ? hygieiaDeploy.getArtifactVersion(): hygieiaDeployPublishStep.getArtifactVersion());
        artifactName = StringUtils.trim(retrieveFromAbstractBuild ? hygieiaDeploy.getArtifactName() : hygieiaDeployPublishStep.getArtifactName());
        applicationName = StringUtils.trim(retrieveFromAbstractBuild ? hygieiaDeploy.getApplicationName() : hygieiaDeployPublishStep.getApplicationName());
        environmentName = StringUtils.trim(retrieveFromAbstractBuild ? hygieiaDeploy.getEnvironmentName() : hygieiaDeployPublishStep.getEnvironmentName());


        try {
            envVars = run.getEnvironment(listener);

            if (envVars != null) {
                artifactVersion = envVars.expand(artifactVersion);
                artifactName = envVars.expand(artifactName);
                environmentName = envVars.expand(environmentName);
                applicationName = envVars.expand(applicationName);
            }
        } catch (IOException | InterruptedException e) {
            listener.getLogger().println("Hygieia BuildArtifact Publisher - IOException getting EnvVars");
        }

        try {
            AbstractBuild build = (run instanceof AbstractBuild) ? (AbstractBuild) run : null;
            rootDirectory = (run instanceof WorkflowRun) ? new FilePath(filePath, StringUtils.trim(hygieiaDeployPublishStep.getArtifactDirectory()))
            : new FilePath(Objects.requireNonNull(build.getWorkspace()), StringUtils.trim(hygieiaDeploy.getArtifactDirectory()));
            listener.getLogger().println("Hygieia Deployment Publisher - Looking for file pattern '" + artifactName + "' in directory " + rootDirectory);
            List<FilePath> artifactFiles = HygieiaUtils.getArtifactFiles(rootDirectory, artifactName, new ArrayList<FilePath>());

            for (FilePath f : artifactFiles) {
                listener.getLogger().println("Hygieia Deployment Publisher: Processing  file: " + f.getRemote());
                DeployDataCreateRequest bac = new DeployDataCreateRequest();
                if (StringUtils.isEmpty(artifactVersion)) {
                    artifactVersion = HygieiaUtils.guessVersionNumber(f.getName());
                }
              
                artifactName = HygieiaUtils.getFileNameMinusVersion(f, artifactVersion);
                
                bac.setArtifactVersion(artifactVersion);
                bac.setArtifactName(artifactName);
                String startedBy = HygieiaUtils.getUserID(run, listener);
                BuildDataCreateRequest buildDataCreateRequest = (run instanceof WorkflowRun)
                        ? new BuildBuilder().createBuildRequestFromRun(run, jenkinsName, listener, result, false, new LinkedList<BuildStage>(), startedBy)
                        : new BuildBuilder().createBuildRequest((AbstractBuild) run, jenkinsName, listener, true, false, new LinkedList<BuildStage>(), startedBy);

                bac.setDeployStatus(buildDataCreateRequest.getBuildStatus());
                bac.setDuration(buildDataCreateRequest.getDuration());
                bac.setEndTime(buildDataCreateRequest.getEndTime());
                bac.setStartTime(buildDataCreateRequest.getStartTime());
                bac.setExecutionId(buildDataCreateRequest.getNumber());
                bac.setHygieiaId(buildId);
                bac.setAppName(applicationName);
                bac.setEnvName(environmentName);
                bac.setJobName(buildDataCreateRequest.getJobName());
                bac.setJobUrl(buildDataCreateRequest.getJobUrl());
                bac.setNiceName(jenkinsName);
                bac.setInstanceUrl(buildDataCreateRequest.getInstanceUrl());
                deploys.add(bac);
            }
        } catch (IOException e) {
            listener.getLogger().println("Hygieia BuildArtifact Publisher - IOException on " + rootDirectory);
        } catch (InterruptedException e) {
            listener.getLogger().println("Hygieia BuildArtifact Publisher - InterruptedException on " + rootDirectory);
        }
        return deploys;
    }

    public Set<DeployDataCreateRequest> getDeploys() {
        return buildDeployRequests();
    }
}
