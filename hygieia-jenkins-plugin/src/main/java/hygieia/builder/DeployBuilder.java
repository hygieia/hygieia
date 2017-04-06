package hygieia.builder;

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
import org.jenkinsci.plugins.workflow.job.WorkflowRun;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import org.apache.commons.io.FilenameUtils;

public class DeployBuilder {

    private static final Logger logger = Logger.getLogger(DeployBuilder.class.getName());
    private AbstractBuild build;
    private Run run;
    private TaskListener listener;
    private String buildId;
    private String jenkinsName;
    private BuildStatus result;

    private  String artifactName;
//    private  String artifactDirectory;
    private  String artifactGroup;
    private  String artifactVersion;
    private  String applicationName;
    private  String environmentName;
    private FilePath rootDirectory;
    private BuildDataCreateRequest buildDataCreateRequest;


    private Set<DeployDataCreateRequest> deploys = new HashSet<>();

    public DeployBuilder(AbstractBuild build, HygieiaPublisher publisher, TaskListener listener, String buildId) {
        //fixme: Need to settle on run vs build dual
        this.build = build;
        this.run = build;
        this.artifactGroup = publisher.getHygieiaDeploy().getArtifactGroup().trim();
        this.artifactName = publisher.getHygieiaDeploy().getArtifactName().trim();
        this.artifactVersion = publisher.getHygieiaDeploy().getArtifactVersion().trim();
        this.applicationName = publisher.getHygieiaDeploy().getApplicationName().trim();
        this.environmentName = publisher.getHygieiaDeploy().getEnvironmentName().trim();
        this.buildId = buildId;
        this.listener = listener;
        rootDirectory = build.getWorkspace().withSuffix(publisher.getHygieiaDeploy().getArtifactDirectory().trim());
        this.jenkinsName = publisher.getDescriptor().getHygieiaJenkinsName();
        buildDeployRequests();
    }

    public DeployBuilder(Run run, String jenkinsName, HygieiaDeployPublishStep publisher, FilePath filePath, TaskListener listener, String buildId, BuildStatus result) {
        this.run = run;
        this.artifactGroup = publisher.getArtifactGroup().trim();
        this.artifactName = publisher.getArtifactName().trim();
        this.artifactVersion = publisher.getArtifactVersion().trim();
        this.applicationName = publisher.getApplicationName().trim();
        this.environmentName = publisher.getEnvironmentName().trim();
        this.buildId = buildId;
        this.listener = listener;
        rootDirectory = filePath.withSuffix(publisher.getArtifactDirectory().trim());
        this.jenkinsName = jenkinsName;
        this.result = result;
        buildDeployRequests();

    }

    private void buildDeployRequests() {
        EnvVars envVars = null;
        try {
            envVars = run.getEnvironment(listener);
            artifactVersion = envVars.expand(artifactVersion);
            artifactGroup = envVars.expand(artifactGroup);
            artifactName = envVars.expand(artifactName);
            environmentName = envVars.expand(environmentName);
            applicationName = envVars.expand(applicationName);
        } catch (IOException e) {
            listener.getLogger().println("Hygieia BuildArtifact Publisher - IOException getting EnvVars");
        } catch (InterruptedException e) {
            listener.getLogger().println("Hygieia BuildArtifact Publisher - IOException getting EnvVars");
        }

        listener.getLogger().println("Hygieia Deployment Publisher - Looking for file pattern '" + artifactName + "' in directory " + rootDirectory);
        try {
            List<FilePath> artifactFiles = HygieiaUtils.getArtifactFiles(rootDirectory, artifactName, new ArrayList<FilePath>());
            for (FilePath f : artifactFiles) {
                listener.getLogger().println("Hygieia Deployment Publisher: Processing  file: " + f.getRemote());
                DeployDataCreateRequest bac = new DeployDataCreateRequest();
                if ("".equals(artifactVersion)) {
                    artifactVersion = HygieiaUtils.guessVersionNumber(f.getName());
                }
              
                String artifactName = HygieiaUtils.getFileNameMinusVersion(f, artifactVersion);
                
                bac.setArtifactVersion(artifactVersion);
                bac.setArtifactName(artifactName);
                
                BuildBuilder buildBuilder;

                if (run instanceof WorkflowRun) {
                    buildBuilder = new BuildBuilder(run, jenkinsName, listener, result, false);

                } else {
                    buildBuilder = new BuildBuilder((AbstractBuild) run, jenkinsName, listener, true, false);
                }

                buildDataCreateRequest = buildBuilder.getBuildData();

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
    }


    public Set<DeployDataCreateRequest> getDeploys() {
        return deploys;
    }

    public BuildDataCreateRequest getBuildDataCreateRequest() {
        return buildDataCreateRequest;
    }
}