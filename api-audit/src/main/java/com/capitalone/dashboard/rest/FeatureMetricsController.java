package com.capitalone.dashboard.rest;

import com.capitalone.dashboard.model.ExecutiveFeatureMetrics;
import com.capitalone.dashboard.model.ComponentFeatureMetrics;
import com.capitalone.dashboard.model.LobFeatureMetrics;
import com.capitalone.dashboard.model.ProductFeatureMetrics;
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
    public ResponseEntity<ComponentFeatureMetrics> getComponentMetrics(@Valid @PathVariable String componentName) {
        ComponentFeatureMetrics featureMetrics = featureMetricsService.getComponentFeatureMetrics(componentName);
        return ResponseEntity.ok().body(featureMetrics);
    }


    @RequestMapping(value = "/metrics/component/{componentName}/metric/{metricName}", method = GET, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<ComponentFeatureMetrics> getComponentMetricByType(@Valid @PathVariable String componentName,
                                                                            @Valid @PathVariable String metricName){
        ComponentFeatureMetrics featureMetrics = featureMetricsService.getComponentFeatureMetricByType(componentName, metricName);

        return ResponseEntity.ok().body(featureMetrics);
    }

    @RequestMapping(value = "metrics/application/{applicationName}", method = GET, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<ProductFeatureMetrics> getProductMetrics(@Valid @PathVariable String applicationName){

        ProductFeatureMetrics productFeatureMetrics = featureMetricsService.getProductFeatureMetrics(applicationName);
        return ResponseEntity.ok(productFeatureMetrics);
    }

    @RequestMapping(value = "metrics/application/{applicationName}/metric/{metricName}", method = GET, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<ProductFeatureMetrics> getProductMetricsByType(@Valid @PathVariable String applicationName
                                                                         ,@Valid @PathVariable String metricName){

        ProductFeatureMetrics productFeatureMetrics = featureMetricsService.getProductFeatureMetricsByType(applicationName,metricName);
        return ResponseEntity.ok(productFeatureMetrics);
    }

    @RequestMapping(value = "metrics/lob/{lobName}", method = GET, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<LobFeatureMetrics> getLobMetrics(@Valid @PathVariable String lobName){

        LobFeatureMetrics lobFeatureMetrics = featureMetricsService.getLobFeatureMetrics(lobName);
        return ResponseEntity.ok(lobFeatureMetrics);
    }

    @RequestMapping(value = "metrics/lob/{lobName}/metric/{type}", method = GET, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<LobFeatureMetrics> getLobMetricsByType(@Valid @PathVariable String lobName,
                                                                 @Valid @PathVariable String type){

        LobFeatureMetrics lobFeatureMetrics = featureMetricsService.getLobFeatureMetricsByType(lobName,type );
        return ResponseEntity.ok(lobFeatureMetrics);
    }

    @RequestMapping(value = "metrics/executive/{executiveName}", method = GET, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<ExecutiveFeatureMetrics> getExecutiveMetrics(@Valid @PathVariable String executiveName){

        ExecutiveFeatureMetrics executiveFeatureMetrics = featureMetricsService.getExecutiveFeatureMetrics(executiveName);
        return ResponseEntity.ok(executiveFeatureMetrics);
    }

    @RequestMapping(value = "metrics/executive/{executiveName}/metric/{type}", method = GET, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<ExecutiveFeatureMetrics> getExecutiveMetricsByType(@Valid @PathVariable String executiveName,
                                                                             @Valid @PathVariable String type){

        ExecutiveFeatureMetrics executiveFeatureMetrics = featureMetricsService.getExecutiveFeatureMetricsByType(executiveName, type);
        return ResponseEntity.ok(executiveFeatureMetrics);
    }
}
