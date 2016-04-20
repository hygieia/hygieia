package com.capitalone.dashboard.model;

import lombok.Data;

/**
 * Class representing any stage of a {@link Pipeline}
 */
@Data
public class EnvironmentStage extends Stage{
    /** {@link BinaryArtifact} The last artifact to be processed */
    private BinaryArtifact lastArtifact;

}
