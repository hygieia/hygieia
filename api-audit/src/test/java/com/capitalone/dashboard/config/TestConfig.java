package com.capitalone.dashboard.config;

import com.capitalone.dashboard.service.BuildAuditService;
import com.capitalone.dashboard.service.CodeQualityAuditService;
import com.capitalone.dashboard.service.DashboardAuditService;
import com.capitalone.dashboard.service.PeerReviewAuditService;
import com.capitalone.dashboard.service.TestResultAuditService;
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
    public PeerReviewAuditService peerReviewAuditService() {
        return Mockito.mock(PeerReviewAuditService.class);
    }


    @Bean
    public BuildAuditService buildAuditService() {
        return Mockito.mock(BuildAuditService.class);
    }

    @Bean
    public CodeQualityAuditService codeQualityAuditService() {
        return Mockito.mock(CodeQualityAuditService.class);
    }

    @Bean
    public TestResultAuditService  testResultAuditService() {
        return Mockito.mock(TestResultAuditService.class);
    }
}
