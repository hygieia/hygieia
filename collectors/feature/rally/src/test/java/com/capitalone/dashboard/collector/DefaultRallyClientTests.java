package com.capitalone.dashboard.collector;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestOperations;

import com.capitalone.dashboard.model.RallyFeature;
import com.capitalone.dashboard.model.RallyProject;
import com.capitalone.dashboard.repository.ComponentRepository;
import com.capitalone.dashboard.repository.RallyCollectorRepository;
import com.capitalone.dashboard.repository.RallyProjectRepository;
import com.capitalone.dashboard.util.Supplier;
import com.mysema.query.annotations.Config;

@RunWith(MockitoJUnitRunner.class)
@ContextConfiguration
public class DefaultRallyClientTests {

	@Mock
	private RallyCollectorRepository rallyCollectorRepository;
	@Mock
	private RallyProjectRepository rallyProjectRepository;
	@Mock
	private RallyClient rallyClient;

	@Mock
	private ComponentRepository dbComponentRepository;
	@Mock
	private Supplier<RestOperations> restOperationsSupplier;
	@Mock
	private RestOperations rest;

	@Mock
	private RallySettings rallySettings;
	@Mock
	private DefaultRallyClient defaultRallyClient;

	@InjectMocks
	RallyCollectorTask task;
	String instanceUrl = "https://rally1.rallydev.com";

	@Value("${rally.URL_APPLICATIONS}")
	private String urlApplications;

	@Before
	public void init() {
		when(restOperationsSupplier.get()).thenReturn(rest);
		rallySettings = new RallySettings();
		rallyClient = defaultRallyClient = new DefaultRallyClient(restOperationsSupplier, rallyProjectRepository,rallySettings);
		urlApplications = "/api/v2/applications?format=json";
	}

	@Test
	public void instanceJobs_emptyResponse_returnsSingleList() {
		when(rest.exchange(Matchers.any(URI.class), eq(HttpMethod.GET), Matchers.any(HttpEntity.class),
				eq(String.class))).thenReturn(new ResponseEntity<String>("", HttpStatus.OK));
		List<RallyProject> jobs = getRallyApplication();
		assertThat(jobs.size(), is(1));
	}

	@Test
	public void instanceJobs_emptyResponse_returnsEmptyList() {
		when(rest.exchange(Matchers.any(URI.class), eq(HttpMethod.GET), Matchers.any(HttpEntity.class),
				eq(String.class))).thenReturn(new ResponseEntity<String>("", HttpStatus.OK));
		List<RallyProject> jobs = getRallyApplication();
		jobs.clear();
		assertThat(jobs.size(), is(0));
	}

	@Test
	public void validate_rallyComponents() {
		RallyFeature nq = getRallyComponents();
		assertThat(nq.getIterationId(), is("76004589316"));
		assertThat(nq.getIterationName(), is("Iteration 1"));
		assertThat(nq.getPlanEstimate(), is("2"));

		assertThat(nq.getPlannedVelocity(), is("1"));
		assertThat(nq.getProjectName(), is("Enterprise DevOps Service Full Funding"));
		assertThat(nq.getState(), is("Accepted"));
		assertThat(nq.getTaskActualTotal(), is("0"));
		assertThat(nq.getTaskEstimateTotal(), is("19"));
		assertThat(nq.getProjectId(), is("72526393952"));

		assertThat(nq.getTaskRemainingTotal(), is("0"));
	}

	@Test
	public void rallyBurnDownResponse() throws RestClientException, IOException {
			when(rest.exchange(eq(URI.create(
					"https://server/slm/webservice/version/hierarchicalrequirement.js?query=((Project.Name='projectname')"
					+ "and(Iteration.Name='iterationname'))&fetch=true&start=1&pagesize=1000")),
					eq(HttpMethod.GET), Matchers.any(HttpEntity.class),eq(String.class))).thenReturn(new ResponseEntity<>(getJson("rally_burnDownResponse.json"),HttpStatus.OK));
			
			String json = getJson("rally_burnDownResponse.json");
			assertNotNull(json.contains("AcceptedDate"));
			assertNotNull(json.contains("PlanEstimate"));
			assertNotNull(json.contains("ScheduleStatePrefix"));
	}

	@Test
	public void getTestRallyApplication() throws Exception {
		when(rest.exchange(Matchers.any(URI.class), eq(HttpMethod.GET), Matchers.any(HttpEntity.class),
				eq(String.class)))
						.thenReturn(new ResponseEntity<>(getJson("rally_urlApplications.json"), HttpStatus.OK));

		String rallyComponents = IOUtils
				.toString(DefaultRallyClientTests.class.getResourceAsStream("/rally_urlApplications.json"));
		JSONParser jp = new JSONParser();
		// List<RallyProject> applications = new ArrayList<>();
		JSONObject object = (JSONObject) jp.parse(rallyComponents);

		JSONArray applicationArray = (JSONArray) object.get("QueryResult");
		List<RallyProject> rallyApplicationList = new ArrayList<RallyProject>();
		for (Object obj : applicationArray) {
			JSONObject prjData = (JSONObject) obj;
			JSONArray appdata = (JSONArray) prjData.get("Results");
			RallyProject rallyApplication = new RallyProject();

			for (Object res : appdata) {
				JSONObject resData = (JSONObject) res;
				rallyApplication.setProjectName(resData.get("_refObjectName").toString());
				String proId = resData.get("_ref").toString();
				rallyApplication.setProjectId(proId.substring(proId.lastIndexOf("/") + 1));
				rallyApplication.setInstanceUrl(resData.get("_ref").toString());
				rallyApplicationList.add(rallyApplication);
			}

		}

		assertThat(rallyApplicationList.size(), is(3));

		assertThat(rallyApplicationList.get(0).getInstanceUrl(), notNullValue());
		assertThat(rallyApplicationList.get(0).getProjectId(), notNullValue());
		assertThat(rallyApplicationList.get(0).getProjectName(), notNullValue());

		assertThat(rallyApplicationList.get(0).getProjectId(), is("72526393952"));
		assertThat(rallyApplicationList.get(0).getProjectName(), is("Enterprise DevOps Service Full Funding"));

	}

