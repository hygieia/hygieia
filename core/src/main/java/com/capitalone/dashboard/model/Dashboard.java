package com.capitalone.dashboard.model;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private String owner;
    private DashboardType type;

    private Application application;

    Dashboard() {
    }

    public Dashboard(String template, String title, Application application,String owner, DashboardType type) {
        this.template = template;
        this.title = title;
        this.application = application;
        this.owner = owner;
        this.type = type;
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
