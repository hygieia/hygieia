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
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;

import com.capitalone.dashboard.model.Feature;

/**
 * Repository for {@link FeatureCollector}.
 */
public interface FeatureRepository extends CrudRepository<Feature, ObjectId>,
		QueryDslPredicateExecutor<Feature> {
	@Query(value = "{ $query: { 'collectorId' : ?0, 'changeDate' : {$gt: ?1}}, $orderby: { 'changeDate' :-1 }}", fields = "{'changeDate' : 1, '_id' : 0}")
	List<Feature> getFeatureMaxChangeDate(ObjectId collectorId,
			String lastChangeDate);

	@Query(value = "{ $query: {'sId' : ?0},{'sId' : 1}}")
	List<Feature> getFeatureIdById(String sId);

	@Query(value = "{ $query: {'sTeamID' : ?0 , 'isDeleted' : 'False', $and : [{'sSprintID' : {$ne : null}} , {'sSprintBeginDate' : {$lte : ?1}} , {'sSprintEndDate' : {$gte : ?1}}]}, $orderby: { 'sStatus' :-1 }}")
	List<Feature> getSprintStoriesByTeamId(String sTeamID,
			String currentISODateTime);

	@Query(value = "{ $query: {'sTeamID' : ?0 , 'isDeleted' : 'False', $and : [{'sSprintID' : {$ne : null}} , {'sSprintBeginDate' : {$lte : ?1}} , {'sSprintEndDate' : {$gte : ?1}}]}, $orderby: { 'sStatus' :-1 }}", fields = "{'sSprintID' : 1, 'sSprintName' : 1,'sSprintBeginDate' : 1, 'sSprintEndDate' : 1}")
	List<Feature> getCurrentSprintDetail(String sTeamID,
			String currentISODateTime);

	@Query(value = "{ $query: {'sTeamID' : ?0 , 'isDeleted' : 'False', $and : [{'sSprintID' : {$ne : null}} , {'sSprintBeginDate' : {$lte : ?1}} , {'sSprintEndDate' : {$gte : ?1}}]}, $orderby: { 'sEpicID' :-1 }}", fields = "{'sEpicID' : 1,'sEpicNumber' : 1,'sEpicName' : 1,'sEstimate' : 1}")
	List<Feature> getInProgressFeaturesEstimatesByTeamId(String sTeamID,
			String currentISODateTime);

	@Query(value = "{ $query: {'sTeamID' : ?0 , 'isDeleted' : 'False', $and : [{'sSprintID' : {$ne : null}} , {'sSprintBeginDate' : {$lte : ?1}} , {'sSprintEndDate' : {$gte : ?1}}]}, $orderby: { 'sStatus' :-1 }}", fields = "{'sStatus': 1,'sEstimate' : 1}")
	List<Feature> getSprintBacklogTotal(String sTeamID,
			String currentISODateTime);

	@Query(value = "{ $query: {'sTeamID' : ?0 , $and : [{'isDeleted' : 'False'} , {'sState' : 'Active'}] , $or : [{'sStatus' : 'In Progress'} , {'sStatus' : 'Waiting'} , {'sStatus' : 'Impeded'}] , $and : [{'sSprintID' : {$ne : null}} , {'sSprintBeginDate' : {$lte : ?1}} , {'sSprintEndDate' : {$gte : ?1}}]}, $orderby: { 'sStatus' :-1 }}", fields = "{'sStatus': 1,'sEstimate' : 1}")
	List<Feature> getSprintBacklogInProgress(String sTeamID,
			String currentISODateTime);

	@Query(value = "{ $query: {'sTeamID' : ?0 , 'isDeleted' : 'False' , $or : [{'sStatus' : 'Done'} , {'sStatus' : 'Accepted'}] , $and : [{'sSprintID' : {$ne : null}} , {'sSprintBeginDate' : {$lte : ?1}} , {'sSprintEndDate' : {$gte : ?1}}]}, $orderby: { 'sStatus' :-1 }}", fields = "{'sStatus': 1,'sEstimate' : 1}")
	List<Feature> getSprintBacklogDone(String sTeamID, String currentISODateTime);

	@Query(value = "{ $query: {'sNumber' : ?0 }}")
	List<Feature> getStoryByNumber(String sNumber);
}