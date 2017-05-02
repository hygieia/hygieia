package com.capitalone.dashboard.collector;

import com.capitalone.dashboard.model.*;
import com.capitalone.dashboard.repository.BaseCollectorItemRepository;
import com.capitalone.dashboard.repository.CommitRepository;
import com.capitalone.dashboard.repository.ComponentRepository;
import com.capitalone.dashboard.repository.HpsmRepository;
import org.bson.types.ObjectId;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class HpsmCollectorTaskTest {

    @Mock private BaseCollectorItemRepository collectors;
    @Mock private HpsmRepository hpsmRepository;
    @Mock private HpsmClient hpsmClient;
    @Mock private HpsmSettings hpsmSettings;
    @Mock private ComponentRepository dbComponentRepository;
    @Mock private CommitRepository commitRepository;

    @Mock private HpsmCollector repo1;
    @Mock private HpsmCollector repo2;

    @Mock private Commit commit;

    @InjectMocks private HpsmCollectorTask task;
    @Test
    public void collect_testCollect1() {

    }
    //@Test
    public void collect_testCollect() {

    }

}