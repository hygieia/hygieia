package com.capitalone.dashboard.collector;

import com.capitalone.dashboard.model.*;
import com.capitalone.dashboard.repository.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bson.types.ObjectId;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.scheduling.TaskScheduler;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class HpsmCollectorTaskTest {
    private static final Log LOG = LogFactory.getLog(HpsmCollectorTaskTest.class);
    @Mock private BaseCollectorRepository<Collector> collectorRepository;
    @Mock private HpsmRepository hpsmRepository;
    @Mock private CmdbRepository cmdbRepository;
    @Mock private HpsmCollector hpsmCollector;
    @Mock private HpsmClient hpsmClient;
    @Mock private HpsmSettings hpsmSettings;

    @Mock private Cmdb cmdb1;
    @Mock private Cmdb cmdb2;

    @InjectMocks private HpsmCollectorTask task;
    @Test
    public void shouldGetCollector() {
        Collector collector = collector();

        assertEquals("Hpsm", collector.getName());
        assertEquals(CollectorType.CMDB, collector.getCollectorType());
        assertTrue(collector.isOnline());
        assertTrue(collector.isEnabled());
    }
    @Test
    public void collect_testCollect() {
        when(hpsmClient.getApps()).thenReturn(getMockList());
        when(cmdbRepository.findAll()).thenReturn(getMockList());


        HpsmCollector collector =collector();
        collector.setId(new ObjectId("111ca42a258ad365fbb64ecc"));

        task.collect(collector);

        verify(cmdbRepository).findByConfigurationItem("ASVTEST");
        assertNull(cmdbRepository.findByConfigurationItem("test213"));



    }
    public ArrayList<Cmdb> getMockList(){
        ArrayList<Cmdb> mockList = new ArrayList<>();


        Cmdb cmdb = new Cmdb();

        cmdb.setId(new ObjectId("1c1ca42a258ad365fbb64ecc"));
        cmdb.setConfigurationItem("ASVTEST");
        cmdb.setOwnerDept("TESTLOB0");
        cmdb.setAssignmentGroup("PROCESS_TEST_AG0");
        cmdb.setAppServiceOwner("John Handcock");
        cmdb.setBusinessOwner("John Doe");
        cmdb.setSupportOwner("John Handcock");
        cmdb.setDevelopmentOwner("Jain Doe");
        cmdb.setCollectorItemId(new ObjectId("111ca42a258ad365fbb64ecc"));
        mockList.add(cmdb);

        cmdb = new Cmdb();
        cmdb.setId(new ObjectId("1c1ca42a258ad365fbb64ecd"));
        cmdb.setConfigurationItem("ASVTEST123");
        cmdb.setOwnerDept("TESTLOB1");
        cmdb.setAssignmentGroup("PROCESS_TEST_AG1");
        cmdb.setAppServiceOwner("Jain Doe");
        cmdb.setBusinessOwner("John Handcock");
        cmdb.setSupportOwner("John Handcock");
        cmdb.setDevelopmentOwner("Jain Doe");
        cmdb.setCollectorItemId(new ObjectId("111ca42a258ad365fbb64ecc"));
        mockList.add(cmdb);

        cmdb = new Cmdb();
        cmdb.setId(new ObjectId("1c1ca42a258ad365fbb64ece"));
        cmdb.setConfigurationItem("ASVTEST1234");
        cmdb.setOwnerDept("TESTLOB2");
        cmdb.setAssignmentGroup("PROCESS_TEST_AG2");
        cmdb.setAppServiceOwner("John Handcock");
        cmdb.setBusinessOwner("John Doe");
        cmdb.setSupportOwner("John Doe");
        cmdb.setDevelopmentOwner("John Handcock");
        cmdb.setCollectorItemId(new ObjectId("111ca42a258ad365fbb64ecc"));
        mockList.add(cmdb);


        cmdb = new Cmdb();
        cmdb.setId(new ObjectId("1c1ca42a258ad365fbb64ecf"));
        cmdb.setConfigurationItem("ASVTEST12346");
        cmdb.setOwnerDept("TESTLOB3");
        cmdb.setAssignmentGroup("PROCESS_TEST_AG3");
        cmdb.setAppServiceOwner("John Doe");
        cmdb.setBusinessOwner("Jain Doe");
        cmdb.setSupportOwner("John Doe");
        cmdb.setDevelopmentOwner("John Handcock");
        cmdb.setCollectorItemId(new ObjectId("111ca42a258ad365fbb64ecc"));
        mockList.add(cmdb);
        return mockList;
    }

    private HpsmCollector collector() {
        return HpsmCollector.prototype();
    }
}