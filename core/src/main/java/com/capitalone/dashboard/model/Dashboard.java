package com.capitalone.dashboard.model;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * A collection of widgets, collectors and application components that represent a software
 * project under development and/or in production use.
 *
 */
@Document(collection="dashboards")
public class Dashboard extends BaseModel {
    //NOTE Mongodb treats strings as different if they have different case
    @Indexed(unique=true)
    private String title;

    private List<Widget> widgets = new ArrayList<>();
    private Map<WidgetFamily, List<WidgetType>> activeWidgetTypes;
    private String owner;
    private DashboardType type;

    private Application application;

    Dashboard() {
    }

    public Dashboard(String title, Application application,String owner, DashboardType type) {
        this.title = title;
        this.application = application;
        this.owner = owner;
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Application getApplication() {
        return application;
    }

    public void setApplication(Application application) {
        this.application = application;
    }

    public List<Widget> getWidgets() {
        return widgets;
    }
    
    public void setWidgets(List<Widget> widgets) {
    	this.widgets = widgets;
    }
    
    public Map<WidgetFamily, List<WidgetType>> getActiveWidgetTypes() {
    	return activeWidgetTypes;
    }

    public void setActiveWidgetTypes(Map<WidgetFamily, List<WidgetType>> activeWidgetTypes) {
    	this.activeWidgetTypes = activeWidgetTypes;
    }
    
	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

    public DashboardType getType(){ return this.type; }

    public void setType(DashboardType type) { this.type = type; }

}
