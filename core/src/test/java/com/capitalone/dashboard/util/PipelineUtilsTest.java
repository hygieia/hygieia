package com.capitalone.dashboard.util;

import com.capitalone.dashboard.model.Application;
import com.capitalone.dashboard.model.AuthType;
import com.capitalone.dashboard.model.Build;
import com.capitalone.dashboard.model.Commit;
import com.capitalone.dashboard.model.Component;
import com.capitalone.dashboard.model.Dashboard;
import com.capitalone.dashboard.model.DashboardType;
import com.capitalone.dashboard.model.Owner;
import com.capitalone.dashboard.model.Pipeline;
import com.capitalone.dashboard.model.Widget;
import com.capitalone.dashboard.repository.CommitRepository;
import org.bson.types.ObjectId;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.capitalone.dashboard.util.TestUtils.createBuild;
import static com.capitalone.dashboard.util.TestUtils.createCommit;
import static com.capitalone.dashboard.util.TestUtils.getPipeline;
import static com.capitalone.dashboard.util.TestUtils.getScm;
import static org.junit.Assert.assertEquals;

/**
 * Created by syq410 on 2/23/17.
 */
@RunWith(MockitoJUnitRunner.class)
public class PipelineUtilsTest {

    private static final ObjectId DASHBOARD_ID = new ObjectId();
    @Mock
    private CommitRepository commitRepository;

    @Test
    public void testOrderForStages() {
        Map<String, String> ordermap = PipelineUtils.getOrderForStages(setupDashboard());
        assertEquals(ordermap.get("0"), "Commit");
        assertEquals(ordermap.get("1"), "Build");
        assertEquals(ordermap.get("2"), "dev");
        assertEquals(ordermap.get("3"), "qa");
        assertEquals(ordermap.get("4"), "int");

    }

    private Dashboard setupDashboard() {
        ObjectId configItemAppId = new ObjectId();
        ObjectId configItemComponetId = new ObjectId();
        List<String> activeWidgets = new ArrayList<>();
        Dashboard rt = new Dashboard("Capone", "hygieia", new Application("hygieia", new Component()), new Owner("owner", AuthType.STANDARD), DashboardType.Team, configItemAppId, configItemComponetId,activeWidgets);

        Widget pipelineWidget = new Widget();
        pipelineWidget.setName("pipeline");
        Map<String, String> mappings = new HashMap<>();
        mappings.put("dev", "DEV");
        mappings.put("qa", "QA");
        mappings.put("int", "INT");
        pipelineWidget.getOptions().put("mappings", mappings);

        Map<String, String> order = new HashMap<>();
        order.put("0", "dev");
        order.put("1", "qa");
        order.put("2", "int");
        pipelineWidget.getOptions().put("order", order);

        rt.getWidgets().add(pipelineWidget);

        rt.setId(DASHBOARD_ID);

        return rt;
    }

    @Test
    public void test_processFailedBuilds() {
        Build successBuild = createBuild();
        Pipeline pipeline = getPipeline(successBuild.getCollectorItemId());
        PipelineUtils.processPreviousFailedBuilds(successBuild, pipeline);
        Assert.assertEquals(pipeline.getFailedBuilds().size(), 0);
    }


    @Test
    public void test_isMoveCommitToBuild() {

        List<Commit> commits = new ArrayList<>();
        commits.add(createCommit("scmRev1", "http://github.com/scmurl"));
        Mockito.when(commitRepository.findByScmRevisionNumber("scmRev1")).thenReturn(commits);
        Assert.assertTrue(PipelineUtils.isMoveCommitToBuild(createBuild(), getScm("scmRev1"), commitRepository));
    }

    @Test
    public void test_isMoveCommitToBuild_false() {

        List<Commit> commits = new ArrayList<>();
        commits.add(createCommit("scmRev1", "http://github.com/scmurl1"));
        Mockito.when(commitRepository.findByScmRevisionNumber("scmRev1")).thenReturn(commits);
        Assert.assertFalse(PipelineUtils.isMoveCommitToBuild(createBuild(), getScm("scmRev1"), commitRepository));
    }


}
