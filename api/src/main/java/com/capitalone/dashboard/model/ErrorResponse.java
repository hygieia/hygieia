package com.capitalone.dashboard.model;

import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 */
public class ErrorResponse {
    private Map<String, List<String>> globalErrors = new HashMap<String, List<String>>();
    private Map<String, List<String>> fieldErrors = new HashMap<String, List<String>>();

    public Map<String, List<String>> getGlobalErrors() {
        return globalErrors;
    }

    public Map<String, List<String>> getFieldErrors() {
        return fieldErrors;
    }

    public void addFieldError(String field, String error) {
        List<String> errors = getFieldErrors().get(field);
        if (errors == null) {
            errors = new ArrayList<String>();
            getFieldErrors().put(field, errors);
        }
        errors.add(error);
    }

    public static ErrorResponse fromBindException(BindException bindException) {
        ErrorResponse errorResponse = new ErrorResponse();

        for (ObjectError objectError : bindException.getGlobalErrors()) {
            List<String> errors = errorResponse.getGlobalErrors().get(objectError.getObjectName());
            if (errors == null) {
                errors = new ArrayList<String>();
                errorResponse.getGlobalErrors().put(objectError.getObjectName(), errors);
            }
            errors.add(objectError.getDefaultMessage());
        }

        for (FieldError fieldError : bindException.getFieldErrors()) {
            List<String> errors = errorResponse.getFieldErrors().get(fieldError.getField());
            if (errors == null) {
                errors = new ArrayList<String>();
                errorResponse.getFieldErrors().put(fieldError.getField(), errors);
            }
            errors.add(fieldError.getDefaultMessage());
        }

        return errorResponse;
    }
}
