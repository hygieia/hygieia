package com.capitalone.dashboard.repository;

import com.capitalone.dashboard.jenkins.JenkinsJob;
import com.capitalone.dashboard.model.JenkinsCodeQualityJob;
import org.bson.types.ObjectId;

import java.util.List;

/**
 * Created by stephengalbraith on 11/10/2016.
 */
public interface JenkinsCodeQualityJobRepository  extends BaseCollectorItemRepository<JenkinsCodeQualityJob> {
    List<JenkinsCodeQualityJob> findAllByCollectorId(ObjectId collectorId);
}
