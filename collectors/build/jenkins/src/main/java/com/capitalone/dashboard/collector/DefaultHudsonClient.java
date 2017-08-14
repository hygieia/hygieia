package com.capitalone.dashboard.collector;

import com.capitalone.dashboard.model.BaseModel;
import com.capitalone.dashboard.model.Build;
import com.capitalone.dashboard.model.BuildStatus;
import com.capitalone.dashboard.model.CollItemCfgHist;
import com.capitalone.dashboard.model.ConfigHistOperationType;
import com.capitalone.dashboard.model.HudsonJob;
import com.capitalone.dashboard.model.RepoBranch;
import com.capitalone.dashboard.model.SCM;
import com.capitalone.dashboard.util.Supplier;
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

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
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


/**
 * HudsonClient implementation that uses RestTemplate and JSONSimple to
 * fetch information from Hudson instances.
 */
@Component
public class DefaultHudsonClient implements HudsonClient {
    private static final Logger LOG = LoggerFactory.getLogger(DefaultHudsonClient.class);

    private final RestOperations rest;
    private final HudsonSettings settings;

    private static final String API_SUFFIX = "api/json?tree=";
    //private static final String JOB_QUERY = "jobs[name,url,builds[number,url],lastSuccessfulBuild[timestamp,builtOn],lastBuild[timestamp,builtOn]]";
    private static final String JOB_QUERY = "jobs[name,url,builds[number,url],lastSuccessfulBuild[timestamp,builtOn],lastBuild[timestamp,builtOn],actions[jobConfigHistory[currentName,date,hasConfig,job,oldName,operation,user,userID]]]";

    private static final String JOBS_URL_SUFFIX = "/api/json?tree=jobs";

    private static final String[] CHANGE_SET_ITEMS_TREE = new String[]{
            "user",
            "author[fullName]",
            "revision",
            "id",
            "msg",
            "timestamp",
            "date",
            "paths[file]"
    };

    private static final String[] BUILD_DETAILS_TREE = new String[]{
            "number",
            "url",
            "timestamp",
            "duration",
            "building",
            "result",
            "culprits[fullName]",
            "changeSets[items[" + StringUtils.join(CHANGE_SET_ITEMS_TREE, ",") + "],kind]",
            "changeSet[items[" + StringUtils.join(CHANGE_SET_ITEMS_TREE, ",") + "]",
            "kind",
            "revisions[module,revision]]",
            "actions[lastBuiltRevision[SHA1,branch[SHA1,name]],remoteUrls]"
    };

    private static final String BUILD_DETAILS_URL_SUFFIX = "/api/json?tree=" + StringUtils.join(BUILD_DETAILS_TREE, ",");

    private static final String DATE_FORMAT = "yyyy-MM-dd_HH-mm-ss";

    @Autowired
    public DefaultHudsonClient(Supplier<RestOperations> restOperationsSupplier, HudsonSettings settings) {
        this.rest = restOperationsSupplier.get();
        this.settings = settings;
    }

