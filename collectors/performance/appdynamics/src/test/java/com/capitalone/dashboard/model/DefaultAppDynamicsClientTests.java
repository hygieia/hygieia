package com.capitalone.dashboard.model;

import com.capitalone.dashboard.collector.AppdynamicsClient;
import com.capitalone.dashboard.collector.AppdynamicsSettings;
import com.capitalone.dashboard.collector.DefaultAppdynamicsClient;
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
import java.net.URI;
import java.util.Map;
import java.util.Set;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

/**
 * Created by syq410 on 5/5/17.
 */
@RunWith(MockitoJUnitRunner.class)
public class DefaultAppDynamicsClientTests {

    @Mock
    private Supplier<RestOperations> restOperationsSupplier;
    @Mock private RestOperations rest;
    @Mock private AppdynamicsSettings settings;
    @Mock private AppdynamicsClient appdynamicsClient;
    private DefaultAppdynamicsClient defaultAppdynamicsClient;

    private static final String URL_TEST = "http://server/job/job2/2/";
    private static final int PAGE_SIZE = 10;

    @Before
    public void init() {
        when(restOperationsSupplier.get()).thenReturn(rest);
        settings = new AppdynamicsSettings();
        defaultAppdynamicsClient = new DefaultAppdynamicsClient(settings,restOperationsSupplier);
    }

    @Test
    public void joinURLsTest() throws Exception {
        String u = DefaultAppdynamicsClient.joinURL("http://appdyn-hqa-c01.kdc.capitalone.com/","app1","app2","app3" );
        assertEquals("http://appdyn-hqa-c01.kdc.capitalone.com/app1/app2/app3", u);
        String u1 = DefaultAppdynamicsClient.joinURL("http://appdyn-hqa-c01.kdc.capitalone.com","app1","app2","app3" );
        assertEquals("http://appdyn-hqa-c01.kdc.capitalone.com/app1/app2/app3", u1);
        String u2 = DefaultAppdynamicsClient.joinURL("http://appdyn-hqa-c01.kdc.capitalone.com","/app1/","/app2/","/app3" );
        assertEquals("http://appdyn-hqa-c01.kdc.capitalone.com/app1/app2/app3", u2);

    }

    @Test
    public void testGetApplications() throws Exception{

        String appJSON = getJson("application.json") ;
        URI appListUrl = URI.create("http://server/job/job2/2/controller/rest/applications?output=json");
        when(rest.exchange(eq(appListUrl), eq(HttpMethod.GET), Matchers.any(null), eq(String.class)))
                .thenReturn(new ResponseEntity<>(appJSON, HttpStatus.OK));
        Set<AppdynamicsApplication>  appDynamicsSet = defaultAppdynamicsClient.getApplications("http://server/job/job2/2/");
        assertThat(appDynamicsSet.size(), is(2));

    }

    @Test
    public void testGetApplicationsNoDescription() throws Exception{

        String appJSON = getJson("application_no_desc.json") ;
        URI appListUrl = URI.create("http://server/job/job2/2/controller/rest/applications?output=json");
        when(rest.exchange(eq(appListUrl), eq(HttpMethod.GET), Matchers.any(null), eq(String.class)))
                .thenReturn(new ResponseEntity<>(appJSON, HttpStatus.OK));
        Set<AppdynamicsApplication>  appDynamicsSet = defaultAppdynamicsClient.getApplications("http://server/job/job2/2/");
        assertThat(appDynamicsSet.size(), is(2));
        AppdynamicsApplication expected = (AppdynamicsApplication) appDynamicsSet.toArray()[0] ;
        assertEquals(expected.getDescription(),"AAA");
    }