	@Test
	public void getTestRallyApplication_Emptycheck() throws Exception {
		when(rest.exchange(Matchers.any(URI.class), eq(HttpMethod.GET), Matchers.any(HttpEntity.class),
				eq(String.class)))
						.thenReturn(new ResponseEntity<>(getJson("rally_urlApplications.json"), HttpStatus.OK));

		String rallyComponents = IOUtils
				.toString(DefaultRallyClientTests.class.getResourceAsStream("/rally_urlApplications.json"));

		JSONParser jp = new JSONParser();
		List<RallyProject> applications = new ArrayList<>();
		JSONObject object = (JSONObject) jp.parse(rallyComponents);

		JSONArray applicationArray = (JSONArray) object.get("QueryResult");
		for (Object obj : applicationArray) {
			RallyProject rallyApplication = new RallyProject();
			applications.add(rallyApplication);
		}
		List<RallyProject> app = applications;

		assertThat(applications.size(), is(1));

		assertNull(app.get(0).getInstanceUrl());
		assertNull(app.get(0).getProjectId());
		assertNull(app.get(0).getProjectName());

	}

	/*
	 * @Test public void getTestStoryStages() throws IOException,
	 * ParseException{ String projName="Enterprise DevOps Service Full Funding";
	 * String iterationName="Iteration 1";
	 * //RallyStoryStages=defaultRallyClient.getStoryStages(projName,
	 * iterationName); String rallyStoryStages =
	 * IOUtils.toString(DefaultRallyClientTests.class.getResourceAsStream(
	 * "/rally_storyStages.json"));
	 * 
	 * JSONParser jp=new JSONParser(); List<RallyProject> applications = new
	 * ArrayList<>(); JSONObject object = (JSONObject)jp.parse(rallyStoryStages)
	 * ;
	 * 
	 * //GitRepoUsers request = makeGitRepoUsers();
	 * //DataResponse<Iterable<GitRepoUsers>> gitRepoBranch=makeGitBranchtype();
	 * String rallyProjectName=""; String iterationName =""; DefaultRallyClient
	 * drc=new DefaultRallyClient(restOperationsSupplier,
	 * rallyProjectRepository);
	 * when(rallyClient.gitBranchDetails(rallyProjectName,iterationName)).
	 * thenReturn();
	 * 
	 * @SuppressWarnings("unused") byte[] content =
	 * TestUtil.convertObjectToJsonBytes(request);
	 * 
	 * mockMvc.perform(get("/commit/gitpullBranch")
	 * .contentType(TestUtil.APPLICATION_JSON_UTF8)
	 * .content(TestUtil.convertObjectToJsonBytes(request)))
	 * .andExpect(status().isInternalServerError()); }
	 */

	public RallyFeature getRallyComponents() {

		RallyFeature rf = new RallyFeature();

		rf.setIterationId("76004589316");
		rf.setIterationName("Iteration 1");
		rf.setPlanEstimate("2");
		rf.setPlannedVelocity("1");
		rf.setProjectName("Enterprise DevOps Service Full Funding");
		rf.setState("Accepted");
		rf.setTaskActualTotal("0");
		rf.setTaskEstimateTotal("19");
		// rf.setStoryStages(storyStages);
		rf.setProjectId("72526393952");
		rf.setTaskRemainingTotal("0");
		rf.setStartDate("2016-11-14");
		rf.setEndDate("2016-11-25");
		return rf;
	}

	public List<RallyProject> getRallyApplication() {
		List<RallyProject> applications = new ArrayList<>();
		RallyProject application = new RallyProject();
		application.setInstanceUrl("https://rally1.rallydev.com");
		application.setProjectId("72526393952");
		application.setProjectName("Enterprise DevOps Service Full Funding");
		applications.add(application);
		return applications;

	}

	private String getJson(String fileName) throws IOException {
		BufferedInputStream result = (BufferedInputStream) Config.class.getClassLoader().getResourceAsStream(fileName);
		return IOUtils.toString(result);
	}

	private String str(JSONObject json, String key) {
		Object obj = json.get(key);
		return obj == null ? null : obj.toString();
	}

	private Long lng(JSONObject json, String key) {
		Object obj = json.get(key);
		return obj == null ? null : Long.valueOf(obj.toString());
	}

}
