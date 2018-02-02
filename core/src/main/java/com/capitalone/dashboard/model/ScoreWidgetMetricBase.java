package com.capitalone.dashboard.model;

public class ScoreWidgetMetricBase {
  private String score;
  private String total;
  private String weight;
  private String id;
  private String name;
  private String propagate;
  private String state;
  private boolean noScore;
  private boolean alert = false;

  private String message;

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
      ", noScore=" + noScore +
      ", alert=" + alert +
      '}';
  }
}
