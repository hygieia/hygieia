package com.capitalone.dashboard.auth.access;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.capitalone.dashboard.auth.AuthenticationUtil;
import com.capitalone.dashboard.model.AuthType;
import com.capitalone.dashboard.model.Dashboard;
import com.capitalone.dashboard.model.Owner;
import com.capitalone.dashboard.repository.DashboardRepository;

@Component
public class MethodLevelSecurityHandler {

	private DashboardRepository dashboardRepository;
	
	@Autowired
	public MethodLevelSecurityHandler(DashboardRepository dashboardRepository) {
		this.dashboardRepository = dashboardRepository;
	}
	
	public boolean isOwnerOfDashboard(ObjectId dashboardId) {
		Dashboard dashboard = dashboardRepository.findOne(dashboardId);
		if (dashboard == null) {
			return false;
		}
		
		String username = AuthenticationUtil.getUsernameFromContext();
		AuthType authType = AuthenticationUtil.getAuthTypeFromContext();

		//remote dashboards created via apikey tokens use an ldap id
		if (authType == AuthType.APIKEY) {
			authType = AuthType.LDAP;
		}
		
		//Check list of owners of dashboard to see if it contains the authenticated user
		if (null != dashboard.getOwners() && dashboard.getOwners().contains(new Owner(username, authType))) {
			return true;
		}
		
		//Maintain backwards compatability for dashboards created before authentication changes
		return authType.equals(AuthType.STANDARD) && username.equals(dashboard.getOwner());
	}
}
