package com.capitalone.dashboard.collector;

import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import javax.annotation.Nonnull;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestOperations;

import com.capitalone.dashboard.model.RallyBurnDownData;
import com.capitalone.dashboard.model.RallyFeature;
import com.capitalone.dashboard.model.RallyProject;
import com.capitalone.dashboard.model.RallyStoryStages;
import com.capitalone.dashboard.model.UserStory;
import com.capitalone.dashboard.repository.RallyProjectRepository;
import com.capitalone.dashboard.util.Supplier;

@Component
public class DefaultRallyClient implements RallyClient {
	
	private static final Log LOG = LogFactory.getLog(DefaultRallyClient.class);

	private static final String URL_PROJECTS = "/slm/webservice/v2.0/project/";
	
	private static final String ID = "_ref";
	private static final String NAME = "_refObjectName";

	private static final String PLANESTIMATE = "PlanEstimate";
	private static final String PLANNEDVELOCITY = "PlannedVelocity";
	private static final String STATE = "State";
	private static final String TASKACTUALTOTAL = "TaskActualTotal";
	private static final String TASKESTIMATETOTAL = "TaskEstimateTotal";
	private static final String TASKREMAININGTOTAL = "TaskRemainingTotal";
	private final RallyProjectRepository rallyProjectRepository;
	private final RestOperations rest;
	private final RallySettings rallySettings;

	@Autowired
	public DefaultRallyClient(Supplier<RestOperations> restOperationsSupplier,
			RallyProjectRepository rallyProjectRepository, RallySettings rallySettings) {
		this.rest = restOperationsSupplier.get();
		this.rallyProjectRepository = rallyProjectRepository;
		this.rallySettings = rallySettings;
	}

	@Override
	public List<RallyProject> getProjects(String instanceUrl) throws ParseException {
		List<RallyProject> projects = new ArrayList<>();
		String url = instanceUrl + URL_PROJECTS
				+ "?query=(Iterations.ObjectID != null)&start=1&pagesize=2000&fetch=iterations";
		JSONObject object = parseAsObject(url);
		JSONObject queryResult = (JSONObject) object.get("QueryResult");
		JSONArray projectArray = (JSONArray) queryResult.get("Results");

		for (Object obj : projectArray) {
			JSONObject prjData = (JSONObject) obj;
			JSONObject iterationData = (JSONObject) prjData.get("Iterations");
			if (Integer.parseInt(iterationData.get("Count").toString()) > 0) {
				RallyProject project = new RallyProject();
				project.setInstanceUrl(instanceUrl);
				project.setProjectId(str(prjData, ID).substring(str(prjData, ID).lastIndexOf("/") + 1));
				project.setProjectName(str(prjData, NAME));
				projects.add(project);
			}
		}
		return projects;
	}

	@Override
	public List<RallyFeature> getRallyIterations(RallyProject project) throws RestClientException {
		String rallyProjectId = project.getProjectId();
		String rallyProjectInstanceUrl = project.getInstanceUrl();
		String rallyProjectName = project.getProjectName();
		List<RallyFeature> iterations = new ArrayList<>();
		Date dte = new Date();
		Date iterationEndDate;
		long milliSeconds = dte.getTime();
		RallyFeature iteration = null;
		Calendar cal = Calendar.getInstance();
		Calendar calendar = Calendar.getInstance();
		cal.add(Calendar.HOUR, 0);
		SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd HH");
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

		String result = s.format(cal.getTime());

		String urlPro = rallyProjectInstanceUrl + URL_PROJECTS + rallyProjectId + "?start=1&pagesize=2000";
		try {
			JSONObject specIterationObject = parseAsObject(urlPro);
			JSONObject specIterationProject = (JSONObject) specIterationObject.get("Project");
			JSONObject specIterationResult = (JSONObject) specIterationProject.get("Iterations");
			if (Integer.parseInt(specIterationResult.get("Count").toString()) > 0) {
				JSONObject iterationListDetails = parseAsObject(specIterationResult.get("_ref")
						+ "?query=((StartDate <= \"today\") AND (EndDate >= \"today\"))&fetch=true&start=1&pagesize=2000"
								.toString());

				JSONObject queryResult = (JSONObject) iterationListDetails.get("QueryResult");
				JSONArray iterationArray = (JSONArray) queryResult.get("Results");
				for (Object obj : iterationArray) {
					iteration = new RallyFeature();
					JSONObject itrData = (JSONObject) obj;
					iteration.setCollectorItemId(project.getId());
					iteration.setProjectId(rallyProjectId);
					iteration.setProjectName(rallyProjectName);
					iteration.setInstanceUrl(itrData.get("_ref").toString());
					iteration.setIterationId(itrData.get("ObjectID").toString());
					iteration.setIterationName(itrData.get("_refObjectName").toString());
					iteration.setPlanEstimate(str(itrData, PLANESTIMATE));
					iteration.setPlannedVelocity(str(itrData, PLANNEDVELOCITY));
					iteration.setState(str(itrData, STATE));
					iteration.setTaskActualTotal(str(itrData, TASKACTUALTOTAL));
					iteration.setTaskEstimateTotal(str(itrData, TASKESTIMATETOTAL));
					iteration.setTaskRemainingTotal(str(itrData, TASKREMAININGTOTAL));
					iteration.setStartDate(itrData.get("StartDate").toString().substring(0, 10));
					iterationEndDate = format.parse(itrData.get("EndDate").toString().substring(0, 10));
					calendar.setTime(iterationEndDate);
					calendar.add(Calendar.DATE, -1);
					iteration.setEndDate(format.format(calendar.getTime()));
					try {
						iteration
								.setRemainingDays(getRemainingDays(itrData.get("StartDate").toString().substring(0, 10),
										itrData.get("EndDate").toString().substring(0, 10), dte));
					} catch (java.text.ParseException e) {
						LOG.info("parse exception is : " + e.getMessage());
					}
					iteration.setLastUpdated(milliSeconds);
					iteration.setLastExecuted(result);
					iteration.setStoryStages(iteration.getStoryStages());
					iteration.setUserListCount(getIterationUserCount(itrData.get("ObjectID").toString()));
					iteration.setTimestamp(milliSeconds);
					iteration.setCollectorItemId(project.getId());
					
					iterations.add(iteration);
				}
			}

		} catch (ParseException e) {
			LOG.error("Could not parse response from: " + urlPro, e);
		} catch (java.text.ParseException e) {
			LOG.error("Could not parse date ");
		}

		return iterations;
	}

