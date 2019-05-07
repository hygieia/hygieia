package com.capitalone.dashboard.collector;

import com.capitalone.dashboard.model.Audit;
import com.capitalone.dashboard.model.AuditStatus;
import com.capitalone.dashboard.model.DataStatus;
import com.capitalone.dashboard.model.AuditType;
import com.capitalone.dashboard.model.AuditResult;
import com.capitalone.dashboard.model.Dashboard;
import com.capitalone.dashboard.model.Cmdb;

import com.capitalone.dashboard.repository.AuditResultRepository;
import com.capitalone.dashboard.repository.CmdbRepository;
import com.capitalone.dashboard.status.CodeReviewAuditStatus;
import com.capitalone.dashboard.status.DashboardAuditStatus;
import com.capitalone.dashboard.status.CodeQualityAuditStatus;
import com.capitalone.dashboard.status.PerformanceTestAuditStatus;
import com.capitalone.dashboard.status.LibraryPolicyAuditStatus;
import com.capitalone.dashboard.status.TestResultAuditStatus;
import org.apache.http.client.utils.URIBuilder;
import org.bson.types.ObjectId;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.jmx.MBeanServerNotFoundException;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Arrays;
import java.util.stream.Stream;
import java.util.Optional;

/**
 * <h1>AuditCollectorUtil</h1>
 * Utility class for NFRR Audit Collector
 *
 * @since 10/04/2018
 */
