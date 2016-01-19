package com.capitalone.dashboard.model;

public class TeamDashboardCollectorItem extends CollectorItem {
    private static final String DASHBOARD_ID = "dashboardId";
    private static final String DATE_ENABLED = "dateEnabled";

    public String getDashboardId() {
        return (String) getOptions().get(DASHBOARD_ID);
    }

    public void setDashboardId(String dashboardId) {
        getOptions().put(DASHBOARD_ID, dashboardId);
    }

    public Long getDateEnabled(){
        return (Long) getOptions().get(DATE_ENABLED);
    }

    public void setDateEnabled(Long dateEnabledTimestamp){
        getOptions().put(DATE_ENABLED, dateEnabledTimestamp);
    }

    @Override
    public boolean equals(Object obj) {
        return ((TeamDashboardCollectorItem)obj).getDashboardId().equals(getDashboardId());
    }

    @Override
    public int hashCode() {
        return getDashboardId().hashCode() + getDateEnabled().hashCode();
    }
}
