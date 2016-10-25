package com.capitalone.dashboard.collecteur;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.when;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestOperations;

import com.capitalone.dashboard.model.Commit;
import com.capitalone.dashboard.model.GitlabGitRepo;
import com.capitalone.dashboard.util.Supplier;

@RunWith(MockitoJUnitRunner.class)
public class DefaultGitlabGitClientTest {
	
	@Mock
	private RestOperations restOperations;
	
	@Mock
	private GitlabUrlUtility gitlabUrlUtility;
	
	@Mock
	private GitlabSettings gitlabSettings;
	
	@Mock
	private Supplier<RestOperations> restOperationsSupplier;
	
	@Mock 
	private GitlabGitRepo repo;
	
	@Mock
	private ResponseEntity<String> response;
	
	@Mock
	private GitlabResponseMapper responseMapper;
	
	private URI apiUrl;
	
	private DefaultGitlabGitClient gitlabClient;
	
	@Before
	public void setup() throws URISyntaxException {
		when(restOperationsSupplier.get()).thenReturn(restOperations);
		gitlabClient = new DefaultGitlabGitClient(gitlabUrlUtility, gitlabSettings, restOperationsSupplier, responseMapper);
		apiUrl = new URI("http://google.com");
	}

	@Test
	public void shouldGetNoCommits() {
		when(gitlabUrlUtility.buildApiUrl(isA(GitlabGitRepo.class), eq(true), anyInt())).thenReturn(apiUrl);
		when(restOperations.exchange(isA(URI.class), eq(HttpMethod.GET), isA(HttpEntity.class), eq(String.class))).thenReturn(response);
		when(response.getBody()).thenReturn("[]");
		List<Commit> commits = gitlabClient.getCommits(repo, true);
		
		assertEquals(0, commits.size());
	}

}
