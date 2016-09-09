package com.capitalone.dashboard.model;

/**
 * Represents a UDeploy environment by ID and name.
 */
public class Environment {
    private String id;
    private String name;

    public Environment(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
