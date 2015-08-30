package com.capitalone.dashboard.repository

import com.capitalone.dashboard.model.StashRepo
import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.Query

trait StashRepoRepository extends BaseCollectorItemRepository[StashRepo] {
  @Query(value = "{ 'collectorId' : ?0, enabled: true}")
  def findStashRepo(collectorId: ObjectId): List[StashRepo]
}
