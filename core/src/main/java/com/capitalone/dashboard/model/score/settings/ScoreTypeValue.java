package com.capitalone.dashboard.model.score.settings;

/**
 * Score Value with Type Details
 */
public class ScoreTypeValue {

  private ScoreType scoreType = ScoreType.value_percent;

  private Double scoreValue;

  //Propagate the score
  private PropagateType propagate = PropagateType.no;

  public ScoreTypeValue() {}

  public static ScoreTypeValue noScore() {
    ScoreTypeValue scoreTypeValue = new ScoreTypeValue();
    scoreTypeValue.setScoreType(ScoreType.no_score);
    return scoreTypeValue;
  }

  public static ScoreTypeValue zeroScore() {
    ScoreTypeValue scoreTypeValue = new ScoreTypeValue();
    scoreTypeValue.setScoreType(ScoreType.zero_score);
    return scoreTypeValue;
  }

  public ScoreTypeValue(Double scoreValue) {
    this.scoreValue = scoreValue;
  }

  public static ScoreTypeValue cloneScoreTypeValue(ScoreTypeValue scoreTypeValue) {
    if (null == scoreTypeValue) {
      return null;
    }
    ScoreTypeValue scoreTypeValueClone = new ScoreTypeValue();
    scoreTypeValueClone.setScoreType(scoreTypeValue.getScoreType());
    scoreTypeValueClone.setScoreValue(scoreTypeValue.getScoreValue());
    scoreTypeValueClone.setPropagate(scoreTypeValue.getPropagate());
    return scoreTypeValueClone;
  }

  public ScoreType getScoreType() {
    return scoreType;
  }

  public boolean isNoScore() {
    return scoreType == ScoreType.no_score;
  }

  public void setScoreType(ScoreType scoreType) {
    this.scoreType = scoreType;
  }

  public Double getScoreValue() {
    if (null != scoreType &&
      (scoreType == ScoreType.no_score || scoreType == ScoreType.zero_score)) {
      return 0.0d;
    }
    return scoreValue;
  }

  public void setScoreValue(Double scoreValue) {
    this.scoreValue = scoreValue;
  }

  public PropagateType getPropagate() {
    return propagate;
  }

  public void setPropagate(PropagateType propagate) {
    this.propagate = propagate;
  }

  @Override
  public String toString() {
    return "ScoreTypeValue{" +
      "scoreType=" + scoreType +
      ", scoreValue=" + scoreValue +
      ", propagate=" + propagate +
      '}';
  }
}
