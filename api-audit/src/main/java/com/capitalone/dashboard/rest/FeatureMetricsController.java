package com.capitalone.dashboard.rest;

import com.capitalone.dashboard.model.FeatureMetrics;
import com.capitalone.dashboard.service.FeatureMetricsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RestController
public class FeatureMetricsController {

    private final FeatureMetricsService featureMetricsService;

    @Autowired
    public FeatureMetricsController(FeatureMetricsService featureMetricsService) {
        this.featureMetricsService = featureMetricsService;
    }

    @RequestMapping(value = "/metrics/component/{componentName}", method = GET, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<FeatureMetrics> getAuditResultsAll(@Valid @PathVariable String componentName) {
        FeatureMetrics featureMetrics = featureMetricsService.getFeatureMetrics(componentName);
        return ResponseEntity.ok().body(featureMetrics);
    }


    @RequestMapping(value = "/metrics/component/{componentName}/metric/{metricName}")
    public ResponseEntity<FeatureMetrics> getFeatureMetricByType(@Valid @PathVariable String componentName,
                                                                  @Valid @PathVariable String metricName){
        FeatureMetrics featureMetrics = featureMetricsService.getFeatureMetricsByType(componentName, metricName);

        return ResponseEntity.ok().body(featureMetrics);
    }
}
