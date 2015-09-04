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

import com.capitalone.dashboard.model.CloudRawData;
//import com.capitalone.dashboard.model.CloudCpuUtilization;`
import com.capitalone.dashboard.model.Feature;

/**
 * Repository for {@link AWSCloudCollector}.
 */
public interface CloudRawDataRepository extends
		CrudRepository<CloudRawData, ObjectId>,
		QueryDslPredicateExecutor<CloudRawData> {
	// TODO: changed feature to CloudRawData
	// Age
	@Query(value = "{ $query: { $and: [ { accountName:  ?0 }, { age: { $gte: 45, $lt: 60 } } ] }}")
	List<CloudRawData> runAgeWarning(String accountName);

	@Query(value = "{ $query: { $and: [ { accountName:  ?0 }, { age: { $gte: 60 } } ] }}")
	List<CloudRawData> runAgeExpired(String accountName);

	@Query(value = "{ $query: { $and: [ { accountName:  ?0 }, { age: { $lt: 45 } } ] }}")
	List<CloudRawData> runAgeGood(String accountName);

	// CPU Utilization
	@Query(value = "{ $query: { $and: [ { accountName:  ?0 }, { cpuUtilization: { $lte: 10 } } ] }}")
	List<CloudRawData> runCpuUtilizationLow(String accountName);

	@Query(value = "{ $query: { $and: [ { accountName:  ?0 }, { cpuUtilization: { $gt: 10, $lt: 80 } } ] }}")
	List<CloudRawData> runCpuUtilizationMid(String accountName);

	@Query(value = "{ $query: { $and: [ { accountName:  ?0 }, { cpuUtilization: { $gte: 80 } } ] }}")
	List<CloudRawData> runCpuUtilizationHigh(String accountName);

	// Tagged
	@Query(value = "{ $query: { $and: [ { accountName:  ?0 }, { isTagged: false } ] }}") 
	List<CloudRawData> runNonTagged(String accountName);

	// Encrypted
	@Query(value = "{ $query: { $and: [ { accountName:  ?0 }, { isEncrypted: false } ] }}")
	List<CloudRawData> runNonEncrypted(String accountName);

	// Stopped
	@Query(value = "{ $query: { $and: [ { accountName:  ?0 }, { isStopped: true  } ] }}")
	List<CloudRawData> runStopped(String accountName);

	// All Instances in an account
	@Query(value = "{ $query: { $and: [ { accountName:  ?0 } ] }}")
	List<CloudRawData> runAllInstanceCount(String accountName);

	// Get every instance details
	@Query(value = "{ accountName : ?0 }")
	List<CloudRawData> runInstanceDetailList(String accountName);

}