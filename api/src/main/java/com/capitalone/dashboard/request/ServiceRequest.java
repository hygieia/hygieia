package com.capitalone.dashboard.request;

import com.capitalone.dashboard.model.Service;
import com.capitalone.dashboard.model.ServiceStatus;

public class ServiceRequest {


    private ServiceStatus status;
    private String message;
    private String url;
    private String name;


    public ServiceStatus getStatus() {
        return status;
    }

    public void setStatus(ServiceStatus status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Service update(Service service) {
        service.setStatus(status);
        service.setMessage(message);
        service.setUrl(url);
        service.setName(name);
        return service;
    }

    public Service refresh(Service service) {
        service.setStatus(status);
        return service;
    }
}
