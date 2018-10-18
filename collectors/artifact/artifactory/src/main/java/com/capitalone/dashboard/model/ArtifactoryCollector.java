package com.capitalone.dashboard.model;

import com.capitalone.dashboard.collector.ArtifactorySettings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Collector implementation for XLDeploy that stores Artifactory server URLs.
 */
public class ArtifactoryCollector extends Collector {
    private List<String> artifactoryServers = new ArrayList<>();

    public List<String> getArtifactoryServers() {
        return artifactoryServers;
    }

    public static ArtifactoryCollector prototype(ArtifactorySettings settings) {
        ArtifactoryCollector protoType = new ArtifactoryCollector();
        protoType.setName("Artifactory");
        protoType.setCollectorType(CollectorType.Artifact);
        protoType.setOnline(true);
        protoType.setEnabled(true);
        protoType.getArtifactoryServers().addAll(settings.getServers().stream().map(ServerSetting::getUrl).collect(Collectors.toList()));
        Map<String, Object> options = new HashMap<>();

        switch (settings.getMode()) {
            case REPO_BASED:
                options.put(ArtifactoryRepo.INSTANCE_URL, "");
                options.put(ArtifactoryRepo.REPO_NAME, "");
                options.put(ArtifactoryRepo.REPO_URL, "");
                break;

            case ARTIFACT_BASED:
                options.put(ArtifactItem.INSTANCE_URL, "");
                options.put(ArtifactItem.REPO_NAME, "");
                options.put(ArtifactItem.ARTIFACT_NAME, "");
                options.put(ArtifactItem.PATH, "");
                break;

            default:
        }
        protoType.setAllFields(options);
        protoType.setUniqueFields(options);
        return protoType;
    }
}
