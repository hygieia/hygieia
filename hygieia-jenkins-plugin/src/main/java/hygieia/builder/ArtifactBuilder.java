package hygieia.builder;

import com.capitalone.dashboard.request.BinaryArtifactCreateRequest;
import hudson.EnvVars;
import hudson.FilePath;
import hudson.model.AbstractBuild;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.scm.ChangeLogSet;
import hygieia.utils.HygieiaUtils;
import jenkins.plugins.hygieia.HygieiaPublisher;
import jenkins.plugins.hygieia.workflow.HygieiaArtifactPublishStep;
import org.jenkinsci.plugins.workflow.job.WorkflowJob;
import org.jenkinsci.plugins.workflow.job.WorkflowRun;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

public class ArtifactBuilder {

    private static final Logger logger = Logger.getLogger(ArtifactBuilder.class.getName());
    private AbstractBuild<?, ?> build;
    private Run<?, ?> run;
//    private BuildListener buildListener;
    private TaskListener listener;
    private String hygieiaBuildId;

    private String directory;
    private String filePattern;
    private String group;
    private String version;
    private FilePath rootDirectory;


    private Set<BinaryArtifactCreateRequest> artifacts = new HashSet<>();

    public ArtifactBuilder(AbstractBuild<?, ?> run, HygieiaPublisher publisher, TaskListener listener, String hygieiaBuildId) {
        this.run = run;
        directory = publisher.getHygieiaArtifact().getArtifactDirectory().trim();
        filePattern = publisher.getHygieiaArtifact().getArtifactName().trim();
        group = publisher.getHygieiaArtifact().getArtifactGroup().trim();
        version = publisher.getHygieiaArtifact().getArtifactVersion().trim();
        this.hygieiaBuildId = hygieiaBuildId;
        this.listener = listener;
        this.rootDirectory = build.getWorkspace().withSuffix(directory);

        buildArtifacts();
    }

    public ArtifactBuilder(Run<?, ?> run, FilePath filePath, HygieiaArtifactPublishStep publisher, TaskListener listener, String hygieiaBuildId) {
        this.run =run;
        directory = publisher.getArtifactDirectory().trim();
        filePattern = publisher.getArtifactName().trim();
        group = publisher.getArtifactGroup().trim();
        version = publisher.getArtifactVersion().trim();
        this.hygieiaBuildId = hygieiaBuildId;
        this.listener = listener;
        this.rootDirectory = filePath.withSuffix(directory);
        buildArtifacts();
    }

    private void buildArtifacts() {
//        String directory = publisher.getHygieiaArtifact().getArtifactDirectory().trim();
//        String filePattern = publisher.getHygieiaArtifact().getArtifactName().trim();
//        String group = publisher.getHygieiaArtifact().getArtifactGroup().trim();
//        String version = publisher.getHygieiaArtifact().getArtifactVersion().trim();

        List<ChangeLogSet<? extends ChangeLogSet.Entry>> changeLogSets = new ArrayList<>();

        EnvVars envVars = new EnvVars();
        try {
            envVars = run.getEnvironment(listener);
            version = envVars.expand(version);
            group = envVars.expand(group);
            directory = envVars.expand(directory);
            filePattern = envVars.expand(filePattern);
        } catch (IOException e) {
            listener.getLogger().println("Hygieia BuildArtifact Publisher - IOException getting EnvVars");
        } catch (InterruptedException e) {
            listener.getLogger().println("Hygieia BuildArtifact Publisher - IOException getting EnvVars");
        }

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
                bac.setTimestamp(run.getTimeInMillis());
                bac.setBuildId(hygieiaBuildId);

                if (run instanceof WorkflowRun) {
                    changeLogSets = ((WorkflowRun) run).getChangeSets();
                } else if (run instanceof AbstractBuild){
                    changeLogSets = ((AbstractBuild) run).getChangeSets();
                }
                CommitBuilder commitBuilder = new CommitBuilder(changeLogSets);
                bac.getSourceChangeSet().addAll(commitBuilder.getCommits());

                bac.getMetadata().put("buildUrl", HygieiaUtils.getBuildUrl(run));
                bac.getMetadata().put("buildNumber", HygieiaUtils.getBuildNumber(run));
                bac.getMetadata().put("jobUrl", HygieiaUtils.getJobUrl(run));
                bac.getMetadata().put("jobName", HygieiaUtils.getJobName(run));
                bac.getMetadata().put("instanceUrl", HygieiaUtils.getInstanceUrl(run, listener));


                if (run instanceof AbstractBuild) {
                    AbstractBuild abstractBuild = (AbstractBuild) run;
                    String scmUrl = HygieiaUtils.getScmUrl(abstractBuild, listener);
                    String scmBranch = HygieiaUtils.getScmBranch(abstractBuild, listener);
                    String scmRevisionNumber = HygieiaUtils.getScmRevisionNumber(abstractBuild, listener);

                    if (scmUrl != null) {
                        bac.getMetadata().put("scmUrl", scmUrl);
                    }
                    if (scmBranch != null) {
                        if (scmBranch.startsWith("origin/")) {
                            scmBranch = scmBranch.substring(7);
                        }
                        bac.getMetadata().put("scmBranch", scmBranch);
                    }
                    if (scmRevisionNumber != null) {
                        bac.getMetadata().put("scmRevisionNumber", scmRevisionNumber);
                    }
                }

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