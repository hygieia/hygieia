package com.capitalone.dashboard.model;

import org.bson.types.ObjectId;
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
    //Ignore Updates
    @Transient
    private String configurationItemBusServName;
    //Ignore Updates
    @Transient
    private String configurationItemBusAppName;

    private ObjectId configurationItemBusServObjectId;

    private ObjectId configurationItemBusAppObjectId;

    private boolean validServiceName;

    private boolean validAppName;

    private boolean remoteCreated;

    private List<String> activeWidgets;

    @Transient
    String errorMessage;

    @Transient
    int errorCode;

    Dashboard() {
    }

    public Dashboard(String template, String title, Application application, Owner owner, DashboardType type, ObjectId configurationItemBusServObjectId, ObjectId configurationItemBusAppObjectId,List<String> activeWidgets) {
        this(false, template, title, application, owner, type,configurationItemBusServObjectId, configurationItemBusAppObjectId,activeWidgets);
    }

    public Dashboard(boolean remoteCreated, String template, String title, Application application, Owner owner, DashboardType type, ObjectId configurationItemBusServObjectId, ObjectId configurationItemBusAppObjectId,List<String> activeWidgets) {
        this.template = template;
        this.title = title;
        this.configurationItemBusServObjectId = configurationItemBusServObjectId;
        this.configurationItemBusAppObjectId = configurationItemBusAppObjectId;
        this.application = application;
        this.type = type;
        this.owners.add(owner);
        this.activeWidgets = activeWidgets;
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

    public ObjectId getConfigurationItemBusServObjectId() {
        return configurationItemBusServObjectId;
    }

    public void setConfigurationItemBusServObjectId(ObjectId configurationItemBusServObjectId) {
        this.configurationItemBusServObjectId = configurationItemBusServObjectId;
    }

    public ObjectId getConfigurationItemBusAppObjectId() {
        return configurationItemBusAppObjectId;
    }

    public void setConfigurationItemBusAppObjectId(ObjectId configurationItemBusAppObjectId) {
        this.configurationItemBusAppObjectId = configurationItemBusAppObjectId;
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

}
