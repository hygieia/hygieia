package com.capitalone.dashboard.service;

import com.capitalone.dashboard.model.CloudSubNetwork;
import com.capitalone.dashboard.model.NameValue;
import com.capitalone.dashboard.repository.CloudSubNetworkRepository;
import com.capitalone.dashboard.repository.CollectorRepository;
import com.capitalone.dashboard.repository.ComponentRepository;
import com.capitalone.dashboard.request.CloudInstanceListRefreshRequest;
import com.capitalone.dashboard.request.CloudSubnetCreateRequest;
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
public class CloudSubnetServiceImplTest {
    @Mock
    private ComponentRepository componentRepository;
    @Mock
    private CloudSubNetworkRepository cloudSubNetworkRepository;
    @Mock
    private CollectorRepository collectorRepository;

    @InjectMocks
    private CloudSubnetServiceImpl cloudSubnetService;


    private static CloudSubNetwork testsubnet12345678;
    private static CloudSubNetwork testsubnet87654321;
    private static Collection<CloudSubNetwork> collectionSubnet;


    @BeforeClass
    public static void setupTestObjects() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        byte[] content = Resources.asByteSource(Resources.getResource("cloud-subnet-12345678.json")).read();
        testsubnet12345678 = mapper.readValue(content, CloudSubNetwork.class);
        content = Resources.asByteSource(Resources.getResource("cloud-subnet-87654321.json")).read();
        testsubnet87654321 = mapper.readValue(content, CloudSubNetwork.class);
        collectionSubnet = new ArrayList<>();
        collectionSubnet.add(testsubnet12345678);
        collectionSubnet.add(testsubnet87654321);
    }


    @Test
    public void getSubNetworkDetails() throws Exception {
        when(cloudSubNetworkRepository.findByAccountNumber("123456789")).thenReturn(collectionSubnet);
        Collection<CloudSubNetwork> result = cloudSubnetService.getSubNetworkDetailsByAccount("123456789");
        assertThat(result, hasSize(2));
    }

    @Test
    public void getSubNetworkDetailsNoMatch() throws Exception {
        when(cloudSubNetworkRepository.findByAccountNumber("999")).thenReturn(new ArrayList<CloudSubNetwork>());
        Collection<CloudSubNetwork> result = cloudSubnetService.getSubNetworkDetailsByAccount("999");
        assertThat(result, hasSize(0));
    }

    @Test
    public void refreshSubnets() throws Exception {
        when(cloudSubNetworkRepository.findByAccountNumber("123456789")).thenReturn(collectionSubnet);
        Collection<String> deleted = cloudSubnetService.refreshSubnets(makeRefreshRequest());
        assertThat(deleted, hasSize(1));
        String[] expected = {testsubnet87654321.getSubnetId()};
        assertArrayEquals(deleted.toArray(), expected);
    }

    @Test
    public void upsertSubNetwork() throws Exception {
        testsubnet12345678.setId(new ObjectId());
        String[] oid = {testsubnet12345678.getId().toString()};
        CloudSubnetCreateRequest[]  req = {makeRequest(testsubnet12345678)};
        when(cloudSubNetworkRepository.findBySubnetId(testsubnet12345678.getSubnetId())).thenReturn(testsubnet12345678);
        when(cloudSubNetworkRepository.save(testsubnet12345678)).thenReturn(testsubnet12345678);
        Collection<String> objectIds = cloudSubnetService.upsertSubNetwork(Arrays.asList(req));
        verify(cloudSubNetworkRepository, times(1)).save(testsubnet12345678);
        assertArrayEquals(objectIds.toArray(),oid);
    }

    @Test
    public void getSubNetworkDetailsBySubnetId() throws Exception {
        when(cloudSubNetworkRepository.findBySubnetId(testsubnet12345678.getSubnetId())).thenReturn(testsubnet12345678);
        CloudSubNetwork result = cloudSubnetService.getSubNetworkDetailsBySubnetId(testsubnet12345678.getSubnetId());
        assertThat(result, is(testsubnet12345678));
    }

    @Test
    public void getSubNetworkDetailsBySubnetIds() throws Exception {
        ArrayList<String> ids = new ArrayList<>();
        ids.add(testsubnet12345678.getSubnetId());
        ids.add(testsubnet87654321.getSubnetId());
        when(cloudSubNetworkRepository.findBySubnetIdIn(ids)).thenReturn(collectionSubnet);
        Collection<CloudSubNetwork> result = cloudSubnetService.getSubNetworkDetailsBySubnetIds(ids);
        assertThat(result, is(collectionSubnet));
    }

    @Test
    public void getSubNetworkDetailsByTags() throws Exception {
        NameValue nv = new NameValue("MYEnvironment", "MYONLYENVIRONMENT");
        ArrayList<NameValue> nvList = new ArrayList<>();
        nvList.add(nv);
        when(cloudSubNetworkRepository.findByTagNameAndValue("MYEnvironment", "MYONLYENVIRONMENT")).thenReturn(collectionSubnet);
        Collection<CloudSubNetwork> result = cloudSubnetService.getSubNetworkDetailsByTags(nvList);
        assertThat(result, hasSize(2));
        assertArrayEquals(collectionSubnet.toArray(), result.toArray());
    }

    private CloudInstanceListRefreshRequest makeRefreshRequest() {
        CloudInstanceListRefreshRequest req = new CloudInstanceListRefreshRequest();
        String[] ids = {testsubnet12345678.getSubnetId()};
        req.setAccountNumber(testsubnet12345678.getAccountNumber());
        req.setInstanceIds(Arrays.asList(ids));
        req.setRefreshDate(new Date());
        return req;
    }


    private CloudSubnetCreateRequest makeRequest(CloudSubNetwork subnet) {
        CloudSubnetCreateRequest req = new CloudSubnetCreateRequest();
        req.setAccountNumber(subnet.getAccountNumber());
        req.setVirtualNetworkId(subnet.getVirtualNetworkId());
        req.setZone(subnet.getZone());
        req.setAvailableIPCount(String.valueOf(subnet.getAvailableIPCount()));
        req.setCidrBlock(subnet.getCidrBlock());
        req.setCidrCount(String.valueOf(subnet.getCidrCount()));
        req.setCreationDate(String.valueOf(subnet.getCreationDate()));
        req.getTags().addAll(subnet.getTags());
        req.setDefaultForZone(String.valueOf(subnet.isDefaultForZone()));
        req.setState(subnet.getState());
        req.setIpUsage(subnet.getIpUsage());
        req.setUsedIPCount(String.valueOf(subnet.getUsedIPCount()));
        req.setLastUpdateDate(String.valueOf(subnet.getLastUpdateDate()));
        req.setSubnetId(subnet.getSubnetId());
        return req;
    }
}