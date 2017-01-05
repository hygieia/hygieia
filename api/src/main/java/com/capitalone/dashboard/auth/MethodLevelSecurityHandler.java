package com.capitalone.dashboard.auth;

import org.bson.types.ObjectId;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
public class MethodLevelSecurityHandler {

	public boolean isOwnerOfDashboard(Authentication authentication, ObjectId dashboardId) {
		return false;
	}
}
