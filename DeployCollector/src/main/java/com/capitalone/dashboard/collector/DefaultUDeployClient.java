package com.capitalone.dashboard.collector;

import com.capitalone.dashboard.model.Environment;
import com.capitalone.dashboard.model.EnvironmentComponent;
import com.capitalone.dashboard.model.EnvironmentStatus;
import com.capitalone.dashboard.model.UDeployApplication;
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
import org.springframework.http.HttpStatus;
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

	@Override
	public List<EnvironmentStatus> getEnvironmentStatusData(
			UDeployApplication application, Environment environment) {
		List<EnvironmentStatus> environmentStatuses = new ArrayList<>();
		String url = "deploy/application/" + application.getApplicationId()
				+ "/components";

		// Get the environment status for each application component
		for (Object item : paresAsArray(makeRestCall(
				application.getInstanceUrl(), url))) {
			JSONObject jsonObject = (JSONObject) item;
			String componentId = str(jsonObject, "id");
			String componentName = str(jsonObject, "name");

			environmentStatuses.addAll(getComponentMappedEnvironmentStatus(
					application, environment, componentName, componentId));
		}
		return environmentStatuses;
	}

	private List<EnvironmentStatus> getComponentMappedEnvironmentStatus(
			UDeployApplication application, Environment environment,
			String componentName, String componentId) {
		List<EnvironmentStatus> environmentStatuses = new ArrayList<>();
		String url = "deploy/environment/" + environment.getId()
				+ "/componentMappings/" + componentId;

		for (Object item : paresAsArray(makeRestCall(
				application.getInstanceUrl(), url))) {
			JSONObject jsonObject = (JSONObject) item;
			JSONArray children = (JSONArray) jsonObject.get("children");

			if (children != null) {
				for (Object child : children) {
					JSONObject resourceObject = (JSONObject) child;
					environmentStatuses.add(statusFor(environment.getName(),
							componentName, resourceObject));
				}
			} else {
				JSONObject resourceObject = (JSONObject) jsonObject
						.get("resource");
				environmentStatuses.add(statusFor(environment.getName(),
						componentName, resourceObject));
			}
		}
		return environmentStatuses;
	}

	// ////// Helpers

	private EnvironmentStatus statusFor(String envName, String componentName,
			JSONObject resourceObject) {
		EnvironmentStatus environmentStatus = new EnvironmentStatus();
		environmentStatus.setEnvironmentName(envName);
		environmentStatus.setComponentName(componentName);
		environmentStatus.setResourceName(str(resourceObject, "name"));
		environmentStatus.setOnline(str(resourceObject, "status").equals(
				"ONLINE"));
		return environmentStatus;
	}

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
