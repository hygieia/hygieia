package com.capitalone.dashboard.model;

import java.util.Map;

import com.capitalone.dashboard.collector.*;

public class ScoreApplication extends CollectorItem {

  //A score can have settings as
  // 1. DEFAULT : this would take default settings from collector
  // 2. CUSTOM : this would override the default settings and use settings from collector item
  public enum SettingsType {
    DEFAULT,
    CUSTOM
  }

  private static final String DASHBOARD_ID = "dashboardId";

  private static final String SETTINGS_TYPE = "settingsType";

  private static final String BUILD_WIDGET = "buildWidget";

  private static final String QUALITY_WIDGET = "qualityWidget";

  private static final String DEPLOY_WIDGET = "deployWidget";

  private static final String GITHUB_SCM_WIDGET = "githubScmWidget";

  public String getDashboardId() {
    return (String) getOptions().get(DASHBOARD_ID);
  }

  public void setDashboardId(String dashboardId) {
    getOptions().put(DASHBOARD_ID, dashboardId);
  }

  public SettingsType getSettingsType() {
    String settings = (String) getOptions().get(SETTINGS_TYPE);
    if (null != settings) {
      return SettingsType.valueOf(settings);
    }
    return null;
  }

  public void setSettingsType(SettingsType settingsType) {
    getOptions().put(SETTINGS_TYPE, settingsType);
  }

  public BuildScoreSettings getBuildWidget() {
    Map<String, Object> buildScoreSettingsMap = (Map<String, Object>) getOptions().get(BUILD_WIDGET);
    BuildScoreSettings scoreParamSettings = getScoreParamSettings(buildScoreSettingsMap, new BuildScoreSettings());
    if (null == scoreParamSettings) {
      return null;
    }

    String numberOfDays = getScoreParamSettingsValue(buildScoreSettingsMap, "numberOfDays");
    if (null != numberOfDays) {
      scoreParamSettings.setNumberOfDays(Integer.valueOf(numberOfDays));
    }

    Map<String, Object> buildScoreStatusSettingsMap = (Map<String, Object>) buildScoreSettingsMap.get("status");
    ScoreParamSettings statusParamSettings = getScoreParamSettings(buildScoreStatusSettingsMap, new ScoreParamSettings());
    if (null != statusParamSettings) {
      scoreParamSettings.setStatus(statusParamSettings);
    }

    Map<String, Object> buildDurationSettingsMap = (Map<String, Object>) buildScoreSettingsMap.get("duration");
    BuildScoreSettings.BuildDurationScoreSettings durationParamSettings = getScoreParamSettings(buildDurationSettingsMap, new BuildScoreSettings.BuildDurationScoreSettings());
    if (null != durationParamSettings) {
      String buildDurationThresholdInMillis = getScoreParamSettingsValue(buildDurationSettingsMap, "buildDurationThresholdInMillis");
      if (null != buildDurationThresholdInMillis) {
        durationParamSettings.setBuildDurationThresholdInMillis(Long.valueOf(buildDurationThresholdInMillis));
      }
      scoreParamSettings.setDuration(durationParamSettings);
    }
    return scoreParamSettings;
  }

  public void setBuildWidget(BuildScoreSettings buildScoreSettings) {
    getOptions().put(BUILD_WIDGET, buildScoreSettings);
  }

  public DeployScoreSettings getDeployWidget() {
    Map<String, Object> deployScoreSettingsMap = (Map<String, Object>) getOptions().get(DEPLOY_WIDGET);
    DeployScoreSettings scoreParamSettings = getScoreParamSettings(deployScoreSettingsMap, new DeployScoreSettings());
    if (null == scoreParamSettings) {
      return null;
    }

    Map<String, Object> deploySuccessSettingsMap = (Map<String, Object>) deployScoreSettingsMap.get("deploySuccess");
    ScoreParamSettings deploySuccessParamSettings = getScoreParamSettings(deploySuccessSettingsMap, new ScoreParamSettings());
    if (null != deploySuccessParamSettings) {
      scoreParamSettings.setDeploySuccess(deploySuccessParamSettings);
    }

    Map<String, Object> intancesOnlineSettingsMap = (Map<String, Object>) deployScoreSettingsMap.get("intancesOnline");
    ScoreParamSettings intancesOnlineParamSettings = getScoreParamSettings(intancesOnlineSettingsMap, new ScoreParamSettings());
    if (null != intancesOnlineParamSettings) {
      scoreParamSettings.setIntancesOnline(intancesOnlineParamSettings);
    }

    return scoreParamSettings;
  }

