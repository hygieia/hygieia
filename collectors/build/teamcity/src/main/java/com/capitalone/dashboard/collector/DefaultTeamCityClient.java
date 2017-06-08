package com.capitalone.dashboard.collector;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.joda.time.DateTime;
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
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestOperations;

import com.capitalone.dashboard.model.Build;
import com.capitalone.dashboard.model.BuildStatus;
import com.capitalone.dashboard.model.TeamCityJob;
import com.capitalone.dashboard.model.RepoBranch;
import com.capitalone.dashboard.model.SCM;
import com.capitalone.dashboard.util.Supplier;


/**
 * TeamCityClient implementation that uses RestTemplate and JSONSimple to
 * fetch information from TeamCity instances.
 */
@Component
public class DefaultTeamCityClient implements TeamCityClient {
    private static final Logger LOG = LoggerFactory.getLogger(DefaultTeamCityClient.class);

    private final RestOperations rest;
    private final TeamCitySettings settings;

    private static final String API_SUFFIX = "/app/rest";

    private static final String JOBS_URL_SUFFIX = "/app/rest/projects";
    
    private static final String JOB_QUERY = ")&fields=buildType(projectId,name,builds($locator(running:false,canceled:false,branch:default:any),build(id,href)))";

    private static final String BUILD_TYPES_URL_SUFFIX = "/buildTypes?locator=affectedProject:(id:";
    
    @Autowired
    public DefaultTeamCityClient(Supplier<RestOperations> restOperationsSupplier, TeamCitySettings settings) {
        this.rest = restOperationsSupplier.get();
        this.settings = settings;
    }

