package com.capitalone.dashboard.collector;



import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.bson.types.ObjectId;
import org.json.simple.parser.ParseException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestOperations;

import com.capitalone.dashboard.model.Collector;
import com.capitalone.dashboard.model.CollectorType;
import com.capitalone.dashboard.model.Component;
import com.capitalone.dashboard.model.Configuration;
import com.capitalone.dashboard.model.RallyBurnDownData;
import com.capitalone.dashboard.model.RallyCollector;
import com.capitalone.dashboard.model.RallyFeature;
import com.capitalone.dashboard.model.RallyProject;
import com.capitalone.dashboard.repository.ComponentRepository;
import com.capitalone.dashboard.repository.ConfigurationRepository;
import com.capitalone.dashboard.repository.RallyBurnDownRepository;
import com.capitalone.dashboard.repository.RallyCollectorRepository;
import com.capitalone.dashboard.repository.RallyFeatureRepository;
import com.capitalone.dashboard.repository.RallyProjectRepository;
import com.capitalone.dashboard.util.Supplier;
import com.mysema.query.annotations.Config;

@RunWith(MockitoJUnitRunner.class)
public class RallyCollectorTaskTests {

	
	@Mock private  RallyCollectorRepository rallyCollectorRepository;
	@Mock private  RallyProjectRepository rallyProjectRepository;
	@Mock private  RallyBurnDownRepository rallyBurnDownRepository;
	@Mock private  RallyFeatureRepository rallyFeatureRepository;
	@Mock private  ConfigurationRepository configurationRepository;
	@Mock private RallyClient rallyClient;
	
	@Mock private  ComponentRepository dbComponentRepository;
	@Mock private Supplier<RestOperations> restOperationsSupplier;
	@Mock private RestOperations rest;
	
	@Mock private RallySettings rallySettings;
	@Mock private DefaultRallyClient defaultRallyClient;
	
	private static final String URL = "URL";
	private static final String SERVER = "server1";
	@InjectMocks  RallyCollectorTask task;
	
	@Mock private RallyFeature rallyFeature;
	@Mock private RallyProject rallyProject1;
	@Mock private RallyProject rallyProject2;
	@Mock private RallyProject rallyProject3;
	
	
	
	@Before
    public void init() {
		rallyProject1=new RallyProject();
		rallyFeature = new RallyFeature();
		rallyProject1.setInstanceUrl("https://rally1.rallydev.com");
		rallyProject1.setProjectId("72526393953");
		rallyProject2=new RallyProject();
		rallyProject2.setInstanceUrl("https://rally1.rallydev.com");
		rallyProject2.setProjectId("72526393952");
		rallyProject3=new RallyProject();
		rallyProject3.setInstanceUrl("https://rally1.rallydev.com");
		rallyProject3.setProjectId("72526393954");
		
        rallyFeature.setStartDate("2016-11-14");
        rallyFeature.setProjectId("72526393952");
        rallyFeature.setIterationId("76004589316");
    }
	
	@Test
	public void testHashCode() throws Exception {
		int hashcode1=rallyProject1.hashCode();
		int hashcode2=rallyProject2.hashCode();
		//assertEquals(hashcode1, hashcode2);
		
		assertTrue("returned unexpected status", hashcode1 != hashcode2 ) ;
	}
	
	@Test
	public void testEquals() throws Exception {
		boolean x=rallyProject2.equals(rallyProject2);
		assertTrue(x);
	}
	
	@Test
	public void testEqualsNegative() throws Exception {
			boolean y=rallyProject3.equals(rallyProject1);
			assertTrue(!y);
	}
	
	 @Test
     public void collect_noRallyServers_nothingIncluded() {
        when(dbComponentRepository.findAll()).thenReturn(components());
        when(configurationRepository.findAll()).thenReturn(makeConfiguration());
        task.collect(new RallyCollector());
     }
	 
	 
	@Test
     public void collect_noJobsOnServer_nothingIncluded() throws ParseException {
        when(rallyClient.getProjects(SERVER)).thenReturn(new ArrayList<RallyProject>());
        when(dbComponentRepository.findAll()).thenReturn(components());
        //task.collect(collectorWithOneServer());
    }
	 
	 @Test
	 public void testHashCodeNegative() throws Exception {
			int hashcode1=rallyProject1.hashCode();
			int hashcode3=rallyProject3.hashCode();
			assertTrue(hashcode1!=hashcode3);
	 }
	 
