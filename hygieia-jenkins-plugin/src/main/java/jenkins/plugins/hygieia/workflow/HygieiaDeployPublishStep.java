package jenkins.plugins.hygieia.workflow;

import com.capitalone.dashboard.model.BuildStatus;
import com.capitalone.dashboard.request.BinaryArtifactCreateRequest;
import com.capitalone.dashboard.request.DeployDataCreateRequest;
import hudson.Extension;
import hudson.FilePath;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.util.FormValidation;
import hudson.util.ListBoxModel;
import hygieia.builder.ArtifactBuilder;
import hygieia.builder.BuildBuilder;
import hygieia.builder.DeployBuilder;
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

import javax.inject.Inject;
import javax.servlet.ServletException;
import java.io.IOException;
import java.util.Set;


public class HygieiaDeployPublishStep extends AbstractStepImpl {


    private  String artifactName;
    private  String artifactDirectory;
    private  String artifactGroup;
    private  String artifactVersion;
    private  String applicationName;
    private  String environmentName;

    private String buildStatus;

    public String getBuildStatus() {
        return buildStatus;
    }

    @DataBoundSetter
    public void setBuildStatus(String buildStatus) {
        this.buildStatus = buildStatus;
    }

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

    public String getApplicationName() {
        return applicationName;
    }

    @DataBoundSetter
    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    public String getEnvironmentName() {
        return environmentName;
    }

    @DataBoundSetter
    public void setEnvironmentName(String environmentName) {
        this.environmentName = environmentName;
    }

    @DataBoundConstructor
    public HygieiaDeployPublishStep(String artifactName, String artifactDirectory, String artifactGroup, String artifactVersion, String applicationName, String environmentName, String buildStatus) {
        this.artifactName = artifactName;
        this.artifactDirectory = artifactDirectory;
        this.artifactGroup = artifactGroup;
        this.artifactVersion = artifactVersion;
        this.applicationName = applicationName;
        this.environmentName = environmentName;
        this.buildStatus = buildStatus;
    }


    public boolean checkFileds() {
        return (!"".equals(artifactName));
    }

    @Extension
    public static class DescriptorImpl extends AbstractStepDescriptorImpl {

        public DescriptorImpl() {
            super(HygieiaDeployPublishStepExecution.class);
        }

        @Override
        public String getFunctionName() {
            return "hygieiaDeployPublishStep";
        }

        @Override
        public String getDisplayName() {
            return "Hygieia Deployment Publish Step";
        }

        public FormValidation doCheckValue(@QueryParameter String value) throws IOException, ServletException {
            if (value.isEmpty()) {
                return FormValidation.warning("You must fill this box!");
            }
            return FormValidation.ok();
        }


        public ListBoxModel doFillBuildStatusItems() {
            ListBoxModel model = new ListBoxModel();
            model.add("Started", "InProgress");
            model.add("Success", BuildStatus.Success.toString());
            model.add("Failure", BuildStatus.Failure.toString());
            model.add("Unstable", BuildStatus.Unstable.toString());
            model.add("Aborted", BuildStatus.Aborted.toString());
            return model;
        }

    }

    public static class HygieiaDeployPublishStepExecution extends AbstractSynchronousNonBlockingStepExecution<Integer> {

        private static final long serialVersionUID = 1L;

        @Inject
        transient HygieiaDeployPublishStep step;

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
                listener.getLogger().println("Hygieia: Published Build Data For Deployment Publishing. " + buildResponse.toString());
            } else {
                listener.getLogger().println("Hygieia: Failed Publishing Build Data for Deployment Publishing. " + buildResponse.toString());
            }

            DeployBuilder deployBuilder = new DeployBuilder(run, hygieiaDesc.getHygieiaJenkinsName(), step, filepath, listener, buildResponse.getResponseValue(), BuildStatus.fromString(step.buildStatus));


            Set<DeployDataCreateRequest> requests = deployBuilder.getDeploys();
            for (DeployDataCreateRequest bac : requests) {
                HygieiaResponse deployResponse = hygieiaService.publishDeployData(bac);
                if (deployResponse.getResponseCode() == HttpStatus.SC_CREATED) {
                    listener.getLogger().println("Hygieia: Published Deploy Data: " + deployResponse.toString());
                } else {
                    listener.getLogger().println("Hygieia: Failed Publishing Deploy Data:" + deployResponse.toString());
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