    @Override
    public Map<TeamCityJob, Set<Build>> getInstanceJobs(String instanceUrl) {
        Map<TeamCityJob, Set<Build>> result = new LinkedHashMap<>();
        
        int jobsCount = getJobsCount(instanceUrl);
        
        int i = 0, pageSize = settings.getPageSize();
        // Default pageSize to 1000 for backward compatibility of settings when pageSize defaults to 0
        if (pageSize <= 0) {
        	pageSize = 10000;
        }
        while (i < jobsCount) {
	        try {
                String url = joinURL(instanceUrl,JOBS_URL_SUFFIX + "?count="+pageSize);
	            ResponseEntity<String> responseEntity = makeRestCall(url);
	            if (responseEntity == null) {
	            	break;
	            }
	            String returnJSON = responseEntity.getBody();
	            if (StringUtils.isEmpty(returnJSON)) {
	            	break;	            	
	            }
	            JSONParser parser = new JSONParser();
	            
	            try {
	                JSONObject object = (JSONObject) parser.parse(returnJSON);
	                JSONArray jobs = getJsonArray(object, "project");
	                if (jobs.size() == 0) {
	                	break;
	                }
	                
	                for (Object job : jobs) {
	                    JSONObject jsonJob = (JSONObject) job;
	
	                    String jobId = getString(jsonJob, "id");
	                    if(!jobId.contains("_Root"))
	                    {
	                       String parentProjId = getString(jsonJob, "parentProjectId");
                  	       if(!parentProjId.contains("_Root"))
                  	       {                 	    	                     
		                    final String jobName = getString(jsonJob, "id");
		                    final String jobURL = getString(jsonJob, "webUrl");
	
	                        LOG.debug("Process ProjectName " + jobName + " ProjectURL " + jobURL);
	
		                    recursiveGetJobDetails(jsonJob, jobName, jobURL, instanceUrl, result);
                  	   	   }
	                    }
	                }
	            } catch (ParseException e) {
	                LOG.error("Parsing jobs details on instance: " + instanceUrl, e);
	            }
	        } catch (RestClientException rce) {
	            LOG.error("client exception loading jobs details", rce);
	            throw rce;
	        } catch (MalformedURLException mfe) {
	            LOG.error("malformed url for loading jobs details", mfe);
	        }  catch (URISyntaxException e1) {
			    LOG.error("wrong syntax url for loading jobs details", e1);
            }
	        
	        i += pageSize;
        }
        return result;
    }

    
    /**
     * Get number of jobs first so that we don't get 500 internal server error when paging with index out of bounds.
     * TODO: We get the jobs JSON without details and then get the size of the array. Is there a better way to get number of jobs for paging?
     * @param 		instanceUrl
     * @return		number of jobs
     */
    private int getJobsCount(String instanceUrl) {
    	int result = 0;
    	
    	try {
            String url = joinURL(instanceUrl, JOBS_URL_SUFFIX);
            ResponseEntity<String> responseEntity = makeRestCall(url);
            if (responseEntity == null) {
            	return result;
            }
            String returnJSON = responseEntity.getBody();
            if (StringUtils.isEmpty(returnJSON)) {
            	return result;	            	
            }
            JSONParser parser = new JSONParser();           
            try {
                JSONObject object = (JSONObject) parser.parse(returnJSON);
                JSONArray jobs = getJsonArray(object, "project");
                result = jobs.size();
            } catch (ParseException e) {
                LOG.error("Parsing jobs on instance: " + instanceUrl, e);
            }
        } catch (RestClientException rce) {
            LOG.error("client exception loading jobs", rce);
            throw rce;
        } catch (MalformedURLException mfe) {
            LOG.error("malformed url for loading jobs", mfe);
        } catch (URISyntaxException e1) {
        	LOG.error("wrong syntax url for loading jobs", e1);
		}
    	return result;
    }
    
    
    private void recursiveGetJobDetails(JSONObject jsonJob, String jobName, String jobURL, String instanceUrl, 
             Map<TeamCityJob, Set<Build>> result) {        
        LOG.debug("recursiveGetJobDetails: ProjectName " + jobName + " ProjectURL: " + jobURL);

        if (jsonJob!=null) {
            TeamCityJob teamcityJob = new TeamCityJob();
            teamcityJob.setInstanceUrl(instanceUrl);    
            Set<Build> builds = new LinkedHashSet<>();       
    
            // A basic Build object. This will be fleshed out later if this is a new Build.
            String dockerLocalHostIP = settings.getDockerLocalHostIP();
            String ProjectLocator = jsonJob.get("id").toString();
            if (StringUtils.isNotEmpty(ProjectLocator)) 
            {
                String buildURL="";
				try {
					buildURL = joinURL(instanceUrl,API_SUFFIX,BUILD_TYPES_URL_SUFFIX+ProjectLocator+JOB_QUERY);
				} catch (MalformedURLException e) {
					e.printStackTrace();
				}

                //Modify localhost if Docker Natting is being done
                if (!dockerLocalHostIP.isEmpty()) {
                    buildURL = buildURL.replace("localhost", dockerLocalHostIP);
                    LOG.debug("Adding build & Updated URL to map LocalHost for Docker: " + buildURL);
                } else {
                    LOG.debug(" Adding Build: " + buildURL);
                }
                
            	try {
                    ResponseEntity<String> responseEntity = makeRestCall(buildURL);
                    if (responseEntity != null)
                    {
	                    String returnJSON = responseEntity.getBody();
	                    if (!StringUtils.isEmpty(returnJSON))
	                    {	
		                    JSONParser parser = new JSONParser();           
		                    try {
			                        JSONObject object = (JSONObject) parser.parse(returnJSON);
			                        JSONArray buildTypes = getJsonArray(object, "buildType");
			                        
			                        for(Object buildTypeObj : buildTypes)
			                        {
			                        	JSONObject JbuildTypeObj = (JSONObject) buildTypeObj;
			                        	if((getString(JbuildTypeObj,"projectId").equals(jobName))&& (getString(JbuildTypeObj,"name").contains("Build")))
			                        	{
				    	                    teamcityJob.setJobName(jobName);
				    	                    teamcityJob.setJobUrl(jobURL);
			                        		JSONObject buildsObj = (JSONObject) JbuildTypeObj.get("builds");
					                        JSONArray buildsList = getJsonArray(buildsObj, "build");
					    	                for (Object buildObj : buildsList) 
					    	                {
					    	                    JSONObject JbuildsObj = (JSONObject) buildObj;	
						    	                    
					    	                    String buildId = JbuildsObj.get("id").toString();
					    	                    
					    	                    if (!"0".equals(buildId)) 
					    	                    {
							                        Build teamcityBuild = new Build();
					    	                        teamcityBuild.setNumber(buildId);
					    	                        String buildDetailsURL="";
					    	        				try {
					    	        					buildDetailsURL = joinURL(instanceUrl, "/app/rest/builds","/?locator=id:"+buildId+"&fields=build(id,number,state,status,startDate,finishDate,triggered(user),buildType(id,name,projectName),properties(property(name,value)),changes(change(id,username,date,comment)),statistics(property(name,value)))");
					    	        				} catch (MalformedURLException e) {
					    	        					e.printStackTrace();
					    	        				}
					    	                        if (!dockerLocalHostIP.isEmpty()) {
					    	                        	buildDetailsURL = buildDetailsURL.replace("localhost", dockerLocalHostIP);
					    	                            LOG.debug("Adding build & Updated URL to map LocalHost for Docker: " + buildDetailsURL);
					    	                        } else {
					    	                            LOG.debug(" Adding Build: " + buildURL);
					    	                        }

					    	                        teamcityBuild.setBuildUrl(buildDetailsURL);
					    	                        builds.add(teamcityBuild);
					    	                    	
					    	                    }
					    	                }			                        		
			                        	}
			                        	else
			                        	{
			                        		break;
			                        	}
		                        	
			                        }	                    

		    	                }
		
		                    	catch (ParseException e) {
		                        LOG.error("Parsing jobs on instance: " + instanceUrl, e);
		                    }
	                    }
                    }
                } catch (RestClientException rce) {
                    LOG.error("client exception loading jobs", rce);
                    throw rce;
                } catch (MalformedURLException mfe) {
                    LOG.error("malformed url for loading jobs", mfe);
                } catch (URISyntaxException e1) {
                	LOG.error("wrong syntax url for loading jobs", e1);
        		}
                                                 
            }    
            // add the builds to the job
            if(!builds.isEmpty())
            {
            	result.put(teamcityJob, builds);
            }
        }
    }

