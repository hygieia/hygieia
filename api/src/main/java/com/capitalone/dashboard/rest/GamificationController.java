package com.capitalone.dashboard.rest;

import com.capitalone.dashboard.model.GamificationMetric;
import com.capitalone.dashboard.service.GamificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RestController
public class GamificationController {

    private final GamificationService gamificationService;

    @Autowired
    public GamificationController(GamificationService gamificationService) {
        this.gamificationService = gamificationService;
    }

    //@PathVariable String metricName, @PathVariable boolean enabled
    @RequestMapping(value = "/gamification/metrics", method = GET, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Iterable<GamificationMetric>> getGamificationMetrics() {
        Collection<GamificationMetric> gamificationMetricCollection = gamificationService.getGamificationMetrics();
        return ResponseEntity
                .ok()
                .body(gamificationMetricCollection);
    }

}
