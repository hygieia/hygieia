package com.capitalone.dashboard.rest;

import com.capitalone.dashboard.misc.HygieiaException;
import com.capitalone.dashboard.request.MetadataCreateRequest;
import com.capitalone.dashboard.service.MetadataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.POST;


@RestController
public class MetadataController {

    private final MetadataService metadataService;

    @Autowired
    public MetadataController(MetadataService metadataService) {
        this.metadataService = metadataService;
    }

    @RequestMapping(value = "/metadata/create", method = POST,
            consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<String> create(@Valid @RequestBody MetadataCreateRequest request) throws HygieiaException {
        String response = metadataService.create(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

}
