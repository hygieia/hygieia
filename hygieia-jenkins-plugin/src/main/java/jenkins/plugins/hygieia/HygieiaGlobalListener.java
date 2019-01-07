package jenkins.plugins.hygieia;

import com.capitalone.dashboard.request.CodeQualityCreateRequest;
import com.capitalone.dashboard.request.GenericCollectorItemCreateRequest;
import com.capitalone.dashboard.response.BuildDataCreateResponse;
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
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Triple;
import org.json.simple.parser.ParseException;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Extension
public class HygieiaGlobalListener extends RunListener<Run<?, ?>> {

    public HygieiaGlobalListener() {
        super();
    }

    @Override
    public void onFinalized(hudson.model.Run<?, ?> run) {
        super.onFinalized(run);
    }

    @Override
    public void onCompleted(Run run, @Nonnull TaskListener listener) {
        super.onCompleted(run, listener);
        HygieiaPublisher.DescriptorImpl hygieiaGlobalListenerDescriptor = getDescriptor();
        final long starttime = System.currentTimeMillis();
        boolean showConsoleOutput = hygieiaGlobalListenerDescriptor.isShowConsoleOutput();
        boolean skipPublish = HygieiaUtils.isJobExcluded(run.getParent().getName(), hygieiaGlobalListenerDescriptor.getHygieiaExcludeJobNames());
        listener.getLogger().println("*** Jenkins Job - " + run.getParent().getName() + " completed. ***");
        if (showConsoleOutput) {
            listener.getLogger().println("Hygieia: Automatically publishing build data to Hygieia using " + hygieiaGlobalListenerDescriptor.getPluginVersionInfo() + ", Please refresh your browser to see the status.");
        }

        if (skipPublish) {
            if (showConsoleOutput) { listener.getLogger().println("Hygieia: Skipping publish to Hygieia as the job was excluded in global configuration. "); }
            super.onCompleted(run, listener);
            return;
        }
        String rawApiEndopints = StringUtils.isNotEmpty(hygieiaGlobalListenerDescriptor.getHygieiaAPIUrl()) ? hygieiaGlobalListenerDescriptor.getHygieiaAPIUrl() : "";
        List<String> apiEndpints = Arrays.asList(rawApiEndopints.split(HygieiaUtils.SEPERATOR));
        String rawAppUrls = StringUtils.isNotEmpty(hygieiaGlobalListenerDescriptor.getHygieiaAppUrl()) ? hygieiaGlobalListenerDescriptor.getHygieiaAppUrl() : "";
        List<String> appUrls = Arrays.asList(rawAppUrls.split(HygieiaUtils.SEPERATOR));
        int index = 0;
        if (CollectionUtils.isEmpty(apiEndpints)) {
            if (showConsoleOutput) { listener.getLogger().println("Hygieia: Skipping publish to Hygieia as no service endpoints were configured. "); }
            super.onCompleted(run, listener);
            return;
        }
        for (String apiEndPoint : apiEndpints) {
            if (StringUtils.isNotEmpty(apiEndPoint)) {
                if (showConsoleOutput) { listener.getLogger().println("Hygieia: *** Publish to API Endpoint - " + (index + 1) + " ***"); }
                HygieiaService hygieiaService = getHygieiaService(hygieiaGlobalListenerDescriptor, apiEndPoint);
                String hygieiaAppUrl = (CollectionUtils.size(appUrls) > index) ? appUrls.get(index) : null;
                Triple<String, String, BuildDataCreateResponse> buildResponseObject = publishBuildData(run, listener, hygieiaGlobalListenerDescriptor, hygieiaService, hygieiaAppUrl);
                String convertedBuildResponseString = null;
                String dashboardLink = null;

                if (buildResponseObject != null) {
                    convertedBuildResponseString = buildResponseObject.getLeft();
                    dashboardLink = buildResponseObject.getMiddle();
                }
                publishSonarData(run, listener, hygieiaGlobalListenerDescriptor, hygieiaService, convertedBuildResponseString);
                publishGenericCollectorItems(run, listener, hygieiaGlobalListenerDescriptor, hygieiaService, convertedBuildResponseString);

                // publish the dashboard link
                if (showConsoleOutput && StringUtils.isNotEmpty(dashboardLink)) {
                    listener.getLogger().println("Hygieia: Link to the Hygieia Dashboard for API Endpoint " + (index + 1) + " - " + dashboardLink);
                }
                index++;
            }
        }
        final long endtime = System.currentTimeMillis();
        if (showConsoleOutput) { listener.getLogger().println("Hygieia: *** Hygieia publish completed in " + (endtime-starttime)/1000 + " seconds at " + org.joda.time.LocalDateTime.now().toString()+" ***"); }
    }

