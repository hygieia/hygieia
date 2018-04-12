package com.capitalone.dashboard.model.score.settings;

/**
 * Bean to hold score settings specific to deploy
 */
public class DeployScoreSettings extends ScoreComponentSettings {

  //Settings for Deploy Success Instances
  private ScoreComponentSettings deploySuccess;

  //Settings for Instances Online
  private ScoreComponentSettings intancesOnline;

  public static DeployScoreSettings cloneDeployScoreSettings(DeployScoreSettings deployScoreSettings) {
    if (null == deployScoreSettings) {
      return null;
    }
    DeployScoreSettings deployScoreSettingsClone = new DeployScoreSettings();
    ScoreComponentSettings.copyScoreComponentSettings(deployScoreSettings, deployScoreSettingsClone);
    deployScoreSettingsClone.setDeploySuccess(
      ScoreComponentSettings.cloneScoreComponentSettings(deployScoreSettings.getDeploySuccess())
    );
    deployScoreSettingsClone.setIntancesOnline(
      ScoreComponentSettings.cloneScoreComponentSettings(deployScoreSettings.getIntancesOnline())
    );
    return deployScoreSettingsClone;
  }

  public ScoreComponentSettings getDeploySuccess() {
    return deploySuccess;
  }

  public void setDeploySuccess(ScoreComponentSettings deploySuccess) {
    this.deploySuccess = deploySuccess;
  }

  public ScoreComponentSettings getIntancesOnline() {
    return intancesOnline;
  }

  public void setIntancesOnline(ScoreComponentSettings intancesOnline) {
    this.intancesOnline = intancesOnline;
  }

  @Override public String toString() {
    return "DeployScoreSettings{" +
      "deploySuccess=" + deploySuccess +
      ", intancesOnline=" + intancesOnline +
      ", disabled=" + isDisabled() +
      ", weight=" + getWeight() +
      ", criteria=" + getCriteria() +
      '}';
  }
}
