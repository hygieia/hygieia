package com.capitalone.dashboard.collector;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyCollection;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isNotNull;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.bson.types.ObjectId;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestOperations;

import com.capitalone.dashboard.model.CodeQualityMetricStatus;
import com.capitalone.dashboard.model.FortifyProject;
import com.capitalone.dashboard.model.FortifyScanReport;
import com.capitalone.dashboard.util.Supplier;

@RunWith(MockitoJUnitRunner.class)
public class DefaultFortifyClientTest {

    @Mock private Supplier<RestOperations> restOperationsSupplier;
    @Mock private RestOperations rest;
    private FortifySettings fortifySettings;
    private FortifyClient fortifyClient;
    private DefaultFortifyClient defaultFortifyClient;
    
    private static final String URL_TEST = "http://somemockurl/ssc/";
    
    @Before
    public void init() {
        when(restOperationsSupplier.get()).thenReturn(rest);
        fortifySettings = new FortifySettings();
        fortifyClient = defaultFortifyClient = new DefaultFortifyClient(restOperationsSupplier);
    }

    @Test(expected = RestClientException.class)
    public void noResponseTest() throws Exception {
    	when(rest.exchange(eq(URL_TEST), eq(HttpMethod.GET), any(), eq(String.class))).thenReturn(null);
    	Map<String, JSONObject> applicationArray = fortifyClient.getApplicationArray(URL_TEST);
    	assertThat(applicationArray.size(),is(0));
    }
    
    @Test(expected = ParseException.class)
    public void parseExceptionTest() throws Exception {
    	when(rest.exchange(eq(URL_TEST + "api/v1/projectVersions"), eq(HttpMethod.GET), any(), eq(String.class))).thenReturn(new ResponseEntity<String>("parseExceptionBody", HttpStatus.ACCEPTED));
    	fortifyClient.getApplicationArray(URL_TEST);
    }
    
    @Test
    public void projectVersionArrayTest() throws Exception {
    	when(rest.exchange(eq(URL_TEST + "api/v1/projectVersions"), eq(HttpMethod.GET), any(), eq(String.class))).thenReturn(new ResponseEntity<String>(getJson("projectVersions.json"), HttpStatus.ACCEPTED));
    	Map<String, JSONObject> versionObjectWithId = fortifyClient.getApplicationArray(URL_TEST);
    	assertThat(versionObjectWithId.size(), is(1));
    	assertNotNull(versionObjectWithId.get("47243171"));
    }
   
    @Test(expected = ParseException.class)
    public void no_projectVersionArrayTest() throws Exception {
    	when(rest.exchange(eq(URL_TEST + "api/v1/projectVersions"), eq(HttpMethod.GET), any(), eq(String.class))).thenReturn(new ResponseEntity<String>("", HttpStatus.ACCEPTED));
    	Map<String, JSONObject> versionObjectWithId = fortifyClient.getApplicationArray(URL_TEST);
    	assertThat(versionObjectWithId.size(), is(0));
    }
   
    @Test
    public void getAppliactionsTest() throws Exception {
    	when(rest.exchange(eq(URL_TEST + "api/v1/projectVersions"), eq(HttpMethod.GET), any(), eq(String.class))).thenReturn(new ResponseEntity<String>(getJson("projectVersions.json"), HttpStatus.ACCEPTED));
    	Map<String, JSONObject> versionObjectWithId = fortifyClient.getApplicationArray(URL_TEST);
    	List<FortifyProject> projects = defaultFortifyClient.getApplications(URL_TEST, versionObjectWithId.values());
    	assertThat(projects.size(), is(versionObjectWithId.size()));
    	assertThat(projects.get(0).getVersionId(), is("47243171"));
    }
    
    @Test
    public void getFortifyReportTest() throws Exception {
    	when(rest.exchange(eq(URL_TEST + "api/v1/projectVersions"), eq(HttpMethod.GET), any(), eq(String.class))).thenReturn(new ResponseEntity<String>(getJson("projectVersions.json"), HttpStatus.ACCEPTED));
    	Map<String, JSONObject> versionObjectWithId = fortifyClient.getApplicationArray(URL_TEST);
    	List<FortifyProject> projects = defaultFortifyClient.getApplications(URL_TEST, versionObjectWithId.values());
    	FortifyProject project = projects.get(0);
    	project.setId(new ObjectId());
    	when(rest.exchange(anyString(), eq(HttpMethod.GET), any(), eq(String.class))).thenReturn(new ResponseEntity<String>(getJson("fortifyReport.json"), HttpStatus.ACCEPTED));
    	FortifyScanReport report = fortifyClient.getFortifyReport(project, versionObjectWithId.get("47243171"));
    	assertThat(report.getThreats().get("Low").getCount(), is(1));
    	assertThat(report.getThreats().get("Low").getComponents().get(0), is("Bag.java"));
    }
    
    @Test(expected = ParseException.class)
    public void fortifyReportTest() throws Exception {
    	when(rest.exchange(anyString(), eq(HttpMethod.GET), any(), eq(String.class))).thenReturn(new ResponseEntity<String>("parseExceptionBody", HttpStatus.ACCEPTED));
    	fortifyClient.getApplicationArray(URL_TEST);
    }
    
	private String getJson(String fileName) throws IOException, ClassNotFoundException {
    	Class<?> cls = Class.forName("com.capitalone.dashboard.collector.DefaultFortifyClientTest");
    	ClassLoader cLoader = cls.getClassLoader();
    	InputStream inputStream = cLoader.getResourceAsStream(fileName);
        return IOUtils.toString(inputStream);
    }

}
