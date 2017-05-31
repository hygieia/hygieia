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
    private String configurationItemAppName;
    //Ignore Updates
    @Transient
    private String configurationItemCompName;
    //@Indexed(unique=true)
    private ObjectId configurationItemAppObjectId;
    //@Indexed(unique=true)
    private ObjectId configurationItemComponentObjectId;

    private boolean validAppName;

    private boolean validCompName;

    Dashboard() {
    }

    public Dashboard(String template, String title, Application application, Owner owner, DashboardType type, ObjectId configurationItemAppObjectId, ObjectId configurationItemComponentObjectId) {
        this.template = template;
        this.title = title;
        this.configurationItemAppObjectId = configurationItemAppObjectId;
        this.configurationItemComponentObjectId = configurationItemComponentObjectId;
        this.application = application;
        this.type = type;
        this.owners.add(owner);
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

    public String getConfigurationItemAppName() {
        return configurationItemAppName;
    }

    public void setConfigurationItemAppName(String configurationItemAppName) {
        this.configurationItemAppName = configurationItemAppName;
    }

    public String getConfigurationItemCompName() {
        return configurationItemCompName;
    }

    public void setConfigurationItemCompName(String configurationItemCompName) {
        this.configurationItemCompName = configurationItemCompName;
    }

    public ObjectId getConfigurationItemAppObjectId() {
        return configurationItemAppObjectId;
    }

    public void setConfigurationItemAppObjectId(ObjectId configurationItemAppObjectId) {
        this.configurationItemAppObjectId = configurationItemAppObjectId;
    }

    public ObjectId getConfigurationItemComponentObjectId() {
        return configurationItemComponentObjectId;
    }

    public void setConfigurationItemComponentObjectId(ObjectId configurationItemComponentObjectId) {
        this.configurationItemComponentObjectId = configurationItemComponentObjectId;
    }

    public boolean isValidAppName() {
        return validAppName;
    }

    public void setValidAppName(boolean validAppName) {
        this.validAppName = validAppName;
    }

    public boolean isValidCompName() {
        return validCompName;
    }

    public void setValidCompName(boolean validCompName) {
        this.validCompName = validCompName;
    }
}
