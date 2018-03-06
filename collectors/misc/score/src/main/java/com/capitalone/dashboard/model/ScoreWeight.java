package com.capitalone.dashboard.model;

import java.util.List;
import java.util.Map;

import com.capitalone.dashboard.model.score.settings.PropagateType;
import com.capitalone.dashboard.model.score.settings.ScoreTypeValue;
import com.google.common.collect.Maps;
import org.bson.types.ObjectId;

public class ScoreWeight {

  public static final int DEFAULT_TOTAL = 100;

  public static enum ProcessingState {
    complete,
    not_processed,
    criteria_failed,
    criteria_passed
  }

  private ScoreTypeValue score = new ScoreTypeValue();

  private int total = DEFAULT_TOTAL;

  private ProcessingState state = ProcessingState.not_processed;

  private String message;

  //Reference Id. When score is for type Dashboard, value is Widget Id
  private ObjectId refId;

  private String id;

  private String name;

  private int weight = 0;

  private boolean alert = false;

  private List<ScoreWeight> children;

  private final Map<String, Object> options = Maps.newHashMap();

  public ScoreWeight() {}

  public ScoreWeight(String id, String name) {
    this.id = id;
    this.name = name;
  }

  public ScoreTypeValue getScore() {
    return score;
  }

  public void setScore(ScoreTypeValue score) {
    this.score = score;
  }

  public int getTotal() {
    return total;
  }

  public void setTotal(int total) {
    this.total = total;
  }

  public ProcessingState getState() {
    return state;
  }

  public void setState(ProcessingState state) {
    this.state = state;
  }

  public PropagateType getPropagate() {
    return (null == score ? PropagateType.no : score.getPropagate());
  }

  public boolean isPropagateToDashboard() {
    return (null != score && score.getPropagate() == PropagateType.dashboard);
  }

  public boolean isPropagateToWidget() {
    return (
      null != score &&
      (score.getPropagate() == PropagateType.widget || score.getPropagate() == PropagateType.dashboard)
    );
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public int getWeight() {
    return weight;
  }

  public void setWeight(int weight) {
    this.weight = weight;
  }

  public ObjectId getRefId() {
    return refId;
  }

  public void setRefId(ObjectId refId) {
    this.refId = refId;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public List<ScoreWeight> getChildren() {
    return children;
  }

  public void setChildren(List<ScoreWeight> children) {
    this.children = children;
  }

  public boolean isAlert() {
    return alert;
  }

  public void setAlert(boolean alert) {
    this.alert = alert;
  }

  public Map<String, Object> getOptions() {
    return options;
  }

  @Override
  public String toString() {
    return "ScoreWeight{" +
      "score=" + score +
      ", total='" + total + '\'' +
      ", state=" + state +
      ", message='" + message + '\'' +
      ", refId='" + refId + '\'' +
      ", id='" + id + '\'' +
      ", name='" + name + '\'' +
      ", weight=" + weight +
      ", children=" + children +
      ", alert=" + alert +
      '}';
  }
}
