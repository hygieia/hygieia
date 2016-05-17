package com.capitalone.dashboard.rest;

import com.capitalone.dashboard.model.Pull;
import com.capitalone.dashboard.model.DataResponse;
import com.capitalone.dashboard.request.PullRequest;
import com.capitalone.dashboard.service.PullService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RestController
public class PullController {



    private final PullService pullService;

    @Autowired
    public PullController(PullService pullService) {
        this.pullService = pullService;
    }

    @RequestMapping(value = "/pulls", method = GET, produces = APPLICATION_JSON_VALUE)
    public DataResponse<Iterable<Pull>> search(@Valid PullRequest request) {
        return pullService.search(request);
    }

    @RequestMapping(value = "/pullsMerged", method = GET, produces = APPLICATION_JSON_VALUE)
    public DataResponse<Iterable<Pull>> searchMerged(@Valid PullRequest request) {
        return pullService.search(request);
    }

}
