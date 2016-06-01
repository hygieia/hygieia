package hygieia.builder;

import com.capitalone.dashboard.request.BinaryArtifactCreateRequest;
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
    private AbstractBuild build;
    private HygieiaPublisher publisher;
    private BuildListener listener;
    private String buildId;

    private Set<BinaryArtifactCreateRequest> artifacts = new HashSet<>();

    public ArtifactBuilder(AbstractBuild build, HygieiaPublisher publisher, BuildListener listener, String buildId) {
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

        FilePath rootDirectory = build.getWorkspace().withSuffix(directory);
        listener.getLogger().println("Hygieia Build Artifact Publisher - Looking for file pattern '" + filePattern + "' in directory " + rootDirectory);
        try {
            List<FilePath> artifactFiles = HygieiaUtils.getArtifactFiles(rootDirectory, filePattern, new ArrayList<FilePath>());
            for (FilePath f : artifactFiles) {
                listener.getLogger().println("Hygieia Artifact Publisher: Processing  file: " + f.getRemote());
                BinaryArtifactCreateRequest bac = new BinaryArtifactCreateRequest();
                String v = "";
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