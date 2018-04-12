package com.capitalone.dashboard.model.score.settings;

public enum PropagateType {
  no(1),
  widget(2),
  dashboard(3);

  private final int value;

  private PropagateType(int value) {
    this.value = value;
  }

  public int getValue() {
    return value;
  }
}
