package hygieia.builder;

import com.capitalone.dashboard.request.DeployDataCreateRequest;
import hudson.EnvVars;
import hudson.FilePath;
import hudson.model.AbstractBuild;
import hudson.model.BuildListener;
import hygieia.utils.HygieiaUtils;
import jenkins.plugins.hygieia.HygieiaPublisher;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

public class DeployBuilder {

    private static final Logger logger = Logger.getLogger(DeployBuilder.class.getName());
    private AbstractBuild build;
    private HygieiaPublisher publisher;
    private BuildListener listener;
    private String buildId;

    private Set<DeployDataCreateRequest> deploys = new HashSet<>();

    public DeployBuilder(AbstractBuild build, HygieiaPublisher publisher, BuildListener listener, String buildId) {
        this.build = build;
        this.publisher = publisher;
        this.buildId = buildId;
        this.listener = listener;
        buildDeployRequests();
    }

    private void buildDeployRequests() {
        String directory = publisher.getHygieiaDeploy().getArtifactDirectory().trim();
        String filePattern = publisher.getHygieiaDeploy().getArtifactName().trim();
        String group = publisher.getHygieiaDeploy().getArtifactGroup().trim();
        String version = publisher.getHygieiaDeploy().getArtifactVersion().trim();
        String environmentName = publisher.getHygieiaDeploy().getEnvironmentName();
        String applicationName = publisher.getHygieiaDeploy().getApplicationName();
        EnvVars envVars = null;
        try {
            envVars = build.getEnvironment(listener);
            version = envVars.expand(version);
            group = envVars.expand(group);
            directory = envVars.expand(directory);
            filePattern = envVars.expand(filePattern);
            environmentName = envVars.expand(environmentName);
        } catch (IOException e) {
            listener.getLogger().println("Hygieia BuildArtifact Publisher - IOException getting EnvVars");
        } catch (InterruptedException e) {
            listener.getLogger().println("Hygieia BuildArtifact Publisher - IOException getting EnvVars");
        }

        FilePath rootDirectory = build.getWorkspace().withSuffix(directory);
        listener.getLogger().println("Hygieia Deployment Publisher - Looking for file pattern '" + filePattern + "' in directory " + rootDirectory);
        try {
            List<FilePath> artifactFiles = HygieiaUtils.getArtifactFiles(rootDirectory, filePattern, new ArrayList<FilePath>());
            for (FilePath f : artifactFiles) {
                listener.getLogger().println("Hygieia Deployment Publisher: Processing  file: " + f.getRemote());
                DeployDataCreateRequest bac = new DeployDataCreateRequest();
                String v = "";
                bac.setArtifactGroup(group);
                if ("".equals(version)) {
                    version = HygieiaUtils.guessVersionNumber(f.getName());
                }
                bac.setArtifactVersion(version);
                bac.setArtifactName(HygieiaUtils.getFileNameMinusVersion(f, version));
                bac.setDeployStatus(build.getResult().toString());
                bac.setDuration(build.getDuration());
                bac.setEndTime(build.getStartTimeInMillis() + build.getDuration());
                bac.setStartTime(build.getStartTimeInMillis());
                bac.setExecutionId(String.valueOf(build.getNumber()));
                bac.setHygieiaId(buildId);
                bac.setAppName(applicationName);
                bac.setEnvName(environmentName);
                bac.setJobName(build.getProject().getName());
                bac.setJobUrl(build.getProject().getAbsoluteUrl());
                bac.setNiceName(publisher.getDescriptor().getHygieiaJenkinsName());
                if (envVars != null) {
                    bac.setInstanceUrl(envVars.get("JENKINS_URL"));
                } else {
                    String jobPath = "/job" + "/" + build.getProject().getName() + "/";
                    int ind = build.getProject().getAbsoluteUrl().indexOf(jobPath);
                    bac.setInstanceUrl(build.getProject().getAbsoluteUrl().substring(0, ind));
                }

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
}