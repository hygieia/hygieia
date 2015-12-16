package com.capitalone.dashboard.collector;

import com.capitalone.dashboard.model.Environment;
import com.capitalone.dashboard.model.UDeployApplication;
import com.capitalone.dashboard.model.UDeployEnvResCompData;
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
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DefaultUDeployClientTest {
    @Mock private Supplier<RestOperations> restOperationsSupplier;
    @Mock private RestOperations rest;
    @Mock private UDeploySettings settings;

    private DefaultUDeployClient defaultUDeployClient;

//    private static final String URL = "URL";


    @Before
    public void init() {
        when(restOperationsSupplier.get()).thenReturn(rest);
        settings = new UDeploySettings();
        defaultUDeployClient = new DefaultUDeployClient(settings, restOperationsSupplier);
    }
    @Test
    public void testGetApplications() throws Exception {
        String appJson = getJson("application.json");

        String instanceUrl = "http://udeploy.com/";
        String appListUrl = "http://udeploy.com/rest/deploy/application";

        when(rest.exchange(eq(appListUrl), eq(HttpMethod.GET), Matchers.any(HttpEntity.class), eq(String.class)))
                .thenReturn(new ResponseEntity<String>(appJson, HttpStatus.OK));
        List<UDeployApplication> apps = defaultUDeployClient.getApplications(instanceUrl);
        assertThat(apps.size(), is(2));
        assertThat(apps.get(0).getApplicationName(), is("AA-JPetstore"));
        assertThat(apps.get(1).getApplicationName(), is("AAA"));
    }

    @Test
    public void testGetEnvironments() throws Exception {
        String appJson = getJson("application.json");

        String instanceUrl = "http://udeploy.com/";
        String appListUrl = "http://udeploy.com/rest/deploy/application";

        when(rest.exchange(eq(appListUrl), eq(HttpMethod.GET), Matchers.any(HttpEntity.class), eq(String.class)))
                .thenReturn(new ResponseEntity<String>(appJson, HttpStatus.OK));
        List<UDeployApplication> apps = defaultUDeployClient.getApplications(instanceUrl);

        String environments = getJson("environments.json");
        String envUrl = "http://udeploy.com/rest/deploy/application/ad88482e-3577-44cd-a6d8-00056062260b/environments/false";

        when(rest.exchange(eq(envUrl), eq(HttpMethod.GET), Matchers.any(HttpEntity.class), eq(String.class)))
                .thenReturn(new ResponseEntity<String>(environments, HttpStatus.OK));

        List<Environment> envs = defaultUDeployClient.getEnvironments(apps.get(0));

        assertThat(envs.size(), is(6));
        assertThat(envs.get(0).getName(), is("Team1"));
        assertThat(envs.get(0).getId().toString(), is("e32de740-160b-4ffb-a63f-0690607d9903"));
    }



    @Test
    public void testGetEnvironmentResourceStatusData() throws Exception {
        String resourceJson = getJson("resources.json");
        String nonComplianceJson = getJson("noncompliance.json");

        String appJson = getJson("application.json");

        String instanceUrl = "http://udeploy.com/";
        String appListUrl = "http://udeploy.com/rest/deploy/application";

        when(rest.exchange(eq(appListUrl), eq(HttpMethod.GET), Matchers.any(HttpEntity.class), eq(String.class)))
                .thenReturn(new ResponseEntity<String>(appJson, HttpStatus.OK));
        List<UDeployApplication> apps = defaultUDeployClient.getApplications(instanceUrl);

        String resourceUrl = "http://udeploy.com/rest/deploy/environment/e32de740-160b-4ffb-a63f-0690607d9903/resources";
        String nonCompUrl = "http://udeploy.com/rest/deploy/environment/e32de740-160b-4ffb-a63f-0690607d9903/noncompliantResources";


        String environments = getJson("environments.json");
        String envUrl = "http://udeploy.com/rest/deploy/application/ad88482e-3577-44cd-a6d8-00056062260b/environments/false";

        when(rest.exchange(eq(envUrl), eq(HttpMethod.GET), Matchers.any(HttpEntity.class), eq(String.class)))
                .thenReturn(new ResponseEntity<String>(environments, HttpStatus.OK));

        List<Environment> envs = defaultUDeployClient.getEnvironments(apps.get(0));


        when(rest.exchange(eq(resourceUrl), eq(HttpMethod.GET), Matchers.any(HttpEntity.class), eq(String.class)))
                .thenReturn(new ResponseEntity<String>(resourceJson, HttpStatus.OK));

        when(rest.exchange(eq(nonCompUrl), eq(HttpMethod.GET), Matchers.any(HttpEntity.class), eq(String.class)))
                .thenReturn(new ResponseEntity<String>(nonComplianceJson, HttpStatus.OK));

        List<UDeployEnvResCompData> data = defaultUDeployClient.getEnvironmentResourceStatusData(apps.get(0), envs.get(0));


        assertThat(data.size(), is(2));
        assertThat(data.get(0).getComponentName(), is("AA-JPetstore.war"));
        assertThat(data.get(0).getResourceName(), is("msp_16"));
        assertThat(data.get(0).isDeployed(), is(false));
        assertThat(data.get(0).isOnline(), is(true));
        assertThat(data.get(0).getEnvironmentName(), is("Team1"));
        assertThat(data.get(1).getComponentName(), is("AA-JPetstore.ear"));
        assertThat(data.get(1).getResourceName(), is("msp_16"));
        assertThat(data.get(1).isDeployed(), is(true));
        assertThat(data.get(1).isOnline(), is(true));
        assertThat(data.get(1).getEnvironmentName(), is("Team1"));

    }


    private String getJson(String fileName) throws IOException {
        InputStream inputStream = DefaultUDeployClientTest.class.getResourceAsStream(fileName);
        return IOUtils.toString(inputStream);
    }
}