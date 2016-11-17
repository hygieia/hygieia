package com.capitalone.dashboard.collector;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.matches;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestOperations;

import com.capitalone.dashboard.model.Environment;
import com.capitalone.dashboard.model.XLDeployApplication;
import com.capitalone.dashboard.model.XLDeployApplicationHistoryItem;
import com.capitalone.dashboard.util.Supplier;

@RunWith(MockitoJUnitRunner.class)
public class DefaultXLDeployClientTest {
    @Mock private Supplier<RestOperations> restOperationsSupplier;
    @Mock private RestOperations rest;
    @Mock private XLDeploySettings settings;
    
    private DefaultXLDeployClient defaultXLDeployClient;
    
    @Before
    public void init() {
        when(restOperationsSupplier.get()).thenReturn(rest);
        settings = new XLDeploySettings();
        settings.setServers(Collections.singletonList("http://xldeploy.company.com"));
        defaultXLDeployClient = new DefaultXLDeployClient(settings, restOperationsSupplier);
    }
    
    @Test
    public void testGetApplications() throws Exception {
        String appXml = getXml("application.xml");

        String instanceUrl = "http://xldeploy.com:4516";
        String appListUrl = "http://xldeploy.com:4516/deployit/repository/query?type=udm.Application&resultsPerPage=-1";

        when(rest.exchange(eq(appListUrl), eq(HttpMethod.GET), Matchers.any(HttpEntity.class), eq(String.class)))
                .thenReturn(new ResponseEntity<>(appXml, HttpStatus.OK));
        List<XLDeployApplication> apps = defaultXLDeployClient.getApplications(instanceUrl);
        assertThat(apps.size(), is(2));
        assertThat(apps.get(0).getApplicationName(), is("Helloworld"));
        assertThat(apps.get(0).getApplicationType(), is("udm.Application"));
        assertThat(apps.get(1).getApplicationName(), is("Goodbyeworld"));
        assertThat(apps.get(1).getApplicationType(), is("udm.ExtendedApplication"));
    	
    }
    
    @Test
    public void testGetEnvironments() throws Exception {
        String appXml = getXml("environments.xml");

        String instanceUrl = "http://xldeploy.com:4516";
        String appListUrl = "http://xldeploy.com:4516/deployit/repository/query?type=udm.Environment&resultsPerPage=-1";

        when(rest.exchange(eq(appListUrl), eq(HttpMethod.GET), Matchers.any(HttpEntity.class), eq(String.class)))
                .thenReturn(new ResponseEntity<>(appXml, HttpStatus.OK));
        List<Environment> envs = defaultXLDeployClient.getEnvironments(instanceUrl);
        assertThat(envs.size(), is(2));
        assertThat(envs.get(0).getName(), is("Production"));
        assertThat(envs.get(0).getType(), is("udm.Environment"));
        assertThat(envs.get(1).getName(), is("QA01"));
        assertThat(envs.get(1).getType(), is("udm.ExtendedEnvironment"));
    	
    }
    
    @Test
    public void testGetApplicationHistory() throws Exception {
    	String appHistXml = getXml("applicationHistory1.xml");
    	String appHistPostXml = getXml("applicationHistoryPost1.xml");
    	
        String instanceUrl = "http://xldeploy.com:4516";
        String appListUrl = "http://xldeploy.com:4516/deployit/internal/reports/tasks\\?filterType=application&begin=[^&]+&end=[^&]+";
    	
    	XLDeployApplication app = new XLDeployApplication();
    	app.setApplicationId("Applications/folder/Helloworld");
    	app.setApplicationName("Helloworld");
    	app.setApplicationType("udm.Application");
    	app.setInstanceUrl(instanceUrl);

        @SuppressWarnings("rawtypes")
		ArgumentCaptor<HttpEntity> entCaptor = ArgumentCaptor.forClass(HttpEntity.class);
        
        when(rest.exchange(matches(appListUrl), eq(HttpMethod.POST), entCaptor.capture(), eq(String.class)))
                .thenReturn(new ResponseEntity<>(appHistXml, HttpStatus.OK));
        List<XLDeployApplicationHistoryItem> hist = defaultXLDeployClient.getApplicationHistory(app, Calendar.getInstance().getTime(), Calendar.getInstance().getTime());
        
        assertThat(((String)entCaptor.getValue().getBody()).replaceAll("(\r|\n|\t)", ""), is(appHistPostXml.replaceAll("(\r|\n|\t)", "")));
        
        assertThat(hist.size(), is(2));
        assertThat(hist.get(0).getEnvironmentName(), is("QA01"));
        assertThat(hist.get(0).getDeploymentPackage(), is("Helloworld/helloworld-v1.0.0"));
        assertThat(hist.get(0).getEnvironmentId(), is("Environments/qa/QA01"));
        assertThat(hist.get(0).getType(), is("Initial"));
        assertThat(hist.get(0).getUser(), is("admin"));
        assertThat(hist.get(0).getTaskId(), is("db0afc80-eedf-410e-bc43-4a0c3fdd47e2"));
        assertThat(hist.get(0).getStartDate(), is(1457967862982L));
        assertThat(hist.get(0).getCompletionDate(), is(1457967864567L));
        assertThat(hist.get(0).getStatus(), is("DONE"));
        
        assertThat(hist.get(1).getEnvironmentName(), is("Production"));
        assertThat(hist.get(1).getDeploymentPackage(), is("Helloworld/helloworld-v1.0.0"));
    }
    
    private String getXml(String fileName) throws IOException {
        InputStream inputStream = DefaultXLDeployClientTest.class.getResourceAsStream(fileName);
        return IOUtils.toString(inputStream);
    }
}
