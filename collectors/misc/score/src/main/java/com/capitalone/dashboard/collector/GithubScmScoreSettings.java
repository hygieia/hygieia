package com.capitalone.dashboard.collector;

import com.capitalone.dashboard.widget.GithubScmWidgetScore;

/**
 * Bean to hold settings specific to the score collector.
 */
public class GithubScmScoreSettings extends ScoreParamSettings {

  private ScoreParamSettings commitsPerDay;

  private int numberOfDays = GithubScmWidgetScore.GITHUB_SCM_NUM_OF_DAYS;

  public int getNumberOfDays() {
    return numberOfDays;
  }

  public void setNumberOfDays(int numberOfDays) {
    this.numberOfDays = numberOfDays;
  }

  public ScoreParamSettings getCommitsPerDay() {
    return commitsPerDay;
  }

  public void setCommitsPerDay(ScoreParamSettings commitsPerDay) {
    this.commitsPerDay = commitsPerDay;
  }

  @Override public String toString() {
    return "GithubScmScoreSettings{" +
      "commitsPerDay=" + commitsPerDay +
      ", numberOfDays=" + numberOfDays +
      '}';
  }
}
