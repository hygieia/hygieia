package com.capitalone.dashboard.service;

import com.capitalone.dashboard.model.score.ScoreValueType;
import com.capitalone.dashboard.model.score.settings.ScoreCriteriaSettings;
import com.capitalone.dashboard.repository.ScoreCriteriaSettingsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ScoreCriteriaSettingsServiceImpl implements ScoreCriteriaSettingsService{

  private final ScoreCriteriaSettingsRepository scoreCriteriaSettingsRepository;

  @Autowired
  public ScoreCriteriaSettingsServiceImpl(
    ScoreCriteriaSettingsRepository scoreCriteriaSettingsRepository
  ) {
    this.scoreCriteriaSettingsRepository = scoreCriteriaSettingsRepository;
  }

  @Override
  public ScoreCriteriaSettings getScoreCriteriaSettingsByType(ScoreValueType type) {
    return this.scoreCriteriaSettingsRepository.findByType(type);
  }
}
