package com.capitalone.dashboard.model.score;

import com.capitalone.dashboard.model.BaseModel;
import com.google.common.collect.Maps;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Collection;
import java.util.Map;

/*
  Collection for scores based on type dashboard/team
 */
@Document(collection = "score_metric")
public class ScoreMetric extends BaseModel {
  //Collector Item Id for score
  private ObjectId collectorItemId;

  //Id for score type as Dashboard/Team
  //If type is DASHBOARD, value should be id of dashboard
  private ObjectId scoreTypeId;

  //Score can be calculated for types defined in ScoreValueType
  private ScoreValueType type = ScoreValueType.DASHBOARD;

  //Time when score was calculated
  private long timestamp;

  //Score value
  private String score;

  //Score total
  private String total;

  //Failure message
  private String failureMssg;

  //If the dashboard has no score, the flag is set as true
  private boolean noScore;

  //Options to save additional properties
  public Map<String, String> options = Maps.newHashMap();

  //Collection of scores for individual components (widgets for dashboard score)
  private Collection<ScoreComponentMetric> componentMetrics;

  public Map<String, String> getOptions() {
    return options;
  }

  public ObjectId getCollectorItemId() {
    return collectorItemId;
  }

  public void setCollectorItemId(ObjectId collectorItemId) {
    this.collectorItemId = collectorItemId;
  }

  public ScoreValueType getType() {
    return type;
  }

  public void setType(ScoreValueType type) {
    this.type = type;
  }

  public ObjectId getScoreTypeId() {
    return scoreTypeId;
  }

  public void setScoreTypeId(ObjectId scoreTypeId) {
    this.scoreTypeId = scoreTypeId;
  }

  public long getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(long timestamp) {
    this.timestamp = timestamp;
  }

  public Collection<ScoreComponentMetric> getComponentMetrics() {
    return componentMetrics;
  }

  public void setComponentMetrics(Collection<ScoreComponentMetric> componentMetrics) {
    this.componentMetrics = componentMetrics;
  }

  public String getScore() {
    return score;
  }

  public void setScore(String score) {
    this.score = score;
  }

  public boolean isNoScore() {
    return noScore;
  }

  public void setNoScore(boolean noScore) {
    this.noScore = noScore;
  }

  public String getTotal() {
    return total;
  }

  public void setTotal(String total) {
    this.total = total;
  }

  public String getFailureMssg() {
    return failureMssg;
  }

  public void setFailureMssg(String failureMssg) {
    this.failureMssg = failureMssg;
  }

  @Override public String toString() {
    return "ScoreMetric{" +
      "collectorItemId=" + collectorItemId +
      ", scoreTypeId=" + scoreTypeId +
      ", timestamp=" + timestamp +
      ", score='" + score + '\'' +
      ", total='" + total + '\'' +
      ", failureMssg='" + failureMssg + '\'' +
      ", noScore=" + noScore +
      ", componentMetrics=" + componentMetrics +
      ", options=" + options + '\'' +
      '}';
  }
}
