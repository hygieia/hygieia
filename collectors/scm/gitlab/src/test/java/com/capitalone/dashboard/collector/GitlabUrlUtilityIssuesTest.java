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

import com.capitalone.dashboard.gitlab.GitlabUrlUtility;
import com.capitalone.dashboard.misc.HygieiaException;
import com.capitalone.dashboard.model.GitlabGitRepo;

@RunWith(MockitoJUnitRunner.class)
public class GitlabUrlUtilityIssuesTest {

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

		URI result = gitlabUrlUtility.buildIssuesApiUrl(gitlabRepo, true, 100);

		assertEquals("http", result.getScheme());
		assertEquals("gitlab.com", result.getHost());
		assertEquals("/api/v4/projects/namespace%2FHygieia/issues/", result.getRawPath());
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

		URI result = gitlabUrlUtility.buildIssuesApiUrl(gitlabRepo, true, 100);

		assertEquals("http", result.getScheme());
		assertEquals("gitlab.com", result.getHost());
		assertEquals("/api/v3/projects/namespace%2FHygieia/issues/", result.getRawPath());
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

		URI result = gitlabUrlUtility.buildIssuesApiUrl(gitlabRepo, true, 100);

		assertEquals("http", result.getScheme());
		assertEquals("gitlab.com", result.getHost());
		assertEquals("/api/v4/projects/namespace%2FHygieia/issues/", result.getRawPath());
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

		URI result = gitlabUrlUtility.buildIssuesApiUrl(gitlabRepo, true, 100);

		assertEquals("https", result.getScheme());
		assertEquals("gitlab.com", result.getHost());
		assertEquals("/api/v4/projects/namespace%2FHygieia/issues/", result.getRawPath());
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

		URI result = gitlabUrlUtility.buildIssuesApiUrl(gitlabRepo, true, 100);

		assertEquals("http", result.getScheme());
		assertEquals("customhost.com", result.getHost());
		assertEquals("/api/v4/projects/namespace%2FHygieia/issues/", result.getRawPath());
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

		URI result = gitlabUrlUtility.buildIssuesApiUrl(gitlabRepo, true, 100);

		assertEquals("http", result.getScheme());
		assertEquals("customhost.com", result.getHost());
		assertEquals("/api/v4/projects/namespace%2FHygieia/issues/", result.getRawPath());
		assertEquals(443, result.getPort());
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

		URI result = gitlabUrlUtility.buildIssuesApiUrl(gitlabRepo, true, 100);

		assertEquals("http", result.getScheme());
		assertEquals("customhost.com", result.getHost());
		assertEquals("/gitlab/is/here/api/v4/projects/namespace%2FHygieia/issues/", result.getRawPath());
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

		URI result = gitlabUrlUtility.buildIssuesApiUrl(gitlabRepo, true, 100);

		assertEquals("http", result.getScheme());
		assertEquals("gitlab.com", result.getHost());
		assertEquals("/api/v4/projects/namespace%2FHygieia/issues/", result.getRawPath());
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

		URI result = gitlabUrlUtility.buildIssuesApiUrl(gitlabRepo, false, 100);

		assertEquals("http", result.getScheme());
		assertEquals("gitlab.com", result.getHost());
		assertEquals("/api/v4/projects/namespace%2FHygieia/issues/", result.getRawPath());
		assertTrue(result.getQuery().contains("scope=all"));
		assertTrue(result.getQuery().contains("per_page=100"));
		assertTrue(result.getQuery().contains("created_after="));
		assertTrue(result.getQuery().contains("order_by=updated_at"));
		assertTrue(result.getQuery().contains("sort=desc"));
	}

	@Test
	public void shouldUpdatePage() throws URISyntaxException {
		URI uri = new URI("http://fakeurl.com");
		URI result = gitlabUrlUtility.updatePage(uri, 2);

		assertTrue(result.getQuery().contains("page=2"));
	}

	@Test
	public void shouldGetOrgAndRepoNameForHttpsProtocol() throws HygieiaException {
		String[] orgAndRepoName = gitlabUrlUtility.getOrgAndRepoName("https://domain.org/namespace/Hygieia");
		assertEquals("namespace", orgAndRepoName[0]);
		assertEquals("Hygieia", orgAndRepoName[1]);
	}

	@Test
	public void shouldGetOrgAndRepoNameForGitExtension() throws HygieiaException {
		String[] orgAndRepoName = gitlabUrlUtility.getOrgAndRepoName("https://domain.org/namespace/Hygieia.git");
		assertEquals("namespace", orgAndRepoName[0]);
		assertEquals("Hygieia", orgAndRepoName[1]);
	}

	@Test
	public void shouldGetOrgAndRepoNameForHttpProtocol() throws HygieiaException {
		String[] orgAndRepoName = gitlabUrlUtility.getOrgAndRepoName("http://domain.org/namespace/Hygieia");
		assertEquals("namespace", orgAndRepoName[0]);
		assertEquals("Hygieia", orgAndRepoName[1]);
	}

	@Test
	public void shouldGetOrgAndRepoNameForGitProtocol() throws HygieiaException {
		String[] orgAndRepoName = gitlabUrlUtility.getOrgAndRepoName("git@domain.org:namespace/Hygieia.git");
		assertEquals("namespace", orgAndRepoName[0]);
		assertEquals("Hygieia", orgAndRepoName[1]);
	}

	@Test
	public void shouldGetOrgAndRepoNameForWebUrl() throws HygieiaException {
		String[] orgAndRepoName = gitlabUrlUtility.getOrgAndRepoName("http://domain.org/namespace/Hygieia/merge_requests/136");
		assertEquals("namespace", orgAndRepoName[0]);
		assertEquals("Hygieia", orgAndRepoName[1]);
	}

}
