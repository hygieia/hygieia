package com.capitalone.dashboard.v2.dtos;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.hateoas.ResourceSupport;

import com.capitalone.dashboard.model.Application;
import com.capitalone.dashboard.model.DashboardType;
import com.capitalone.dashboard.model.Owner;
import com.capitalone.dashboard.model.Widget;

public class DashboardDTO extends ResourceSupport {

    private String dashboardId;
    private String template;
    private String title;
    private Collection<Widget> widgets;
    private List<Owner> owners;
    private DashboardType type;
    private Application application;
    
    public DashboardDTO() {
        widgets = new ArrayList<>();
        owners = new ArrayList<>();
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
    
}
