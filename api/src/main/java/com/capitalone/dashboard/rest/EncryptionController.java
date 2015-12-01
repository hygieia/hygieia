package com.capitalone.dashboard.rest;

import com.capitalone.dashboard.request.EncryptionRequest;
import com.capitalone.dashboard.service.EncryptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RestController
public class EncryptionController {
	private final EncryptionService encryptionService;

	@Autowired
    public EncryptionController(EncryptionService encryptionService) {
        this.encryptionService = encryptionService;
    }

    @RequestMapping(value = "/encrypt/{message}", method = GET, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<String> encrypt(@Valid EncryptionRequest request) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(encryptionService.encrypt(request.getMessage()));
    }
}

