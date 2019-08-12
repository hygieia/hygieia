package com.capitalone.dashboard.collector;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.net.URI;
import java.net.URISyntaxException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpHeaders;

import com.capitalone.dashboard.gitlab.GitlabUrlUtility;
import com.capitalone.dashboard.model.GitlabGitRepo;

@RunWith(MockitoJUnitRunner.class)
public class GitlabUrlUtilityMergeRequestsTest {

	@Mock
	private GitlabSettings gitlabSettings;

	@Mock
	private GitlabGitRepo gitlabRepo;

	@InjectMocks
	private GitlabUrlUtility gitlabUrlUtility;

	@Test
	public void shouldBuildV4ApiUrl() {
		when(gitlabRepo.getRepoUrl()).thenReturn("https://domain.org/namespace/Hygieia");
		when(gitlabRepo.getBranch()).thenReturn("master");

		URI result = gitlabUrlUtility.buildMergeRequestsApiUrl(gitlabRepo, "all", true, 100);

		assertEquals("http", result.getScheme());
		assertEquals("gitlab.com", result.getHost());
		assertEquals("/api/v4/projects/namespace%2FHygieia/merge_requests/", result.getRawPath());
		assertTrue(result.getQuery().contains("state=all"));
		assertTrue(result.getQuery().contains("scope=all"));
		assertTrue(result.getQuery().contains("per_page=100"));
		assertTrue(result.getQuery().contains("created_after="));
		assertTrue(result.getQuery().contains("order_by=updated_at"));
		assertTrue(result.getQuery().contains("sort=desc"));
	}

	@Test
	public void shouldBuildV3ApiUrl() {
		when(gitlabRepo.getRepoUrl()).thenReturn("https://domain.org/namespace/Hygieia");
		when(gitlabRepo.getBranch()).thenReturn("master");
		when(gitlabSettings.getApiVersion()).thenReturn(3);

		URI result = gitlabUrlUtility.buildMergeRequestsApiUrl(gitlabRepo, "all", true, 100);

		assertEquals("http", result.getScheme());
		assertEquals("gitlab.com", result.getHost());
		assertEquals("/api/v3/projects/namespace%2FHygieia/merge_requests/", result.getRawPath());
		assertTrue(result.getQuery().contains("state=all"));
		assertTrue(result.getQuery().contains("scope=all"));
		assertTrue(result.getQuery().contains("per_page=100"));
		assertTrue(result.getQuery().contains("created_after="));
		assertTrue(result.getQuery().contains("order_by=updated_at"));
		assertTrue(result.getQuery().contains("sort=desc"));
	}

	@Test
	public void shouldbuildApiUrlsWithGitExtension() {
		when(gitlabRepo.getRepoUrl()).thenReturn("https://domain.org/namespace/Hygieia.git");
		when(gitlabRepo.getBranch()).thenReturn("master");

		URI result = gitlabUrlUtility.buildMergeRequestsApiUrl(gitlabRepo, "all", true, 100);

		assertEquals("http", result.getScheme());
		assertEquals("gitlab.com", result.getHost());
		assertEquals("/api/v4/projects/namespace%2FHygieia/merge_requests/", result.getRawPath());
		assertTrue(result.getQuery().contains("state=all"));
		assertTrue(result.getQuery().contains("scope=all"));
		assertTrue(result.getQuery().contains("per_page=100"));
		assertTrue(result.getQuery().contains("created_after="));
		assertTrue(result.getQuery().contains("order_by=updated_at"));
		assertTrue(result.getQuery().contains("sort=desc"));
	}

