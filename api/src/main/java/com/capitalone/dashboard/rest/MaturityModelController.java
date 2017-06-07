package com.capitalone.dashboard.rest;

import com.capitalone.dashboard.model.MaturityModel;
import com.capitalone.dashboard.service.MaturityModelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;


@RestController
public class MaturityModelController {

    private final MaturityModelService maturityModelService;


    @Autowired
    public MaturityModelController(MaturityModelService maturityModelService) {
        this.maturityModelService = maturityModelService;
    }


    @RequestMapping(value = "/maturityModel/profiles", method = GET, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<List<String>> getMaturiyModelProfiles() {
        return ResponseEntity.ok().body(maturityModelService.getProfiles());
    }

    @RequestMapping(value = "/maturityModel/profile/{profile}", method = GET, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<MaturityModel> getMaturiyModel(@PathVariable String profile) {
        return ResponseEntity.ok().body(maturityModelService.getMaturityModel(profile));
    }
}
