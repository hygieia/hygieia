package com.capitalone.dashboard.rest;

import com.capitalone.dashboard.misc.HygieiaException;
import com.capitalone.dashboard.model.GitRepoData;
import com.capitalone.dashboard.model.DataResponse;
import com.capitalone.dashboard.request.GitreposRequest;
import com.capitalone.dashboard.service.GitreposService;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
public class GitreposController {



    private final GitreposService service;

    @Autowired
    public GitreposController(GitreposService commitService) {
        this.service = commitService;
    }

    @RequestMapping(value = "/repos", method = GET, produces = APPLICATION_JSON_VALUE)
    public DataResponse<Iterable<GitRepoData>> search(@Valid GitreposRequest request) {
        return service.search(request);
    }


    /*@RequestMapping(value = "/repos/github/v3", method = POST,
            consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<String> createGitHubv3(@RequestBody JSONObject request) throws ParseException, HygieiaException {
        String response = service.createFromGitHubv3(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }*/
}
