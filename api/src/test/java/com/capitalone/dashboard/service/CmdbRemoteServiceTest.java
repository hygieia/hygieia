package com.capitalone.dashboard.service;

import com.capitalone.dashboard.misc.HygieiaException;
import com.capitalone.dashboard.model.Cmdb;
import com.capitalone.dashboard.model.Collector;
import com.capitalone.dashboard.model.CollectorItem;
import com.capitalone.dashboard.model.CollectorType;
import com.capitalone.dashboard.repository.CmdbRepository;
import com.capitalone.dashboard.repository.CollectorRepository;
import com.capitalone.dashboard.request.CmdbRequest;
import org.bson.types.ObjectId;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CmdbRemoteServiceTest {

    @Mock
    private CmdbRepository cmdbRepository;
    @Mock
    private CollectorRepository collectorRepository;
    @Mock
    private CollectorService collectorService;

    @InjectMocks
    private CmdbRemoteServiceImpl cmdbRemoteService;

    /**
     * Tests the remote create functionality
     * @throws HygieiaException
     */
    @Test
    public void remoteCreate() throws HygieiaException {
        Cmdb expected = makeCmdbItem("BAPTEST", "subtype",
                "type", "assignmentgroup","owner", "BAPTEST");
        ObjectId objectId = ObjectId.get();
        expected.setId(objectId);
        CmdbRequest request = makeCmdbRequest("BAPTEST", "subtype",
                "type", "assignmentgroup","owner", "BAPTEST", "", "cmdbCollector");
        when(cmdbRepository.findByConfigurationItemAndItemType("","" )).thenReturn(null);
        when(collectorService.createCollectorItem(Matchers.any(CollectorItem.class) )).thenReturn(makeCollectorItem());
        when(collectorRepository.findByCollectorTypeAndName(CollectorType.CMDB, request.getToolName())).thenReturn(makeCollector( request.getToolName(), CollectorType.CMDB));
        when(cmdbRepository.save(Matchers.any(Cmdb.class))).thenReturn(expected);

        assertThat(cmdbRemoteService.remoteCreate(request), is(expected));
    }

    /**
     * Tests remoteCreate functionality ConfigurationItemBusServName is invalid
     * @throws HygieiaException
     */
    @Test
    public void remoteCreateInvalidBusService() throws HygieiaException {
        Cmdb expected = makeCmdbItem("BAPTEST", "subtype",
                "type", "assignmentgroup","owner", "BAPTEST");
        ObjectId objectId = ObjectId.get();
        expected.setId(objectId);
        CmdbRequest request = makeCmdbRequest("BAPTEST", "subtype",
                "type", "assignmentgroup","owner", "BAPTEST", "ASVTEST", "cmdbCollector");
        when(cmdbRepository.findByConfigurationItemAndItemType("","" )).thenReturn(null);
        when(collectorService.createCollectorItem(Matchers.any(CollectorItem.class) )).thenReturn(makeCollectorItem());
        when(collectorRepository.findByCollectorTypeAndName(CollectorType.CMDB, request.getToolName())).thenReturn(makeCollector( request.getToolName(), CollectorType.CMDB));
        when(cmdbRepository.save(Matchers.any(Cmdb.class))).thenReturn(expected);

        Throwable t = new Throwable();
        RuntimeException excep = new RuntimeException("Configuration Item " + request.getConfigurationItemBusServName() + " does not exist", t);
        try {
            cmdbRemoteService.remoteCreate(request);
            fail("Should throw RuntimeException");
        } catch(Exception e) {
            assertEquals(excep.getMessage(), e.getMessage());
        }
    }
    /**
     * Tests remoteCreate functionality ConfigurationItemBusServName doesn't have existing relationships
     * @throws HygieiaException
     */
    @Test
    public void remoteCreateRelationshipUpdateExistingCompsNull() throws HygieiaException {
        Cmdb businessServiceItem = makeCmdbItem("ASVTEST", "subtype",
                "type", "assignmentgroup","owner", "ASVTEST");
        Cmdb expected = makeCmdbItem("BAPTEST", "subtype",
                "type", "assignmentgroup","owner", "BAPTEST");
        ObjectId objectId = ObjectId.get();
        expected.setId(objectId);
        CmdbRequest request = makeCmdbRequest("BAPTEST", "subtype",
                "type", "assignmentgroup","owner", "BAPTEST", "ASVTEST", "cmdbCollector");
        when(cmdbRepository.findByConfigurationItemAndItemType("","" )).thenReturn(null);
        when(collectorService.createCollectorItem(Matchers.any(CollectorItem.class) )).thenReturn(makeCollectorItem());
        when(collectorRepository.findByCollectorTypeAndName(CollectorType.CMDB, request.getToolName())).thenReturn(makeCollector( request.getToolName(), CollectorType.CMDB));
        when(cmdbRepository.findByConfigurationItemAndItemType( request.getConfigurationItemBusServName(), "app" )).thenReturn(businessServiceItem);
        when(cmdbRepository.save(Matchers.any(Cmdb.class))).thenReturn(expected);

        assertThat(cmdbRemoteService.remoteCreate(request), is(expected));
    }
    /**
     * Tests remoteCreate functionality ConfigurationItemBusServName does have existing relationships
     * @throws HygieiaException
     */
    @Test
    public void remoteCreateRelationshipUpdateExistingComps() throws HygieiaException {
        Cmdb businessServiceItem = makeCmdbItem("ASVTEST", "subtype",
                "type", "assignmentgroup","owner", "ASVTEST");
        List<String> compList = new ArrayList<>();
        compList.add("relationshipValue");
        businessServiceItem.setComponents(compList);
        Cmdb expected = makeCmdbItem("BAPTEST", "subtype",
                "type", "assignmentgroup","owner", "BAPTEST");
        ObjectId objectId = ObjectId.get();
        expected.setId(objectId);
        CmdbRequest request = makeCmdbRequest("BAPTEST", "subtype",
                "type", "assignmentgroup","owner", "BAPTEST", "ASVTEST", "cmdbCollector");
        when(cmdbRepository.findByConfigurationItemAndItemType("","" )).thenReturn(null);
        when(collectorService.createCollectorItem(Matchers.any(CollectorItem.class) )).thenReturn(makeCollectorItem());
        when(collectorRepository.findByCollectorTypeAndName(CollectorType.CMDB, request.getToolName())).thenReturn(makeCollector( request.getToolName(), CollectorType.CMDB));
        when(cmdbRepository.findByConfigurationItemAndItemType( request.getConfigurationItemBusServName(), "app" )).thenReturn(businessServiceItem);
        when(cmdbRepository.save(Matchers.any(Cmdb.class))).thenReturn(expected);

        assertThat(cmdbRemoteService.remoteCreate(request), is(expected));
    }

    private Cmdb makeCmdbItem(String configurationItem,
                              String configurationItemSubType,
                              String configurationItemType,
                              String assignmentGroup,
                              String ownerDept,
                              String commonName){
        Cmdb cmdb = new Cmdb();
        cmdb.setConfigurationItem(configurationItem);
        cmdb.setConfigurationItemSubType(configurationItemSubType);
        cmdb.setConfigurationItemType(configurationItemType);
        cmdb.setAssignmentGroup(assignmentGroup);
        cmdb.setOwnerDept(ownerDept);
        cmdb.setCommonName(commonName);

        return cmdb;
    }
    private CmdbRequest makeCmdbRequest(String configurationItem,
                                        String configurationItemSubType,
                                        String configurationItemType,
                                        String assignmentGroup,
                                        String ownerDept,
                                        String commonName,
                                        String configurationItemBusServName,
                                        String toolName){

        CmdbRequest request = new CmdbRequest();
        request.setConfigurationItem(configurationItem);
        request.setConfigurationItemSubType(configurationItemSubType);
        request.setConfigurationItemType(configurationItemType);
        request.setAssignmentGroup(assignmentGroup);
        request.setOwnerDept(ownerDept);
        request.setCommonName(commonName);
        request.setConfigurationItemBusServName(configurationItemBusServName);
        request.setToolName(toolName);
        return request;
    }

    private List<Collector> makeCollector(String name, CollectorType type) {
        List<Collector> list = new ArrayList<>();
        Collector collector = new Collector();
        collector.setId(ObjectId.get());
        collector.setName(name);
        collector.setCollectorType(type);
        collector.setEnabled(true);
        collector.setOnline(true);
        collector.setLastExecuted(System.currentTimeMillis());
        list.add(collector);
        return list;
    }

    private CollectorItem makeCollectorItem() {
        CollectorItem item = new CollectorItem();
        item.setCollectorId(new ObjectId());
        item.setId(new ObjectId());
        item.setEnabled(true);
        return item;
    }
}
