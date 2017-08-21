package com.capitalone.dashboard.gitlab;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.net.URI;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpEntity;

import com.capitalone.dashboard.collector.FeatureSettings;

@RunWith(MockitoJUnitRunner.class)
public class GitlabUriUtilityTest {
	
	@Mock
	private FeatureSettings settings;
	
	@InjectMocks
	private GitlabUriUtility urlUtility;

   @Test
    public void shouldBuildV3ProjectsUri() {
        when(settings.getApiVersion()).thenReturn(3);
        URI result = urlUtility.buildProjectsUri(); 
        assertEquals("http://gitlab.com/api/v3/projects?per_page=100", result.toString());
    }
	
	@Test
	public void shouldBuildProjectsUriWithCustomHost() {
		when(settings.getHost()).thenReturn("company.com");
		URI result = urlUtility.buildProjectsUri(); 
		assertEquals("http://company.com/api/v4/projects?per_page=100", result.toString());
	}
	
	@Test
	public void shouldBuildTeamUriWithCustomPort() {
		when(settings.getPort()).thenReturn("8181");
		URI result = urlUtility.buildTeamsUri(); 
		assertEquals("http://gitlab.com:8181/api/v4/groups?per_page=100", result.toString());
	}
	
	@Test
    public void shouldBuildTeamUriWithCustomPath() {
        when(settings.getPath()).thenReturn("/gitlab/resides/here");
        URI result = urlUtility.buildTeamsUri(); 
        assertEquals("http://gitlab.com/gitlab/resides/here/api/v4/groups?per_page=100", result.toString());
    }
	
	@Test
	public void shouldBuildTeamUriWithNoPort() {
		when(settings.getPort()).thenReturn("");
		URI result = urlUtility.buildTeamsUri(); 
		assertEquals("http://gitlab.com/api/v4/groups?per_page=100", result.toString());
	}
	
	@Test
	public void shouldBuildBoardsUriWithCustomProtocol() {
		when(settings.getProtocol()).thenReturn("https");
		URI result = urlUtility.buildBoardsUri("capitalone", "hygieia"); 
		assertEquals("https://gitlab.com/api/v4/projects/capitalone%2Fhygieia/boards?per_page=100", result.toString());
	}
	
	@Test
	public void shouldBuildIssuesForProjectUri() {
		URI result = urlUtility.buildIssuesForProjectUri("capitalone", "hygieia"); 
		assertEquals("http://gitlab.com/api/v4/projects/capitalone%2Fhygieia/issues?per_page=100", result.toString());
	}
	
	@Test
    public void shouldBuildProjectsForTeamUri() {
        URI result = urlUtility.buildProjectsForTeamUri("23"); 
        assertEquals("http://gitlab.com/api/v4/groups/23/projects?per_page=100", result.toString());
    }
	
	@Test
    public void shouldBuildProjectsByIdUri() {
        URI result = urlUtility.buildProjectsByIdUri("23"); 
        assertEquals("http://gitlab.com/api/v4/projects/23?per_page=100", result.toString());
    }
	
	@Test
	public void shouldUpdatePage() {
		URI uri = urlUtility.buildIssuesForProjectUri("capitalone", "hygieia"); 
		URI result = urlUtility.updatePage(uri, "2");
		assertEquals("http://gitlab.com/api/v4/projects/capitalone%2Fhygieia/issues?per_page=100&page=2", result.toString());
	}
	
	@Test
	public void shouldBuildAuthenticationHeader() {
		String apiToken = "apiToken";
		when(settings.getApiToken()).thenReturn(apiToken);
		HttpEntity<String> authHeader = urlUtility.buildAuthenticationHeader();
		assertEquals(apiToken, authHeader.getHeaders().get("PRIVATE-TOKEN").get(0));
	}
	
}
