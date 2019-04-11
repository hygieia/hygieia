package com.capitalone.dashboard.config;

import com.capitalone.dashboard.ApiSettings;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.core.annotation.Order;

import java.util.Arrays;
import java.util.List;

/**
 * Spring context configuration for Testing purposes
 */
@Order(1)
@Configuration
@ComponentScan(basePackages ={ "com.capitalone.dashboard.evaluator","com.capitalone.dashboard.service", "com.capitalone.dashboard.model"}, includeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value=com.capitalone.dashboard.ApiSettings.class))
public class TestConfig {

    @Bean
    public ApiSettings settings() {
        ApiSettings settings = new ApiSettings();
        settings.setPeerReviewContexts("approvals/lgtmeow");
        settings.setFeatureIDPattern("((?<!([A-Za-z]{1,10})-?)[A-Z]+-\\\\d+)");
        settings.setTraceabilityThreshold(80);
        List<String> validStoryStatus = Arrays.asList("ACCEPTED", "DONE" ,"RESOLVED" ,"CLOSED");
        settings.setValidStoryStatus(validStoryStatus);
        return settings;
    }

/*    @Bean
    public DashboardAuditService dashboardAuditService() {
        return Mockito.mock(DashboardAuditService.class);
    }*/
}
