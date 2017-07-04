package com.capitalone.dashboard.collector;

import com.capitalone.dashboard.model.Environment;
import com.capitalone.dashboard.model.EnvironmentComponent;
import com.capitalone.dashboard.model.UDeployApplication;
import com.capitalone.dashboard.model.UDeployEnvResCompData;
import com.capitalone.dashboard.util.Supplier;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestOperations;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
public class DefaultUDeployClient implements UDeployClient {
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultUDeployClient.class);

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

    @SuppressWarnings("PMD.AvoidCatchingNPE")
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
                component.setEnvironmentID(environment.getId());
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
            LOGGER.info("No Environment data found, No components deployed");
        }

        return components;
    }

    // Called by DefaultEnvironmentStatusUpdater
//    @SuppressWarnings("PMD.AvoidDeeplyNestedIfStmts") // agreed, this method needs refactoring.
    @Override
    public List<UDeployEnvResCompData> getEnvironmentResourceStatusData(
            UDeployApplication application, Environment environment) {

        List<UDeployEnvResCompData> environmentStatuses = new ArrayList<>();
		String urlInventory = "deploy/environment/" + environment.getId() + "/latestDesiredInventory";

		ResponseEntity<String> inventoryResponse = makeRestCall(application.getInstanceUrl(), urlInventory);

		JSONArray inventoryJSON = paresAsArray(inventoryResponse);

		String urlAllResources = "deploy/environment/" + environment.getId() + "/resources";

		ResponseEntity<String> allResourceResponse = makeRestCall(application.getInstanceUrl(), urlAllResources);

        JSONArray allResourceJSON = paresAsArray(allResourceResponse);
/**
 * New logic - Dec16/2015
 * json has generic parent->children relationship that can be N deep where N can be anything.
 * Logic should be to get each parentobject, get to the the lowest leaf children and process each child.
 * How to get to the lowest leaf? Leaf child has '"hasChildren": false'
 *
 * For resource json, the structure is this: top->agent (agent) -> domain (subresource) -> component
 * For nonCompliance resources, the path is: top -> children -> version -> component
 * 1. Write a method to return the lowest leaf children as jsonArray and then process.
 * 2. From resources json, if version is empty, it is not a component to deploy. if version if non-empty, those are the actual compnent
 * 3. From nonCompliance resource, it will have entries that were failed in depolyment.
 */
        
        if(inventoryJSON != null && inventoryJSON.size() > 0)
        {
	        // Failed to deploy list:
			Set<String> failedComponents = getFailedComponents(inventoryJSON);
	        Map<String, List<String>> versionFileMap = new HashMap<>();
	        for (Object item : allResourceJSON) {
	            JSONObject jsonObject = (JSONObject) item;
	            if (jsonObject == null) continue;
				JSONArray childArray = getResourceComponent(application, jsonObject, new JSONArray());
	            if (childArray.isEmpty()) continue;
	            for (Object child : childArray) {
	                JSONObject childObject = (JSONObject) child;
	                JSONArray jsonVersions = (JSONArray) childObject.get("versions");
	                if (jsonVersions == null || jsonVersions.size() == 0) continue;
	                JSONObject versionObject = (JSONObject) jsonVersions.get(0);
	                // get version fileTree and build data.
	                List<String> physicalFileNames = versionFileMap.get(str(versionObject, "id"));
	                if (CollectionUtils.isEmpty(physicalFileNames)) {
	                    physicalFileNames = getPhysicalFileList(application, versionObject);
	                    versionFileMap.put(str(versionObject, "id"), physicalFileNames);
	                }
	                for (String fileName : physicalFileNames) {
	                    environmentStatuses.add(buildUdeployEnvResCompData(environment, application, versionObject, fileName, childObject, failedComponents));
	                }
	            }
	        }
        }
        return environmentStatuses;
	}

	@SuppressWarnings("unchecked")
	private JSONArray getResourceComponent(UDeployApplication application, JSONObject topParent,
			JSONArray returnArray) {

		JSONObject resourceRole = (JSONObject) topParent.get("role");

		String resourceSpecialType = null;

		if (resourceRole != null) {
			resourceSpecialType = str(resourceRole, "specialType");
		}

		if (resourceSpecialType != null && resourceSpecialType.equalsIgnoreCase("COMPONENT")) {
			JSONArray jsonVersions = (JSONArray) topParent.get("versions");

			if (jsonVersions != null && jsonVersions.size() > 0) {
				returnArray.add(topParent);
			}
		} else {
			String hasChildren = str(topParent, "hasChildren");

			if ( "true".equalsIgnoreCase(hasChildren)) {
				String resourceId = str(topParent, "id");

				String urlResources = "resource/resource/" + resourceId + "/resources";

				ResponseEntity<String> resourceResponse = makeRestCall(application.getInstanceUrl(), urlResources);

				JSONArray resourceListJSON = paresAsArray(resourceResponse);

				for (Object resourceObject : resourceListJSON) {
					JSONObject childJSON = (JSONObject) resourceObject;

					getResourceComponent(application, childJSON, returnArray);
				}
			}
		}

		return returnArray;
	}
    private List<String> getPhysicalFileList(UDeployApplication application, JSONObject versionObject) {
        List<String> list = new ArrayList<>();
        String fileTreeUrl = "deploy/version/" + str(versionObject, "id") + "/fileTree";
        ResponseEntity<String> fileTreeResponse = makeRestCall(
                application.getInstanceUrl(), fileTreeUrl);
        JSONArray fileTreeJson = paresAsArray(fileTreeResponse);
        for (Object f : fileTreeJson) {
            JSONObject fileJson = (JSONObject) f;
            list.add(cleanFileName(str(fileJson, "name"), str(versionObject, "name")));
        }
        return list;
    }

	private Set<String> getFailedComponents(JSONArray environmentInventoryJSON) {
		HashSet<String> failedComponents = new HashSet<>();

		for (Object inventory : environmentInventoryJSON) {
			JSONObject inventoryObject = (JSONObject) inventory;
			JSONObject compliancyObject = (JSONObject) inventoryObject.get("compliancy");

			if (compliancyObject == null)
				continue;

			long correctCount = date(compliancyObject, "correctCount");
			long desiredCount = date(compliancyObject, "desiredCount");

			if (correctCount < desiredCount) {
				JSONObject componentObject = (JSONObject) inventoryObject.get("component");
				if (componentObject != null) {
					failedComponents.add(str(componentObject, "name"));
				}
			}
		}

		return failedComponents;
	}

    private String cleanFileName(String fileName, String version) {
        if (fileName.contains("-" + version))
            return fileName.replace("-" + version, "");
        if (fileName.contains(version))
            return fileName.replace(version, "");
        return fileName;
    }

    private UDeployEnvResCompData buildUdeployEnvResCompData(Environment environment, UDeployApplication application, JSONObject versionObject, String fileName, JSONObject childObject, Set<String> failedComponents) {
        UDeployEnvResCompData data = new UDeployEnvResCompData();
        data.setEnvironmentName(environment.getName());
        data.setCollectorItemId(application.getId());
        data.setComponentVersion(str(versionObject, "name"));
        data.setAsOfDate(date(versionObject, "created"));
        
        JSONObject childRole = (JSONObject) childObject.get("role");
        String childRoleName = str(childRole, "name");
        
        data.setDeployed(!failedComponents.contains(childRoleName));
        data.setComponentName(fileName);
        data.setOnline("ONLINE".equalsIgnoreCase(str(
                childObject, "status")));
		JSONObject resource = getParentAgent(childObject);
        if (resource != null) {
            data.setResourceName(str(resource, "name"));
        }
        return data;
    }

	public JSONObject getParentAgent(JSONObject childObject) {
		JSONObject parentAgent = null;
		String resourceType = null;
		String hasAgent = null;

		JSONObject parentObject = (JSONObject) childObject.get("parent");

		if (parentObject != null) {
			resourceType = str(parentObject, "type");
			hasAgent = str(parentObject, "hasAgent");

			if (resourceType != null && resourceType.equalsIgnoreCase("agent")) {
				parentAgent = parentObject;
			} else {
				if ("true".equalsIgnoreCase(hasAgent)) {
					parentAgent = getParentAgent(parentObject);
				}
			}
		}

		return parentAgent;
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
            LOGGER.error("Error with REST url: " + url);
            LOGGER.error(re.getMessage());
        }
        return response;
    }

    private String normalizeUrl(String instanceUrl, String remainder) {
        return StringUtils.removeEnd(instanceUrl, "/") + remainder;
    }

    protected HttpHeaders createHeaders() {
        String authHeader = null;
        String token = uDeploySettings.getToken();
        if (StringUtils.isEmpty(token)) {
            String auth = uDeploySettings.getUsername() + ":"
                    + uDeploySettings.getPassword();
            byte[] encodedAuth = Base64.encodeBase64(auth.getBytes(
                    StandardCharsets.US_ASCII));
            authHeader = "Basic " + new String(encodedAuth);
        } else {
            String passwordIsAuthToken = "PasswordIsAuthToken:{\"token\":\"" + token + "\"}";
            byte[] encodedAuth = Base64.encodeBase64(passwordIsAuthToken.getBytes(
                    StandardCharsets.US_ASCII));
            authHeader = "Basic " + new String(encodedAuth);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", authHeader);
        return headers;
    }

    private JSONArray paresAsArray(ResponseEntity<String> response) {
        if (response == null)
            return new JSONArray();
        try {
            return (JSONArray) new JSONParser().parse(response.getBody());
        } catch (ParseException pe) {
            LOGGER.debug(response.getBody());
            LOGGER.error(pe.getMessage());
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
