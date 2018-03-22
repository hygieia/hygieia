package com.capitalone.dashboard.model.score.settings;

/**
 * Bean to hold score settings specific to build
 */
public class BuildScoreSettings extends ScoreComponentSettings {

  public static final long BUILD_DURATION_THRESHOLD_MILLIS = 300000;

  public static final int BUILD_STATUS_NUM_OF_DAYS = 14;

  //Settings for Build Threshold Duration
  private BuildDurationScoreSettings duration;

  //Settings for Build Status
  private ScoreComponentSettings status;

  //Number Of Days to calculate build score
  private int numberOfDays = BUILD_STATUS_NUM_OF_DAYS;

  public static BuildScoreSettings cloneBuildScoreSettings(BuildScoreSettings buildScoreSettings) {
    if (null == buildScoreSettings) {
      return null;
    }
    BuildScoreSettings buildScoreSettingsClone = new BuildScoreSettings();
    ScoreComponentSettings.copyScoreComponentSettings(buildScoreSettings, buildScoreSettingsClone);
    buildScoreSettingsClone.setNumberOfDays(buildScoreSettings.getNumberOfDays());
    buildScoreSettingsClone.setStatus(
      ScoreComponentSettings.cloneScoreComponentSettings(buildScoreSettings.getStatus())
    );

    buildScoreSettingsClone.setDuration(
      BuildDurationScoreSettings.cloneBuildDurationScoreSettings(buildScoreSettings.getDuration())
    );

    return buildScoreSettingsClone;
  }


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

  public ScoreComponentSettings getStatus() {
    return status;
  }

  public void setStatus(ScoreComponentSettings status) {
    this.status = status;
  }

  public static class BuildDurationScoreSettings extends ScoreComponentSettings {

    private long buildDurationThresholdInMillis = BUILD_DURATION_THRESHOLD_MILLIS;

    public static BuildDurationScoreSettings cloneBuildDurationScoreSettings(BuildDurationScoreSettings buildDurationScoreSettings) {
      if (null == buildDurationScoreSettings) {
        return null;
      }

      BuildDurationScoreSettings buildDurationScoreSettingsClone = new BuildDurationScoreSettings();
      ScoreComponentSettings.copyScoreComponentSettings(buildDurationScoreSettings, buildDurationScoreSettingsClone);
      buildDurationScoreSettingsClone.setBuildDurationThresholdInMillis(
        buildDurationScoreSettings.getBuildDurationThresholdInMillis()
      );
      return buildDurationScoreSettingsClone;
    }


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
        ", disabled=" + isDisabled() +
        ", weight=" + getWeight() +
        ", criteria=" + getCriteria() +
        '}';
    }
  }

  @Override
  public String toString() {
    return "BuildScoreSettings{" +
      "duration=" + duration +
      ", status=" + status +
      ", numberOfDays=" + numberOfDays +
      ", disabled=" + isDisabled() +
      ", weight=" + getWeight() +
      ", criteria=" + getCriteria() +
      '}';
  }
}
