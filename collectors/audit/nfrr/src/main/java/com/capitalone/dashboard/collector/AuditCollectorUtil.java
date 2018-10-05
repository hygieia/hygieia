package com.capitalone.dashboard.collector;

import com.capitalone.dashboard.model.*;
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

import java.util.*;
import java.util.stream.Stream;

public class AuditCollectorUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuditCollectorUtil.class);
    private static final String HYGIEIA_AUDIT_URL = "/api-audit/dashboardReview?";
    private static List<AuditResult> auditResults = new ArrayList();

    private static Audit getCodeReviewAudit(JSONArray jsonArray, JSONArray global) {
        Audit audit = new Audit();
        audit.setType(AuditType.CODE_REVIEW);

        Audit basicAudit;
        if((basicAudit = getBasicAudit(jsonArray, global, AuditType.CODE_REVIEW)) != null){
            return basicAudit;
        }
        else {
            audit.setAuditStatus(AuditStatus.OK);
            audit.setDataStatus(DataStatus.OK);
            Stream<JSONObject> jsonObjectStream = jsonArray.stream().map((Object object) -> (JSONObject) object);
            jsonObjectStream.forEach(jsonObject -> audit.getUrl().add((String) jsonObject.get("url")));
            if(jsonObjectStream.anyMatch(jsonObject -> (!CollectionUtils.isEmpty((JSONArray) jsonObject.get("directCommits"))))){
                audit.setAuditStatus(AuditStatus.FAIL);
                audit.setDataStatus(DataStatus.OK);
                return audit;
            }
            else {
                Stream<JSONObject> pullStream = jsonObjectStream.filter(jsonObject -> (!((JSONObject)jsonObject.get("pullRequests")).isEmpty()));
                for (Object po : pullStream.toArray()) {
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

    private static Audit getBasicAudit(JSONArray jsonArray, JSONArray global, AuditType auditType) {
        Audit audit = new Audit();
        audit.setType(auditType);
        if(!isConfigured(auditType, global)){
            audit.setDataStatus(DataStatus.NOT_CONFIGURED);
            audit.setAuditStatus(AuditStatus.NA);
            return audit;
        }
        else if(jsonArray == null || CollectionUtils.isEmpty(jsonArray)){
            audit.setAuditStatus(AuditStatus.NA);
            audit.setDataStatus(DataStatus.NO_DATA);
            return audit;
        }
        else if(isCollectorError(jsonArray)) {
            audit.setAuditStatus(AuditStatus.NA);
            audit.setDataStatus(DataStatus.ERROR);
            return audit;
        }
        else{
            return null;
        }
    }

    private static boolean isCollectorError(JSONArray jsonArray){
        Stream<JSONObject> jsonObjectStream = jsonArray.stream().map((Object object) -> (JSONObject)object);
        Stream<JSONArray> auditStatusArray = jsonObjectStream.map(jsonObject -> (JSONArray)jsonObject.get("auditStatuses"));
        return auditStatusArray.anyMatch(aSArray -> aSArray.toJSONString().contains("COLLECTOR_ITEM_ERROR"));
    }

    private static boolean isConfigured(AuditType auditType, JSONArray jsonArray) {
        if (auditType.equals(AuditType.CODE_REVIEW)){
            return (jsonArray.toJSONString().contains("DASHBOARD_REPO_CONFIGURED")? true : false);
        }
        else if (auditType.equals(AuditType.CODE_QUALITY)){
            return (jsonArray.toJSONString().contains("DASHBOARD_CODEQUALITY_CONFIGURED")? true : false);
        }
        else if (auditType.equals(AuditType.STATIC_SECURITY_ANALYSIS)){
            return (jsonArray.toJSONString().contains("DASHBOARD_STATIC_SECURITY_ANALYSIS_CONFIGURED")? true : false);
        }
        else if (auditType.equals(AuditType.LIBRARY_POLICY)){
            return (jsonArray.toJSONString().contains("DASHBOARD_LIBRARY_POLICY_ANALYSIS_CONFIGURED")? true : false);
        }
        else if (auditType.equals(AuditType.TEST_RESULT)){
            return (jsonArray.toJSONString().contains("DASHBOARD_TEST_CONFIGURED")? true : false);
        }
        else if (auditType.equals(AuditType.PERF_TEST)){
            return (jsonArray.toJSONString().contains("DASHBOARD_PERFORMANCE_TEST_CONFIGURED")? true : false);
        }
        else if (auditType.equals(AuditType.BUILD_REVIEW)){
            return (jsonArray.toJSONString().contains("DASHBOARD_BUILD_CONFIGURED")? true : false);
        }else{
            return false;
        }
    }


    private static Audit getCodeQualityAudit(JSONArray jsonArray, JSONArray global) {
        Audit audit = new Audit();
        audit.setType(AuditType.CODE_QUALITY);
        Audit basicAudit;
        if ((basicAudit = getBasicAudit(jsonArray, global, AuditType.CODE_QUALITY)) != null) {
            return basicAudit;
        } else {
            audit.setAuditStatus(AuditStatus.OK);
            audit.setDataStatus(DataStatus.OK);
            Stream<JSONObject> jsonObjectStream = jsonArray.stream().map((Object object) -> (JSONObject) object);
            jsonObjectStream.forEach(jsonObject -> audit.getUrl().add((String) jsonObject.get("url")));
            Stream<JSONArray> auditStatusArray = jsonObjectStream.map(jsonObject -> (JSONArray)jsonObject.get("auditStatuses"));
            auditStatusArray.forEach(auditStatusArray1 -> audit.getAuditStatusCodes().add(auditStatusArray1.toJSONString()));
            if(auditStatusArray.anyMatch(aSArray->aSArray.toJSONString().contains("CODE_QUALITY_DETAIL_MISSING"))){
                audit.setAuditStatus(AuditStatus.NA);
                audit.setDataStatus(DataStatus.NO_DATA);
                return audit;
            }
            else if(auditStatusArray.anyMatch(aSArray->aSArray.toJSONString().contains("CODE_QUALITY_AUDIT_OK"))){
                return audit;
            }
            else{
                audit.setAuditStatus(AuditStatus.FAIL);
                return audit;
            }
        }
    }


    private static Audit getSecurityAudit(JSONArray jsonArray, JSONArray global) {
        Audit audit = new Audit();
        audit.setType(AuditType.STATIC_SECURITY_ANALYSIS);
        Audit basicAudit;
        if ((basicAudit = getBasicAudit(jsonArray, global, AuditType.STATIC_SECURITY_ANALYSIS)) != null) {
            return basicAudit;
        }else {
            audit.setAuditStatus(AuditStatus.OK);
            audit.setDataStatus(DataStatus.OK);
            Stream<JSONObject> jsonObjectStream = jsonArray.stream().map((Object object) -> (JSONObject) object);
            jsonObjectStream.forEach(jsonObject -> audit.getUrl().add((String) jsonObject.get("url")));
            Stream<JSONArray> auditStatusArray = jsonObjectStream.map(jsonObject -> (JSONArray)jsonObject.get("auditStatuses"));
            auditStatusArray.forEach(auditStatusArray1 -> audit.getAuditStatusCodes().add(auditStatusArray1.toJSONString()));
            if(auditStatusArray.anyMatch(aSArray->aSArray.toJSONString().contains("STATIC_SECURITY_SCAN_MISSING"))){
                audit.setAuditStatus(AuditStatus.NA);
                audit.setDataStatus(DataStatus.NO_DATA);
                return audit;
            }
            else if(auditStatusArray.anyMatch(aSArray->aSArray.toJSONString().contains("STATIC_SECURITY_SCAN_OK"))){
                return audit;
            }
            else{
                audit.setAuditStatus(AuditStatus.FAIL);
                return audit;
            }
        }
    }


    private static Audit getOSSAudit(JSONArray jsonArray, JSONArray global) {
        Audit audit = new Audit();
        audit.setType(AuditType.LIBRARY_POLICY);

        Audit basicAudit;
        if ((basicAudit = getBasicAudit(jsonArray, global, AuditType.LIBRARY_POLICY)) != null) {
            return basicAudit;
        }else {
            audit.setAuditStatus(AuditStatus.OK);
            audit.setDataStatus(DataStatus.OK);
            Stream<JSONObject> jsonObjectStream = jsonArray.stream().map((Object object) -> (JSONObject) object);
            jsonObjectStream.forEach(jsonObject -> audit.getUrl().add((String) jsonObject.get("url")));
            Stream<JSONArray> auditStatusArray = jsonObjectStream.map(jsonObject -> (JSONArray) jsonObject.get("auditStatuses"));
            auditStatusArray.forEach(auditStatusArray1 -> audit.getAuditStatusCodes().add(auditStatusArray1.toJSONString()));
            if (auditStatusArray.anyMatch(aSArray -> aSArray.toJSONString().contains("LIBRARY_POLICY_AUDIT_MISSING"))) {
                audit.setAuditStatus(AuditStatus.NA);
                audit.setDataStatus(DataStatus.NO_DATA);
                return audit;
            } else if (auditStatusArray.anyMatch(aSArray -> aSArray.toJSONString().contains("LIBRARY_POLICY_AUDIT_OK"))) {
                return audit;
            } else {
                audit.setAuditStatus(AuditStatus.FAIL);
                return audit;
            }
        }
    }


    private static Audit getTestAudit(JSONArray jsonArray, JSONArray global) {
        Audit audit = new Audit();
        audit.setType(AuditType.TEST_RESULT);

        Audit basicAudit;
        if ((basicAudit = getBasicAudit(jsonArray, global, AuditType.TEST_RESULT)) != null) {
            return basicAudit;
        }else {
            audit.setAuditStatus(AuditStatus.OK);
            audit.setDataStatus(DataStatus.OK);
            Stream<JSONObject> jsonObjectStream = jsonArray.stream().map((Object object) -> (JSONObject) object);
            jsonObjectStream.forEach(jsonObject -> audit.getUrl().add((String) jsonObject.get("url")));
            Stream<JSONArray> auditStatusArray = jsonObjectStream.map(jsonObject -> (JSONArray) jsonObject.get("auditStatuses"));
            auditStatusArray.forEach(auditStatusArray1 -> audit.getAuditStatusCodes().add(auditStatusArray1.toJSONString()));
            if (auditStatusArray.anyMatch(aSArray -> aSArray.toJSONString().contains("TEST_RESULT_MISSING"))) {
                audit.setAuditStatus(AuditStatus.NA);
                audit.setDataStatus(DataStatus.NO_DATA);
                return audit;
            } else if (auditStatusArray.anyMatch(aSArray -> aSArray.toJSONString().contains("TEST_RESULT_AUDIT_OK"))) {
                return audit;
            } else {
                audit.setAuditStatus(AuditStatus.FAIL);
                return audit;
            }
        }
    }


    private static Audit getPerfAudit(JSONArray jsonArray, JSONArray global) {
        Audit audit = new Audit();
        audit.setType(AuditType.PERF_TEST);

        Audit basicAudit;
        if ((basicAudit = getBasicAudit(jsonArray, global, AuditType.PERF_TEST)) != null) {
            return basicAudit;
        }else {
            audit.setAuditStatus(AuditStatus.OK);
            audit.setDataStatus(DataStatus.OK);
            Stream<JSONObject> jsonObjectStream = jsonArray.stream().map((Object object) -> (JSONObject) object);
            jsonObjectStream.forEach(jsonObject -> audit.getUrl().add((String) jsonObject.get("url")));
            Stream<JSONArray> auditStatusArray = jsonObjectStream.map(jsonObject -> (JSONArray) jsonObject.get("auditStatuses"));
            auditStatusArray.forEach(auditStatusArray1 -> audit.getAuditStatusCodes().add(auditStatusArray1.toJSONString()));
            if (auditStatusArray.anyMatch(aSArray -> aSArray.toJSONString().contains("PERF_RESULT_AUDIT_MISSING"))) {
                audit.setAuditStatus(AuditStatus.NA);
                audit.setDataStatus(DataStatus.NO_DATA);
                return audit;
            } else if (auditStatusArray.anyMatch(aSArray -> aSArray.toJSONString().contains("PERF_RESULT_AUDIT_OK"))) {
                return audit;
            } else {
                audit.setAuditStatus(AuditStatus.FAIL);
                return audit;
            }
        }
    }

    @SuppressWarnings("PMD")
    public static Map<AuditType, Audit> getAudit(Dashboard dashboard, AuditSettings settings, long begin, long end) throws ParseException {
        Map<AuditType, Audit> audits = new HashMap<>();
        String url = getUrl(dashboard, settings, begin, end);

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

        LOGGER.info("Audit key set number =" + audits.keySet().size());
        return audits;
    }

    protected static JSONObject parseObject(String url, AuditSettings settings) throws ParseException {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, getHeaders(settings), String.class);
        JSONParser jsonParser = new JSONParser();
        return (JSONObject) jsonParser.parse(response.getBody());
    }

    @SuppressWarnings("PMD.NPathComplexity")
    protected static String getUrl(Dashboard dashboard, AuditSettings settings, long beginDate, long endDate) {
        if(CollectionUtils.isEmpty(settings.getServers())){
            LOGGER.error("No Server Found to run NoFearRelease audit collector");
            throw new MBeanServerNotFoundException("No Server Found to run NoFearRelease audit collector");
        }
        return settings.getServers().get(0)
                + HYGIEIA_AUDIT_URL.concat("auditType=CODE_REVIEW,CODE_QUALITY,STATIC_SECURITY_ANALYSIS,LIBRARY_POLICY"
                .concat("&title=").concat(dashboard.getTitle()==null?"":dashboard.getTitle())
                .concat("&businessService=").concat(dashboard.getConfigurationItemBusServName()==null?"":dashboard.getConfigurationItemBusServName())
                .concat("&businessApplication=").concat(dashboard.getConfigurationItemBusAppName()==null?"":dashboard.getConfigurationItemBusAppName())
                .concat("&beginDate=").concat(String.valueOf(beginDate))
                .concat("&endDate=").concat(String.valueOf(endDate)));
    }

    protected static HttpEntity getHeaders(AuditSettings auditSettings) {
        HttpHeaders headers = new HttpHeaders();
        if (!CollectionUtils.isEmpty(auditSettings.getUsernames()) && !CollectionUtils.isEmpty(auditSettings.getApiKeys())) {
            headers.set("apiUser", auditSettings.getUsernames().iterator().next());
            headers.set("Authorization", "apiToken " + auditSettings.getApiKeys().iterator().next());
        }
        return new HttpEntity<>(headers);
    }

    @SuppressWarnings("PMD.NPathComplexity")
    public static void addAuditResultByAuditType(Dashboard dashboard, Map<AuditType, Audit> auditMap, CmdbRepository cmdbRepository, long timestamp) {
        Cmdb cmdb = cmdbRepository.findByConfigurationItem(dashboard.getConfigurationItemBusServName());
        ObjectId dashboardId = dashboard.getId();
        String dashboardTitle = dashboard.getTitle();
        String ownerDept = (cmdb.getOwnerDept() == null ? "" : cmdb.getOwnerDept());
        String appService = (dashboard.getConfigurationItemBusServName() == null ? "" : dashboard.getConfigurationItemBusServName());
        String appBusApp = (dashboard.getConfigurationItemBusAppName() == null ? "" : dashboard.getConfigurationItemBusAppName());
        String appServiceOwner = (cmdb.getAppServiceOwner() == null ? "" : cmdb.getAppServiceOwner());
        String appBusAppOwner = (cmdb.getBusinessOwner() == null ? "" : cmdb.getBusinessOwner());

        Arrays.stream(AuditType.values()).forEach((AuditType auditType) -> {
            if(!(auditType.equals(AuditType.ALL) || auditType.equals(AuditType.BUILD_REVIEW) || cmdb == null)){
                Audit audit = auditMap.get(auditType);
                AuditResult auditResult = new AuditResult(dashboardId, dashboardTitle, ownerDept, appService, appBusApp, appServiceOwner, appBusAppOwner,
                        auditType.name(), audit.getDataStatus().name(), audit.getAuditStatus().name(), String.join(",", audit.getAuditStatusCodes()),
                        String.join(",", audit.getUrl()), timestamp);
                auditResults.add(auditResult);
        }
    });
    }

    protected static List<AuditResult> getAuditResults(){
        return auditResults;
    }

}
