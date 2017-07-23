package com.capitalone.dashboard.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Collector implementation for XLDeploy that stores Artifactory server URLs.
 */
public class ArtifactoryCollector extends Collector {
    private List<String> artifactoryServers = new ArrayList<>();

    public List<String> getArtifactoryServers() {
        return artifactoryServers;
    }

    public static ArtifactoryCollector prototype(List<String> servers) {
    	ArtifactoryCollector protoType = new ArtifactoryCollector();
        protoType.setName("Artifactory");
        protoType.setCollectorType(CollectorType.Artifact);
        protoType.setOnline(true);
        protoType.setEnabled(true);
        protoType.getArtifactoryServers().addAll(servers);
        protoType.getRequiredFields().addAll(Arrays.asList(ArtifactoryRepo.INSTANCE_URL, ArtifactoryRepo.REPO_NAME, ArtifactoryRepo.REPO_URL));
        return protoType;
    }
}
