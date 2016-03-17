package com.capitalone.dashboard.rest;

import com.capitalone.dashboard.misc.HygieiaException;
import com.capitalone.dashboard.model.Commit;
import com.capitalone.dashboard.model.DataResponse;
import com.capitalone.dashboard.request.CommitRequest;
import com.capitalone.dashboard.service.CommitService;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
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
public class CommitController {



    private final CommitService commitService;

    @Autowired
    public CommitController(CommitService commitService) {
        this.commitService = commitService;
    }

    @RequestMapping(value = "/commit", method = GET, produces = APPLICATION_JSON_VALUE)
    public DataResponse<Iterable<Commit>> search(@Valid CommitRequest request) {
        return commitService.search(request);
    }


    @RequestMapping(value = "/commit/github/v3", method = POST, consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<String> createGitHubv3(@RequestBody String request) throws ParseException, HygieiaException {

        JSONObject jsonObject = (JSONObject) new JSONParser().parse(request);
        String response = commitService.createFromGitHubv3(jsonObject);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }
}