	@Test
	public void shouldbuildApiUrlsWithCustomProtocol() {
		when(gitlabRepo.getRepoUrl()).thenReturn("https://domain.org/namespace/Hygieia");
		when(gitlabRepo.getBranch()).thenReturn("master");
		when(gitlabSettings.getProtocol()).thenReturn("https");

		URI result = gitlabUrlUtility.buildMergeRequestsApiUrl(gitlabRepo, "all", true, 100);

		assertEquals("https", result.getScheme());
		assertEquals("gitlab.com", result.getHost());
		assertEquals("/api/v4/projects/namespace%2FHygieia/merge_requests/", result.getRawPath());
		assertTrue(result.getQuery().contains("state=all"));
		assertTrue(result.getQuery().contains("scope=all"));
		assertTrue(result.getQuery().contains("per_page=100"));
		assertTrue(result.getQuery().contains("created_after="));
		assertTrue(result.getQuery().contains("order_by=updated_at"));
		assertTrue(result.getQuery().contains("sort=desc"));
	}

	@Test
	public void shouldbuildApiUrlsWithCustomHost() {
		when(gitlabRepo.getRepoUrl()).thenReturn("https://domain.org/namespace/Hygieia");
		when(gitlabRepo.getBranch()).thenReturn("master");
		when(gitlabSettings.getHost()).thenReturn("customhost.com");

		URI result = gitlabUrlUtility.buildMergeRequestsApiUrl(gitlabRepo, "all", true, 100);

		assertEquals("http", result.getScheme());
		assertEquals("customhost.com", result.getHost());
		assertEquals("/api/v4/projects/namespace%2FHygieia/merge_requests/", result.getRawPath());
		assertTrue(result.getQuery().contains("state=all"));
		assertTrue(result.getQuery().contains("scope=all"));
		assertTrue(result.getQuery().contains("per_page=100"));
		assertTrue(result.getQuery().contains("created_after="));
		assertTrue(result.getQuery().contains("order_by=updated_at"));
		assertTrue(result.getQuery().contains("sort=desc"));
	}

	@Test
	public void shouldbuildApiUrlsWithCustomPort() {
		when(gitlabRepo.getRepoUrl()).thenReturn("https://domain.org/namespace/Hygieia");
		when(gitlabRepo.getBranch()).thenReturn("master");
		when(gitlabSettings.getHost()).thenReturn("customhost.com");
		when(gitlabSettings.getPort()).thenReturn("443");

		URI result = gitlabUrlUtility.buildMergeRequestsApiUrl(gitlabRepo, "all", true, 100);

		assertEquals("http", result.getScheme());
		assertEquals("customhost.com", result.getHost());
		assertEquals("/api/v4/projects/namespace%2FHygieia/merge_requests/", result.getRawPath());
		assertEquals(443, result.getPort());
		assertTrue(result.getQuery().contains("state=all"));
		assertTrue(result.getQuery().contains("scope=all"));
		assertTrue(result.getQuery().contains("per_page=100"));
		assertTrue(result.getQuery().contains("created_after="));
		assertTrue(result.getQuery().contains("order_by=updated_at"));
		assertTrue(result.getQuery().contains("sort=desc"));
	}

	@Test
	public void shouldbuildApiUrlsWithCustomPath() {
		when(gitlabRepo.getRepoUrl()).thenReturn("https://domain.org/namespace/Hygieia");
		when(gitlabRepo.getBranch()).thenReturn("master");
		when(gitlabSettings.getHost()).thenReturn("customhost.com");
		when(gitlabSettings.getPath()).thenReturn("/gitlab/is/here");

		URI result = gitlabUrlUtility.buildMergeRequestsApiUrl(gitlabRepo, "all", true, 100);

		assertEquals("http", result.getScheme());
		assertEquals("customhost.com", result.getHost());
		assertEquals("/gitlab/is/here/api/v4/projects/namespace%2FHygieia/merge_requests/", result.getRawPath());
		assertTrue(result.getQuery().contains("state=all"));
		assertTrue(result.getQuery().contains("scope=all"));
		assertTrue(result.getQuery().contains("per_page=100"));
		assertTrue(result.getQuery().contains("created_after="));
		assertTrue(result.getQuery().contains("order_by=updated_at"));
		assertTrue(result.getQuery().contains("sort=desc"));
	}

