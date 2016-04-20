package com.capitalone.dashboard.model;

import org.springframework.data.mongodb.core.mapping.DBRef;

import lombok.Data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The software application the team is developing and/or operating. Consists of one or more software
 * components and may exist in one or more environments.
 */
@Data
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

    public void addComponent(Component component) {
        getComponents().add(component);
    }
}
