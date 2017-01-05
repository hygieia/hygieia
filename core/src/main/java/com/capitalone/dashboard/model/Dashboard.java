package com.capitalone.dashboard.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

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

    Dashboard() {
    }

    public Dashboard(String template, String title, Application application, Owner owner, DashboardType type) {
        this.template = template;
        this.title = title;
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

    /**
     * Finds the mapped names for each stage type from the widget options
     * @return
     */
	public Map<PipelineStageType, String> findEnvironmentMappings(){

        HashMap<String, String> environmentMappings = new HashMap<>();
        for(Widget widget : this.getWidgets()) {
            if (widget.getName().equalsIgnoreCase("pipeline")) {
                HashMap<?, ?> gh = (HashMap<?, ?>)widget.getOptions().get("mappings");
                for (Map.Entry<?, ?> entry : gh.entrySet()) {
                    environmentMappings.put((String) entry.getKey(), (String) entry.getValue());
                }
            }
        }
        Map<PipelineStageType, String> stageTypeToEnvironmentNameMap = new HashMap<>();
        if(!environmentMappings.isEmpty()){
            for (Map.Entry<String,String> mapping : environmentMappings.entrySet()) {
                stageTypeToEnvironmentNameMap.put(PipelineStageType.fromString((String) mapping.getKey()), (String) mapping.getValue());
            }
        }
        return stageTypeToEnvironmentNameMap;
    }

}