    @Override
    public Map<HudsonJob, Map<jobData, Set<BaseModel>>> getInstanceJobs(String instanceUrl) {
        LOG.debug("Enter getInstanceJobs");
        //Map<HudsonJob, Set<Build>> result = new LinkedHashMap<>();
        Map<HudsonJob, Map<jobData, Set<BaseModel>>> result = new LinkedHashMap<>();
        
        int jobsCount = getJobsCount(instanceUrl);
        LOG.debug("Number of jobs " + jobsCount);
        
        int i = 0, pageSize = settings.getPageSize();
        // Default pageSize to 1000 for backward compatibility of settings when pageSize defaults to 0
        if (pageSize <= 0) {
        	pageSize = 1000;
        }
        while (i < jobsCount) {
	        try {
                String url = joinURL(instanceUrl, API_SUFFIX + buildJobQueryString() + URLEncoder.encode("{" + i + "," + (i + pageSize) + "}", "UTF-8"));
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
	                JSONArray jobs = getJsonArray(object, "jobs");
	                if (jobs.size() == 0) {
	                	break;
	                }
	                
	                for (Object job : jobs) {
	                    JSONObject jsonJob = (JSONObject) job;
	
	                    final String jobName = getString(jsonJob, "name");
	                    final String jobURL = getString(jsonJob, "url");
                        final String jobClass = getString(jsonJob, "_class");

                        LOG.debug("Process jobName " + jobName + " jobURL " + jobURL + " jobClass " + jobClass);

	                    recursiveGetJobDetails(jsonJob, jobName, jobURL, instanceUrl, parser, result);
	                }
	            } catch (ParseException e) {
	                LOG.error("Parsing jobs details on instance: " + instanceUrl, e);
	            }
	        } catch (RestClientException rce) {
	            LOG.error("client exception loading jobs details", rce);
	            throw rce;
	        } catch (MalformedURLException mfe) {
	            LOG.error("malformed url for loading jobs details", mfe);
	        } catch (UnsupportedEncodingException uee) {
	        	LOG.error("unsupported encoding for loading jobs details", uee);
			} catch (URISyntaxException e1) {
			    LOG.error("wrong syntax url for loading jobs details", e1);
            }
	        
	        i += pageSize;
        }
        return result;
    }

    public String buildJobQueryString () {
        StringBuilder query = new StringBuilder(JOB_QUERY);
        int depth = settings.getFolderDepth();
        for (int i = 1; i < depth; i++) {
            query.insert((query.length()-i), ",");
            query.insert((query.length()-i), JOB_QUERY.substring(0,JOB_QUERY.length()-1));
            query.insert((query.length()-i), "]");
        }
        return query.toString();
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
                JSONArray jobs = getJsonArray(object, "jobs");
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

    @SuppressWarnings({"PMD.NPathComplexity","PMD.ExcessiveMethodLength","PMD.AvoidBranchingStatementAsLastInLoop","PMD.EmptyIfStmt"})
    private void recursiveGetJobDetails(JSONObject jsonJob, String jobName, String jobURL, String instanceUrl, 
            JSONParser parser, Map<HudsonJob, Map<jobData, Set<BaseModel>>> result) {
        LOG.debug("recursiveGetJobDetails: jobName " + jobName + " jobURL: " + jobURL);

        Map<jobData, Set<BaseModel>> jobDataMap = new HashMap();

        HudsonJob hudsonJob = new HudsonJob();
        hudsonJob.setInstanceUrl(instanceUrl);
        hudsonJob.setJobName(jobName);
        hudsonJob.setJobUrl(jobURL);

        JSONArray jsonBuilds = getJsonArray(jsonJob, "builds");
        if (!jsonBuilds.isEmpty()) {
    
            Set<BaseModel> builds = new LinkedHashSet<>();
            for (Object build : jsonBuilds) {
                JSONObject jsonBuild = (JSONObject) build;
    
                // A basic Build object. This will be fleshed out later if this is a new Build.
                String dockerLocalHostIP = settings.getDockerLocalHostIP();
                String buildNumber = jsonBuild.get("number").toString();
                LOG.debug(" buildNumber: " + buildNumber);
                if (!"0".equals(buildNumber)) {
                    Build hudsonBuild = new Build();
                    hudsonBuild.setNumber(buildNumber);
                    String buildURL = getString(jsonBuild, "url");
    
                    //Modify localhost if Docker Natting is being done
                    if (!dockerLocalHostIP.isEmpty()) {
                        buildURL = buildURL.replace("localhost", dockerLocalHostIP);
                        LOG.debug("Adding build & Updated URL to map LocalHost for Docker: " + buildURL);
                    } else {
                        LOG.debug(" Adding Build: " + buildURL);
                    }
    
                    hudsonBuild.setBuildUrl(buildURL);
                    builds.add(hudsonBuild);
                }
            }
            jobDataMap.put(jobData.BUILD, builds);
        }

        JSONArray jsonActions = getJsonArray(jsonJob, "actions");
        for (Object jsonAction : jsonActions) {
            JSONObject jsonActionJob = (JSONObject) jsonAction;
            JSONArray jsonConfigs = null;
            if (jsonActionJob != null) {
                jsonConfigs = getJsonArray(jsonActionJob, "jobConfigHistory");
            }

            if (jsonConfigs != null && jsonConfigs.size() > 0) {
                Set<BaseModel> configs = new LinkedHashSet<>();
                for (Object config : jsonConfigs) {
                    JSONObject jsonConfig = (JSONObject) config;

                    CollItemCfgHist hudsonConfig = new CollItemCfgHist();
                    hudsonConfig.setJob(getString(jsonConfig, "currentName"));
                    hudsonConfig.setTimestamp(timestamp(jsonConfig, "date"));
                    hudsonConfig.setHasConfig((getBoolean(jsonConfig, "hasConfig")));
                    hudsonConfig.setJob(getString(jsonConfig, "job"));
                    hudsonConfig.setOldName(getString(jsonConfig, "oldName"));
                    hudsonConfig.setOperation(ConfigHistOperationType.fromString(getString(jsonConfig, "operation")));
                    hudsonConfig.setUser(getString(jsonConfig, "user"));
                    hudsonConfig.setUserID(getString(jsonConfig, "userID"));
                    hudsonConfig.setJobUrl(jobURL);

                    configs.add(hudsonConfig);
                }
                jobDataMap.put(jobData.CONFIG, configs);
            }
        }

        if (jobDataMap.containsKey(jobData.BUILD) || jobDataMap.containsKey(jobData.CONFIG)) {
            // add the builds and configs to the job
            result.put(hudsonJob, jobDataMap);
        }

        JSONArray subJobs = getJsonArray(jsonJob, "jobs");

        for (Object subJob : subJobs) {
            // has sub-jobs (like Pipeline Multibranch project)
            final String subJobName = getString((JSONObject) subJob, "name");
            final String subJobURL = getString((JSONObject) subJob, "url");

            JSONObject jsonSubJob = (JSONObject) subJob;
            recursiveGetJobDetails(jsonSubJob, jobName + "/" + subJobName, subJobURL, instanceUrl, parser, result);
        }
    }

    @Override
    public Build getBuildDetails(String buildUrl, String instanceUrl) {
        try {
            String newUrl = rebuildJobUrl(buildUrl, instanceUrl);
            String url = joinURL(newUrl, BUILD_DETAILS_URL_SUFFIX);
            ResponseEntity<String> result = makeRestCall(url);
            String resultJSON = result.getBody();
            if (StringUtils.isEmpty(resultJSON)) {
                LOG.error("Error getting build details for. URL=" + url);
                return null;
            }
            JSONParser parser = new JSONParser();
            try {
                JSONObject buildJson = (JSONObject) parser.parse(resultJSON);
                Boolean building = (Boolean) buildJson.get("building");
                // Ignore jobs that are building
                if (!building) {
                    Build build = new Build();
                    build.setNumber(buildJson.get("number").toString());
                    build.setBuildUrl(buildUrl);
                    build.setTimestamp(System.currentTimeMillis());
                    build.setStartTime((Long) buildJson.get("timestamp"));
                    build.setDuration((Long) buildJson.get("duration"));
                    build.setEndTime(build.getStartTime() + build.getDuration());
                    build.setBuildStatus(getBuildStatus(buildJson));
                    build.setStartedBy(firstCulprit(buildJson));
                    if (settings.isSaveLog()) {
                        build.setLog(getLog(buildUrl));
                    }
                    
                    //For git SCM, add the repoBranches. For other SCM types, it's handled while adding changesets
                    build.getCodeRepos().addAll(getGitRepoBranch(buildJson));
                    
                    boolean isPipelineJob = "org.jenkinsci.plugins.workflow.job.WorkflowRun".equals(getString(buildJson, "_class"));
                    
                    // Need to handle duplicate changesets bug in Pipeline jobs (https://issues.jenkins-ci.org/browse/JENKINS-40352)
                    Set<String> commitIds = new HashSet<>();
                    // This is empty for git
                    Set<String> revisions = new HashSet<>();
                    
                    if (isPipelineJob) {
                        for (Object changeSetObj : getJsonArray(buildJson, "changeSets")) {
                            JSONObject changeSet = (JSONObject) changeSetObj;
                            addChangeSet(build, changeSet, commitIds, revisions);
                        }
                    } else {
                        JSONObject changeSet = (JSONObject) buildJson.get("changeSet");
                        if (changeSet != null) {
                            addChangeSet(build, changeSet, commitIds, revisions);
                        }
                    }
                    return build;
                }

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

    //This method will rebuild the API endpoint because the buildUrl obtained via Jenkins API
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
        URI newUri = new URI(instanceProtocol, userInfo, host, port, buildPath, null, null);
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
    private void addChangeSet(Build build, JSONObject changeSet, Set<String> commitIds, Set<String> revisions) {        
        String scmType = getString(changeSet, "kind");
        Map<String, RepoBranch> revisionToUrl = new HashMap<>();

        // Build a map of revision to module (scm url). This is not always
        // provided by the Hudson API, but we can use it if available.
        // For git, this map is empty.
        for (Object revision : getJsonArray(changeSet, "revisions")) {
            JSONObject json = (JSONObject) revision;
            String revisionId = json.get("revision").toString();
            if (StringUtils.isNotEmpty(revisionId) && !revisions.contains(revisionId)) {
                RepoBranch rb = new RepoBranch();
                rb.setUrl(getString(json, "module"));
                rb.setType(RepoBranch.RepoType.fromString(scmType));
                revisionToUrl.put(revisionId, rb);
                build.getCodeRepos().add(rb);
            }
        }

        for (Object item : getJsonArray(changeSet, "items")) {
            JSONObject jsonItem = (JSONObject) item;
            String commitId = getRevision(jsonItem);
            if (StringUtils.isNotEmpty(commitId) && !commitIds.contains(commitId)) {
                SCM scm = new SCM();
                scm.setScmAuthor(getCommitAuthor(jsonItem));
                scm.setScmCommitLog(getString(jsonItem, "msg"));
                scm.setScmCommitTimestamp(getCommitTimestamp(jsonItem));
                scm.setScmRevisionNumber(commitId);
                RepoBranch repoBranch = revisionToUrl.get(scm.getScmRevisionNumber());
                if (repoBranch != null) {
                    scm.setScmUrl(repoBranch.getUrl());
                    scm.setScmBranch(repoBranch.getBranch());
                }
    
                scm.setNumberOfChanges(getJsonArray(jsonItem, "paths").size());
                build.getSourceChangeSet().add(scm);
                commitIds.add(commitId);
            }
        }
    }

    /**
     * Gathers repo urls, and the branch name from the last built revision.
     * Filters out the qualifiers from the branch name and sets the unqualified branch name.
     * We assume that all branches are in remotes/origin.
     */

    @SuppressWarnings("PMD")
    private List<RepoBranch> getGitRepoBranch(JSONObject buildJson) {
        List<RepoBranch> list = new ArrayList<>();        
        
        JSONArray actions = getJsonArray(buildJson, "actions");
        for (Object action : actions) {
            JSONObject jsonAction = (JSONObject) action;
            if (jsonAction.size() > 0) {
                JSONObject lastBuiltRevision = null;
                JSONArray branches = null;
                JSONArray remoteUrls = getJsonArray ((JSONObject) action, "remoteUrls");       
                if (!remoteUrls.isEmpty()) {
                	lastBuiltRevision = (JSONObject) jsonAction.get("lastBuiltRevision");
                }
                if (lastBuiltRevision != null) {
                	branches = getJsonArray ((JSONObject) lastBuiltRevision, "branch");
                }
                // As of git plugin 3.0.0, when multiple repos are configured in the git plugin itself instead of MultiSCM plugin, 
            	// they are stored unordered in a HashSet. So it's buggy and we cannot associate the correct branch information.
                // So for now, we loop through all the remoteUrls and associate the built branch(es) with all of them.
                if (branches != null && !branches.isEmpty()) {
                	for (Object url : remoteUrls) {
                		String sUrl = (String) url;
                		if (sUrl != null && !sUrl.isEmpty()) {
                			sUrl = removeGitExtensionFromUrl(sUrl);
		                	for (Object branchObj : branches) {
		                		String branchName = getString((JSONObject) branchObj, "name");
		                		if (branchName != null) {
		                			String unqualifiedBranchName = getUnqualifiedBranch(branchName);
		                			RepoBranch grb = new RepoBranch(sUrl, unqualifiedBranchName, RepoBranch.RepoType.GIT);
		                			list.add(grb);
		                		}
		                	}
                		}
                	}
                }
            }
        }
        return list;
    }
    
    private String removeGitExtensionFromUrl(String url) {
    	String sUrl = url;
    	//remove .git from the urls
    	if (sUrl.endsWith(".git")) {
            sUrl = sUrl.substring(0, sUrl.lastIndexOf(".git"));
        }
    	return sUrl;
    }
    
    /**
     * Gets the unqualified branch name given the qualified one of the following forms:
     * 1. refs/remotes/<remote name>/<branch name>
     * 2. remotes/<remote name>/<branch name>
     * 3. origin/<branch name>
     * 4. <branch name>
     * @param qualifiedBranch
     * @return the unqualified branch name
     */
        
    private String getUnqualifiedBranch(String qualifiedBranch) {
    	String branchName = qualifiedBranch;
    	Pattern pattern = Pattern.compile("(refs/)?remotes/[^/]+/(.*)|(origin[0-9]*/)?(.*)");
    	Matcher matcher = pattern.matcher(branchName);
    	if(matcher.matches()) {
    		if (matcher.group(2) != null) {
    			branchName = matcher.group(2);
    		} else if (matcher.group(4) != null) {
    			branchName = matcher.group(4);
    		}
    	}
    	return branchName;
    }

    private long getCommitTimestamp(JSONObject jsonItem) {
        if (jsonItem.get("timestamp") != null) {
            return (Long) jsonItem.get("timestamp");
        } else if (jsonItem.get("date") != null) {
            String dateString = (String) jsonItem.get("date");
            try {
                return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS").parse(dateString).getTime();
            } catch (java.text.ParseException e) {
                // Try an alternate date format...looks like this one is used by Git
                try {
                    return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z").parse(dateString).getTime();
                } catch (java.text.ParseException e1) {
                    LOG.error("Invalid date string: " + dateString, e);
                }
            }
        }
        return 0;
    }

    private String getString(JSONObject json, String key) {
        return (String) json.get(key);
    }

    private Boolean getBoolean(JSONObject json, String key) {
        return (Boolean) json.get(key);
    }

    private long timestamp(JSONObject json, String key) {
        Object obj = json.get(key);
        if (obj != null) {
            try {
                return new SimpleDateFormat(DATE_FORMAT).parse(obj.toString()).getTime();
            } catch (java.text.ParseException e) {
                LOG.warn(obj + " is not in expected format " + DATE_FORMAT + e);
            }
        }
        return 0;
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
        JSONArray culprits = getJsonArray(buildJson, "culprits");
        if (CollectionUtils.isEmpty(culprits)) {
            return null;
        }
        JSONObject culprit = (JSONObject) culprits.get(0);
        return getFullName(culprit);
    }

    private String getFullName(JSONObject author) {
        return getString(author, "fullName");
    }

    private String getCommitAuthor(JSONObject jsonItem) {
        // Use user if provided, otherwise use author.fullName
        JSONObject author = (JSONObject) jsonItem.get("author");
        return author == null ? getString(jsonItem, "user") : getFullName(author);
    }

    private BuildStatus getBuildStatus(JSONObject buildJson) {
        String status = buildJson.get("result").toString();
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
        					+ "in the build url returned by Jenkins and the Jenkins instance url in your Hygieia configuration do not match: "
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
            return rest.exchange(thisuri, HttpMethod.GET, null,
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
}