    protected Triple<String, String, BuildDataCreateResponse> publishBuildData(Run run, TaskListener listener, HygieiaPublisher.DescriptorImpl hygieiaGlobalListenerDescriptor, HygieiaService hygieiaService, String hygieiaAppUrl) {
        String dashboardLink = null;
        String convertedBuildResponseString = null;
        BuildDataCreateResponse buildDataResponse = null;
        boolean publishBuildData = hygieiaGlobalListenerDescriptor.isHygieiaPublishBuildDataGlobal() || hygieiaGlobalListenerDescriptor.isHygieiaPublishSonarDataGlobal();
        boolean showConsoleOutput = hygieiaGlobalListenerDescriptor.isShowConsoleOutput();
        if (publishBuildData) {
            BuildBuilder builder = getBuildBuilder(run, listener, hygieiaGlobalListenerDescriptor);
            HygieiaResponse buildResponse = hygieiaService.publishBuildDataV3(builder.getBuildData());
            if (buildResponse.getResponseCode() == HttpStatus.SC_CREATED) {
                try {
                    buildDataResponse = HygieiaUtils.convertJsonToObject(buildResponse.getResponseValue(), BuildDataCreateResponse.class);
                    convertedBuildResponseString = String.format("%s,%s", buildDataResponse.getId().toString(), buildDataResponse.getCollectorItemId().toString());
                    if (StringUtils.isNotEmpty(hygieiaAppUrl) && buildDataResponse.getDashboardId() != null && StringUtils.isNotEmpty(buildDataResponse.getDashboardId().toString())) {
                        dashboardLink = hygieiaAppUrl + HygieiaUtils.DASHBOARD_URI + buildDataResponse.getDashboardId().toString();
                    }
                    if (showConsoleOutput) { listener.getLogger().println("Hygieia: Auto Published Build Complete Data. Response Code: " + buildResponse.getResponseCode() + ". " + convertedBuildResponseString); }
                } catch (IOException e) {
                    if (showConsoleOutput) { listener.getLogger().println("Hygieia: Publishing Build Complete Data, however error reading response. " + '\n' + e.getMessage()); }
                    return null;
                }

            } else {
                if (showConsoleOutput) { listener.getLogger().println("Hygieia: Failed Publishing Build Complete Data. " + buildResponse.toString()); }
                return null;
            }
        }
        convertedBuildResponseString = StringUtils.isEmpty(convertedBuildResponseString) ? "" : convertedBuildResponseString;
        return Triple.of(convertedBuildResponseString, dashboardLink, buildDataResponse);
    }

    protected void publishSonarData(Run run, TaskListener listener, HygieiaPublisher.DescriptorImpl hygieiaGlobalListenerDescriptor, HygieiaService hygieiaService, String convertedBuildResponseString) {
        if (!hygieiaGlobalListenerDescriptor.isHygieiaPublishSonarDataGlobal()) { return; }
        boolean showConsoleOutput = hygieiaGlobalListenerDescriptor.isShowConsoleOutput();
        try {
            // Quickfix by using convertedBuildResponseString to make it work with current SonarBuilder will revisit later.
            SonarBuilder sonarBuilder = getSonarBuilder(convertedBuildResponseString, run, listener, hygieiaGlobalListenerDescriptor);
            CodeQualityCreateRequest request = sonarBuilder.getSonarMetrics();
            if (request != null) {
                HygieiaResponse sonarResponse = hygieiaService.publishSonarResults(request);
                if (sonarResponse.getResponseCode() == HttpStatus.SC_CREATED) {
                    if (showConsoleOutput) { listener.getLogger().println("Hygieia: Auto Published Sonar Data. " + sonarResponse.toString()); }
                } else {
                    if (showConsoleOutput) { listener.getLogger().println("Hygieia: Failed Auto Publishing Sonar Data. " + sonarResponse.toString()); }
                }
            } else {
                if (showConsoleOutput) { listener.getLogger().println("Hygieia: Auto Published Sonar Result. Nothing to publish"); }
            }
        } catch (ParseException e) {
            if (showConsoleOutput) { listener.getLogger().println("Hygieia: Error Auto Publishing Sonar data." + '\n' + e.getMessage()); }
        }
    }

