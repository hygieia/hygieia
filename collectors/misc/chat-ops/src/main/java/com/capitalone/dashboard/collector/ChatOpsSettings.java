package com.capitalone.dashboard.collector;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Bean to hold settings specific to the UDeploy collector.
 */
@Component
@ConfigurationProperties(prefix = "chatops")
public class ChatOpsSettings {
    private String cron;
    


	public String getCron() {
        return cron;
    }

    public void setCron(String cron) {
        this.cron = cron;
    }

  


}
