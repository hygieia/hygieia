package com.capitalone.dashboard.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Collector implementation for XLDeploy that stores Artifactory server URLs.
 */
public class ArtifactoryCollector extends Collector {
    private List<String> artifactoryServers = new ArrayList<>();
    private List<String> artifactoryEndpoints = new ArrayList<>();

    public List<String> getArtifactoryServers() {
        return artifactoryServers;
    }
    
    public List<String> getArtifactoryEndpoints() {
        return artifactoryEndpoints;
    }

    public static ArtifactoryCollector prototype(List<String> servers, List<String> artifactoryEndpoints) {
    	ArtifactoryCollector protoType = new ArtifactoryCollector();
        protoType.setName("Artifactory");
        protoType.setCollectorType(CollectorType.Artifact);
        protoType.setOnline(true);
        protoType.setEnabled(true);
        protoType.getArtifactoryServers().addAll(servers);
        protoType.getArtifactoryEndpoints().addAll(artifactoryEndpoints);
        return protoType;
    }
}
