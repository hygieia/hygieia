package com.capitalone.dashboard.service;

import com.capitalone.dashboard.misc.HygieiaException;
import com.capitalone.dashboard.model.Metadata;
import com.capitalone.dashboard.repository.MetadataRepository;
import com.capitalone.dashboard.request.MetadataCreateRequest;
import org.bson.types.ObjectId;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class MetadataServiceTest {

    @Mock
    private MetadataRepository metadataRepository;

    @InjectMocks
    private MetadataServiceImpl metadataServiceImpl;

    private static final String RAWDATA = "{\n" +
            "   \"token\":\"\",\n" +
            "   \"type\":\"1\",\n" +
            "   \"message\":{\n" +
            "      \"type\":1,\n" +
            "      \"message\":\"message\"\n" +
            "   }\n" +
            "}";

    private static final String KEY = "https://somejenkinsurl.com/somejob/1/";

    private static final String TYPE = "Build";

    private static final String SOURCE = "Jenkins";

    @Test
    public void testCreateGoodJSON() {

        Metadata metadata = buildMetadata();
        String expectedResult = metadata.getId().toString();
        String actualResult = "";
        when(metadataRepository.save(metadata)).thenReturn(metadata);
        try {
            actualResult = metadataServiceImpl.create(buildRequest());
        } catch (Exception e) {
            fail("Should not throw any exception as the json is a valid one");
        }

        assertEquals(expectedResult, actualResult);
    }

    @Test(expected = HygieiaException.class)
    public void testCreateBadJSON() throws HygieiaException {
        Metadata expectedResult = buildMetadata();
        when(metadataRepository.save(expectedResult)).thenReturn(expectedResult);
        MetadataCreateRequest request = buildRequest();
        request.setRawData("some bad json data");
        metadataServiceImpl

                .create(request);
    }


    private MetadataCreateRequest buildRequest() {
        MetadataCreateRequest request = new MetadataCreateRequest();
        request.setKey(KEY);
        request.setType(TYPE);
        request.setSource(SOURCE);
        request.setRawData(RAWDATA);
        return request;
    }

    private Metadata buildMetadata() {
        Metadata metadata = new Metadata();
        metadata.setType(TYPE);
        metadata.setSource(SOURCE);
        metadata.setKey(KEY);
        metadata.setRawData(RAWDATA);
        metadata.setId(ObjectId.get());
        return metadata;
    }

}
