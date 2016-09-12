package com.capitalone.dashboard.repository;

import com.capitalone.dashboard.model.SubversionRepo;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface SubversionRepoRepository extends BaseCollectorItemRepository<SubversionRepo> {

    @Query(value="{ 'collectorId' : ?0, options.url : ?1}")
    SubversionRepo findSubversionRepo(ObjectId collectorId, String url);

    @Query(value="{ 'collectorId' : ?0, enabled: true}")
    List<SubversionRepo> findEnabledSubversionRepos(ObjectId collectorId);
}
