package com.capitalone.dashboard.collector.config;

import com.capitalone.dashboard.ApiSettings;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.core.annotation.Order;

/**
 * Spring context configuration for Testing purposes
 */
@Order(1)
@Configuration
@ComponentScan(basePackages ={ "com.capitalone.dashboard.evaluator","com.capitalone.dashboard.service", "com.capitalone.dashboard.model"}, includeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value=ApiSettings.class))
public class TestConfig {

    @Bean
    public ApiSettings settings() {
        ApiSettings settings = new ApiSettings();
        return settings;
    }
}
