package com.capitalone.dashboard.collector;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.Matchers.instanceOf;
import static org.junit.Assert.assertThat;


@RunWith(MockitoJUnitRunner.class)
public class SonarClientSelectorTest {

    @InjectMocks
    private SonarClientSelector selector;
    @Mock
    private DefaultSonarClient defaultSonarClient;
    @Mock
    private DefaultSonar6Client defaultSonar6Client;

    @Test
    public void getSonarClient4() throws Exception {
        SonarClient sonarClient = selector.getSonarClient((double) 4);
        assertThat(sonarClient, instanceOf(DefaultSonarClient.class));
    }

    @Test
    public void getSonarClientNull() throws Exception {
        SonarClient sonarClient = selector.getSonarClient(null);
        assertThat(sonarClient, instanceOf(DefaultSonarClient.class));
    }

    @Test
    public void getSonarClient54() throws Exception {
        SonarClient sonarClient = selector.getSonarClient(5.4);
        assertThat(sonarClient, instanceOf(DefaultSonarClient.class));
    }

    @Test
    public void getSonarClient6() throws Exception {
        SonarClient sonarClient = selector.getSonarClient(6.31);
        assertThat(sonarClient, instanceOf(DefaultSonar6Client.class));
    }

}