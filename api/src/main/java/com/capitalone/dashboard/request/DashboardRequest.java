package com.capitalone.dashboard.request;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.capitalone.dashboard.model.Application;
import com.capitalone.dashboard.model.Component;
import com.capitalone.dashboard.model.Dashboard;
import com.capitalone.dashboard.model.DashboardType;

public class DashboardRequest {
    @NotNull
    @Size(min=1, message="Please select a template")
    private String template;

    @Valid
    @NotNull
    private DashboardRequestTitle dashboardRequestTitle;

    private String applicationName;

    private String componentName;
    
    @NotNull
    private String owner;

    @NotNull
    @Size(min=1, message="Please select a type")
    private String type;

    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }

	public DashboardRequestTitle getDashboardRequestTitle() {
		return dashboardRequestTitle;
	}

	public void setDashboardRequestTitle(DashboardRequestTitle dashboardRequestTitle) {
		this.dashboardRequestTitle = dashboardRequestTitle;
	}

	public void setTitle(String title) {
		DashboardRequestTitle dashboardRequestTitle = new DashboardRequestTitle();
		dashboardRequestTitle.setTitle(title);
		this.dashboardRequestTitle = dashboardRequestTitle;
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

    public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public Dashboard toDashboard() {
        DashboardType type = DashboardType.fromString(this.type);
        Application application = new Application(applicationName, new Component(componentName));
        return new Dashboard(template, dashboardRequestTitle.getTitle(), application, owner, type);
    }

    public Dashboard copyTo(Dashboard dashboard) {
        Dashboard updated = toDashboard();
        updated.setId(dashboard.getId());
        return updated;
    }
}
