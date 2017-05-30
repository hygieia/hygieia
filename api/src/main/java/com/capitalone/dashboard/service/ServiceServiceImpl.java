package com.capitalone.dashboard.service;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.capitalone.dashboard.model.Dashboard;
import com.capitalone.dashboard.model.Service;
import com.capitalone.dashboard.model.ServiceStatus;
import com.capitalone.dashboard.model.monitor.MonitorService;
import com.capitalone.dashboard.repository.DashboardRepository;
import com.capitalone.dashboard.repository.ServiceRepository;
import com.capitalone.dashboard.util.URLConnectionFactory;


@org.springframework.stereotype.Service
public class ServiceServiceImpl implements ServiceService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceServiceImpl.class);
    private final URLConnectionFactory urlConnectionFactory;
    private final ServiceRepository serviceRepository;
    private final DashboardRepository dashboardRepository;

    @Autowired
    public ServiceServiceImpl(URLConnectionFactory urlConnectionFactory, ServiceRepository serviceRepository, DashboardRepository dashboardRepository) {
        this.urlConnectionFactory = urlConnectionFactory;
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
        service.setStatus(getServiceStatus(service.getUrl(), dashboardId));
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
        service.setDashboardId(dashboardId);
        service.setStatus(getServiceStatus(service.getUrl(), dashboardId));
        service.setLastUpdated(System.currentTimeMillis());
        Dashboard dashboard = dashboardRepository.findOne(dashboardId);
        service.setApplicationName(dashboard.getApplication().getName());
        serviceRepository.save(service);
    }

    private ServiceStatus getServiceStatus(String url, ObjectId dashboardId) {
    	URLConnection connection = getUrlConnection(url);
    	
    	if(connection == null) {
    		return ServiceStatus.Alert;
    	}
    	
    	return new MonitorService((HttpURLConnection) connection, dashboardId).getServiceStatus();
	}

	private HttpURLConnection getUrlConnection(String url) {
    	try {
			return urlConnectionFactory.get(new URL(url));
		} catch (IOException e) {
			LOGGER.error(e.getMessage());
		}
    	
    	return null;
	}
    
}
