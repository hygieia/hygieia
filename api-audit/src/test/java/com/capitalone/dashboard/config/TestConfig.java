package com.capitalone.dashboard.config;

import com.capitalone.dashboard.service.CodeReviewAuditService;
import com.capitalone.dashboard.service.DashboardAuditService;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Spring context configuration for Testing purposes
 */
@Configuration
public class TestConfig {

    @Bean
    public DashboardAuditService dashboardAuditService() {
        return Mockito.mock(DashboardAuditService.class);
    }

    @Bean
    public CodeReviewAuditService peerReviewAuditService() {
        return Mockito.mock(CodeReviewAuditService.class);
    }

}
