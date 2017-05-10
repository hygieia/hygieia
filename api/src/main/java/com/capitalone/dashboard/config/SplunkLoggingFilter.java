package com.capitalone.dashboard.config;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SplunkLoggingFilter extends AbstractLoggingFilter<SplunkLog> {
    
    private static final Logger LOGGER = Logger.getLogger(SplunkLoggingFilter.class);
    
    @Value("${application.name}")
    private String appName;
    
    @Value("${version.number}")
    private String version;

    @Override
    protected SplunkLog getLogEntry(HttpServletRequest request, HttpServletResponse response) {
        
        SplunkLog log = new SplunkLog();
        log.with("REMOTE_ADDRESS", request.getRemoteAddr())
            .with("APPLICATION_NAME", appName)
            .with("APPLICATION_VERSION", version)
            .with("REQUEST_URL", request.getRequestURL().toString())
            .with("REQUEST_METHOD", request.getMethod())
            .with("STATUS_CODE", response.getStatus())
            ;
        
        HttpSession session = request.getSession(false);
        if(session != null) {
            log.with("SESSION_ID", session.getId());
        }
        
        Authentication user = SecurityContextHolder.getContext().getAuthentication();
        if(user != null) {
            log.with("USER_NAME", user.getPrincipal()).with("USER_DETAILS", user.getDetails().toString()).with("USER_AUTHORITIES", user.getAuthorities().toString());
        }
        
        
        return log;
    }

    @Override
    protected void log(SplunkLog log) {
        LOGGER.info(log);
    }

}
