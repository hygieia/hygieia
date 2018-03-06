package com.capitalone.dashboard.repository;

import com.capitalone.dashboard.model.score.ScoreMetric;
import com.capitalone.dashboard.model.score.ScoreValueType;
import com.capitalone.dashboard.model.score.settings.ScoreCriteriaSettings;
import org.bson.types.ObjectId;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;

/**
 * Repository for {@link ScoreCriteriaSettings} data.
 */
public interface ScoreCriteriaSettingsRepository extends CrudRepository<ScoreCriteriaSettings, ObjectId>, QueryDslPredicateExecutor<ScoreMetric> {

    /**
     * Finds {@link ScoreCriteriaSettings}s for a given type {@link com.capitalone.dashboard.model.score.ScoreValueType}.
     *
     * @param type Score Value Type
     * @return a {@link ScoreCriteriaSettings}
     */
    ScoreCriteriaSettings findByType(ScoreValueType type);

}
