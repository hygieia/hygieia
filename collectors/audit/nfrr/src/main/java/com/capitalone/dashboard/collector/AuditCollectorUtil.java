package com.capitalone.dashboard.collector;

import com.capitalone.dashboard.model.Audit;
import com.capitalone.dashboard.model.AuditStatus;
import com.capitalone.dashboard.model.DataStatus;
import com.capitalone.dashboard.model.AuditType;
import com.capitalone.dashboard.model.AuditResult;
import com.capitalone.dashboard.model.Dashboard;
import com.capitalone.dashboard.model.Cmdb;
import com.capitalone.dashboard.model.CollectorItem;
import com.capitalone.dashboard.model.Collector;
import com.capitalone.dashboard.model.CollectorType;
import com.capitalone.dashboard.model.Component;

import com.capitalone.dashboard.repository.AuditResultRepository;
import com.capitalone.dashboard.repository.CollectorItemRepository;
import com.capitalone.dashboard.repository.ComponentRepository;


import com.capitalone.dashboard.status.CodeReviewAuditStatus;
import com.capitalone.dashboard.status.DashboardAuditStatus;
import com.capitalone.dashboard.status.CodeQualityAuditStatus;
import com.capitalone.dashboard.status.PerformanceTestAuditStatus;
import com.capitalone.dashboard.status.LibraryPolicyAuditStatus;
import com.capitalone.dashboard.status.TestResultAuditStatus;

import org.apache.commons.lang3.math.NumberUtils;
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
import java.util.HashMap;
import java.util.Map;
import java.util.Arrays;
import java.util.Optional;
import java.util.StringJoiner;

