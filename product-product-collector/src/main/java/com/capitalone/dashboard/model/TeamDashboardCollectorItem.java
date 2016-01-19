package com.capitalone.dashboard.model;

public class TeamDashboardCollectorItem extends CollectorItem {
    private static final String DASHBOARD_ID = "dashboardId";

    public String getDashboardId() {
        return (String) getOptions().get(DASHBOARD_ID);
    }

    public void setDashboardId(String dashboardId) {
        this.getOptions().put(DASHBOARD_ID, dashboardId);
    }

    @Override
    public boolean equals(Object obj) {
        return ((TeamDashboardCollectorItem)obj).getDashboardId().equals(getDashboardId());
    }
}
