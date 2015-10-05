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

import com.capitalone.dashboard.model.CloudComputeRawData;
//import com.capitalone.dashboard.model.CloudCpuUtilization;`
import com.capitalone.dashboard.model.Feature;

/**
 * Repository for {@link AWSCloudCollector}.
 */
public interface CloudRawDataRepository extends
		CrudRepository<CloudComputeRawData, ObjectId>,
		QueryDslPredicateExecutor<CloudComputeRawData> {

	// Age
	@Query(value = "{ $query: { $and: [ { collectorItemId:  ?0 }, { age: { $gte: 45, $lt: 60 } } ] }}")
	List<CloudComputeRawData> runAgeWarning(ObjectId collectorItemId);

	@Query(value = "{ $query: { $and: [ { collectorItemId:  ?0 }, { age: { $gte: 60 } } ] }}")
	List<CloudComputeRawData> runAgeExpired(ObjectId collectorItemId);

	@Query(value = "{ $query: { $and: [ { collectorItemId:  ?0 }, { age: { $lt: 45 } } ] }}")
	List<CloudComputeRawData> runAgeGood(ObjectId collectorItemId);

	// CPU Utilization
	@Query(value = "{ $query: { $and: [ { collectorItemId:  ?0 }, { cpuUtilization: { $lte: 10 } } ] }}")
	List<CloudComputeRawData> runCpuUtilizationLow(ObjectId collectorItemId);

	@Query(value = "{ $query: { $and: [ { collectorItemId:  ?0 }, { cpuUtilization: { $gt: 10, $lt: 80 } } ] }}")
	List<CloudComputeRawData> runCpuUtilizationMid(ObjectId collectorItemId);

	@Query(value = "{ $query: { $and: [ { collectorItemId:  ?0 }, { cpuUtilization: { $gte: 80 } } ] }}")
	List<CloudComputeRawData> runCpuUtilizationHigh(ObjectId collectorItemId);

	// Tagged
	@Query(value = "{ $query: { $and: [ { collectorItemId:  ?0 }, { isTagged: false } ] }}") 
	List<CloudComputeRawData> runNonTagged(ObjectId collectorItemId);

	// Encrypted
	@Query(value = "{ $query: { $and: [ { collectorItemId:  ?0 }, { isEncrypted: false } ] }}")
	List<CloudComputeRawData> runNonEncrypted(ObjectId collectorItemId);

	// Stopped
	@Query(value = "{ $query: { $and: [ { collectorItemId:  ?0 }, { isStopped: true  } ] }}")
	List<CloudComputeRawData> runStopped(ObjectId collectorItemId);

	// All Instances in an account
	@Query(value = "{ $query: { $and: [ { collectorItemId:  ?0 } ] }}")
	List<CloudComputeRawData> runAllInstanceCount(ObjectId collectorItemId);

	// Get every instance details
	@Query(value = "{ collectorItemId : ?0 }")
	List<CloudComputeRawData> runInstanceDetailList(ObjectId collectorItemId);

}