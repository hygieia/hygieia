package hygieia.builder;

import com.capitalone.dashboard.request.BinaryArtifactCreateRequest;
import hudson.EnvVars;
import hudson.model.AbstractBuild;
import hudson.model.BuildListener;
import hygieia.utils.HygieiaUtils;
import jenkins.plugins.hygieia.HygieiaPublisher;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

public class ArtifactBuilder {

    private static final Logger logger = Logger.getLogger(ArtifactBuilder.class.getName());
    AbstractBuild build;
    HygieiaPublisher publisher;
    BuildListener listener;
    String buildId;

    Set<BinaryArtifactCreateRequest> artifacts = new HashSet<BinaryArtifactCreateRequest>();

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
            BinaryArtifactCreateRequest bac = new BinaryArtifactCreateRequest();
            String v = "";
            bac.setArtifactGroup(group);
            if ("".equals(version)) {
                version = guessVersionNumber(f.getName());
            }
            bac.setArtifactVersion(version);
            bac.setCanonicalName(f.getName());
            bac.setArtifactName(getFileNameMinusVersion(f, version));
            bac.setTimestamp(build.getTimeInMillis());
            bac.setBuildId(buildId);
            CommitBuilder commitBuilder = new CommitBuilder(build);
            bac.getSourceChangeSet().addAll(commitBuilder.getCommits());
            artifacts.add(bac);
        }
    }

    private static String getFileNameMinusVersion(File file, String version) {
        String ext = FilenameUtils.getExtension(file.getName());
        if ("".equals(version)) return file.getName();
        int vIndex = file.getName().indexOf(version);
        if (vIndex == 0) return file.getName();
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


    public Set<BinaryArtifactCreateRequest> getArtifacts() {
        return artifacts;
    }
}