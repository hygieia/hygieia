package com.capitalone.dashboard.logging;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.capitalone.dashboard.model.SplunkLog;

public class SplunkConnectionLoggingFilter implements Filter {
    
    private static final Logger LOGGER = Logger.getLogger(SplunkConnectionLoggingFilter.class);
    
    @Value("${application.name}")
    private String appName;
    
    @Value("${version.number}")
    private String version;
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequestWrapper requestWrapper = new HttpServletRequestWrapper((HttpServletRequest)request);
        HttpServletResponseWrapper responseWrapper = new HttpServletResponseWrapper((HttpServletResponse) response);
        
        chain.doFilter(request, response);
        
        SplunkLog log = getLogEntry(requestWrapper, responseWrapper);
        
        LOGGER.info(log.toString());
    }

    private SplunkLog getLogEntry(HttpServletRequest request, HttpServletResponse response) {
        
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
    public void init(FilterConfig filterConfig) throws ServletException {
        // noop
    }

    @Override
    public void destroy() {
        // noop
        
    }

}
