package com.capitalone.dashboard.service;

import com.capitalone.dashboard.model.Dashboard;
import com.capitalone.dashboard.model.Service;
import com.capitalone.dashboard.model.ServiceStatus;
import com.capitalone.dashboard.repository.DashboardRepository;
import com.capitalone.dashboard.repository.ServiceRepository;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.List;


@org.springframework.stereotype.Service
public class ServiceServiceImpl implements ServiceService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceServiceImpl.class);
    private final ServiceRepository serviceRepository;
    private final DashboardRepository dashboardRepository;

    @Autowired
    public ServiceServiceImpl(ServiceRepository serviceRepository, DashboardRepository dashboardRepository) {
        this.serviceRepository = serviceRepository;
        this.dashboardRepository = dashboardRepository;
    }

    @Override
    public Iterable<Service> all() {
        return serviceRepository.findAll();
    }

    @Override
    public List<Service> dashboardServices(ObjectId dashboardId) {
        return serviceRepository.findByDashboardId(dashboardId);
    }

    @Override
    public List<Service> dashboardDependentServices(ObjectId dashboardId) {
        return serviceRepository.findByDependedBy(dashboardId);
    }

    @Override
    public Service get(ObjectId id) {
        return serviceRepository.findOne(id);
    }

    @Override
    public Service create(ObjectId dashboardId, String name, String url) {
        Service service = new Service();
        service.setName(name);
        service.setUrl(url);
        service.setDashboardId(dashboardId);
        service.setStatus(ServiceStatus.Warning);
        service.setLastUpdated(System.currentTimeMillis());
        Dashboard dashboard = dashboardRepository.findOne(dashboardId);
        service.setApplicationName(dashboard.getApplication().getName());
        return serviceRepository.save(service);
    }

    @Override
    public Service update(ObjectId dashboardId, Service service) {
        if (!service.getDashboardId().equals(dashboardId)) {
            throw new IllegalStateException("Not allowed to update this service from this dashboard!");
        }
        int code = getHttpCode(service.getUrl());
        ServiceStatus status = getServiceStatusBasedOnHttpReturnCode(code);
        service.setStatus(status);
        service.setDashboardId(dashboardId);
        service.setLastUpdated(System.currentTimeMillis());
        return serviceRepository.save(service);
    }

    @Override
    public void delete(ObjectId dashboardId, ObjectId serviceId) {
        Service service = get(serviceId);
        if (!service.getDashboardId().equals(dashboardId)) {
            throw new IllegalStateException("Not allowed to delete this service from this dashboard!");
        }
        serviceRepository.delete(service);
    }

    @Override
    public Service addDependentService(ObjectId dashboardId, ObjectId serviceId) {
        Service service = get(serviceId);
        if (service.getDashboardId().equals(dashboardId)) {
            throw new IllegalStateException("Not allowed to add service owned by dashboard to dependent service!");
        }
        service.getDependedBy().add(dashboardId);
        return serviceRepository.save(service);
    }

    @Override
    public void deleteDependentService(ObjectId dashboardId, ObjectId serviceId) {
        Service service = get(serviceId);
        service.getDependedBy().remove(dashboardId);
        serviceRepository.save(service);
    }

    @Override
    public void refreshService(ObjectId dashboardId, ObjectId serviceId) {
        Service service = get(serviceId);
        LOGGER.debug("URL is :" + service.getUrl());
        ServiceStatus status = getServiceStatusBasedOnHttpReturnCode(getHttpCode(service.getUrl()));
        service.setDashboardId(dashboardId);
        service.setStatus(status);
        service.setLastUpdated(System.currentTimeMillis());
        Dashboard dashboard = dashboardRepository.findOne(dashboardId);
        service.setApplicationName(dashboard.getApplication().getName());
        serviceRepository.save(service);
    }

    private ServiceStatus getServiceStatusBasedOnHttpReturnCode(int code) {
        ServiceStatus status = null;
        if (code == 200) {
            status = ServiceStatus.Ok;
        } else if (code >= 300 && code <= 400) {
            status = ServiceStatus.Warning;
        } else {
            status = ServiceStatus.Alert;
        }
        return status;
    }

    private int getHttpCode(String myUrl) {
        int code = 0;
        try {
            URL url = new URL(myUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.connect();
            code = connection.getResponseCode();
            LOGGER.debug("HTTP Status code for URL " + myUrl + " is :" + code);
        } catch (MalformedURLException e) {
            LOGGER.error(myUrl+" failed with "+e.getMessage());
        } catch (ProtocolException e) {
            LOGGER.error(myUrl+"failed with"+e.getMessage());
        } catch (IOException e) {
            LOGGER.error(myUrl+"failed with"+e.getMessage());
        }
        return code;
    }
}
