package com.capitalone.dashboard.config;

import org.springframework.context.annotation.Configuration;

/**
 * Configuration class that creates asynchronous task executor to handle application events
 */
@Configuration
public class ApplicationEventConfiguration {

//    @Bean
//    public ApplicationEventMulticaster applicationEventMulticaster(BeanFactory beanFactory) {
//        SimpleApplicationEventMulticaster multicaster = new SimpleApplicationEventMulticaster(beanFactory);
//        // TODO - Consider using a ThreadPoolTaskExecutor!
//        multicaster.setTaskExecutor(new SimpleAsyncTaskExecutor());
//        return multicaster;
//    }
}
