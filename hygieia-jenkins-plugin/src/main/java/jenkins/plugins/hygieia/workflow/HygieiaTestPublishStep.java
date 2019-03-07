package jenkins.plugins.hygieia.workflow;

import com.capitalone.dashboard.model.BuildStatus;
import com.capitalone.dashboard.model.TestSuiteType;
import com.capitalone.dashboard.request.TestDataCreateRequest;
import hudson.Extension;
import hudson.FilePath;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.util.FormValidation;
import hudson.util.ListBoxModel;
import hygieia.builder.BuildBuilder;
import hygieia.builder.CucumberTestBuilder;
import hygieia.transformer.HygieiaConstants;
import hygieia.utils.HygieiaUtils;
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


public class HygieiaTestPublishStep extends AbstractStepImpl {

    private String  buildStatus;
    private String testFileNamePattern;
    private String testResultsDirectory;
    private String testType;
    private String testApplicationName;
    private String testEnvironmentName;

    public String getBuildStatus() {
        return buildStatus;
    }

    @DataBoundSetter
    public void setBuildStatus(String buildStatus) {
        this.buildStatus = buildStatus;
    }

    public String getTestFileNamePattern() {
        return testFileNamePattern;
    }

    @DataBoundSetter
    public void setTestFileNamePattern(String testFileNamePattern) {
        this.testFileNamePattern = testFileNamePattern;
    }

    public String getTestResultsDirectory() {
        return testResultsDirectory;
    }

    @DataBoundSetter
    public void setTestResultsDirectory(String testResultsDirectory) {
        this.testResultsDirectory = testResultsDirectory;
    }

    public String getTestType() {
        return testType;
    }

    @DataBoundSetter
    public void setTestType(String testType) {
        this.testType = testType;
    }

    public String getTestApplicationName() {
        return testApplicationName;
    }

    @DataBoundSetter
    public void setTestApplicationName(String testApplicationName) {
        this.testApplicationName = testApplicationName;
    }

    public String getTestEnvironmentName() {
        return testEnvironmentName;
    }

    @DataBoundSetter
    public void setTestEnvironmentName(String testEnvironmentName) {
        this.testEnvironmentName = testEnvironmentName;
    }

    @DataBoundConstructor
    public HygieiaTestPublishStep(String buildStatus, String testFileNamePattern, String testResultsDirectory, String testType, String testApplicationName, String testEnvironmentName) {
        this.buildStatus = buildStatus;
        this.testFileNamePattern = testFileNamePattern;
        this.testResultsDirectory = testResultsDirectory;
        this.testType = testType;
        this.testApplicationName = testApplicationName;
        this.testEnvironmentName = testEnvironmentName;
    }


    @Extension
    public static class DescriptorImpl extends AbstractStepDescriptorImpl {

        public DescriptorImpl() {
            super(HygieiaArtifactPublishStepExecution.class);
        }

        @Override
        public String getFunctionName() {
            return "hygieiaTestPublishStep";
        }

        @Override
        public String getDisplayName() {
            return "Hygieia Test Publish Step";
        }

        public FormValidation doCheckValue(@QueryParameter String value) {
            if (value.isEmpty()) {
                return FormValidation.warning("You must fill this box!");
            }
            return FormValidation.ok();
        }
        public ListBoxModel doFillBuildStatusItems() {
            ListBoxModel model = new ListBoxModel();
            model.add("Success", BuildStatus.Success.toString());
            model.add("Failure", BuildStatus.Failure.toString());
            model.add("Unstable", BuildStatus.Unstable.toString());
            model.add("Aborted", BuildStatus.Aborted.toString());
            return model;
        }
        public ListBoxModel doFillTestTypeItems(String testType) {
            ListBoxModel model = new ListBoxModel();

            model.add(HygieiaConstants.UNIT_TEST_DISPLAY, TestSuiteType.Unit.toString());
            model.add(HygieiaConstants.INTEGRATION_TEST_DISPLAY, TestSuiteType.Integration.toString());
            model.add(HygieiaConstants.FUNCTIONAL_TEST_DISPLAY, TestSuiteType.Functional.toString());
            model.add(HygieiaConstants.REGRESSION_TEST_DISPLAY, TestSuiteType.Regression.toString());
            model.add(HygieiaConstants.PERFORMANCE_TEST_DISPLAY, TestSuiteType.Performance.toString());
            model.add(HygieiaConstants.SECURITY_TEST_DISPLAY, TestSuiteType.Security.toString());
            return model;
        }
    }

    public static class HygieiaArtifactPublishStepExecution extends AbstractSynchronousNonBlockingStepExecution<Integer> {

        private static final long serialVersionUID = 1L;

        @Inject
        transient HygieiaTestPublishStep step;

        @StepContextParameter
        transient TaskListener listener;

        @StepContextParameter
        transient Run run;

        @StepContextParameter
        transient FilePath filepath;

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
                            listener, BuildStatus.fromString(step.buildStatus), false));

            if (buildResponse.getResponseCode() == HttpStatus.SC_CREATED) {
                listener.getLogger().println("Hygieia: Published Build Data For Test Publishing. " + buildResponse.toString());
            } else {
                listener.getLogger().println("Hygieia: Failed Publishing Build Data for Test Publishing. " + buildResponse.toString());
            }
            TestDataCreateRequest request = new CucumberTestBuilder().getTestDataCreateRequest(run, listener, BuildStatus.fromString(step.buildStatus), filepath, step.testApplicationName,
                    step.testEnvironmentName, step.testType, step.testFileNamePattern, step.testResultsDirectory,
                    hygieiaDesc.getHygieiaJenkinsName(), HygieiaUtils.getBuildCollectionId(buildResponse.getResponseValue()));
            if (request != null) {
                HygieiaResponse testResponse = hygieiaService.publishTestResults(request);
                if (testResponse.getResponseCode() == HttpStatus.SC_CREATED) {
                    listener.getLogger().println("Hygieia: Published Test Data. " + testResponse.toString());
                } else {
                    listener.getLogger().println("Hygieia: Failed Publishing Test Data. " + testResponse.toString());
                }
            } else {
                listener.getLogger().println("Hygieia: Published Test Data. Nothing to publish");
            }

            return buildResponse.getResponseCode();
        }

        //streamline unit testing
        HygieiaService getHygieiaService(String hygieiaAPIUrl, String hygieiaToken, String hygieiaJenkinsName, boolean useProxy) {
            return new DefaultHygieiaService(hygieiaAPIUrl, hygieiaToken, hygieiaJenkinsName, useProxy);
        }
    }


}
