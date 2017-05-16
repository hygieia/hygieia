package com.capitalone.dashboard.repository;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;

import com.capitalone.dashboard.model.WebLogicMonitor;

@Component
public interface WLMonitorRepository extends CrudRepository<WebLogicMonitor, ObjectId>, QueryDslPredicateExecutor<WebLogicMonitor>{

    List<WebLogicMonitor> findAll();
    @Query(value="{ 'environment' : ?0, 'name': ?1, 'state': ?2, 'health': ?3}")
    WebLogicMonitor findVmonitorApplicationExist(String envName, String name, String state, String health);
    @Query(value="{ 'environment' : ?0}")
    List<WebLogicMonitor> findAllByEnvironementName(String envName);
}