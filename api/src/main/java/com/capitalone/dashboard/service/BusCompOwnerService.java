package com.capitalone.dashboard.service;

import org.springframework.security.core.Authentication;

public interface BusCompOwnerService {

    void assignOwnerToDashboards(String firstName, String middleName, String lastName, Authentication authentication);
}
