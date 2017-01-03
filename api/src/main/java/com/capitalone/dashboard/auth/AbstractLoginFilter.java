package com.capitalone.dashboard.auth;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.Assert;
import org.springframework.web.filter.OncePerRequestFilter;

public abstract class AbstractLoginFilter extends OncePerRequestFilter {

	private RequestMatcher requestMatcher;

    public AbstractLoginFilter(String path) {
    	Assert.notNull(path, "path cannot be null");
        this.requestMatcher = new AntPathRequestMatcher(path);
    }
	
	@Override
	public void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain)
			throws IOException, ServletException {
		
		if (requestMatcher.matches(httpServletRequest)) {
			SecurityContextHolder.getContext().setAuthentication(createAuthentication(httpServletRequest));
		} else {
			filterChain.doFilter(httpServletRequest, httpServletResponse);
		}
		
	}
	
	public abstract Authentication createAuthentication(HttpServletRequest httpServletRequest);

}
