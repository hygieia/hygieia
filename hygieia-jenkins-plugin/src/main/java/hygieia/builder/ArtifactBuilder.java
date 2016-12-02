package hygieia.builder;

import com.capitalone.dashboard.request.BinaryArtifactCreateRequest;
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

public class ArtifactBuilder {

	private static final Logger logger = Logger.getLogger(ArtifactBuilder.class.getName());
    private AbstractBuild<?, ?> build;
    private HygieiaPublisher publisher;
    private BuildListener listener;
    private String buildId;

    private Set<BinaryArtifactCreateRequest> artifacts = new HashSet<>();

    public ArtifactBuilder(AbstractBuild<?, ?> build, HygieiaPublisher publisher, BuildListener listener, String buildId) {
        this.build = build;
        this.publisher = publisher;
        this.buildId = buildId;
        this.listener = listener;
        buildArtifacts();
    }

    private void buildArtifacts() {
        String directory = publisher.getHygieiaArtifact().getArtifactDirectory().trim();
        String filePattern = publisher.getHygieiaArtifact().getArtifactName().trim();
        String group = publisher.getHygieiaArtifact().getArtifactGroup().trim();
        String version = publisher.getHygieiaArtifact().getArtifactVersion().trim();

        EnvVars envVars = new EnvVars();
        try {
            envVars = build.getEnvironment(listener);
            version = envVars.expand(version);
            group = envVars.expand(group);
            directory = envVars.expand(directory);
            filePattern = envVars.expand(filePattern);
        } catch (IOException e) {
            listener.getLogger().println("Hygieia BuildArtifact Publisher - IOException getting EnvVars");
        } catch (InterruptedException e) {
            listener.getLogger().println("Hygieia BuildArtifact Publisher - IOException getting EnvVars");
        }

        FilePath rootDirectory = build.getWorkspace().withSuffix(directory);
        listener.getLogger().println("Hygieia Build Artifact Publisher - Looking for file pattern '" + filePattern + "' in directory " + rootDirectory);
        try {
            List<FilePath> artifactFiles = HygieiaUtils.getArtifactFiles(rootDirectory, filePattern, new ArrayList<FilePath>());
            for (FilePath f : artifactFiles) {
                listener.getLogger().println("Hygieia Artifact Publisher: Processing  file: " + f.getRemote());
                BinaryArtifactCreateRequest bac = new BinaryArtifactCreateRequest();
                bac.setArtifactGroup(group);
                if ("".equals(version)) {
                    version = HygieiaUtils.guessVersionNumber(f.getName());
                }
                bac.setArtifactVersion(version);
                bac.setCanonicalName(f.getName());
                bac.setArtifactName(HygieiaUtils.getFileNameMinusVersion(f, version));
                bac.setTimestamp(build.getTimeInMillis());
                bac.setBuildId(buildId);
                CommitBuilder commitBuilder = new CommitBuilder(build);
                bac.getSourceChangeSet().addAll(commitBuilder.getCommits());
                
                bac.getMetadata().put("buildUrl", HygieiaUtils.getBuildUrl(build));
                bac.getMetadata().put("buildNumber", HygieiaUtils.getBuildNumber(build));
                bac.getMetadata().put("jobUrl", HygieiaUtils.getJobUrl(build));
                bac.getMetadata().put("jobName", HygieiaUtils.getJobName(build));
                bac.getMetadata().put("instanceUrl", HygieiaUtils.getInstanceUrl(build, listener));
                
                String scmUrl = HygieiaUtils.getScmUrl(build, listener);
                String scmBranch = HygieiaUtils.getScmBranch(build, listener);
                String scmRevisionNumber = HygieiaUtils.getScmRevisionNumber(build, listener);
                
                if (scmUrl != null) { bac.getMetadata().put("scmUrl", scmUrl); }
                if (scmBranch != null) { 
                	if (scmBranch.startsWith("origin/")) {
                		scmBranch = scmBranch.substring(7);
                	}
                	bac.getMetadata().put("scmBranch", scmBranch); 
                }
                if (scmRevisionNumber != null) { bac.getMetadata().put("scmRevisionNumber", scmRevisionNumber); }
                
                artifacts.add(bac);
            }
        } catch (IOException e) {
            listener.getLogger().println("Hygieia BuildArtifact Publisher - IOException on " + rootDirectory);
        } catch (InterruptedException e) {
            listener.getLogger().println("Hygieia BuildArtifact Publisher - InterruptedException on " + rootDirectory);
        }
    }


    public Set<BinaryArtifactCreateRequest> getArtifacts() {
        return artifacts;
    }
}