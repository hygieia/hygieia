package com.capitalone.dashboard.request;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.capitalone.dashboard.auth.AuthenticationUtil;
import com.capitalone.dashboard.model.Application;
import com.capitalone.dashboard.model.Component;
import com.capitalone.dashboard.model.Dashboard;
import com.capitalone.dashboard.model.DashboardType;
import com.capitalone.dashboard.model.Owner;
import com.capitalone.dashboard.model.ScoreDisplayType;

import com.google.common.collect.Lists;

import java.util.List;

public class DashboardRequest {
    @NotNull
    @Size(min=1, message="Please select a template")
    private String template;

    @Valid
    @NotNull
    private DashboardRequestTitle dashboardRequestTitle;

    private String applicationName;

    private String componentName;

    private String configurationItemBusServName;

    private String configurationItemBusAppName;

    //Enable/Disable scoring for the dashboard
    //Disabled by default. It can be enabled when dashboard type is Team
    private boolean scoreEnabled = false;

    //Display position for score.
    private String scoreDisplay;

    @NotNull
    @Size(min=1, message="Please select a type")
    private String type;

    private List<String> activeWidgets;

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

    public String getConfigurationItemBusServName() {
        return configurationItemBusServName;
    }

    public void setConfigurationItemBusServName(String configurationItemBusServName) {
        this.configurationItemBusServName = configurationItemBusServName;
    }

    public String getConfigurationItemBusAppName() {
        return configurationItemBusAppName;
    }

    public void setConfigurationItemBusAppName(String configurationItemBusAppName) {
        this.configurationItemBusAppName = configurationItemBusAppName;
    }

    public List<String> getActiveWidgets() {
        return activeWidgets;
    }

    public void setActiveWidgets(List<String> activeWidgets) {
        this.activeWidgets = activeWidgets;
    }

    public boolean isScoreEnabled() {
        return scoreEnabled;
    }

    public void setScoreEnabled(boolean scoreEnabled) {
        this.scoreEnabled = scoreEnabled;
    }

    public String getScoreDisplay() {
        return scoreDisplay;
    }

    public void setScoreDisplay(String scoreDisplay) {
        this.scoreDisplay = scoreDisplay;
    }

    public Dashboard toDashboard() {
        DashboardType type = DashboardType.fromString(this.type);
        Application application = new Application(applicationName, new Component(componentName));
        Owner owner = new Owner(AuthenticationUtil.getUsernameFromContext(), AuthenticationUtil.getAuthTypeFromContext());
        List<Owner> owners = Lists.newArrayList();
        owners.add(owner);
        return new Dashboard(
          template,
          dashboardRequestTitle.getTitle(),
          application,
          owners,
          type ,
          configurationItemBusServName,
          configurationItemBusAppName,
          activeWidgets,
          scoreEnabled,
          ScoreDisplayType.fromString(scoreDisplay)
        );


    }

    public Dashboard copyTo(Dashboard dashboard) {
        Dashboard updated = toDashboard();
        updated.setId(dashboard.getId());
        return updated;
    }
}
