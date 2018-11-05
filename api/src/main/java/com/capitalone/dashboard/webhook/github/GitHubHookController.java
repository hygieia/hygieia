package com.capitalone.dashboard.webhook.github;

import com.capitalone.dashboard.misc.HygieiaException;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.MalformedURLException;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
@RequestMapping("/webhook")
public class GitHubHookController {
    private final GitHubHookService gitHubHookService;

    @Autowired
    public GitHubHookController(GitHubHookService gitHubHookService) {
        this.gitHubHookService = gitHubHookService;
    }

    @RequestMapping(value = "/github/v3", method = POST,
            consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<String> createGitHubv3(@RequestBody JSONObject request) throws ParseException, HygieiaException, MalformedURLException {
        String response = gitHubHookService.createFromGitHubv3(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }
}