import java.util.function.Supplier;
import java.util.stream.Stream;

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
    private static final String STR_FEATURE_TEST_RESULT = "featureTestResult";
    private static final String STR_PERCENTAGE = "percentage";
    private static final String STR_MANUAL = "Manual";
    private static final String STR_AUTOMATED = "Automated";
    private static final String STR_FUNCTIONAL = "Functional";
    private static final String STR_TYPE = "type";
    private static final String SUCCESS_COUNT = "successCount";
    private static final String FAILURE_COUNT = "failureCount";
    private static final String SKIP_COUNT = "skippedCount";
    private static final String TOTAL_COUNT = "totalCount";
    private static final String OPT_DBRD_ID = "dashboardId";

    private Dashboard dashboard;
    static List<CollectorItem> auditCollectorItems = new ArrayList<>();

    private CollectorItemRepository collectorItemRepository;
    private ComponentRepository componentRepository;
    private Collector collector;

    public AuditCollectorUtil(Collector collector, ComponentRepository componentRepository,
                              CollectorItemRepository collectorItemRepository){
        this.collector = collector;
        this.componentRepository = componentRepository;
        this.collectorItemRepository = collectorItemRepository;
    }

    /**
     * Get Code Review Audit Results
     */
    private Audit getCodeReviewAudit(JSONArray jsonArray, JSONArray global) {

        LOGGER.info("NFRR Audit Collector auditing CODE_REVIEW");
        Audit audit = new Audit();
        audit.setType(AuditType.CODE_REVIEW);

        CollectorItem collectorItem = createCollectorItem(audit.getType());
        audit.setCollectorItem(collectorItem);
        auditCollectorItems.add(collectorItem);
        Audit basicAudit;
        if ((basicAudit = doBasicAuditCheck(jsonArray, global, AuditType.CODE_REVIEW)) != null) {
            basicAudit.setCollectorItem(collectorItem);
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
                    audit.getAuditStatusCodes().add(status);
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
    private Audit doBasicAuditCheck(JSONArray jsonArray, JSONArray global, AuditType auditType) {
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
    private boolean isCollectorError(JSONArray jsonArray) {
        Stream<JSONObject> jsonObjectStream = jsonArray.stream().map((Object object) -> (JSONObject) object);
        Stream<JSONArray> auditStatusArray = jsonObjectStream.map(jsonObject -> (JSONArray) jsonObject.get(STR_AUDITSTATUSES));
        return auditStatusArray.anyMatch(aSArray -> aSArray.toJSONString().contains(DashboardAuditStatus.COLLECTOR_ITEM_ERROR.name()));
    }

    /**
     * Check for dashboard audit type configuration
     */
    @SuppressWarnings("PMD.NPathComplexity")
    private boolean isConfigured(AuditType auditType, JSONArray jsonArray) {
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
    private Audit getCodeQualityAudit(JSONArray jsonArray, JSONArray global) {

        LOGGER.info("NFRR Audit Collector auditing CODE_QUALITY");
        Audit audit = new Audit();
        audit.setType(AuditType.CODE_QUALITY);
        CollectorItem collectorItem = createCollectorItem(audit.getType());
        audit.setCollectorItem(collectorItem);
        auditCollectorItems.add(collectorItem);

        Audit basicAudit;
        if ((basicAudit = doBasicAuditCheck(jsonArray, global, AuditType.CODE_QUALITY)) != null) {
            basicAudit.setCollectorItem(collectorItem);
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
    private Audit getSecurityAudit(JSONArray jsonArray, JSONArray global) {

        LOGGER.info("NFRR Audit Collector auditing STATIC_SECURITY_ANALYSIS");
        Audit audit = new Audit();
        audit.setType(AuditType.STATIC_SECURITY_ANALYSIS);
        CollectorItem collectorItem = createCollectorItem(audit.getType());
        audit.setCollectorItem(collectorItem);
        auditCollectorItems.add(collectorItem);

        Audit basicAudit;
        if ((basicAudit = doBasicAuditCheck(jsonArray, global, AuditType.STATIC_SECURITY_ANALYSIS)) != null) {
            basicAudit.setCollectorItem(collectorItem);
            return basicAudit;
        }
        Set<String> auditStatuses;
        for (Object o : jsonArray) {
            JSONArray auditJO = (JSONArray) ((JSONObject) o).get(STR_AUDITSTATUSES);
            Optional<Object> urlOptObj = Optional.ofNullable(((JSONObject) o).get(STR_URL));
            urlOptObj.ifPresent(urlObj -> audit.getUrl().add(urlOptObj.get().toString()));
            auditJO.stream().forEach(status -> audit.getAuditStatusCodes().add((String) status));
        }
        auditStatuses = audit.getAuditStatusCodes();
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
    private Audit getOSSAudit(JSONArray jsonArray, JSONArray global) {

        LOGGER.info("NFRR Audit Collector auditing LIBRARY_POLICY");
        Audit audit = new Audit();
        audit.setType(AuditType.LIBRARY_POLICY);
        CollectorItem collectorItem = createCollectorItem(audit.getType());
        audit.setCollectorItem(collectorItem);
        auditCollectorItems.add(collectorItem);

        Audit basicAudit;
        if ((basicAudit = doBasicAuditCheck(jsonArray, global, AuditType.LIBRARY_POLICY)) != null) {
            basicAudit.setCollectorItem(collectorItem);
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
                audit.getAuditStatusCodes().add(status);
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
    protected Audit getTestAudit(JSONArray jsonArray, JSONArray global) {

        LOGGER.info("NFRR Audit Collector auditing TEST_RESULT");
        Audit audit = new Audit();
        audit.setType(AuditType.TEST_RESULT);
        CollectorItem collectorItem = createCollectorItem(audit.getType());
        audit.setCollectorItem(collectorItem);
        auditCollectorItems.add(collectorItem);

        Audit basicAudit;
        if ((basicAudit = doBasicAuditCheck(jsonArray, global, AuditType.TEST_RESULT)) != null) {
            basicAudit.setCollectorItem(collectorItem);
            return basicAudit;
        }
        audit.setAuditStatus(AuditStatus.OK);
        audit.setDataStatus(DataStatus.OK);
        Set<String> auditStatuses;
        for (Object o : jsonArray) {
            JSONArray auditJO = (JSONArray) ((JSONObject) o).get(STR_AUDITSTATUSES);
            Optional<Object> urlOptObj = Optional.ofNullable(((JSONObject) o).get(STR_URL));
            urlOptObj.ifPresent(urlObj -> audit.getUrl().add(urlOptObj.get().toString()));
            auditJO.stream().forEach(status -> audit.getAuditStatusCodes().add((String) status));
        }
        audit.setOptions(getTestAuditOptions(jsonArray));
        auditStatuses = audit.getAuditStatusCodes();
        if (auditStatuses.contains(TestResultAuditStatus.TEST_RESULT_AUDIT_FAIL.name())){
            audit.setAuditStatus(AuditStatus.FAIL);
            audit.setDataStatus(DataStatus.OK);
        } else if (auditStatuses.contains(TestResultAuditStatus.TEST_RESULT_AUDIT_OK.name())) {
            audit.setAuditStatus(AuditStatus.OK);
            audit.setDataStatus(DataStatus.OK);
        }else{
            audit.setAuditStatus(AuditStatus.NA);
            audit.setDataStatus(DataStatus.NO_DATA);
        }
        return audit;
    }


    /**
     * Get performance testing audit results
     */
    private Audit getPerfAudit(JSONArray jsonArray, JSONArray global) {

        LOGGER.info("NFRR Audit Collector auditing PERF_TEST");
        Audit audit = new Audit();
        audit.setType(AuditType.PERF_TEST);
        CollectorItem collectorItem = createCollectorItem(audit.getType());
        audit.setCollectorItem(collectorItem);
        auditCollectorItems.add(collectorItem);

        Audit basicAudit;
        if ((basicAudit = doBasicAuditCheck(jsonArray, global, AuditType.PERF_TEST)) != null) {
            basicAudit.setCollectorItem(collectorItem);
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
                audit.getAuditStatusCodes().add(status);
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
    public Map<AuditType, Audit> getAudit(Dashboard dashboard, AuditSettings settings, long begin, long end) {
        Map<AuditType, Audit> audits = new HashMap<>();
        setDashboard(dashboard);

        String url = getAuditAPIUrl(dashboard, settings, begin, end);
        JSONObject auditResponseObj = parseObject(url, settings);
        if(auditResponseObj == null){
            return audits;
        }
        JSONArray globalStatus = (JSONArray) auditResponseObj.get(STR_AUDITSTATUSES);
        JSONObject review = (JSONObject) auditResponseObj.get(STR_REVIEW);

        auditCollectorItems.clear();
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

        updateComponent(dashboard);
        return audits;
    }

    /**
     * Update component with audit collector items
     * @param dashboard
     */
    private void updateComponent(Dashboard dashboard)  {
        List<Component> components = dashboard.getApplication().getComponents();
        if(components.iterator().hasNext()){
            Component component = componentRepository.findOne(components.iterator().next().getId());
            Map<CollectorType, List<CollectorItem>> collectorItems = component.getCollectorItems();
            collectorItems.put(CollectorType.Audit, auditCollectorItems);
            component.setCollectorItems(collectorItems);
            componentRepository.save(component);
        }
    }

    /**
     * Make audit api rest call and parse response
     */
    protected JSONObject parseObject(String url, AuditSettings settings){
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
    protected String getAuditAPIUrl(Dashboard dashboard, AuditSettings settings, long beginDate, long endDate) {
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
    protected HttpEntity getHeaders(AuditSettings auditSettings) {
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
    public static void addAuditResultByAuditType(Dashboard dashboard, Map<AuditType, Audit> auditMap, Cmdb cmdb, long timestamp) {

        if(CollectionUtils.isEmpty(auditMap)){ return; }
        ObjectId dashboardId = dashboard.getId();
        String dashboardTitle = dashboard.getTitle();
        String ownerDept = ((cmdb == null || cmdb.getOwnerDept() == null) ? "" : cmdb.getOwnerDept());
        String appService = (dashboard.getConfigurationItemBusServName() == null ? "" : dashboard.getConfigurationItemBusServName());
        String appBusApp = (dashboard.getConfigurationItemBusAppName() == null ? "" : dashboard.getConfigurationItemBusAppName());
        String appServiceOwner = ((cmdb == null || cmdb.getAppServiceOwner() == null) ? "" : cmdb.getAppServiceOwner());
        String appBusAppOwner = ((cmdb == null || cmdb.getBusinessOwner() == null) ? "" : cmdb.getBusinessOwner());

        Arrays.stream(AuditType.values()).forEach((AuditType auditType) -> {
            if (!(auditType.equals(AuditType.ALL) || auditType.equals(AuditType.BUILD_REVIEW) || auditType.equals(AuditType.ARTIFACT))) {
                Audit audit = auditMap.get(auditType);
                if (audit != null) {
                    AuditResult auditResult = new AuditResult(dashboardId, dashboardTitle, ownerDept, appService, appBusApp, appServiceOwner, appBusAppOwner,
                            auditType, audit.getDataStatus().name(), audit.getAuditStatus().name(), String.join(",", audit.getAuditStatusCodes()),
                            String.join(",", audit.getUrl()), timestamp);
                    auditResult.setCollectorItemId(audit.getCollectorItem().getId());
                    auditResult.setOptions(audit.getOptions() != null ? audit.getOptions() : null);
                    auditResults.add(auditResult);
                }
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

    /**
     * Get test audit only optional data
     * @return
     * @param jsonArray
     */
    public Map<String,Object> getTestAuditOptions(JSONArray jsonArray) {
        Map<String, Object> options = new HashMap<>();

        Supplier<Stream> manualTestStream = () -> jsonArray.stream()
                .filter(jObj-> Optional.ofNullable(((JSONObject)jObj).get(STR_TYPE)).orElse("").toString().equalsIgnoreCase(STR_MANUAL));
        Supplier<Stream>  automatedTestStream = () -> jsonArray.stream()
                .filter(jObj-> Optional.ofNullable(((JSONObject)jObj).get(STR_TYPE)).orElse("").toString().equalsIgnoreCase(STR_FUNCTIONAL));

        Map<String, Double> traceability = new HashMap<>();
        traceability.put(STR_AUTOMATED, getAvgTracePercent(automatedTestStream.get()));
        traceability.put(STR_MANUAL, getAvgTracePercent(manualTestStream.get()));
        options.put(STR_TRACEABILITY, traceability);

        Map<String, Map> featureTestResult = new HashMap<>();
        Map featureAutoTestResultMap = new HashMap();
        Map featureManualTestResultMap = new HashMap();

        featureAutoTestResultMap.put(SUCCESS_COUNT, getFeatureTestCount(SUCCESS_COUNT, automatedTestStream.get()));
        featureAutoTestResultMap.put(FAILURE_COUNT, getFeatureTestCount(FAILURE_COUNT, automatedTestStream.get()));
        featureAutoTestResultMap.put(SKIP_COUNT, getFeatureTestCount(SKIP_COUNT, automatedTestStream.get()));
        featureAutoTestResultMap.put(TOTAL_COUNT, getFeatureTestCount(TOTAL_COUNT, automatedTestStream.get()));

        featureManualTestResultMap.put(SUCCESS_COUNT, getFeatureTestCount(SUCCESS_COUNT, manualTestStream.get()));
        featureManualTestResultMap.put(FAILURE_COUNT, getFeatureTestCount(FAILURE_COUNT, manualTestStream.get()));
        featureManualTestResultMap.put(SKIP_COUNT, getFeatureTestCount(SKIP_COUNT, manualTestStream.get()));
        featureManualTestResultMap.put(TOTAL_COUNT, getFeatureTestCount(TOTAL_COUNT, manualTestStream.get()));

        featureTestResult.put(STR_AUTOMATED, featureAutoTestResultMap);
        featureTestResult.put(STR_MANUAL, featureManualTestResultMap);
        options.put(STR_FEATURE_TEST_RESULT, featureTestResult);
        return options;
    }

    private Integer getFeatureTestCount(String countType, Stream stream) {

        return stream
                .map(jObj -> Optional.ofNullable(((JSONObject)jObj).get(STR_FEATURE_TEST_RESULT)).orElse(new Object()))
                .map(featureTestResult -> Optional.ofNullable(((JSONObject)featureTestResult).get(countType)).orElse(NumberUtils.INTEGER_ZERO))
                .mapToInt(n -> Integer.valueOf(n.toString())).sum();
    }

    private Double getAvgTracePercent(Stream stream) {
        return stream
                .map(jObj -> Optional.ofNullable(((JSONObject)jObj).get(STR_TRACEABILITY)).orElse(new Object()))
                .map(traceability -> Optional.ofNullable(((JSONObject)traceability).get(STR_PERCENTAGE)).orElse(NumberUtils.DOUBLE_ZERO))
                .mapToDouble(s-> Double.valueOf(s.toString())).average().orElse(NumberUtils.DOUBLE_ZERO);
    }

    /**
     * Create collector item for audit type if not exists already
     * @param auditType
     * @return
     */
    protected CollectorItem createCollectorItem(AuditType auditType){
        String description = getDescription(auditType);
        Iterable<CollectorItem> collectorItems = collectorItemRepository.findByDescription(description);
        Optional<CollectorItem> optCollectorItem = Optional.ofNullable(collectorItems.iterator().hasNext() ? collectorItems.iterator().next() : null);
        optCollectorItem.ifPresent(collectorItem -> collectorItem.setLastUpdated(System.currentTimeMillis()));
        optCollectorItem = Optional.ofNullable(optCollectorItem.orElseGet(() -> {
            CollectorItem collectorItem = new CollectorItem();
            collectorItem.setId(ObjectId.get());
            collectorItem.setCollectorId(this.collector.getId());
            collectorItem.setCollector(this.collector);
            collectorItem.setEnabled(true);
            collectorItem.setPushed(false);
            collectorItem.setLastUpdated(System.currentTimeMillis());
            collectorItem.setDescription(description);
            collectorItem.getOptions().put(OPT_DBRD_ID, getDashboard().getId());
            return collectorItem;
        }));
        return collectorItemRepository.save(optCollectorItem.get());
    }

    public void setDashboard(Dashboard dashboard) {
        this.dashboard = dashboard;
    }

    public Dashboard getDashboard() {
        return dashboard;
    }

    /**
     * Get description for collector item
     * @param auditType
     * @return
     */
    public String getDescription(AuditType auditType) {
        StringJoiner description = new StringJoiner(" ");
        Optional<Dashboard> dashboardOpt = Optional.ofNullable(this.getDashboard());
        description.add(dashboardOpt.isPresent() ? dashboardOpt.get().getTitle() : "title");
        description.add(auditType.name().toLowerCase());
        description.add("audit process");
        return description.toString();
    }
}