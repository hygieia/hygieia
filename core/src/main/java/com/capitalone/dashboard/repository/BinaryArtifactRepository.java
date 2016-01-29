package com.capitalone.dashboard.repository;

import com.capitalone.dashboard.model.BinaryArtifact;
import com.capitalone.dashboard.model.Build;
import org.bson.types.ObjectId;
import org.springframework.data.repository.CrudRepository;

public interface BinaryArtifactRepository extends CrudRepository<BinaryArtifact, ObjectId> {

    Iterable<BinaryArtifact> findByCollectorItemId(ObjectId collectorItemId);

    Iterable<BinaryArtifact> findByArtifactGroupIdAndArtifactNameAndArtifactVersion(String artifactGroupId, String artifactName, String artifactVersion);

    Iterable<BinaryArtifact> findByArtifactName(String artifactName);

    Iterable<BinaryArtifact> findByArtifactNameAndArtifactVersion(String artifactName, String artifactVersion);

    Iterable<BinaryArtifact> findByArtifactGroupId(String artifactGroupId);

    Iterable<BinaryArtifact> findByBuildInfo (Build buildInfo);

    BinaryArtifact findByArtifactGroupIdAndArtifactNameAndArtifactVersionAndBuildInfo (String artifactGroupId, String artifactName, String artifactVersion, Build buildInfo);

}
