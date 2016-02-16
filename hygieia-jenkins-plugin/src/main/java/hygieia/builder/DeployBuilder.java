package hygieia.builder;

import com.capitalone.dashboard.request.DeployDataCreateRequest;
import hudson.EnvVars;
import hudson.model.AbstractBuild;
import hudson.model.BuildListener;
import hygieia.utils.HygieiaUtils;
import jenkins.plugins.hygieia.HygieiaPublisher;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

public class DeployBuilder {

    private static final Logger logger = Logger.getLogger(DeployBuilder.class.getName());
    AbstractBuild build;
    HygieiaPublisher publisher;
    BuildListener listener;
    String buildId;

    Set<DeployDataCreateRequest> deploys = new HashSet<DeployDataCreateRequest>();

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

        EnvVars env;
        try {
            env = build.getEnvironment(listener);
        } catch (Exception e) {
            listener.getLogger().println("Error retrieving environment vars: " + e.getMessage());
            env = new EnvVars();
        }

        String path = env.expand("$WORKSPACE");

        if (directory.startsWith("/")) {
            path = path + directory;
        } else {
            path = path + "/" + directory;
        }

        List<File> artifactFiles = HygieiaUtils.getArtifactFiles(new File(path), filePattern, new ArrayList<File>());

        for (File f : artifactFiles) {
            DeployDataCreateRequest bac = new DeployDataCreateRequest();
            String v = "";
            bac.setArtifactGroup(group);
            if ("".equals(version)) {
                version = guessVersionNumber(f.getName());
            }
            bac.setArtifactVersion(version);
            bac.setArtifactName(getFileNameMinusVersion(f, version));
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
            try {
                env = build.getEnvironment(listener);
            } catch (IOException e) {
                logger.warning("Error getting environment variables");
            } catch (InterruptedException e) {
                logger.warning("Error getting environment variables");
            }
            if (env != null) {
                bac.setInstanceUrl(env.get("JENKINS_URL"));
            } else {
                String jobPath = "/job" + "/" + build.getProject().getName() + "/";
                int ind = build.getProject().getAbsoluteUrl().indexOf(jobPath);
                bac.setInstanceUrl(build.getProject().getAbsoluteUrl().substring(0, ind));
            }

            deploys.add(bac);
        }
    }

    private static String getFileNameMinusVersion(File file, String version) {
        String ext = FilenameUtils.getExtension(file.getName());
        if ("".equals(version)) return file.getName();

        int vIndex = file.getName().indexOf(version);
        if (vIndex <= 0) return file.getName();
        if ((file.getName().charAt(vIndex - 1) == '-') || (file.getName().charAt(vIndex - 1) == '_')) {
            vIndex = vIndex - 1;
        }
        return file.getName().substring(0, vIndex) + "." + ext;
    }

    private String guessVersionNumber(String source) {
        String versionNumber = "";
        String fileName = source.substring(0, source.lastIndexOf("."));
        if (fileName.contains(".")) {
            String majorVersion = fileName.substring(0, fileName.indexOf("."));
            String minorVersion = fileName.substring(fileName.indexOf("."));
            int delimiter = majorVersion.lastIndexOf("-");
            if (majorVersion.indexOf("_") > delimiter) delimiter = majorVersion.indexOf("_");
            majorVersion = majorVersion.substring(delimiter + 1, fileName.indexOf("."));
            versionNumber = majorVersion + minorVersion;
        }
        return versionNumber;
    }


    public Set<DeployDataCreateRequest> getDeploys() {
        return deploys;
    }
}