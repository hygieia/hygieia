package com.capitalone.dashboard.collector;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestOperations;

import com.capitalone.dashboard.collector.DefaultBitbucketServerClient;
import com.capitalone.dashboard.collector.GitSettings;
import com.capitalone.dashboard.model.Commit;
import com.capitalone.dashboard.model.GitRepo;
import com.capitalone.dashboard.util.Encryption;
import com.capitalone.dashboard.util.EncryptionException;
import com.capitalone.dashboard.util.Supplier;

@RunWith(MockitoJUnitRunner.class)
public class DefaultBitbucketServerClientTest {
	@Mock private Supplier<RestOperations> restOperationsSupplier;
	@Mock private RestOperations rest;
	@Mock private GitSettings settings;
	
	@Captor private ArgumentCaptor<HttpEntity<?>> httpEntityCaptor;
	
	private DefaultBitbucketServerClient client;
	
    @Before
    public void init() {
        when(restOperationsSupplier.get()).thenReturn(rest);
        settings = new GitSettings();
    	settings.setApi("/rest/api/1.0/");
    	settings.setPageSize(25);
    	
        client = new DefaultBitbucketServerClient(settings, restOperationsSupplier);
    }
	
    @Test
    public void testGetCommits() throws IOException {
    	// Note that there always is paging even if results only take 1 page
    	String jsonResponse1 = getJson("/bitbucket-server/response1a.json");
    	String jsonResponse2 = getJson("/bitbucket-server/response1b.json");
    	
    	settings.setPageSize(1);
    	
    	GitRepo repo = new GitRepo();
    	String repoUrl = "https://username@company.com/scm/myproject/myrepository.git";
    	repo.setRepoUrl(repoUrl);
    	repo.getOptions().put("url", repoUrl);
    	repo.setBranch("master");
    	URI uri1 = URI.create("https://company.com/rest/api/1.0/projects/myproject/repos/myrepository/commits?until=master&limit=1");
    	URI uri2 = URI.create("https://company.com/rest/api/1.0/projects/myproject/repos/myrepository/commits?until=master&limit=1&start=1");
    	
        when(rest.exchange(eq(uri1), eq(HttpMethod.GET), Matchers.any(HttpEntity.class), eq(String.class)))
    		.thenReturn(new ResponseEntity<>(jsonResponse1, HttpStatus.OK));
	    when(rest.exchange(eq(uri2), eq(HttpMethod.GET), Matchers.any(HttpEntity.class), eq(String.class)))
			.thenReturn(new ResponseEntity<>(jsonResponse2, HttpStatus.OK));
    	
        List<Commit> commits = client.getCommits(repo, true);
        
        assertEquals(2, commits.size());
        
        assertTrue(0 != commits.get(0).getTimestamp());
        assertEquals(repoUrl, commits.get(0).getScmUrl());
        assertEquals("215e5a6cbbda3a0cf4271a7e7c799306d3adb9ad", commits.get(0).getScmRevisionNumber());
        assertEquals("billybob", commits.get(0).getScmAuthor());
        assertEquals("Message 1", commits.get(0).getScmCommitLog());
        assertEquals(2, commits.get(0).getScmParentRevisionNumbers().size());
        assertEquals("9097aee6916a1883945b9cf9b77d351dc6802307", commits.get(0).getScmParentRevisionNumbers().get(0));
        assertEquals("30a9559513e471fb8f1deff10bd8823ad74a2fab", commits.get(0).getScmParentRevisionNumbers().get(1));
        assertEquals(1463771960000L, commits.get(0).getScmCommitTimestamp());
        
        assertTrue(0 != commits.get(1).getTimestamp());
        assertEquals(repoUrl, commits.get(1).getScmUrl());
        assertEquals("30a9559513e471fb8f1deff10bd8823ad74a2fab", commits.get(1).getScmRevisionNumber());
        assertEquals("billybob", commits.get(1).getScmAuthor());
        assertEquals("Message 2", commits.get(1).getScmCommitLog());
        assertEquals(1, commits.get(1).getScmParentRevisionNumbers().size());
        assertEquals("9097aee6916a1883945b9cf9b77d351dc6802307", commits.get(1).getScmParentRevisionNumbers().get(0));
        assertEquals(1463771869000L, commits.get(1).getScmCommitTimestamp());
    }
    
