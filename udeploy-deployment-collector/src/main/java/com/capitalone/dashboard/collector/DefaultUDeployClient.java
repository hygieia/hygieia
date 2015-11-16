package com.capitalone.dashboard.collector;

import com.capitalone.dashboard.model.Environment;
import com.capitalone.dashboard.model.EnvironmentComponent;
import com.capitalone.dashboard.model.UDeployApplication;
import com.capitalone.dashboard.model.UDeployEnvResCompData;
import com.capitalone.dashboard.util.Supplier;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
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

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

@Component
public class DefaultUDeployClient implements UDeployClient {
	private static final Log LOG = LogFactory
			.getLog(DefaultUDeployClient.class);

	private final UDeploySettings uDeploySettings;
	private final RestOperations restOperations;

	@Autowired
	public DefaultUDeployClient(UDeploySettings uDeploySettings,
			Supplier<RestOperations> restOperationsSupplier) {
		this.uDeploySettings = uDeploySettings;
		this.restOperations = restOperationsSupplier.get();
	}

	@Override
	public List<UDeployApplication> getApplications(String instanceUrl) {
		List<UDeployApplication> applications = new ArrayList<>();

		for (Object item : paresAsArray(makeRestCall(instanceUrl,
				"deploy/application"))) {
			JSONObject jsonObject = (JSONObject) item;
			UDeployApplication application = new UDeployApplication();
			application.setInstanceUrl(instanceUrl);
			application.setApplicationName(str(jsonObject, "name"));
			application.setApplicationId(str(jsonObject, "id"));
			applications.add(application);
		}
		return applications;
	}

	@Override
	public List<Environment> getEnvironments(UDeployApplication application) {
		List<Environment> environments = new ArrayList<>();
		String url = "deploy/application/" + application.getApplicationId()
				+ "/environments/false";

		for (Object item : paresAsArray(makeRestCall(
				application.getInstanceUrl(), url))) {
			JSONObject jsonObject = (JSONObject) item;
			environments.add(new Environment(str(jsonObject, "id"), str(
					jsonObject, "name")));
		}

		return environments;
	}

	@Override
	public List<EnvironmentComponent> getEnvironmentComponents(
			UDeployApplication application, Environment environment) {
		List<EnvironmentComponent> components = new ArrayList<>();
		String url = "deploy/environment/" + environment.getId()
				+ "/latestDesiredInventory";
		try {
			for (Object item : paresAsArray(makeRestCall(
					application.getInstanceUrl(), url))) {
				JSONObject jsonObject = (JSONObject) item;

				JSONObject versionObject = (JSONObject) jsonObject
						.get("version");
				JSONObject componentObject = (JSONObject) jsonObject
						.get("component");
				JSONObject complianceObject = (JSONObject) jsonObject
						.get("compliancy");

				EnvironmentComponent component = new EnvironmentComponent();
				component.setEnvironmentName(environment.getName());
				component.setEnvironmentUrl(normalizeUrl(
						application.getInstanceUrl(), "/#environment/"
								+ environment.getId()));
				component.setComponentID(str(componentObject, "id"));
				component.setComponentName(str(componentObject, "name"));
				component.setComponentVersion(str(versionObject, "name"));
				component.setDeployed(complianceObject.get("correctCount")
						.equals(complianceObject.get("desiredCount")));
				component.setAsOfDate(date(jsonObject, "date"));
				components.add(component);
			}
		} catch (NullPointerException npe) {
			LOG.info("No Environment data found, No components deployed");
		}

		return components;
	}

