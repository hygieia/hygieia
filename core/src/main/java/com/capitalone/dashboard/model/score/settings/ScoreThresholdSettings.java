package com.capitalone.dashboard.model.score.settings;

/**
 * Score Threshold Settings
 */
public class ScoreThresholdSettings {

  //Compare with these operators
  public enum ComparatorType {
    equals,
    less,
    greater,
    less_or_equal,
    greater_or_equal
  }

  //The value can be of type percent for percent of days, Or days for number of days
  public enum ValueType {
    percent,
    days
  }

  private ComparatorType comparator = ComparatorType.less_or_equal;

  private ValueType type = ValueType.percent;

  //If the threshold is met set the score
  private ScoreTypeValue score;

  //Number of days to check for while applying threshold
  private Integer numDaysToCheck;

  //Value to compare
  private Double value;

  public static ScoreThresholdSettings cloneScoreThresholdSettings(ScoreThresholdSettings scoreThresholdSettings) {
    if (null == scoreThresholdSettings) {
      return scoreThresholdSettings;
    }

    ScoreThresholdSettings scoreThresholdSettingsClone = new ScoreThresholdSettings();
    scoreThresholdSettingsClone.setComparator(scoreThresholdSettings.getComparator());
    scoreThresholdSettingsClone.setType(scoreThresholdSettings.getType());
    scoreThresholdSettingsClone.setValue(scoreThresholdSettings.getValue());
    scoreThresholdSettingsClone.setNumDaysToCheck(scoreThresholdSettings.getNumDaysToCheck());
    scoreThresholdSettingsClone.setScore(
      ScoreTypeValue.cloneScoreTypeValue(scoreThresholdSettings.getScore())
      );
    return scoreThresholdSettingsClone;
  }


  public ComparatorType getComparator() {
    return comparator;
  }

  public void setComparator(ComparatorType comparator) {
    this.comparator = comparator;
  }

  public ValueType getType() {
    return type;
  }

  public void setType(ValueType type) {
    this.type = type;
  }

  public ScoreTypeValue getScore() {
    return score;
  }

  public void setScore(ScoreTypeValue score) {
    this.score = score;
  }

  public Double getValue() {
    return value;
  }

  public void setValue(Double value) {
    this.value = value;
  }

  public Integer getNumDaysToCheck() {
    return numDaysToCheck;
  }

  public void setNumDaysToCheck(Integer numDaysToCheck) {
    this.numDaysToCheck = numDaysToCheck;
  }

  @Override
  public String toString() {
    return "ScoreThresholdSettings{" +
      "comparator=" + comparator +
      ", type=" + type +
      ", score=" + score +
      ", numDaysToCheck=" + numDaysToCheck +
      ", value=" + value +
      '}';
  }
}
