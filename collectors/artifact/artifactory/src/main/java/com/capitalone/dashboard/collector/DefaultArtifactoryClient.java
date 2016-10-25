package com.capitalone.dashboard.collector;

import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.collections.CollectionUtils;
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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestOperations;

import com.capitalone.dashboard.model.ArtifactoryRepo;
import com.capitalone.dashboard.model.BinaryArtifact;
import com.capitalone.dashboard.util.Supplier;

@Component
public class DefaultArtifactoryClient implements ArtifactoryClient {
	private static final Logger LOGGER = LoggerFactory.getLogger(DefaultArtifactoryClient.class);
	
	private static final String REPOS_URL_SUFFIX = "api/repositories";
	private static final String AQL_URL_SUFFIX = "api/search/aql";
	
	private final DateFormat FULL_DATE = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX");
	
	private final ArtifactorySettings artifactorySettings;
	private final RestOperations restOperations;
	
	@Autowired
	public DefaultArtifactoryClient(ArtifactorySettings artifactorySettings, Supplier<RestOperations> restOperationsSupplier) {
        this.artifactorySettings = artifactorySettings;
        this.restOperations = restOperationsSupplier.get();
	}
	
	public List<ArtifactoryRepo> getRepos(String instanceUrl) {
		List<ArtifactoryRepo> result = new ArrayList<>();
		ResponseEntity<String> responseEntity = makeRestCall(instanceUrl, REPOS_URL_SUFFIX);
		String returnJSON = responseEntity.getBody();
        JSONParser parser = new JSONParser();

        try {
        	JSONArray jsonRepos = (JSONArray) parser.parse(returnJSON);

            for (Object repo : jsonRepos) {
                JSONObject jsonRepo = (JSONObject) repo;

                final String repoName = getString(jsonRepo, "key");
                final String repoURL = getString(jsonRepo, "url");
                LOGGER.debug("repoName:" + repoName);
                LOGGER.debug("repoURL: " + repoURL);
                ArtifactoryRepo artifactoryRepo = new ArtifactoryRepo();
                artifactoryRepo.setInstanceUrl(instanceUrl);
                artifactoryRepo.setRepoName(repoName);
                artifactoryRepo.setRepoUrl(repoURL);
                
                // add the repo
                result.add(artifactoryRepo);
            }
        } catch (ParseException e) {
            LOGGER.error("Parsing repos on instance: " + instanceUrl, e);
        }		
		
		return result;
	}
	
	public List<BinaryArtifact> getArtifacts(String instanceUrl, String repoName, long lastUpdated) {
		List<BinaryArtifact> result = new ArrayList<>();
		
		// get the list of artifacts
		if (StringUtils.isNotEmpty(instanceUrl) && StringUtils.isNotEmpty(repoName)) {
			String body = "items.find({\"modified\" : {\"$gt\" : \"" + FULL_DATE.format(new Date(lastUpdated))
					+ "\"},\"repo\":{\"$eq\":\"" + repoName
					+ "\"}}).include(\"repo\", \"name\", \"path\", \"created\", \"modified\", \"property\")";
			
			ResponseEntity<String> responseEntity = makeRestPost(instanceUrl, AQL_URL_SUFFIX, body);
			String returnJSON = responseEntity.getBody();
	        JSONParser parser = new JSONParser();
	        
	        try {
	        	JSONObject json = (JSONObject) parser.parse(returnJSON);
	        	JSONArray jsonArtifacts = getJsonArray(json, "results");
	            for (Object artifact : jsonArtifacts) {
	                JSONObject jsonArtifact = (JSONObject) artifact;

	                final String artifactCanonicalName = getString(jsonArtifact, "name");
	                String artifactPath = getString(jsonArtifact, "path");
	                if (artifactPath.charAt(artifactPath.length()-1) == '/') {
	                	artifactPath = artifactPath.substring(0, artifactPath.length()-1);
	                }
	                String sTimestamp = getString(jsonArtifact, "modified");
	                if (sTimestamp == null) {
	                	sTimestamp = getString(jsonArtifact, "created");
	                }
	                long timestamp = 0;
	                if (sTimestamp != null) {
						try {
							Date date = FULL_DATE.parse(sTimestamp);
							timestamp = date.getTime();
						} catch (java.text.ParseException e) {
							LOGGER.error("Parsing artifact timestamp: " + sTimestamp, e);
						}
	                }
	                BinaryArtifact ba = createArtifact(artifactCanonicalName, artifactPath, timestamp, jsonArtifact);
	                if (ba != null) {
	                	result.add(ba);
	                }
	            }
	        } catch (ParseException e) {
	            LOGGER.error("Parsing artifacts on instance: " + instanceUrl + " and repo: " + repoName, e);
	        }
		}
			
		return result;
	}
	
