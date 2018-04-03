package com.capitalone.dashboard.model.score.settings;

import com.capitalone.dashboard.model.BaseModel;
import com.capitalone.dashboard.model.score.ScoreValueType;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "score_criteria_settings")
public class ScoreCriteriaSettings extends BaseModel {

  //Max Score
  private int maxScore;

  //Build Score Settings
  private BuildScoreSettings build;

  //Quality Score Settings
  private QualityScoreSettings quality;

  //Deploy Score Settings
  private DeployScoreSettings deploy;

  //SCM Score Settings
  private ScmScoreSettings scm;

  //Alert Score Component Settings
  private ComponentAlert componentAlert;

  //Score can be calculated for types defined in ScoreValueType
  private ScoreValueType type = ScoreValueType.DASHBOARD;

  //Time when score was calculated
  private long timestamp;

  public int getMaxScore() {
    return maxScore;
  }

  public void setMaxScore(int maxScore) {
    this.maxScore = maxScore;
  }

  public BuildScoreSettings getBuild() {
    return build;
  }

  public void setBuild(BuildScoreSettings build) {
    this.build = build;
  }

  public QualityScoreSettings getQuality() {
    return quality;
  }

  public void setQuality(QualityScoreSettings quality) {
    this.quality = quality;
  }

  public DeployScoreSettings getDeploy() {
    return deploy;
  }

  public void setDeploy(DeployScoreSettings deploy) {
    this.deploy = deploy;
  }

  public ScmScoreSettings getScm() {
    return scm;
  }

  public void setScm(ScmScoreSettings scm) {
    this.scm = scm;
  }

  public ComponentAlert getComponentAlert() {
    return componentAlert;
  }

  public void setComponentAlert(ComponentAlert componentAlert) {
    this.componentAlert = componentAlert;
  }

  public ScoreValueType getType() {
    return type;
  }

  public void setType(ScoreValueType type) {
    this.type = type;
  }

  public long getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(long timestamp) {
    this.timestamp = timestamp;
  }

  @Override public String toString() {
    return "ScoreCriteriaSettings{" +
      "maxScore=" + maxScore +
      ", build=" + build +
      ", quality=" + quality +
      ", deploy=" + deploy +
      ", scm=" + scm +
      ", componentAlert=" + componentAlert +
      ", type=" + type +
      ", timestamp=" + timestamp +
      '}';
  }
}
