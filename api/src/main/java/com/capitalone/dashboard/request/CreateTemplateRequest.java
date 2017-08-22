package com.capitalone.dashboard.request;

import com.capitalone.dashboard.model.Template;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.List;

public class CreateTemplateRequest {

    @Valid
    @NotNull
    @Size(min = 6, max = 50)
    @Pattern(message = "Special character(s) found", regexp = "^[a-zA-Z0-9 ]*$")
    private String template;

    private List<String> widgets;

    private List<String> order;

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


    public Template toTemplate() {
        return new Template(template, widgets, order);
    }

    public Template copyTo(Template template) {
        Template updated = toTemplate();
        updated.setId(template.getId());
        return updated;
    }

}
