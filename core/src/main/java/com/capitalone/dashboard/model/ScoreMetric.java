package com.capitalone.dashboard.model;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Collection;

@Document(collection = "score_metric")
public class ScoreMetric extends BaseModel {
  private ObjectId collectorItemId;
  private ObjectId dashboardId;
  private long timestamp;
  private String score;
  private String total;
  private String failureMssg;
  private boolean noScore;

  private Collection<ScoreWidgetMetric> scoreWidgetMetrics;

  public ObjectId getCollectorItemId() {
    return collectorItemId;
  }

  public void setCollectorItemId(ObjectId collectorItemId) {
    this.collectorItemId = collectorItemId;
  }

  public ObjectId getDashboardId() {
    return dashboardId;
  }

  public void setDashboardId(ObjectId dashboardId) {
    this.dashboardId = dashboardId;
  }

  public long getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(long timestamp) {
    this.timestamp = timestamp;
  }

  public Collection<ScoreWidgetMetric> getScoreWidgetMetrics() {
    return scoreWidgetMetrics;
  }

  public void setScoreWidgetMetrics(Collection<ScoreWidgetMetric> scoreWidgetMetrics) {
    this.scoreWidgetMetrics = scoreWidgetMetrics;
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
      ", dashboardId=" + dashboardId +
      ", timestamp=" + timestamp +
      ", score='" + score + '\'' +
      ", total='" + total + '\'' +
      ", failureMssg='" + failureMssg + '\'' +
      ", noScore=" + noScore +
      ", scoreWidgetMetrics=" + scoreWidgetMetrics +
      '}';
  }
}
