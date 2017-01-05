package com.capitalone.dashboard.auth;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import com.capitalone.dashboard.model.Dashboard;
import com.capitalone.dashboard.repository.DashboardRepository;

@Component
public class MethodLevelSecurityHandler {

	private DashboardRepository dashboardRepository;
	
	@Autowired
	public MethodLevelSecurityHandler(DashboardRepository dashboardRepository) {
		this.dashboardRepository = dashboardRepository;
	}
	
	public boolean isOwnerOfDashboard(Authentication authentication, ObjectId dashboardId) {
		Dashboard findOne = dashboardRepository.findOne(dashboardId);
		String owner = findOne.getOwner();
		boolean value = owner.equals(authentication.getPrincipal());
		return value;
	}
}