    @Test
    public void testGetPerformanceMetrics() throws Exception{

        String appJSON = getJson("application_metrics.json") ;
        String appHealthMetricsJSON = getJson("application_Health_metrics.json") ;
        String appEmptyMetricsJSON = getJson("application_Empty_metrics.json") ;
        String appBusinessHealthMetricsJSON = getJson("application_BusinessHealth_metrics.json") ;
        URI appListUrl = URI.create("http://server.com/controller/rest/applications/3306/metric-data?metric-path=Overall+Application+Performance%7C*&time-range-type=BEFORE_NOW&duration-in-mins=15&output=json");
        String instanceUrl = "http://server.com/";

        URI appHealthMetricsUrl = URI.create("http://server.com/controller/rest/applications/3306/problems/healthrule-violations?time-range-type=BEFORE_NOW&duration-in-mins=15&output=json");
        URI appNodeHealthURI = URI.create("http://server.com/controller/rest/applications/3306/nodes?output=json");
        URI appBusinessTransactionListUrl = URI.create("http://server.com/controller/rest/applications/3306/business-transactions?output=json");
        URI appViolationUrl = URI.create("http://server.com/controller/rest/applications/3306/problems/healthrule-violations?time-range-type=BEFORE_NOW&duration-in-mins=15&output=json");
        URI appSevertyMetricsUrl = URI.create("http://server.com/controller/rest/applications/3306/problems/healthrule-violations?time-range-type=BEFORE_NOW&duration-in-mins=15&output=json");

        AppdynamicsApplication appdynamicsApplication = new AppdynamicsApplication();
        appdynamicsApplication.setAppID("3306");

        when(rest.exchange(eq(appListUrl), eq(HttpMethod.GET), Matchers.any(null), eq(String.class)))
                .thenReturn(new ResponseEntity<>(appJSON, HttpStatus.OK));

        when(rest.exchange(eq(appHealthMetricsUrl), eq(HttpMethod.GET), Matchers.any(null), eq(String.class)))
                .thenReturn(new ResponseEntity<>(appEmptyMetricsJSON, HttpStatus.OK));

        when(rest.exchange(eq(appNodeHealthURI), eq(HttpMethod.GET), Matchers.any(null), eq(String.class)))
                .thenReturn(new ResponseEntity<>(appHealthMetricsJSON, HttpStatus.OK));

        when(rest.exchange(eq(appBusinessTransactionListUrl), eq(HttpMethod.GET), Matchers.any(null), eq(String.class)))
                .thenReturn(new ResponseEntity<>(appBusinessHealthMetricsJSON, HttpStatus.OK));

        when(rest.exchange(eq(appViolationUrl), eq(HttpMethod.GET), Matchers.any(null), eq(String.class)))
                .thenReturn(new ResponseEntity<>(appEmptyMetricsJSON, HttpStatus.OK));

        when(rest.exchange(eq(appSevertyMetricsUrl), eq(HttpMethod.GET), Matchers.any(null), eq(String.class)))
                .thenReturn(new ResponseEntity<>(appEmptyMetricsJSON, HttpStatus.OK));



        Map<String,Object> appDynamicsPerformanceMetris = defaultAppdynamicsClient.getPerformanceMetrics(appdynamicsApplication,instanceUrl);
        System.out.println(appDynamicsPerformanceMetris);
        assertEquals(new Long(0),appDynamicsPerformanceMetris.get("responseTimeSeverity"));
        assertEquals(new Long(0),appDynamicsPerformanceMetris.get("errorsperMinute"));
        assertEquals(new Double(1.0),appDynamicsPerformanceMetris.get("businessTransactionHealthPercent"));
        assertEquals(new Long(1),appDynamicsPerformanceMetris.get("averageResponseTime"));
        assertEquals(new Double(1.0),appDynamicsPerformanceMetris.get("nodeHealthPercent"));
        assertEquals(new Long(0),appDynamicsPerformanceMetris.get("errorRateSeverity"));
        assertEquals(new Long(8),appDynamicsPerformanceMetris.get("callsperMinute"));
        assertEquals(new Long(120),appDynamicsPerformanceMetris.get("totalCalls"));
        assertEquals(new Long(0),appDynamicsPerformanceMetris.get("totalErrors"));
    }


    private String getJson(String fileName) throws IOException {
        InputStream inputStream = DefaultAppdynamicsClient.class.getResourceAsStream(fileName);
        return IOUtils.toString(inputStream);
    }
}
