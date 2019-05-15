package com.capitalone.dashboard.service;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyCollection;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyMap;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.capitalone.dashboard.repository.CustomRepositoryQuery;
import org.apache.commons.collections4.MapUtils;
import org.bson.types.ObjectId;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.capitalone.dashboard.model.Collector;
import com.capitalone.dashboard.model.CollectorItem;
import com.capitalone.dashboard.model.CollectorType;
import com.capitalone.dashboard.repository.CollectorItemRepository;
import com.capitalone.dashboard.repository.CollectorRepository;

@RunWith(MockitoJUnitRunner.class)
public class CollectorServiceTest {
	private static final String FILTER_STRING = "";

	@Mock
	private CollectorRepository collectorRepository;
	@Mock
	private CollectorItemRepository collectorItemRepository;
	@InjectMocks
	private CollectorServiceImpl collectorService;


	@Test
	@SuppressWarnings("unchecked")
	public void collectorItemsByType() {
		Collector c = makeCollector();
		CollectorItem item1 = makeCollectorItem(true);
		CollectorItem item2 = makeCollectorItem(true);
		when(collectorRepository.findByCollectorType(CollectorType.Build)).thenReturn(Arrays.asList(c));

		Page<CollectorItem> page = new PageImpl<CollectorItem>(Arrays.asList(item1, item2), null, 2);
		when(collectorItemRepository.findByCollectorIdAndSearchField(any(List.class),any(String.class), any(String.class),
				any(Pageable.class))).thenReturn(page);
		Page<CollectorItem> items = collectorService.collectorItemsByTypeWithFilter(CollectorType.Build, FILTER_STRING,
				null);
		assertThat(items.getTotalElements(), is(2L));
		assertTrue(items.getContent().contains(item1));
		assertTrue(items.getContent().contains(item2));
	}

	private Collector makeCollector() {
		Collector collector = new Collector();
		collector.setId(ObjectId.get());
		Map<String, Object> uniqueOptions = new HashMap<>();
		uniqueOptions.put("projectName","");
		uniqueOptions.put("instanceUrl","");
		collector.setUniqueFields(uniqueOptions);
		return collector;
	}



	private CollectorItem makeCollectorItem(boolean enabled) {
		CollectorItem item = new CollectorItem();
		item.setId(ObjectId.get());
		item.setEnabled(enabled);
		return item;
	}

	private CollectorItem makeCollectorItemWithOptions(boolean enabled, Map<String,Object> options,ObjectId id,long timestamp) {
		CollectorItem item = new CollectorItem();
		item.setId(id);
		item.setEnabled(enabled);
		item.setOptions(options);
		item.setLastUpdated(timestamp);
		return item;
	}


	@Test
	public void testCreateCollectorItem(){
		Collector c = makeCollector();
		Map<String, Object> uniqueOptions = new HashMap<>();
		uniqueOptions.put("projectName","A");
		uniqueOptions.put("instanceUrl","https://a.com");

		Map<String, Object> uniqueOptions2 = new HashMap<>();
		uniqueOptions2.put("projectName","A");
		uniqueOptions2.put("instanceUrl","https://a.com");
		uniqueOptions2.put("appName","Appname");
		uniqueOptions2.put("appId","appId");

		CollectorItem c1 = makeCollectorItemWithOptions(true,uniqueOptions,ObjectId.get(),1557332269095L);
		CollectorItem c2 = makeCollectorItemWithOptions(true,uniqueOptions2,ObjectId.get(),1557947220000L);
		when(collectorRepository.findOne(any(ObjectId.class))).thenReturn(c);
		when(collectorItemRepository.findAllByOptionMapAndCollectorIdsIn(anyMap(),anyList())).thenReturn(Arrays.asList(c2));
		when(collectorItemRepository.save(any(CollectorItem.class))).thenReturn(c1);
		CollectorItem actual = collectorService.createCollectorItem(c1);
		assertTrue(actual.getId().equals(c2.getId()));
		verify(collectorRepository,times(1)).findOne(any(ObjectId.class));
		verify(collectorItemRepository,times(1)).findAllByOptionMapAndCollectorIdsIn(anyMap(),anyList());
		verify(collectorItemRepository,times(1)).save(any(CollectorItem.class));
	}