	/**
	 * Creates an artifact given its canonical name and path.
	 * Artifacts can be of the following forms:
	 * 1. Maven artifacts:
	 * 		[org]/[module]/[version]/[module]-[version][-classifier].[ext]
	 * 2. Ivy artifacts:
	 * 		(a) [org]/[module]/[revision]/[type]/[artifact]-[revision](-[classifier]).[ext]
	 * 		(b) [org]/[module]/[revision]/ivy-[revision](-[classifier]).xml
	 * Using these patterns, we extract the artifact name, version and group id from the canonical name and path.
	 * 
	 * @param artifactCanonicalName			artifact's canonical name in artifactory
	 * @param artifactPath					artifact's path in artifactory
	 * @param timestamp						the artifact's timestamp
	 * @param jsonArtifact 					the artifact metadata is extracted from here
	 * @return
	 */
	private BinaryArtifact createArtifact(String artifactCanonicalName, String artifactPath, long timestamp, JSONObject jsonArtifact) {
		BinaryArtifact result = null;
		
		Pattern pathPattern = Pattern.compile("(?<org>.+)/(?<module>[^/]+)/(?<version>[^/]+)");
        Matcher pathMatcher = pathPattern.matcher(artifactPath);
        if (pathMatcher.matches() && pathMatcher.group("org") != null && pathMatcher.group("module") != null && pathMatcher.group("version") != null) {
        	if (artifactCanonicalName.matches("ivy-" + pathMatcher.group("version") + "(-[^\\.]+)?.xml")) {
        		// ivy artifact in the format [org]/[module]/[revision]/ivy-[revision](-[classifier]).xml
        		if (LOGGER.isDebugEnabled()) {
        			LOGGER.debug("ivy artifact of form [org]/[module]/[revision]/ivy-[revision](-[classifier]).xml found: NAME=" + artifactCanonicalName + " PATH=" + artifactPath);
        		}
        		result = new BinaryArtifact();
        		result.setCanonicalName(artifactCanonicalName);
        		result.setArtifactName("ivy");
        		result.setArtifactVersion(pathMatcher.group("version"));
        		result.setArtifactGroupId(pathMatcher.group("org"));
        		result.setTimestamp(timestamp);
        		addMetadataToArtifact(result, jsonArtifact);
        	} else if (artifactCanonicalName.matches(pathMatcher.group("module") + "-" + pathMatcher.group("version") + "(-[^\\.]+)?.[^\\.]+")) {
    			// maven artifact in format [org]/[module]/[version]/[module]-[version][-classifier].[ext]
        		if (LOGGER.isDebugEnabled()) {
        			LOGGER.debug("maven artifact of form [org]/[module]/[version]/[module]-[version][-classifier].[ext] found: NAME=" + artifactCanonicalName + " PATH=" + artifactPath);
        		}
        		result = new BinaryArtifact();
        		result.setCanonicalName(artifactCanonicalName);
        		result.setArtifactName(pathMatcher.group("module"));
        		result.setArtifactVersion(pathMatcher.group("version"));
        		result.setArtifactGroupId(pathMatcher.group("org").replace('/', '.'));
        		result.setTimestamp(timestamp);
        		addMetadataToArtifact(result, jsonArtifact);
            } else {
        		pathPattern = Pattern.compile("(?<org>.+)/(?<module>[^/]+)/(?<revision>[^/]+)/(?<type>[^/]+)");
        		pathMatcher = pathPattern.matcher(artifactPath);
        		if (pathMatcher.matches() && pathMatcher.group("org") != null && pathMatcher.group("module") != null && pathMatcher.group("revision") != null && pathMatcher.group("type") != null) {
        			if (artifactCanonicalName.matches(".+-" + pathMatcher.group("revision") + "(-[^\\.]+)?.[^\\.]+")) {
	        			// ivy artifact in the format [org]/[module]/[revision]/[type]/[artifact]-[revision](-[classifier]).[ext]
        				if (LOGGER.isDebugEnabled()) {
        					LOGGER.debug("ivy artifact of form [org]/[module]/[revision]/[type]/[artifact]-[revision](-[classifier]).[ext] found: NAME=" + artifactCanonicalName + " PATH=" + artifactPath);
        				}
        				result = new BinaryArtifact();
                		result.setCanonicalName(artifactCanonicalName);
                		result.setArtifactName(artifactCanonicalName.substring(0, artifactCanonicalName.indexOf("-" + pathMatcher.group("revision"))));
                		result.setArtifactVersion(pathMatcher.group("revision"));
                		result.setArtifactGroupId(pathMatcher.group("org"));
                		result.setTimestamp(timestamp);
                		addMetadataToArtifact(result, jsonArtifact);
        			} else {
        				if (LOGGER.isDebugEnabled()) {
        					LOGGER.debug("Unsupported artifact name: NAME=" + artifactCanonicalName + " PATH=" + artifactPath);
        				}
        			}
        		} else {
        			if (LOGGER.isDebugEnabled()) {
        				LOGGER.debug("Unsupported artifact: NAME=" + artifactCanonicalName + " PATH=" + artifactPath);
        			}
                }
        	}
        } else {
        	if (LOGGER.isDebugEnabled()) {
        		LOGGER.debug("Unsupported artifact path: NAME=" + artifactCanonicalName + " PATH=" + artifactPath);
        	}
        }
        
        return result;
	}
	
