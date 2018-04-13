package com.capitalone.dashboard.model.score.settings;

/**
 * Alert Settings for Scoring component
 */
public class ComponentAlert {

  private ScoreThresholdSettings.ComparatorType comparator = ScoreThresholdSettings.ComparatorType.less_or_equal;

  //Value to compare
  private Double value;

  public static ComponentAlert cloneComponentAlert(ComponentAlert componentAlert) {
    if (null == componentAlert) {
      return null;
    }

    ComponentAlert componentAlertClone = new ComponentAlert();
    componentAlertClone.setComparator(componentAlert.getComparator());
    componentAlertClone.setValue(componentAlert.getValue());
    return componentAlertClone;
  }

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
