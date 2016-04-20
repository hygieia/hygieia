package com.capitalone.dashboard.model;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

import java.util.HashSet;
import java.util.Set;

/**
 * Represents code quality at a specific point in time. This could include
 * a unit test run, a security scan, static analysis, functional tests,
 * manual acceptance tests or bug reports.
 *
 * Possible Collectors:
 *  Sonar (in scope)
 *  Fortify
 *  ALM
 *  Various build system test results
 *
 */
@Data
@Document(collection="code_quality")
public class CodeQuality extends BaseModel {
    private ObjectId collectorItemId;
    private long timestamp;

    private String name;
    private String url;
    private CodeQualityType type;
    private String version;
    private  ObjectId buildId;

    private Set<CodeQualityMetric> metrics = new HashSet<>();

    public Set<CodeQualityMetric> getMetrics() {
        return metrics;
    }
}
