package com.capitalone.dashboard.repository;

import com.capitalone.dashboard.model.ChatOpsRepo;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface ChatOpsRepository extends BaseCollectorItemRepository<ChatOpsRepo> {

    @Query(value="{ 'collectorId' : ?0, options.repoUrl : ?1, options.branch : ?2}")
    ChatOpsRepo findChatOpsRepo(ObjectId collectorId, String url, String branch);
//
    @Query(value="{ 'collectorId' : ?0, enabled: true}")
    List<ChatOpsRepo> findEnabledChatOpsRepos(ObjectId collectorId);
}
