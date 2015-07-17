package com.capitalone.dashboard.service;

import com.capitalone.dashboard.model.Dashboard;
import com.capitalone.dashboard.model.Service;
import com.capitalone.dashboard.model.ServiceStatus;
import com.capitalone.dashboard.repository.DashboardRepository;
import com.capitalone.dashboard.repository.ServiceRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;


@org.springframework.stereotype.Service
public class ServiceServiceImpl implements ServiceService {
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
    public Service create(ObjectId dashboardId, String name) {
        Service service = new Service();
        service.setName(name);
        service.setDashboardId(dashboardId);
        service.setStatus(ServiceStatus.Ok);
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
}
