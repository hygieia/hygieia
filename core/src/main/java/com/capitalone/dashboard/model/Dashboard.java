package com.capitalone.dashboard.model;

import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A collection of widgets, collectors and application components that represent a software
 * project under development and/or in production use.
 *
 */
@Data
@Document(collection="dashboards")
public class Dashboard extends BaseModel {
    private String template;
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

    /**
     * Finds the mapped names for each stage type from the widget options
     * @return
     */
	public Map<PipelineStageType, String> findEnvironmentMappings(){
        Map<String, String> environmentMappings = null;
        for(Widget widget : this.getWidgets()) {
            if (widget.getName().equalsIgnoreCase("pipeline")) {
                environmentMappings =  (Map<String, String>) widget.getOptions().get("mappings");
            }
        }
        Map<PipelineStageType, String> stageTypeToEnvironmentNameMap = new HashMap<>();
        if(environmentMappings != null && !environmentMappings.isEmpty()){
            for (Map.Entry<String,String> mapping : environmentMappings.entrySet()) {
                stageTypeToEnvironmentNameMap.put(PipelineStageType.fromString((String) mapping.getKey()), (String) mapping.getValue());
            }
        }
        return stageTypeToEnvironmentNameMap;
    }

}
