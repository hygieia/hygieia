package com.capitalone.dashboard.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

import com.capitalone.dashboard.logging.DatabaseLoggingCondition;
import com.capitalone.dashboard.logging.LoggingFilter;
import com.capitalone.dashboard.logging.SplunkConnectionLoggingFilter;
import com.capitalone.dashboard.logging.SplunkLoggingCondition;

@Configuration
public class LoggingConfig {
    
    @Bean
    @Conditional(SplunkLoggingCondition.class)
    public SplunkConnectionLoggingFilter splunkConnectionLoggingFilter() {
        return new SplunkConnectionLoggingFilter();
    }
    
    @Bean
    @Conditional(DatabaseLoggingCondition.class)
    public LoggingFilter loggingFilter() {
        return new LoggingFilter();
    }

}
