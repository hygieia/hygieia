package com.capitalone.dashboard.model;

import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Data
@Document(collection = "requests")
public class RequestLog extends BaseModel {
    private String client;
    private String endpoint;
    private String method;
    private String parameter;
    private long requestSize;
    private String requestContentType;
    private Object requestBody;
    private long responseSize;
    private String responseContentType;
    private Object responseBody;
    private int responseCode;
    private long timestamp;

    public String toString() {
        return "REST Request - " + "[" + this.method + "] [PARAMETERS:" + parameter + "] [BODY:" + requestBody + "] [REMOTE:" + client + "] [STATUS:" + responseCode + "]";
    }
}
