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

    private HygieiaPublisher.DescriptorImpl hygieiaGlobalListenerDescriptor;
    private HygieiaService hygieiaService;
    private SonarBuilder sonarBuilder;
    private BuildBuilder builder;

    @Override
    public void onCompleted(AbstractBuild build, TaskListener listener) {
        if (hygieiaGlobalListenerDescriptor == null) {
            hygieiaGlobalListenerDescriptor = Jenkins.getInstance().getDescriptorByType(HygieiaPublisher.DescriptorImpl.class);
        }
        if (hygieiaService == null) {
            hygieiaService = new DefaultHygieiaService(hygieiaGlobalListenerDescriptor.getHygieiaAPIUrl(), hygieiaGlobalListenerDescriptor.getHygieiaToken(),
                    hygieiaGlobalListenerDescriptor.getHygieiaJenkinsName(), hygieiaGlobalListenerDescriptor.isUseProxy());
        }
        HygieiaResponse buildResponse = null;

        if (hygieiaGlobalListenerDescriptor.isHygieiaPublishBuildDataGlobal() || hygieiaGlobalListenerDescriptor.isHygieiaPublishSonarDataGlobal()) {
            if (builder == null) {
                builder = new BuildBuilder(build, hygieiaGlobalListenerDescriptor.getHygieiaJenkinsName(), listener, true, true);
            }
            buildResponse = hygieiaService.publishBuildData(builder.getBuildData());
            if (buildResponse.getResponseCode() == HttpStatus.SC_CREATED) {
                listener.getLogger().println("Hygieia: Auto Published Build Complete Data. " + buildResponse.toString());
            } else {
                listener.getLogger().println("Hygieia: Failed Publishing Build Complete Data. " + buildResponse.toString());
            }
        }

        if (hygieiaGlobalListenerDescriptor.isHygieiaPublishSonarDataGlobal()) {
            try {
                if (sonarBuilder == null) {
                    sonarBuilder = new SonarBuilder(build, listener, hygieiaGlobalListenerDescriptor.getHygieiaJenkinsName(), null,
                            null, buildResponse != null ? buildResponse.getResponseValue(): "", hygieiaGlobalListenerDescriptor.isUseProxy());
                }
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

}
