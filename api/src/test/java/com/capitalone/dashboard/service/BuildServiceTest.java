package com.capitalone.dashboard.service;

import com.capitalone.dashboard.model.Collector;
import com.capitalone.dashboard.model.CollectorItem;
import com.capitalone.dashboard.model.CollectorType;
import com.capitalone.dashboard.model.Component;
import com.capitalone.dashboard.model.SCM;
import com.capitalone.dashboard.repository.BuildRepository;
import com.capitalone.dashboard.repository.CollectorRepository;
import com.capitalone.dashboard.repository.ComponentRepository;
import com.capitalone.dashboard.request.BuildDataCreateRequest;
import com.capitalone.dashboard.request.BuildSearchRequest;
import com.mysema.query.types.Predicate;
import org.bson.types.ObjectId;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.joda.time.LocalDate;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;

import static org.mockito.Mockito.argThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class BuildServiceTest {

    @Mock private BuildRepository buildRepository;
    @Mock private ComponentRepository componentRepository;
    @Mock private CollectorRepository collectorRepository;
    @InjectMocks private BuildServiceImpl buildService;

    @Test
    public void search() {
        ObjectId componentId = ObjectId.get();
        ObjectId collectorItemId = ObjectId.get();
        ObjectId collectorId = ObjectId.get();

        BuildSearchRequest request = new BuildSearchRequest();
        request.setComponentId(componentId);

        when(componentRepository.findOne(request.getComponentId())).thenReturn(makeComponent(collectorItemId, collectorId));
        when(collectorRepository.findOne(collectorId)).thenReturn(new Collector());

        buildService.search(request);

        verify(buildRepository, times(1)).findAll(argThat(hasPredicate("build.collectorItemId = " + collectorItemId.toString())));
    }


    @Test
    public void search_14days() {
        ObjectId componentId = ObjectId.get();
        ObjectId collectorItemId = ObjectId.get();
        ObjectId collectorId = ObjectId.get();

        BuildSearchRequest request = new BuildSearchRequest();
        request.setComponentId(componentId);
        request.setNumberOfDays(14);

        when(componentRepository.findOne(request.getComponentId())).thenReturn(makeComponent(collectorItemId, collectorId));
        when(collectorRepository.findOne(collectorId)).thenReturn(new Collector());

        buildService.search(request);

        long endTimeTarget = new LocalDate().minusDays(request.getNumberOfDays()).toDate().getTime();
        String expectedPredicate = "build.collectorItemId = " + collectorItemId.toString() + " && build.endTime >= " + endTimeTarget;
        verify(buildRepository, times(1)).findAll(argThat(hasPredicate(expectedPredicate)));
    }

    private Component makeComponent(ObjectId collectorItemId, ObjectId collectorId) {
        CollectorItem item = new CollectorItem();
        item.setId(collectorItemId);
        item.setCollectorId(collectorId);
        Component c = new Component();
        c.getCollectorItems().put(CollectorType.Build, Arrays.asList(item));
        return c;
    }

    private Matcher<Predicate> hasPredicate(final String value) {
        return new TypeSafeMatcher<Predicate>() {
            @Override
            protected boolean matchesSafely(Predicate predicate) {
                return predicate.toString().equalsIgnoreCase(value);
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("a Predicate equal to " + value);
            }
        };
    }

    private SCM makeScm() {
        SCM scm = new SCM();
        scm.setScmUrl("scmUrl");
        scm.setScmRevisionNumber("revNum");
        scm.setNumberOfChanges(20);
        scm.setScmCommitTimestamp(200);
        scm.setScmCommitLog("Log message");
        scm.setScmAuthor("bob");
        return scm;
    }


    @SuppressWarnings("unused")
	private BuildDataCreateRequest makeBuildRequest() {
        BuildDataCreateRequest build = new BuildDataCreateRequest();
        build.setNumber("1");
        build.setBuildUrl("buildUrl");
        build.setStartTime(3);
        build.setEndTime(8);
        build.setDuration(5);
        build.setBuildStatus("Success");
        build.setStartedBy("foo");
        build.setJobName("MyJob");
        build.getSourceChangeSet().add(makeScm());
        return build;
    }
}
