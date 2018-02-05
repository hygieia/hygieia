package com.capitalone.dashboard.testutil;

import org.springframework.http.HttpStatus;

public class TestResponse<T> {
    private T body;
    private HttpStatus status;

    public TestResponse(T body, HttpStatus status) {
        this.body = body;
        this.status = status;
    }

    public T getBody() {
        return body;
    }

    public void setBody(T body) {
        this.body = body;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public void setStatus(HttpStatus status) {
        this.status = status;
    }
}
