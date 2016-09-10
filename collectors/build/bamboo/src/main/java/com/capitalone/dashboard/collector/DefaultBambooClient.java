package com.capitalone.dashboard.collector;

import com.capitalone.dashboard.model.Build;
import com.capitalone.dashboard.model.BuildStatus;
import com.capitalone.dashboard.model.BambooJob;
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

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.LinkedHashMap;
import java.util.Date;


/**
 * BambooClient implementation that uses RestTemplate and JSONSimple to
 * fetch information from Bamboo instances.
 */
@Component
public class DefaultBambooClient implements BambooClient {
    private static final Logger LOG = LoggerFactory.getLogger(DefaultBambooClient.class);

    private final RestOperations rest;
    private final BambooSettings settings;

    private static final String JOBS_URL_SUFFIX = "rest/api/latest/plan?expand=plans&max-result=2000";
    private static final String JOBS_RESULT_SUFFIX= "rest/api/latest/result/";
    private static final String BUILD_DETAILS_URL_SUFFIX = "?expand=results.result.artifacts&expand=changes.change.files";

    @Autowired
    public DefaultBambooClient(Supplier<RestOperations> restOperationsSupplier, BambooSettings settings) {
        this.rest = restOperationsSupplier.get();
        this.settings = settings;
    }
    @SuppressWarnings("PMD.ExcessiveMethodLength")
    @Override
    public Map<BambooJob, Set<Build>> getInstanceJobs(String instanceUrl) {
        Map<BambooJob, Set<Build>> result = new LinkedHashMap<>();
        try {
            String url = joinURL(instanceUrl, JOBS_URL_SUFFIX);
            ResponseEntity<String> responseEntity = makeRestCall(url);
            String returnJSON = responseEntity.getBody();
            // LOG.info(returnJSON);
            JSONParser parser = new JSONParser();

            try {
                 JSONObject object = (JSONObject) parser.parse(returnJSON);

                for (Object job : getJsonArray((JSONObject)object.get("plans"), "plan")) {
                    JSONObject jsonJob = (JSONObject) job;

                    final String planName = getString(jsonJob, "key");
                    JSONObject link=(JSONObject)jsonJob.get("link");
                    final String planURL = getString(link, "href");

                    LOG.info("Plan:" + planName);
                    LOG.info("PlanURL: " + planURL);

                    // In terms of Bamboo this is the plan not job
                    BambooJob bambooJob = new BambooJob();
                    bambooJob.setInstanceUrl(instanceUrl);
                    bambooJob.setJobName(planName);
                    bambooJob.setJobUrl(planURL);

                    // Finding out the results of the top-level plan 

                    String resultUrl = joinURL(instanceUrl,JOBS_RESULT_SUFFIX);
                    resultUrl = joinURL(resultUrl,planName);
//                    LOG.info("Job:" + planName);
//                    LOG.info("Result URL:"+ resultUrl);
                    responseEntity = makeRestCall(resultUrl);
                    returnJSON = responseEntity.getBody();
//                    LOG.info("Result :"+ returnJSON);
                    jsonJob = (JSONObject) parser.parse(returnJSON);

                    Set<Build> builds = new LinkedHashSet<>();
                    for (Object build : getJsonArray((JSONObject)jsonJob.get("results"), "result")) {
                        JSONObject jsonBuild = (JSONObject) build;
//                        LOG.info("Entered each build for job : "+ planName);
                        // A basic Build object. This will be fleshed out later if this is a new Build.
                        String dockerLocalHostIP = settings.getDockerLocalHostIP();
                        String buildNumber = jsonBuild.get("buildNumber").toString();
                        if (!"0".equals(buildNumber)) {
//                            LOG.info("BuildNO " + buildNumber + " for planName: " + planName);
                            Build bambooBuild = new Build();
                            bambooBuild.setNumber(buildNumber);
                            String buildURL = joinURL(resultUrl,buildNumber); //getString(jsonBuild, "url");
//                            LOG.info(buildURL);
                            //Modify localhost if Docker Natting is being done
                            if (!dockerLocalHostIP.isEmpty()) {
                                buildURL = buildURL.replace("localhost", dockerLocalHostIP);
                                LOG.debug("Adding build & Updated URL to map LocalHost for Docker: " + buildURL);
                            } else {
                                LOG.debug(" Adding Build: " + buildURL);
                            }

                            bambooBuild.setBuildUrl(buildURL);
                            builds.add(bambooBuild);
                        }
                    }
                    // add the builds to the job
                    result.put(bambooJob, builds);

                    //But we might have many branches and subplans in them so we have to find them out as well
                    String branchesUrl= joinURL(planURL,"/branch");
                    responseEntity = makeRestCall(branchesUrl);
                    returnJSON = responseEntity.getBody();
                    JSONObject jsonBranches = (JSONObject) parser.parse(returnJSON);

                    for (Object branch : getJsonArray((JSONObject)jsonBranches.get("branches"), "branch")) {
                        JSONObject branchObject=(JSONObject) branch;
                        String subPlan=branchObject.get("key").toString();
                        // Figure out nested jobs under the branches

                        resultUrl = joinURL(instanceUrl,JOBS_RESULT_SUFFIX);
                        resultUrl = joinURL(resultUrl,subPlan);
                        LOG.info("sub Plan:" + subPlan);
                        LOG.info("sub plan-Result URL:"+ resultUrl);
                        responseEntity = makeRestCall(resultUrl);
                        returnJSON = responseEntity.getBody();
    //                    LOG.info("Result :"+ returnJSON);
                        jsonJob = (JSONObject) parser.parse(returnJSON);

                        for (Object build : getJsonArray((JSONObject)jsonJob.get("results"), "result")) {
                            JSONObject jsonBuild = (JSONObject) build;
                           LOG.info("Entered each build for nested plan : "+ subPlan);
                            // A basic Build object. This will be fleshed out later if this is a new Build.
                            String dockerLocalHostIP = settings.getDockerLocalHostIP();
                            String buildNumber = jsonBuild.get("buildNumber").toString();
                            if (!"0".equals(buildNumber)) {
    //                            LOG.info("BuildNO " + buildNumber + " for planName: " + planName);
                                Build bambooBuild = new Build();
                                bambooBuild.setNumber(buildNumber);
                                String buildURL = joinURL(resultUrl,buildNumber); //getString(jsonBuild, "url");
    //                            LOG.info(buildURL);
                                //Modify localhost if Docker Natting is being done
                                if (!dockerLocalHostIP.isEmpty()) {
                                    buildURL = buildURL.replace("localhost", dockerLocalHostIP);
                                    LOG.debug("Adding build & Updated URL to map LocalHost for Docker: " + buildURL);
                                } else {
                                    LOG.debug(" Adding Build: " + buildURL);
                                }

                                bambooBuild.setBuildUrl(buildURL);
                                builds.add(bambooBuild);
                            }
                        }
                        // add the builds to the job
                        result.put(bambooJob, builds);

                        // Ended with nested branches
                    }

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
//            LOG.info("Build Details URL:"+ url);
            ResponseEntity<String> result = makeRestCall(url);
            String resultJSON = result.getBody();
//            LOG.info("Build Details :"+ resultJSON);
            if (StringUtils.isEmpty(resultJSON)) {
                LOG.error("Error getting build details for. URL=" + url);
                return null;
            }
            JSONParser parser = new JSONParser();
            try {
                JSONObject buildJson = (JSONObject) parser.parse(resultJSON);
                Boolean finished = (Boolean) buildJson.get("finished");
                // Ignore jobs that are building
                if (finished) {
                    Build build = new Build();
                    build.setNumber(buildJson.get("buildNumber").toString());
                    build.setBuildUrl(buildUrl);
                    build.setTimestamp(System.currentTimeMillis());
                    
                    
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");//"2016-06-23T09:13:29.961+07:00"
                    Date parsedDate = dateFormat.parse(buildJson.get("buildStartedTime").toString());
                    build.setStartTime((Long) parsedDate.getTime());
                    
                    build.setDuration((Long) buildJson.get("buildDuration"));
                    build.setEndTime(build.getStartTime() + build.getDuration());
                    build.setBuildStatus(getBuildStatus(buildJson));
//                    build.setStartedBy(firstCulprit(buildJson));
                    if (settings.isSaveLog()) {
                        build.setLog(getLog(buildUrl));
                    }
                    addChangeSets(build, buildJson);
                    return build;
                }

            } catch (Exception e) {
                LOG.error("Parsing build: " + buildUrl, e);
            }
        } catch (RestClientException rce) {
            LOG.error("Client exception loading build details: " + rce.getMessage() + ". URL =" + buildUrl );
        } catch (MalformedURLException mfe) {
            LOG.error("Malformed url for loading build details" + mfe.getMessage() + ". URL =" + buildUrl );
        } catch (URISyntaxException use) {
            LOG.error("Uri syntax exception for loading build details"+ use.getMessage() + ". URL =" + buildUrl );
        } catch (RuntimeException re) {
            LOG.error("Unknown error in getting build details. URL="+ buildUrl, re);
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
        JSONObject changeSet = (JSONObject) buildJson.get("changes");

//        Map<String, String> revisionToUrl = new HashMap<>();

//        // Build a map of revision to module (scm url). This is not always
//        // provided by the Bamboo API, but we can use it if available.
//        for (Object revision : getJsonArray(changeSet, "revisions")) {
//            JSONObject json = (JSONObject) revision;
//            revisionToUrl.put(json.get("revision").toString(), getString(json, "module"));
//        }

        for (Object item : getJsonArray(changeSet, "change")) {
            JSONObject jsonItem = (JSONObject) item;
            SCM scm = new SCM();
            scm.setScmAuthor(getString(jsonItem, "author"));
            scm.setScmCommitLog(getString(jsonItem, "comment"));
            scm.setScmCommitTimestamp(getCommitTimestamp(jsonItem));
            scm.setScmRevisionNumber(getRevision(jsonItem));
            scm.setScmUrl(getString(jsonItem,"commitUrl"));
            scm.setNumberOfChanges(getJsonArray((JSONObject)jsonItem.get("files"), "file").size());
            build.getSourceChangeSet().add(scm);
        }
    }

    ////// Helpers

    private long getCommitTimestamp(JSONObject jsonItem) {
        if (jsonItem.get("timestamp") != null) {
            return (Long) jsonItem.get("timestamp");
        } else if (jsonItem.get("date") != null) {
            String dateString = (String) jsonItem.get("date");
            try {
                return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX").parse(dateString).getTime();
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
        return revision == null ? getString(jsonItem, "changesetId") : revision.toString();
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
        String status = buildJson.get("buildState").toString();
        switch (status) {
            case "Successful":
                return BuildStatus.Success;
            case "UNSTABLE":
                return BuildStatus.Unstable;
            case "Failed":
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
        headers.set(HttpHeaders.ACCEPT,"application/json");
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
