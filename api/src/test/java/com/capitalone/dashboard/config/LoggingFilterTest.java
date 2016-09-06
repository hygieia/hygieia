package com.capitalone.dashboard.config;

import com.capitalone.dashboard.ApiSettings;
import com.capitalone.dashboard.model.RequestLog;
import com.capitalone.dashboard.repository.RequestLogRepository;
import org.apache.log4j.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;

import javax.servlet.FilterChain;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.class)
public class LoggingFilterTest {

    @Mock
    private RequestLogRepository requestLogRepository;

    @Mock
    private Logger logger;
    @InjectMocks
    @Autowired
    private LoggingFilter loggingFilter;

    @Mock
    private ApiSettings settings;
    @Test
    public void testDoFilterPut() throws Exception {
        HttpServletRequest httpServletRequest = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse httpServletResponse =  Mockito.mock(HttpServletResponse.class);
        FilterChain filterChain =  Mockito.mock(FilterChain.class);
        when(httpServletRequest.getInputStream()).thenReturn(Mockito.mock(ServletInputStream.class));
        when(httpServletRequest.getRequestURI()).thenReturn("Success");
        when(httpServletRequest.getMethod()).thenReturn(HttpMethod.PUT.toString());
        when(settings.isLogRequest()).thenReturn(true);
        when(requestLogRepository.save(any(RequestLog.class))).thenReturn(new RequestLog());
        when(httpServletRequest.getContentType()).thenReturn("application/json;charset=UTF-8");
        when(httpServletResponse.getContentType()).thenReturn("application/json;charset=UTF-8");
        loggingFilter.doFilter(httpServletRequest, httpServletResponse,
                filterChain);
        verify(requestLogRepository, times(1)).save(any(RequestLog.class));
    }


    @Test
    public void testDoFilterGet() throws Exception {
        HttpServletRequest httpServletRequest = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse httpServletResponse =  Mockito.mock(HttpServletResponse.class);
        FilterChain filterChain =  Mockito.mock(FilterChain.class);
        when(httpServletRequest.getInputStream()).thenReturn(Mockito.mock(ServletInputStream.class));
        when(httpServletRequest.getRequestURI()).thenReturn("Success");
        when(httpServletRequest.getMethod()).thenReturn(HttpMethod.GET.toString());
        when(requestLogRepository.save(any(RequestLog.class))).thenReturn(new RequestLog());
        when(httpServletRequest.getContentType()).thenReturn("application/json;charset=UTF-8");
        when(httpServletResponse.getContentType()).thenReturn("application/json;charset=UTF-8");
        when(settings.isLogRequest()).thenReturn(true);
        loggingFilter.doFilter(httpServletRequest, httpServletResponse,
                filterChain);
        verify(requestLogRepository, times(0)).save(any(RequestLog.class));
    }


    @Test
    public void testDoFilterPost() throws Exception {
        HttpServletRequest httpServletRequest = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse httpServletResponse =  Mockito.mock(HttpServletResponse.class);
        FilterChain filterChain =  Mockito.mock(FilterChain.class);
        when(httpServletRequest.getInputStream()).thenReturn(Mockito.mock(ServletInputStream.class));
        when(httpServletRequest.getRequestURI()).thenReturn("Success");
        when(httpServletRequest.getMethod()).thenReturn(HttpMethod.POST.toString());
        when(requestLogRepository.save(any(RequestLog.class))).thenReturn(new RequestLog());
        when(httpServletRequest.getContentType()).thenReturn("application/json;charset=UTF-8");
        when(httpServletResponse.getContentType()).thenReturn("application/json;charset=UTF-8");
        when(settings.isLogRequest()).thenReturn(true);
        loggingFilter.doFilter(httpServletRequest, httpServletResponse,
                filterChain);
        verify(requestLogRepository, times(1)).save(any(RequestLog.class));
    }



    @Test
    public void testDoFilterDelete() throws Exception {
        HttpServletRequest httpServletRequest = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse httpServletResponse =  Mockito.mock(HttpServletResponse.class);
        FilterChain filterChain =  Mockito.mock(FilterChain.class);
        when(httpServletRequest.getInputStream()).thenReturn(Mockito.mock(ServletInputStream.class));
        when(httpServletRequest.getRequestURI()).thenReturn("Success");
        when(httpServletRequest.getMethod()).thenReturn(HttpMethod.DELETE.toString());
        when(requestLogRepository.save(any(RequestLog.class))).thenReturn(new RequestLog());
        when(httpServletRequest.getContentType()).thenReturn("application/json;charset=UTF-8");
        when(httpServletResponse.getContentType()).thenReturn("application/json;charset=UTF-8");
        when(settings.isLogRequest()).thenReturn(true);
        loggingFilter.doFilter(httpServletRequest, httpServletResponse,
                filterChain);
        verify(requestLogRepository, times(1)).save(any(RequestLog.class));
    }