    @Override
    public Build getBuildDetails(String buildUrl, String instanceUrl) {
        try {
            String newUrl = rebuildJobUrl(buildUrl, instanceUrl);
            ResponseEntity<String> result = makeRestCall(newUrl);
            String resultJSON = result.getBody();
            if (StringUtils.isEmpty(resultJSON)) {
                return null;
            }
            JSONParser parser = new JSONParser();
            try {
                JSONObject buildJson = (JSONObject) parser.parse(resultJSON);
                JSONArray buildJSONArray =  getJsonArray(buildJson,"build");
                Build build = new Build();
	                for(Object obj : buildJSONArray)
	                {
	                	JSONObject buildDetails = (JSONObject) obj;
	                    String building = getString(buildDetails,"state");
	                    // Ignore jobs that are running or cancelled
	                    if (!building.equalsIgnoreCase("running") || !building.equalsIgnoreCase("cancelled")) {
	                        build.setNumber(buildDetails.get("id").toString());
	                        build.setBuildUrl(buildUrl);
	                        build.setTimestamp(System.currentTimeMillis());
	                        build.setStartTime(getMilliSeconds(buildDetails.get("startDate").toString()));
							build.setEndTime(getMilliSeconds(buildDetails.get("finishDate").toString()));
	                        JSONObject statisticsObj = (JSONObject)buildDetails.get("statistics");
	                        
	                        build.setDuration(Long.parseLong(getPropertyValue(statisticsObj, "BuildDurationNetTime")));
	                        build.setBuildStatus(getBuildStatus(buildDetails));
	                        build.setStartedBy(firstCulprit(buildDetails));	  
	                        if (settings.isSaveLog()) {
	                            build.setLog(getLog(buildUrl));
	                        }	                        
	                                                
	                        Set<String> commitIds = new HashSet<>();

                            addChangeSet(build, buildDetails, commitIds);                    	                        
	                }
                   
                }
                return build;

            } catch (ParseException e) {
                LOG.error("Parsing build: " + buildUrl, e);
            }
        } catch (RestClientException rce) {
            LOG.error("Client exception loading build details: " + rce.getMessage() + ". URL =" + buildUrl);
        } catch (MalformedURLException mfe) {
            LOG.error("Malformed url for loading build details" + mfe.getMessage() + ". URL =" + buildUrl);
        } catch (URISyntaxException use) {
            LOG.error("Uri syntax exception for loading build details" + use.getMessage() + ". URL =" + buildUrl);
        } catch (RuntimeException re) {
            LOG.error("Unknown error in getting build details. URL=" + buildUrl, re);
        } catch (UnsupportedEncodingException unse) {
            LOG.error("Unsupported Encoding Exception in getting build details. URL=" + buildUrl, unse);
        }
        return null;
    }

