package com.capitalone.dashboard.model;

/**
 * Denotes the status of a product or service
 */
public enum ServiceStatus {
    Ok(200, 200),
    Warning(300, 400),
    Unauth(401, 401),
    Alert(0, 999);

	private int low;
	private int high;
	
	private ServiceStatus(int low, int high) {
		this.low = low;
		this.high = high;
	}
	
	public static ServiceStatus fromString(String value) {
        for(ServiceStatus status : ServiceStatus.values()) {
            if (status.toString().equalsIgnoreCase(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException(value + " is not a valid ServiceStatus.");
    }
	
	public static ServiceStatus getServiceStatus(int statusCode) {
		for(ServiceStatus status : ServiceStatus.values()) {
			if(status.low <= statusCode && status.high >= statusCode) {
				return status;
			}
		}
		
		return Alert;
	}

}
