package com.capitalone.dashboard.model.score.settings;

/**
 * Score Settings for a Component
 */
public class ScoreComponentSettings {

  //If component is disabled, this can be set true
  private boolean disabled = false;

  //Weight for component in score (0-100)
  private int weight = 0;

  //Score Criterias for component
  private ScoreCriteria criteria;

  public static ScoreComponentSettings cloneScoreComponentSettings(ScoreComponentSettings scoreComponentSettings) {
    if (null == scoreComponentSettings) {
      return null;
    }
    ScoreComponentSettings scoreComponentSettingsClone = new ScoreComponentSettings();
    copyScoreComponentSettings(scoreComponentSettings, scoreComponentSettingsClone);
    return scoreComponentSettingsClone;
  }

  public static void copyScoreComponentSettings(ScoreComponentSettings from, ScoreComponentSettings to) {
    if (null == from || null == to) {
      return;
    }
    to.setDisabled(from.isDisabled());
    to.setWeight(from.getWeight());
    ScoreCriteria criteria = from.getCriteria();
    if (null == criteria) {
      return;
    }
    to.setCriteria(
      ScoreCriteria.cloneScoreCriteria(criteria)
    );
  }

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

  public ScoreCriteria getCriteria() {
    return criteria;
  }

  public void setCriteria(ScoreCriteria criteria) {
    this.criteria = criteria;
  }

  @Override
  public String toString() {
    return "ScoreComponentSettings{" +
      "disabled=" + disabled +
      ", weight=" + weight +
      ", criteria=" + criteria +
      '}';
  }
}