    //This method will rebuild the API endpoint because the buildUrl obtained via teamcity API
    //does not save the auth user info and we need to add it back.
    public static String rebuildJobUrl(String build, String server) throws URISyntaxException, MalformedURLException, UnsupportedEncodingException {
    	URL instanceUrl = new URL(server);
        String userInfo = instanceUrl.getUserInfo();
        String instanceProtocol = instanceUrl.getProtocol();

        //decode to handle spaces in the job name.
        URL buildUrl = new URL(URLDecoder.decode(build, "UTF-8"));
        String buildPath = buildUrl.getPath();

        String host = buildUrl.getHost();
        int port = buildUrl.getPort();
        URI newUri = new URI(instanceProtocol, userInfo, host, port, buildPath, buildUrl.getQuery(), null);
        return newUri.toString();
    }
  
    /**
     * Grabs changeset information for the given build.
     *
     * @param build     a Build
     * @param changeSet the build JSON object
     * @param commitIds the commitIds
     * @param revisions the revisions
     */
    private void addChangeSet(Build build, JSONObject buildDetails, Set<String> commitIds) {        
        String scmType = "GIT";
        Map<String, RepoBranch> commitsToUrl = new HashMap<>();
        JSONObject changeSet = (JSONObject) buildDetails.get("changes");
        JSONObject properties = (JSONObject) buildDetails.get("properties");
        RepoBranch rb = new RepoBranch();
        String Company = getPropertyValue(properties,"fetchURLCompanyName");
        String URLRoot = getPropertyValue(properties,"fetchURLRoot");
        String CompanyUrl = URLRoot.replace("%fetchURLCompanyName%", Company);
        String FetchURL = getPropertyValue(properties,"fetchURL").replaceAll("%fetchURLRoot%", CompanyUrl);
        rb.setUrl(FetchURL);
        rb.setBranch(getPropertyValue(properties, "defaultBranch"));
        rb.setType(RepoBranch.RepoType.fromString(scmType));
        build.getCodeRepos().add(rb);

        for (Object change : getJsonArray(changeSet, "change")) {
            JSONObject json = (JSONObject) change;
            String changeId = json.get("id").toString();
            if (StringUtils.isNotEmpty(changeId) && !commitIds.contains(changeId)) {
                commitsToUrl.put(changeId, rb);
                SCM scm = new SCM();
                scm.setScmAuthor(getString(json,"username"));
                scm.setScmCommitLog(getString(json, "comment"));
                scm.setScmCommitTimestamp(getCommitTimestamp(json));
                scm.setScmRevisionNumber(changeId);
                RepoBranch repoBranch = commitsToUrl.get(scm.getScmRevisionNumber());  
                if (repoBranch != null) {
                    scm.setScmUrl(repoBranch.getUrl());
                    scm.setScmBranch(repoBranch.getBranch());
                }  
                scm.setNumberOfChanges(getJsonArray(changeSet, "change").size());
                build.getSourceChangeSet().add(scm);
                commitIds.add(changeId);                  
                
            }
        }
    }

 