	 @Test
	 public void instanceJobs_emptyResponse_returnsEmptyList() throws ParseException {
	        when(rest.exchange(Matchers.any(URI.class), eq(HttpMethod.GET), Matchers.any(HttpEntity.class), eq(String.class)))
	                .thenReturn(new ResponseEntity<String>("", HttpStatus.OK));
	        List<RallyProject> jobs = rallyClient.getProjects(URL);
	        assertThat(jobs.size(), is(0));
	 } 
	 
	@Test
	public void test_rally_collectortask(){
		when(dbComponentRepository.findAll()).thenReturn(components());
		Set<ObjectId> collectorID = new HashSet<>();
		collectorID.add(new ObjectId("58490b086beff41c388896e3"));
		Collector collector = new Collector();
        collector.setEnabled(false);
        collector.setName("collector");
        collector.setId(new ObjectId("58490b086beff41c388896e3"));
		
		when(rallyProjectRepository.findByCollectorIdIn(collectorID)).thenReturn(getExistingApplications());//.thenReturn(getGitHubs()
		//task.collect(getRallyCollector());
		
	}
	
	@Test
	public void test_burnDownData_unique(){
		when(rallyBurnDownRepository.findByIterationIdAndProjectId(rallyFeature.getIterationId(), rallyFeature.getProjectId())).thenReturn(getRallyBurnDown());

		assertNotNull(rallyBurnDownRepository.findByIterationIdAndProjectId(rallyFeature.getIterationId(), rallyFeature.getProjectId()));
																					
	}
      private RallyBurnDownData getRallyBurnDown() {
    	  RallyBurnDownData rallyBurnDownData = new RallyBurnDownData();
    	  rallyBurnDownData.setTotalEstimate(300.00);
    	  rallyBurnDownData.getBurnDownData().add(0, new HashMap<String, String>());   	  
		return rallyBurnDownData;
	}

	private List<RallyProject> getExistingApplications() {
		    RallyProject nq = new RallyProject();
	        List<RallyProject> servers=new ArrayList<>();
	        nq.setInstanceUrl("https://rally1.rallydev.com");
	        nq.setProjectName("rally");
	        nq.setProjectId("58490b086beff41c388896e3");
	        nq.setCollectorId(new ObjectId("58490b086beff41c388896e3"));
	        servers.add(nq);
	        return servers;
	    } 
	   
	   private static RallyCollector getRallyCollector() {
		   RallyCollector protoType = new RallyCollector();
		    List<String> servers=new ArrayList<>();
		    servers.add("https://rally1.rallydev.com");
		    servers.add("https://rally1.rallydev.com");
		    protoType.setId(new ObjectId("58490b086beff41c388896e3"));
	        protoType.setName("rally");//Rally
	        protoType.setCollectorType(CollectorType.AgileTool);
	        protoType.setOnline(true);
	        protoType.setEnabled(true);
	        protoType.getRallyServers().addAll(servers);
	        
	        return protoType;
	    } 
	 
	 private RallyCollector collectorWithOneServer() {
	        return RallyCollector.prototype(Arrays.asList(SERVER));
	 }
	 
	 private ArrayList<com.capitalone.dashboard.model.Component> components() {
	    	ArrayList<com.capitalone.dashboard.model.Component> cArray = new ArrayList<com.capitalone.dashboard.model.Component>();
	    	com.capitalone.dashboard.model.Component c = new Component();
	    	c.setId(new ObjectId());
	    	c.setName("COMPONENT1");
	    	c.setOwner("Gokul");
	    	cArray.add(c);
	    	return cArray;
	}
	 
    private String getJson(String fileName) throws IOException {
    	BufferedInputStream result = (BufferedInputStream) 
    	         Config.class.getClassLoader().getResourceAsStream(fileName);
      //  InputStream inputStream = DefaultRallyClientTests.class.getResourceAsStream(fileName);
        return IOUtils.toString(result);
    }

    private void assertJob(RallyProject job, String name, String url) {
        assertThat(job.getProjectName(), is(name));
        assertThat(job.getInstanceUrl(), is(url));
    }
    
    private List<Configuration> makeConfiguration() {
    	List<Configuration> configFiles = new ArrayList<>();
		Configuration config = new Configuration();
		Set<Map<String,String>> serverConfig = new HashSet<>();
		Map<String,String> options = new HashMap<>();
		options.put("url", "http://jenkinsserver.domain.com:9999/");
		serverConfig.add(options);
		config.setInfo(serverConfig);
		configFiles.add(config);
		return configFiles;
	}
	 
}
