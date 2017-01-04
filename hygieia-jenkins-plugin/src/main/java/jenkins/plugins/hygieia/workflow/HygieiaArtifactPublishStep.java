package jenkins.plugins.hygieia.workflow;

import com.capitalone.dashboard.model.BuildStatus;
import com.capitalone.dashboard.request.BinaryArtifactCreateRequest;
import hudson.Extension;
import hudson.FilePath;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.util.FormValidation;
import hygieia.builder.ArtifactBuilder;
import hygieia.builder.BuildBuilder;
import jenkins.model.Jenkins;
import jenkins.plugins.hygieia.DefaultHygieiaService;
import jenkins.plugins.hygieia.HygieiaPublisher;
import jenkins.plugins.hygieia.HygieiaResponse;
import jenkins.plugins.hygieia.HygieiaService;
import org.apache.commons.httpclient.HttpStatus;
import org.jenkinsci.plugins.workflow.steps.AbstractStepDescriptorImpl;
import org.jenkinsci.plugins.workflow.steps.AbstractStepImpl;
import org.jenkinsci.plugins.workflow.steps.AbstractSynchronousNonBlockingStepExecution;
import org.jenkinsci.plugins.workflow.steps.StepContextParameter;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;
import org.kohsuke.stapler.QueryParameter;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.servlet.ServletException;
import java.io.IOException;
import java.util.Set;


public class HygieiaArtifactPublishStep extends AbstractStepImpl {


    private String artifactName;
    private String artifactDirectory;
    private String artifactGroup;
    private String artifactVersion;

    public String getArtifactName() {
        return artifactName;
    }

    @DataBoundSetter
    public void setArtifactName(String artifactName) {
        this.artifactName = artifactName;
    }

    public String getArtifactDirectory() {
        return artifactDirectory;
    }

    @DataBoundSetter
    public void setArtifactDirectory(String artifactDirectory) {
        this.artifactDirectory = artifactDirectory;
    }

    public String getArtifactGroup() {
        return artifactGroup;
    }

    @DataBoundSetter
    public void setArtifactGroup(String artifactGroup) {
        this.artifactGroup = artifactGroup;
    }

    public String getArtifactVersion() {
        return artifactVersion;
    }

    @DataBoundSetter
    public void setArtifactVersion(String artifactVersion) {
        this.artifactVersion = artifactVersion;
    }

    @DataBoundConstructor
    public HygieiaArtifactPublishStep(@Nonnull String artifactName, @Nonnull String artifactDirectory, @Nonnull String artifactGroup, String artifactVersion) {
        this.artifactName = artifactName;
        this.artifactDirectory = artifactDirectory;
        this.artifactGroup = artifactGroup;
        this.artifactVersion = artifactVersion;
    }


    public boolean checkFileds() {
        return (!"".equals(artifactName));
    }

    @Extension
    public static class DescriptorImpl extends AbstractStepDescriptorImpl {

        public DescriptorImpl() {
            super(HygieiaArtifactPublishStepExecution.class);
        }

        @Override
        public String getFunctionName() {
            return "hygieiaArtifactPublishStep";
        }

        @Override
        public String getDisplayName() {
            return "Hygieia Artifact Publish Step";
        }

        public FormValidation doCheckValue(@QueryParameter String value) throws IOException, ServletException {
            if (value.isEmpty()) {
                return FormValidation.warning("You must fill this box!");
            }
            return FormValidation.ok();
        }

    }

    public static class HygieiaArtifactPublishStepExecution extends AbstractSynchronousNonBlockingStepExecution<Integer> {

        private static final long serialVersionUID = 1L;

        @Inject
        transient HygieiaArtifactPublishStep step;

        @StepContextParameter
        transient TaskListener listener;

        @StepContextParameter
        transient Run run;

        @StepContextParameter
        transient FilePath filepath;

        // This run MUST return a non-Void object, otherwise it will be executed three times!!!! No idea why
        @Override
        protected Integer run() throws Exception {

            //default to global config values if not set in step, but allow step to override all global settings

            Jenkins jenkins;
            try {
                jenkins = Jenkins.getInstance();
            } catch (NullPointerException ne) {
                listener.error(ne.toString());
                return -1;
            }

            HygieiaPublisher.DescriptorImpl hygieiaDesc = jenkins.getDescriptorByType(HygieiaPublisher.DescriptorImpl.class);
            HygieiaService hygieiaService = getHygieiaService(hygieiaDesc.getHygieiaAPIUrl(), hygieiaDesc.getHygieiaToken(),
                    hygieiaDesc.getHygieiaJenkinsName(), hygieiaDesc.isUseProxy());

            BuildBuilder buildBuilder = new BuildBuilder(run, hygieiaDesc.getHygieiaJenkinsName(), listener, BuildStatus.Success, true);
            HygieiaResponse buildResponse = hygieiaService.publishBuildData(buildBuilder.getBuildData());

            if (buildResponse.getResponseCode() == HttpStatus.SC_CREATED) {
                listener.getLogger().println("Hygieia: Published Build Data For Artifacts Publishing. " + buildResponse.toString());
            } else {
                listener.getLogger().println("Hygieia: Failed Publishing Build Data for Artifacts Publishing. " + buildResponse.toString());
            }

            ArtifactBuilder artifactBuilder = new ArtifactBuilder(run, filepath, step, listener, buildResponse.getResponseValue());
            Set<BinaryArtifactCreateRequest> requests = artifactBuilder.getArtifacts();
            for (BinaryArtifactCreateRequest bac : requests) {
                HygieiaResponse artifactResponse = hygieiaService.publishArtifactData(bac);
                if (artifactResponse.getResponseCode() == HttpStatus.SC_CREATED) {
                    listener.getLogger().println("Hygieia: Published Build Artifact Data. Filename=" +
                            bac.getCanonicalName() + ", Name=" + bac.getArtifactName() + ", Version=" + bac.getArtifactVersion() +
                            ", Group=" + bac.getArtifactGroup() + ". " + artifactResponse.toString());
                } else {
                    listener.getLogger().println("Hygieia: Failed Publishing Build Artifact Data. " + bac.getCanonicalName() + ", Name=" + bac.getArtifactName() + ", Version=" + bac.getArtifactVersion() +
                            ", Group=" + bac.getArtifactGroup() + ". " + artifactResponse.toString());
                }
            }

            return buildResponse.getResponseCode();
        }


        //streamline unit testing
        HygieiaService getHygieiaService(String hygieiaAPIUrl, String hygieiaToken, String hygieiaJenkinsName, boolean useProxy) {
            return new DefaultHygieiaService(hygieiaAPIUrl, hygieiaToken, hygieiaJenkinsName, useProxy);
        }
    }


}
