package com.capitalone.dashboard;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(ConfigServiceApplication.class)
@TestPropertySource(locations = {"classpath:test.properties"})
public class ConfigServiceApplicationTest {

	@Test
    public void contextLoads() throws Exception {}
	
}
