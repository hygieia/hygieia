package com.capitalone.dashboard.rest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import io.swagger.annotations.ApiOperation;

@RestController
public class PingController {

    public PingController() {

    }

    @RequestMapping(value = "/ping", method = GET, produces = APPLICATION_JSON_VALUE)
	@ApiOperation(value = "Ping Controller", notes = "This endpoint verifies from the web browser that the Audit API is running successfully.")
    public ResponseEntity<String> ping () {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body("hello audit");
    }

}
