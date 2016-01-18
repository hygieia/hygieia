package com.capitalone.dashboard.request;

import com.capitalone.dashboard.model.*;

import javax.validation.constraints.NotNull;

public class DashboardRequest {
    @NotNull
    private String template;

    @NotNull
    private String title;

    private String applicationName;

    private String componentName;
    
    @NotNull
    private String owner;

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

    public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public Dashboard toDashboard() {
        DashboardType type = DashboardType.fromString(this.type);
        Application application = new Application(applicationName, new Component(componentName));
        return new Dashboard(template, title, application, owner, type);
    }

    public Dashboard copyTo(Dashboard dashboard) {
        Dashboard updated = toDashboard();
        updated.setId(dashboard.getId());
        return updated;
    }
}
