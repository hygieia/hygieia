package com.capitalone.dashboard.collector;

import com.capitalone.dashboard.model.AppdynamicsApplication;
import com.capitalone.dashboard.model.Performance;
import com.capitalone.dashboard.model.PerformanceMetricStatus;
import com.capitalone.dashboard.util.Supplier;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestOperations;

import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.List;

@Component
public class DefaultAppdynamicsClient implements AppdynamicsClient {
    private static final Log LOG = LogFactory.getLog(DefaultAppdynamicsClient.class);
    private static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ssZ";
    private static final String STATUS_WARN = "WARN";
    private static final String STATUS_ALERT = "ALERT";

    private final RestOperations rest;
    private final HttpEntity<String> httpHeaders;
    private final AppdynamicsSettings appdynamicsSettings;

    @Autowired
    public DefaultAppdynamicsClient(Supplier<RestOperations> restOperationsSupplier, AppdynamicsSettings settings) {
        this.httpHeaders = new HttpEntity<String>(
                this.createHeaders(settings.getUsername(), settings.getPassword())
            );
        this.rest = restOperationsSupplier.get();
        this.appdynamicsSettings = settings;
    }



    private JSONArray parseAsArray(String url) throws ParseException {
        ResponseEntity<String> response = rest.exchange(url, HttpMethod.GET, this.httpHeaders, String.class);
        return (JSONArray) new JSONParser().parse(response.getBody());
    }

    private long timestamp(JSONObject json, String key) {
        Object obj = json.get(key);
        if (obj != null) {
            try {
                return new SimpleDateFormat(DATE_FORMAT).parse(obj.toString()).getTime();
            } catch (java.text.ParseException e) {
                LOG.error(obj + " is not in expected format " + DATE_FORMAT, e);
            }
        }
        return 0;
    }

    private String str(JSONObject json, String key) {
        Object obj = json.get(key);
        return obj == null ? null : obj.toString();
    }
    @SuppressWarnings("unused")
    private Integer integer(JSONObject json, String key) {
        Object obj = json.get(key);
        return obj == null ? null : (Integer) obj;
    }

    @SuppressWarnings("unused")
    private BigDecimal decimal(JSONObject json, String key) {
        Object obj = json.get(key);
        return obj == null ? null : new BigDecimal(obj.toString());
    }

    @SuppressWarnings("unused")
    private Boolean bool(JSONObject json, String key) {
        Object obj = json.get(key);
        return obj == null ? null : Boolean.valueOf(obj.toString());
    }

    private PerformanceMetricStatus metricStatus(String status) {
        if (StringUtils.isBlank(status)) {
            return PerformanceMetricStatus.Ok;
        }

        switch(status) {
            case STATUS_WARN:  return PerformanceMetricStatus.Warning;
            case STATUS_ALERT: return PerformanceMetricStatus.Alert;
            default:           return PerformanceMetricStatus.Ok;
        }
    }

    private HttpHeaders createHeaders(String username, String password){
        HttpHeaders headers = new HttpHeaders();
        if (username != null && !username.isEmpty() &&
            password != null && !password.isEmpty()) {
          String auth = username + ":" + password;
          byte[] encodedAuth = Base64.encodeBase64(
              auth.getBytes(Charset.forName("US-ASCII"))
          );
          String authHeader = "Basic " + new String(encodedAuth);
          headers.set("Authorization", authHeader);
        }
        return headers;
    }

    // TODO: Implement these using AppD rest api
    @Override
    public List<AppdynamicsApplication> getApplications(String server) {

        /**
         * user1@customer1:secret http://demo.appdynamics.com/controller/rest/applications
         *
         * <applications>
         <application>
         <id>5</id>
         <name>ECommerce_E2E</name>
         </application>
         <application>
         <id>8</id>
         <name>ECommerce_E2E-Fulfillment</name>
         </application>
         <application>
         <id>11</id>
         <name>jimix12110919</name>
         <description></description>
         </application>
         </applications>
         */
        return null;
    }

    @Override
    public Performance getPerformance(AppdynamicsApplication application) {
        /**
         * All metrics names: http://demo.appdynamics.com/controller/rest/applications/DIGITAL_RTM_PERF/metrics?output=json
         *
         *
         * Get all metrics for a given app:
         * http://demo.appdynamics.com/controller/rest/applications/DIGITAL_RTM_PERF/metric-data?metric-path=*|*|*|*|*&time-range-type=BEFORE_NOW&duration-in-mins=15&output=json
         *
         * UI rest api (sort of backdoor apis)
         *
         * Get overall performance stats
         * http://demo.appdynamics.com/controller/restui/bt/performanceRequestStats?applicationId=996&entityType=APPLICATION&entityId=996&time-range=last_15_minutes.BEFORE_NOW.-1.-1.15
         *
         * Get Application health:
         * http://demo.appdynamics.com/controller/restui/applicationManagerUiBean/applicationHealthSummary/996?time-range=last_15_minutes.BEFORE_NOW.-1.-1.15
         *
         */
        return null;
    }
}
