package com.capitalone.dashboard.model.score.settings;

/**
 * Bean to hold score settings specific to scm
 */
public class ScmScoreSettings extends ScoreComponentSettings {

  public static final int SCM_NUM_OF_DAYS = 14;

  private ScoreComponentSettings commitsPerDay;

  private int numberOfDays = SCM_NUM_OF_DAYS;

  public static ScmScoreSettings cloneScmScoreSettings(ScmScoreSettings scmScoreSettings) {
    if (null == scmScoreSettings) {
      return null;
    }

    ScmScoreSettings scmScoreSettingsClone = new ScmScoreSettings();
    ScoreComponentSettings.copyScoreComponentSettings(scmScoreSettings, scmScoreSettingsClone);
    scmScoreSettingsClone.setNumberOfDays(
      scmScoreSettings.getNumberOfDays()
    );
    scmScoreSettingsClone.setCommitsPerDay(
      ScoreComponentSettings.cloneScoreComponentSettings(scmScoreSettings.getCommitsPerDay())
    );

    return scmScoreSettingsClone;
  }

  public int getNumberOfDays() {
    return numberOfDays;
  }

  public void setNumberOfDays(int numberOfDays) {
    this.numberOfDays = numberOfDays;
  }

  public ScoreComponentSettings getCommitsPerDay() {
    return commitsPerDay;
  }

  public void setCommitsPerDay(ScoreComponentSettings commitsPerDay) {
    this.commitsPerDay = commitsPerDay;
  }

  @Override public String toString() {
    return "ScmScoreSettings{" +
      "commitsPerDay=" + commitsPerDay +
      ", numberOfDays=" + numberOfDays +
      ", disabled=" + isDisabled() +
      ", weight=" + getWeight() +
      ", criteria=" + getCriteria() +
      '}';
  }
}
