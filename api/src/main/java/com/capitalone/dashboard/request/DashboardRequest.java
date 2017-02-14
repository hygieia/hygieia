package com.capitalone.dashboard.request;

import java.util.List;
import java.util.Map;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.capitalone.dashboard.model.Application;
import com.capitalone.dashboard.model.Component;
import com.capitalone.dashboard.model.Dashboard;
import com.capitalone.dashboard.model.DashboardType;
import com.capitalone.dashboard.model.Widget;
import com.capitalone.dashboard.model.WidgetFamily;
import com.capitalone.dashboard.model.WidgetType;

public class DashboardRequest {

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
    
    private Map<WidgetFamily, List<WidgetType>> activeWidgetTypes;
    private List<Widget> widgets;
    
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

	public Map<WidgetFamily, List<WidgetType>> getActiveWidgetTypes() {
		return activeWidgetTypes;
	}

	public void setActiveWidgetTypes(Map<WidgetFamily, List<WidgetType>> activeWidgetTypes) {
		this.activeWidgetTypes = activeWidgetTypes;
	}
	
	public List<Widget> getWidgets() {
		return widgets;
	}

	public void setWidgets(List<Widget> widgets) {
		this.widgets = widgets;
	}

	public Dashboard toDashboard() {
        DashboardType type = DashboardType.fromString(this.type);
        Application application = new Application(applicationName, new Component(componentName));
        Dashboard dashboard = new Dashboard(dashboardRequestTitle.getTitle(), application, owner, type);
        dashboard.setActiveWidgetTypes(activeWidgetTypes);
        dashboard.setWidgets(widgets);
        return dashboard;
    }

    public Dashboard copyTo(Dashboard dashboard) {
        Dashboard updated = toDashboard();
        updated.setId(dashboard.getId());
        return updated;
    }
    
}
