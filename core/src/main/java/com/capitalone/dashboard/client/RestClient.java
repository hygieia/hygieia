package com.capitalone.dashboard.client;

import com.capitalone.dashboard.util.Encryption;
import com.capitalone.dashboard.util.EncryptionException;
import com.capitalone.dashboard.util.Supplier;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
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
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestOperations;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Component
public class RestClient {
    private static final Log LOG = LogFactory.getLog(RestClient.class);
    private final RestOperations restOperations;

    @Autowired
    public RestClient(Supplier<RestOperations> restOperationsSupplier) {
        this.restOperations = restOperationsSupplier.get();
    }

    public ResponseEntity<String> makeRestCallPost(String url, JSONObject body) {
        if (restOperations == null) { return null; }

        return restOperations.exchange(url, HttpMethod.POST, new HttpEntity<Object>(body, null), String.class);
    }

    public ResponseEntity<String> makeRestCallPost(String url, String headerKey, String token, JSONObject body) {
        if (restOperations == null) { return null; }

        if (StringUtils.isEmpty(headerKey) || StringUtils.isEmpty(token)) {
            return makeRestCallPost(url, body);
        }
        return restOperations.exchange(url, HttpMethod.POST, new HttpEntity<Object>(body, createHeaders(headerKey, token)), String.class);
    }

    public ResponseEntity<String> makeRestCallPost(String url, RestUserInfo userInfo, JSONObject body) {
        if (restOperations == null) { return null; }

        if ((userInfo == null)) {
            return makeRestCallPost(url, body);
        }
        return restOperations.exchange(url, HttpMethod.POST, new HttpEntity<Object>(body, createHeaders(userInfo.getFormattedString())), String.class);

    }

    public ResponseEntity<String> makeRestCallGet(String url) throws RestClientException {
        if (restOperations == null) { return null; }

        return restOperations.exchange(url, HttpMethod.GET, null, String.class);
    }

    public ResponseEntity<String> makeRestCallGet(String url, String headerKey, String token) throws RestClientException {
        if (restOperations == null) { return null; }

        if (StringUtils.isEmpty(headerKey) || StringUtils.isEmpty(token)) {
            return makeRestCallGet(url);
        }
        return restOperations.exchange(url, HttpMethod.GET, new HttpEntity<>(createHeaders(headerKey, token)), String.class);
    }

    public ResponseEntity<String> makeRestCallGet(String url, RestUserInfo userInfo) throws RestClientException {
        if (restOperations == null) { return null; }

        if ((userInfo == null) || StringUtils.isEmpty(userInfo.getFormattedString())) {
            return makeRestCallGet(url);
        }
        return restOperations.exchange(url, HttpMethod.GET, new HttpEntity<>(createHeaders(userInfo.getFormattedString())), String.class);
    }

    protected HttpHeaders createHeaders(RestUserInfo restUserInfo) {
        return createHeaders(restUserInfo.getFormattedString());
    }

    protected HttpHeaders createHeaders(String user) {
        byte[] encodedAuth = Base64.encodeBase64(user.getBytes(StandardCharsets.US_ASCII));
        String authHeader = "Basic " + new String(encodedAuth);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", authHeader);
        return headers;
    }

    protected HttpHeaders createHeaders(String key, String token) {
        String authHeader = key.trim() + " " + token.trim();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", authHeader);
        return headers;
    }

    public JSONObject parseAsObject(ResponseEntity<String> response) throws ParseException {
        if (response == null) { return new JSONObject(); }

        return (JSONObject) new JSONParser().parse(response.getBody());
    }

    public JSONArray parseAsArray(ResponseEntity<String> response) throws ParseException {
        return (JSONArray) new JSONParser().parse(response.getBody());
    }

    public JSONArray getArray(JSONObject json, String key) {
        if (json == null) return new JSONArray();
        if (json.get(key) == null) return new JSONArray();
        return (JSONArray) json.get(key);
    }

    public String getString(Object obj, String key) {
        if (obj == null) return "";

        if (obj instanceof Map) {
            Map map = (Map) obj;
            Object value = map.get(key);
            return (value == null) ? "" : value.toString();
        } else if (obj instanceof JSONObject) {
            JSONObject json = (JSONObject) obj;
            Object value = json.get(key);
            return (value == null) ? "" : value.toString();
        }
        return "";
    }

    public Integer getInteger(Object obj, String key) throws NumberFormatException{
        return NumberUtils.toInt(getString(obj, key));
    }

    public Object getAsObject(Object obj, String key) {
        if (obj == null) return null;

        if (obj instanceof Map) {
            Map map = (Map) obj;
            return map.get(key);
        } else if (obj instanceof JSONObject) {
            JSONObject json = (JSONObject) obj;
            return json.get(key);
        }

        return null;
    }

    public boolean getBoolean(Object obj, String key) {
        if (obj == null) return false;

        if (obj instanceof Map) {
            Map map = (Map) obj;
            return (Boolean) map.get(key);
        } else if (obj instanceof JSONObject) {
            JSONObject json = (JSONObject) obj;
            return (Boolean) json.get(key);
        }

        return false;
    }

    public Long getLong(Object obj, String key) throws NumberFormatException{
        return NumberUtils.toLong(getString(obj, key));
    }

    /**
     * Decrypt string
     *
     * @param string
     * @param key
     * @return String
     */
    public static String decryptString(String string, String key) {
        if (StringUtils.isEmpty(string)) { return ""; }

        String result = "";
        try {
            result = Encryption.decryptString(string, key);
        } catch (EncryptionException e) {
            LOG.error(e.getMessage());
        }
        return result;
    }
}
