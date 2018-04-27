package com.capitalone.dashboard.service;

import static org.mockito.Mockito.times;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.bson.types.ObjectId;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.capitalone.dashboard.model.Configuration;
import com.capitalone.dashboard.repository.ConfigurationRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;


@RunWith(MockitoJUnitRunner.class)
public class ConfigurationServiceTest {
	@Mock 
	ConfigurationRepository configurationRepository;
	
	@InjectMocks
	ConfigurationServiceImpl service;
	
	@Captor 
	ArgumentCaptor<Configuration> configurationCaptor;
	

	@Test
	public void testCreate() throws Exception {
		List<Configuration> createRequest = getObjectFromJson("configuration.json");
		for(Configuration create : createRequest) {
			create.setId(new ObjectId());
		}
		Mockito.doReturn(createRequest).when(configurationRepository).save(Matchers.anyListOf(Configuration.class));
		service.insertConfigurationData(createRequest);
		Mockito.verify(configurationRepository, times(1)).save(createRequest);
		
	}
	
    private <T> List<Configuration> getObjectFromJson(String fileName) throws IOException, ClassNotFoundException {
    	Class<?> cls = Class.forName("com.capitalone.dashboard.model.Configuration");
    	ObjectMapper mapper = new ObjectMapper();
    	ClassLoader cLoader = cls.getClassLoader();
    	InputStream inputStream = cLoader.getResourceAsStream(fileName);
        return  mapper.readValue(inputStream, new TypeReference<List<Configuration>>(){});
		}
}
