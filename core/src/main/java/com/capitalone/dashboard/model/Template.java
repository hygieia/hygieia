package com.capitalone.dashboard.model;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

/**
 * A collection of templates represent a software
 * project under development and/or in production use.
 */
@Document(collection = "templates")
public class Template extends BaseModel {
    @Indexed(unique = true)
    private String template;

    private List<String> widgets = new ArrayList<>();

    private List<String> order = new ArrayList<>();

    public Template(String template, List<String> widgets, List<String> order) {
        this.template = template;
        this.widgets = widgets;
        this.order = order;
    }

    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }

    public List<String> getWidgets() {
        return widgets;
    }

    public void setWidgets(List<String> widgets) {
        this.widgets = widgets;
    }

    public List<String> getOrder() {
        return order;
    }

    public void setOrder(List<String> order) {
        this.order = order;
    }

}
