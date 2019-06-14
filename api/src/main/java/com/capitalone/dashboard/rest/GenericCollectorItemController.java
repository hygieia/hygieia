package com.capitalone.dashboard.rest;

import com.capitalone.dashboard.misc.HygieiaException;
import com.capitalone.dashboard.request.GenericCollectorItemCreateRequest;
import com.capitalone.dashboard.service.GenericCollectorItemService;
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
public class GenericCollectorItemController {

    private final GenericCollectorItemService genericCollectorItemService;

    @Autowired
    public GenericCollectorItemController(GenericCollectorItemService genericCollectorItemService) {
        this.genericCollectorItemService = genericCollectorItemService;
    }


    @RequestMapping(value = "/generic-item", method = POST,
            consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<String> createGenericItem (@Valid @RequestBody GenericCollectorItemCreateRequest request) throws HygieiaException {
        String response = genericCollectorItemService.create(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @RequestMapping(value = "/generic-binary-artifact", method = POST,
            consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<String> createGenericBinaryArtifact (@Valid @RequestBody GenericCollectorItemCreateRequest request) throws HygieiaException {
        String response = genericCollectorItemService.createGenericBinaryArtifactData(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

}