    private long getCommitTimestamp(JSONObject jsonItem) {
        if (jsonItem.get("timestamp") != null) {
            return (Long) jsonItem.get("timestamp");
        } else if (jsonItem.get("date") != null) {
            String dateString = (String) jsonItem.get("date");
            try {
                // Try an alternate date format...looks like this one is used by Git
                return new SimpleDateFormat("yyyyMMdd'T'HHmmss-hhmm").parse(dateString).getTime();
            } catch (java.text.ParseException e) {
                try {
                    return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z").parse(dateString).getTime();
                } catch (java.text.ParseException e1) {
                    LOG.error("Invalid date string: " + dateString, e);
                }
            }
        }
        return 0;
    }

    private String getPropertyValue(JSONObject json,String name){
        JSONArray properties = getJsonArray(json,"property");
        for(Object property : properties)
        {
        	if(getString((JSONObject) property, "name").contains(name))
			{
        		return ((JSONObject) property).get("value").toString();	                       		
			}
        }
        return null;
    }
    
    private String getString(JSONObject json, String key) {
        return (String) json.get(key);
    }

    private String getRevision(JSONObject jsonItem) {
        // Use revision if provided, otherwise use id
        Long revision = (Long) jsonItem.get("revision");
        return revision == null ? getString(jsonItem, "id") : revision.toString();
    }

    private JSONArray getJsonArray(JSONObject json, String key) {
        Object array = json.get(key);
        return array == null ? new JSONArray() : (JSONArray) array;
    }

    private String firstCulprit(JSONObject buildJson) {

        JSONObject culprit = (JSONObject) buildJson.get("triggered");
        //LOG.error("triggered data:"+ culprit.toJSONString());
        return getFullName(culprit);
    }

    private String getFullName(JSONObject author) {

    	JSONObject name = (JSONObject) author.get("user");
    	if(name!=null)
    	{
        return name.get("username").toString();
    	}
    	else
		{
			return "Automated build";
		}

    }

    private String getCommitAuthor(JSONObject jsonItem) {
        // Use user if provided, otherwise use author.fullName
        JSONObject author = (JSONObject) jsonItem.get("author");
        return author == null ? getString(jsonItem, "user") : getFullName(author);
    }

    private BuildStatus getBuildStatus(JSONObject buildJson) {
        String status = buildJson.get("status").toString();
        switch (status) {
            case "SUCCESS":
                return BuildStatus.Success;
            case "UNSTABLE":
                return BuildStatus.Unstable;
            case "FAILURE":
                return BuildStatus.Failure;
            case "ABORTED":
                return BuildStatus.Aborted;
            default:
                return BuildStatus.Unknown;
        }
    }
    
