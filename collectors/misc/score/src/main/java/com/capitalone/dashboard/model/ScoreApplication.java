package com.capitalone.dashboard.model;

import org.bson.types.ObjectId;

public class ScoreApplication extends CollectorItem {

  private static final String DASHBOARD_ID = "dashboardId";

  public ObjectId getDashboardId() {
    return (ObjectId) getOptions().get(DASHBOARD_ID);
  }

  public void setDashboardId(ObjectId dashboardId) {
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
