package com.capitalone.dashboard.model.deploy;

import java.util.ArrayList;
import java.util.List;

public class Environment {
    private final String name;
    private final String url;
    private final List<DeployableUnit> units = new ArrayList<>();

    public Environment(String name, String url) {
        this.name = name;
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }

    public List<DeployableUnit> getUnits() {
        return units;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Environment that = (Environment) o;
        return name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