	@Test
	public void testCreateCollectorItemWithMoreOptions(){
		Collector c = makeCollector();
		Map<String, Object> uniqueOptions = new HashMap<>();
		uniqueOptions.put("projectName","A");
		uniqueOptions.put("instanceUrl","https://a.com");

		Map<String, Object> uniqueOptions2 = new HashMap<>();
		uniqueOptions2.put("projectName","A");
		uniqueOptions2.put("instanceUrl","https://a.com");
		uniqueOptions2.put("appName","Appname");
		uniqueOptions2.put("appId","appId");

		CollectorItem c1 = makeCollectorItemWithOptions(true,uniqueOptions,ObjectId.get(),1557947220000L);
		CollectorItem c2 = makeCollectorItemWithOptions(true,uniqueOptions2,ObjectId.get(),1557332269095L);
		when(collectorRepository.findOne(any(ObjectId.class))).thenReturn(c);
		when(collectorItemRepository.findAllByOptionMapAndCollectorIdsIn(anyMap(),anyList())).thenReturn(Arrays.asList(c1));
		when(collectorItemRepository.save(any(CollectorItem.class))).thenReturn(c2);
		CollectorItem actual = collectorService.createCollectorItem(c2);
		assertTrue(actual.getId().equals(c1.getId()));
		verify(collectorRepository,times(1)).findOne(any(ObjectId.class));
		verify(collectorItemRepository,times(1)).findAllByOptionMapAndCollectorIdsIn(anyMap(),anyList());
		verify(collectorItemRepository,times(1)).save(any(CollectorItem.class));
	}

	@Test
	public void testCreateCollectorItemWithMoreOptionsWithDisabled(){
		Collector c = makeCollector();
		Map<String, Object> uniqueOptions = new HashMap<>();
		uniqueOptions.put("projectName","A");
		uniqueOptions.put("instanceUrl","https://a.com");

		Map<String, Object> uniqueOptions2 = new HashMap<>();
		uniqueOptions2.put("projectName","A");
		uniqueOptions2.put("instanceUrl","https://a.com");
		uniqueOptions2.put("appName","Appname");
		uniqueOptions2.put("appId","appId");

		CollectorItem c1 = makeCollectorItemWithOptions(false,uniqueOptions,ObjectId.get(),1557332269095L);
		CollectorItem c2 = makeCollectorItemWithOptions(true,uniqueOptions2,ObjectId.get(),1557332269095L);
		when(collectorRepository.findOne(any(ObjectId.class))).thenReturn(c);
		when(collectorItemRepository.findAllByOptionMapAndCollectorIdsIn(anyMap(),anyList())).thenReturn(Arrays.asList(c1));
		when(collectorItemRepository.save(any(CollectorItem.class))).thenReturn(c2);
		CollectorItem actual = collectorService.createCollectorItem(c2);
		assertTrue(actual.getId().equals(c1.getId()));
		verify(collectorRepository,times(1)).findOne(any(ObjectId.class));
		verify(collectorItemRepository,times(1)).findAllByOptionMapAndCollectorIdsIn(anyMap(),anyList());
		verify(collectorItemRepository,times(1)).save(any(CollectorItem.class));
	}


	@Test
	public void testCreateCollectorItemWithNonExisting(){
		Collector c = makeCollector();
		Map<String, Object> uniqueOptions = new HashMap<>();
		uniqueOptions.put("projectName","A");
		uniqueOptions.put("instanceUrl","https://a.com");

		CollectorItem c1 = makeCollectorItemWithOptions(false,uniqueOptions,ObjectId.get(),1557332269095L);
		when(collectorRepository.findOne(any(ObjectId.class))).thenReturn(c);
		when(collectorItemRepository.findAllByOptionMapAndCollectorIdsIn(anyMap(),anyList())).thenReturn(null);
		when(collectorItemRepository.save(any(CollectorItem.class))).thenReturn(c1);
		CollectorItem actual = collectorService.createCollectorItem(c1);
		assertNotNull(actual);
		verify(collectorRepository,times(1)).findOne(any(ObjectId.class));
		verify(collectorItemRepository,times(1)).findAllByOptionMapAndCollectorIdsIn(anyMap(),anyList());
		verify(collectorItemRepository,times(1)).save(any(CollectorItem.class));
	}


