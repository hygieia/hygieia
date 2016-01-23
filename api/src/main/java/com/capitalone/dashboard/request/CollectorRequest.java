package com.capitalone.dashboard.request;

import com.capitalone.dashboard.model.Collector;
import com.capitalone.dashboard.model.CollectorType;

import javax.validation.constraints.NotNull;

/**
 * Created by yaf107 on 1/11/16.
 */
public class CollectorRequest {
    @NotNull
    private String name;
    @NotNull
    private CollectorType collectorType;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public CollectorType getCollectorType() {
        return collectorType;
    }

    public void setCollectorType(CollectorType collectorType) {
        this.collectorType = collectorType;
    }

    public Collector toCollector() {
        Collector collector = new Collector();
        collector.setCollectorType(collectorType);
        collector.setName(name);
        return collector;
    }
}
