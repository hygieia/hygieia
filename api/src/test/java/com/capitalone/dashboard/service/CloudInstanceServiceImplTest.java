package com.capitalone.dashboard.service;

import com.capitalone.dashboard.misc.HygieiaException;
import com.capitalone.dashboard.model.CloudInstance;
import com.capitalone.dashboard.model.CloudInstanceHistory;
import com.capitalone.dashboard.model.NameValue;
import com.capitalone.dashboard.repository.CloudInstanceHistoryRepository;
import com.capitalone.dashboard.repository.CloudInstanceRepository;
import com.capitalone.dashboard.repository.CollectorRepository;
import com.capitalone.dashboard.repository.ComponentRepository;
import com.capitalone.dashboard.request.CloudInstanceCreateRequest;
import com.capitalone.dashboard.request.CloudInstanceListRefreshRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Resources;
import org.bson.types.ObjectId;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CloudInstanceServiceImplTest {
    @Mock
    private ComponentRepository componentRepository;
    @Mock
    private CloudInstanceHistoryRepository cloudInstanceHistoryRepository;
    @Mock
    private CloudInstanceRepository cloudInstanceRepository;
    @Mock
    private CollectorRepository collectorRepository;

    @InjectMocks
    private CloudInstanceServiceImpl cloudInstanceService;

    private static CloudInstance testInstance12345678;
    private static CloudInstance testInstance9876543;
    private static CloudInstanceHistory testInstanceHistory;
    private static Collection<CloudInstance> collectionInstance;
    private static Collection<CloudInstanceHistory> collectionHistory;


    @BeforeClass
    public static void setupTestObjects() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        byte[] content = Resources.asByteSource(Resources.getResource("cloudinstance-12345678.json")).read();
        testInstance12345678 = mapper.readValue(content, CloudInstance.class);
        content = Resources.asByteSource(Resources.getResource("cloudinstance-9876543.json")).read();
        testInstance9876543 = mapper.readValue(content, CloudInstance.class);
        collectionInstance = new ArrayList<>();
        collectionInstance.add(testInstance12345678);
        collectionInstance.add(testInstance9876543);
        collectionHistory = new ArrayList<>();
        collectionHistory.add(testInstanceHistory);
        content = Resources.asByteSource(Resources.getResource("cloudinstance-history.json")).read();
        testInstanceHistory = mapper.readValue(content, CloudInstanceHistory.class);
    }


    @Test
    public void getInstanceDetails() throws Exception {
        when(cloudInstanceRepository.findByAccountNumber("123456789")).thenReturn(collectionInstance);
        Collection<CloudInstance> result = cloudInstanceService.getInstanceDetailsByAccount("123456789");
        assertThat(result, hasSize(2));
    }

    @Test
    public void getInstanceDetailsNoMatch() throws Exception {
        when(cloudInstanceRepository.findByAccountNumber("123456789")).thenReturn(collectionInstance);
        Collection<CloudInstance> result = cloudInstanceService.getInstanceDetailsByAccount("234567");
        assertThat(result, hasSize(0));
    }


    @Test
    public void getInstanceDetailsByInstanceId() throws Exception {
        when(cloudInstanceRepository.findByInstanceId("12345678")).thenReturn(testInstance12345678);
        CloudInstance result = cloudInstanceService.getInstanceDetailsByInstanceId("12345678");
        assertThat(result, is(testInstance12345678));
    }

    @Test
    public void getInstanceDetailsByInstanceIds() throws Exception {

        ArrayList<String> ids = new ArrayList<>();
        ids.add("12345678");
        ids.add("9876543");
        when(cloudInstanceRepository.findByInstanceIdIn(ids)).thenReturn(collectionInstance);
        Collection<CloudInstance> result = cloudInstanceService.getInstanceDetailsByInstanceIds(ids);
        assertThat(result, is(collectionInstance));
    }

    @Test
    public void getInstanceDetailsByTags() throws Exception {
        NameValue nv = new NameValue("MYEnvironment", "MYONLYENVIRONMENT");
        ArrayList<NameValue> nvList = new ArrayList<>();
        nvList.add(nv);
        when(cloudInstanceRepository.findByTagNameAndValue("MYEnvironment", "MYONLYENVIRONMENT")).thenReturn(collectionInstance);
        Collection<CloudInstance> result = cloudInstanceService.getInstanceDetailsByTags(nvList);
        assertThat(result, hasSize(2));
        assertArrayEquals(collectionInstance.toArray(), result.toArray());
    }


    @Test
    public void getInstanceHistoryByAccount() throws Exception {
        when(cloudInstanceHistoryRepository.findByAccountNumber(testInstanceHistory.getAccountNumber())).thenReturn(collectionHistory);
        Collection<CloudInstanceHistory> result = cloudInstanceService.getInstanceHistoryByAccount(testInstanceHistory.getAccountNumber());
        assertThat(result, is(collectionHistory));
    }

    @Test
    public void refreshInstances() throws Exception {
        when(cloudInstanceRepository.findByAccountNumber("123456789")).thenReturn(collectionInstance);
        Collection<String> deleted = cloudInstanceService.refreshInstances(makeRefreshRequest());
        assertThat(deleted, hasSize(1));
        String[] expected = {testInstance9876543.getInstanceId()};
        assertArrayEquals(deleted.toArray(), expected);
    }

    @Test
    public void refreshInstancesEmpty() throws Exception {
        when(cloudInstanceRepository.findByAccountNumber("123456789")).thenReturn(collectionInstance);
        Collection<String> deleted = cloudInstanceService.refreshInstances(new CloudInstanceListRefreshRequest());
        assertThat(deleted, hasSize(0));
    }

    @Test(expected=HygieiaException.class)
    public void upsertInstanceEmpty() throws HygieiaException {
        CloudInstanceCreateRequest[]  req = {new CloudInstanceCreateRequest()};
        cloudInstanceService.upsertInstance(Arrays.asList(req));
    }


    @Test
    public void upsertInstanceSame() throws HygieiaException {
        testInstance12345678.setId(new ObjectId());
        String[] oid = {testInstance12345678.getId().toString()};
        CloudInstanceCreateRequest[]  req = {makeRequest(testInstance12345678)};
        when(cloudInstanceRepository.findByInstanceId(testInstance12345678.getInstanceId())).thenReturn(testInstance12345678);
        when(cloudInstanceRepository.save(testInstance12345678)).thenReturn(testInstance12345678);
        Collection<String> objectIds = cloudInstanceService.upsertInstance(Arrays.asList(req));
        verify(cloudInstanceRepository, times(1)).save(testInstance12345678);
        assertArrayEquals(objectIds.toArray(),oid);
    }


    private CloudInstanceListRefreshRequest makeRefreshRequest() {
        CloudInstanceListRefreshRequest req = new CloudInstanceListRefreshRequest();
        String[] ids = {testInstance12345678.getInstanceId()};
        req.setAccountNumber(testInstance12345678.getAccountNumber());
        req.setInstanceIds(Arrays.asList(ids));
        req.setRefreshDate(new Date());
        return req;
    }

    private CloudInstanceCreateRequest makeRequest(CloudInstance instance) {
        CloudInstanceCreateRequest req = new CloudInstanceCreateRequest();
        req.setAccountNumber(instance.getAccountNumber());
        req.setVirtualNetworkId(instance.getVirtualNetworkId());
        req.setLastAction(instance.getLastAction());
        req.setAge(String.valueOf(instance.getAge()));
        req.setAutoScaleName(instance.getAutoScaleName());
        req.setCpuUtilization(String.valueOf(instance.getCpuUtilization()));
        req.setDiskRead(String.valueOf(instance.getDiskRead()));
        req.setDiskWrite(String.valueOf(instance.getDiskWrite()));
        req.setImageApproved(String.valueOf(instance.isImageApproved()));
        req.setImageExpirationDate(String.valueOf(instance.getImageExpirationDate()));
        req.setImageId(instance.getImageId());
        req.setInstanceId(instance.getInstanceId());
        req.setInstanceOwner(instance.getInstanceOwner());
        req.setInstanceType(instance.getInstanceType());
        req.setIsMonitored(String.valueOf(instance.isMonitored()));
        req.setIsTagged(String.valueOf(instance.isTagged()));
        req.setIsStopped(String.valueOf(instance.isStopped()));
        req.setNetworkIn(String.valueOf(instance.getNetworkIn()));
        req.setNetworkOut(String.valueOf(instance.getNetworkOut()));
        req.setPrivateDns(instance.getPrivateDns());
        req.setPublicDns(instance.getPublicDns());
        req.setPublicIp(instance.getPublicIp());
        req.setPrivateIp(instance.getPrivateIp());
        req.setImageExpirationDate(String.valueOf(instance.getImageExpirationDate()));
        req.getTags().addAll(instance.getTags());
        req.getSecurityGroups().addAll(instance.getSecurityGroups());
        return req;
    }
}