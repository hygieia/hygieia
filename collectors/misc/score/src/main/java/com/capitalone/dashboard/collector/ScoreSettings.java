package com.capitalone.dashboard.collector;

import com.capitalone.dashboard.model.score.settings.*;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import com.capitalone.dashboard.Constants;

/**
 * Bean to hold settings specific to the score collector.
 */
@Component
@ConfigurationProperties(prefix = "score")
public class ScoreSettings {
  private String cron;

  private BuildScoreSettings buildWidget;

  private QualityScoreSettings qualityWidget;

  private DeployScoreSettings deployWidget;

  private ScmScoreSettings scmWidget;

  private ScoreCriteria criteria;

  private ComponentAlert componentAlert;

  private int maxScore = Constants.MAX_SCORE;

  public String getCron() {
    return cron;
  }

  public void setCron(String cron) {
    this.cron = cron;
  }

  public BuildScoreSettings getBuildWidget() {
    return buildWidget;
  }

  public void setBuildWidget(BuildScoreSettings buildWidget) {
    this.buildWidget = buildWidget;
  }

  public QualityScoreSettings getQualityWidget() {
    return qualityWidget;
  }

  public void setQualityWidget(QualityScoreSettings qualityWidget) {
    this.qualityWidget = qualityWidget;
  }

  public DeployScoreSettings getDeployWidget() {
    return deployWidget;
  }

  public void setDeployWidget(DeployScoreSettings deployWidget) {
    this.deployWidget = deployWidget;
  }

  public ScmScoreSettings getScmWidget() {
    return scmWidget;
  }

  public void setScmWidget(ScmScoreSettings scmWidget) {
    this.scmWidget = scmWidget;
  }

  public ScoreCriteria getCriteria() {
    return criteria;
  }

  public void setCriteria(ScoreCriteria criteria) {
    this.criteria = criteria;
  }

  public int getMaxScore() {
    return maxScore;
  }

  public void setMaxScore(int maxScore) {
    this.maxScore = maxScore;
  }

  public ComponentAlert getComponentAlert() {
    return componentAlert;
  }

  public void setComponentAlert(ComponentAlert componentAlert) {
    this.componentAlert = componentAlert;
  }

  @Override public String toString() {
    return "ScoreSettings{" +
      "cron='" + cron + '\'' +
      ", buildWidget=" + buildWidget +
      ", qualityWidget=" + qualityWidget +
      ", deployWidget=" + deployWidget +
      ", scmWidget=" + scmWidget +
      ", criteria=" + criteria +
      ", maxScore=" + maxScore +
      ", componentAlert=" + componentAlert +
      '}';
  }
}
