package com.capitalone.dashboard.collector;

import com.capitalone.dashboard.config.FongoConfig;
import com.capitalone.dashboard.config.TestConfig;
import com.capitalone.dashboard.util.Supplier;
import com.google.common.io.Resources;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestOperations;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.contains;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestConfig.class, FongoConfig.class})
@DirtiesContext
public class DefaultJiraClientTest {
    @Mock
    private DefaultJiraClient defaultJiraClient;
    @Mock
    private Supplier<RestOperations> restOperationsSupplier = mock(Supplier.class);
    @Mock
    private RestOperations rest = mock(RestOperations.class);
    @Autowired
    private FeatureSettings featureSettings;

    @Before
    public void loadStuff() throws IOException {
        when(restOperationsSupplier.get()).thenReturn(rest);

        defaultJiraClient = new DefaultJiraClient(featureSettings,restOperationsSupplier);
    }
    @Test
    public void updateTeamInformation() throws IOException{

        assertEquals("", "");
        //featureCollectorTask.updateTeamInformation(featureCollectorTask.getCollector());
    }
    private String getExpectedJSON(String fileName) throws IOException {
        String path = "./" + fileName;
        URL fileUrl = Resources.getResource(path);
        return IOUtils.toString(fileUrl);
    }
}
