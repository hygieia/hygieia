package com.capitalone.dashboard.rest;

import com.capitalone.dashboard.editors.CaseInsensitiveBuildStatusEditor;
import com.capitalone.dashboard.misc.HygieiaException;
import com.capitalone.dashboard.model.Build;
import com.capitalone.dashboard.response.BuildDataCreateResponse;
import com.capitalone.dashboard.model.BuildStatus;
import com.capitalone.dashboard.model.DataResponse;
import com.capitalone.dashboard.request.BuildDataCreateRequest;
import com.capitalone.dashboard.request.BuildSearchRequest;
import com.capitalone.dashboard.service.BuildService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
public class BuildController {

    private final BuildService buildService;

    @Autowired
    public BuildController(BuildService buildService) {
        this.buildService = buildService;
    }

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(BuildStatus.class, new CaseInsensitiveBuildStatusEditor());
    }

    @RequestMapping(value = "/build", method = GET, produces = APPLICATION_JSON_VALUE)
    public DataResponse<Iterable<Build>> builds(@Valid BuildSearchRequest request) {
        return buildService.search(request);
    }

    @RequestMapping(value = "/build", method = POST,
            consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<String> createBuild(@Valid @RequestBody BuildDataCreateRequest request) throws HygieiaException {
        String response = buildService.create(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @RequestMapping(value = "/v2/build", method = POST,
            consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<String> createBuildv2(@Valid @RequestBody BuildDataCreateRequest request) throws HygieiaException {
        String response = buildService.createV2(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @RequestMapping(value = "/v3/build", method = POST,
            consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<BuildDataCreateResponse> createBuildv3(@Valid @RequestBody BuildDataCreateRequest request) throws HygieiaException {
        BuildDataCreateResponse response = buildService.createV3(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }
}
