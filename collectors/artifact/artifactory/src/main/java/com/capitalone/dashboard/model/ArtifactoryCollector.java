package com.capitalone.dashboard.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        Map<String, Object> options = new HashMap<>();
        options.put(ArtifactoryRepo.INSTANCE_URL,"");
        options.put(ArtifactoryRepo.REPO_NAME,"");
        options.put(ArtifactoryRepo.REPO_URL,"");
        protoType.setAllFields(options);
        protoType.setUniqueFields(options);
        return protoType;
    }
}
