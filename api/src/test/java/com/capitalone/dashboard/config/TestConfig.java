package com.capitalone.dashboard.config;

import com.capitalone.dashboard.service.*;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Spring context configuration for Testing purposes
 */
@Configuration
public class TestConfig {

	@Bean
	public AuthenticationService authenticationService() {
		return Mockito.mock(AuthenticationService.class);
	}

	@Bean
	public DashboardService dashboardService() {
		return Mockito.mock(DashboardService.class);
	}

	@Bean
	public BuildService buildService() {
		return Mockito.mock(BuildService.class);
	}

	@Bean
	public CollectorService collectorService() {
		return Mockito.mock(CollectorService.class);
	}

	@Bean
	public ServiceService serviceService() {
		return Mockito.mock(ServiceService.class);
	}

	@Bean
	public DeployService deployService() {
		return Mockito.mock(DeployService.class);
	}

	@Bean
	public CodeQualityService codeQualityService() {
		return Mockito.mock(CodeQualityService.class);
	}

	@Bean
	public CommitService commitService() {
		return Mockito.mock(CommitService.class);
	}

	@Bean
	public TestResultService testResultService() {
		return Mockito.mock(TestResultService.class);
	}

	@Bean
	public FeatureService featureService() {
		return Mockito.mock(FeatureService.class);
	}

	@Bean
	public ScopeService scopeService() {
		return Mockito.mock(ScopeService.class);
	}
}
