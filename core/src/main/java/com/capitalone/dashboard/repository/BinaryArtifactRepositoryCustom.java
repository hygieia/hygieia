package com.capitalone.dashboard.repository;

import java.util.Map;

import com.capitalone.dashboard.model.BinaryArtifact;

public interface BinaryArtifactRepositoryCustom {
	String CANONICAL_NAME = "canonicalName";
	String ARTIFACT_GROUP_ID = "artifactGroupId";
	String ARTIFACT_MODULE = "artifactModule";
	String ARTIFACT_VERSION = "artifactVersion";
	String ARTIFACT_NAME = "artifactName";
	String ARTIFACT_CLASSIFIER = "artifactClassifier";
	String ARTIFACT_EXTENSION = "artifactExtension";
	String BUILD_INFO_ID = "buildInfo.id";
			
	Iterable<BinaryArtifact> findByAttributes(Map<String, Object> attributes);
}
