package com.capitalone.dashboard.model.score;

import java.util.List;

/*
  Score for a component like widget
 */
public class ScoreComponentMetric extends ScoreComponentMetricBase {
  //Individual components within a component, with scores for them
  private List<ScoreComponentMetricBase> children;

  public List<ScoreComponentMetricBase> getChildren() {
    return children;
  }

  public void setChildren(List<ScoreComponentMetricBase> children) {
    this.children = children;
  }

  @Override public String toString() {
    return "ScoreComponentMetric{" +
      "score='" + getScore() + '\'' +
      ", total='" + getTotal() + '\'' +
      ", weight='" + getWeight() + '\'' +
      ", refId='" + getRefId() + '\'' +
      ", displayId='" + getDisplayId() + '\'' +
      ", displayName='" + getDisplayName() + '\'' +
      ", message='" + getMessage() + '\'' +
      ", propagate='" + getPropagate() + '\'' +
      ", state='" + getState() + '\'' +
      ", noScore=" + isNoScore() +
      ", children=" + children + '\'' +
      ", options=" + getOptions() + '\'' +
      ", alert='" + isAlert() +
      '}';
  }
}
