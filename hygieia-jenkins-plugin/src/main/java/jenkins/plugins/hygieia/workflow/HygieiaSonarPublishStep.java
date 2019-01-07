package jenkins.plugins.hygieia.workflow;

import com.capitalone.dashboard.model.BuildStatus;
import com.capitalone.dashboard.request.CodeQualityCreateRequest;
import hudson.Extension;
import hudson.model.Run;
import hudson.model.TaskListener;
import hygieia.builder.BuildBuilder;
import hygieia.builder.SonarBuilder;
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
import org.json.simple.parser.ParseException;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;

import javax.inject.Inject;


public class HygieiaSonarPublishStep extends AbstractStepImpl {


    //Sonar 5.2+ changes: get query interval and max attempts from config
    private String ceQueryIntervalInSeconds;
    private String ceQueryMaxAttempts;



    public String getCeQueryIntervalInSeconds() {
        return ceQueryIntervalInSeconds;
    }

    public String getCeQueryMaxAttempts() {
        return ceQueryMaxAttempts;
    }

    @DataBoundSetter
    public void setCeQueryIntervalInSeconds(String ceQueryIntervalInSeconds) {
        this.ceQueryIntervalInSeconds = ceQueryIntervalInSeconds;
    }

    @DataBoundSetter
    public void setCeQueryMaxAttempts(String ceQueryMaxAttempts) {
        this.ceQueryMaxAttempts = ceQueryMaxAttempts;
    }

    @DataBoundConstructor
    public HygieiaSonarPublishStep(String ceQueryIntervalInSeconds, String ceQueryMaxAttempts) {
        this.ceQueryIntervalInSeconds = ceQueryIntervalInSeconds;
        this.ceQueryMaxAttempts = ceQueryMaxAttempts;
    }


    @Extension
    public static class DescriptorImpl extends AbstractStepDescriptorImpl {

        public DescriptorImpl() {
            super(HygieiaSonarPublishStepExecution.class);
        }

        @Override
        public String getFunctionName() {
            return "hygieiaSonarPublishStep";
        }

        @Override
        public String getDisplayName() {
            return "Hygieia Sonar Publish Step";
        }


    }

    public static class HygieiaSonarPublishStepExecution extends AbstractSynchronousNonBlockingStepExecution<Integer> {

        private static final long serialVersionUID = 1L;

        @Inject
        transient HygieiaSonarPublishStep step;

        @StepContextParameter
        transient TaskListener listener;

        @StepContextParameter
        transient Run run;

        // This run MUST return a non-Void object, otherwise it will be executed three times!!!! No idea why
        @Override
        protected Integer run() {

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

            HygieiaResponse buildResponse = hygieiaService.publishBuildData(new BuildBuilder()
                    .createBuildRequestFromRun(run, hygieiaDesc.getHygieiaJenkinsName(),
                            listener, BuildStatus.Success, false));

            if (buildResponse.getResponseCode() == HttpStatus.SC_CREATED) {
                listener.getLogger().println("Hygieia: Published Build Data For Sonar Publishing. " + buildResponse.toString());
            } else {
                listener.getLogger().println("Hygieia: Failed Publishing Build Data for Sonar Publishing. " + buildResponse.toString());
            }

            try {
                CodeQualityCreateRequest request = SonarBuilder.getInstance().getSonarMetrics(run, listener, hygieiaDesc.getHygieiaJenkinsName(), step.getCeQueryIntervalInSeconds(),
                        step.getCeQueryMaxAttempts(), buildResponse.getResponseValue(), hygieiaDesc.isUseProxy());
                if (request != null) {
                    HygieiaResponse sonarResponse = hygieiaService.publishSonarResults(request);
                    if (sonarResponse.getResponseCode() == HttpStatus.SC_CREATED) {
                        listener.getLogger().println("Hygieia: Published Sonar Data. " + sonarResponse.toString());
                    } else {
                        listener.getLogger().println("Hygieia: Failed Publishing Sonar Data. " + sonarResponse.toString());
                    }
                } else {
                    listener.getLogger().println("Hygieia: Published Sonar Result. Nothing to publish");
                }
            } catch (ParseException e) {
                listener.getLogger().println("Hygieia: Publishing error" + '\n' + e.getMessage());
            }

            return buildResponse.getResponseCode();
        }


        //streamline unit testing
        HygieiaService getHygieiaService(String hygieiaAPIUrl, String hygieiaToken, String hygieiaJenkinsName, boolean useProxy) {
            return new DefaultHygieiaService(hygieiaAPIUrl, hygieiaToken, hygieiaJenkinsName, useProxy);
        }
    }


}
