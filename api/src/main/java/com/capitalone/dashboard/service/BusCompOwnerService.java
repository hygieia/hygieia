package com.capitalone.dashboard.service;

import org.springframework.security.core.userdetails.UserDetails;

public interface BusCompOwnerService {

    String assignOwnerToDashboards(String firstName, String middleName, String lastName, UserDetails user);
}
