package com.capitalone.dashboard.repository;

import com.capitalone.dashboard.jenkins.model.JenkinsCodeQualityJob;
import org.bson.types.ObjectId;

import java.util.List;

public interface JenkinsCodeQualityJobRepository extends BaseCollectorItemRepository<JenkinsCodeQualityJob> {
    List<JenkinsCodeQualityJob> findAllByCollectorId(ObjectId collectorId);
}
