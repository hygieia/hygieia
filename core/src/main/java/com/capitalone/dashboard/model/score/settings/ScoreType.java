package com.capitalone.dashboard.model.score.settings;

/**
 * Score Type
 */
public enum ScoreType {
  //When Type is no_score component is not considered for scoring
  no_score,
  //When Type is zero_score component component score is `0`
  zero_score,
  //When Type is value_percent component score is a defined value
  value_percent
}
