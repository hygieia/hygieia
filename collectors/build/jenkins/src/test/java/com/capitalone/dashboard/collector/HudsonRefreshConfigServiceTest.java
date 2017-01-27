package com.capitalone.dashboard.collector;

import static org.mockito.Mockito.verify;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.cloud.endpoint.RefreshEndpoint;

@RunWith(MockitoJUnitRunner.class)
public class HudsonRefreshConfigServiceTest {

	@Mock
	private RefreshEndpoint refreshEndpoint;
	
	@InjectMocks
	private HudsonRefreshConfigService service;
	
	@Test
	public void refershEndpoint(){
		service.refreshEndpoint();
		verify(refreshEndpoint).refresh();
	}
}
