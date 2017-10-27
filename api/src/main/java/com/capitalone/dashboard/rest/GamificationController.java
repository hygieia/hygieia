package com.capitalone.dashboard.rest;

import com.capitalone.dashboard.misc.HygieiaException;
import com.capitalone.dashboard.model.GamificationMetric;
import com.capitalone.dashboard.service.GamificationService;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
public class GamificationController {

    private final GamificationService gamificationService;

    @Autowired
    public GamificationController(GamificationService gamificationService) {
        this.gamificationService = gamificationService;
    }

    @RequestMapping(value = "/gamification/metrics", method = GET, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Iterable<GamificationMetric>> getGamificationMetrics(@RequestParam(value = "enabled", required = false, defaultValue = "") Boolean enabled) {
        Iterable<GamificationMetric> gamificationMetricCollection = gamificationService.getGamificationMetrics(enabled);
        return ResponseEntity
                .ok()
                .body(gamificationMetricCollection);
    }

    @RequestMapping(value = "/gamification/metrics", method = POST,
            consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<String> saveGamificationMetrics(@Valid @RequestBody GamificationMetric gamificationMetric)
            throws ParseException, HygieiaException {
        GamificationMetric response = gamificationService.saveGamificationMetric(gamificationMetric);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body("Metric saved successfully : " + response.getId());
    }
}
