/*************************DA-BOARD-LICENSE-START*********************************
 * Copyright 2014 CapitalOne, LLC.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *************************DA-BOARD-LICENSE-END*********************************/

package com.capitalone.dashboard.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.RestController;

import com.capitalone.dashboard.repository.CollectorItemRepository;
import com.capitalone.dashboard.repository.DashboardRepository;
import com.capitalone.dashboard.repository.PipelineRepository;
import com.capitalone.dashboard.service.BinaryArtifactService;
import com.capitalone.dashboard.service.BuildService;
import com.capitalone.dashboard.service.CommitService;
import com.capitalone.dashboard.service.DeployService;
import com.capitalone.dashboard.service.PipelineService;
import com.capitalone.dashboard.service.DynamicPipelineServiceImpl;
import com.capitalone.dashboard.service.PipelineServiceImpl;

@Order(1)
@Configuration
@ComponentScan(
        excludeFilters = {
                @ComponentScan.Filter( RestController.class ),
                @ComponentScan.Filter( type = FilterType.ASSIGNABLE_TYPE, value = WebMVCConfig.class )
        },
        basePackages = "com.capitalone.dashboard"
)
public class RestApiAppConfig {
	
	@Value("${feature.dynamicPipeline:disabled}")
    private String featureDynamicPipeline;
	
	@Bean PipelineService pipelineService(PipelineRepository pipelineRepository, DashboardRepository dashboardRepository,
			CollectorItemRepository collectorItemRepository, BinaryArtifactService binaryArtifactService,
			BuildService buildService, CommitService commitService, DeployService deployService) {
		if (featureEnabled(featureDynamicPipeline)) {
			return new DynamicPipelineServiceImpl(pipelineRepository, dashboardRepository,
					collectorItemRepository, binaryArtifactService,
					buildService, commitService, deployService);
		} else {
			return new PipelineServiceImpl(pipelineRepository, dashboardRepository, collectorItemRepository);
		}
	}
	
	private boolean featureEnabled(String featureValue) {
		return "enable".equalsIgnoreCase(featureValue)
				|| "enabled".equalsIgnoreCase(featureValue)
				|| "true".equalsIgnoreCase(featureValue);
	}
}
