package com.capitalone.dashboard.service;

import com.capitalone.dashboard.model.CollectorItem;
import com.capitalone.dashboard.model.Dashboard;
import com.capitalone.dashboard.repository.CollectorItemRepository;
import com.capitalone.dashboard.repository.DashboardRepository;
import com.capitalone.dashboard.repository.PipelineRepository;
import com.capitalone.dashboard.request.PipelineSearchRequest;
import org.apache.commons.lang.NotImplementedException;
import org.bson.types.ObjectId;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.List;


public class PipelineServiceTest {

    @Mock
    private PipelineRepository pipelineRepository;
    @Mock
    private DashboardRepository dashboardRepository;
    @Mock
    private CollectorItemRepository collectorItemRepository;
    @InjectMocks
    private PipelineServiceImpl pipelineService;

    @Ignore
    @Test
    public void search() throws Exception {
        //create dashboard with pipeline
        //get collectoritem
        ObjectId dashboardCollectorItemId = ObjectId.get();
        PipelineSearchRequest request = new PipelineSearchRequest();
        List<ObjectId> dashboardCollectorItemIds = new ArrayList<>();
        dashboardCollectorItemIds.add(dashboardCollectorItemId);



    }

    @Ignore
    @Test
    public void search_commit_moves_from_commit_to_dev() throws Exception {

    }

    @Ignore
    @Test
    public void search_45_day_production_timespan() throws Exception {

    }

    @Ignore
    @Test
    public void search_broken_build_moves_to_dev() throws Exception {

    }

    private Dashboard makeDashboard(){
        throw new NotImplementedException();
    }

    private CollectorItem makeDashboardCollectorItem(){
        throw new NotImplementedException();
    }
}
