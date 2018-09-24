package jenkins.plugins.hygieia.workflow;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import org.apache.commons.httpclient.HttpStatus;
import org.jenkinsci.plugins.workflow.steps.AbstractStepDescriptorImpl;
import org.jenkinsci.plugins.workflow.steps.AbstractStepImpl;
import org.jenkinsci.plugins.workflow.steps.AbstractSynchronousNonBlockingStepExecution;
import org.jenkinsci.plugins.workflow.steps.StepContextParameter;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;

import com.capitalone.dashboard.model.BuildStatus;

import hudson.Extension;
import hudson.model.Run;
import hudson.model.TaskListener;
import hygieia.builder.BuildBuilder;
import jenkins.model.Jenkins;
import jenkins.plugins.hygieia.DefaultHygieiaService;
import jenkins.plugins.hygieia.HygieiaPublisher;
import jenkins.plugins.hygieia.HygieiaResponse;
import jenkins.plugins.hygieia.HygieiaService;

public class HygieiaBuildPublishStep extends AbstractStepImpl {

	private String buildStatus;

	public String getBuildStatus() {
		return buildStatus;
	}

	@DataBoundSetter
	public void setBuildStatus(String buildStatus) {
		this.buildStatus = buildStatus;
	}

	@DataBoundConstructor
	public HygieiaBuildPublishStep(@Nonnull String buildStatus) {
		this.buildStatus = buildStatus;
	}

	@Extension
	public static class DescriptorImpl extends AbstractStepDescriptorImpl {

		public DescriptorImpl() {
			super(HygieiaBuildPublishStepExecution.class);
		}

		@Override
		public String getFunctionName() {
			return "hygieiaBuildPublishStep";
		}

		@Override
		public String getDisplayName() {
			return "Hygieia Build Publish Step";
		}
	}

	public static class HygieiaBuildPublishStepExecution
			extends AbstractSynchronousNonBlockingStepExecution<List<Integer>> {

		private static final long serialVersionUID = 1L;

		@Inject
		transient HygieiaBuildPublishStep step;

		@StepContextParameter
		transient TaskListener listener;

		@StepContextParameter
		transient Run run;

		// This run MUST return a non-Void object, otherwise it will be executed
		// three times!!!! No idea why
		@Override
		protected List<Integer> run() throws Exception {

			// default to global config values if not set in step, but allow
			// step to override all global settings

			Jenkins jenkins;
			try {
				jenkins = Jenkins.getInstance();
			} catch (NullPointerException ne) {
				listener.error(ne.toString());
				return null;
			}
			HygieiaPublisher.DescriptorImpl hygieiaDesc = (HygieiaPublisher.DescriptorImpl) jenkins
					.getDescriptorByType(HygieiaPublisher.DescriptorImpl.class);
			List<String> hygieiaAPIUrls = Arrays.asList(hygieiaDesc.getHygieiaAPIUrl().split(";"));
			List<Integer> responseCodes = new ArrayList<>();
			for (String hygieiaAPIUrl : hygieiaAPIUrls) {
				this.listener.getLogger().println("Publishing data for API " + hygieiaAPIUrl.toString());
				HygieiaService hygieiaService = getHygieiaService(hygieiaAPIUrl,
						hygieiaDesc.getHygieiaToken(), hygieiaDesc.getHygieiaJenkinsName(), hygieiaDesc.isUseProxy());
				BuildBuilder builder = new BuildBuilder(run, hygieiaDesc.getHygieiaJenkinsName(), listener,
						BuildStatus.fromString(step.buildStatus), true);
				HygieiaResponse buildResponse = hygieiaService.publishBuildData(builder.getBuildData());
				if (buildResponse.getResponseCode() == HttpStatus.SC_CREATED) {
					listener.getLogger().println("Hygieia: Published Build Complete Data. " + buildResponse.toString());
				} else {
					listener.getLogger()
							.println("Hygieia: Failed Publishing Build Complete Data. " + buildResponse.toString());
				}
				responseCodes.add(Integer.valueOf(buildResponse.getResponseCode()));
			}
			return responseCodes;
		}

		// streamline unit testing
		HygieiaService getHygieiaService(String hygieiaAPIUrl, String hygieiaToken, String hygieiaJenkinsName,
				boolean useProxy) {
			return new DefaultHygieiaService(hygieiaAPIUrl, hygieiaToken, hygieiaJenkinsName, useProxy);
		}
	}

}
