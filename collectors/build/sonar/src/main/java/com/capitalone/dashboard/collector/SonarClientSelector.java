package com.capitalone.dashboard.collector;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class SonarClientSelector {

    @Autowired
    private DefaultSonar6Client sonar6Client;
    @Autowired
    private DefaultSonar56Client sonar56Client;
    @Autowired
    @Qualifier("DefaultSonarClient")
    private DefaultSonarClient sonarClient;

    public SonarClient getSonarClient(Double version) {
        if(version != null && version == 5.6){
          return sonar56Client;
        }
        return ((version == null) || (version < 6.3)) ? sonarClient : sonar6Client;
    }
}
