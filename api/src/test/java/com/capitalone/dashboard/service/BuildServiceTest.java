package com.capitalone.dashboard.service;

import com.capitalone.dashboard.model.*;
import com.capitalone.dashboard.repository.BuildRepository;
import com.capitalone.dashboard.repository.CollectorRepository;
import com.capitalone.dashboard.repository.ComponentRepository;
import com.capitalone.dashboard.request.BuildDataCreateRequest;
import com.capitalone.dashboard.request.BuildSearchRequest;
import com.capitalone.dashboard.request.BuildServerWatchRequest;
import com.mysema.query.types.Predicate;
import org.apache.catalina.connector.Response;
import org.apache.commons.logging.Log;
import org.bson.types.ObjectId;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.joda.time.LocalDate;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;


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


    @Test
    public void watch_validRequest() {

        BuildServerWatchRequest request = new BuildServerWatchRequest();
        request.setBuildServerUrl("http://jenkins.com/path/to/job");
        request.setCollectorName("Hudson");

        HudsonCollector hudsonCollector = new HudsonCollector();

        when(collectorRepository.findByName("Hudson")).thenReturn(hudsonCollector);
        when(collectorRepository.save(hudsonCollector)).thenReturn(hudsonCollector);

        ResponseEntity responseEntity = buildService.watch(request);
        int httpReturnCode = responseEntity.getStatusCode().value();
        Assert.assertTrue("body response is " + responseEntity.getBody() , httpReturnCode == HttpStatus.OK.value());
        verify(collectorRepository, times(1)).save(hudsonCollector);

    }

    @Test
    public void watch_duplicateUrl() {

        BuildServerWatchRequest request = new BuildServerWatchRequest();
        request.setBuildServerUrl("http://jenkins.com/path/to/job");
        request.setCollectorName("Hudson");

        HudsonCollector hudsonCollector = new HudsonCollector();

        when(collectorRepository.findByName("Hudson")).thenReturn(hudsonCollector);

        buildService.watch(request);
        int httpReturnCode = buildService.watch(request).getStatusCode().value();
        Assert.assertTrue(httpReturnCode == HttpStatus.OK.value());
    }

    @Test
    public void watch_internalError() {

        BuildServerWatchRequest request = new BuildServerWatchRequest();
        request.setBuildServerUrl("http://jenkins.com/path/to/job");
        request.setCollectorName("Hudson");

        //test for when collector is not found
        when(collectorRepository.findByName("Hudson")).thenReturn(null);

        int httpReturnCode = buildService.watch(request).getStatusCode().value();
        Assert.assertTrue(httpReturnCode == HttpStatus.INTERNAL_SERVER_ERROR.value());

        //test when save fails
        HudsonCollector hudsonCollector = new HudsonCollector();

        when(collectorRepository.findByName("Hudson")).thenReturn(hudsonCollector);
        when(collectorRepository.save(hudsonCollector)).thenReturn(null);

        httpReturnCode = buildService.watch(request).getStatusCode().value();
        Assert.assertTrue(httpReturnCode == HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    @Test
    public void watch_unsupportedBuildCollectorName() {

        BuildServerWatchRequest request = new BuildServerWatchRequest();
        request.setBuildServerUrl("http://jenkins.com/path/to/job");
        request.setCollectorName("Unsupported");

        int httpReturnCode = buildService.watch(request).getStatusCode().value();
        Assert.assertTrue(httpReturnCode == HttpStatus.NOT_IMPLEMENTED.value());

    }

    @Test
    public void watch_invalidBuildServerUrl() {
        BuildServerWatchRequest request = new BuildServerWatchRequest();
        request.setBuildServerUrl("ftp://jenkins.com/path/to/job");
        request.setCollectorName("Hudson");

        HudsonCollector hudsonCollector = new HudsonCollector();

        when(collectorRepository.findByName("Hudson")).thenReturn(hudsonCollector);
        when(collectorRepository.save(hudsonCollector)).thenReturn(hudsonCollector);

        ResponseEntity responseEntity = buildService.watch(request);
        int httpReturnCode = responseEntity.getStatusCode().value();
        Assert.assertTrue("reponse body: " + responseEntity.getBody(), httpReturnCode == HttpStatus.BAD_REQUEST.value());

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
