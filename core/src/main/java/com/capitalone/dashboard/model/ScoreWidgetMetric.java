package com.capitalone.dashboard.model;

import java.util.List;

public class ScoreWidgetMetric extends ScoreWidgetMetricBase {
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
      ", alert='" + isAlert() +
      '}';
  }
}
