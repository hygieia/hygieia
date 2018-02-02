package com.capitalone.dashboard.collector;

import com.capitalone.dashboard.widget.BuildWidgetScore;

/**
 * Bean to hold settings specific to the score collector.
 */
public class BuildScoreSettings extends ScoreParamSettings {

  private BuildDurationScoreSettings duration;

  private ScoreParamSettings status;

  private int numberOfDays = BuildWidgetScore.BUILD_STATUS_NUM_OF_DAYS;

  public int getNumberOfDays() {
    return numberOfDays;
  }

  public void setNumberOfDays(int numberOfDays) {
    this.numberOfDays = numberOfDays;
  }

  public BuildDurationScoreSettings getDuration() {
    return duration;
  }

  public void setDuration(BuildDurationScoreSettings duration) {
    this.duration = duration;
  }

  public ScoreParamSettings getStatus() {
    return status;
  }

  public void setStatus(ScoreParamSettings status) {
    this.status = status;
  }

  public static class BuildDurationScoreSettings extends ScoreParamSettings {

    private long buildDurationThresholdInMillis = BuildWidgetScore.BUILD_DURATION_THRESHOLD_MILLIS;

    public long getBuildDurationThresholdInMillis() {
      return buildDurationThresholdInMillis;
    }

    public void setBuildDurationThresholdInMillis(long buildDurationThresholdInMillis) {
      this.buildDurationThresholdInMillis = buildDurationThresholdInMillis;
    }

    @Override
    public String toString() {
      return "BuildDurationScoreSettings{" +
        "buildDurationThresholdInMillis=" + buildDurationThresholdInMillis +
        '}';
    }
  }

  @Override
  public String toString() {
    return "BuildScoreSettings{" +
      "duration=" + duration +
      ", status=" + status +
      ", numberOfDays=" + numberOfDays +
      '}';
  }
}
