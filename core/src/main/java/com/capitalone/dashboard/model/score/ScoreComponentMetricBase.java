package com.capitalone.dashboard.model.score;

import com.google.common.collect.Maps;
import org.bson.types.ObjectId;

import java.util.Map;

public class ScoreComponentMetricBase {
  //Score for the widget/component
  private String score;

  //Score total
  private String total;

  //Weight in percent for a widget/component
  private String weight;

  //Reference Id. When score is for type Dashboard, value is Widget Id
  private ObjectId refId;

  //Unique display id for widget/component
  private String displayId;

  //Display name for the widget/component
  private String displayName;

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

  public String getDisplayName() {
    return displayName;
  }

  public void setDisplayName(String displayName) {
    this.displayName = displayName;
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

  public String getDisplayId() {
    return displayId;
  }

  public void setDisplayId(String displayId) {
    this.displayId = displayId;
  }

  public ObjectId getRefId() {
    return refId;
  }

  public void setRefId(ObjectId refId) {
    this.refId = refId;
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
    return "ScoreComponentMetricBase{" +
      "score='" + score + '\'' +
      ", total='" + total + '\'' +
      ", weight='" + weight + '\'' +
      ", refId='" + refId + '\'' +
      ", displayId='" + displayId + '\'' +
      ", displayName='" + displayName + '\'' +
      ", message='" + message + '\'' +
      ", propagate='" + propagate + '\'' +
      ", state='" + state + '\'' +
      ", noScore=" + noScore + '\'' +
      ", alert=" + alert +
      ", options=" + options +
      '}';
  }
}