    protected void publishGenericCollectorItems(Run run, TaskListener listener, HygieiaPublisher.DescriptorImpl hygieiaGlobalListenerDescriptor, HygieiaService hygieiaService, String convertedBuildResponseString) {
        if (CollectionUtils.isEmpty(hygieiaGlobalListenerDescriptor.getHygieiaPublishGenericCollectorItems())) { return; }
        boolean showConsoleOutput = hygieiaGlobalListenerDescriptor.isShowConsoleOutput();
        List<HygieiaPublisher.GenericCollectorItem> items = hygieiaGlobalListenerDescriptor.getHygieiaPublishGenericCollectorItems();
        for (HygieiaPublisher.GenericCollectorItem item : items) {
            GenericCollectorItemBuilder genericCollectorItemBuilder = getGenericCollectorItemBuilder(run, hygieiaGlobalListenerDescriptor, item.toolName, item.pattern, convertedBuildResponseString);
            try {
                List<GenericCollectorItemCreateRequest> genericCollectorItemCreateRequests = genericCollectorItemBuilder.getRequests();
                if (CollectionUtils.isEmpty(genericCollectorItemCreateRequests)) continue;
                for (GenericCollectorItemCreateRequest gcir : genericCollectorItemCreateRequests) {
                    HygieiaResponse genericItemResponse = hygieiaService.publishGenericCollectorItemData(gcir);
                    if (genericItemResponse.getResponseCode() == HttpStatus.SC_CREATED) {
                        if (showConsoleOutput) { listener.getLogger().println("Hygieia: Auto Published " + gcir.getToolName() + " Data. " + genericItemResponse.toString()); }
                    } else {
                        if (showConsoleOutput) { listener.getLogger().println("Hygieia: Auto Published " + gcir.getToolName() + " Data. " + genericItemResponse.toString()); }
                    }
                }
            } catch (IOException e) {
                if (showConsoleOutput) { listener.getLogger().println("Hygieia: Error Auto Publishing Generic Collector Item data." + '\n' + e.getMessage()); }
            }
        }
    }

    protected HygieiaPublisher.DescriptorImpl getDescriptor() {
        return Objects.requireNonNull(Jenkins.getInstance()).getDescriptorByType(HygieiaPublisher.DescriptorImpl.class);
    }

    protected BuildBuilder getBuildBuilder(Run run, TaskListener listener, HygieiaPublisher.DescriptorImpl hygieiaGlobalListenerDescriptor) {
        return new BuildBuilder(run, hygieiaGlobalListenerDescriptor.getHygieiaJenkinsName(), listener, HygieiaUtils.getBuildStatus(run.getResult()), true);
    }

    protected SonarBuilder getSonarBuilder(String buildResponse, Run run, TaskListener listener, HygieiaPublisher.DescriptorImpl hygieiaGlobalListenerDescriptor) {
        return new SonarBuilder(run, listener, hygieiaGlobalListenerDescriptor.getHygieiaJenkinsName(), null,
                null, buildResponse, hygieiaGlobalListenerDescriptor.isUseProxy());
    }

    protected GenericCollectorItemBuilder getGenericCollectorItemBuilder(Run run, HygieiaPublisher.DescriptorImpl hygieiaGlobalListenerDescriptor, String toolName, String pattern, String convertedBuildResponseString) {
        return new GenericCollectorItemBuilder(run, toolName, pattern, convertedBuildResponseString);
    }

    protected HygieiaService getHygieiaService(HygieiaPublisher.DescriptorImpl hygieiaGlobalListenerDescriptor, String apiEndpoint) {
        return hygieiaGlobalListenerDescriptor.getHygieiaService(apiEndpoint, hygieiaGlobalListenerDescriptor.getHygieiaToken(),
                hygieiaGlobalListenerDescriptor.getHygieiaJenkinsName(), hygieiaGlobalListenerDescriptor.isUseProxy());
    }

}
