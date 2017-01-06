package com.capitalone.dashboard.repository;

import com.capitalone.dashboard.model.BinaryArtifact;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface BinaryArtifactRepository extends CrudRepository<BinaryArtifact, ObjectId>, BinaryArtifactRepositoryCustom {

    Iterable<BinaryArtifact> findByCollectorItemId(ObjectId collectorItemId);
    
    @Query("{ 'collectorItemId': ?0, 'artifactGroupId' : ?1, 'artifactModule' : ?2, 'artifactVersion' : ?3, 'artifactName' : ?4, 'artifactClassifier' : ?5, 'artifactExtension' : ?6 }")
    Iterable<BinaryArtifact> findByAttributes(Object collectorItemId, String artifactGroupId, String artifactModule, String artifactVersion, String artifactName, String artifactClassifier, String artifactExtension);
    
    @Query("{ 'artifactGroupId' : ?0, 'artifactModule' : ?1, 'artifactVersion' : ?2, 'artifactName' : ?3, 'artifactClassifier' : ?4, 'artifactExtension' : ?5 }")
    Iterable<BinaryArtifact> findByAttributes(String artifactGroupId, String artifactModule, String artifactVersion, String artifactName, String artifactClassifier, String artifactExtension);

    Iterable<BinaryArtifact> findByBuildInfoId (ObjectId artifactBuildId);

    Iterable<BinaryArtifact> findByArtifactNameAndTimestampGreaterThan(String artifactName, Long timestamp);
    Iterable<BinaryArtifact> findByArtifactNameAndArtifactExtensionAndTimestampGreaterThan(String artifactName, String artifactExtension, Long timestamp);

    @Query(value="{'metadata.buildUrl' : ?0}")
    Iterable<BinaryArtifact> findByMetadataBuildUrl(String buildUrl);
}
