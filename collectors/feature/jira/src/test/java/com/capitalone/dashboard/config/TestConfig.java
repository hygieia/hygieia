package com.capitalone.dashboard.config;

import com.capitalone.dashboard.collector.FeatureSettings;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.TaskScheduler;

/**
 * Spring context configuration for Testing purposes
 */
@Order(1)
@Configuration
@ComponentScan(basePackages ={ "com.capitalone.dashboard.model","com.capitalone.dashboard.collector"}, includeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value=com.capitalone.dashboard.collector.FeatureSettings.class))
public class TestConfig {

    @Bean
    public FeatureSettings featureSettings() {
        FeatureSettings settings = new FeatureSettings();
        settings.setCron("* * * * * *");
        settings.setJiraBaseUrl("https://jira.com/");
        settings.setJiraTeamFieldName("customfield_11248");
        settings.setJiraSprintDataFieldName("customfield_10007");
        settings.setJiraEpicIdFieldName("customfield_10003");
        settings.setJiraStoryPointsFieldName("customfield_10004");
        settings.setMaxNumberOfFeaturesPerBoard(10000);
        settings.setJiraEpicId("6");
        settings.setJiraStoryId("7");


        return settings;
    }
    @Bean
    public TaskScheduler taskScheduler() { return Mockito.mock(TaskScheduler.class); }
}
