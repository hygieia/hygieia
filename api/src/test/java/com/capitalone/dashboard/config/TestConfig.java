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
	public Monitor2Service awsStatusService() {return Mockito.mock(Monitor2Service.class); }

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
	public CloudSubnetService cloudService() {
		return Mockito.mock(CloudSubnetService.class);
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
	
	@Bean
	public EncryptionService encryptionService() {
		return Mockito.mock(EncryptionService.class);
	}

    @Bean
    public BinaryArtifactService artifactService() {
        return Mockito.mock(BinaryArtifactService.class);
    }

	@Bean
	public PipelineService pipelineService() {
		return Mockito.mock(PipelineService.class);
	}
	
	@Bean
	public SystemConfigService systemConfigService() {
		return Mockito.mock(SystemConfigService.class);
	}

    @Bean
    public CloudInstanceService cloudInstanceService() {
        return Mockito.mock(CloudInstanceService.class);
    }

    @Bean
    public CloudSubnetService cloudSubnetService() {
        return Mockito.mock(CloudSubnetService.class);
    }

    @Bean
    public CloudVirtualNetworkService cloudVirtualNetworkService() {
        return Mockito.mock(CloudVirtualNetworkService.class);
    }

    @Bean
    public CloudVolumeService cloudVolumeService() {
        return Mockito.mock(CloudVolumeService.class);
    }
}