	private void addMetadataToArtifact(BinaryArtifact ba, JSONObject jsonArtifact) {
		if (ba != null && jsonArtifact != null) {
        	JSONArray jsonProperties = getJsonArray(jsonArtifact, "properties");
        	for (Object property : jsonProperties) {
        		JSONObject jsonProperty = (JSONObject) property;
        		String key = getString(jsonProperty, "key");
        		String value = getString(jsonProperty, "value");
        		switch (key) {
            		case "build.url":
            		case "build_url":
            		case "buildUrl":
            			ba.setBuildUrl(value);
            			break;
            		case "build.number":
            		case "build_number":
            		case "buildNumber":
            			ba.setBuildNumber(value);
            			break;
            		case "job.url":
            		case "job_url":
            		case "jobUrl":
            			ba.setJobUrl(value);
            			break;
            		case "job.name":
            		case "job_name":
            		case "jobName":
            			ba.setJobName(value);
            			break;
            		case "instance.url":
            		case "instance_url":
            		case "instanceUrl":
            			ba.setInstanceUrl(value);
            			break;
            		case "vcs.url":
            		case "vcs_url":
            		case "vcsUrl":
            			ba.setScmUrl(value);
            			break;
            		case "vcs.branch":
            		case "vcs_branch":
            		case "vcsBranch":
            			ba.setScmBranch(value);
            			break;
            		case "vcs.revision":
            		case "vcs_revision":
            		case "vcsRevision":
            			ba.setScmRevisionNumber(value);
            			break;
            		default:
            			// MongoDB doesn't allow dots in keys. So we handle it by converting 
            			// the letter following it to uppercase, and ignoring the dot.
            			if (key.contains(".")) {
            				StringBuilder newKey = new StringBuilder();
            				char prevChar = 0;
            				for (char c : key.toCharArray()) {
            					if (c != '.') {
            						if (prevChar == '.') {
            							c = Character.toUpperCase(c);
            						}
            						newKey.append(c);
            					}
            					prevChar = c;
            				}
            				key = newKey.toString();
            			}
            			if (StringUtils.isNotEmpty(key)) {
            				ba.getMetadata().put(key, value);
            			}
            			break;
        		}
        	}
        }
	}
	