	@Override
	public JSONArray getIterationStories(RallyFeature iteration) throws ParseException, RestClientException {

		String rpn = "\"" + iteration.getProjectName() + "\"";
		String itn = "\"" + iteration.getIterationName() + "\"";
		String baseURL = "https://rally1.rallydev.com/slm/webservice/v2.0/";
		String suffixURL = "?query=((Project.Name = " + rpn + ") and (Iteration.Name = " + itn
				+ "))&fetch=true&start=1&pagesize=2000";
		String userStoryPath = "hierarchicalrequirement.js";
		String defectPath = "defect.js";

		String storyStagesUrl = baseURL + userStoryPath + suffixURL;
		String defectsUrl = baseURL + defectPath + suffixURL;
		JSONObject specIterationObject = parseAsObject(storyStagesUrl);
		JSONObject queryResult = (JSONObject) specIterationObject.get("QueryResult");
		JSONArray iterationArray = (JSONArray) queryResult.get("Results");

		JSONObject defects = parseAsObject(defectsUrl);
		JSONObject defectQueryResult = (JSONObject) defects.get("QueryResult");
		JSONArray defectResults = (JSONArray) defectQueryResult.get("Results");

		iterationArray.addAll(defectResults);

		return iterationArray;
	}

	private String getIterationUserCount(String iterationId) {

		String userCapacityUrl = "https://rally1.rallydev.com/slm/webservice/v2.0/Iteration/" + iterationId
				+ "/UserIterationCapacities?fetch=true&start=1&pagesize=2000";
		String userCount = "";
		try {
			JSONObject userIterationObject = parseAsObject(userCapacityUrl);
			JSONObject queryResult = (JSONObject) userIterationObject.get("QueryResult");
			userCount = queryResult.get("TotalResultCount").toString();
		} catch (ParseException pe) {
			LOG.error("Could not parse response from: " + userCapacityUrl, pe);
		}

		return userCount;

	}

