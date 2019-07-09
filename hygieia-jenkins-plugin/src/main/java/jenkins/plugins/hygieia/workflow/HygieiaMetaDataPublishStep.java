package jenkins.plugins.hygieia.workflow;


import hudson.Extension;
import hudson.model.Run;
import hudson.model.TaskListener;
import hygieia.builder.MetaDataBuilder;
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

import javax.inject.Inject;
import javax.xml.bind.JAXBException;
import java.util.ArrayList;
import java.util.List;


public class HygieiaMetaDataPublishStep extends AbstractStepImpl {

    private String key;
    private String type;
    private String rawData;
    private String source;

    public String getKey() {
        return key;
    }

    @DataBoundSetter
    public void setKey(String key) {
        this.key = key;
    }

    public String getType() {
        return type;
    }

    @DataBoundSetter
    public void setType(String type) {
        this.type = type;
    }

    public String getRawData() {
        return rawData;
    }

    @DataBoundSetter
    public void setRawData(String rawData) {
        this.rawData = rawData;
    }

    public String getSource() {
        return source;
    }

    @DataBoundSetter
    public void setSource(String source) {
        this.source = source;
    }


    @DataBoundConstructor
    public HygieiaMetaDataPublishStep(String key, String type, String rawData, String source) throws JAXBException {
        this.key = key;
        this.type = type;
        this.rawData = rawData;
        this.source = source;
    }


    @Extension
    public static class DescriptorImpl extends AbstractStepDescriptorImpl {

        public DescriptorImpl() {
            super(HygieiaMetaDataPublishStep.HygieiaMetaDataPublisherStepExecution.class);
        }

        @Override
        public String getFunctionName() {
            return "hygieiaMetaDataPublishStep";
        }

        @Override
        public String getDisplayName() {
            return "Hygieia Metadata Publish Step";
        }

    }


    public static class HygieiaMetaDataPublisherStepExecution extends AbstractSynchronousNonBlockingStepExecution<List<Integer>> {

        private static final long serialVersionUID = 1L;

        @Inject
        transient HygieiaMetaDataPublishStep step;

        @StepContextParameter
        transient TaskListener listener;

        @StepContextParameter
        transient Run run;

        @Override
        protected List<Integer> run() {

            Jenkins jenkins;

            try {
                jenkins = Jenkins.getInstance();
            } catch (NullPointerException ne) {
                this.listener.error(ne.toString());
                return null;
            }

            HygieiaPublisher.DescriptorImpl hygieiaDesc = jenkins
                    .getDescriptorByType(HygieiaPublisher.DescriptorImpl.class);
            String[] hygieiaAPIUrls = hygieiaDesc.getHygieiaAPIUrl().split(";");
            List<Integer> responseCodes = new ArrayList<>();
            for (String hygieiaAPIUrl : hygieiaAPIUrls) {
                this.listener.getLogger().println("Publishing metadata for API " + hygieiaAPIUrl);
                HygieiaService hygieiaService = getHygieiaService(hygieiaAPIUrl, hygieiaDesc.getHygieiaToken(),
                        hygieiaDesc.getHygieiaJenkinsName(), hygieiaDesc.isUseProxy());
                HygieiaResponse metadataResponse = hygieiaService.publishMetaData(new MetaDataBuilder().createRequest(this.run, step));
                if (metadataResponse.getResponseCode() == HttpStatus.SC_CREATED) {
                    listener.getLogger()
                            .println("Hygieia: Published Metadata. BuildUrl=" + step.getKey()
                                    + ", type=" + step.getType() + ", source=" + step.getSource()
                                    + ". " + metadataResponse.toString());
                } else {
                    listener.getLogger()
                            .println("Hygieia: Failed Publishing Metadata. BuildUrl=" + step.getKey()
                                    + ", type=" + step.getType() + ", source=" + step.getSource()
                                    + ". " + metadataResponse.toString());
                }

                responseCodes.add(Integer.valueOf(metadataResponse.getResponseCode()));
            }
            return responseCodes;
        }

        HygieiaService getHygieiaService(String hygieiaAPIUrl, String hygieiaToken, String hygieiaJenkinsName,
                                         boolean useProxy) {
            return new DefaultHygieiaService(hygieiaAPIUrl, hygieiaToken, hygieiaJenkinsName, useProxy);
        }

    }
}
