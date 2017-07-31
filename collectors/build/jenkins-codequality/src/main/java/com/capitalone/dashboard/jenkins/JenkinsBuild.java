package com.capitalone.dashboard.jenkins;

import java.util.ArrayList;
import java.util.List;

public class JenkinsBuild {
    private List<Artifact> artifacts;
    private long timestamp;

    private JenkinsBuild() {
        // required for converter
    }

    private JenkinsBuild(Builder builder) {
        this.artifacts = builder.artifacts;
        this.timestamp = builder.timestamp;
    }

    @SuppressWarnings("PMD.AccessorClassGeneration")
    public static Builder newBuilder() {
        return new Builder();
    }

    public List<Artifact> getArtifacts() {
        return artifacts;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public static final class Builder {
        private List<Artifact> artifacts = new ArrayList<>();
        private long timestamp;

        private Builder() {
        }

        public Builder artifact(Artifact artifact) {
            this.artifacts.add(artifact);
            return this;
        }

        @SuppressWarnings("PMD.AccessorClassGeneration")
        public JenkinsBuild build() {
            return new JenkinsBuild(this);
        }

        public Builder timestamp(long timestamp) {
            this.timestamp = timestamp;
            return this;
        }
    }
}
