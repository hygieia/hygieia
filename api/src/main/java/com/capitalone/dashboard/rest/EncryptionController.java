package com.capitalone.dashboard.rest;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.capitalone.dashboard.request.EncryptionRequest;
import com.capitalone.dashboard.service.EncryptionService;

@RestController
public class EncryptionController {
	private final EncryptionService encryptionService;
	private static final String JSON = MediaType.APPLICATION_JSON_VALUE;
	
	@Autowired
    public EncryptionController(EncryptionService encryptionService) {
        this.encryptionService = encryptionService;
    }
	
    @RequestMapping(value = "/encrypt/{message}", method = GET, produces = JSON)
    public ResponseEntity<String> encrypt(@Valid EncryptionRequest request) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(encryptionService.encrypt(request.getMessage()));
    }
}

