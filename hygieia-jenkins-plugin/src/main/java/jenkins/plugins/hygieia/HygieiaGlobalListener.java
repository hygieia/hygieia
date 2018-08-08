package jenkins.plugins.hygieia;

import hudson.Extension;
import hudson.model.AbstractBuild;
import hudson.model.TaskListener;
import hudson.model.listeners.RunListener;
import hygieia.builder.BuildBuilder;
import jenkins.model.Jenkins;
import org.apache.commons.httpclient.HttpStatus;

@Extension
public class HygieiaGlobalListener extends RunListener<AbstractBuild> {


    HygieiaService getHygieiaService(String hygieiaAPIUrl, String hygieiaToken, String hygieiaJenkinsName, boolean useProxy) {
        return new DefaultHygieiaService(hygieiaAPIUrl, hygieiaToken, hygieiaJenkinsName, useProxy);
    }

    @Override
    public void onCompleted(AbstractBuild build, TaskListener listener) {
        HygieiaPublisher.DescriptorImpl hygieiaGlobalListenerDescriptor = Jenkins.getInstance().getDescriptorByType(HygieiaPublisher.DescriptorImpl.class);

        if (hygieiaGlobalListenerDescriptor.isHygieiaPublishBuildDataGlobal()) {
            Jenkins jenkins = null;
            try {
                jenkins = Jenkins.getInstance();
            } catch (NullPointerException ne) {
                listener.error(ne.toString());
            }

            if (jenkins != null) {
                HygieiaPublisher.DescriptorImpl hygieiaDesc = jenkins.getDescriptorByType(HygieiaPublisher.DescriptorImpl.class);
                HygieiaService hygieiaService = getHygieiaService(hygieiaDesc.getHygieiaAPIUrl(), hygieiaDesc.getHygieiaToken(),
                        hygieiaDesc.getHygieiaJenkinsName(), hygieiaDesc.isUseProxy());
                BuildBuilder builder = new BuildBuilder(build, hygieiaGlobalListenerDescriptor.getHygieiaJenkinsName(), listener, true, true);
                HygieiaResponse buildResponse = hygieiaService.publishBuildData(builder.getBuildData());
                if (buildResponse.getResponseCode() == HttpStatus.SC_CREATED) {
                    listener.getLogger().println("Hygieia: Auto Published Build Complete Data. " + buildResponse.toString());
                } else {
                    listener.getLogger().println("Hygieia: Failed Publishing Build Complete Data. " + buildResponse.toString());
                }
            }
        }
    }
}
