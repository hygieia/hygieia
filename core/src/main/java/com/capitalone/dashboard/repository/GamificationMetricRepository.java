package com.capitalone.dashboard.repository;

import com.capitalone.dashboard.model.GamificationMetric;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface GamificationMetricRepository extends CrudRepository<GamificationMetric, ObjectId>, QueryDslPredicateExecutor<GamificationMetric> {

    List<GamificationMetric> findAll();

    @Query(value = " {'metricName' : ?0 }")
    GamificationMetric findMetricByName(String metricName);
}
