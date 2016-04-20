package com.capitalone.dashboard.model;

import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

/**
 * The collectors that have been registered in the given Dashboard app instance.
 */
@Data
@Document(collection="collectors")
public class Collector extends BaseModel {
    private String name;
    private CollectorType collectorType;
    private boolean enabled;
    private boolean online;
    private long lastExecuted;

    public Collector() {
    }

    public Collector(String name, CollectorType collectorType) {
        this.name = name;
        this.collectorType = collectorType;
    }
}
