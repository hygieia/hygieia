package com.capitalone.dashboard.collector;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SonarClientSelector {

    private final DefaultSonar6Client sonar6Client;
    private final DefaultSonarClient sonarClient;

    @Autowired
    public SonarClientSelector(DefaultSonar6Client sonar6Client, DefaultSonarClient sonarClient) {
        this.sonar6Client = sonar6Client;
        this.sonarClient = sonarClient;
    }

    public SonarClient getSonarClient(Double version) {
        return ((version == null) || (version < 6.0)) ? sonarClient : sonar6Client;
    }
}
