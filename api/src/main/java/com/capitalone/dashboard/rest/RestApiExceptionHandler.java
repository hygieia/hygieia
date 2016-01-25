package com.capitalone.dashboard.rest;

import com.capitalone.dashboard.Application;
import com.capitalone.dashboard.misc.HygieiaException;
import com.capitalone.dashboard.model.ErrorResponse;
import com.capitalone.dashboard.util.UnsafeDeleteException;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * Controller advice to handle exceptions globally.
 */
@EnableWebMvc
@ControllerAdvice(annotations = RestController.class, basePackageClasses = Application.class)
public class RestApiExceptionHandler extends ResponseEntityExceptionHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(RestApiExceptionHandler.class);

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body, HttpHeaders headers,
                                                             HttpStatus status, WebRequest request) {
        headers.setContentType(MediaType.APPLICATION_JSON);
        LOGGER.error(ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body((Object) "Internal error");
    }

    @Override
    protected ResponseEntity<Object> handleBindException(BindException ex, HttpHeaders headers, HttpStatus status,
                                                         WebRequest request) {
        headers.setContentType(MediaType.APPLICATION_JSON);
        LOGGER.warn("Bad Request - bind exception: ", ex);
        return new ResponseEntity<Object>(ErrorResponse.fromBindException(ex), headers, status);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers, HttpStatus status,
                                                                  WebRequest request) {
        return handleBindException(new BindException(ex.getBindingResult()), headers, status, request);
    }

    /**
     * TODO - Figure out why this method is not being called
     */
    @ExceptionHandler(UnrecognizedPropertyException.class)
    public ResponseEntity<?> handleUnrecognizedProperty(UnrecognizedPropertyException ex, HttpServletRequest request) {
        ErrorResponse response = new ErrorResponse();
        response.addFieldError(ex.getPropertyName(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(UnsafeDeleteException.class)
    protected ResponseEntity<?> handleUnsafeDelete(UnsafeDeleteException ex,  HttpServletRequest request) {
        LOGGER.error(ex.getMessage());
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("errorMessage", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Object> handleConflict(RuntimeException e) {
        LOGGER.error(e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body((Object) "Internal error.");
    }

    @ExceptionHandler(HygieiaException.class)
    public ResponseEntity<Object> handleHygieiaException(HygieiaException e) {
        LOGGER.error(e.getMessage(), e);
        switch (e.getErrorCode()) {
            case HygieiaException.ERROR_INSERTING_DATA:
            case HygieiaException.COLLECTOR_CREATE_ERROR:
            case HygieiaException.COLLECTOR_ITEM_CREATE_ERROR:
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body((Object) ("Internal error." + e.getMessage() + "Error code=: " + e.getErrorCode()));
            case HygieiaException.DUPLICATE_DATA:
            case HygieiaException.JSON_FORMAT_ERROR:
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body((Object) ("Bad request. " + e.getMessage() + "Error code=: " + e.getErrorCode()));
            case HygieiaException.NOTHING_TO_UPDATE:
                return ResponseEntity.status(HttpStatus.NOT_MODIFIED).body((Object) ("Internal error. " + e.getMessage() + "Error code=: " + e.getErrorCode()));
            default:
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body((Object) ("Internal error. " + e.getMessage() + "Error code=: " + e.getErrorCode()));
        }
    }
}
