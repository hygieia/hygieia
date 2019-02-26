package com.capitalone.dashboard.collector.coverity.soap;

import static java.util.Collections.emptyList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doReturn;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.ws.WebServiceException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import coverity.ws.configuration.ConfigurationServiceService;
import coverity.ws.configuration.ProjectDataObj;
import coverity.ws.configuration.ProjectFilterSpecDataObj;
import coverity.ws.defect.DefectServiceService;
import coverity.ws.defect.MergedDefectsPageDataObj;

@RunWith(MockitoJUnitRunner.class)
public class CoveritySoapClientTest  {

    @Spy private CoveritySoapClient clientSpy;

    private static final String URL_TEST = "http://mockServer.com:8080/ssc";

    @Before
    public void init() {
        clientSpy.setServerDetails(covServerDetails());
    }

    @Test
    public void noProjectsInCoverityReturnsEmptyList() throws Exception {

        doReturn(emptyList())
            .when(clientSpy)
            .getProjectsStubbable(any(ProjectFilterSpecDataObj.class));
        // prevent test from trying to "ping" for wsdl at mock server url
        doReturn(new ConfigurationServiceService())
            .when(clientSpy)
            .stubbableNewCofigSrvcSrvc(anyString());

    	List<ProjectDataObj> list = clientSpy.getAllProjects(URL_TEST);
    	assertTrue(list.isEmpty());
    }

    @Test(expected = WebServiceException.class)
    public void noCredentialsForUnknownInstance_getProjects() throws Exception {
    	clientSpy.getAllProjects("http://example.com");
    }

    @Test
    public void projectWithNoDefectsReturnsEmptyList() throws Exception {

        MergedDefectsPageDataObj dummy = new MergedDefectsPageDataObj();
        dummy.setTotalNumberOfRecords(0);

        doReturn(dummy)
            .when(clientSpy)
            .getMergedDefectsForStreamsStubbable(any(), any(), any(), any());
        // prevent test from trying to "ping" for wsdl at mock server url
        doReturn(new DefectServiceService())
            .when(clientSpy)
            .stubbableNewDefectSrvcSrvc(anyString());

        MergedDefectsPageDataObj defectPage = clientSpy.getSecurityDefectsForStream(URL_TEST, "app", 0);

        assertEquals("Expected zero records", (Integer) 0, defectPage.getTotalNumberOfRecords());
    }

    @Test(expected = WebServiceException.class)
    public void noCredentialsForUnknownInstance_getDefects() throws Exception {
        clientSpy.getSecurityDefectsForStream("http://example.com", "dummy", 0);
    }

    /**
     * Helper method to create details for a fake Coverity server
     * @return Configuration with Coverity server
     */
    private Set<Map<String,String>> covServerDetails() {

        Set<Map<String,String>> coverityServersCollection = new HashSet<>();

        Map<String,String> coverityServer = new HashMap<>();

        coverityServer.put("url", URL_TEST);
        coverityServer.put("userName", "test");
        coverityServer.put("password", "testpwd");

        coverityServersCollection.add(coverityServer);

        return coverityServersCollection;
    }
}
