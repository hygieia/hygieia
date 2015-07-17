/*************************DA-BOARD-LICENSE-START*********************************
 * Copyright 2014 CapitalOne, LLC.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *************************DA-BOARD-LICENSE-END*********************************/

package com.capitalone.dashboard.repository;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.Query;

import com.capitalone.dashboard.model.TeamCollectorItem;

/**
 * CollectorItem repository for {@link TeamCollectorItem}.
 */
public interface TeamRepository extends
		BaseCollectorItemRepository<TeamCollectorItem> {
	@Query(value = "{ 'collectorId' : ?0, options.teamId : ?1, options.name : ?2}")
	TeamCollectorItem findTeamCollector(ObjectId collectorId, String teamId,
			String name);

	@Query(value = "{ 'collectorId' : ?0, options.teamId : ?1, enabled: true}")
	List<TeamCollectorItem> findEnabledTeamCollectors(ObjectId collectorId,
			String teamId);

	@Query(value = "{ $query: { 'collectorId' : ?0, 'options.changeDate' : {$gt: ?1}, '_class' : 'com.capitalone.dashboard.model.TeamCollectorItem'}, $orderby: { 'options.changeDate' :-1 }}", fields = "{'options.changeDate' : 1, '_id' : 0}")
	List<TeamCollectorItem> getTeamMaxChangeDate(ObjectId collectorId,
			String lastChangeDate);

	@Query(value = "{ $query: {'options.teamId' : ?0},{'options.teamId' : 1}}")
	List<TeamCollectorItem> getTeamIdById(String teamId);
}
