package com.capitalone.dashboard.config;

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

import com.capitalone.dashboard.model.LogEntry;

public abstract class AbstractLoggingFilter<T extends LogEntry> implements Filter {
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequestWrapper requestWrapper = new HttpServletRequestWrapper((HttpServletRequest)request);
        HttpServletResponseWrapper responseWrapper = new HttpServletResponseWrapper((HttpServletResponse) response);
        
        chain.doFilter(request, response);
        
        T log = getLogEntry(requestWrapper, responseWrapper);
        
        log(log); 
    }


    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // noop
        
    }

    @Override
    public void destroy() {
        // noop
        
    }
    
    protected abstract T getLogEntry(HttpServletRequest request, HttpServletResponse response);
    
    protected abstract void log(T log);

}
