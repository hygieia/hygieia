package com.capitalone.dashboard.model;

import com.google.common.collect.Maps;

import java.util.Map;

public class ScoreWidgetMetricBase {
  //Score for the widget
  private String score;

  //Score total
  private String total;

  //Weight in percent for a widget
  private String weight;

  //Unique id for widget/component
  private String id;

  //Display name for the widget/component
  private String name;

  //Propagate type value for widget/component
  private String propagate;

  //Status of score calculation
  private String state;

  //If widget/component has no score, flag is true
  private boolean noScore;

  //Alert is true if there is message to alert
  private boolean alert = false;

  //Message to display
  private String message;

  //Additional options to save data
  public Map<String, Object> options = Maps.newHashMap();

  public Map<String, Object> getOptions() {
    return options;
  }

  public String getScore() {
    return score;
  }

  public void setScore(String score) {
    this.score = score;
  }

  public String getTotal() {
    return total;
  }

  public void setTotal(String total) {
    this.total = total;
  }

  public String getWeight() {
    return weight;
  }

  public void setWeight(String weight) {
    this.weight = weight;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public String getPropagate() {
    return propagate;
  }

  public void setPropagate(String propagate) {
    this.propagate = propagate;
  }

  public boolean isNoScore() {
    return noScore;
  }

  public void setNoScore(boolean noScore) {
    this.noScore = noScore;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getState() {
    return state;
  }

  public void setState(String state) {
    this.state = state;
  }

  public boolean isAlert() {
    return alert;
  }

  public void setAlert(boolean alert) {
    this.alert = alert;
  }

  @Override public String toString() {
    return "ScoreWidgetMetricBase{" +
      "score='" + score + '\'' +
      ", total='" + total + '\'' +
      ", weight='" + weight + '\'' +
      ", id='" + id + '\'' +
      ", name='" + name + '\'' +
      ", message='" + message + '\'' +
      ", propagate='" + propagate + '\'' +
      ", state='" + state + '\'' +
      ", noScore=" + noScore + '\'' +
      ", alert=" + alert +
      ", options=" + options +
      '}';
  }
}