	@Test
	public void testCreateCollectorItemWithMoreOptionsWithEnabledAndDisabled(){
		Collector c = makeCollector();
		Map<String, Object> uniqueOptions = new HashMap<>();
		uniqueOptions.put("projectName","A");
		uniqueOptions.put("instanceUrl","https://a.com");

		Map<String, Object> uniqueOptions2 = new HashMap<>();
		uniqueOptions2.put("projectName","A");
		uniqueOptions2.put("instanceUrl","https://a.com");
		uniqueOptions2.put("appName","Appname");
		uniqueOptions2.put("appId","appId");


		Map<String, Object> uniqueOptions3 = new HashMap<>();
		uniqueOptions3.put("projectName","A");
		uniqueOptions3.put("instanceUrl","https://a.com");
		uniqueOptions3.put("appId","appId");


		CollectorItem c1 = makeCollectorItemWithOptions(false,uniqueOptions,ObjectId.get(),1557947220000L);
		CollectorItem c2 = makeCollectorItemWithOptions(true,uniqueOptions2,ObjectId.get(),1557947220000L);
		CollectorItem c3 = makeCollectorItemWithOptions(true,uniqueOptions3,ObjectId.get(),1557332269095L);
		when(collectorRepository.findOne(any(ObjectId.class))).thenReturn(c);
		when(collectorItemRepository.findAllByOptionMapAndCollectorIdsIn(anyMap(),anyList())).thenReturn(Arrays.asList(c1,c3));
		when(collectorItemRepository.save(any(CollectorItem.class))).thenReturn(c2);
		CollectorItem actual = collectorService.createCollectorItem(c2);
		assertTrue(actual.getId().equals(c3.getId()));
		verify(collectorRepository,times(1)).findOne(any(ObjectId.class));
		verify(collectorItemRepository,times(1)).findAllByOptionMapAndCollectorIdsIn(anyMap(),anyList());
		verify(collectorItemRepository,times(1)).save(any(CollectorItem.class));
	}

	@Test
	public void testCreateCollectorItemWithLastUpdatedAndEnabled(){
		Collector c = makeCollector();
		Map<String, Object> uniqueOptions = new HashMap<>();
		uniqueOptions.put("projectName","A");
		uniqueOptions.put("instanceUrl","https://a.com");

		Map<String, Object> uniqueOptions2 = new HashMap<>();
		uniqueOptions2.put("projectName","A");
		uniqueOptions2.put("instanceUrl","https://a.com");
		uniqueOptions2.put("appName","Appname");
		uniqueOptions2.put("appId","appId");


		Map<String, Object> uniqueOptions3 = new HashMap<>();
		uniqueOptions3.put("projectName","A");
		uniqueOptions3.put("instanceUrl","https://a.com");
		uniqueOptions3.put("appId","appId");


		CollectorItem c1 = makeCollectorItemWithOptions(true,uniqueOptions,ObjectId.get(),1557947220000L);
		CollectorItem c2 = makeCollectorItemWithOptions(true,uniqueOptions2,ObjectId.get(),1557947220000L);
		CollectorItem c3 = makeCollectorItemWithOptions(true,uniqueOptions3,ObjectId.get(),1557860820000L);
		CollectorItem c4 = makeCollectorItemWithOptions(true,uniqueOptions3,ObjectId.get(),1557949459000L);
		when(collectorRepository.findOne(any(ObjectId.class))).thenReturn(c);

		when(collectorItemRepository.findAllByOptionMapAndCollectorIdsIn(anyMap(),anyList())).thenReturn(Arrays.asList(c1,c3,c4));
		when(collectorItemRepository.save(any(CollectorItem.class))).thenReturn(c2);
		CollectorItem actual = collectorService.createCollectorItem(c2);
		assertTrue(actual.getId().equals(c4.getId()));
		verify(collectorRepository,times(1)).findOne(any(ObjectId.class));
		verify(collectorItemRepository,times(1)).findAllByOptionMapAndCollectorIdsIn(anyMap(),anyList());
		verify(collectorItemRepository,times(1)).save(any(CollectorItem.class));
	}




}