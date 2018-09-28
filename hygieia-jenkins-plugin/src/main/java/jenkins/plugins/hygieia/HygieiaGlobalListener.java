package jenkins.plugins.hygieia;

import com.capitalone.dashboard.request.CodeQualityCreateRequest;
import com.capitalone.dashboard.request.GenericCollectorItemCreateRequest;
import hudson.Extension;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.model.listeners.RunListener;
import hygieia.builder.BuildBuilder;
import hygieia.builder.GenericCollectorItemBuilder;
import hygieia.builder.SonarBuilder;
import hygieia.utils.HygieiaUtils;
import jenkins.model.Jenkins;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.httpclient.HttpStatus;
import org.json.simple.parser.ParseException;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

@Extension
public class HygieiaGlobalListener extends RunListener<Run<?,?>> {


    @Override
    public void onCompleted(Run run, @Nonnull TaskListener listener) {

        HygieiaPublisher.DescriptorImpl hygieiaGlobalListenerDescriptor = getDescriptor();
        
        HygieiaService hygieiaService = getHygieiaService(hygieiaGlobalListenerDescriptor);

        HygieiaResponse buildResponse = null;

        if (hygieiaGlobalListenerDescriptor.isHygieiaPublishBuildDataGlobal() || hygieiaGlobalListenerDescriptor.isHygieiaPublishSonarDataGlobal()) {

            BuildBuilder builder = getBuildBuilder(run, listener, hygieiaGlobalListenerDescriptor);

            buildResponse = hygieiaService.publishBuildData(builder.getBuildData());
            if (buildResponse.getResponseCode() == HttpStatus.SC_CREATED) {
                listener.getLogger().println("Hygieia: Auto Published Build Complete Data. " + buildResponse.toString());
            } else {
                listener.getLogger().println("Hygieia: Failed Publishing Build Complete Data. " + buildResponse.toString());
            }
        }

        if (hygieiaGlobalListenerDescriptor.isHygieiaPublishSonarDataGlobal()) {
            try {

                SonarBuilder sonarBuilder = getSonarBuilder(buildResponse, run, listener, hygieiaGlobalListenerDescriptor);

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

        if (!CollectionUtils.isEmpty(hygieiaGlobalListenerDescriptor.getHygieiaPublishGenericCollectorItems())) {
            List<HygieiaPublisher.GenericCollectorItem> items = hygieiaGlobalListenerDescriptor.getHygieiaPublishGenericCollectorItems();
            for (HygieiaPublisher.GenericCollectorItem item : items) {
                GenericCollectorItemBuilder genericCollectorItemBuilder = getGenericCollectorItemBuilder(buildResponse, run, hygieiaGlobalListenerDescriptor, item.toolName, item.pattern);
                try {
                    List<GenericCollectorItemCreateRequest> genericCollectorItemCreateRequests = genericCollectorItemBuilder.getRequests();
                    if (CollectionUtils.isEmpty(genericCollectorItemCreateRequests)) continue;
                    for (GenericCollectorItemCreateRequest gcir : genericCollectorItemCreateRequests) {
                        HygieiaResponse genericItemResponse = hygieiaService.publishGenericCollectorItemData(gcir);
                        if (genericItemResponse.getResponseCode() == HttpStatus.SC_CREATED) {
                            listener.getLogger().println("Hygieia: Auto Published " + gcir.getToolName() + " Data. " + genericItemResponse.toString());
                        } else {
                            listener.getLogger().println("Hygieia: Auto Published " + gcir.getToolName() + " Data. " + genericItemResponse.toString());
                        }
                    }
                } catch (IOException e) {
                    listener.getLogger().println("Hygieia: Error Auto Publishing Generic Collector Item data." + '\n' + e.getMessage());
                }

            }
        }

    }

    protected HygieiaPublisher.DescriptorImpl getDescriptor() {
        return Jenkins.getInstance().getDescriptorByType(HygieiaPublisher.DescriptorImpl.class);
    }

    protected BuildBuilder getBuildBuilder(Run run, TaskListener listener, HygieiaPublisher.DescriptorImpl hygieiaGlobalListenerDescriptor) {
        return new BuildBuilder(run, hygieiaGlobalListenerDescriptor.getHygieiaJenkinsName(), listener, HygieiaUtils.getBuildStatus(run.getResult()), true);
    }

    protected SonarBuilder getSonarBuilder(HygieiaResponse buildResponse, Run run, TaskListener listener, HygieiaPublisher.DescriptorImpl hygieiaGlobalListenerDescriptor) throws ParseException, IOException, URISyntaxException {
        return new SonarBuilder(run, listener, hygieiaGlobalListenerDescriptor.getHygieiaJenkinsName(), null,
                null, buildResponse != null ? buildResponse.getResponseValue() : "", hygieiaGlobalListenerDescriptor.isUseProxy());
    }

    protected GenericCollectorItemBuilder getGenericCollectorItemBuilder (HygieiaResponse buildResponse, Run run, HygieiaPublisher.DescriptorImpl hygieiaGlobalListenerDescriptor, String toolName, String pattern) {
        return new GenericCollectorItemBuilder(run, hygieiaGlobalListenerDescriptor.getHygieiaJenkinsName(),toolName, pattern, buildResponse != null ? buildResponse.getResponseValue() : "");
    }

    protected HygieiaService getHygieiaService(HygieiaPublisher.DescriptorImpl hygieiaGlobalListenerDescriptor) {
        return  hygieiaGlobalListenerDescriptor.getHygieiaService(hygieiaGlobalListenerDescriptor.getHygieiaAPIUrl(), hygieiaGlobalListenerDescriptor.getHygieiaToken(),
                hygieiaGlobalListenerDescriptor.getHygieiaJenkinsName(), hygieiaGlobalListenerDescriptor.isUseProxy());
    }

}
