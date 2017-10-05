package com.capitalone.dashboard.config;

import com.capitalone.dashboard.service.AuditService;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Spring context configuration for Testing purposes
 */
@Configuration
public class TestConfig {

    @Bean
    public AuditService auditService() {
        return Mockito.mock(AuditService.class);
    }
}
