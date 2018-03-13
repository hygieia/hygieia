package com.capitalone.dashboard.rest;

import com.capitalone.dashboard.model.DataResponse;
import com.capitalone.dashboard.model.score.ScoreMetric;
import com.capitalone.dashboard.service.ScoreService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RestController
public class ScoreController {

    private final ScoreService scoreService;

    @Autowired
    public ScoreController(ScoreService scoreService) {
        this.scoreService = scoreService;
    }

    @RequestMapping(value = "/score/metric/{dashboardId}", method = GET, produces = APPLICATION_JSON_VALUE)
    public DataResponse<ScoreMetric> scoreMetric(@PathVariable ObjectId dashboardId) {
        return scoreService.getScoreMetric(dashboardId);
    }

}
