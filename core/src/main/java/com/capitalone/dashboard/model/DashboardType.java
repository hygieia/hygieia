package com.capitalone.dashboard.model;

/**
 * Defines the type of {@link Dashboard}.
 */
public enum DashboardType {
    Product,
    Team;

    public static DashboardType fromString(String value){
        for(DashboardType dashboardType : values()){
            if(dashboardType.toString().equalsIgnoreCase(value)){
                return dashboardType;
            }
        }
        throw new IllegalArgumentException(value+" is not a valid DashboardType");
    }
}
