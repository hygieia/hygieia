package com.capitalone.dashboard.v2.dashboard;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.hateoas.ResourceSupport;

import com.capitalone.dashboard.auth.AuthenticationUtil;
import com.capitalone.dashboard.model.Application;
import com.capitalone.dashboard.model.DashboardType;
import com.capitalone.dashboard.model.Owner;
import com.capitalone.dashboard.model.Widget;

public class Dashboard extends ResourceSupport {

    private String dashboardId;
    private String template;
    private String title;
    private Collection<Widget> widgets;
    private List<Owner> owners;
    private DashboardType type;
    private Application application;
    
    public Dashboard() {
        widgets = new ArrayList<>();
        owners = new ArrayList<>();
    }
    
    public Dashboard(com.capitalone.dashboard.model.Dashboard dashboard) {
        this.dashboardId = dashboard.getId().toHexString();
        this.template = dashboard.getTemplate();
        this.title = dashboard.getTitle();
        this.type = dashboard.getType();
        this.widgets = dashboard.getWidgets();
        this.owners = dashboard.getOwners();
        this.application = dashboard.getApplication();
    }

    public String getDashboardId() {
        return dashboardId;
    }

    public void setDashboardId(String dashboardId) {
        this.dashboardId = dashboardId;
    }

    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Collection<Widget> getWidgets() {
        return widgets;
    }

    public void setWidgets(Collection<Widget> widgets) {
        this.widgets = widgets;
    }

    public List<Owner> getOwners() {
        return owners;
    }

    public void setOwners(List<Owner> owners) {
        this.owners = owners;
    }

    public DashboardType getType() {
        return type;
    }

    public void setType(DashboardType type) {
        this.type = type;
    }

    public Application getApplication() {
        return application;
    }

    public void setApplication(Application application) {
        this.application = application;
    }
    
    public com.capitalone.dashboard.model.Dashboard toDomainModel() {
        Owner owner = new Owner(AuthenticationUtil.getUsernameFromContext(), AuthenticationUtil.getAuthTypeFromContext());
        com.capitalone.dashboard.model.Dashboard dashboard = new com.capitalone.dashboard.model.Dashboard(this.template, this.title, this.application, owner, this.type);
        
        return dashboard;
    }
    
}
