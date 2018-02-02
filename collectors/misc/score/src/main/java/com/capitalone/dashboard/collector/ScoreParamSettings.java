package com.capitalone.dashboard.collector;

/**
 * Bean to hold settings specific to the score collector.
 */
public class ScoreParamSettings {

  public static final String CALCULATE_SCORE_FUNC = "calculateScore";

  private boolean disabled = false;

  private int weight = 0;

  //Score calculation by a javascript file which should accept i/p data and output the score
  private String funcFilePath;

  //Default function name to be called in javascript file
  private String funcName = CALCULATE_SCORE_FUNC;

  private ScoreCriteria criteria;

  public boolean isDisabled() {
    return disabled;
  }

  public void setDisabled(boolean disabled) {
    this.disabled = disabled;
  }

  public int getWeight() {
    return weight;
  }

  public void setWeight(int weight) {
    this.weight = weight;
  }

  public String getFuncFilePath() {
    return funcFilePath;
  }

  public void setFuncFilePath(String funcFilePath) {
    this.funcFilePath = funcFilePath;
  }

  public String getFuncName() {
    return funcName;
  }

  public void setFuncName(String funcName) {
    this.funcName = funcName;
  }

  public ScoreCriteria getCriteria() {
    return criteria;
  }

  public void setCriteria(ScoreCriteria criteria) {
    this.criteria = criteria;
  }

  @Override
  public String toString() {
    return "ScoreParamSettings{" +
      "disabled=" + disabled +
      ", weight=" + weight +
      ", funcFilePath='" + funcFilePath + '\'' +
      ", funcName='" + funcName + '\'' +
      ", criteria=" + criteria +
      '}';
  }
}
