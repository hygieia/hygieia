package com.capitalone.dashboard.rest;

import static org.springframework.http.MediaType.*;
import static org.springframework.web.bind.annotation.RequestMethod.*;

import java.util.List;

import javax.validation.Valid;

import com.capitalone.dashboard.misc.HygieiaException;
import com.capitalone.dashboard.model.DataResponse;
import com.capitalone.dashboard.model.GitRequest;
import com.capitalone.dashboard.model.pullrequest.PullRequest;
import com.capitalone.dashboard.request.GitRequestRequest;
import com.capitalone.dashboard.service.GitRequestService;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GitRequestController {


    private final GitRequestService gitRequestService;

    @Autowired
    public GitRequestController(GitRequestService gitRequestService) {
        this.gitRequestService = gitRequestService;
    }

    @RequestMapping(value = "/gitrequests/type/{type}/state/{state}", method = GET, produces = APPLICATION_JSON_VALUE)
    public DataResponse<Iterable<GitRequest>> search(@Valid GitRequestRequest request,
                                                     @PathVariable String type,
                                                     @PathVariable String state) {
        return gitRequestService.search(request, type, state);
    }

    @RequestMapping(value = "/gitrequests/github/v3", method = POST,
            consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<String> createGitHubv3(@RequestBody JSONObject request)
            throws ParseException, HygieiaException {
        String response = gitRequestService.createFromGitHubv3(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @RequestMapping(value = "/pending-pull-requests/{collectorId}/{repoName}", method = GET, produces = APPLICATION_JSON_VALUE)
    public List<PullRequest> pendingPullRequestsByCollectorIdAndRepoName(
            @PathVariable String collectorId,
            @PathVariable String repoName) {
        return this.gitRequestService.getPullRequestsByRepoName(repoName);
    }

    @RequestMapping(value = "/pending-pull-requests/{repoName}", method = GET, produces = APPLICATION_JSON_VALUE)
    public List<PullRequest> pendingPullRequestsByRepoName(
            @PathVariable String repoName) {
        return this.gitRequestService.getPullRequestsByRepoName(repoName);
    }

}