	@Override
	public RallyStoryStages getStoryStages(String rallyProjectId, JSONArray iterationArray) {
		List<UserStory> userStories = new ArrayList<>();
		UserStory userStory = null;
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		String buildStoryUrl = "https://rally1.rallydev.com/#/";
		Date date = new Date();

		int accepted = 0;
		int backlog = 0;
		int completed = 0;
		int defined = 0;
		int inProgress = 0;
		int defects = 0;

		RallyStoryStages rs = new RallyStoryStages();

		for (int i = 0; i < iterationArray.size(); i++) {
			userStory = new UserStory();
			JSONObject stgResult = (JSONObject) iterationArray.get(i);
			JSONObject owner = (JSONObject) stgResult.get("Owner");
			try {
				date = (Date) format.parse(stgResult.get("LastUpdateDate").toString());
				userStory.setLastUpdateDate(date.getTime());
			} catch (java.text.ParseException e) {
				LOG.error("Parse exception " + e);
			}

			if (owner != null) {
				userStory.setOwnerName(owner.get("_refObjectName").toString());
			} else {
				userStory.setOwnerName("-");
			}
			if (stgResult.get("_refObjectName") != null && (!stgResult.get("_refObjectName").equals(""))) {
				userStory.setStoryName(stgResult.get("_refObjectName").toString());
			} else {
				userStory.setStoryName("-");
			}
			userStory.setStoryId(stgResult.get("FormattedID").toString());
			String rallyStoryUrl = buildStoryUrl + rallyProjectId + "d/detail/userstory/"
					+ stgResult.get("ObjectID").toString();
			userStory.setStoryUrl(rallyStoryUrl);

			if (stgResult.get("ScheduleStatePrefix").equals("B")) {
				backlog = backlog + 1;
				userStory.setState("Backlog");
			} else if (stgResult.get("ScheduleStatePrefix").equals("D")) {
				defined = defined + 1;
				userStory.setState("Defined");
			} else if (stgResult.get("ScheduleStatePrefix").equals("P")) {
				inProgress = inProgress + 1;
				userStory.setState("In Progress");
			} else if (stgResult.get("ScheduleStatePrefix").equals("C")) {
				completed = completed + 1;
				userStory.setState("Completed");
			} else if (stgResult.get("ScheduleStatePrefix").equals("A")) {
				accepted = accepted + 1;
				userStory.setState("Accepted");

			}

			userStories.add(userStory);
		}
		rs.setAccepted(Integer.toString(accepted));
		rs.setBacklog(Integer.toString(backlog));
		rs.setCompleted(Integer.toString(completed));
		rs.setDefined(Integer.toString(defined));
		rs.setInProgress(Integer.toString(inProgress));
		rs.setDefects(Integer.toString(defects));
		rs.setUserStories(userStories);

		return rs;

	}

	@Override
	public RallyBurnDownData getBurnDownData(RallyFeature iteration, JSONArray iterationArray,
			RallyBurnDownData existingBurnDownData) {
		RallyBurnDownData rallyBurnDownData = new RallyBurnDownData();
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Date currentDate = new Date();
		List<String> dates = getIterationPeriodRange(iteration.getStartDate().toString(),
				iteration.getEndDate().toString());
			if (existingBurnDownData!=null && existingBurnDownData.getTotalEstimate() < Double
					.parseDouble(iteration.getTaskEstimateTotal())) {
				rallyBurnDownData.setTotalEstimate(Double.parseDouble(iteration.getTaskEstimateTotal()));
			}
			
			if(iteration.getTaskEstimateTotal()==null) {
				iteration.setTaskActualTotal("0");
				iteration.setTaskRemainingTotal("0");
			}
			
		try {
			Map<String, String> burnDownDetails = null;
			for (String iterationDate : dates) {
				burnDownDetails = new HashMap<>();
				burnDownDetails.put(RallyBurnDownData.ITERATION_DATE, iterationDate);
				//To generate the Burn down data from the metrics provided by the Rally API.
				if (dateFormat.format(dateFormat.parse(iterationDate)).equals(dateFormat.format(currentDate))) {
					double taskEstimate = arrayToStream(iterationArray).map(JSONObject.class::cast)
							.filter(obj -> obj.get("PlanEstimate") != null && obj.get("AcceptedDate") != null)
							.mapToDouble(obj -> (Double) obj.get("PlanEstimate")).sum();
					burnDownDetails.put(RallyBurnDownData.ACCEPTED_POINTS, Double.toString(taskEstimate));
					burnDownDetails.put(RallyBurnDownData.ITERATION_TO_DO_HOURS, iteration.getTaskRemainingTotal());
					rallyBurnDownData.setTotalEstimate(Double.parseDouble(iteration.getTaskEstimateTotal()));
				} else if (existingBurnDownData != null
						&& dateFormat.parse(iterationDate).before(dateFormat.parse(dateFormat.format(currentDate)))) {
					rallyBurnDownData.setId(existingBurnDownData.getId());
					rallyBurnDownData.getBurnDownData()
									.add(existingBurnDownData.getBurnDownData().stream()
									.filter(stream -> stream.get(RallyBurnDownData.ITERATION_DATE).equals(iterationDate))
									.findAny()
									.orElse(createEmptyBurnDown(burnDownDetails)));
					continue;
				} else {
					createEmptyBurnDown(burnDownDetails);
				}
				if (iteration.getTaskEstimateTotal() != null) {
					rallyBurnDownData.setTotalEstimate(Double.parseDouble(iteration.getTaskEstimateTotal()));
				}
				rallyBurnDownData.getBurnDownData().add(burnDownDetails);
			}

			rallyBurnDownData.setProjectId(iteration.getProjectId());
			rallyBurnDownData.setIterationId(iteration.getIterationId().toString());
			rallyBurnDownData.setProjectName(iteration.getProjectName());
			rallyBurnDownData.setIterationName(iteration.getIterationName());
			rallyBurnDownData.setLastUpdated(System.currentTimeMillis());
		} catch (NumberFormatException e) {
			LOG.error("Number format exception");
		} catch (java.text.ParseException e) {
			LOG.error("Could not parse date ");
		}
		return rallyBurnDownData;
	}

