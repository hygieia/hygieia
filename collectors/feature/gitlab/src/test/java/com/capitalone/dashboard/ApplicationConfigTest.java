package com.capitalone.dashboard;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.springframework.web.client.RestOperations;

public class ApplicationConfigTest {
	
	private ApplicationConfig config = new ApplicationConfig();

	@Test
	public void shouldCreateRestTemplate() {
		RestOperations restOperations = config.restOperations();
		
		assertNotNull(restOperations);
		assertTrue(restOperations instanceof RestOperations);
	}

}