    @SuppressWarnings("PMD")
    protected ResponseEntity<String> makeRestCall(String sUrl) throws MalformedURLException, URISyntaxException {
        LOG.debug("Enter makeRestCall " + sUrl);
        URI thisuri = URI.create(sUrl);
        String userInfo = thisuri.getUserInfo();

        //get userinfo from URI or settings (in spring properties)
        if (StringUtils.isEmpty(userInfo)) {
        	List<String> servers = this.settings.getServers();
        	List<String> usernames = this.settings.getUsernames();
        	List<String> apiKeys = this.settings.getApiKeys();
        	if (CollectionUtils.isNotEmpty(servers) && CollectionUtils.isNotEmpty(usernames) && CollectionUtils.isNotEmpty(apiKeys)) {
        		boolean exactMatchFound = false;
	        	for (int i = 0; i < servers.size(); i++) {
	        		if ((servers.get(i) != null)) {
	        			String domain1 = getDomain(sUrl);
	        			String domain2 = getDomain(servers.get(i));
	        			if (StringUtils.isNotEmpty(domain1) && StringUtils.isNotEmpty(domain2) && domain1.equals(domain2)
	        					&& getPort(sUrl) == getPort(servers.get(i))) {
	                		exactMatchFound = true;	
	        			}
	        			if (exactMatchFound && (i < usernames.size()) && (i < apiKeys.size()) 
	        					&& (StringUtils.isNotEmpty(usernames.get(i))) && (StringUtils.isNotEmpty(apiKeys.get(i)))) {
	        				userInfo = usernames.get(i) + ":" + apiKeys.get(i);
        				}
	        			if (exactMatchFound) {
	        				break;
	        			}
	        		}
	        	}	        	
        		if (!exactMatchFound) {
        			LOG.warn("Credentials for the following url was not found. This could happen if the domain/subdomain/IP address "
        					+ "in the build url returned by TeamCity and the TeamCity instance url in your Hygieia configuration do not match: "
        					+ "\"" + sUrl + "\"");
        		}
        	}
        }
        // Basic Auth only.
        if (StringUtils.isNotEmpty(userInfo)) {
            return rest.exchange(thisuri, HttpMethod.GET,
                    new HttpEntity<>(createHeaders(userInfo)),
                    String.class);
        } else {
            return rest.exchange(thisuri, HttpMethod.GET, new HttpEntity<>(createAcceptHeaders()),
                    String.class);
        }

    }
    
    private String getDomain(String url) throws URISyntaxException {
        URI uri = new URI(url);
        String domain = uri.getHost();
        return domain;
    }
    
    private int getPort(String url) throws URISyntaxException {
        URI uri = new URI(url);
        return uri.getPort();
    }

    protected HttpHeaders createHeaders(final String userInfo) {
        byte[] encodedAuth = Base64.encodeBase64(
                userInfo.getBytes(StandardCharsets.US_ASCII));
        String authHeader = "Basic " + new String(encodedAuth);

        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION, authHeader);
        return headers;
    }
    
    protected HttpHeaders createAcceptHeaders() {

        String authHeader = "application/json";

        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.ACCEPT, authHeader);
        return headers;
    }

    protected String getLog(String buildUrl) {
        try {
            return makeRestCall(joinURL(buildUrl, "consoleText")).getBody();
        } catch (MalformedURLException mfe) {
            LOG.error("malformed url for build log", mfe);
        } catch (URISyntaxException e) {
        	LOG.error("wrong syntax url for build log", e);
		}

        return "";
    }

    // join a base url to another path or paths - this will handle trailing or non-trailing /'s
    public static String joinURL(String base, String... paths) throws MalformedURLException {
        StringBuilder result = new StringBuilder(base);
        for (String path : paths) {
            String p = path.replaceFirst("^(\\/)+", "");
            if (result.lastIndexOf("/") != result.length() - 1) {
                result.append('/');
            }
            result.append(p);
        }
        return result.toString();
    }
    
    private Long getMilliSeconds(String isoDateFormat){
		String [] dateAndtime = isoDateFormat.split("T");
		DateTime dateTime = new DateTime(
				 Integer.valueOf(dateAndtime[0].substring(0, 4)), 
						 Integer.valueOf(dateAndtime[0].substring(4, 6)), 
								 Integer.valueOf(dateAndtime[0].substring(6, 8)), 
										 Integer.valueOf(dateAndtime[1].substring(0, 2)), 
												 Integer.valueOf(dateAndtime[1].substring(2, 4)), 
														 Integer.valueOf(dateAndtime[1].substring(4, 6)));
        return dateTime.getMillis();
    	
    }
}
