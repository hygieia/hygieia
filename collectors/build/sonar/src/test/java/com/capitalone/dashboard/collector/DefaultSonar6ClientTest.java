package com.capitalone.dashboard.collector;

import com.capitalone.dashboard.model.CodeQuality;
import com.capitalone.dashboard.model.CodeQualityType;
import com.capitalone.dashboard.model.SonarProject;
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
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DefaultSonar6ClientTest {
    @Mock
    private Supplier<RestOperations> restOperationsSupplier;
    @Mock
    private RestOperations rest;
    @Mock
    private SonarSettings settings;
    private DefaultSonar6Client defaultSonar6Client;

    private static final String URL_RESOURCES = "/api/components/search?qualifiers=TRK&ps=10000";
    private static final String URL_RESOURCE_DETAILS = "/api/measures/component?format=json&componentId=%s&metricKeys=%s&includealerts=true";
    private static final String URL_PROJECT_ANALYSES = "/api/project_analyses/search?project=%s";
    private static final String SONAR_URL = "http://sonar.com";
    private static final String METRICS = "ncloc,violations,new_vulnerabilities,critical_violations,major_violations,blocker_violations,tests,test_success_density,test_errors,test_failures,coverage,line_coverage,sqale_index,alert_status,quality_gate_details";

    @Before
    public void init() {
        when(restOperationsSupplier.get()).thenReturn(rest);
        settings = new SonarSettings();
        settings.setMetrics(Collections.singletonList(METRICS));
        defaultSonar6Client = new DefaultSonar6Client(restOperationsSupplier, settings);
    }

    @Test
    public void getProjects() throws Exception {
        String projectJson = getJson("sonar6projects.json");
        String projectsUrl = SONAR_URL + URL_RESOURCES;
        doReturn(new ResponseEntity<>(projectJson, HttpStatus.OK)).when(rest).exchange(eq(projectsUrl), eq(HttpMethod.GET), Matchers.any(HttpEntity.class), eq(String.class));
        List<SonarProject> projects = defaultSonar6Client.getProjects(SONAR_URL);
        assertThat(projects.size(), is(2));
        assertThat(projects.get(0).getProjectName(), is("com.capitalone.test:TestProject"));
        assertThat(projects.get(1).getProjectName(), is("com.capitalone.test:AnotherTestProject"));
        assertThat(projects.get(0).getProjectId(), is("AVu3b-MAphY78UZXuYHp"));
        assertThat(projects.get(1).getProjectId(), is("BVx3b-MAphY78UZXuYHp"));
    }

    @Test
    public void currentCodeQuality() throws Exception {
        String measureJson = getJson("sonar6measures.json");
        String analysesJson = getJson("sonar6analyses.json");
        SonarProject project = getProject();
        String measureUrl = String.format(SONAR_URL + URL_RESOURCE_DETAILS,project.getProjectId(),METRICS);
        String analysesUrl = String.format(SONAR_URL + URL_PROJECT_ANALYSES,project.getProjectName());
        doReturn(new ResponseEntity<>(measureJson, HttpStatus.OK)).when(rest).exchange(eq(measureUrl), eq(HttpMethod.GET), Matchers.any(HttpEntity.class), eq(String.class));
        doReturn(new ResponseEntity<>(analysesJson, HttpStatus.OK)).when(rest).exchange(eq(analysesUrl), eq(HttpMethod.GET), Matchers.any(HttpEntity.class), eq(String.class));
        CodeQuality quality = defaultSonar6Client.currentCodeQuality(getProject(),settings.getMetrics().get(0));
        assertThat(quality.getMetrics().size(), is(15));
        assertThat(quality.getType(), is (CodeQualityType.StaticAnalysis));
        assertThat(quality.getName(), is ("com.capitalone.test:TestProject"));
        assertThat(quality.getVersion(), is ("2.0.0"));
    }


    private String getJson(String fileName) throws IOException {
        InputStream inputStream = DefaultSonar6ClientTest.class.getResourceAsStream(fileName);
        return IOUtils.toString(inputStream);
    }


    private SonarProject getProject() {
        SonarProject project = new SonarProject();
        project.setInstanceUrl(SONAR_URL);
        project.setProjectName("com.capitalone.test:TestProject");
        project.setProjectId("AVu3b-MAphY78UZXuYHp");
        return project;
    }
}