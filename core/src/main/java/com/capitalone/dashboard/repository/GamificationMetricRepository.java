package com.capitalone.dashboard.repository;

import com.capitalone.dashboard.model.GamificationMetric;
import org.bson.types.ObjectId;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;

import java.util.Collection;

public interface GamificationMetricRepository extends CrudRepository<GamificationMetric, ObjectId>, QueryDslPredicateExecutor<GamificationMetric> {

    Collection<GamificationMetric> findAll();

    GamificationMetric findByMetricName(String metricName);

    Collection<GamificationMetric> findAllByEnabled(Boolean enabled);

}
