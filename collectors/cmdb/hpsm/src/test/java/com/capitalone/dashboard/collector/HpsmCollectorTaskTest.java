package com.capitalone.dashboard.collector;

import com.capitalone.dashboard.misc.HygieiaException;
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
    @Mock private IncidentRepository incidentRepository;
    @Mock private ChangeOrderRepository changeOrderRepository;

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

    private void testCollectorAction(String collectorAction){
        System.setProperty("collector.action", collectorAction);
        Collector collector = collector(collectorAction);

        assertEquals(collectorAction, collector.getName());
        assertEquals(CollectorType.CMDB, collector.getCollectorType());
        assertTrue(collector.isOnline());
        assertTrue(collector.isEnabled());
    }

    @Test
    public void shouldGetIncidentCollector() {
        testCollectorAction("HpsmIncident");
    }

    @Test
    public void shouldGetChangeCollector() {
        testCollectorAction("HpsmChange");
    }

    @Test
    public void shouldGetHpsmCollector() {
        testCollectorAction("Hpsm");
    }

    @Test
    public void collect_testCollect() throws HygieiaException {
        when(hpsmClient.getApps()).thenReturn(getMockList());
        when(cmdbRepository.findAll()).thenReturn(getMockList());
        when(cmdbRepository.findAllByValidConfigItem(true)).thenReturn(getMockList());

        HpsmCollector collector =collector();
        collector.setId(new ObjectId("111ca42a258ad365fbb64ecc"));

        task.setCollectorAction("Hpsm");
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

    @Test
    public void collect_testCollectChangeOrders() throws HygieiaException {
        String collectorAction = "HpsmChange";
        System.setProperty("collector.action", collectorAction);

        when(hpsmClient.getChangeOrders()).thenReturn(getMockChangeOrderList());
        when(changeOrderRepository.findAll()).thenReturn(getMockChangeOrderList());

        HpsmCollector collector =collector(collectorAction);
        collector.setId(new ObjectId(createGuid()));

        task.setCollectorAction(collectorAction);
        task.collect(collector);

        verify(changeOrderRepository).findByChangeID("C012345");
        assertNull(changeOrderRepository.findByChangeID("C012399"));

    }

    public ArrayList<ChangeOrder> getMockChangeOrderList(){
        ObjectId collectorItemId = new ObjectId(createGuid());
        ArrayList<ChangeOrder> mockList = new ArrayList<>();
        ChangeOrder changeOrder = new ChangeOrder();

        changeOrder.setId(new ObjectId(createGuid()));
        changeOrder.setCollectorItemId(collectorItemId);
        changeOrder.setChangeID("C012345");
        changeOrder.setAssignmentGroup("HYGIEIA");
        mockList.add(changeOrder);

        changeOrder = new ChangeOrder();
        changeOrder.setId(new ObjectId(createGuid()));
        changeOrder.setCollectorItemId(collectorItemId);
        changeOrder.setChangeID("C012346");
        changeOrder.setAssignmentGroup("GITHUB");
        mockList.add(changeOrder);

        changeOrder = new ChangeOrder();
        changeOrder.setId(new ObjectId(createGuid()));
        changeOrder.setCollectorItemId(collectorItemId);
        changeOrder.setChangeID("C012347");
        changeOrder.setAssignmentGroup("JENKINS");
        mockList.add(changeOrder);

        changeOrder = new ChangeOrder();
        changeOrder.setId(new ObjectId(createGuid()));
        changeOrder.setCollectorItemId(collectorItemId);
        changeOrder.setChangeID("C012348");
        changeOrder.setAssignmentGroup("ARTIFACTORY");
        mockList.add(changeOrder);

        return mockList;
    }

    @Test
    public void collect_testCollectIncidents() throws HygieiaException{
        String collectorAction = "HpsmIncident";
        System.setProperty("collector.action", collectorAction);

        when(hpsmClient.getIncidents()).thenReturn(getMockIncidentList());
        when(incidentRepository.findAll()).thenReturn(getMockIncidentList());

        HpsmCollector collector =collector(collectorAction);
        collector.setId(new ObjectId(createGuid()));

        task.setCollectorAction(collectorAction);
        task.collect(collector);

        verify(incidentRepository).findByIncidentID("IR01234");
        assertNull(incidentRepository.findByIncidentID("IR02468"));

    }

    public ArrayList<Incident> getMockIncidentList(){
        ObjectId collectorItemId = new ObjectId(createGuid());
        ArrayList<Incident> mockList = new ArrayList<>();
        Incident incident = new Incident();

        incident.setId(new ObjectId(createGuid()));
        incident.setIncidentID("IR01234");
        incident.setCollectorItemId(collectorItemId);
        incident.setPrimaryAssignmentGroup("HYGIEIA");
        mockList.add(incident);

        incident = new Incident();
        incident.setId(new ObjectId(createGuid()));
        incident.setIncidentID("IR01235");
        incident.setCollectorItemId(collectorItemId);
        incident.setPrimaryAssignmentGroup("JENKINS");
        mockList.add(incident);

        incident = new Incident();
        incident.setId(new ObjectId(createGuid()));
        incident.setIncidentID("IR01236");
        incident.setCollectorItemId(collectorItemId);
        incident.setPrimaryAssignmentGroup("ARTIFACTORY");
        mockList.add(incident);

        incident = new Incident();
        incident.setId(new ObjectId(createGuid()));
        incident.setIncidentID("IR01237");
        incident.setCollectorItemId(collectorItemId);
        incident.setPrimaryAssignmentGroup("GITHUB");
        mockList.add(incident);

        return mockList;
    }


    private HpsmCollector collector() {
        return HpsmCollector.prototype();
    }

    private HpsmCollector collector(String collectorAction) {
        return HpsmCollector.prototype(collectorAction);
    }

    public static String createGuid() {
        byte[]  bytes = new byte[12];
        new Random().nextBytes(bytes);

        char[] hexArray = "0123456789abcdef".toCharArray();
        char[] hexChars = new char[bytes.length * 2];
        for ( int j = 0; j < bytes.length; j++ ) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

}