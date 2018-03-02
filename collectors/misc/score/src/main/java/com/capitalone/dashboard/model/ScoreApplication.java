package com.capitalone.dashboard.model;

public class ScoreApplication extends CollectorItem {

  private static final String DASHBOARD_ID = "dashboardId";

  public String getDashboardId() {
    return (String) getOptions().get(DASHBOARD_ID);
  }

  public void setDashboardId(String dashboardId) {
    getOptions().put(DASHBOARD_ID, dashboardId);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    ScoreApplication that = (ScoreApplication) o;
    return getDashboardId().equals(that.getDashboardId());
  }

  @Override
  public int hashCode() {
    int result = getDashboardId().hashCode();
    return result;
  }


}
