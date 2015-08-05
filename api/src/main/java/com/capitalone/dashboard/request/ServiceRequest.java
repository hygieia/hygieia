package com.capitalone.dashboard.request;

import com.capitalone.dashboard.model.Service;
import com.capitalone.dashboard.model.ServiceStatus;

public class ServiceRequest {
    private ServiceStatus status;
    private String message;

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

    public Service update(Service service) {
        service.setStatus(status);
        service.setMessage(message);
        return service;
    }
}
