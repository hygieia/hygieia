package com.capitalone.dashboard.repository;

import java.util.List;

import org.bson.types.ObjectId;

import com.capitalone.dashboard.model.Feature;

/**
 * Repository for {@link FeatureCollector} with custom methods.
 */
public interface FeatureRepositoryCustom {
    
    /**
     * Find all features with active sprints that are between the provided date and that have an end date < 9999-12-31 EOD
     * 
     * @param sTeamId
     * @param sProjectId
     * @param currentISODateTime
     * @param minimal
     * @return
     */
    List<Feature> findByActiveEndingSprints(String sTeamId, String sProjectId, ObjectId collectorId, String currentISODateTime, boolean minimal);

    /**
     * Find all features that have sprints set but without an end date (or an end date >= 9999-12-31 EOD)
     * 
     * @param sTeamId
     * @param sProjectId
     * @param minimal
     * @return
     */
    List<Feature> findByUnendingSprints(String sTeamId, String sProjectId, ObjectId collectorId, boolean minimal);
    
    /**
     * Find all features without sprints set
     * 
     * @param sTeamId
     * @param sProjectId
     * @param minimal
     * @return
     */
    List<Feature> findByNullSprints(String sTeamId, String sProjectId, ObjectId collectorId, boolean minimal);
    
}
