package com.capitalone.dashboard.repository;

import com.capitalone.dashboard.model.FeatureBoard;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface FeatureBoardRepository  extends BaseCollectorItemRepository<FeatureBoard> {

    @Query(value="{ 'collectorId' : ?0, enabled: true}")
    List<FeatureBoard> findEnabledFeatureBoards(ObjectId collectorId);

    @Query(value="{ 'collectorId' : ?0, 'options.teamId' : ?1, enabled: true}")
    FeatureBoard findFeatureBoard(ObjectId collectorId, String teamId);
}
