
package com.capitalone.dashboard.collector;


import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.net.MalformedURLException;
import java.net.URI;

import java.nio.charset.StandardCharsets;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import org.springframework.web.client.RestTemplate;

@EnableAsync
@Component
class BambooAsyncFetch { 

    private static final Logger LOG = LoggerFactory.getLogger(BambooAsyncFetch.class);

    @Bean
    public Executor asyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(20);
        executor.setMaxPoolSize(1000);
        executor.setThreadNamePrefix("Async-");
        return executor;
    }

    @Async
    public CompletableFuture<JSONObject> fetchPlanAsync(JSONObject plan, String instanceUrl, String jobsResultSuffix, 
            BambooSettings settings  ) {

        // get plan name
        String planName = getString(plan, "key");

        // get plan link
        JSONObject link = (JSONObject) plan.get("link");
        final String planURL = getString(link, "href");

        LOG.info("(ASYNC) FETCHING PLAN AND ITS SUB-PLANS / BRANCHES:  " + planName);

        JSONObject planJSON;
        JSONObject branchJSON;

        try {

            // Build plan URL
            String resultUrl = joinURL(instanceUrl, jobsResultSuffix);
            resultUrl = joinURL(resultUrl, planName);

            // Build sub-plans / branches URL
            String branchesUrl = joinURL(planURL, "/branch");

            // Fetch plans
            ResponseEntity<String> responseEntity = makeRestCall(resultUrl, settings);
            String planString = responseEntity.getBody();

            // Fetch sub-plans / branches
            responseEntity = makeRestCall(branchesUrl, settings);
            String branchString = responseEntity.getBody();

            try {
                JSONParser parser = new JSONParser();
                planJSON = (JSONObject) parser.parse(planString);
                branchJSON = (JSONObject) parser.parse(branchString);
                planJSON.put("plan", plan);
                planJSON.put("subplans", branchJSON);

            } catch (ParseException e) {
                planJSON = new JSONObject();
            }

        } catch (MalformedURLException mfe) {
            planJSON = new JSONObject();
        }

        return CompletableFuture.completedFuture(planJSON);

    }

    @Async
    public CompletableFuture<JSONObject> fetchBranchAsync(JSONObject branch, String instanceUrl,
            String jobsResultSuffix, BambooSettings settings) {

        String subPlan = branch.get("key").toString();
        LOG.info("(ASYNC) FETCHING BRANCH FOR SUBPLAN:  " + subPlan);

        JSONObject subplanJSON;

        try {

            // Build branch URL
            String resultUrl = joinURL(instanceUrl, jobsResultSuffix);
            resultUrl = joinURL(resultUrl, subPlan);

            // Fetch branches
            ResponseEntity<String> responseEntity = makeRestCall(resultUrl, settings);
            String subplanString = responseEntity.getBody();

            try {
                JSONParser parser = new JSONParser();
                subplanJSON = (JSONObject) parser.parse(subplanString);

            } catch (ParseException e) {
                subplanJSON = new JSONObject();
            }

        } catch (MalformedURLException mfe) {
            subplanJSON = new JSONObject();
        }

        return CompletableFuture.completedFuture(subplanJSON);

    }

    public String getString(JSONObject json, String key) {
        return (String) json.get(key);
    }

 

    private JSONArray getJsonArray(JSONObject json, String key) {
        Object array = json.get(key);
        return array == null ? new JSONArray() : (JSONArray) array;
    }

    protected ResponseEntity<String> makeRestCall(String sUrl, BambooSettings settings) throws MalformedURLException {
        URI thisuri = URI.create(sUrl);
        String userInfo = thisuri.getUserInfo();

        RestTemplate rest = new RestTemplate(); 

        // get userinfo from URI or settings (in spring properties)
        if (StringUtils.isEmpty(userInfo) && (settings.getUsername() != null)
                && (settings.getApiKey() != null)) {
            userInfo = settings.getUsername() + ":" + settings.getApiKey();
        }
        // Basic Auth only.
        if (StringUtils.isNotEmpty(userInfo)) {
            return rest.exchange(thisuri, HttpMethod.GET, new HttpEntity<>(createHeaders(userInfo)), String.class);
        } else {
            return rest.exchange(thisuri, HttpMethod.GET, null, String.class);
        }

    }

    // join a base url to another path or paths - this will handle trailing or
    // non-trailing /'s
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

    protected HttpHeaders createHeaders(final String userInfo) {
        byte[] encodedAuth = Base64.encodeBase64(userInfo.getBytes(StandardCharsets.US_ASCII));
        String authHeader = "Basic " + new String(encodedAuth);

        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION, authHeader);
        headers.set(HttpHeaders.ACCEPT, "application/json");
        return headers;
    }

}