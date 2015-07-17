package com.capitalone.dashboard.model;

import org.springframework.data.mongodb.core.mapping.DBRef;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The software application the team is developing and/or operating. Consists of one or more software
 * components and may exist in one or more environments.
 */
public class Application {
    private String name;
    private String owner;
    private String lineOfBusiness;
    @DBRef
    private List<Component> components = new ArrayList<>();

    Application() {
    }

    public Application(String name, Component... componentsArray) {
        this.name = name;
        Collections.addAll(components, componentsArray);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getLineOfBusiness() {
        return lineOfBusiness;
    }

    public void setLineOfBusiness(String lineOfBusiness) {
        this.lineOfBusiness = lineOfBusiness;
    }

    public List<Component> getComponents() {
        return components;
    }

    public void addComponent(Component component) {
        getComponents().add(component);
    }
}
