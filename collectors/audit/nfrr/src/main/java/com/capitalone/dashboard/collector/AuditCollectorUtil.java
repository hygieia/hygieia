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
import org.bson.types.ObjectId;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.jmx.MBeanServerNotFoundException;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

/**
 * <h1>AuditCollectorUtil</h1>
 * Utility class for NFRR Audit Collector
 *
 * @since 10/04/2018
 */
public class AuditCollectorUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuditCollectorUtil.class);
    private static final String HYGIEIA_AUDIT_URL = "/api-audit/dashboardReview?";
    private static List<AuditResult> auditResults = new ArrayList<>();

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
        } else {
            audit.setAuditStatus(AuditStatus.OK);
            audit.setDataStatus(DataStatus.OK);
            for (Object o : jsonArray) {
                JSONObject jo = (JSONObject) o;
                audit.getUrl().add((String) jo.get("url"));
                JSONArray directCommits = (JSONArray) jo.get("directCommits");
                if (!CollectionUtils.isEmpty(directCommits)) {
                    audit.setAuditStatus(AuditStatus.FAIL);
                    audit.setDataStatus(DataStatus.OK);
                    return audit;
                }
                JSONArray pulls = (JSONArray) ((JSONObject) o).get("pullRequests");
                for (Object po : pulls) {
                    JSONArray auditJO = (JSONArray) ((JSONObject) po).get("auditStatuses");
                    boolean reviewed = false;
                    auditJO.stream().map(aj -> audit.getAuditStatusCodes().add((String) aj));

                    for (Object s : auditJO) {
                        String status = (String) s;
                        if ("PEER_REVIEW_GHR".equalsIgnoreCase(status) || ("PEER_REVIEW_LGTM_SUCCESS".equalsIgnoreCase(status))) {
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
        } else if (jsonArray == null || CollectionUtils.isEmpty(jsonArray)) {
            audit.setAuditStatus(AuditStatus.NA);
            audit.setDataStatus(DataStatus.NO_DATA);
            return audit;
        } else if (isCollectorError(jsonArray)) {
            audit.setAuditStatus(AuditStatus.NA);
            audit.setDataStatus(DataStatus.ERROR);
            return audit;
        } else {
            return null;
        }
    }

    /**
     * Check for collector error
     */
    private static boolean isCollectorError(JSONArray jsonArray) {
        Stream<JSONObject> jsonObjectStream = jsonArray.stream().map((Object object) -> (JSONObject) object);
        Stream<JSONArray> auditStatusArray = jsonObjectStream.map(jsonObject -> (JSONArray) jsonObject.get("auditStatuses"));
        return auditStatusArray.anyMatch(aSArray -> aSArray.toJSONString().contains("COLLECTOR_ITEM_ERROR"));
    }

    /**
     * Check for dashboard audit type configuration
     */
    private static boolean isConfigured(AuditType auditType, JSONArray jsonArray) {
        if (auditType.equals(AuditType.CODE_REVIEW)) {
            return (jsonArray.toJSONString().contains("DASHBOARD_REPO_CONFIGURED") ? true : false);
        } else if (auditType.equals(AuditType.CODE_QUALITY)) {
            return (jsonArray.toJSONString().contains("DASHBOARD_CODEQUALITY_CONFIGURED") ? true : false);
        } else if (auditType.equals(AuditType.STATIC_SECURITY_ANALYSIS)) {
            return (jsonArray.toJSONString().contains("DASHBOARD_STATIC_SECURITY_ANALYSIS_CONFIGURED") ? true : false);
        } else if (auditType.equals(AuditType.LIBRARY_POLICY)) {
            return (jsonArray.toJSONString().contains("DASHBOARD_LIBRARY_POLICY_ANALYSIS_CONFIGURED") ? true : false);
        } else if (auditType.equals(AuditType.TEST_RESULT)) {
            return (jsonArray.toJSONString().contains("DASHBOARD_TEST_CONFIGURED") ? true : false);
        } else if (auditType.equals(AuditType.PERF_TEST)) {
            return (jsonArray.toJSONString().contains("DASHBOARD_PERFORMANCE_TEST_CONFIGURED") ? true : false);
        } else if (auditType.equals(AuditType.BUILD_REVIEW)) {
            return (jsonArray.toJSONString().contains("DASHBOARD_BUILD_CONFIGURED") ? true : false);
        } else {
            return false;
        }
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
        } else {
            audit.setAuditStatus(AuditStatus.OK);
            audit.setDataStatus(DataStatus.OK);
            for (Object o : jsonArray) {
                audit.getUrl().add((String) ((JSONObject) o).get("url"));
                JSONArray auditJO = (JSONArray) ((JSONObject) o).get("auditStatuses");
                auditJO.stream().map(aj -> audit.getAuditStatusCodes().add((String) aj));
                boolean ok = false;
                for (Object s : auditJO) {
                    String status = (String) s;
                    audit.getAuditStatusCodes().add(status);
                    if ("CODE_QUALITY_AUDIT_OK".equalsIgnoreCase(status)) {
                        ok = true;
                        break;
                    }
                    if ("CODE_QUALITY_DETAIL_MISSING".equalsIgnoreCase(status)) {
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
        } else {
            audit.setAuditStatus(AuditStatus.OK);
            audit.setDataStatus(DataStatus.OK);
            for (Object o : jsonArray) {
                JSONArray auditJO = (JSONArray) ((JSONObject) o).get("auditStatuses");
                audit.getUrl().add((String) ((JSONObject) o).get("url"));
                auditJO.stream().map(aj -> audit.getAuditStatusCodes().add((String) aj));
                boolean ok = false;
                for (Object s : auditJO) {
                    String status = (String) s;
                    if ("STATIC_SECURITY_SCAN_OK".equalsIgnoreCase(status)) {
                        ok = true;
                        break;
                    }
                    if ("STATIC_SECURITY_SCAN_MISSING".equalsIgnoreCase(status)) {
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
        } else {
            audit.setAuditStatus(AuditStatus.OK);
            audit.setDataStatus(DataStatus.OK);
            for (Object o : jsonArray) {
                JSONArray auditJO = (JSONArray) ((JSONObject) o).get("auditStatuses");
                audit.getUrl().add((String) ((JSONObject) o).get("url"));
                auditJO.stream().map(aj -> audit.getAuditStatusCodes().add((String) aj));
                boolean ok = false;
                for (Object s : auditJO) {
                    String status = (String) s;
                    if ("LIBRARY_POLICY_AUDIT_OK".equalsIgnoreCase(status)) {
                        ok = true;
                        break;
                    }
                    if ("LIBRARY_POLICY_AUDIT_MISSING".equalsIgnoreCase(status)) {
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
        } else {
            audit.setAuditStatus(AuditStatus.OK);
            audit.setDataStatus(DataStatus.OK);
            for (Object o : jsonArray) {
                JSONArray auditJO = (JSONArray) ((JSONObject) o).get("auditStatuses");
                audit.getUrl().add((String) ((JSONObject) o).get("url"));
                auditJO.stream().map(aj -> audit.getAuditStatusCodes().add((String) aj));
                boolean ok = false;
                for (Object s : auditJO) {
                    String status = (String) s;
                    if ("TEST_RESULT_AUDIT_OK".equalsIgnoreCase(status)) {
                        ok = true;
                        break;
                    }
                    if ("TEST_RESULT_MISSING".equalsIgnoreCase(status)) {
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
        } else {
            audit.setAuditStatus(AuditStatus.OK);
            audit.setDataStatus(DataStatus.OK);
            for (Object o : jsonArray) {
                JSONArray auditJO = (JSONArray) ((JSONObject) o).get("auditStatuses");
                audit.getUrl().add((String) ((JSONObject) o).get("url"));
                auditJO.stream().map(aj -> audit.getAuditStatusCodes().add((String) aj));
                boolean ok = false;
                for (Object s : auditJO) {
                    String status = (String) s;
                    if ("PERF_RESULT_AUDIT_OK".equalsIgnoreCase(status)) {
                        ok = true;
                        break;
                    }
                    if ("PERF_RESULT_AUDIT_MISSING".equalsIgnoreCase(status)) {
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
        }
        return audit;
    }

    /**
     * Get all audit results
     */
    @SuppressWarnings("PMD")
    public static Map<AuditType, Audit> getAudit(Dashboard dashboard, AuditSettings settings, long begin, long end) throws ParseException {
        Map<AuditType, Audit> audits = new HashMap<>();

        String url = getAuditAPIUrl(dashboard, settings, begin, end);

        JSONObject jsonObject = parseObject(url, settings);

        JSONArray globalStatus = (JSONArray) jsonObject.get("auditStatuses");

        JSONObject review = (JSONObject) jsonObject.get("review");

        JSONArray codeReviewJO = review.get("CODE_REVIEW") == null ? null : (JSONArray) review.get("CODE_REVIEW");
        Audit audit = getCodeReviewAudit(codeReviewJO, globalStatus);
        audits.put(audit.getType(), audit);

        JSONArray scaJO = review.get("CODE_QUALITY") == null ? null : (JSONArray) review.get("CODE_QUALITY");
        audit = getCodeQualityAudit(scaJO, globalStatus);
        audits.put(audit.getType(), audit);

        JSONArray perfJO = review.get("PERF_TEST") == null ? null : (JSONArray) review.get("PERF_TEST");
        audit = getPerfAudit(perfJO, globalStatus);
        audits.put(audit.getType(), audit);

        JSONArray ossJO = review.get("LIBRARY_POLICY") == null ? null : (JSONArray) review.get("LIBRARY_POLICY");
        audit = getOSSAudit(ossJO, globalStatus);
        audits.put(audit.getType(), audit);

        JSONArray testJO = review.get("TEST_RESULT") == null ? null : (JSONArray) review.get("TEST_RESULT");
        audit = getTestAudit(testJO, globalStatus);
        audits.put(audit.getType(), audit);

        JSONArray sscaJO = review.get("STATIC_SECURITY_ANALYSIS") == null ? null : (JSONArray) review.get("STATIC_SECURITY_ANALYSIS");
        audit = getSecurityAudit(sscaJO, globalStatus);
        audits.put(audit.getType(), audit);

        return audits;
    }

    /**
     * Make audit api rest call and parse response
     */
    protected static JSONObject parseObject(String url, AuditSettings settings) throws ParseException {
        LOGGER.info("NFRR Audit Collector Audit API Call");
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, getHeaders(settings), String.class);
        JSONParser jsonParser = new JSONParser();
        return (JSONObject) jsonParser.parse(response.getBody());
    }

    /**
     * Construct audit api url
     */
    @SuppressWarnings("PMD.NPathComplexity")
    protected static String getAuditAPIUrl(Dashboard dashboard, AuditSettings settings, long beginDate, long endDate) {

        LOGGER.info("NFRR Audit Collector creates Audit API URL");
        if (CollectionUtils.isEmpty(settings.getServers())) {
            LOGGER.error("No Server Found to run NoFearRelease audit collector");
            throw new MBeanServerNotFoundException("No Server Found to run NoFearRelease audit collector");
        }
        return settings.getServers().get(0)
                + HYGIEIA_AUDIT_URL.concat("auditType=ALL"
                .concat("&title=").concat(dashboard.getTitle() == null ? "" : dashboard.getTitle())
                .concat("&businessService=").concat(dashboard.getConfigurationItemBusServName() == null ? "" : dashboard.getConfigurationItemBusServName())
                .concat("&businessApplication=").concat(dashboard.getConfigurationItemBusAppName() == null ? "" : dashboard.getConfigurationItemBusAppName())
                .concat("&beginDate=").concat(String.valueOf(beginDate))
                .concat("&endDate=").concat(String.valueOf(endDate)));
    }

    /**
     * Get api authentication headers
     */
    protected static HttpEntity getHeaders(AuditSettings auditSettings) {
        HttpHeaders headers = new HttpHeaders();
        if (!CollectionUtils.isEmpty(auditSettings.getUsernames()) && !CollectionUtils.isEmpty(auditSettings.getApiKeys())) {
            headers.set("apiUser", auditSettings.getUsernames().iterator().next());
            headers.set("Authorization", "apiToken " + auditSettings.getApiKeys().iterator().next());
        }
        return new HttpEntity<>(headers);
    }

    /**
     * Add audit result by audit type
     */
    @SuppressWarnings("PMD.NPathComplexity")
    public static void addAuditResultByAuditType(Dashboard dashboard, Map<AuditType, Audit> auditMap, CmdbRepository cmdbRepository, long timestamp) {
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
                        String.join(",", audit.getUrl()), timestamp);
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
