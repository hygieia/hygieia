package com.capitalone.dashboard.rest;

import com.capitalone.dashboard.editors.CaseInsensitiveBuildStatusEditor;
import com.capitalone.dashboard.model.Build;
import com.capitalone.dashboard.model.BuildStatus;
import com.capitalone.dashboard.model.DataResponse;
import com.capitalone.dashboard.request.BuildRequest;
import com.capitalone.dashboard.service.BuildService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RestController
public class BuildController {

    private final BuildService buildService;

    private static final String JSON = MediaType.APPLICATION_JSON_VALUE;

    @Autowired
    public BuildController(BuildService buildService) {
        this.buildService = buildService;
    }

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(BuildStatus.class, new CaseInsensitiveBuildStatusEditor());
    }

    @RequestMapping(value = "/build", method = GET, produces = JSON)
    public DataResponse<Iterable<Build>> builds(@Valid BuildRequest request) {
        return buildService.search(request);
    }
}
