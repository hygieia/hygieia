package com.capitalone.dashboard.collector;

import com.capitalone.dashboard.model.CloudInstance;
import com.capitalone.dashboard.model.LibraryPolicyReport;
import com.capitalone.dashboard.model.LibraryPolicyResult;
import com.capitalone.dashboard.model.LibraryPolicyThreatLevel;
import com.capitalone.dashboard.model.LibraryPolicyType;
import com.capitalone.dashboard.model.NexusIQApplication;
import com.capitalone.dashboard.util.Supplier;
import org.apache.commons.io.IOUtils;
import org.json.simple.JSONObject;
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
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestOperations;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isNull;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DefaultNexusIQClientTest {
    @Mock
    private Supplier<RestOperations> restOperationsSupplier;
    @Mock
    private RestOperations rest;
    @Mock
    private NexusIQSettings settings;

    private DefaultNexusIQClient defaultNexusIQClient;
    private static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX";


    @Before
    public void init() {
        when(restOperationsSupplier.get()).thenReturn(rest);
        settings = new NexusIQSettings();
        defaultNexusIQClient = new DefaultNexusIQClient(restOperationsSupplier, settings);
    }

    //
    @Test
    public void getApplications() throws Exception {
        settings.setSelectStricterLicense(true);
        String appJson = getJson("applications.json");

        String instanceUrl = "http://nexusiq.com";
        String appListUrl = "http://nexusiq.com/api/v2/applications";

        doReturn(new ResponseEntity<>(appJson, HttpStatus.OK)).when(rest).exchange(eq(appListUrl), eq(HttpMethod.GET), Matchers.any(HttpEntity.class), eq(String.class));
        List<NexusIQApplication> apps = defaultNexusIQClient.getApplications(instanceUrl);
        assertThat(apps.size(), is(2));
        assertThat(apps.get(0).getApplicationName(), is("Innovation-Challenge-picket"));
        assertThat(apps.get(1).getApplicationName(), is("ABCD4567"));
        assertThat(apps.get(0).getApplicationId(), is("65df87ab0cd04810b18562146b6083bf"));
        assertThat(apps.get(1).getApplicationId(), is("d50b407aeb21480f8bbce3d46f1a5574"));
    }

    @Test
    public void getApplicationsEmpty() throws Exception {
        settings.setSelectStricterLicense(true);
        String appJson = getJson("applications.json");

        String instanceUrl = "http://nexusiq.com";
        String appListUrl = "http://nexusiq.com/api/v2/applications";

        doReturn(new ResponseEntity<>("", HttpStatus.OK)).when(rest).exchange(eq(appListUrl), eq(HttpMethod.GET), Matchers.any(HttpEntity.class), eq(String.class));
        List<NexusIQApplication> apps = defaultNexusIQClient.getApplications(instanceUrl);
        assertThat(apps.size(), is(0));
    }
    @Test
    public void getApplications404() throws Exception {
        settings.setSelectStricterLicense(true);
        String appJson = getJson("applications.json");

        String instanceUrl = "http://nexusiq.com";
        String appListUrl = "http://nexusiq.com/api/v2/applications";

        doThrow(new RestClientException("404")).when(rest).exchange(eq(appListUrl), eq(HttpMethod.GET), Matchers.any(HttpEntity.class), eq(String.class));
        List<NexusIQApplication> apps = defaultNexusIQClient.getApplications(instanceUrl);
        assertThat(apps.size(), is(0));
    }
    @Test
    public void getApplicationReport() throws Exception {
        settings.setSelectStricterLicense(false);
        String appJson = getJson("applications.json");
        String reportJson = getJson("applicationReports.json");

        String instanceUrl = "http://nexusiq.com";
        String reportListUrl = "http://nexusiq.com/api/v2/reports/applications/65df87ab0cd04810b18562146b6083bf";
        String appListUrl = "http://nexusiq.com/api/v2/applications";

        String reportDateS1 = "2017-03-23T15:31:39.680-04:00";
        String reportDateS2 = "2017-02-17T10:12:06.960-05:00";

        doReturn(new ResponseEntity<>(appJson, HttpStatus.OK)).when(rest).exchange(eq(appListUrl), eq(HttpMethod.GET), Matchers.any(HttpEntity.class), eq(String.class));
        List<NexusIQApplication> apps = defaultNexusIQClient.getApplications(instanceUrl);

        doReturn(new ResponseEntity<>(reportJson, HttpStatus.OK)).when(rest).exchange(eq(reportListUrl), eq(HttpMethod.GET), Matchers.any(HttpEntity.class), eq(String.class));
        List<LibraryPolicyReport> reports = defaultNexusIQClient.getApplicationReport(apps.get(0));
        assertThat(reports.size(), is(2));
        assertThat(reports.get(0).getEvaluationDate(), is(timestamp(reportDateS1)));
        assertThat(reports.get(0).getReportDataUrl(), is("http://nexusiq.com/api/v2/applications/Innovation-Challenge-picket/reports/967635a9cdc54d2cb30cf6f87f3a8943"));
        assertThat(reports.get(0).getReportUIUrl(), is("http://nexusiq.com/ui/links/application/Innovation-Challenge-picket/report/967635a9cdc54d2cb30cf6f87f3a8943"));
        assertThat(reports.get(0).getStage(), is("build"));
        assertThat(reports.get(1).getEvaluationDate(), is(timestamp(reportDateS2)));
        assertThat(reports.get(1).getReportDataUrl(), is("http://nexusiq.com/api/v2/applications/Innovation-Challenge-picket/reports/0cd22cb55c27407592dc98f2265ddc9e"));
        assertThat(reports.get(1).getReportUIUrl(), is("http://nexusiq.com/ui/links/application/Innovation-Challenge-picket/report/0cd22cb55c27407592dc98f2265ddc9e"));
        assertThat(reports.get(1).getStage(), is("stage-release"));
    }

    @Test
    public void getDetailedReport() throws Exception {
        settings.setSelectStricterLicense(false);
        String fullReport = getJson("fullreport.json");
        String reportDataUrl = "http://nexus.com/api/v2/applications/Innovation-Challenge-picket/reports/967635a9cdc54d2cb30cf6f87f3a8943";
        doReturn(new ResponseEntity<>(fullReport, HttpStatus.OK)).when(rest).exchange(eq(reportDataUrl), eq(HttpMethod.GET), Matchers.any(HttpEntity.class), eq(String.class));
        LibraryPolicyResult result = defaultNexusIQClient.getDetailedReport(reportDataUrl);
        assertThat(result.getEvaluationTimestamp(), is(0L));
        assertNull(result.getReportUrl());
        Map<LibraryPolicyType, Set<LibraryPolicyResult.Threat>> threats = result.getThreats();

        assertThat(threats.size(), is(2));
        assertThat(threats.containsKey(LibraryPolicyType.License), is(true));
        assertThat(threats.containsKey(LibraryPolicyType.Security), is(true));
        assertThat(threats.containsKey(LibraryPolicyType.Other), is(false));

        Set<LibraryPolicyResult.Threat> licenseThreats = threats.get(LibraryPolicyType.License);
        Set<LibraryPolicyResult.Threat> securityThreats = threats.get(LibraryPolicyType.Security);

        assertThat(threats.size(), is(2));
        assertThat(threats.size(), is(2));
        assertThat(licenseThreats.size(), is(3));
        assertThat(securityThreats.size(), is(2));

        LibraryPolicyResult.Threat threat = getThreat(securityThreats, LibraryPolicyThreatLevel.High);
        assertNotNull(threat);
        assertThat(threat.getCount(), is(8));
        assertThat(threat.getComponents().size(),is(6));

        threat = getThreat(securityThreats, LibraryPolicyThreatLevel.Medium);
        assertNotNull(threat);
        assertThat(threat.getCount(), is(6));
        assertThat(threat.getComponents().size(),is(6));

        threat = getThreat(licenseThreats, LibraryPolicyThreatLevel.High);
        assertNotNull(threat);
        assertThat(threat.getCount(), is(1));
        assertThat(threat.getComponents().size(),is(1));

        threat = getThreat(licenseThreats, LibraryPolicyThreatLevel.Medium);
        assertNotNull(threat);
        assertThat(threat.getCount(), is(3));
        assertThat(threat.getComponents().size(),is(3));

        threat = getThreat(licenseThreats, LibraryPolicyThreatLevel.Low);
        assertNotNull(threat);
        assertThat(threat.getCount(), is(49));
        assertThat(threat.getComponents().size(),is(49));

    }

    @Test
    public void getDetailedReportStrictSecurity() throws Exception {
        String fullReport = getJson("fullreport.json");
        String reportDataUrl = "http://nexus.com/api/v2/applications/Innovation-Challenge-picket/reports/967635a9cdc54d2cb30cf6f87f3a8943";
        settings.setSelectStricterLicense(true);
        doReturn(new ResponseEntity<>(fullReport, HttpStatus.OK)).when(rest).exchange(eq(reportDataUrl), eq(HttpMethod.GET), Matchers.any(HttpEntity.class), eq(String.class));
        LibraryPolicyResult result = defaultNexusIQClient.getDetailedReport(reportDataUrl);
        assertThat(result.getEvaluationTimestamp(), is(0L));
        assertNull(result.getReportUrl());
        Map<LibraryPolicyType, Set<LibraryPolicyResult.Threat>> threats = result.getThreats();

        assertThat(threats.size(), is(2));
        assertThat(threats.containsKey(LibraryPolicyType.License), is(true));
        assertThat(threats.containsKey(LibraryPolicyType.Security), is(true));
        assertThat(threats.containsKey(LibraryPolicyType.Other), is(false));

        Set<LibraryPolicyResult.Threat> licenseThreats = threats.get(LibraryPolicyType.License);
        Set<LibraryPolicyResult.Threat> securityThreats = threats.get(LibraryPolicyType.Security);

        assertThat(threats.size(), is(2));
        assertThat(threats.size(), is(2));
        assertThat(licenseThreats.size(), is(3));
        assertThat(securityThreats.size(), is(2));

        LibraryPolicyResult.Threat threat = getThreat(securityThreats, LibraryPolicyThreatLevel.High);
        assertNotNull(threat);
        assertThat(threat.getCount(), is(8));
        assertThat(threat.getComponents().size(),is(6));

        threat = getThreat(securityThreats, LibraryPolicyThreatLevel.Medium);
        assertNotNull(threat);
        assertThat(threat.getCount(), is(6));
        assertThat(threat.getComponents().size(),is(6));

        threat = getThreat(licenseThreats, LibraryPolicyThreatLevel.High);
        assertNotNull(threat);
        assertThat(threat.getCount(), is(52));
        assertThat(threat.getComponents().size(),is(52));

        threat = getThreat(licenseThreats, LibraryPolicyThreatLevel.Medium);
        assertNotNull(threat);
        assertThat(threat.getCount(), is(3));
        assertThat(threat.getComponents().size(),is(3));

        threat = getThreat(licenseThreats, LibraryPolicyThreatLevel.Low);
        assertNotNull(threat);
        assertThat(threat.getCount(), is(8));
        assertThat(threat.getComponents().size(),is(8));

    }

    @Test
    public void getDetailedReportEmptyResponse() throws Exception {
        String fullReport = getJson("fullreport-empty.json");
        String reportDataUrl = "http://nexus.com/api/v2/applications/Innovation-Challenge-picket/reports/967635a9cdc54d2cb30cf6f87f3a8943";
        settings.setSelectStricterLicense(true);
        doReturn(new ResponseEntity<>(fullReport, HttpStatus.OK)).when(rest).exchange(eq(reportDataUrl), eq(HttpMethod.GET), Matchers.any(HttpEntity.class), eq(String.class));
        LibraryPolicyResult result = defaultNexusIQClient.getDetailedReport(reportDataUrl);
        assertNull(result);
    }

    @Test
    public void getDetailedReport404() throws Exception {
        String reportDataUrl = "http://nexus.com/api/v2/applications/Innovation-Challenge-picket/reports/967635a9cdc54d2cb30cf6f87f3a8943";
        settings.setSelectStricterLicense(true);
        doThrow(new RestClientException("404")).when(rest).exchange(eq(reportDataUrl), eq(HttpMethod.GET), Matchers.any(HttpEntity.class), eq(String.class));
        LibraryPolicyResult result = defaultNexusIQClient.getDetailedReport(reportDataUrl);
        assertNull(result);
    }

    private LibraryPolicyResult.Threat getThreat (Set<LibraryPolicyResult.Threat> threats, LibraryPolicyThreatLevel level) {
        for (Object o : threats.toArray()) {
            LibraryPolicyResult.Threat t = (LibraryPolicyResult.Threat) o;
            if (t.getLevel().equals(level)) {
                return t;
            }
        }
        return null;
    }

    private String getJson(String fileName) throws IOException {
        InputStream inputStream = DefaultNexusIQClientTest.class.getResourceAsStream(fileName);
        return IOUtils.toString(inputStream);
    }

    private long timestamp(String date) throws ParseException {
        return new SimpleDateFormat(DATE_FORMAT).parse(date).getTime();
    }
}