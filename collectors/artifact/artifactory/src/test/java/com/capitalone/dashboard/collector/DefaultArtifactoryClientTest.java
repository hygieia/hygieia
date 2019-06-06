package com.capitalone.dashboard.collector;

import com.capitalone.dashboard.model.ArtifactoryRepo;
import com.capitalone.dashboard.model.BaseArtifact;
import com.capitalone.dashboard.model.BinaryArtifact;
import com.capitalone.dashboard.model.RepoAndPattern;
import com.capitalone.dashboard.model.ServerSetting;
import com.capitalone.dashboard.util.ArtifactUtilTest;
import com.capitalone.dashboard.util.Supplier;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestOperations;

import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DefaultArtifactoryClientTest {
	@Mock private Supplier<RestOperations> restOperationsSupplier;
    @Mock private RestOperations rest;
    @Mock private ArtifactorySettings settings;
    
    private final DateFormat FULL_DATE = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX");
    
    private DefaultArtifactoryClient defaultArtifactoryClient;
    
    @Before
    public void init() {
    	when(restOperationsSupplier.get()).thenReturn(rest);
        settings = new ArtifactorySettings();
        ServerSetting serverSetting = new ServerSetting();
		serverSetting.setUrl("http://localhost:8081/artifactory");
		RepoAndPattern r = new RepoAndPattern();
		r.setPatterns(Arrays.asList(ArtifactUtilTest.IVY_PATTERN1, ArtifactUtilTest.IVY_ARTIFACT_PATTERN1, ArtifactUtilTest.MAVEN_PATTERN1,ArtifactUtilTest.ARTIFACT_PATTERN));
		serverSetting.setRepoAndPatterns(Collections.singletonList(r));
        settings.setServers(Collections.singletonList(serverSetting));
        defaultArtifactoryClient = new DefaultArtifactoryClient(settings, restOperationsSupplier);
    }
    
    @Test
    public void testGetRepos() throws Exception {
    	String reposJson = getJson("repos.json");
    	
    	String instanceUrl = "http://localhost:8081/artifactory/";
    	String reposListUrl = "http://localhost:8081/artifactory/api/repositories";
    	
    	when(rest.exchange(eq(reposListUrl), eq(HttpMethod.GET), Matchers.any(HttpEntity.class), eq(String.class)))
    		.thenReturn(new ResponseEntity<>(reposJson, HttpStatus.OK));
    	List<ArtifactoryRepo> repos = defaultArtifactoryClient.getRepos(instanceUrl);
    	assertThat(repos.size(), is(2));
        assertThat(repos.get(0).getRepoName(), is("release"));
        assertThat(repos.get(0).getRepoUrl(), is("http://localhost:8081/artifactory/release"));
        assertThat(repos.get(1).getRepoName(), is("xldeploy"));
        assertThat(repos.get(1).getRepoUrl(), is("http://localhost:8081/artifactory/xldeploy"));
    }
    
    @Test
    public void testGetEmptyArtifacts() throws Exception {
    	String emptyArtifactsJson = getJson("emptyArtifacts.json");
    	
    	String instanceUrl = "http://localhost:8081/artifactory/";
    	String aqlUrl = "http://localhost:8081/artifactory/api/search/aql";
    	String repoName = "release";
    	
    	when(rest.exchange(eq(aqlUrl), eq(HttpMethod.POST), Matchers.any(HttpEntity.class), eq(String.class)))
    		.thenReturn(new ResponseEntity<>(emptyArtifactsJson, HttpStatus.OK));
    	List<BinaryArtifact> artifacts = defaultArtifactoryClient.getArtifacts(instanceUrl, repoName, 0);
    	assertThat(artifacts.size(), is(0));
    }


	@Test
	public void testGetArtifactItems() throws Exception {
		String artifactItemsJson = getJson("artifactItems.json");

		String instanceUrl = "http://localhost:8081/artifactory/";
		String aqlUrl = "http://localhost:8081/artifactory/api/search/aql";
		String repoName = "release";

		when(rest.exchange(eq(aqlUrl), eq(HttpMethod.POST), Matchers.any(HttpEntity.class), eq(String.class)))
				.thenReturn(new ResponseEntity<>(artifactItemsJson, HttpStatus.OK));
		List<BaseArtifact> baseArtifacts = defaultArtifactoryClient.getArtifactItems(instanceUrl, repoName, ArtifactUtilTest.ARTIFACT_PATTERN,0);
		assertThat(baseArtifacts.size(), is(1));
		assertThat(baseArtifacts.get(0).getArtifactItem().getArtifactName(),is("test-dev"));
		assertThat(baseArtifacts.get(0).getArtifactItem().getInstanceUrl(),is("http://localhost:8081/artifactory/"));
		assertThat(baseArtifacts.get(0).getArtifactItem().getRepoName(),is("repoName"));
		assertThat(baseArtifacts.get(0).getArtifactItem().getPath(),is("dummy/test-dev"));
		assertThat(baseArtifacts.get(0).getBinaryArtifacts().get(0).getCanonicalName(),is("manifest.json"));
		assertThat(baseArtifacts.get(0).getBinaryArtifacts().get(0).getArtifactGroupId(),is("dummy"));
		assertThat(baseArtifacts.get(0).getBinaryArtifacts().get(0).getActual_md5(),is("111aadc11ed11b1111df111d16d6c8d821112f1"));
		assertThat(baseArtifacts.get(0).getBinaryArtifacts().get(0).getActual_sha1(),is("111aadc11ed11b1111df111d16d6c8d821112f1"));
		assertThat(baseArtifacts.get(0).getBinaryArtifacts().get(0).getArtifactExtension(),is("json"));
		assertThat(baseArtifacts.get(0).getBinaryArtifacts().get(0).getArtifactName(),is("test-dev"));
		assertThat(baseArtifacts.get(0).getBinaryArtifacts().get(0).getType(),is("file"));
		assertThat(baseArtifacts.get(0).getBinaryArtifacts().get(0).getModifiedBy(),is("robot"));
		assertThat(baseArtifacts.get(0).getBinaryArtifacts().get(0).getModifiedTimeStamp(),is(new Long("1539268736471")));
		assertThat(baseArtifacts.get(0).getBinaryArtifacts().get(0).getCreatedBy(),is("robot"));
		assertThat(baseArtifacts.get(0).getBinaryArtifacts().get(0).getCreatedTimeStamp(),is(new Long("1539268036031")));
		assertThat(baseArtifacts.get(0).getBinaryArtifacts().get(0).getArtifactVersion(),is("1"));

	}


	@Test
    public void testGetMavenArtifacts() throws Exception {
    	String mavenArtifactsJson = getJson("mavenArtifacts.json");
    	
    	String instanceUrl = "http://localhost:8081/artifactory/";
    	String aqlUrl = "http://localhost:8081/artifactory/api/search/aql";
    	String repoName = "release";
    	
    	when(rest.exchange(eq(aqlUrl), eq(HttpMethod.POST), Matchers.any(HttpEntity.class), eq(String.class)))
    		.thenReturn(new ResponseEntity<>(mavenArtifactsJson, HttpStatus.OK));
    	List<BinaryArtifact> artifacts = defaultArtifactoryClient.getArtifacts(instanceUrl, repoName, 0);
    	assertThat(artifacts.size(), is(1));
    	
    	assertThat(artifacts.get(0).getArtifactName(), is("helloworld"));
    	assertThat(artifacts.get(0).getArtifactGroupId(), is("com.mycompany.myapp"));
    	assertThat(artifacts.get(0).getArtifactVersion(), is("4.8.5.20160909-091018I"));
    	assertThat(artifacts.get(0).getCanonicalName(), is("helloworld-4.8.5.20160909-091018I.jar"));
    	assertThat(artifacts.get(0).getTimestamp(), is(FULL_DATE.parse("2016-09-09T09:10:37.945-04:00").getTime()));
    	assertThat(artifacts.get(0).getBuildUrl(), is("http://localhost:8080/job/myname_helloworld/1/"));
    	assertThat(artifacts.get(0).getBuildNumber(), is("1"));
    	assertThat(artifacts.get(0).getInstanceUrl(), is("http://localhost:8080/"));
    	assertThat(artifacts.get(0).getJobName(), is("myname_helloworld"));
    	assertThat(artifacts.get(0).getJobUrl(), is("http://localhost:8080/job/myname_helloworld"));
    	assertThat(artifacts.get(0).getScmUrl(), is("https://github.com/~myname/helloworld.git"));
    	assertThat(artifacts.get(0).getScmBranch(), is("origin/master"));
    	assertThat(artifacts.get(0).getScmRevisionNumber(), is("943a7c299ec551d985356e5ad52766b38c52e893"));
    }
    
    @Test
    public void testGetIvyArtifacts() throws Exception {
    	String ivyArtifactsJson = getJson("ivyArtifacts.json");
    	
    	String instanceUrl = "http://localhost:8081/artifactory/";
    	String aqlUrl = "http://localhost:8081/artifactory/api/search/aql";
    	String repoName = "release";
    	
    	when(rest.exchange(eq(aqlUrl), eq(HttpMethod.POST), Matchers.any(HttpEntity.class), eq(String.class)))
    		.thenReturn(new ResponseEntity<>(ivyArtifactsJson, HttpStatus.OK));
    	List<BinaryArtifact> artifacts = defaultArtifactoryClient.getArtifacts(instanceUrl, repoName, 0);
    	assertThat(artifacts.size(), is(2));
    	
    	assertThat(artifacts.get(0).getArtifactName(), is("helloworld"));
    	assertThat(artifacts.get(0).getArtifactGroupId(), is("com.mycompany.myapp"));
    	assertThat(artifacts.get(0).getArtifactVersion(), is("4.8.5.20160909-091018I"));
    	assertThat(artifacts.get(0).getCanonicalName(), is("helloworld-4.8.5.20160909-091018I.jar"));
    	assertThat(artifacts.get(0).getTimestamp(), is(FULL_DATE.parse("2016-09-09T09:10:37.945-04:00").getTime()));
    	assertThat(artifacts.get(0).getBuildUrl(), is("http://localhost:8080/job/myname_helloworld/1/"));
    	assertThat(artifacts.get(0).getBuildNumber(), is("1"));
    	assertThat(artifacts.get(0).getInstanceUrl(), is("http://localhost:8080/"));
    	assertThat(artifacts.get(0).getJobName(), is("myname_helloworld"));
    	assertThat(artifacts.get(0).getJobUrl(), is("http://localhost:8080/job/myname_helloworld"));
    	assertThat(artifacts.get(0).getScmUrl(), is("https://github.com/~myname/helloworld.git"));
    	assertThat(artifacts.get(0).getScmBranch(), is("origin/master"));
    	assertThat(artifacts.get(0).getScmRevisionNumber(), is("943a7c299ec551d985356e5ad52766b38c52e893"));
    	
    	assertThat(artifacts.get(1).getArtifactName(), is("ivy"));
    	assertThat(artifacts.get(1).getArtifactGroupId(), is("com.mycompany.myapp"));
    	assertThat(artifacts.get(1).getArtifactVersion(), is("4.8.5.20160909-091018I"));
    	assertThat(artifacts.get(1).getCanonicalName(), is("ivy-4.8.5.20160909-091018I.xml"));
    	assertThat(artifacts.get(1).getTimestamp(), is(FULL_DATE.parse("2016-10-13T05:10:49.209-04:00").getTime()));
    }
    
    private String getJson(String fileName) throws IOException {
        InputStream inputStream = DefaultArtifactoryClient.class.getResourceAsStream(fileName);
        return IOUtils.toString(inputStream);
    }
}
