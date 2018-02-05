package com.capitalone.dashboard.testutil;

import com.capitalone.dashboard.collector.RestOperationsSupplier;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.RestOperations;

import java.util.HashMap;
import java.util.Map;

public class TestRestOperations<T> extends RestOperationsSupplier {
    TestRestTemplate<? extends T> template;

    private Map<String, TestResponse<T>> response;

    public TestRestOperations(Map<String, TestResponse<T>> response) {
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
        if (template != null) {
            template.addResponse(key, new TestResponse<>(body, httpStatus));
        }
    }

    public TestRestTemplate<? extends T> getTemplate() {
        return template;
    }

    public void setTemplate(TestRestTemplate<? extends T> template) {
        this.template = template;
    }

    @Override
    public RestOperations get() {
        if (template == null) {
            template = new TestRestTemplate<>(response);
        }
        return template;
    }
}
