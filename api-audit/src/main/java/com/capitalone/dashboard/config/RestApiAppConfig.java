
package com.capitalone.dashboard.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.RestController;

@Order(1)
@Configuration
@ComponentScan(
        excludeFilters = {
                @ComponentScan.Filter(RestController.class),
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = WebMVCConfig.class)
        },
        basePackages = "com.capitalone.dashboard"
)
public class RestApiAppConfig {

}