	// Called by DefaultEnvironmentStatusUpdater
	@Override
	public List<UDeployEnvResCompData> getEnvironmentResourceStatusData(
			UDeployApplication application, Environment environment) {

		List<UDeployEnvResCompData> environmentStatuses = new ArrayList<>();
		String urlNonCompliantResources = "deploy/environment/"
				+ environment.getId() + "/noncompliantResources";
		String urlAllResources = "deploy/environment/" + environment.getId()
				+ "/resources";

		ResponseEntity<String> nonCompliantResourceResponse = makeRestCall(
				application.getInstanceUrl(), urlNonCompliantResources);
		JSONArray nonCompliantResourceJSON = paresAsArray(nonCompliantResourceResponse);
		ResponseEntity<String> allResourceResponse = makeRestCall(
				application.getInstanceUrl(), urlAllResources);
		JSONArray allResourceJSON = paresAsArray(allResourceResponse);

		for (Object item : allResourceJSON) {
			JSONObject jsonObject = (JSONObject) item;
			if (jsonObject != null) {
				JSONObject parentObject = (JSONObject) jsonObject.get("parent");
				if (parentObject != null) {
					String resourceName = str(jsonObject, "name");
					boolean status = "ONLINE".equalsIgnoreCase(str(
							parentObject, "status"));
					JSONArray jsonChildren = (JSONArray) jsonObject
							.get("children");
					if (jsonChildren != null && jsonChildren.size() > 0) {
						for (Object children : jsonChildren) {
							JSONObject childrenObject = (JSONObject) children;
							String componentName = (String) childrenObject
									.get("name");
							UDeployEnvResCompData data = new UDeployEnvResCompData();
							data.setEnvironmentName(environment.getName());
							data.setCollectorItemId(application.getId());
							data.setResourceName(resourceName);
							data.setOnline(status);
							data.setComponentName(componentName);
							JSONArray jsonVersions = (JSONArray) childrenObject
									.get("versions");
							String version = "UNKNOWN";
							data.setDeployed(false);

							if (jsonVersions != null && jsonVersions.size() > 0) {
								JSONObject versionObject = (JSONObject) jsonVersions.get(0);
								version = (String) versionObject.get("name");
								data.setAsOfDate(date(versionObject, "created"));
								data.setDeployed(true);
							} else {
								// get it from non-compliant resource list
								nonCompliantSearchLoop: for (Object nonCompItem : nonCompliantResourceJSON) {
									JSONArray nonCompChildrenArray = (JSONArray) ((JSONObject) nonCompItem)
											.get("children");
									for (Object nonCompChildItem : nonCompChildrenArray) {
										JSONObject nonCompChildObject = (JSONObject) nonCompChildItem;
										JSONObject nonCompVersonObject = (JSONObject) nonCompChildObject
												.get("version");
										if (nonCompVersonObject != null) {
											JSONObject nonCompComponentObject =
													(JSONObject) nonCompVersonObject.get("component");
											if (nonCompComponentObject != null &&
													componentName.equalsIgnoreCase(
															(String) nonCompComponentObject.get("name"))) {
												version = (String) nonCompVersonObject
														.get("name");
												data.setAsOfDate(date(
														nonCompVersonObject,
														"created"));
												data.setDeployed(false);
												break nonCompliantSearchLoop;
											}
										}
									}
								}
							}
							data.setComponentVersion(version);
							environmentStatuses.add(data);
						}
					}
				}
			}
		}
		return environmentStatuses;
	}

	// ////// Helpers

	private ResponseEntity<String> makeRestCall(String instanceUrl,
			String endpoint) {
		String url = normalizeUrl(instanceUrl, "/rest/" + endpoint);
		ResponseEntity<String> response = null;
		try {
			response = restOperations.exchange(url, HttpMethod.GET,
					new HttpEntity<>(createHeaders()), String.class);

		} catch (RestClientException re) {
			LOG.error("Error with REST url: " + url);
			LOG.error(re.getMessage());
		}
		return response;
	}

	private String normalizeUrl(String instanceUrl, String remainder) {
		return StringUtils.removeEnd(instanceUrl, "/") + remainder;
	}

	private HttpHeaders createHeaders() {
		return new HttpHeaders() {
			{
				String auth = uDeploySettings.getUsername() + ":"
						+ uDeploySettings.getPassword();
				byte[] encodedAuth = Base64.encodeBase64(auth.getBytes(Charset
						.forName("US-ASCII")));
				String authHeader = "Basic " + new String(encodedAuth);
				set("Authorization", authHeader);
			}
		};
	}

	private JSONArray paresAsArray(ResponseEntity<String> response) {
		if (response == null)
			return new JSONArray();
		try {
			return (JSONArray) new JSONParser().parse(response.getBody());
		} catch (ParseException pe) {
			LOG.debug(response.getBody());
			LOG.error(pe.getMessage());
		}
		return new JSONArray();
	}

	private String str(JSONObject json, String key) {
		Object value = json.get(key);
		return value == null ? null : value.toString();
	}

	private long date(JSONObject jsonObject, String key) {
		Object value = jsonObject.get(key);
		return value == null ? 0 : (long) value;
	}
}
