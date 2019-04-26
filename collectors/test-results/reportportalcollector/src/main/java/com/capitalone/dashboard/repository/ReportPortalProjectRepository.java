package com.capitalone.dashboard.repository;

import com.capitalone.dashboard.model.ReportPortalProject;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface ReportPortalProjectRepository extends BaseCollectorItemRepository<ReportPortalProject> {

    @Query(value="{ 'collectorId' : ?0, options.instanceUrl : ?1, options.projectId : ?2}")
    ReportPortalProject findReportProject(ObjectId collectorId, String instanceUrl, String projectId);

    @Query(value="{ 'collectorId' : ?0, options.instanceUrl : ?1, enabled: true}")
    List<ReportPortalProject> findEnabledProjects(ObjectId collectorId, String instanceUrl);
}
