package com.capitalone.dashboard.model.monitor;

import java.io.IOException;
import java.net.HttpURLConnection;

import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.capitalone.dashboard.model.ServiceStatus;

public class MonitorService {

	private static final Logger LOGGER = LoggerFactory.getLogger(MonitorService.class);
	
	private String logString;
	private HttpURLConnection httpUrlConnection;
	
	public MonitorService(HttpURLConnection httpUrlConnection, ObjectId dashboardId) {
		this.httpUrlConnection = httpUrlConnection;
		this.logString = "dashboardId: " + dashboardId.toString() + " ";
	}
	
	public ServiceStatus getServiceStatus() {
		return ServiceStatus.getServiceStatus(getResponseCode());
	}
	
	private int getResponseCode() {
        int code = 0;
        try {
        	httpUrlConnection.connect();
            code = httpUrlConnection.getResponseCode();
            LOGGER.debug(logString + httpUrlConnection.getURL().toString() + ": " + code);
        } catch (IOException e) {
            LOGGER.error(logString + httpUrlConnection.getURL().toString() + " failed with " + e.getMessage());
        }
        return code;
	}
	
}
