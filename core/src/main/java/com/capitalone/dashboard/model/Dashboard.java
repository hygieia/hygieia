package com.capitalone.dashboard.model;

import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

/**
 * A collection of widgets, collectors and application components that represent a software
 * project under development and/or in production use.
 *
 */
@Document(collection="dashboards")
public class Dashboard extends BaseModel {

    private String template;

    //NOTE Mongodb treats strings as different if they have different case
    @Indexed(unique=true)
    private String title;

    private List<Widget> widgets = new ArrayList<>();

    // multiple owner references for backwards compatibility
    // TODO: remove once impacts of breaking change are assessed
    private String owner;
    private List<Owner> owners = new ArrayList<Owner>();
    
    private DashboardType type;

    private Application application;

    private String configurationItemBusServName;

    private String configurationItemBusAppName;

    private boolean validServiceName;

    private boolean validAppName;

    private boolean remoteCreated;

    //Enable/Disable scoring for the dashboard
    private boolean scoreEnabled;

    //Display position for score.
    //Default to HEADER
    private ScoreDisplayType scoreDisplay = ScoreDisplayType.HEADER;

    private List<String> activeWidgets;

    @Transient
    String errorMessage;

    @Transient
    int errorCode;

    Dashboard() {
    }

    public Dashboard(String template, String title, Application application, Owner owner, DashboardType type, String configurationItemBusServName, String configurationItemBusAppName, List<String> activeWidgets, boolean scoreEnabled, ScoreDisplayType scoreDisplay) {
        this(false, template, title, application, owner, type,configurationItemBusServName, configurationItemBusAppName,activeWidgets, scoreEnabled, scoreDisplay);
    }

    public Dashboard(boolean remoteCreated, String template, String title, Application application, Owner owner, DashboardType type, String configurationItemBusServName, String configurationItemBusAppName,List<String> activeWidgets, boolean scoreEnabled, ScoreDisplayType scoreDisplay) {
        this.template = template;
        this.title = title;
        this.configurationItemBusServName = configurationItemBusServName;
        this.configurationItemBusAppName = configurationItemBusAppName;
        this.application = application;
        this.type = type;
        this.owners.add(owner);
        this.activeWidgets = activeWidgets;
        this.scoreEnabled = scoreEnabled;
        this.scoreDisplay = scoreDisplay;
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

    public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}
	
	public List<Owner> getOwners() {
		return owners;
	}

	public void setOwners(List<Owner> owners) {
		this.owners = owners;
	}

    public DashboardType getType(){ return this.type; }

    public void setType(DashboardType type) { this.type = type; }

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

    public boolean isValidServiceName() {
        return validServiceName;
    }

    public void setValidServiceName(boolean validServiceName) {
        this.validServiceName = validServiceName;
    }

    public boolean isValidAppName() {
        return validAppName;
    }

    public void setValidAppName(boolean validAppName) {
        this.validAppName = validAppName;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public boolean isRemoteCreated() {
        return remoteCreated;
    }

    public void setRemoteCreated(boolean remoteCreated) {
        this.remoteCreated = remoteCreated;
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

    public ScoreDisplayType getScoreDisplay() {
        return scoreDisplay;
    }

    public void setScoreDisplay(ScoreDisplayType scoreDisplay) {
        this.scoreDisplay = scoreDisplay;
    }
}
