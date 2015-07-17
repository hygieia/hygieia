package com.capitalone.dashboard.model;

/**
 * Denotes the status of a product or service
 */
public enum ServiceStatus {
    Ok, Warning, Alert;

    public static ServiceStatus fromString(String value) {
        for(ServiceStatus status : ServiceStatus.values()) {
            if (status.toString().equalsIgnoreCase(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException(value + " is not a valid ServiceStatus.");
    }
}