	@Test
	public void shouldbuildApiUrlsForFirstRunProvidedHistoryDays() {
		when(gitlabRepo.getRepoUrl()).thenReturn("https://domain.org/namespace/Hygieia");
		when(gitlabRepo.getBranch()).thenReturn("master");
		when(gitlabSettings.getFirstRunHistoryDays()).thenReturn(10);

		URI result = gitlabUrlUtility.buildMergeRequestsApiUrl(gitlabRepo, "all", true, 100);

		assertEquals("http", result.getScheme());
		assertEquals("gitlab.com", result.getHost());
		assertEquals("/api/v4/projects/namespace%2FHygieia/merge_requests/", result.getRawPath());
		assertTrue(result.getQuery().contains("state=all"));
		assertTrue(result.getQuery().contains("scope=all"));
		assertTrue(result.getQuery().contains("per_page=100"));
		assertTrue(result.getQuery().contains("created_after="));
		assertTrue(result.getQuery().contains("order_by=updated_at"));
		assertTrue(result.getQuery().contains("sort=desc"));
	}

	@Test
	public void shouldbuildApiUrlsForNonFirstRun() {
		when(gitlabRepo.getRepoUrl()).thenReturn("https://domain.org/namespace/Hygieia");
		when(gitlabRepo.getBranch()).thenReturn("master");
		when(gitlabRepo.getLastUpdated()).thenReturn(1477513100920L);

		URI result = gitlabUrlUtility.buildMergeRequestsApiUrl(gitlabRepo, "all", false, 100);

		assertEquals("http", result.getScheme());
		assertEquals("gitlab.com", result.getHost());
		assertEquals("/api/v4/projects/namespace%2FHygieia/merge_requests/", result.getRawPath());
		assertTrue(result.getQuery().contains("state=all"));
		assertTrue(result.getQuery().contains("scope=all"));
		assertTrue(result.getQuery().contains("per_page=100"));
		assertTrue(result.getQuery().contains("created_after="));
		assertTrue(result.getQuery().contains("order_by=updated_at"));
		assertTrue(result.getQuery().contains("sort=desc"));
	}

	@Test
	public void shouldbuildApiUrlsForMergeRequestNotes() {
		URI result = gitlabUrlUtility.buildMergeRequestNotesApiUrl("https://domain.org/namespace/Hygieia", "198", 100);

		assertEquals("http", result.getScheme());
		assertEquals("gitlab.com", result.getHost());
		assertEquals("/api/v4/projects/namespace%2FHygieia/merge_requests/198/notes/", result.getRawPath());
		assertTrue(result.getQuery().contains("per_page=100"));
		assertTrue(result.getQuery().contains("order_by=updated_at"));
		assertTrue(result.getQuery().contains("sort=desc"));
	}

	@Test
	public void shouldbuildApiUrlsForCommitStatuses() {
		URI result = gitlabUrlUtility.buildCommitStatusesApiUrl("https://domain.org/namespace/Hygieia", "master", "c06065d0829fb0b9b8aca473b16c37f4abe3a60f", 100);

		assertEquals("http", result.getScheme());
		assertEquals("gitlab.com", result.getHost());
		assertEquals("/api/v4/projects/namespace%2FHygieia/repository/commits/c06065d0829fb0b9b8aca473b16c37f4abe3a60f/statuses/", result.getRawPath());
		assertTrue(result.getQuery().contains("per_page=100"));
		assertTrue(result.getQuery().contains("ref=master"));
	}

	@Test
	public void shouldUpdatePage() throws URISyntaxException {
		URI uri = new URI("http://fakeurl.com");
		URI result = gitlabUrlUtility.updatePage(uri, 2);

		assertTrue(result.getQuery().contains("page=2"));
	}

	@Test
	public void shouldBuildHttpHeaders() {
	    String expectedApiToken = "fakeApiToken";
	    HttpHeaders httpHeaders = gitlabUrlUtility.createHttpHeaders(expectedApiToken);

        assertEquals(expectedApiToken, httpHeaders.get("PRIVATE-TOKEN").get(0));
	}
}
