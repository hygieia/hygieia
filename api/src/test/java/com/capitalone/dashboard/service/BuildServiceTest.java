package com.capitalone.dashboard.service;

import com.capitalone.dashboard.misc.HygieiaException;
import com.capitalone.dashboard.model.Build;
import com.capitalone.dashboard.model.BuildStatus;
import com.capitalone.dashboard.model.CodeReposBuilds;
import com.capitalone.dashboard.model.Collector;
import com.capitalone.dashboard.model.CollectorItem;
import com.capitalone.dashboard.model.CollectorType;
import com.capitalone.dashboard.model.Component;
import com.capitalone.dashboard.model.Dashboard;
import com.capitalone.dashboard.model.DashboardType;
import com.capitalone.dashboard.model.DataResponse;
import com.capitalone.dashboard.model.RepoBranch;
import com.capitalone.dashboard.model.SCM;
import com.capitalone.dashboard.model.ScoreDisplayType;
import com.capitalone.dashboard.repository.BuildRepository;
import com.capitalone.dashboard.repository.CodeReposBuildsRepository;
import com.capitalone.dashboard.repository.CollectorItemRepository;
import com.capitalone.dashboard.repository.CollectorRepository;
import com.capitalone.dashboard.repository.ComponentRepository;
import com.capitalone.dashboard.request.BuildDataCreateRequest;
import com.capitalone.dashboard.request.BuildSearchRequest;
import com.capitalone.dashboard.response.BuildDataCreateResponse;
import com.capitalone.dashboard.settings.ApiSettings;
import com.capitalone.dashboard.webhook.settings.JenkinsBuildWebHookSettings;
import com.capitalone.dashboard.webhook.settings.WebHookSettings;
import com.mysema.query.types.Predicate;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.argThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class BuildServiceTest {

    @Mock private BuildRepository buildRepository;
    @Mock private ComponentRepository componentRepository;
    @Mock private CollectorRepository collectorRepository;
    @Mock private CollectorItemRepository collectorItemRepository;
    @Mock private CodeReposBuildsRepository codeReposBuildsRepository;
    @InjectMocks private BuildServiceImpl buildService;
    @Mock private DashboardServiceImpl dashboardService;
    @Mock
    private CollectorService collectorService;
    @Mock
    private ApiSettings apiSettings;

    @Mock
    private WebHookSettings webHookSettings;

    @Test
    public void search() {
        ObjectId componentId = ObjectId.get();
        ObjectId collectorItemId = ObjectId.get();
        ObjectId collectorId = ObjectId.get();

        BuildSearchRequest request = new BuildSearchRequest();
        request.setComponentId(componentId);

        when(componentRepository.findOne(request.getComponentId())).thenReturn(makeComponent(collectorItemId, collectorId, true));
        when(collectorRepository.findOne(collectorId)).thenReturn(new Collector());

        buildService.search(request);

        verify(buildRepository, times(1)).findAll(argThat(hasPredicate("build.collectorItemId = " + collectorItemId.toString())));
    }

    @Test
    public void search_Empty_Response_No_CollectorItems() {
        ObjectId componentId = ObjectId.get();
        ObjectId collectorItemId = ObjectId.get();
        ObjectId collectorId = ObjectId.get();

        BuildSearchRequest request = new BuildSearchRequest();
        request.setComponentId(componentId);

        when(componentRepository.findOne(request.getComponentId())).thenReturn(makeComponent(collectorItemId, collectorId, false));
        when(collectorRepository.findOne(collectorId)).thenReturn(new Collector());

        DataResponse<Iterable<Build>> response = buildService.search(request);

        List<Build> result = (List<Build>) response.getResult();
        Assert.assertEquals(0, result.size());
    }

    @Test
    public void search_Empty_Response_No_Component() {
        ObjectId collectorId = ObjectId.get();
        BuildSearchRequest request = new BuildSearchRequest();

        when(componentRepository.findOne(request.getComponentId())).thenReturn(null);
        when(collectorRepository.findOne(collectorId)).thenReturn(new Collector());

        DataResponse<Iterable<Build>> response = buildService.search(request);

        List<Build> result = (List<Build>) response.getResult();
        Assert.assertEquals(0, result.size());
    }

    @Test
    public void search_14days() {
        ObjectId componentId = ObjectId.get();
        ObjectId collectorItemId = ObjectId.get();
        ObjectId collectorId = ObjectId.get();

        BuildSearchRequest request = new BuildSearchRequest();
        request.setComponentId(componentId);
        request.setNumberOfDays(14);

        when(componentRepository.findOne(request.getComponentId())).thenReturn(makeComponent(collectorItemId, collectorId, true));
        when(collectorRepository.findOne(collectorId)).thenReturn(new Collector());

        buildService.search(request);

        long endTimeTarget = new LocalDate().minusDays(request.getNumberOfDays()).toDate().getTime();
        String expectedPredicate = "build.collectorItemId = " + collectorItemId.toString() + " && build.endTime >= " + endTimeTarget;
        verify(buildRepository, times(1)).findAll(argThat(hasPredicate(expectedPredicate)));
    }

    @Test
    public void createWithGoodRequest() throws HygieiaException {
        ObjectId collectorId = ObjectId.get();

        BuildDataCreateRequest request = makeBuildRequest();

        when(collectorRepository.findOne(collectorId)).thenReturn(new Collector());
        when(collectorService.createCollector(any(Collector.class))).thenReturn(new Collector());
        when(collectorService.createCollectorItem(any(CollectorItem.class))).thenReturn(new CollectorItem());

        Build build = makeBuild();

        when(buildRepository.save(any(Build.class))).thenReturn(build);
        when(apiSettings.getWebHook()).thenReturn(webHookSettings);
        when(webHookSettings.getJenkinsBuild()).thenReturn(jenkinsSettings());
        String response = buildService.create(request);
        String expected = build.getId().toString();
        assertEquals(response, expected);
    }

    @Test
    public void createV2WithGoodRequest() throws HygieiaException {
        ObjectId collectorId = ObjectId.get();

        BuildDataCreateRequest request = makeBuildRequest();

        when(collectorRepository.findOne(collectorId)).thenReturn(new Collector());
        when(collectorService.createCollector(any(Collector.class))).thenReturn(new Collector());
        when(collectorService.createCollectorItem(any(CollectorItem.class))).thenReturn(new CollectorItem());

        Build build = makeBuild();

        when(buildRepository.save(any(Build.class))).thenReturn(build);
        when(apiSettings.getWebHook()).thenReturn(webHookSettings);
        when(webHookSettings.getJenkinsBuild()).thenReturn(jenkinsSettings());
        String response = buildService.createV2(request);
        String expected = build.getId().toString() + "," + build.getCollectorItemId();
        assertEquals(response, expected);
    }

    @Test
    public void createV3WithGoodRequest() throws HygieiaException {
        ObjectId collectorId = ObjectId.get();
        BuildDataCreateRequest request = makeBuildRequest();

        when(collectorRepository.findOne(collectorId)).thenReturn(new Collector());
        when(collectorService.createCollector(any(Collector.class))).thenReturn(new Collector());
        when(collectorService.createCollectorItem(any(CollectorItem.class))).thenReturn(new CollectorItem());
        when(collectorItemRepository.findOne(any(ObjectId.class))).thenReturn(new CollectorItem());
        Build build = makeBuild();
        List<Dashboard> dashboards = new ArrayList<>();
        when(buildRepository.save(any(Build.class))).thenReturn(build);
        when(dashboardService.getDashboardsByCollectorItems(any(Set.class),any(CollectorType.class))).thenReturn(dashboards);
        when(apiSettings.getWebHook()).thenReturn(webHookSettings);
        when(webHookSettings.getJenkinsBuild()).thenReturn(jenkinsSettings());
        BuildDataCreateResponse response = buildService.createV3(request);
        assertEquals(build.getStartedBy(), response.getStartedBy());
        assertEquals(build.getNumber(), response.getNumber());
    }

    @Test
    public void createV3WithGoodRequestEnableDashboardLookup() throws HygieiaException {
        ObjectId collectorId = ObjectId.get();
        BuildDataCreateRequest request = makeBuildRequest();
        request.getCodeRepos().add(new RepoBranch("https://github.com/someorg/somerepo","master", RepoBranch.RepoType.GIT));
        Build build = makeBuild();
        build.getCodeRepos().add(new RepoBranch("https://github.com/someorg/somerepo","master", RepoBranch.RepoType.GIT));
        List<Dashboard> dashboards = Collections.singletonList(new Dashboard("team", "title", null, null, DashboardType.Team, "configItemAppName", "configItemComponentName", null, false, ScoreDisplayType.HEADER));
        when(collectorRepository.findOne(collectorId)).thenReturn(new Collector());
        when(collectorService.createCollector(any(Collector.class))).thenReturn(new Collector());
        when(collectorService.createCollectorItem(any(CollectorItem.class))).thenReturn(new CollectorItem());
        when(collectorItemRepository.findOne(any(ObjectId.class))).thenReturn(new CollectorItem());
        when(buildRepository.save(any(Build.class))).thenReturn(build);
        when(apiSettings.isLookupDashboardForBuildDataCreate()).thenReturn(Boolean.TRUE);
        when(dashboardService.getDashboardsByCollectorItems(any(Set.class), any(CollectorType.class))).thenReturn(dashboards);
        when(apiSettings.getWebHook()).thenReturn(webHookSettings);
        when(webHookSettings.getJenkinsBuild()).thenReturn(jenkinsSettings());
        when(codeReposBuildsRepository.findByCodeRepo("https://github.com/someorg/somerepo")).thenReturn(null);
        when(codeReposBuildsRepository.save(any(CodeReposBuilds.class))).thenReturn(new CodeReposBuilds());
        BuildDataCreateResponse response = buildService.createV3(request);
        assertEquals(build.getStartedBy(), response.getStartedBy());
        assertEquals(build.getNumber(), response.getNumber());
    }

    @Test
    public void createV3WithGoodRequestLibraryThresholdExceed() throws HygieiaException {
        ObjectId collectorId = ObjectId.get();
        BuildDataCreateRequest request = makeBuildRequest();
        request.getCodeRepos().add(new RepoBranch("https://github.com/someorg/somerepo","master", RepoBranch.RepoType.GIT));
        Build build = makeBuild();
        build.getCodeRepos().add(new RepoBranch("https://github.com/someorg/somerepo","master", RepoBranch.RepoType.GIT));
        List<Dashboard> dashboards = Collections.singletonList(new Dashboard("team", "title", null, null, DashboardType.Team, "configItemAppName", "configItemComponentName", null, false, ScoreDisplayType.HEADER));
        when(collectorRepository.findOne(collectorId)).thenReturn(new Collector());
        when(collectorService.createCollector(any(Collector.class))).thenReturn(new Collector());
        when(collectorService.createCollectorItem(any(CollectorItem.class))).thenReturn(new CollectorItem());
        when(collectorItemRepository.findOne(any(ObjectId.class))).thenReturn(new CollectorItem());
        when(buildRepository.save(any(Build.class))).thenReturn(build);
        when(apiSettings.isLookupDashboardForBuildDataCreate()).thenReturn(Boolean.FALSE);
        when(dashboardService.getDashboardsByCollectorItems(any(Set.class), any(CollectorType.class))).thenReturn(dashboards);
        when(apiSettings.getWebHook()).thenReturn(webHookSettings);
        when(webHookSettings.getJenkinsBuild()).thenReturn(jenkinsSettings());
        when(codeReposBuildsRepository.findByCodeRepo("https://github.com/someorg/somerepo")).thenReturn(makeCodeReposBuilds());
        when(codeReposBuildsRepository.save(any(CodeReposBuilds.class))).thenReturn(new CodeReposBuilds());
        BuildDataCreateResponse response = buildService.createV3(request);
        assertEquals(build.getStartedBy(), response.getStartedBy());
        assertEquals(build.getNumber(), response.getNumber());
    }

    private CodeReposBuilds makeCodeReposBuilds() {
        CodeReposBuilds entity = new CodeReposBuilds();
        entity.getBuildCollectorItems().add(ObjectId.get());
        entity.getBuildCollectorItems().add(ObjectId.get());
        entity.getBuildCollectorItems().add(ObjectId.get());
        entity.getBuildCollectorItems().add(ObjectId.get());
        return entity;
    }

    private Component makeComponent(ObjectId collectorItemId, ObjectId collectorId, boolean populateCollectorItems) {
        CollectorItem item = new CollectorItem();
        item.setId(collectorItemId);
        item.setCollectorId(collectorId);
        Component c = new Component();
        if (populateCollectorItems) {
            c.getCollectorItems().put(CollectorType.Build, Collections.singletonList(item));
        }

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

    private Build makeBuild() {
        Build build = new Build();
        build.setNumber("1");
        build.setBuildUrl("buildUrl");
        build.setStartTime(3);
        build.setEndTime(8);
        build.setDuration(5);
        build.setBuildStatus(BuildStatus.Success);
        build.setStartedBy("foo");
        build.setId(ObjectId.get());
        build.setCollectorItemId(ObjectId.get());
        build.getSourceChangeSet().add(makeScm());
        return build;
    }

    private JenkinsBuildWebHookSettings jenkinsSettings() {
        JenkinsBuildWebHookSettings settings = new JenkinsBuildWebHookSettings();
        settings.setEnableFilterLibraryRepos(true);
        settings.setExcludeLibraryRepoThreshold(3);
        settings.setExcludeCodeReposInBuild(Arrays.asList("https://github.com/someorg/somerepo"));
        return settings;
    }

}
