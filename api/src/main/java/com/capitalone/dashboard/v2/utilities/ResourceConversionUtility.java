package com.capitalone.dashboard.v2.utilities;

import org.springframework.stereotype.Component;

import com.capitalone.dashboard.auth.AuthenticationUtil;
import com.capitalone.dashboard.model.Dashboard;
import com.capitalone.dashboard.model.Owner;
import com.capitalone.dashboard.v2.dtos.DashboardDTO;

@Component
public class ResourceConversionUtility {

    public Dashboard toDashboard(DashboardDTO resource) {
        Owner owner = new Owner(AuthenticationUtil.getUsernameFromContext(), AuthenticationUtil.getAuthTypeFromContext());
        Dashboard dashboard = new Dashboard(resource.getTemplate(), resource.getTitle(), resource.getApplication(), owner, resource.getType());
        
        return dashboard;
    }
    
    public DashboardDTO toDashboardDTO(Dashboard dashboard) {
        DashboardDTO resource = new DashboardDTO();
        resource.setDashboardId(dashboard.getId().toHexString());
        resource.setTemplate(dashboard.getTemplate());
        resource.setTitle(dashboard.getTitle());
        resource.setType(dashboard.getType());
        resource.setWidgets(dashboard.getWidgets());
        resource.setOwners(dashboard.getOwners());
        resource.setApplication(dashboard.getApplication());
        
        return resource;
    }
    
}
