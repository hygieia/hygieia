package com.capitalone.dashboard.model.score;

/*
  Score Type for calculating scores
 */
public enum ScoreValueType {
  DASHBOARD;

  public static ScoreValueType fromString(String value) {
    for (ScoreValueType scoreValueType : values()) {
      if (scoreValueType.toString().equalsIgnoreCase(value)) {
        return scoreValueType;
      }
    }
    throw new IllegalArgumentException(value + " is not a valid ScoreValueType.");
  }
}
