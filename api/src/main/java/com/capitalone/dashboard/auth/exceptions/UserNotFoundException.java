package com.capitalone.dashboard.auth.exceptions;

import com.capitalone.dashboard.model.AuthType;

public class UserNotFoundException extends RuntimeException {

    private static final long serialVersionUID = -8596676033217258687L;
    
    private static final String MESSAGE = "No user found with name: %1$2s, and authorization type %2$2s.";

    public UserNotFoundException(String username, AuthType authType) { 
        super(String.format(MESSAGE, username, authType.name()));
    }

}
