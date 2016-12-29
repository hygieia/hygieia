package com.capitalone.dashboard.request;

import java.util.List;
import java.util.Map;

import javax.validation.constraints.NotNull;

import com.capitalone.dashboard.model.Application;
import com.capitalone.dashboard.model.Component;
import com.capitalone.dashboard.model.Dashboard;
import com.capitalone.dashboard.model.DashboardType;
import com.capitalone.dashboard.model.WidgetFamily;
import com.capitalone.dashboard.model.WidgetType;

public class DashboardRequest {
    @NotNull
    private String title;

    private String applicationName;

    private String componentName;
    
    @NotNull
    private String owner;

    @NotNull
    private String type;
    
    private Map<WidgetFamily, List<WidgetType>> activeWidgetTypes;

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

	public Map<WidgetFamily, List<WidgetType>> getActiveWidgetTypes() {
		return activeWidgetTypes;
	}

	public void setActiveWidgetTypes(Map<WidgetFamily, List<WidgetType>> activeWidgetTypes) {
		this.activeWidgetTypes = activeWidgetTypes;
	}
	
	public Dashboard toDashboard() {
        DashboardType type = DashboardType.fromString(this.type);
        Application application = new Application(applicationName, new Component(componentName));
        Dashboard dashboard = new Dashboard(title, application, owner, type);
        dashboard.setActiveWidgetTypes(activeWidgetTypes);
        return dashboard;
    }

    public Dashboard copyTo(Dashboard dashboard) {
        Dashboard updated = toDashboard();
        updated.setId(dashboard.getId());
        return updated;
    }
    
}
