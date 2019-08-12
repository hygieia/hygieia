package com.capitalone.dashboard.collector;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestOperations;

import com.capitalone.dashboard.gitlab.DefaultGitlabGitClient;
import com.capitalone.dashboard.gitlab.GitlabCommitsResponseMapper;
import com.capitalone.dashboard.gitlab.GitlabIssuesResponseMapper;
import com.capitalone.dashboard.gitlab.GitlabRequestsResponseMapper;
import com.capitalone.dashboard.gitlab.GitlabUrlUtility;
import com.capitalone.dashboard.gitlab.model.GitlabCommit;
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
	private ResponseEntity<GitlabCommit[]> response;

	@Mock
	private GitlabCommitsResponseMapper commitsResponseMapper;

	@Mock
	private GitlabIssuesResponseMapper issuesResponseMapper;

	@Mock
	private GitlabRequestsResponseMapper requestsResponseMapper;

	private URI apiUrl;

	private DefaultGitlabGitClient gitlabClient;

	@Before
	public void setup() throws URISyntaxException {
		when(restOperationsSupplier.get()).thenReturn(restOperations);
		gitlabClient = new DefaultGitlabGitClient(gitlabUrlUtility, gitlabSettings, restOperationsSupplier, commitsResponseMapper, issuesResponseMapper, requestsResponseMapper);
		apiUrl = new URI("http://google.com");
	}

	@Test
	public void shouldGetNoCommits() {
		when(gitlabUrlUtility.buildCommitsApiUrl(isA(GitlabGitRepo.class), eq(true), anyInt())).thenReturn(apiUrl);
		when(restOperations.exchange(isA(URI.class), eq(HttpMethod.GET), isA(HttpEntity.class), eq(GitlabCommit[].class))).thenReturn(response);
		List<Commit> commits = gitlabClient.getCommits(repo, true);

		assertEquals(0, commits.size());
	}

	@Test
	public void shouldGetOnePageOfCommits() {
		when(gitlabUrlUtility.buildCommitsApiUrl(isA(GitlabGitRepo.class), eq(true), anyInt())).thenReturn(apiUrl);
		when(restOperations.exchange(isA(URI.class), eq(HttpMethod.GET), isA(HttpEntity.class), eq(GitlabCommit[].class))).thenReturn(response);
		List<Commit> pageOfCommits = new ArrayList<Commit>();
		pageOfCommits.add(new Commit());
		when(commitsResponseMapper.map(eq(response.getBody()), anyString(), anyString())).thenReturn(pageOfCommits);

		List<Commit> commits = gitlabClient.getCommits(repo, true);

		assertEquals(pageOfCommits.size(), commits.size());
		assertTrue(CollectionUtils.isEqualCollection(pageOfCommits, commits));
	}

	@Test
	public void shouldGetMultiplePagesOfCommits() {
		when(gitlabUrlUtility.buildCommitsApiUrl(isA(GitlabGitRepo.class), eq(true), anyInt())).thenReturn(apiUrl);
		when(gitlabUrlUtility.updatePage(isA(URI.class), anyInt())).thenReturn(apiUrl);
		when(restOperations.exchange(isA(URI.class), eq(HttpMethod.GET), isA(HttpEntity.class), eq(GitlabCommit[].class))).thenReturn(response);
		List<Commit> firstPageOfCommits = new ArrayList<Commit>();
		for (int i = 0; i < 100; i++) {
			firstPageOfCommits.add(new Commit());
		}
		ArrayList<Commit> secondPageOfCommits = new ArrayList<>();
		secondPageOfCommits.add(new Commit());
		when(commitsResponseMapper.map(eq(response.getBody()), anyString(), anyString())).thenReturn(firstPageOfCommits).thenReturn(secondPageOfCommits);

		List<Commit> commits = gitlabClient.getCommits(repo, true);

		assertNotEquals(firstPageOfCommits.size(), commits.size());
		assertTrue(commits.containsAll(firstPageOfCommits));
		assertTrue(commits.containsAll(secondPageOfCommits));
	}

	@Test(expected = HttpClientErrorException.class)
	public void shouldLogException() {
		when(gitlabUrlUtility.buildCommitsApiUrl(isA(GitlabGitRepo.class), eq(true), anyInt())).thenReturn(apiUrl);
		when(restOperations.exchange(isA(URI.class), eq(HttpMethod.GET), isA(HttpEntity.class), eq(GitlabCommit[].class))).thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND));

		gitlabClient.getCommits(repo, true);

		verify(commitsResponseMapper, never()).map(isA(GitlabCommit[].class), anyString(), anyString());
	}

	@Test
	public void shouldUseApiTokenIfProvided() {
		when(gitlabUrlUtility.buildCommitsApiUrl(isA(GitlabGitRepo.class), eq(true), anyInt())).thenReturn(apiUrl);
		ArgumentCaptor<HttpEntity> captor = ArgumentCaptor.forClass(HttpEntity.class);
		when(restOperations.exchange(isA(URI.class), eq(HttpMethod.GET), captor.capture(), eq(GitlabCommit[].class))).thenReturn(response);
		String expectedApiKey = "fakeApiKey";
		when(repo.getUserId()).thenReturn(expectedApiKey);
        HttpHeaders headers = new HttpHeaders();
        headers.add("PRIVATE-TOKEN", expectedApiKey);
		when(gitlabUrlUtility.createHttpHeaders(anyString())).thenReturn(headers);

		gitlabClient.getCommits(repo, true);

		assertEquals(expectedApiKey, captor.getAllValues().get(0).getHeaders().get("PRIVATE-TOKEN").get(0));
	}

	@Test
	public void shouldUseCollectorsApiTokenIfNotProvided() {
		when(gitlabUrlUtility.buildCommitsApiUrl(isA(GitlabGitRepo.class), eq(true), anyInt())).thenReturn(apiUrl);
		ArgumentCaptor<HttpEntity> captor = ArgumentCaptor.forClass(HttpEntity.class);
		when(restOperations.exchange(isA(URI.class), eq(HttpMethod.GET), captor.capture(), eq(GitlabCommit[].class))).thenReturn(response);
		String expectedApiKey = "fakeApiKey";
		when(gitlabSettings.getApiToken()).thenReturn(expectedApiKey);
        HttpHeaders headers = new HttpHeaders();
        headers.add("PRIVATE-TOKEN", expectedApiKey);
        when(gitlabUrlUtility.createHttpHeaders(anyString())).thenReturn(headers);

		gitlabClient.getCommits(repo, true);

		assertEquals(expectedApiKey, captor.getAllValues().get(0).getHeaders().get("PRIVATE-TOKEN").get(0));
	}

}
