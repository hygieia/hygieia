package com.capitalone.dashboard.collector;

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

  private GithubScmScoreSettings githubScmWidget;

  private ScoreCriteria criteria;

  private WidgetAlert widgetAlert;

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

  public GithubScmScoreSettings getGithubScmWidget() {
    return githubScmWidget;
  }

  public void setGithubScmWidget(GithubScmScoreSettings githubScmWidget) {
    this.githubScmWidget = githubScmWidget;
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

  public WidgetAlert getWidgetAlert() {
    return widgetAlert;
  }

  public void setWidgetAlert(WidgetAlert widgetAlert) {
    this.widgetAlert = widgetAlert;
  }

  @Override public String toString() {
    return "ScoreSettings{" +
      "cron='" + cron + '\'' +
      ", buildWidget=" + buildWidget +
      ", qualityWidget=" + qualityWidget +
      ", deployWidget=" + deployWidget +
      ", githubScmWidget=" + githubScmWidget +
      ", criteria=" + criteria +
      ", maxScore=" + maxScore +
      ", widgetAlert=" + widgetAlert +
      '}';
  }
}
