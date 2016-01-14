package com.capitalone.dashboard.service;

import com.capitalone.dashboard.model.*;
import com.capitalone.dashboard.repository.DashboardRepository;
import com.capitalone.dashboard.repository.ServiceRepository;
import org.bson.types.ObjectId;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatcher;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ServiceServiceTest {

    @Mock DashboardRepository dashboardRepository;
    @Mock ServiceRepository serviceRepository;
    @InjectMocks ServiceServiceImpl serviceService;

    @Test
    public void all() {
        serviceService.all();
        verify(serviceRepository).findAll();
    }

    @Test
    public void dashboardServices() {
        ObjectId id = ObjectId.get();
        serviceService.dashboardServices(id);
        verify(serviceRepository).findByDashboardId(id);
    }

    @Test
    public void dashboardDependentServices() {
        ObjectId id = ObjectId.get();
        serviceService.dashboardDependentServices(id);
        verify(serviceRepository).findByDependedBy(id);
    }

    @Test
    public void get() {
        ObjectId id = ObjectId.get();
        serviceService.get(id);
        verify(serviceRepository).findOne(id);
    }

    @Test
    public void create() {
        final ObjectId id = ObjectId.get();
        final String name = "service";
        final Dashboard dashboard = new Dashboard("template", "title", new Application("app"), "amit", DashboardType.Team);
        when(dashboardRepository.findOne(id)).thenReturn(dashboard);

        serviceService.create(id, name);

        verify(serviceRepository).save(argThat(new ArgumentMatcher<Service>() {
            @Override
            public boolean matches(Object o) {
                Service service = (Service) o;
                return service.getName().equals(name) &&
                        service.getDashboardId().equals(id) &&
                        service.getStatus().equals(ServiceStatus.Ok) &&
                        service.getApplicationName().equals(dashboard.getApplication().getName());
            }
        }));
    }

    @Test
    public void update() {
        ObjectId dashId = ObjectId.get();
        Service service = new Service();
        service.setDashboardId(dashId);
        service.setLastUpdated(0l);

        serviceService.update(dashId, service);

        verify(serviceRepository).save(argThat(new ArgumentMatcher<Service>() {

            @Override
            public boolean matches(Object o) {
                return ((Service) o).getLastUpdated() > 0;
            }
        }));
    }

    @Test
    public void delete() {
        ObjectId dashId = ObjectId.get();
        ObjectId serviceId = ObjectId.get();
        Service service = new Service();
        service.setDashboardId(dashId);
        when(serviceRepository.findOne(serviceId)).thenReturn(service);

        serviceService.delete(dashId, serviceId);

        verify(serviceRepository).delete(service);
    }

    @Test
    public void addDependentService() {
        final ObjectId dashId = ObjectId.get();
        ObjectId serviceId = ObjectId.get();
        Service service = new Service();
        service.setDashboardId(ObjectId.get());
        when(serviceRepository.findOne(serviceId)).thenReturn(service);

        serviceService.addDependentService(dashId, serviceId);

        verify(serviceRepository).save(argThat(new ArgumentMatcher<Service>() {
            @Override
            public boolean matches(Object o) {
                Service service = (Service) o;
                return service.getDependedBy().contains(dashId);
            }
        }));
    }

    @Test
    public void deleteDependentService() {
        final ObjectId dashId = ObjectId.get();
        ObjectId serviceId = ObjectId.get();
        Service service = new Service();
        service.setDashboardId(dashId);
        when(serviceRepository.findOne(serviceId)).thenReturn(service);

        serviceService.deleteDependentService(dashId, serviceId);

        verify(serviceRepository).save(argThat(new ArgumentMatcher<Service>() {
            @Override
            public boolean matches(Object o) {
                Service service = (Service) o;
                return !service.getDependedBy().contains(dashId);
            }
        }));
    }

}
