package com.capitalone.dashboard.testutil;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

public class TestRestTemplate<T> extends RestTemplate {

    private Map<String, TestResponse<T>> response;

    public TestRestTemplate(Map<String, TestResponse<T>> response) {
        this.response = response;
    }

    public Map<String, TestResponse<T>> getResponse() {
        return response;
    }


    public void addResponse(String key, TestResponse testResponse) {
        if (response == null) {
            response = new HashMap<>();
        }
        this.response.put(key, testResponse);
    }

    public void addResponse(String key, T body, HttpStatus httpStatus) {
        if (response == null) {
            response = new HashMap<>();
        }
        this.response.put(key, new TestResponse<>(body, httpStatus));
    }

    public void clearResponse() {
        if (response != null) {
            response.clear();
        }
    }
    @Override
    public <T> ResponseEntity<T> exchange(String var1, HttpMethod var2, HttpEntity<?> var3, Class<T> var4, Object... var5) throws RestClientException {

        if (response.containsKey(var1)) {
            return new ResponseEntity(response.get(var1).getBody(),response.get(var1).getStatus());
        } else {
            return new ResponseEntity<>(HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }
}