    @Test
    public void testDoFilterPutSettingOff() throws Exception {
        HttpServletRequest httpServletRequest = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse httpServletResponse =  Mockito.mock(HttpServletResponse.class);
        FilterChain filterChain =  Mockito.mock(FilterChain.class);
        when(httpServletRequest.getInputStream()).thenReturn(Mockito.mock(ServletInputStream.class));
        when(httpServletRequest.getRequestURI()).thenReturn("Success");
        when(httpServletRequest.getMethod()).thenReturn(HttpMethod.PUT.toString());
        when(settings.isLogRequest()).thenReturn(false);
        when(requestLogRepository.save(any(RequestLog.class))).thenReturn(new RequestLog());
        when(httpServletRequest.getContentType()).thenReturn("application/json;charset=UTF-8");
        when(httpServletResponse.getContentType()).thenReturn("application/json;charset=UTF-8");
        loggingFilter.doFilter(httpServletRequest, httpServletResponse,
                filterChain);

        verify(requestLogRepository, times(0)).save(any(RequestLog.class));
    }


    @Test
    public void testDoFilterGetSettingsOff() throws Exception {
        HttpServletRequest httpServletRequest = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse httpServletResponse =  Mockito.mock(HttpServletResponse.class);
        FilterChain filterChain =  Mockito.mock(FilterChain.class);
        when(httpServletRequest.getInputStream()).thenReturn(Mockito.mock(ServletInputStream.class));
        when(httpServletRequest.getRequestURI()).thenReturn("Success");
        when(httpServletRequest.getMethod()).thenReturn(HttpMethod.GET.toString());
        when(requestLogRepository.save(any(RequestLog.class))).thenReturn(new RequestLog());
        when(httpServletRequest.getContentType()).thenReturn("application/json;charset=UTF-8");
        when(httpServletResponse.getContentType()).thenReturn("application/json;charset=UTF-8");
        when(settings.isLogRequest()).thenReturn(false);
        loggingFilter.doFilter(httpServletRequest, httpServletResponse,
                filterChain);
        verify(requestLogRepository, times(0)).save(any(RequestLog.class));
    }


    @Test
    public void testDoFilterPostSettingsOff() throws Exception {
        HttpServletRequest httpServletRequest = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse httpServletResponse =  Mockito.mock(HttpServletResponse.class);
        FilterChain filterChain =  Mockito.mock(FilterChain.class);
        when(httpServletRequest.getInputStream()).thenReturn(Mockito.mock(ServletInputStream.class));
        when(httpServletRequest.getRequestURI()).thenReturn("Success");
        when(httpServletRequest.getMethod()).thenReturn(HttpMethod.POST.toString());
        when(requestLogRepository.save(any(RequestLog.class))).thenReturn(new RequestLog());
        when(httpServletRequest.getContentType()).thenReturn("application/json;charset=UTF-8");
        when(httpServletResponse.getContentType()).thenReturn("application/json;charset=UTF-8");
        when(settings.isLogRequest()).thenReturn(false);
        loggingFilter.doFilter(httpServletRequest, httpServletResponse,
                filterChain);
        verify(requestLogRepository, times(0)).save(any(RequestLog.class));
    }



    @Test
    public void testDoFilterDeleteSettingsOff() throws Exception {
        HttpServletRequest httpServletRequest = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse httpServletResponse =  Mockito.mock(HttpServletResponse.class);
        FilterChain filterChain =  Mockito.mock(FilterChain.class);
        when(httpServletRequest.getInputStream()).thenReturn(Mockito.mock(ServletInputStream.class));
        when(httpServletRequest.getRequestURI()).thenReturn("Success");
        when(httpServletRequest.getMethod()).thenReturn(HttpMethod.DELETE.toString());
        when(requestLogRepository.save(any(RequestLog.class))).thenReturn(new RequestLog());
        when(httpServletRequest.getContentType()).thenReturn("application/json;charset=UTF-8");
        when(httpServletResponse.getContentType()).thenReturn("application/json;charset=UTF-8");
        when(settings.isLogRequest()).thenReturn(false);
        loggingFilter.doFilter(httpServletRequest, httpServletResponse,
                filterChain);
        verify(requestLogRepository, times(0)).save(any(RequestLog.class));
    }
}