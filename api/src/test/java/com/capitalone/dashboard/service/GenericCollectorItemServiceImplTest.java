package com.capitalone.dashboard.service;

import com.capitalone.dashboard.misc.HygieiaException;
import com.capitalone.dashboard.model.Collector;
import com.capitalone.dashboard.model.GenericCollectorItem;
import com.capitalone.dashboard.repository.BinaryArtifactRepository;
import com.capitalone.dashboard.repository.BuildRepository;
import com.capitalone.dashboard.repository.CollectorItemRepository;
import com.capitalone.dashboard.repository.CollectorRepository;
import com.capitalone.dashboard.repository.GenericCollectorItemRepository;
import com.capitalone.dashboard.request.GenericCollectorItemCreateRequest;
import com.capitalone.dashboard.settings.ApiSettings;
import com.capitalone.dashboard.testutil.FongoConfig;
import com.capitalone.dashboard.testutil.GsonUtil;
import com.github.fakemongo.junit.FongoRule;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.bson.types.ObjectId;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.*;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {FongoConfig.class})
@DirtiesContext
public class GenericCollectorItemServiceImplTest {

    @Rule
    public FongoRule fongoRule = new FongoRule();

    @Autowired
    private GenericCollectorItemRepository genericCollectorItemRepository;

    @Autowired
    private CollectorRepository collectorRepository;

    @Autowired
    private BuildRepository buildRepository;

    @Autowired
    private CollectorItemRepository collectorItemRepository;

    private ApiSettings apiSettings;

    @Autowired
    private BinaryArtifactRepository binaryArtifactRepository;



    @Bean
    private GenericCollectorItemService genericCollectorItemService() {
        return new GenericCollectorItemServiceImpl(genericCollectorItemRepository, collectorRepository,buildRepository,collectorItemRepository,binaryArtifactRepository,apiSettings);
    }

    @Test
    public void createNew() throws IOException, HygieiaException {
        GenericCollectorItemCreateRequest request = createRequest("GitHub", "some data", "some source", "5ba136290be2d32568777fa8", "5bae541b099739600663ef9a");
        loadCollector();
        genericCollectorItemRepository.deleteAll();
        String response = genericCollectorItemService().create(request);
        List<GenericCollectorItem> genericCollectorItems = Lists.newArrayList(genericCollectorItemRepository.findAll());
        assertTrue(!CollectionUtils.isEmpty(genericCollectorItems));
        GenericCollectorItem genericCollectorItem = genericCollectorItems.get(0);
        assertTrue(genericCollectorItem.getRawData().equalsIgnoreCase("some data"));
        assertTrue(genericCollectorItem.getSource().equalsIgnoreCase("some source"));
        assertTrue(genericCollectorItem.getToolName().equalsIgnoreCase("GitHub"));
        assertEquals(genericCollectorItem.getBuildId(), new ObjectId("5ba136290be2d32568777fa8"));
        assertEquals(genericCollectorItem.getRelatedCollectorItem(), new ObjectId("5bae541b099739600663ef9a"));
        assertEquals(genericCollectorItem.getCollectorId(), new ObjectId("5ba16a0b0be2d34a64291205"));
    }

    @Test
    public void createWithExisting() throws IOException, HygieiaException {
        GenericCollectorItemCreateRequest request = createRequest("GitHub", "some data", "some source", "5ba136290be2d32568777fa8", "5bae541b099739600663ef9a");
        loadCollector();
        genericCollectorItemRepository.deleteAll();
        loadData("GitHub", "some data", "some source",new ObjectId("5ba136290be2d32568777fa8"), new ObjectId("5bae541b099739600663ef9a"), new ObjectId("5ba16a0b0be2d34a64291205"));
        genericCollectorItemService().create(request);
        List<GenericCollectorItem> genericCollectorItems = Lists.newArrayList(genericCollectorItemRepository.findAll());
        assertTrue(!CollectionUtils.isEmpty(genericCollectorItems));
        assertEquals(1, genericCollectorItems.size());
        GenericCollectorItem genericCollectorItem = genericCollectorItems.get(0);
        assertTrue(genericCollectorItem.getRawData().equalsIgnoreCase("some data"));
        assertTrue(genericCollectorItem.getSource().equalsIgnoreCase("some source"));
        assertTrue(genericCollectorItem.getToolName().equalsIgnoreCase("GitHub"));
        assertEquals(genericCollectorItem.getBuildId(), new ObjectId("5ba136290be2d32568777fa8"));
        assertEquals(genericCollectorItem.getRelatedCollectorItem(), new ObjectId("5bae541b099739600663ef9a"));
        assertEquals(genericCollectorItem.getCollectorId(), new ObjectId("5ba16a0b0be2d34a64291205"));
    }


    @Test (expected = HygieiaException.class)
    public void createNewBadToolName() throws IOException, HygieiaException {
        GenericCollectorItemCreateRequest request = createRequest("Tool", "some data", "some source", "5ba136290be2d32568777fa8", "5bae541b099739600663ef9a");
        loadCollector();
        genericCollectorItemRepository.deleteAll();
        String response = genericCollectorItemService().create(request);
    }

    @Test (expected = HygieiaException.class)
    public void createNewBadObjectId() throws HygieiaException {
        genericCollectorItemRepository.deleteAll();
        GenericCollectorItemCreateRequest request = createRequest("GitHub", "some data", "some source", "1aaaa", "5bae541b099739600663ef9a");
        String response = genericCollectorItemService().create(request);
    }

    private void loadData(String toolName, String rawData, String source, ObjectId buildId, ObjectId relatedId, ObjectId collectorId) {
        GenericCollectorItem genericCollectorItem = new GenericCollectorItem();
        genericCollectorItem.setToolName(toolName);
        genericCollectorItem.setRawData(rawData);
        genericCollectorItem.setSource(source);
        genericCollectorItem.setBuildId(buildId);
        genericCollectorItem.setRelatedCollectorItem(relatedId);
        genericCollectorItem.setCollectorId(collectorId);
        genericCollectorItemRepository.save(genericCollectorItem);
    }

    private GenericCollectorItemCreateRequest createRequest(String toolName, String rawData, String source, String buildId, String relatedCollectorItemId) {
        GenericCollectorItemCreateRequest genericCollectorItem = new GenericCollectorItemCreateRequest();
        genericCollectorItem.setToolName(toolName);
        genericCollectorItem.setBuildId(buildId);
        genericCollectorItem.setRelatedCollectorItemId(relatedCollectorItemId);
        genericCollectorItem.setRawData(rawData);
        genericCollectorItem.setSource(source);
        return genericCollectorItem;
    }


    public void loadCollector() throws IOException {
        Gson gson = GsonUtil.getGson();
        String json = IOUtils.toString(BinaryArtifactServiceTest.class.getResourceAsStream("coll.json"));
        List<Collector> collector = gson.fromJson(json, new TypeToken<List<Collector>>() {
        }.getType());
        collectorRepository.save(collector);
    }
}