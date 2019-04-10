package com.capitalone.dashboard.evaluator;

import com.capitalone.dashboard.ApiSettings;
import com.capitalone.dashboard.model.AuditException;
import com.capitalone.dashboard.model.BinaryArtifact;
import com.capitalone.dashboard.model.CollectorItem;
import com.capitalone.dashboard.model.CollectorType;
import com.capitalone.dashboard.model.Dashboard;
import com.capitalone.dashboard.repository.BinaryArtifactRepository;
import com.capitalone.dashboard.response.ArtifactAuditResponse;
import com.capitalone.dashboard.status.ArtifactAuditStatus;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Component
public class ArtifactEvaluator extends Evaluator<ArtifactAuditResponse> {

    private final BinaryArtifactRepository binaryArtifactRepository;
    private final ApiSettings apiSettings;
    private final static String DOCKER = "docker";
    private final static String ARTIFACT_NAME = "artifactName";
    private final static String PATH = "path";
    private final static String REPO_NAME = "repoName";

    @Autowired
    public ArtifactEvaluator(BinaryArtifactRepository binaryArtifactRepository, ApiSettings apiSettings) {
        this.binaryArtifactRepository = binaryArtifactRepository;
        this.apiSettings = apiSettings;
    }

    @Override
    public Collection<ArtifactAuditResponse> evaluate(Dashboard dashboard, long beginDate, long endDate, Map<?, ?> data) throws AuditException {

        List<CollectorItem> artifactCollectorItems = getCollectorItems(dashboard, "build", CollectorType.Artifact);
        if (CollectionUtils.isEmpty(artifactCollectorItems)) {
            throw new AuditException("No artifacts are available", AuditException.NO_COLLECTOR_ITEM_CONFIGURED);
        }
        return artifactCollectorItems.stream().map(item -> evaluate(item, beginDate, endDate, null)).collect(Collectors.toList());
    }

    @Override
    public ArtifactAuditResponse evaluate(CollectorItem collectorItem, long beginDate, long endDate, Map<?, ?> data) {
        return getArtifactAuditResponse(collectorItem, beginDate, endDate);
    }

    private ArtifactAuditResponse getArtifactAuditResponse(CollectorItem collectorItem, long beginDate, long endDate) {
        ArtifactAuditResponse artifactAuditResponse = new ArtifactAuditResponse();
        if(collectorItem ==null) return artifactNotConfigured();

        String artifactName = getValue(collectorItem, ARTIFACT_NAME);
        String path = getValue(collectorItem, PATH);
        String repoName = getValue(collectorItem, REPO_NAME);

        if (StringUtils.isEmpty(artifactName) || StringUtils.isEmpty(repoName) || StringUtils.isEmpty(path)) {
            return getErrorResponse(collectorItem, ArtifactAuditStatus.COLLECTOR_ITEM_ERROR);
        }
        if (!CollectionUtils.isEmpty(collectorItem.getErrors())) {
            return getErrorResponse(collectorItem, ArtifactAuditStatus.UNAVAILABLE);
        }
        List<BinaryArtifact> binaryArtifacts = binaryArtifactRepository.findByCollectorItemIdAndTimestampIsBetweenOrderByTimestampDesc(collectorItem.getId(), beginDate - 1, endDate + 1);
        if (CollectionUtils.isEmpty(binaryArtifacts)) {
            return getErrorResponse(collectorItem, ArtifactAuditStatus.NO_ACTIVITY);
        }

        BinaryArtifact binaryArtifact = binaryArtifacts.get(0);
        artifactAuditResponse.setBinaryArtifact(binaryArtifact);
        artifactAuditResponse.setLastUpdated(binaryArtifact.getTimestamp());
        boolean isBuild = binaryArtifact.getBuildInfo() != null;
        if (isServiceAccount(binaryArtifact.getCreatedBy())) {
            evaluateArtifactForServiceAccountAndBuild(artifactAuditResponse, isBuild);
        }
        if (binaryArtifact.getVirtualRepos().stream().anyMatch(repo -> repo.contains(DOCKER))) {
            artifactAuditResponse.addAuditStatus(ArtifactAuditStatus.ART_DOCK_IMG_FOUND);
        }
        return artifactAuditResponse;
    }

    private String getValue(CollectorItem collectorItem, String attribute) {
        return (String) collectorItem.getOptions().get(attribute);
    }

    private void evaluateArtifactForServiceAccountAndBuild(ArtifactAuditResponse artifactAuditResponse, boolean isBuild) {
        if (isBuild) {
            artifactAuditResponse.addAuditStatus(ArtifactAuditStatus.ART_SYS_ACCT_BUILD_AUTO);
        } else {
            artifactAuditResponse.addAuditStatus(ArtifactAuditStatus.ART_SYS_ACCT_BUILD_USER);
        }
    }

    private boolean isServiceAccount(String createdBy) {
        return !Pattern.compile(apiSettings.getServiceAccountRegEx()).matcher(createdBy).matches();

    }

    private ArtifactAuditResponse getErrorResponse(CollectorItem collectorItem, ArtifactAuditStatus artifactAuditStatus) {
        ArtifactAuditResponse errorAuditResponse = new ArtifactAuditResponse();
        errorAuditResponse.addAuditStatus(artifactAuditStatus);
        errorAuditResponse.setLastExecutionTime(collectorItem.getLastUpdated());
        errorAuditResponse.setArtifactName(getValue(collectorItem, ARTIFACT_NAME));
        return errorAuditResponse;
    }

    private ArtifactAuditResponse artifactNotConfigured(){
        ArtifactAuditResponse notConfigured = new ArtifactAuditResponse();
        notConfigured.addAuditStatus(ArtifactAuditStatus.ARTIFACT_NOT_CONFIGURED);
        notConfigured.setErrorMessage("Unable to register in Hygieia, Artifact not configured ");
        return notConfigured;
    }

}
