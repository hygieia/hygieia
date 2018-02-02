package com.capitalone.dashboard.collector;


/**
 * Bean to hold settings specific to the score collector.
 */
public class DeployScoreSettings extends ScoreParamSettings {

  private ScoreParamSettings deploySuccess;

  private ScoreParamSettings intancesOnline;

  public ScoreParamSettings getDeploySuccess() {
    return deploySuccess;
  }

  public void setDeploySuccess(ScoreParamSettings deploySuccess) {
    this.deploySuccess = deploySuccess;
  }

  public ScoreParamSettings getIntancesOnline() {
    return intancesOnline;
  }

  public void setIntancesOnline(ScoreParamSettings intancesOnline) {
    this.intancesOnline = intancesOnline;
  }

  @Override public String toString() {
    return "DeployScoreSettings{" +
      "deploySuccess=" + deploySuccess +
      ", intancesOnline=" + intancesOnline +
      '}';
  }
}