    // ////// Helpers
	
    private ResponseEntity<String> makeRestCall(String instanceUrl, String suffix) {
    	ResponseEntity<String> response = null;
        try {
            response = restOperations.exchange(joinUrl(instanceUrl, artifactorySettings.getEndpoint(), suffix), HttpMethod.GET,
                    new HttpEntity<>(createHeaders(instanceUrl)), String.class);

        } catch (RestClientException re) {
            LOGGER.error("Error with REST url: " + joinUrl(instanceUrl, artifactorySettings.getEndpoint(), suffix));
            LOGGER.error(re.getMessage());
        }
        return response;
    }
    
    private ResponseEntity<String> makeRestPost(String instanceUrl, String suffix, Object body) {
        ResponseEntity<String> response = null;
        try {
        	HttpHeaders headers = createHeaders(instanceUrl);
        	headers.setContentType(MediaType.APPLICATION_JSON);
            response = restOperations.exchange(joinUrl(instanceUrl, artifactorySettings.getEndpoint(), suffix), HttpMethod.POST,
                    new HttpEntity<>(body, headers), String.class);

        } catch (RestClientException re) {
            LOGGER.error("Error with REST url: " + joinUrl(instanceUrl, artifactorySettings.getEndpoint(), suffix));
            LOGGER.error(re.getMessage());
        }
        return response;
    }
    
    // join a base url to another path or paths - this will handle trailing or non-trailing /'s
    private String joinUrl(String url, String... paths) {
    	StringBuilder result = new StringBuilder(url);
    	for (String path : paths) {
    		if (path != null) {
	            String p = path.replaceFirst("^(\\/)+", "");
		    	if (result.lastIndexOf("/") != result.length() - 1) {
		            result.append('/');
		        }
		    	result.append(p);
    		}
    	}
        return result.toString();
    }
    
    protected HttpHeaders createHeaders(String instanceUrl) {
    	HttpHeaders headers = new HttpHeaders();
    	List<String> servers = this.artifactorySettings.getServers();
    	List<String> usernames = this.artifactorySettings.getUsernames();
    	List<String> apiKeys = this.artifactorySettings.getApiKeys();
    	if (CollectionUtils.isNotEmpty(servers) && CollectionUtils.isNotEmpty(usernames) && CollectionUtils.isNotEmpty(apiKeys)) {
    		for (int i = 0; i < servers.size(); i++) {
        		if (servers.get(i) != null && servers.get(i).equals(instanceUrl) 
        				&& i < usernames.size() && i < apiKeys.size() && usernames.get(i) != null && apiKeys.get(i) != null) {
        			String userInfo = usernames.get(i) + ":" + apiKeys.get(i);
        			byte[] encodedAuth = Base64.encodeBase64(
                            userInfo.getBytes(StandardCharsets.US_ASCII));
                    String authHeader = "Basic " + new String(encodedAuth);           
                    headers.set(HttpHeaders.AUTHORIZATION, authHeader);         
        		}
        	}
    	}
    	return headers;
    }
    
    private JSONArray getJsonArray(JSONObject json, String key) {
        Object array = json.get(key);
        return array == null ? new JSONArray() : (JSONArray) array;
    }
    
    private String getString(JSONObject json, String key) {
        return (String) json.get(key);
    }
}
