package com.capitalone.dashboard.service;

import com.capitalone.dashboard.model.Collector;
import com.capitalone.dashboard.model.DataResponse;
import com.capitalone.dashboard.model.GitRequest;
import com.capitalone.dashboard.model.Component;
import com.capitalone.dashboard.model.CollectorItem;
import com.capitalone.dashboard.model.CollectorType;
import com.capitalone.dashboard.repository.CollectorRepository;
import com.capitalone.dashboard.repository.ComponentRepository;
import com.capitalone.dashboard.repository.GitRequestRepository;
import com.capitalone.dashboard.request.GitRequestRequest;
import com.mysema.query.types.Predicate;
import org.bson.types.ObjectId;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Collections;
import java.util.List;

import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class GitRequestServiceTest {
    @Mock
    private ComponentRepository componentRepository;
    @Mock
    private CollectorRepository collectorRepository;
    @Mock
    private GitRequestRepository gitRequestRepository;
    @InjectMocks
    private GitRequestServiceImpl gitRequestService;

    @Test
    public void searchTest() {
        ObjectId componentId = ObjectId.get();
        ObjectId collectorItemId = ObjectId.get();
        ObjectId collectorId = ObjectId.get();

        Collector collector = new Collector();
        collector.setId(collectorId);

        GitRequestRequest request = new GitRequestRequest();
        request.setComponentId(componentId);

        when(componentRepository.findOne(request.getComponentId())).thenReturn(makeComponent(collectorItemId, collectorId, true));
        when(collectorRepository.findOne(collectorId)).thenReturn(collector);

        gitRequestService.search(request,"pull", "all");

        verify(gitRequestRepository, times(1)).findAll((Predicate) anyObject());
    }

    @Test
    public void search_Empty_Response_No_CollectorItems() {
        ObjectId componentId = ObjectId.get();
        ObjectId collectorItemId = ObjectId.get();
        ObjectId collectorId = ObjectId.get();

        GitRequestRequest request = new GitRequestRequest();
        request.setComponentId(componentId);

        when(componentRepository.findOne(request.getComponentId())).thenReturn(makeComponent(collectorItemId, collectorId, false));
        when(collectorRepository.findOne(collectorId)).thenReturn(new Collector());

        DataResponse<Iterable<GitRequest>> response = gitRequestService.search(request, "pull", "all");

        List<GitRequest> result = (List<GitRequest>) response.getResult();
        Assert.assertEquals(0, result.size());
    }

    @Test
    public void search_Empty_Response_No_Component() {
        ObjectId collectorId = ObjectId.get();

        GitRequestRequest request = new GitRequestRequest();

        when(componentRepository.findOne(request.getComponentId())).thenReturn(null);
        when(collectorRepository.findOne(collectorId)).thenReturn(new Collector());

        DataResponse<Iterable<GitRequest>> response = gitRequestService.search(request, "pull", "all");

        List<GitRequest> result = (List<GitRequest>) response.getResult();
        Assert.assertEquals(0, result.size());
    }

    private Component makeComponent(ObjectId collectorItemId, ObjectId collectorId, boolean populateCollectorItems) {
        CollectorItem item = new CollectorItem();
        item.setId(collectorItemId);
        item.setCollectorId(collectorId);
        Component c = new Component();
        if (populateCollectorItems) {
            c.getCollectorItems().put(CollectorType.SCM, Collections.singletonList(item));
        }
        return c;
    }
}
