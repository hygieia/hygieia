package com.capitalone.dashboard.service;

import com.capitalone.dashboard.model.Collector;
import com.capitalone.dashboard.model.CollectorItem;
import com.capitalone.dashboard.model.CollectorType;
import com.capitalone.dashboard.repository.CollectorItemRepository;
import com.capitalone.dashboard.repository.CollectorRepository;
import org.bson.types.ObjectId;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class CollectorServiceTest {

    @Mock private CollectorRepository collectorRepository;
    @Mock private CollectorItemRepository collectorItemRepository;
    @InjectMocks private CollectorServiceImpl collectorService;

    @Test
    @SuppressWarnings("unchecked")
    public void collectorItemsByType() {
        Collector c = makeCollector();
        CollectorItem item1 = makeCollectorItem();
        CollectorItem item2 = makeCollectorItem();

        when(collectorRepository.findByCollectorType(CollectorType.Build)).thenReturn(Arrays.asList(c));
        when(collectorItemRepository.findByCollectorIdIn(anyCollection())).thenReturn(Arrays.asList(item1, item2));

        List<CollectorItem> items = collectorService.collectorItemsByType(CollectorType.Build);

        assertThat(items.size(), is(2));
        assertThat(items, contains(item1, item2));
    }

    private Collector makeCollector() {
        Collector collector = new Collector();
        collector.setId(ObjectId.get());
        return collector;
    }

    private CollectorItem makeCollectorItem() {
        CollectorItem item = new CollectorItem();
        item.setId(ObjectId.get());
        return item;
    }
}