  public void setDeployWidget(DeployScoreSettings deployScoreSettings) {
    getOptions().put(DEPLOY_WIDGET, deployScoreSettings);
  }

  public QualityScoreSettings getQualityWidget() {
    Map<String, Object> qualityScoreSettingsMap = (Map<String, Object>) getOptions().get(QUALITY_WIDGET);
    QualityScoreSettings scoreParamSettings = getScoreParamSettings(qualityScoreSettingsMap, new QualityScoreSettings());
    if (null == scoreParamSettings) {
      return null;
    }

    Map<String, Object> ccSettingsMap = (Map<String, Object>) qualityScoreSettingsMap.get("codeCoverage");
    ScoreParamSettings ccParamSettings = getScoreParamSettings(ccSettingsMap, new ScoreParamSettings());
    if (null != ccParamSettings) {
      scoreParamSettings.setCodeCoverage(ccParamSettings);
    }

    Map<String, Object> utSettingsMap = (Map<String, Object>) qualityScoreSettingsMap.get("unitTests");
    ScoreParamSettings utParamSettings = getScoreParamSettings(utSettingsMap, new ScoreParamSettings());
    if (null != utParamSettings) {
      scoreParamSettings.setUnitTests(utParamSettings);
    }

    return scoreParamSettings;
  }

  public GithubScmScoreSettings getGithubScmWidget() {
    Map<String, Object> githubScmScoreSettingsMap = (Map<String, Object>) getOptions().get(GITHUB_SCM_WIDGET);
    GithubScmScoreSettings scoreParamSettings = getScoreParamSettings(githubScmScoreSettingsMap, new GithubScmScoreSettings());
    if (null == scoreParamSettings) {
      return null;
    }

    Map<String, Object> commitsPerDaySettingsMap = (Map<String, Object>) githubScmScoreSettingsMap.get("commitsPerDay");
    ScoreParamSettings commitsPerDayParamSettings = getScoreParamSettings(commitsPerDaySettingsMap, new ScoreParamSettings());
    if (null != commitsPerDayParamSettings) {
      scoreParamSettings.setCommitsPerDay(commitsPerDayParamSettings);
    }

    return scoreParamSettings;
  }

  public void setGithubScmWidget(GithubScmScoreSettings githubScmScoreSettings) {
    getOptions().put(GITHUB_SCM_WIDGET, githubScmScoreSettings);
  }

  public void setQualityWidget(QualityScoreSettings qualityScoreSettings) {
    getOptions().put(QUALITY_WIDGET, qualityScoreSettings);
  }

  private <T extends ScoreParamSettings> T getScoreParamSettings(Map<String, Object> settingsMap, T scoreParamSettings) {
    if (null == settingsMap) {
      return null;
    }

    Object weight = settingsMap.get("weight");
    if (null != weight) {
      scoreParamSettings.setWeight((Integer) weight);
    }

    Object disabled = settingsMap.get("disabled");
    if (null != disabled) {
      scoreParamSettings.setDisabled((Boolean) disabled);
    }

    return scoreParamSettings;
  }

  private String getScoreParamSettingsValue(Map<String, Object> settingsMap, String name) {
    Object value = settingsMap.get(name);
    if (null != value) {
      return String.valueOf(value);
    }
    return null;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    ScoreApplication that = (ScoreApplication) o;
    return getDashboardId().equals(that.getDashboardId());
  }

  @Override
  public int hashCode() {
    int result = getDashboardId().hashCode();
    return result;
  }


}
