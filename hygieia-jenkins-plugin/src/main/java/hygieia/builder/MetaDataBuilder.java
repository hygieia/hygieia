package hygieia.builder;

import com.capitalone.dashboard.request.MetadataCreateRequest;
import hudson.model.AbstractBuild;
import hudson.model.Run;
import hygieia.utils.HygieiaUtils;
import jenkins.plugins.hygieia.workflow.HygieiaMetaDataPublishStep;

public class MetaDataBuilder {

    public MetaDataBuilder() {

    }

    public MetadataCreateRequest createRequest(AbstractBuild<?, ?> build, HygieiaMetaDataPublishStep publisherStep) {
        MetadataCreateRequest metadataCreateRequest = new MetadataCreateRequest();
        metadataCreateRequest.setKey(HygieiaUtils.getBuildUrl(build));
        setMetaData(publisherStep, metadataCreateRequest);
        return metadataCreateRequest;
    }

    public MetadataCreateRequest createRequest(Run<?, ?> run, HygieiaMetaDataPublishStep publisherStep) {
        MetadataCreateRequest metadataCreateRequest = new MetadataCreateRequest();
        metadataCreateRequest.setKey(HygieiaUtils.getBuildUrl(run));
        setMetaData(publisherStep, metadataCreateRequest);
        return metadataCreateRequest;
    }

    private void setMetaData(HygieiaMetaDataPublishStep publisherStep, MetadataCreateRequest metadataCreateRequest) {
        metadataCreateRequest.setKey(publisherStep.getKey());
        metadataCreateRequest.setType(publisherStep.getType());
        metadataCreateRequest.setRawData(publisherStep.getRawData());
        metadataCreateRequest.setSource(publisherStep.getSource());
    }


}
