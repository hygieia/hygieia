package com.capitalone.dashboard.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;

import org.bson.types.ObjectId;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import com.capitalone.dashboard.model.BinaryArtifact;
import com.capitalone.dashboard.repository.BinaryArtifactRepository;
import com.capitalone.dashboard.request.BinaryArtifactCreateRequest;
import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(MockitoJUnitRunner.class)
public class BinaryArtifactServiceTest {
	
	@Mock 
	BinaryArtifactRepository artifactRepository;
	@InjectMocks
	BinaryArtifactServiceImpl service;
	
	@Captor 
	ArgumentCaptor<BinaryArtifact> binaryArtifactCaptor;
	
	@Test
	public void testCreate() throws Exception {
		BinaryArtifactCreateRequest createRequest1 = getObjectFromJson("artifact1.json", BinaryArtifactCreateRequest.class);
		BinaryArtifact stubBA = new BinaryArtifact();
		stubBA.setId(new ObjectId("57d80a9646e0fb000ecc2ac7"));
		
		Mockito.when(artifactRepository.findByArtifactGroupIdAndArtifactNameAndArtifactVersion(Mockito.anyString(), Mockito.anyString(), Mockito.anyString()))
			.thenReturn(Collections.<BinaryArtifact>emptyList());
		
		Mockito.when(artifactRepository.save(Mockito.any(BinaryArtifact.class))).thenReturn(stubBA);
		
		String result = service.create(createRequest1);
		assertEquals("57d80a9646e0fb000ecc2ac7", result);

		Mockito.verify(artifactRepository).save(binaryArtifactCaptor.capture());
        
		BinaryArtifact ba = binaryArtifactCaptor.getValue();
		
		assertNotNull(ba);
		assertEquals("Helloworld", ba.getArtifactName());
		assertEquals("Helloworld", ba.getCanonicalName());
		assertEquals("com.capitalone.helloworld", ba.getArtifactGroupId());
		assertEquals("1.0.0", ba.getArtifactVersion());
		assertEquals(1473776278638L, ba.getTimestamp());
		
		// Metadata
		assertEquals("job_helloworld", ba.getJobName());
		assertEquals("http://myjenkins:8080/job/job_helloworld/1/", ba.getBuildUrl());
		assertEquals("http://myjenkins:8080/job/job_helloworld/", ba.getJobUrl());
		assertEquals("1", ba.getBuildNumber());
		assertEquals("http://myjenkins:8080/", ba.getInstanceUrl());
		assertEquals("bar", ba.getMetadata().get("foo"));
	}
	
    private <T> T getObjectFromJson(String fileName, Class<T> clazz) throws IOException {
        InputStream inputStream = BinaryArtifactServiceTest.class.getResourceAsStream(fileName);
        return new ObjectMapper().readValue(inputStream, clazz);
    }
}