public class AuditCollectorUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuditCollectorUtil.class);
    private static final String HYGIEIA_AUDIT_URL = "/dashboardReview";
    private static List<AuditResult> auditResults = new ArrayList<>();
    private static final String AUDITTYPES_PARAM  = "CODE_REVIEW,CODE_QUALITY,STATIC_SECURITY_ANALYSIS,LIBRARY_POLICY,TEST_RESULT,PERF_TEST";
    private enum AUDIT_PARAMS {title,businessService,businessApplication,beginDate,endDate,auditType};


    private static final String STR_URL = "url";
    private static final String STR_REPORTURL = "reportUrl";
    private static final String STR_LIBRARYPOLICYRESULT = "libraryPolicyResult";
    private static final String STR_PULLREQUESTS = "pullRequests";
    private static final String STR_DIRECTCOMMITS = "directCommits";
    private static final String STR_AUDITSTATUSES = "auditStatuses";
    private static final String STR_REVIEW = "review";
    private static final String STR_APIUSER = "apiUser";
    private static final String STR_APITOKENSPACE = "apiToken ";
    private static final String STR_AUTHORIZATION = "Authorization";
    private static final String STR_TRACEABILITY = "traceability";


    /**
     * Get Code Review Audit Results
     */
    private static Audit getCodeReviewAudit(JSONArray jsonArray, JSONArray global) {

        LOGGER.info("NFRR Audit Collector auditing CODE_REVIEW");
        Audit audit = new Audit();
        audit.setType(AuditType.CODE_REVIEW);

        Audit basicAudit;
        if ((basicAudit = doBasicAuditCheck(jsonArray, global, AuditType.CODE_REVIEW)) != null) {
            return basicAudit;
        }
        audit.setAuditStatus(AuditStatus.OK);
        audit.setDataStatus(DataStatus.OK);
        for (Object o : jsonArray) {
            JSONObject jo = (JSONObject) o;
            audit.getUrl().add((String) jo.get(STR_URL));
            JSONArray directCommits = (JSONArray) jo.get(STR_DIRECTCOMMITS);
            if (!CollectionUtils.isEmpty(directCommits)) {
                audit.setAuditStatus(AuditStatus.FAIL);
                audit.setDataStatus(DataStatus.OK);
                return audit;
            }
            JSONArray pulls = (JSONArray) ((JSONObject) o).get(STR_PULLREQUESTS);
            for (Object po : pulls) {
                JSONArray auditJO = (JSONArray) ((JSONObject) po).get(STR_AUDITSTATUSES);
                boolean reviewed = false;
                auditJO.stream().map(aj -> audit.getAuditStatusCodes().add((String) aj));

                for (Object s : auditJO) {
                    String status = (String) s;
                    if (CodeReviewAuditStatus.PEER_REVIEW_GHR.name().equalsIgnoreCase(status) ||
                            (CodeReviewAuditStatus.PEER_REVIEW_LGTM_SUCCESS.name().equalsIgnoreCase(status))) {
                        reviewed = true;
                        break;
                    }
                }
                if (!reviewed) {
                    audit.setAuditStatus(AuditStatus.FAIL);
                    audit.setDataStatus(DataStatus.OK);
                    return audit;
                }
            }
        }
        return audit;
    }

    /**
     * Do basic audit check - configuration, collector error, no data
     */
    private static Audit doBasicAuditCheck(JSONArray jsonArray, JSONArray global, AuditType auditType) {
        Audit audit = new Audit();
        audit.setType(auditType);
        if (!isConfigured(auditType, global)) {
            audit.setDataStatus(DataStatus.NOT_CONFIGURED);
            audit.setAuditStatus(AuditStatus.NA);
            return audit;
        }
        if (jsonArray == null || CollectionUtils.isEmpty(jsonArray)) {
            audit.setAuditStatus(AuditStatus.NA);
            audit.setDataStatus(DataStatus.NO_DATA);
            return audit;
        }
        if (isCollectorError(jsonArray)) {
            audit.setAuditStatus(AuditStatus.NA);
            audit.setDataStatus(DataStatus.ERROR);
            return audit;
        }
        return null;
    }

    /**
     * Check for collector error
     */
    private static boolean isCollectorError(JSONArray jsonArray) {
        Stream<JSONObject> jsonObjectStream = jsonArray.stream().map((Object object) -> (JSONObject) object);
        Stream<JSONArray> auditStatusArray = jsonObjectStream.map(jsonObject -> (JSONArray) jsonObject.get(STR_AUDITSTATUSES));
        return auditStatusArray.anyMatch(aSArray -> aSArray.toJSONString().contains(DashboardAuditStatus.COLLECTOR_ITEM_ERROR.name()));
    }

    /**
     * Check for dashboard audit type configuration
     */
    @SuppressWarnings("PMD.NPathComplexity")
    private static boolean isConfigured(AuditType auditType, JSONArray jsonArray) {
        if (auditType.equals(AuditType.CODE_REVIEW)) {
            return (jsonArray.toJSONString().contains(DashboardAuditStatus.DASHBOARD_REPO_CONFIGURED.name()) ? true : false);
        }
        if (auditType.equals(AuditType.CODE_QUALITY)) {
            return (jsonArray.toJSONString().contains(DashboardAuditStatus.DASHBOARD_CODEQUALITY_CONFIGURED.name()) ? true : false);
        }
        if (auditType.equals(AuditType.STATIC_SECURITY_ANALYSIS)) {
            return (jsonArray.toJSONString().contains(DashboardAuditStatus.DASHBOARD_STATIC_SECURITY_ANALYSIS_CONFIGURED.name()) ? true : false);
        }
        if (auditType.equals(AuditType.LIBRARY_POLICY)) {
            return (jsonArray.toJSONString().contains(DashboardAuditStatus.DASHBOARD_LIBRARY_POLICY_ANALYSIS_CONFIGURED.name()) ? true : false);
        }
        if (auditType.equals(AuditType.TEST_RESULT)) {
            return (jsonArray.toJSONString().contains(DashboardAuditStatus.DASHBOARD_TEST_CONFIGURED.name()) ? true : false);
        }
        if (auditType.equals(AuditType.PERF_TEST)) {
            return (jsonArray.toJSONString().contains(DashboardAuditStatus.DASHBOARD_PERFORMANCE_TEST_CONFIGURED.name()) ? true : false);
        }
        if (auditType.equals(AuditType.BUILD_REVIEW)) {
            return (jsonArray.toJSONString().contains(DashboardAuditStatus.DASHBOARD_BUILD_CONFIGURED.name()) ? true : false);
        }
        return false;
    }


    /**
     * Get code quality audit results
     */
    private static Audit getCodeQualityAudit(JSONArray jsonArray, JSONArray global) {

        LOGGER.info("NFRR Audit Collector auditing CODE_QUALITY");
        Audit audit = new Audit();
        audit.setType(AuditType.CODE_QUALITY);
        Audit basicAudit;
        if ((basicAudit = doBasicAuditCheck(jsonArray, global, AuditType.CODE_QUALITY)) != null) {
            return basicAudit;
        }
        audit.setAuditStatus(AuditStatus.OK);
        audit.setDataStatus(DataStatus.OK);
        for (Object o : jsonArray) {
            Optional<Object> urlOptObj = Optional.ofNullable(((JSONObject) o).get(STR_URL));
            urlOptObj.ifPresent(urlObj -> audit.getUrl().add(urlOptObj.get().toString()));
            JSONArray auditJO = (JSONArray) ((JSONObject) o).get(STR_AUDITSTATUSES);
            auditJO.stream().map(aj -> audit.getAuditStatusCodes().add((String) aj));
            boolean ok = false;
            for (Object s : auditJO) {
                String status = (String) s;
                audit.getAuditStatusCodes().add(status);
                if (CodeQualityAuditStatus.CODE_QUALITY_AUDIT_OK.name().equalsIgnoreCase(status)) {
                    ok = true;
                    break;
                }
                if (CodeQualityAuditStatus.CODE_QUALITY_DETAIL_MISSING.name().equalsIgnoreCase(status)) {
                    audit.setAuditStatus(AuditStatus.NA);
                    audit.setDataStatus(DataStatus.NO_DATA);
                    return audit;
                }
            }
            if (!ok) {
                audit.setAuditStatus(AuditStatus.FAIL);
                return audit;
            }
        }
        return audit;
    }


    /**
     * Get security audit results
     */
    private static Audit getSecurityAudit(JSONArray jsonArray, JSONArray global) {

        LOGGER.info("NFRR Audit Collector auditing STATIC_SECURITY_ANALYSIS");
        Audit audit = new Audit();
        audit.setType(AuditType.STATIC_SECURITY_ANALYSIS);
        Audit basicAudit;
        if ((basicAudit = doBasicAuditCheck(jsonArray, global, AuditType.STATIC_SECURITY_ANALYSIS)) != null) {
            return basicAudit;
        }
        Set<String> auditStatuses = new HashSet<>();
        for (Object o : jsonArray) {
            JSONArray auditJO = (JSONArray) ((JSONObject) o).get(STR_AUDITSTATUSES);
            Optional<Object> urlOptObj = Optional.ofNullable(((JSONObject) o).get(STR_URL));
            urlOptObj.ifPresent(urlObj -> audit.getUrl().add(urlOptObj.get().toString()));
            for (Object a:auditJO) { auditStatuses.add((String) a); }
        }
        if(auditStatuses.contains(CodeQualityAuditStatus.STATIC_SECURITY_SCAN_FAIL.name()) ||
                auditStatuses.contains(CodeQualityAuditStatus.STATIC_SECURITY_SCAN_FOUND_HIGH.name()) ||
                auditStatuses.contains(CodeQualityAuditStatus.STATIC_SECURITY_SCAN_FOUND_CRITICAL.name())){
            audit.setAuditStatus(AuditStatus.FAIL);
            audit.setDataStatus(DataStatus.OK);
        }else if(auditStatuses.contains(CodeQualityAuditStatus.STATIC_SECURITY_SCAN_OK.name()) ){
            audit.setAuditStatus(AuditStatus.OK);
            audit.setDataStatus(DataStatus.OK);
        }else {
            audit.setAuditStatus(AuditStatus.NA);
            audit.setDataStatus(DataStatus.NO_DATA);
        }
        return audit;
    }


    /**
     * Get library policy  audit results
     */
    private static Audit getOSSAudit(JSONArray jsonArray, JSONArray global) {

        LOGGER.info("NFRR Audit Collector auditing LIBRARY_POLICY");
        Audit audit = new Audit();
        audit.setType(AuditType.LIBRARY_POLICY);

        Audit basicAudit;
        if ((basicAudit = doBasicAuditCheck(jsonArray, global, AuditType.LIBRARY_POLICY)) != null) {
            return basicAudit;
        }
        audit.setAuditStatus(AuditStatus.OK);
        audit.setDataStatus(DataStatus.OK);
        for (Object o : jsonArray) {
            JSONArray auditJO = (JSONArray) ((JSONObject) o).get(STR_AUDITSTATUSES);
            Optional<Object> lpOptObj = Optional.ofNullable(((JSONObject) o).get(STR_LIBRARYPOLICYRESULT));
            lpOptObj.ifPresent(lpObj -> audit.getUrl().add(((JSONObject) lpOptObj.get()).get(STR_REPORTURL).toString()));
            auditJO.stream().map(aj -> audit.getAuditStatusCodes().add((String) aj));
            boolean ok = false;
            for (Object s : auditJO) {
                String status = (String) s;
                if (LibraryPolicyAuditStatus.LIBRARY_POLICY_AUDIT_OK.name().equalsIgnoreCase(status)) {
                    ok = true;
                    break;
                }
                if (LibraryPolicyAuditStatus.LIBRARY_POLICY_AUDIT_MISSING.name().equalsIgnoreCase(status)) {
                    audit.setAuditStatus(AuditStatus.NA);
                    audit.setDataStatus(DataStatus.NO_DATA);
                    return audit;
                }
            }
            if (!ok) {
                audit.setAuditStatus(AuditStatus.FAIL);
                return audit;
            }
        }
        return audit;
    }


    /**
     * Get test result audit results
     */
    private static Audit getTestAudit(JSONArray jsonArray, JSONArray global) {

        LOGGER.info("NFRR Audit Collector auditing TEST_RESULT");
        Audit audit = new Audit();
        audit.setType(AuditType.TEST_RESULT);

        Audit basicAudit;
        if ((basicAudit = doBasicAuditCheck(jsonArray, global, AuditType.TEST_RESULT)) != null) {
            return basicAudit;
        }
        audit.setAuditStatus(AuditStatus.OK);
        audit.setDataStatus(DataStatus.OK);
        for (Object o : jsonArray) {
            Map tMap = (Map) ((JSONObject) o).get(STR_TRACEABILITY);
            audit.setTraceability(tMap != null ? tMap : new HashMap());
            JSONArray auditJO = (JSONArray) ((JSONObject) o).get(STR_AUDITSTATUSES);
            Optional<Object> urlOptObj = Optional.ofNullable(((JSONObject) o).get(STR_URL));
            urlOptObj.ifPresent(urlObj -> audit.getUrl().add(urlOptObj.get().toString()));
            auditJO.stream().map(aj -> audit.getAuditStatusCodes().add((String) aj));
            boolean ok = false;
            for (Object s : auditJO) {
                String status = (String) s;
                if (TestResultAuditStatus.TEST_RESULT_AUDIT_OK.name().equalsIgnoreCase(status)) {
                    ok = true;
                    break;
                }
                if (TestResultAuditStatus.TEST_RESULT_MISSING.name().equalsIgnoreCase(status)) {
                    audit.setAuditStatus(AuditStatus.NA);
                    audit.setDataStatus(DataStatus.NO_DATA);
                    return audit;
                }
            }
            if (!ok) {
                audit.setAuditStatus(AuditStatus.FAIL);
                return audit;
            }
        }
        return audit;
    }


    /**
     * Get performance testing audit results
     */
    private static Audit getPerfAudit(JSONArray jsonArray, JSONArray global) {

        LOGGER.info("NFRR Audit Collector auditing PERF_TEST");
        Audit audit = new Audit();
        audit.setType(AuditType.PERF_TEST);

        Audit basicAudit;
        if ((basicAudit = doBasicAuditCheck(jsonArray, global, AuditType.PERF_TEST)) != null) {
            return basicAudit;
        }
        audit.setAuditStatus(AuditStatus.OK);
        audit.setDataStatus(DataStatus.OK);
        for (Object o : jsonArray) {
            JSONArray auditJO = (JSONArray) ((JSONObject) o).get(STR_AUDITSTATUSES);
            Optional<Object> urlOptObj = Optional.ofNullable(((JSONObject) o).get(STR_URL));
            urlOptObj.ifPresent(urlObj -> audit.getUrl().add(urlOptObj.get().toString()));
            auditJO.stream().map(aj -> audit.getAuditStatusCodes().add((String) aj));
            boolean ok = false;
            for (Object s : auditJO) {
                String status = (String) s;
                if (PerformanceTestAuditStatus.PERF_RESULT_AUDIT_OK.name().equalsIgnoreCase(status)) {
                    ok = true;
                    break;
                }
                if (PerformanceTestAuditStatus.PERF_RESULT_AUDIT_MISSING.name().equalsIgnoreCase(status)) {
                    audit.setAuditStatus(AuditStatus.NA);
                    audit.setDataStatus(DataStatus.NO_DATA);
                    return audit;
                }
            }
            if (!ok) {
                audit.setAuditStatus(AuditStatus.FAIL);
                return audit;
            }
        }
        return audit;
    }

    /**
     * Get all audit results
     */
    @SuppressWarnings("PMD")
    public static Map<AuditType, Audit> getAudit(Dashboard dashboard, AuditSettings settings, long begin, long end) {
        Map<AuditType, Audit> audits = new HashMap<>();

        String url = getAuditAPIUrl(dashboard, settings, begin, end);
        JSONObject auditResponseObj = parseObject(url, settings);
        if(auditResponseObj == null){
            return audits;
        }
        JSONArray globalStatus = (JSONArray) auditResponseObj.get(STR_AUDITSTATUSES);
        JSONObject review = (JSONObject) auditResponseObj.get(STR_REVIEW);

        JSONArray codeReviewJO = review.get(AuditType.CODE_REVIEW.name()) == null ? null : (JSONArray) review.get(AuditType.CODE_REVIEW.name());
        Audit audit = getCodeReviewAudit(codeReviewJO, globalStatus);
        audits.put(audit.getType(), audit);

        JSONArray scaJO = review.get(AuditType.CODE_QUALITY.name()) == null ? null : (JSONArray) review.get(AuditType.CODE_QUALITY.name());
        audit = getCodeQualityAudit(scaJO, globalStatus);
        audits.put(audit.getType(), audit);

        JSONArray perfJO = review.get(AuditType.PERF_TEST.name()) == null ? null : (JSONArray) review.get(AuditType.PERF_TEST.name());
        audit = getPerfAudit(perfJO, globalStatus);
        audits.put(audit.getType(), audit);

        JSONArray ossJO = review.get(AuditType.LIBRARY_POLICY.name()) == null ? null : (JSONArray) review.get(AuditType.LIBRARY_POLICY.name());
        audit = getOSSAudit(ossJO, globalStatus);
        audits.put(audit.getType(), audit);

        JSONArray testJO = review.get(AuditType.TEST_RESULT.name()) == null ? null : (JSONArray) review.get(AuditType.TEST_RESULT.name());
        audit = getTestAudit(testJO, globalStatus);
        audits.put(audit.getType(), audit);

        JSONArray sscaJO = review.get(AuditType.STATIC_SECURITY_ANALYSIS.name()) == null ? null : (JSONArray) review.get(AuditType.STATIC_SECURITY_ANALYSIS.name());
        audit = getSecurityAudit(sscaJO, globalStatus);
        audits.put(audit.getType(), audit);
        return audits;
    }

    /**
     * Make audit api rest call and parse response
     */
    protected static JSONObject parseObject(String url, AuditSettings settings){
        LOGGER.info("NFRR Audit Collector Audit API Call");
        RestTemplate restTemplate = new RestTemplate();
        JSONObject responseObj = null;
        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, getHeaders(settings), String.class);
            JSONParser jsonParser = new JSONParser();
            responseObj = (JSONObject) jsonParser.parse(response.getBody());
        } catch (Exception e) {
            LOGGER.error("Error while calling audit api for the params : " + url.substring(url.lastIndexOf("?")),e);
        }
        return responseObj;
    }

    /**
     * Construct audit api url
     */
    protected static String getAuditAPIUrl(Dashboard dashboard, AuditSettings settings, long beginDate, long endDate) {
        LOGGER.info("NFRR Audit Collector creates Audit API URL");
        if (CollectionUtils.isEmpty(settings.getServers())) {
            LOGGER.error("No Server Found to run NoFearRelease audit collector");
            throw new MBeanServerNotFoundException("No Server Found to run NoFearRelease audit collector");
        }
        URIBuilder auditURI = new URIBuilder();
        auditURI.setPath(settings.getServers().get(0) + HYGIEIA_AUDIT_URL);
        auditURI.addParameter(AUDIT_PARAMS.title.name(), dashboard.getTitle());
        auditURI.addParameter(AUDIT_PARAMS.businessService.name(), dashboard.getConfigurationItemBusServName());
        auditURI.addParameter(AUDIT_PARAMS.businessApplication.name(), dashboard.getConfigurationItemBusAppName());
        auditURI.addParameter(AUDIT_PARAMS.beginDate.name(), String.valueOf(beginDate));
        auditURI.addParameter(AUDIT_PARAMS.endDate.name(), String.valueOf(endDate));
        auditURI.addParameter(AUDIT_PARAMS.auditType.name(),"");
        String auditURIStr = auditURI.toString().replace("+", " ");
        return auditURIStr + AUDITTYPES_PARAM;
    }

    /**
     * Get api authentication headers
     */
    protected static HttpEntity getHeaders(AuditSettings auditSettings) {
        HttpHeaders headers = new HttpHeaders();
        if (!CollectionUtils.isEmpty(auditSettings.getUsernames()) && !CollectionUtils.isEmpty(auditSettings.getApiKeys())) {
            headers.set(STR_APIUSER, auditSettings.getUsernames().iterator().next());
            headers.set(STR_AUTHORIZATION, STR_APITOKENSPACE + auditSettings.getApiKeys().iterator().next());
        }
        return new HttpEntity<>(headers);
    }

    /**
     * Add audit result by audit type
     */
    @SuppressWarnings("PMD.NPathComplexity")
    public static void addAuditResultByAuditType(Dashboard dashboard, Map<AuditType, Audit> auditMap, CmdbRepository cmdbRepository, long timestamp) {

        if(CollectionUtils.isEmpty(auditMap)){ return; }
        Cmdb cmdb = cmdbRepository.findByConfigurationItem(dashboard.getConfigurationItemBusServName());
        ObjectId dashboardId = dashboard.getId();
        String dashboardTitle = dashboard.getTitle();
        String ownerDept = ((cmdb == null || cmdb.getOwnerDept() == null) ? "" : cmdb.getOwnerDept());
        String appService = (dashboard.getConfigurationItemBusServName() == null ? "" : dashboard.getConfigurationItemBusServName());
        String appBusApp = (dashboard.getConfigurationItemBusAppName() == null ? "" : dashboard.getConfigurationItemBusAppName());
        String appServiceOwner = ((cmdb == null || cmdb.getAppServiceOwner() == null) ? "" : cmdb.getAppServiceOwner());
        String appBusAppOwner = ((cmdb == null || cmdb.getBusinessOwner() == null) ? "" : cmdb.getBusinessOwner());

        Arrays.stream(AuditType.values()).forEach((AuditType auditType) -> {
            if (!(auditType.equals(AuditType.ALL) || auditType.equals(AuditType.BUILD_REVIEW))) {
                Audit audit = auditMap.get(auditType);
                AuditResult auditResult = new AuditResult(dashboardId, dashboardTitle, ownerDept, appService, appBusApp, appServiceOwner, appBusAppOwner,
                        auditType, audit.getDataStatus().name(), audit.getAuditStatus().name(), String.join(",", audit.getAuditStatusCodes()),
                        String.join(",", audit.getUrl()), audit.getTraceability(), timestamp);
                auditResults.add(auditResult);
            }
        });
    }

    /**
     * Get audit results collection
     */
    protected static List<AuditResult> getAuditResults() {
        return auditResults;
    }

    /**
     * Clear audit results repository
     */
    public static void clearAuditResultRepo(AuditResultRepository auditResultRepository) {
        LOGGER.info("NFRR Audit Collector clears last collected audit results from database");
        auditResultRepository.deleteAll();
    }

    /**
     * Clear audit results collection
     */
    public static void clearAuditResults() {
        auditResults.clear();
    }
}
