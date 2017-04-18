package com.capitalone.dashboard.auth.exceptions;

public class DeleteLastAdminException extends RuntimeException {

    private static final long serialVersionUID = -4670507474875127809L;
    private static final String MESSAGE = "There must always be at least one admin";

    public DeleteLastAdminException() {
        super(MESSAGE);
    }

}
