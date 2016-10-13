package com.capitalone.dashboard.jenkins;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by plv163 on 13/10/2016.
 */
public class JenkinsBuild {
    private List<Artifact> artifacts;

    private JenkinsBuild() {
        // required for converter
    }

    private JenkinsBuild(Builder builder) {
        artifacts = builder.artifacts;
    }

    @SuppressWarnings("PMD.AccessorClassGeneration")
    public static Builder newBuilder() {
        return new Builder();
    }

    public List<Artifact> getArtifacts() {
        return artifacts;
    }


    public static final class Builder {
        private List<Artifact> artifacts = new ArrayList<>();

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
    }
}