    @Test
    public void testBasicAuthentication() throws IOException, EncryptionException {
    	// Note that there always is paging even if results only take 1 page
    	String jsonResponse1 = "{ \"values\": [] }";
    	settings.setKey("abcdefghijklmnopqrstuvwxyz1234567");
    	String encPassword = Encryption.encryptString("password", settings.getKey());
    	
    	settings.setPageSize(1);
    	
    	GitRepo repo = new GitRepo();
    	repo.setUserId("user");
    	repo.setPassword(encPassword);
    	
    	String repoUrl = "https://username@company.com/scm/myproject/myrepository.git";
    	repo.setRepoUrl(repoUrl);
    	repo.getOptions().put("url", repoUrl);
    	repo.setBranch("master");
    	URI uri1 = URI.create("https://company.com/rest/api/1.0/projects/myproject/repos/myrepository/commits?until=master&limit=1");
    	
        when(rest.exchange(eq(uri1), eq(HttpMethod.GET), Matchers.any(HttpEntity.class), eq(String.class)))
    		.thenReturn(new ResponseEntity<>(jsonResponse1, HttpStatus.OK));
    	
        client.getCommits(repo, true);
        
        Mockito.verify(rest).exchange(eq(uri1), eq(HttpMethod.GET), httpEntityCaptor.capture(), eq(String.class));
        
        HttpEntity<?> entity = httpEntityCaptor.getValue();
        assertEquals("Basic dXNlcjpwYXNzd29yZA==", entity.getHeaders().get("Authorization").iterator().next());
    }
    
    @Test
    public void testBuildUri() throws URISyntaxException {
		
		String url1 = "ssh://git@company.com/myproject/myrepository.git";
		String url2 = "https://username@company.com/scm/space space/myrepository.git";
		String url3 = "http://git@company.com/~myusername/myrepository";

		String branch1 = "feature/NEW_STUFF_NOW";

		String lastKnownCommit1 = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

		URI res;

		// First try basic combinations
		res = client.buildUri(url1, null, null);
		assertEquals("https://company.com/rest/api/1.0/projects/myproject/repos/myrepository/commits?until=master&limit=25", res.toString());

		res = client.buildUri(url2, null, null);
		assertEquals("https://company.com/rest/api/1.0/projects/space%20space/repos/myrepository/commits?until=master&limit=25", res.toString());

		res = client.buildUri(url3, null, null);
		assertEquals("http://company.com/rest/api/1.0/projects/~myusername/repos/myrepository/commits?until=master&limit=25", res.toString());

		// try branch
		res = client.buildUri(url2, branch1, null);
		assertEquals("https://company.com/rest/api/1.0/projects/space%20space/repos/myrepository/commits?until=feature%2FNEW_STUFF_NOW&limit=25", res.toString());

		// try since
		res = client.buildUri(url2, null, lastKnownCommit1);
		assertEquals("https://company.com/rest/api/1.0/projects/space%20space/repos/myrepository/commits?until=master&since=ABCDEFGHIJKLMNOPQRSTUVWXYZ&limit=25", res.toString());

		// try no page limit
		settings.setPageSize(-1);
		res = client.buildUri(url2, "master", null);
		assertEquals("https://company.com/rest/api/1.0/projects/space%20space/repos/myrepository/commits?until=master", res.toString());
    }
    
    private String getJson(String fileName) throws IOException {
        InputStream inputStream = DefaultBitbucketServerClientTest.class.getResourceAsStream(fileName);
        return IOUtils.toString(inputStream);
    }
}
