package com.capitalone.dashboard.exception;

import com.capitalone.dashboard.model.AuditException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ExceptionHandlerAdvice {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExceptionHandlerAdvice.class);
    @ExceptionHandler(AuditException.class)
    public ResponseEntity<String> handleException(AuditException e) {
        LOGGER.error("Bad request: " + e.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body("Bad request: " + e.getMessage());
    }
}



