package com.capitalone.dashboard.rest;

import com.capitalone.dashboard.model.PipelineResponse;
import com.capitalone.dashboard.request.PipelineSearchRequest;
import com.capitalone.dashboard.service.PipelineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RestController
public class PipelineController {
    private final PipelineService pipelineService;

    @Autowired
    public PipelineController(PipelineService pipelineService) {
        this.pipelineService = pipelineService;
    }

    @RequestMapping(value = "/pipeline", method = GET, produces = APPLICATION_JSON_VALUE)
    public Iterable<PipelineResponse> searchPipelines(@Valid PipelineSearchRequest searchRequest) {
        return pipelineService.search(searchRequest);
    }
}
