package jenkins.plugins.hygieia;

import com.capitalone.dashboard.request.CodeQualityCreateRequest;
import hudson.Extension;
import hudson.model.AbstractBuild;
import hudson.model.TaskListener;
import hudson.model.listeners.RunListener;
import hygieia.builder.BuildBuilder;
import hygieia.builder.SonarBuilder;
import jenkins.model.Jenkins;
import org.apache.commons.httpclient.HttpStatus;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.net.URISyntaxException;

@Extension
public class HygieiaGlobalListener extends RunListener<AbstractBuild> {


    @Override
    public void onCompleted(AbstractBuild build, TaskListener listener) {

        HygieiaPublisher.DescriptorImpl hygieiaGlobalListenerDescriptor = getDescriptor();
        
        HygieiaService hygieiaService = getHygieiaService(hygieiaGlobalListenerDescriptor);

        HygieiaResponse buildResponse = null;

        if (hygieiaGlobalListenerDescriptor.isHygieiaPublishBuildDataGlobal() || hygieiaGlobalListenerDescriptor.isHygieiaPublishSonarDataGlobal()) {

            BuildBuilder builder = getBuildBuilder(build, listener, hygieiaGlobalListenerDescriptor);

            buildResponse = hygieiaService.publishBuildData(builder.getBuildData());
            if (buildResponse.getResponseCode() == HttpStatus.SC_CREATED) {
                listener.getLogger().println("Hygieia: Auto Published Build Complete Data. " + buildResponse.toString());
            } else {
                listener.getLogger().println("Hygieia: Failed Publishing Build Complete Data. " + buildResponse.toString());
            }
        }

        if (hygieiaGlobalListenerDescriptor.isHygieiaPublishSonarDataGlobal()) {
            try {

                SonarBuilder sonarBuilder = getSonarBuilder(buildResponse, build, listener, hygieiaGlobalListenerDescriptor);

                CodeQualityCreateRequest request = sonarBuilder.getSonarMetrics();
                if (request != null) {
                    HygieiaResponse sonarResponse = hygieiaService.publishSonarResults(request);
                    if (sonarResponse.getResponseCode() == HttpStatus.SC_CREATED) {
                        listener.getLogger().println("Hygieia: Auto Published Sonar Data. " + sonarResponse.toString());
                    } else {
                        listener.getLogger().println("Hygieia: Failed Auto Publishing Sonar Data. " + sonarResponse.toString());
                    }
                } else {
                    listener.getLogger().println("Hygieia: Auto Published Sonar Result. Nothing to publish");
                }
            } catch (IOException | URISyntaxException | ParseException e) {
                listener.getLogger().println("Hygieia: Error Auto Publishing Sonar data." + '\n' + e.getMessage());
            }
        }

    }

    protected HygieiaPublisher.DescriptorImpl getDescriptor() {
        return Jenkins.getInstance().getDescriptorByType(HygieiaPublisher.DescriptorImpl.class);
    }

    protected BuildBuilder getBuildBuilder(AbstractBuild build, TaskListener listener, HygieiaPublisher.DescriptorImpl hygieiaGlobalListenerDescriptor) {
        return new BuildBuilder(build, hygieiaGlobalListenerDescriptor.getHygieiaJenkinsName(), listener, true, true);
    }

    protected SonarBuilder getSonarBuilder(HygieiaResponse buildResponse, AbstractBuild build, TaskListener listener, HygieiaPublisher.DescriptorImpl hygieiaGlobalListenerDescriptor) throws ParseException, IOException, URISyntaxException {
        return new SonarBuilder(build, listener, hygieiaGlobalListenerDescriptor.getHygieiaJenkinsName(), null,
                null, buildResponse != null ? buildResponse.getResponseValue() : "", hygieiaGlobalListenerDescriptor.isUseProxy());
    }

    protected HygieiaService getHygieiaService(HygieiaPublisher.DescriptorImpl hygieiaGlobalListenerDescriptor) {
        return  hygieiaGlobalListenerDescriptor.getHygieiaService(hygieiaGlobalListenerDescriptor.getHygieiaAPIUrl(), hygieiaGlobalListenerDescriptor.getHygieiaToken(),
                hygieiaGlobalListenerDescriptor.getHygieiaJenkinsName(), hygieiaGlobalListenerDescriptor.isUseProxy());
    }

}
