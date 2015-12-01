package com.capitalone.dashboard.rest;

import com.capitalone.dashboard.model.Commit;
import com.capitalone.dashboard.model.DataResponse;
import com.capitalone.dashboard.request.CommitRequest;
import com.capitalone.dashboard.service.CommitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RestController
public class CommitController {



    private final CommitService commitService;

    @Autowired
    public CommitController(CommitService commitService) {
        this.commitService = commitService;
    }

    @RequestMapping(value = "/commit", method = GET, produces = APPLICATION_JSON_VALUE)
    public DataResponse<Iterable<Commit>> builds(@Valid CommitRequest request) {
        return commitService.search(request);
    }
}
