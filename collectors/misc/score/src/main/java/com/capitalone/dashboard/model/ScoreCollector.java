package com.capitalone.dashboard.model;

/**
 * Collector implementation for Score.
 */
public class ScoreCollector extends Collector {

  public static ScoreCollector prototype() {
    ScoreCollector protoType = new ScoreCollector();
    protoType.setName("Score");
    protoType.setCollectorType(CollectorType.Score);
    protoType.setOnline(true);
    protoType.setEnabled(true);
    return protoType;
  }
}
