package com.capitalone.dashboard.collector;

public class WidgetAlert {

  private ScoreThresholdSettings.ComparatorType comparator = ScoreThresholdSettings.ComparatorType.less_or_equal;

  //Value to compare
  private Double value;

  public ScoreThresholdSettings.ComparatorType getComparator() {
    return comparator;
  }

  public void setComparator(ScoreThresholdSettings.ComparatorType comparator) {
    this.comparator = comparator;
  }

  public Double getValue() {
    return value;
  }

  public void setValue(Double value) {
    this.value = value;
  }

  @Override public String toString() {
    return "WidgetAlert{" +
      "comparator=" + comparator +
      ", value=" + value +
      '}';
  }
}
