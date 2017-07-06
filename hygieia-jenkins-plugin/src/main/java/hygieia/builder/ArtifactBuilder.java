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
import org.jenkinsci.plugins.workflow.job.WorkflowRun;

import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;

import org.apache.commons.io.FilenameUtils;
import static hygieia.utils.HygieiaUtils.getEnvironment;


public class ArtifactBuilder {

    private static final Logger logger = Logger.getLogger(ArtifactBuilder.class.getName());
    private Run<?, ?> run;
    private TaskListener listener;
    private String hygieiaBuildId;

    private String directory;
    private String filePattern;
    private String group;
    private String version;
    private FilePath rootDirectory;


    private Set<BinaryArtifactCreateRequest> artifacts = new HashSet<>();

    public ArtifactBuilder(AbstractBuild<?, ?> build, HygieiaPublisher publisher, TaskListener listener, String hygieiaBuildId) {
        //fixme: Need to fix the run and build dual!
        this.run = build;
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
        this.run = run;
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

        List<ChangeLogSet<? extends ChangeLogSet.Entry>> changeLogSets = new ArrayList<>();

        EnvVars envVars = getEnvironment(run, listener);
        if (envVars != null) {
            version = envVars.expand(version);
            group = envVars.expand(group);
            directory = envVars.expand(directory);
            filePattern = envVars.expand(filePattern);
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
                String artifactName = HygieiaUtils.determineArtifactName(f, version);
                
                bac.setArtifactVersion(version);
                bac.setCanonicalName(f.getName());

                bac.setArtifactName(artifactName);
                bac.setArtifactModule(artifactName); // for now assume maven artifact
                bac.setArtifactExtension(FilenameUtils.getExtension(f.getName()));
                bac.setTimestamp(run.getTimeInMillis());
                bac.setBuildId(hygieiaBuildId);

                if (run instanceof WorkflowRun) {
                    changeLogSets = ((WorkflowRun) run).getChangeSets();
                } else if (run instanceof AbstractBuild) {
                    ChangeLogSet<? extends ChangeLogSet.Entry> sets = ((AbstractBuild) run).getChangeSet();
                    changeLogSets = sets.isEmptySet() ? Collections.<ChangeLogSet<? extends ChangeLogSet.Entry>>emptyList() : Collections.<ChangeLogSet<? extends ChangeLogSet.Entry>>singletonList(sets);
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