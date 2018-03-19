package com.capitalone.dashboard.service;

import com.capitalone.dashboard.model.score.ScoreValueType;
import com.capitalone.dashboard.model.score.settings.ScoreCriteriaSettings;

public interface ScoreCriteriaSettingsService {

  /**
   * Fetch ScoreCriteriaSettings by ScoreValueType
   *
   * @param type ScoreValueType
   * @return ScoreCriteriaSettings
   */
  ScoreCriteriaSettings getScoreCriteriaSettingsByType(ScoreValueType type);

}