	@Override
	public List<String> getIterationPeriodRange(String startDate, String endDate) {
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		List<String> iterationDates = new ArrayList<>();
		Calendar iterationEndDate = Calendar.getInstance();
		Calendar iterationStartDate = Calendar.getInstance();

		try {
			iterationEndDate.setTime(dateFormat.parse(endDate));
			iterationStartDate.setTime(dateFormat.parse(startDate));

		} catch (java.text.ParseException e) {
			LOG.error("Parse exception " + e.getMessage());
		}
		while (iterationEndDate.compareTo(iterationStartDate) >= 0) {

			if (iterationStartDate.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY
					&& iterationStartDate.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
				iterationDates.add(dateFormat.format(iterationStartDate.getTime()));
			}
			iterationStartDate.add(Calendar.DAY_OF_MONTH, 1);
		}
		return iterationDates;
	}

	private JSONObject parseAsObject(String url) throws ParseException {
		ResponseEntity<String> response = rest.exchange(url, HttpMethod.GET,
				new HttpEntity<String>(this.createHeaders(getUserName(url), getPassword(url))), String.class);
		return (JSONObject) new JSONParser().parse(response.getBody());
	}

	private String getPassword(String url) {
		for (String rallyServer : rallySettings.getServers()) {
			if (url.contains(rallyServer)) {
				return rallySettings.getPasswords().get(rallySettings.getServers().indexOf(rallyServer));
			}
		}
		return null;
	}

	private String getUserName(String url) {
		for (String rallyServer : rallySettings.getServers()) {
			if (url.contains(rallyServer)) {
				return rallySettings.getUsernames().get(rallySettings.getServers().indexOf(rallyServer));
			}
		}
		return null;
	}

	private String str(JSONObject json, String key) {
		Object obj = json.get(key);
		return obj == null ? null : obj.toString();
	}

	private HttpHeaders createHeaders(String username, String password) {

		HttpHeaders headers = new HttpHeaders();
		if (username != null && !username.isEmpty() && password != null && !password.isEmpty()) {
			String auth = username + ":" + password;
			byte[] encodedAuth = Base64.encodeBase64(auth.getBytes(Charset.forName("US-ASCII")));
			String authHeader = "Basic " + new String(encodedAuth);
			headers.set("Authorization", authHeader);
		}
		return headers;
	}

	public List<RallyProject> getRallyProjectCollector(String projectId) {
		return rallyProjectRepository.findByProjectCollectorItemId(projectId);
	}

	// Helper method to find the remaining days
	public int getRemainingDays(String startDate, String endDate, Date currentDate) throws java.text.ParseException {
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

		Calendar iterationEndDate = Calendar.getInstance();
		iterationEndDate.setTime(dateFormat.parse(endDate));

		Calendar iterationStartDate = Calendar.getInstance();
		iterationStartDate.setTime(dateFormat.parse(startDate));

		Calendar currentCalendarDate = Calendar.getInstance();
		currentCalendarDate.setTime(currentDate);

		int remainingDays = 0;

		if ((iterationStartDate.compareTo(currentCalendarDate) == 1)
				|| (iterationEndDate.compareTo(currentCalendarDate) == -1)) {

			return remainingDays;
		}

		while (iterationEndDate.compareTo(currentCalendarDate) == 1) {
			if ((currentCalendarDate.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY)
					&& (currentCalendarDate.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY)) {
				++remainingDays;
			}
			currentCalendarDate.add(Calendar.DAY_OF_MONTH, 1);
		}

		return remainingDays;
	}

	@Nonnull
	private static Stream<Object> arrayToStream(JSONArray array) {
		return StreamSupport.stream(array.spliterator(), false);
	}
	
	private Map<String, String> createEmptyBurnDown(Map<String, String> burnDownDetails) {
		burnDownDetails.put(RallyBurnDownData.ACCEPTED_POINTS, "0");
		burnDownDetails.put(RallyBurnDownData.ITERATION_TO_DO_HOURS, "0");
		return burnDownDetails;
	}
}
