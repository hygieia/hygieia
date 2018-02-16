package com.capitalone.dashboard.model;

import java.util.List;

/*
  Score for a widget
 */
public class ScoreWidgetMetric extends ScoreWidgetMetricBase {
  //Individual components within a widget, with scores for them
  private List<ScoreWidgetMetricBase> children;

  public List<ScoreWidgetMetricBase> getChildren() {
    return children;
  }

  public void setChildren(List<ScoreWidgetMetricBase> children) {
    this.children = children;
  }

  @Override public String toString() {
    return "ScoreWidgetMetric{" +
      "score='" + getScore() + '\'' +
      ", total='" + getTotal() + '\'' +
      ", weight='" + getWeight() + '\'' +
      ", id='" + getId() + '\'' +
      ", name='" + getName() + '\'' +
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
