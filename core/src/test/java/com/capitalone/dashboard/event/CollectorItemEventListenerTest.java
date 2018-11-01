package com.capitalone.dashboard.event;

import com.capitalone.dashboard.model.Collector;
import com.capitalone.dashboard.model.CollectorItem;
import com.capitalone.dashboard.model.CollectorType;
import com.capitalone.dashboard.model.Component;
import com.capitalone.dashboard.repository.CollectorRepository;
import com.capitalone.dashboard.repository.ComponentRepository;
import org.bson.types.ObjectId;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.data.mongodb.core.mapping.event.AfterSaveEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CollectorItemEventListenerTest {
    @Mock
    private ComponentRepository componentRepository;
    @Mock
    private CollectorRepository collectorRepository;
    @InjectMocks
    private CollectorItemEventListener eventListener;

    @Test
    public void collectorItem_Updated_lastUpdatedTimeStamp() {
        Component component = createComponent(1537476665987L);
        CollectorItem c = setupData(component, true);
        eventListener.onAfterSave(new AfterSaveEvent<>(c, null, ""));
        assertThat(component.getCollectorItems(CollectorType.SCM).get(0).getLastUpdated(), is(1537476665987L));
        verify(componentRepository).save(component);
    }

    @Test
    public void collectorItem_Updated_lastUpdatedTimeStamp_Zero() {
        Component component = createComponent(0);
        CollectorItem c = setupData(component, true);
        eventListener.onAfterSave(new AfterSaveEvent<>(c, null, ""));
        assertThat(component.getCollectorItems(CollectorType.SCM).get(0).getLastUpdated(), is(1537471111111L));
        verify(componentRepository).save(component);
    }

    @Test
    public void collectorItem_NotEnabled() {
        Component component = createComponent(0);
        CollectorItem c = setupData(component, false);
        eventListener.onAfterSave(new AfterSaveEvent<>(c, null, ""));
        assertThat(component.getCollectorItems(CollectorType.SCM).get(0).getLastUpdated(), is(0L));
        verify(componentRepository, never()).save(component);
    }


    private CollectorItem setupData(Component component, boolean enabled) {
        CollectorItem c = collectorItem(1537471111111L, enabled);
        c.setId(component.getCollectorItems(CollectorType.SCM).get(0).getId());
        setupComponent(component);
        return c;
    }

    private CollectorItem collectorItem(long lastUpdated, boolean enabled) {
        CollectorItem item = new CollectorItem();
        item.setId(ObjectId.get());
        item.setLastUpdated(lastUpdated);
        item.setEnabled(enabled);
        return item;
    }

    private Collector createCollector(CollectorType collectorType) {
        Collector collector = new Collector();
        collector.setCollectorType(collectorType);
        collector.setId(ObjectId.get());
        return collector;
    }

    private Component createComponent(long lastUpdated) {
        Component component = new Component();
        component.getCollectorItems().put(CollectorType.SCM, new ArrayList(Collections.singleton(collectorItem(lastUpdated, true))));
        return component;
    }

    private void setupComponent(Component component) {
        ObjectId collectorItemId = component.getCollectorItems(CollectorType.SCM).get(0).getId();
        when(collectorRepository.findOne(Matchers.any(ObjectId.class))).thenReturn(createCollector(CollectorType.SCM));
        when(componentRepository.findByCollectorTypeAndItemIdIn(any(CollectorType.class), any(List.class))).thenReturn(new ArrayList(Collections.singleton(component)));

    }


}
