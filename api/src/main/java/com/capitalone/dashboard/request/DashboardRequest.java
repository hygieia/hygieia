package com.capitalone.dashboard.request;

import javax.validation.constraints.NotNull;

import com.capitalone.dashboard.auth.AuthenticationUtil;
import com.capitalone.dashboard.model.Application;
import com.capitalone.dashboard.model.Component;
import com.capitalone.dashboard.model.Dashboard;
import com.capitalone.dashboard.model.DashboardType;
import com.capitalone.dashboard.model.Owner;

public class DashboardRequest {
    @NotNull
    private String template;

    @NotNull
    private String title;

    private String applicationName;

    private String componentName;
    
    @NotNull
    private String type;

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

    public String getApplicationName() {
        return applicationName;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    public String getComponentName() {
        return componentName;
    }

    public void setComponentName(String componentName) {
        this.componentName = componentName;
    }

    public String getType() { return type; }

    public void setType(String type) { this.type = type; }

	public Dashboard toDashboard() {
        DashboardType type = DashboardType.fromString(this.type);
        Application application = new Application(applicationName, new Component(componentName));
        Owner owner = new Owner(AuthenticationUtil.getUsernameFromContext(), AuthenticationUtil.getAuthTypeFromContext());
        return new Dashboard(template, title, application, owner, type);
    }

    public Dashboard copyTo(Dashboard dashboard) {
        Dashboard updated = toDashboard();
        updated.setId(dashboard.getId());
        return updated;
    }
}
