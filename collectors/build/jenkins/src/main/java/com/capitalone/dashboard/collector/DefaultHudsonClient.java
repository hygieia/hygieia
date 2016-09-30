package com.capitalone.dashboard.collector;

import com.capitalone.dashboard.model.Build;
import com.capitalone.dashboard.model.BuildStatus;
import com.capitalone.dashboard.model.HudsonJob;
import com.capitalone.dashboard.model.RepoBranch;
import com.capitalone.dashboard.model.SCM;
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
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.StringReader;
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
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;


/**
 * HudsonClient implementation that uses RestTemplate and JSONSimple to
 * fetch information from Hudson instances.
 */
@Component
public class DefaultHudsonClient implements HudsonClient {
    private static final Logger LOG = LoggerFactory.getLogger(DefaultHudsonClient.class);

    private final RestOperations rest;
    private final HudsonSettings settings;

    private static final String JOBS_URL_SUFFIX = "/api/json?tree=jobs[name,url,builds[number,url]]";

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
            "changeSet[items[" + StringUtils.join(CHANGE_SET_ITEMS_TREE, ",") + "]",
            "kind",
            "revisions[module,revision]]",
            "actions[lastBuiltRevision[SHA1,branch[SHA1,name]],remoteUrls]"
    };

    private static final String BUILD_DETAILS_URL_SUFFIX = "/api/json?tree=" + StringUtils.join(BUILD_DETAILS_TREE, ",");

    @Autowired
    public DefaultHudsonClient(Supplier<RestOperations> restOperationsSupplier, HudsonSettings settings) {
        this.rest = restOperationsSupplier.get();
        this.settings = settings;
    }


    @Override
    public Map<HudsonJob, Set<Build>> getInstanceJobs(String instanceUrl) {
        Map<HudsonJob, Set<Build>> result = new LinkedHashMap<>();
        try {
            String url = joinURL(instanceUrl, JOBS_URL_SUFFIX);
            ResponseEntity<String> responseEntity = makeRestCall(url);
            String returnJSON = responseEntity.getBody();
            JSONParser parser = new JSONParser();

            try {
                JSONObject object = (JSONObject) parser.parse(returnJSON);

                for (Object job : getJsonArray(object, "jobs")) {
                    JSONObject jsonJob = (JSONObject) job;

                    final String jobName = getString(jsonJob, "name");
                    final String jobURL = getString(jsonJob, "url");
                    LOG.debug("Job:" + jobName);
                    LOG.debug("jobURL: " + jobURL);
                    
                    HudsonJob hudsonJob = new HudsonJob();
                    hudsonJob.setInstanceUrl(instanceUrl);
                    hudsonJob.setJobName(jobName);
                    hudsonJob.setJobUrl(jobURL);

                    Set<Build> builds = new LinkedHashSet<>();
                    for (Object build : getJsonArray(jsonJob, "builds")) {
                        JSONObject jsonBuild = (JSONObject) build;

                        // A basic Build object. This will be fleshed out later if this is a new Build.
                        String dockerLocalHostIP = settings.getDockerLocalHostIP();
                        String buildNumber = jsonBuild.get("number").toString();
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
                    // add the builds to the job
                    result.put(hudsonJob, builds);
                }
            } catch (ParseException e) {
                LOG.error("Parsing jobs on instance: " + instanceUrl, e);
            }
        } catch (RestClientException rce) {
            LOG.error("client exception loading jobs", rce);
            throw rce;
        } catch (MalformedURLException mfe) {
            LOG.error("malformed url for loading jobs", mfe);
        }
        return result;
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
                    addChangeSets(build, buildJson);
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
     * @param buildJson the build JSON object
     */
    private void addChangeSets(Build build, JSONObject buildJson) {
        JSONObject changeSet = (JSONObject) buildJson.get("changeSet");
        String scmType = getString(changeSet, "kind");
        Map<String, RepoBranch> revisionToUrl = new HashMap<>();

        // Build a map of revision to module (scm url). This is not always
        // provided by the Hudson API, but we can use it if available.
        // For git, this map is empty.
        for (Object revision : getJsonArray(changeSet, "revisions")) {
            JSONObject json = (JSONObject) revision;
            RepoBranch rb = new RepoBranch();
            rb.setUrl(getString(json, "module"));
            rb.setType(RepoBranch.RepoType.fromString(scmType));
            revisionToUrl.put(json.get("revision").toString(), rb);
            build.getCodeRepos().add(rb);
        }
        //For git SCM, the below is to get the repoBranch
        build.getCodeRepos().addAll(getGitRepoBranch(buildJson));

        for (Object item : getJsonArray(changeSet, "items")) {
            JSONObject jsonItem = (JSONObject) item;
            SCM scm = new SCM();
            scm.setScmAuthor(getCommitAuthor(jsonItem));
            scm.setScmCommitLog(getString(jsonItem, "msg"));
            scm.setScmCommitTimestamp(getCommitTimestamp(jsonItem));
            scm.setScmRevisionNumber(getRevision(jsonItem));
            RepoBranch repoBranch = revisionToUrl.get(scm.getScmRevisionNumber());
            if (repoBranch != null) {
                scm.setScmUrl(repoBranch.getUrl());
                scm.setScmBranch(repoBranch.getBranch());
            }

            scm.setNumberOfChanges(getJsonArray(jsonItem, "paths").size());
            build.getSourceChangeSet().add(scm);
        }
    }

    /**
     * Gathers repo urls, and the branch name from the last built revision.
     * Filters out the qualifiers from the branch name and sets the unqualified branch name.
     * We assume that all branches are in remotes/origin.
     */

    private List<RepoBranch> getGitRepoBranch(JSONObject buildJson) {
        List<RepoBranch> list = new ArrayList<>();        
        List<ArrayList<String>> remoteConfigs = getRemoteConfigs(buildJson);
        
        int remoteConfigIndex = 0;
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
                if (branches != null && !branches.isEmpty() && remoteUrls.size() > 1 
                		&& remoteConfigIndex >= 0 && remoteConfigIndex < remoteConfigs.size()) {     
                	// As of git plugin 3.0.0, when multiple repos are configured, they are stored unordered in a HashSet.
                	// So we get remote urls in order from job's config.xml so that we can associate the urls and the corresponding branches
                	List<String> orderedRemoteUrls = remoteConfigs.get(remoteConfigIndex);
                	remoteConfigIndex++;

                	for (Object branchObj : branches) {
                		String branchName = getString((JSONObject) branchObj, "name");
                		int originNumber = -1;
                		if (branchName != null) {
                			originNumber = getOriginNumber(branchName);
                		}
                		if (originNumber >= 0 && originNumber < orderedRemoteUrls.size()) {
                			String unqualifiedBranchName = getUnqualifiedBranch(branchName);
                			String sUrl = removeGitExtensionFromUrl(orderedRemoteUrls.get(originNumber));
                			RepoBranch grb = new RepoBranch(sUrl, unqualifiedBranchName, RepoBranch.RepoType.GIT);
                			list.add(grb);
                		}
                	}
                } else if (branches != null && !branches.isEmpty() && remoteUrls.size() == 1) {
                	remoteConfigIndex++;
                	String sUrl = removeGitExtensionFromUrl((String) remoteUrls.get(0));
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
        return list;
    }
    
    /**
     * Gathers the list of urls configured in the Jenkins job, the outer list being segregated by 
     * Multiple SCM plugin and the inner list being multiple repositories added without Multiple SCM plugin.
     */
    
    private List<ArrayList<String>> getRemoteConfigs(JSONObject buildJson) {
    	List<ArrayList<String>> remoteConfigs = new ArrayList<ArrayList<String>>();
        
        // use the build url to get the job url       
        String buildUrl = getString((JSONObject) buildJson, "url");
        if (buildUrl.lastIndexOf("/") == buildUrl.length() - 1) {
        	buildUrl = buildUrl.substring(0, buildUrl.length() - 1);
        }
        // remove the build number at the end to get the job url
        String jobUrl = buildUrl.substring(0, buildUrl.lastIndexOf("/"));
        // Get the remote configs (all SCM urls) from the job's config file
        try {
        	DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        	InputSource is = new InputSource();
        	ResponseEntity<String> result = makeRestCall(joinURL(jobUrl, "config.xml"));
        	is.setCharacterStream(new StringReader(result.getBody()));
        	Document configXML = db.parse(is);
        	if (configXML != null) {
        		parseConfig(configXML.getDocumentElement(), remoteConfigs);
            }
        } catch (ParserConfigurationException pcex) {
            LOG.error("Unable to create the document parser");
        } catch (SAXException sex) {
            LOG.error("Error parsing the XML document");
        } catch (MalformedURLException mfex) {
        	LOG.error("Invalid config URL");
        } catch (IOException ioex) {
        	LOG.error("Unable to parse output");
        }
        
        return remoteConfigs;
    }

    ////// Helpers
    
    /**
     * Recursive helper method to parse the job's config.xml file
     * @param root				the root element to parse from
     * @param remoteConfigs		the SCM urls in the config.xml are stored and returned back through this
     */
    
    private void parseConfig(Element root, List<ArrayList<String>> remoteConfigs) {
    	if (root != null) {
        	NodeList nodes = root.getChildNodes();
        	for (int i = 0; i < nodes.getLength(); i++) {
                Node node = nodes.item(i);
                if (node != null && node.getNodeName() != null && node instanceof Element) {
                	if ("userRemoteConfigs".equals(node.getNodeName())) {
                		// remote config Multi SCM set encountered
                		remoteConfigs.add(new ArrayList<String>());
                		parseConfig((Element)node, remoteConfigs);
                	} else if ("hudson.plugins.git.UserRemoteConfig".equals(node.getNodeName())) {
                		NodeList cfgNodes = node.getChildNodes();
                		for (int j = 0; j < cfgNodes.getLength(); j++) {
                            Node cfgNode = cfgNodes.item(j);
                            if (cfgNode != null && cfgNode.getNodeName() != null 
                            		&& cfgNode instanceof Element && "url".equals(cfgNode.getNodeName())) {
                            	// git urls encountered
                        		String content = cfgNode.getLastChild().getTextContent().trim();
                        		if (content != null) {
                        			ArrayList<String> repoUrls = remoteConfigs.get(remoteConfigs.size() - 1);
                        			repoUrls.add(content);
                        		}
                            }
                		}
                	} else {
                		parseConfig((Element)node, remoteConfigs);
                	}
                }                        	
        	}
        }
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
     * Gets the origin number given the qualified branch name of the following forms:
     * 1. refs/remotes/origin<number>/<branch name>
     * 2. remotes/origin<number>/<branch name>
     * 3. origin<number>/<branch name>
     * @param qualifiedBranch
     * @return the origin number
     */
        
    private int getOriginNumber(String qualifiedBranch) {
    	int branchNumber = 0;
    	Pattern pattern = Pattern.compile("(refs/)?(remotes/)?origin([0-9]*)/.*");
    	Matcher matcher = pattern.matcher(qualifiedBranch);
    	if(matcher.matches() && matcher.group(3) != null && !(matcher.group(3)).isEmpty()) {
			try {
				branchNumber = Integer.parseInt(matcher.group(3));
			} catch (NumberFormatException e) {
				LOG.error("Invalid origin number: " + matcher.group(3));
			}
    	}
    	return branchNumber;
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

    protected ResponseEntity<String> makeRestCall(String sUrl) throws MalformedURLException {
        URI thisuri = URI.create(sUrl);
        String userInfo = thisuri.getUserInfo();

        //get userinfo from URI or settings (in spring properties)
        if (StringUtils.isEmpty(userInfo) && (this.settings.getUsername() != null) && (this.settings.getApiKey() != null)) {
            userInfo = this.settings.getUsername() + ":" + this.settings.getApiKey();
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
